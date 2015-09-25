package ru.catinbank.framework;

import ru.catinbank.nyctophilia.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicFader 
{
	private final float FADE_SPEED = 0.7f;
	private final float CONST_VOLUME = 0.6f;
	private float MAX_VOLUME = CONST_VOLUME;
	
	public enum State
	{
		FADE_IN,
		FADE_OUT,
		FADED_IN,
		FADED_OUT
	}
	
	private State state = State.FADE_OUT;
	private Music music;
	private float volume = 0;
	private String nextMusic, currentMusic;
	
	public MusicFader(Music music)
	{
		nextMusic = "";
		currentMusic = "";
		this.music = music;
		mute();
	}
	
	public void update()
	{
		float delta = Gdx.graphics.getDeltaTime(); 
		
		if(state != State.FADED_IN && state != State.FADED_OUT)
		{
			if(state == State.FADE_IN)
				volume -= FADE_SPEED * delta;
			else if(state == State.FADE_OUT)
				volume += FADE_SPEED * delta;
			
			if(state == State.FADE_IN && volume < 0)
			{
				volume = 0;
				if(music != null)
					music.stop();
				state = State.FADED_IN;
				if(currentMusic.compareTo(nextMusic) != 0)
				{
					currentMusic = new String(nextMusic);
					music.dispose();
					music = Gdx.audio.newMusic(Gdx.files.internal("music/" + nextMusic + ".mp3"));
					music.play();
					music.setLooping(true);
					state = State.FADE_OUT;
				}
			}
			
			if(state == State.FADE_OUT && volume > MAX_VOLUME)
			{
				volume = MAX_VOLUME;
				state = State.FADED_OUT;
			}
			
			if(music != null)
				music.setVolume(volume);
		}
	}
	
	public void loadMusic(String file)
	{
		nextMusic = file;
		if(music != null && music.isPlaying())
			state = State.FADE_IN;
		else
		{
			currentMusic = new String(nextMusic);
			music = Gdx.audio.newMusic(Gdx.files.internal("music/" + nextMusic + ".mp3"));
			music.play();
			music.setLooping(true);
			state = State.FADE_OUT;
		}
	}
	
	public void mute()
	{
		if(Settings.sound)
		{
			MAX_VOLUME = CONST_VOLUME;
			volume = CONST_VOLUME;
			state = State.FADED_OUT;
		}
		else
		{
			MAX_VOLUME = 0;
			volume = 0;
			state = State.FADED_IN;
		}
		if(music != null)
			music.setVolume(volume);
	}
	
	public String getMusicName() { return nextMusic; }
	
	public State getState() { return state; }
	
	public void fadeIn() { state = State.FADE_IN; }
	
	public void fadeOut() 
	{ 
		state = State.FADE_OUT;
		music.play();
	}
	
	public boolean isFading(){ return (state == State.FADED_IN || state == State.FADED_OUT) ? false : true; }
}