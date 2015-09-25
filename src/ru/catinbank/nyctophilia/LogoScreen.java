package ru.catinbank.nyctophilia;

import ru.catinbank.framework.Fader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.filters.Combine;
import com.bitfire.postprocessing.filters.CrtScreen.Effect;
import com.bitfire.postprocessing.filters.CrtScreen.RgbMode;
import com.bitfire.utils.ShaderLoader;

public class LogoScreen implements Screen
{
	private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
	private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
	private final float MAX_TIME = 3.5f;
	
	private final Nyctophilia game;
	private OrthographicCamera camera;
	private PostProcessor postProcessor;
	private CrtMonitor crt;
	private Fader fader;
	private float time = 0;

	public LogoScreen(Nyctophilia game)
	{
		this.game = game;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		
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
        
        fader = new Fader(game.batch);
        
        Assets.playSound("intro");
	}

	@Override
	public void render(float delta) 
	{
		time += delta;
		crt.setTime(time);
		fader.update();
		
		postProcessor.capture();
		game.batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.begin();
		game.batch.draw(Assets.logo, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		game.batch.end();
		postProcessor.render();
		
		if((time > MAX_TIME || Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) && fader.getState() != Fader.State.FADED_IN)
			fader.fadeIn();
		
		if(fader.getState() == Fader.State.FADED_IN)
		{
			if(Settings.first == false)
				game.setScreen(new MainMenuScreen(game));
			else
				game.setScreen(new LanguageScreen(game));
		}
	}
	
	@Override
	public void dispose() { Assets.logo.dispose(); }
	
	@Override
	public void resume() {}
	
	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}
}