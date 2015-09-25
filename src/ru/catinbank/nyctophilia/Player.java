package ru.catinbank.nyctophilia;

import ru.catinbank.framework.DynamicGameObject;
import ru.catinbank.framework.Light;

public class Player extends DynamicGameObject
{
	private final static float PLAYER_Y = 15;
	private final static float PLAYER_WIDTH = 32;
	private final static float PLAYER_HEIGHT = 112;
	private final static float PLAYER_VELOCITY = 60;
	private final static float LIGHT_WIDTH = 120;
	private final static float LIGHT_HEIGHT = 120;
	private final static float MAX_STEPTIME = 0.6f;
	
	public enum Direction
	{
		LEFT,
		RIGHT
	}
	
	public static class State
	{
		public final static int STANDING = 0;
		public final static int WALKING = 1;
	}
	
	private Direction direction;
	private Light light;
	private float time = 0;
	private int state = 0;
	private int character = 0;
	private boolean locked = false;
	private float stepTime = 0;
	private String stepName;
	
	public Player(float x)
	{
		super(x, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, true);
		direction = Direction.RIGHT;
		light = new Light(position.x - LIGHT_WIDTH/2, position.y - LIGHT_HEIGHT/2, LIGHT_WIDTH, LIGHT_HEIGHT, true);
		state = State.STANDING;
	}
	
	public void update(float delta)
	{
		position.add(velocity.x * delta, velocity.y * delta);
    	bounds.setPosition(position);
    	light.setPosition(getCenterX() - LIGHT_WIDTH/2, getCenterY() - LIGHT_HEIGHT/2 + 20);
    	
    	time += delta;
    	
    	if(time > Assets.player[character][state].getAnimationDuration())
    		time = 0;
    	
    	if(state == State.WALKING)
    		stepTime += delta;
    	
    	if(stepTime > MAX_STEPTIME)
    	{
    		stepTime = 0;
    		if(Assets.soundStepName.compareTo("") != 0)
    			Assets.playStepSound();
    	}
	}
	
	public void changeCharacter()
	{
		if(character == 0)
		{
			character = 1;
			stepName = Assets.soundStepName;
			Assets.setStepSound("wood");
		}
		else
		{
			character = 0;
			Assets.setStepSound(stepName);
		}
		Assets.playSound("electro");
	}
	
	public void changeCharacter(int c) { character = c; }
	
	public void setVec(Direction d)
	{		
		direction = d;
		if(direction == Direction.RIGHT)
			velocity.set(PLAYER_VELOCITY, 0); 
		else
			velocity.set(-PLAYER_VELOCITY, 0);
		
		setState(State.WALKING);
	}
	
	public void stop() 
	{
		velocity.setZero(); 
		position.set((int) position.x, (int) position.y);
		setState(State.STANDING);
		stepTime = 0;
	}
	
	public void reverse(float delta)
	{
		position.add(velocity.scl(-1).scl(delta));
		setState(State.STANDING);
	}
	
	public void setState(int state)
	{
		if(this.state != state)
			time = 0;
		this.state = state;	
	}
	
	public int getCharacter() { return character; }
	
	public void lookRight() { direction = Direction.RIGHT; }
	
	public void lookLeft() { direction = Direction.LEFT; }
	
	public boolean getLocked() { return locked; }
	
	public void setLocked(boolean locked) { this.locked = locked; }
	
	public int getState() { return state; }
	
	public void setDirection(Direction d) { direction = d; }
	
	public float getTime() { return time; }
	
	public Light getLight() { return light; }
	
	@Override
	public void setX(float x) { position.x = x; light.setX(getCenterX() - LIGHT_WIDTH/2); }
	
	@Override
	public float getX() { return (direction == Direction.RIGHT) ? position.x : position.x + bounds.width; }
	
	@Override
	public float getWidth() { return (direction == Direction.RIGHT) ? bounds.width : -bounds.width; }
}