package ru.catinbank.nyctophilia;

import ru.catinbank.framework.Fader;
import ru.catinbank.framework.MusicFader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.filters.Combine;
import com.bitfire.postprocessing.filters.CrtScreen.Effect;
import com.bitfire.postprocessing.filters.CrtScreen.RgbMode;
import com.bitfire.utils.ShaderLoader;

public class MainMenuScreen implements Screen, InputProcessor
{
	private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
	private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
	private final int VIEWPORT_WIDTH = 1920;
	private final int VIEWPORT_HEIGHT = 1080;
	private final int MIN_SELECTED = Settings.savedGame ? 0 : 1;
	private final String[] menu = {"continue", "play", "options", "exit"};
	
	private final Nyctophilia game;
	private OrthographicCamera camera;
	private Fader fader;
	private FrameBuffer frameBuffer;
	private PostProcessor postProcessor;
	private CrtMonitor crt;
	
	private State state = State.MENU;
	private float intensity = 0.3f;
	private Vector3 color = new Vector3(0.2f, 0.2f, 0.2f);
	private float y = 0;
	private int selected = 0;
	private int selectedOption = 0;
	
	private enum State
	{
		MENU,
		OPTIONS
	}
	
	public MainMenuScreen(Nyctophilia game)
	{
		this.game = game;
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		fader = new Fader(game.batch);
		
		frameBuffer = new FrameBuffer(Format.RGBA8888, SCREEN_WIDTH, SCREEN_HEIGHT, false);
		
		Assets.lightShader.begin();
		Assets.lightShader.setUniformi("u_lightmap", 1);
		Assets.lightShader.setUniformf("ambientColor", color.x, color.y, color.z, intensity);
		Assets.lightShader.setUniformf("resolution", SCREEN_WIDTH, SCREEN_HEIGHT);
		Assets.lightShader.end();
		
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
        
        selected = MIN_SELECTED;
        
        Assets.font.setScale(6f);
        Assets.fader.loadMusic("Nyctophilia - Shattered Dreams");
	}
	
	@Override
	public void render(float delta) 
	{
		fader.update();
		Assets.fader.update();
		
		frameBuffer.begin();
		game.batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setShader(Assets.defaultShader);
		game.batch.begin();
		game.batch.draw(Assets.back_light, 0, 0, 1920, 1080);
		game.batch.end();
		frameBuffer.end();
		
		postProcessor.capture();
		game.batch.setShader(Assets.lightShader);
		game.batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.begin();
		frameBuffer.getColorBufferTexture().bind(1);
		Assets.background_menu.bind(0);
		game.batch.draw(Assets.background, 0, y, 1920, 1080);
		game.batch.draw(Assets.background, 0, y - 1080, 1920, 1080);
		game.batch.end();
		postProcessor.render();
		
		game.batch.setShader(null);
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		
		if(state == State.MENU)
			drawMenu();
		else
			drawOptions();
		
		game.batch.end();
		
		y += 100 * delta;
		if(y >= 1080)
			y = 0;
		
		if(fader.getState() == Fader.State.FADED_IN && (Assets.fader.getState() == MusicFader.State.FADED_IN || !Settings.sound))
		{
			switch(selected)
			{
			case 0:
				game.setScreen(new GameScreen(game, "load"));
				break;
			case 1:
				Settings.actions = 0;
				game.setScreen(new GameScreen(game, "intro"));
				break;
			case 3:
				Gdx.app.exit();
			}
		}
	}
	
	private void drawOptions()
	{
		Assets.font.setColor(1, 1, 1, 1);
		
		Assets.font.draw(game.batch, Assets.bundle.get("language"), 850, 1000);
		
		Assets.frame[0].draw(game.batch, Settings.language ? 418 : 1138, 618, 364, 244);
		
		game.batch.draw(Assets.flag[0], 420, 620, 360, 240);
		game.batch.draw(Assets.flag[1], 1140, 620, 360, 240);
		
		Assets.font.draw(game.batch, Assets.bundle.get("sound"), 890, 520);
		
		if(Settings.sound)
			Assets.font.setColor(0, selectedOption == 1 ? 0 : 2, 1, 1);
		else
			Assets.font.setColor(1, 1, 1, 1);
		Assets.font.draw(game.batch, Assets.bundle.get("enabled"), 490, 320);
		
		if(Settings.sound)
			Assets.font.setColor(1, 1, 1, 1);
		else
			Assets.font.setColor(0, selectedOption == 1 ? 0 : 2, 1, 1);
		Assets.font.draw(game.batch, Assets.bundle.get("disabled"), 1290, 320);
		
		if(selectedOption == 2)
			Assets.font.setColor(0, 2, 1, 1);
		else
			Assets.font.setColor(1, 1, 1, 1);
		Assets.font.draw(game.batch, Assets.bundle.get("exit"), 910, 120);
	}
	
	private void drawMenu()
	{
		Assets.font_menu.setScale(1f);
		Assets.font_menu.draw(game.batch, "NYCTOPHILIA", 350, 800);
		Assets.font_menu.setScale(0.35f);
		Assets.font_menu.draw(game.batch, "A GAME BY DENIS VYATKIN", 745, 650);
		
		for(int i = 0; i < menu.length; i++)
		{
			Assets.font.setColor(1, 1, 1, 1);
			if(selected == i)
				Assets.font.setColor(0, 2, 1, 1);
			if(i == 0 && !Settings.savedGame)
				Assets.font.setColor(0.5f, 0.5f, 0.5f, 1);
				
			Assets.font.draw(game.batch, Assets.bundle.get(menu[i]), 200 + i*450, 300);
		}
		
		Assets.font.setColor(1, 1, 1, 1);
		Assets.font.draw(game.batch, Assets.bundle.get("use"), Settings.language ? 870 : 780, 90);
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		Assets.playSound("click");
		if(state == State.MENU)
		{
			if(fader.getState() == Fader.State.FADED_OUT)
			{
				if(keycode == Input.Keys.LEFT)
				{
					selected--;
					if (selected < MIN_SELECTED)
						selected = 3;
				}
				if(keycode == Input.Keys.RIGHT)
				{
					selected++;
					if (selected > 3)
						selected = MIN_SELECTED;
				}
			}
			if(keycode == Input.Keys.X)
			{
				if(selected != 2)
				{
					fader.fadeIn();
					Assets.fader.fadeIn();
				}
				else
					state = State.OPTIONS;
			}
		}
		else
		{
			if(keycode == Input.Keys.UP)
			{
				selectedOption--;
				if (selectedOption < 0)
					selectedOption = 2;
			}
			if(keycode == Input.Keys.DOWN)
			{
				selectedOption++;
				if (selectedOption > 2)
					selectedOption = 0;
			}
			if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT)
			{
				switch(selectedOption)
				{
				case 0:
					Settings.language = !Settings.language;
					Assets.loadLanguage();
					Assets.font.setScale(6f);
					break;
				case 1:
					Settings.sound = !Settings.sound;
					Assets.fader.mute();
				}
			}
			if(keycode == Input.Keys.X || keycode == Input.Keys.ESCAPE)
			{
				if(selectedOption == 2)
				{
					selectedOption = 0;
					Settings.save();
					state = State.MENU;
				}
			}
		}
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