package ru.catinbank.nyctophilia;

import ru.catinbank.framework.MusicFader;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Nyctophilia extends Game
{
	public static boolean developerMode = false;
	private final byte MAX_SPRITES = 127;
	SpriteBatch batch;
	
	@Override
	public void create () 
	{
		batch = new SpriteBatch(MAX_SPRITES);
		Settings.load();
		Assets.load();
		Assets.fader = new MusicFader(Assets.music);
		Pixmap pm;
		if(developerMode)
			pm = new Pixmap(Gdx.files.internal("stuff/cursor.png"));
		else
			pm = new Pixmap(Gdx.files.internal("stuff/cursor2.png"));
		Gdx.input.setCursorImage(pm, 0, 0);
		if(developerMode)
			this.setScreen(new GameScreen(this));
		else
			this.setScreen(new LogoScreen(this));
	}

	@Override
	public void render() 
	{
		super.render();
	}
	
	public void dispose() 
	{
		batch.dispose();
		Assets.dispose();
	}
}