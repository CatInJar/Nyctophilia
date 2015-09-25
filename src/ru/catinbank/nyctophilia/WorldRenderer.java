package ru.catinbank.nyctophilia;

import java.util.Map;
import java.util.Map.Entry;

import ru.catinbank.framework.Entity;
import ru.catinbank.framework.Light;
import ru.catinbank.framework.Selector;
import ru.catinbank.nyctophilia.World.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.filters.Combine;
import com.bitfire.postprocessing.filters.CrtScreen.Effect;
import com.bitfire.postprocessing.filters.CrtScreen.RgbMode;
import com.bitfire.utils.ShaderLoader;

public class WorldRenderer
{
	private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
	private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
	private final int VIEWPORT_WIDTH = 320;
	private final int VIEWPORT_HEIGHT = 180;
	
	private final Nyctophilia game;
	private World world;
	private OrthographicCamera camera, UICamera;
	private FrameBuffer frameBuffer;
	private PostProcessor postProcessor;
	CrtMonitor crt;
	
	private float intensity = 0.7f;
	private Vector3 color = new Vector3(0.3f, 0.3f, 0.7f);
	private float time = 0;
	
	public WorldRenderer(Nyctophilia game, World world)
	{
		this.game = game;
		this.world = world;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		UICamera = new OrthographicCamera();
		UICamera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		frameBuffer = new FrameBuffer(Format.RGBA8888, SCREEN_WIDTH, SCREEN_HEIGHT, false);
		
		setLightOptions();
		
		ShaderLoader.BasePath = "shaders/";
        postProcessor = new PostProcessor(false, false, true);
        int effects = Effect.TweakContrast.v | Effect.PhosphorVibrance.v | Effect.Scanlines.v | Effect.Tint.v;
        crt = new CrtMonitor(SCREEN_WIDTH, SCREEN_HEIGHT, false, false, RgbMode.ChromaticAberrations, effects);
        Combine combine = crt.getCombinePass();
		combine.setSource1Intensity( 0f );
		combine.setSource2Intensity( 1f );
		combine.setSource1Saturation( 0f );
		combine.setSource2Saturation( 1f );
        postProcessor.addEffect(crt);
        crt.setEnabled(true);
        
        Assets.font.setScale(1.0f);
	}
	
	public void render()
	{	
		if(world.getLevel() != null && world.getLevel().isLightEnabled())
		{
			time += Gdx.graphics.getDeltaTime();
			crt.setTime(time);
			if(time >= Float.MAX_VALUE)
				time = 0;
		}
		
		updateCamera();
		
		if(world.getLevel() != null)
		{
			postProcessor.setEnabled(world.getLevel().isLightEnabled());
			if(world.getState() == World.State.CHANGING_SCENE || world.getState() == World.State.CHANGING_LEVEL)
				setLightOptions();
		}
		
		if(world.getState() != State.PAUSED_MENU && world.getState() != State.PAUSED_OPTIONS)
			renderRunning();
		else
			renderPaused();
	}
	
	private void renderRunning()
	{
		frameBuffer.begin();
		game.batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setShader(Assets.defaultShader);
		game.batch.begin();
		renderLights();
		game.batch.end();
		frameBuffer.end();
		
		postProcessor.capture();
		game.batch.setShader(Assets.lightShader);
		game.batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.begin();
		frameBuffer.getColorBufferTexture().bind(1);
		Assets.light.bind(0);
		if(Assets.level_lights != null)
			Assets.level_lights.bind(0);
		renderObjects();
		game.batch.end();
		postProcessor.render();
		
		game.batch.setShader(null);
		game.batch.setProjectionMatrix(UICamera.combined);
		game.batch.begin();
		if(world.getState() == World.State.RUNNING)
			renderUI();
		else if(world.getState() == World.State.READING)
			renderNote();
		game.batch.end();
	}
	
	private void renderPaused()
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setProjectionMatrix(UICamera.combined);
		game.batch.begin();
		
