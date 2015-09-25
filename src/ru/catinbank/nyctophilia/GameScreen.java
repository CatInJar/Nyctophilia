package ru.catinbank.nyctophilia;

import ru.catinbank.framework.Entity;
import ru.catinbank.framework.MusicFader;
import ru.catinbank.framework.ScreenshotFactory;
import ru.catinbank.nyctophilia.World.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen, InputProcessor
{
	private final Nyctophilia game;
	private Vector3 touchPoint;
	private World world;
	private WorldRenderer renderer;
	private InputMultiplexer plex;
	private UI ui;

	public GameScreen(Nyctophilia game)
	{
		this.game = game;
		touchPoint = new Vector3();
		world = new World(game.batch);
		renderer = new WorldRenderer(game, world);
		plex = new InputMultiplexer();
		plex.addProcessor(this);
		if(Nyctophilia.developerMode)
			ui = new UI(world, renderer, plex);
		Gdx.input.setInputProcessor(plex);
		
		if(Assets.fader == null)
			Assets.fader = new MusicFader(Assets.music);
	}
	
	public GameScreen(Nyctophilia game, String level)
	{
		this.game = game;
		touchPoint = new Vector3();
		world = new World(game.batch, level);
		renderer = new WorldRenderer(game, world);
		plex = new InputMultiplexer();
		plex.addProcessor(this);
		if(Nyctophilia.developerMode)
			ui = new UI(world, renderer, plex);
		Gdx.input.setInputProcessor(plex);
		
		if(Assets.fader == null)
			Assets.fader = new MusicFader(Assets.music);
	}

	@Override
	public void render(float delta) 
	{
		renderer.render();
		world.update(delta);
		if(!world.getInventory().isVisible() && world.getState() != World.State.READING && !world.getPlayer().getLocked() 
				&& world.getMessages().isEmpty() && world.getActions().isEmpty())
			updateInput();
		if(Nyctophilia.developerMode)
			ui.update(delta);
	}
	
	private void updateInput()
	{
		Player p = world.getPlayer();
		if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))
		{
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
				p.setVec(Player.Direction.RIGHT);
			
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
				p.setVec(Player.Direction.LEFT);
		}
		
		if(Gdx.input.isTouched())
		{	
			touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			renderer.getCamera().unproject(touchPoint);
			
			if(world.isEditing())
				ui.checkSelected(touchPoint);
			
			if(Nyctophilia.developerMode && ui.getSelected() != null)
			{
				Entity e = ui.getSelected();
				e.setPosition(touchPoint.x - e.getWidth()/2, touchPoint.y - e.getHeight()/2);
			}
		}
	}
	
	@Override
	public boolean keyUp(int keycode) 
	{
		if(!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))
			world.getPlayer().stop();
		
		switch(keycode)
		{
		case Input.Keys.ESCAPE:
			Assets.playSound("click");
			if(world.getState() == State.RUNNING && world.getMessages().isEmpty() && !world.isPauseBlocked())
				world.setState(State.PAUSED_MENU);
			else if(world.getState() == State.READING)
				world.setState(State.RUNNING);
			else if(world.getState() == State.PAUSED_MENU || world.getState() == State.PAUSED_OPTIONS)
				world.setState(State.RUNNING);
			break;
		case Input.Keys.F2:
			if(Nyctophilia.developerMode)
			{
				Settings.save();
				Gdx.app.exit();
			}
			break;
		case Input.Keys.SPACE:
			if(Nyctophilia.developerMode)
			{
				ui.changeVisible();
				if(world.getState() == State.RUNNING)
					world.setState(State.EDITING_ENTITY);
			}
			break;
		case Input.Keys.Z:
			if(Nyctophilia.developerMode)
				ui.deleteSelected();
			break;
		case Input.Keys.F1:
			ScreenshotFactory.saveScreenshot();
			break;
		}
		return false;
	}
	
	private void gameKeys(int keycode)
	{
		if(!world.getPlayer().getLocked())
		{
		switch(keycode)
		{
		case Input.Keys.RIGHT:
		case Input.Keys.D:
			world.getInventory().nextSection();
			break;
		case Input.Keys.LEFT:
		case Input.Keys.A:
			world.getInventory().previousSection();
			break;
		case Input.Keys.UP:
		case Input.Keys.W:
			world.getInventory().previousStuff();
			if(!world.getActions().isEmpty() && world.getMessages().isEmpty())
				world.getActions().previousAction();;
			break;
		case Input.Keys.DOWN:
		case Input.Keys.S:
			world.getInventory().nextStuff();
			if(!world.getActions().isEmpty() && world.getMessages().isEmpty())
				world.getActions().nextAction();
			break;
		case Input.Keys.TAB:
			if(world.getMessages().isEmpty())
			{
				world.getInventory().setVisible();
				Assets.playSound("click");
			}
			break;	
		case Input.Keys.X:
			if(world.getMessages().isEmpty() && world.getActions().isEmpty())
			{
				if(world.getState() == World.State.RUNNING)
					world.launchSelectedScript();
				else
					world.setState(World.State.RUNNING);
			}
			else if(!world.getMessages().isEmpty())
			{
				world.getMessages().deleteCurrent();
			}
			else if(!world.getActions().isEmpty())
			{
				Assets.playSound("click");
				world.launchScript(world.getActions().getSelected());
				world.getActions().setEmpty();
			}
			break;
		}
		}
	}
	
	private void pauseKeys(int keycode)
	{
		Assets.playSound("click");
		if(world.getState() == World.State.PAUSED_MENU)
		{
			if(keycode == Input.Keys.LEFT)
			{
				world.setSelectedMenu(world.getSelectedMenu()-1);
				if (world.getSelectedMenu() < 0)
					world.setSelectedMenu(2);
			}
			if(keycode == Input.Keys.RIGHT)
			{
				world.setSelectedMenu(world.getSelectedMenu()+1);
				if (world.getSelectedMenu() > 2)
					world.setSelectedMenu(0);
			}
			if(keycode == Input.Keys.X)
			{
				if(world.getSelectedMenu() == 0)
					world.setState(World.State.RUNNING);
				else if(world.getSelectedMenu() == 1)
					world.setState(World.State.PAUSED_OPTIONS);
				else if(world.getSelectedMenu() == 2)
				{
					Settings.save();
					Settings.saveGame(world.getLevel(), Assets.clips, world.getInventory(), world.getPlayer());
					game.setScreen(new MainMenuScreen(game));
				}
			}
		}
		else
		{
			if(keycode == Input.Keys.UP)
			{
				world.setSelectedOption(world.getSelectedOption()-1);
				if (world.getSelectedOption() < 0)
					world.setSelectedOption(2);
			}
			if(keycode == Input.Keys.DOWN)
			{
				world.setSelectedOption(world.getSelectedOption()+1);
				if (world.getSelectedOption() > 2)
					world.setSelectedOption(0);
			}
			if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT)
			{
				switch(world.getSelectedOption())
				{
				case 0:
					Settings.language = !Settings.language;
					Assets.loadLanguage();
					Assets.loadLevelLanguage(world.getLevel().getName());
					Assets.font.setScale(6f);
					break;
				case 1:
					Settings.sound = !Settings.sound;
					Assets.fader.mute();
				}
			}
			if(keycode == Input.Keys.X || keycode == Input.Keys.ESCAPE)
			{
				
				if(world.getSelectedOption() == 2)
				{
					world.setSelectedOption(0);
					Settings.save();
					world.setState(World.State.PAUSED_MENU);
				}
			}
		}
	}
	
	@Override
	public boolean keyDown(int keycode) 
	{
		if(world.getState() != State.PAUSED_MENU && world.getState() != State.PAUSED_OPTIONS)
			gameKeys(keycode);
		else
			pauseKeys(keycode);
		return false; 
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) 
	{
		world.getPlayer().stop();
		if(Nyctophilia.developerMode)
			ui.nullSelected();
		return false;
	}
	
	@Override
	public void dispose()
	{ 
		renderer.dispose();
		if(Nyctophilia.developerMode)
			ui.dispose();
	}
	
	@Override
	public void resume() { renderer.resume(); }
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) { return false; }
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
	
	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public boolean keyTyped(char character) {return false;}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}

	@Override
	public boolean scrolled(int amount) {return false;}
}