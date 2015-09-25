package ru.catinbank.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Fader 
{
	private final float FADE_SPEED = 2f;
	
	public enum State
	{
		FADE_IN,
		FADE_OUT,
		FADED_IN,
		FADED_OUT
	}
	
	private State state = State.FADE_OUT;
	private SpriteBatch batch;
	private float alpha = 0;
	
	public Fader(SpriteBatch batch)
	{
		this.batch = batch;
		this.batch.setColor(1, 1, 1, alpha);
	}
	
	public void update()
	{
		float delta = Gdx.graphics.getDeltaTime(); 
		
		if(state != State.FADED_IN && state != State.FADED_OUT)
		{
			if(state == State.FADE_IN)
				alpha -= FADE_SPEED * delta;
			else if(state == State.FADE_OUT)
				alpha += FADE_SPEED * delta;
			
			if(state == State.FADE_IN && alpha < 0)
			{
				alpha = 0;
				state = State.FADED_IN;
			}
			
			if(state == State.FADE_OUT && alpha > 1)
			{
				alpha = 1;
				state = State.FADED_OUT;
			}
			
			batch.setColor(1, 1, 1, alpha);
		}
	}
	public State getState() { return state; }
	
	public void fadeIn() { state = State.FADE_IN; }
	
	public void fadeOut() { state = State.FADE_OUT; }
	
	public boolean isFading(){ return (state == State.FADED_IN || state == State.FADED_OUT) ? false : true; }
}