		Assets.font.setScale(1.0f);
		if(world.getState() == State.PAUSED_MENU)
		{
			for(int i = 0; i < 3; i++)
			{
				Assets.font.setColor(1, 1, 1, 1);
				if(world.getSelectedMenu() == i)
					Assets.font.setColor(0, 2, 1, 1);
				Assets.font.draw(game.batch, Assets.bundle.get(world.getMenu()[i]), 32 + i*80, 50);
			}
			Assets.font.setColor(1, 1, 1, 1);
			Assets.font.draw(game.batch, Assets.bundle.get("use"), Settings.language ? 145 : 135, 15);
		}
		else
		{
			Assets.font.setColor(1, 1, 1, 1);
			
			Assets.font.draw(game.batch, Assets.bundle.get("language"), 145, 165);
			
			Assets.frame[0].draw(game.batch, Settings.language ? 68 : 188, 101, 64, 44);
			
			game.batch.draw(Assets.flag[0], 70, 103, 60, 40);
			game.batch.draw(Assets.flag[1], 190, 103, 60, 40);
			
			Assets.font.draw(game.batch, Assets.bundle.get("sound"), 148, 87);
			
			if(Settings.sound)
				Assets.font.setColor(0, world.getSelectedOption() == 1 ? 0 : 2, 1, 1);
			else
				Assets.font.setColor(1, 1, 1, 1);
			Assets.font.draw(game.batch, Assets.bundle.get("enabled"), 82, 53);
			if(Settings.sound)
				Assets.font.setColor(1, 1, 1, 1);
			else
				Assets.font.setColor(0, world.getSelectedOption() == 1 ? 0 : 2, 1, 1);
			Assets.font.draw(game.batch, Assets.bundle.get("disabled"), 215, 53);
			
			if(world.getSelectedOption() == 2)
				Assets.font.setColor(0, 2, 1, 1);
			else
				Assets.font.setColor(1, 1, 1, 1);
			Assets.font.draw(game.batch, Assets.bundle.get("exit"), 152, 20);
		}
		
