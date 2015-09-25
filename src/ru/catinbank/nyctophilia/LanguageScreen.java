package ru.catinbank.nyctophilia;

import ru.catinbank.framework.Fader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class LanguageScreen implements Screen, InputProcessor
{
	private final int VIEWPORT_WIDTH = 320;
	private final int VIEWPORT_HEIGHT = 180;
	
	private final Nyctophilia game;
	private OrthographicCamera camera;
	private Fader fader;
	
	public LanguageScreen(Nyctophilia game)
	{
		this.game = game;
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		fader = new Fader(game.batch);
	}
	
	@Override
	public void render(float delta) 
	{
		fader.update();
		
		game.batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.batch.begin();
		
		Assets.frame[0].draw(game.batch, Settings.language ? 68 : 188, 68, 64, 44);
		
		game.batch.draw(Assets.flag[0], 70, 70, 60, 40);
		game.batch.draw(Assets.flag[1], 190, 70, 60, 40);
		
		Assets.frame[0].draw(game.batch, 125, 0, 70, 20);
		Assets.font.draw(game.batch, Settings.language ? "Press X" : "Yf;fnm {", Settings.language ? 145 : 135, 15);
		
		game.batch.end();
		
		if(fader.getState() == Fader.State.FADED_IN)
		{
			Settings.first = false;
			Settings.save();
			Assets.loadLanguage();
			game.setScreen(new MainMenuScreen(game));
		}
	}
	
	@Override
	public boolean keyDown(int keycode)
	{ 
		if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT)
		{
			Settings.language = !Settings.language;
			Assets.loadLanguage();
		}
		
		if(keycode == Input.Keys.X)
			fader.fadeIn();

		return false;
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

	@Override
	public boolean keyUp(int keycode) { return false; }

	@Override
	public boolean keyTyped(char character) { return false; }

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

	@Override
	public boolean mouseMoved(int screenX, int screenY) { return false; }

	@Override
	public boolean scrolled(int amount) { return false; }
}