		game.batch.end();
	}
	
	private void renderLights()
	{
		Light l;
		if(world.isLoaded())
		{
			Map<String, Light> lights = world.getLevel().getScene().getLights();
			for(Entry<String, Light> entry: lights.entrySet())
			{
				l = entry.getValue();
				if((l.isVisible() || world.isEditing()) 
						&& (l.getX() + l.getWidth() > camera.position.x - VIEWPORT_WIDTH/2 
						|| l.getX() < camera.position.x + VIEWPORT_WIDTH/2))
					game.batch.draw(Assets.lights.get(entry.getKey()), l.getX(), l.getY(), l.getWidth(), l.getHeight());
			}
		}
		
		l = world.getPlayer().getLight();
		if(l.isVisible() && world.getPlayer().isVisible())
			game.batch.draw(Assets.player_light, l.getX(), l.getY(), l.getWidth(), l.getHeight());
	}
	
	private void renderObjects()
	{
		if(world.isLoaded())
		{	
			Map<String, Entity> entities = world.getLevel().getScene().getEntities();
			Entity e;
			for(Entry<String, Entity> entry: entities.entrySet())
			{
				e = entry.getValue();
				if((e.isVisible() || world.isEditing()) 
						&& (e.getX() + e.getWidth() > camera.position.x - VIEWPORT_WIDTH/2 
						|| e.getX() < camera.position.x + VIEWPORT_WIDTH/2))
					game.batch.draw(Assets.entities.get(entry.getKey()), e.getX(), e.getY(), e.getWidth(), e.getHeight());
			}
		}
		
		Player p = world.getPlayer();
		if(p.isVisible())
			game.batch.draw(Assets.player[p.getCharacter()][p.getState()].getKeyFrame(p.getLocked() ? 0 : p.getTime()), p.getX(), p.getY(), p.getWidth(), p.getHeight());
	}
	
	private void renderUI()
	{
		if((world.getSelected() != null || !world.getMessages().isEmpty()) && !world.getInventory().isVisible() && !world.getPlayer().getLocked())
		{
			Assets.frame[0].draw(game.batch, 125, 0, 70, 20);
			Assets.font.draw(game.batch, Assets.bundle.get("use"), Settings.language ? 145 : 130, 15);
		}
		
		if(!world.getMessages().isEmpty())
		{
			Assets.frame[0].draw(game.batch, 16, 145, 288, 35);
			Assets.font.drawWrapped(game.batch, world.getMessages().getCurrent(), 20, 175, 280);
		}
		
		if(!world.getNotifications().isEmpty() && !world.getInventory().isVisible() && world.getMessages().isEmpty())
			Assets.font.drawWrapped(game.batch, world.getNotifications().getCurrent(), 2, 178, 315);
		
		
		if(!world.getActions().isEmpty() && world.getMessages().isEmpty()) 
		{
			String[] actions = world.getActions().getActions();
			for(int i = 0; i < actions.length; i++)
			{
				if(i == world.getActions().getSelectedNumber())
					game.batch.draw(Assets.blueAlpha, 140, 160 - i*15, 
							Settings.language ? Assets.level_bundle.get(world.getActions().getSelected()).length()*8
									: Assets.level_bundle.get(world.getActions().getSelected()).length()*10, 12);
				
				Assets.font.draw(game.batch, Assets.level_bundle.get(actions[i]), 145, 170 - i*15);
			}
		}
		
		if(world.getInventory().isVisible())
		{
			Inventory inv = world.getInventory();
			for(int i = 0; i < 3; i++)
			{
				Rectangle r = inv.getSection(i);
				Assets.frame[inv.getSectionIndex() == i ? 1 : 0].draw(game.batch, r.x, r.y, r.width, r.height);
				switch(i)
				{
				case 0:
					Assets.font.draw(game.batch, Assets.bundle.get("items"), r.x + 11, r.y + 14);
					break;
				case 1:
					Assets.font.draw(game.batch, Assets.bundle.get("notes"), r.x + 11, r.y + 14);
					break;
				case 2:
					Assets.font.draw(game.batch, Assets.bundle.get("goals"), r.x + 7, r.y + 14);
					break;
				}
			}
			
			Assets.frame[0].draw(game.batch, 15, 10, 290, 131);
			
			Selector s = inv.getSelector();
			for(int i = s.getShift(); i < s.getShift() + s.getMax(); i++)
			{
				if(i < inv.getStuffSize())
				{
					switch(inv.getSectionIndex())
					{
					case 0:
						if(inv.getSelector().getSelected() == i)
							game.batch.draw(Assets.blueAlpha, 25, 106 - (i - s.getShift())*25 - (i - s.getShift())*5, 270, 30);
						game.batch.draw(Assets.item_entities.get(inv.getStuff(i).getName()), 30, 108 - (i - s.getShift())*25 - (i - s.getShift())*5, 25, 25);
						Assets.font.draw(game.batch, Assets.text_bundle.get(inv.getStuff(i).getName()), 60, 125 - (i - s.getShift())*25 - (i - s.getShift())*5);
						break;
					case 1:
						if(inv.getSelector().getSelected() == i)
							game.batch.draw(Assets.blueAlpha, 25, 106 - (i - s.getShift())*25 - (i - s.getShift())*5, 270, 30);
						game.batch.draw(Assets.note, 30, 108 - (i - s.getShift())*25 - (i - s.getShift())*5, 25, 25);
						Assets.font.draw(game.batch, Assets.text_bundle.get(inv.getStuff(i).getName()), 60, 125 - (i - s.getShift())*25 - (i - s.getShift())*5);
						break;
					case 2:
						if(inv.getSelector().getSelected() == i)
							game.batch.draw(Assets.blueAlpha, 25, 120 - (i - s.getShift())*10 - (i - s.getShift())*5, 270, 15);
						Assets.font.draw(game.batch, Assets.level_bundle.get(inv.getStuff(i).getName()), 30, 130 - (i - s.getShift())*10 - (i - s.getShift())*5);
						Goal g = (Goal) inv.getStuff(i);
						if(g.isDone() == true)
							game.batch.draw(Assets.whiteAlpha, 25, 125 - (i - s.getShift())*10 - (i - s.getShift())*5, 270, 1);
						break;
					}
				}
			}
		}
	}
	
	private void renderNote()
	{
		game.batch.draw(Assets.blackAlpha, 0, 0, 320, 180);
		Assets.font.drawMultiLine(game.batch, 
				Assets.text_bundle.get(world.getInventory().getStuff(world.getInventory().getSelector().getSelected()).getName()), 120, 175);
		Assets.font.drawWrapped(game.batch, 
				Assets.text_bundle.get(world.getInventory().getStuff(world.getInventory().getSelector().getSelected()).getName() + "_text"), 20, 150, 280, HAlignment.CENTER);
	}
	
	private void updateCamera()
	{
		camera.position.x = world.getPlayer().getX() + world.getPlayer().getWidth()/2;
		
		if(world.isLoaded()) 
		{
			Scene s = world.getLevel().getScene();
			if (camera.position.x < s.getMinX() + VIEWPORT_WIDTH / 2)
				camera.position.x = s.getMinX() + VIEWPORT_WIDTH / 2;
			if (camera.position.x > s.getMaxX() - VIEWPORT_WIDTH / 2)
				camera.position.x = s.getMaxX() - VIEWPORT_WIDTH / 2;
		}
		
		camera.update();
	}
	
	public void setLightOptions()
	{
		if(world.isLoaded())
		{
			intensity = world.getLevel().getScene().getIntensity();
			color = world.getLevel().getScene().getColor();
		}
		
		Assets.lightShader.begin();
		Assets.lightShader.setUniformi("u_lightmap", 1);
		Assets.lightShader.setUniformf("ambientColor", color.x, color.y, color.z, intensity);
		Assets.lightShader.setUniformf("resolution", SCREEN_WIDTH, SCREEN_HEIGHT);
		Assets.lightShader.end();
	}
	
	public OrthographicCamera getCamera() { return camera; }
	
	public void resume() { postProcessor.rebind(); }
	
	public void dispose()
	{
		frameBuffer.dispose();
		postProcessor.dispose();
	}
}