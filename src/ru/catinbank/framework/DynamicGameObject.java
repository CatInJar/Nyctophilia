package ru.catinbank.framework;


import com.badlogic.gdx.math.Vector2;

public class DynamicGameObject extends Entity {
	protected Vector2 velocity;
	protected Vector2 accel;
    
    public DynamicGameObject() {
        super();
        velocity = new Vector2();
        accel = new Vector2();
    }
    
    public DynamicGameObject(float x, float y, float width, float height, boolean visible, boolean collision) 
    {
        super(x, y, width, height, visible);
        velocity = new Vector2();
        accel = new Vector2();
    }
    
    public DynamicGameObject(float x, float y, float width, float height, boolean visible) 
    {
        super(x, y, width, height, visible);
        velocity = new Vector2();
        accel = new Vector2();
    }
    
    public void update(float delta)
    {
    	position.add(velocity.x * delta, velocity.y * delta);
    	bounds.setPosition(position);
    }
    
    public Vector2 getVec() { return velocity; }
    
    public Vector2 getAccel() { return accel; }
    
    public void setVec(float x, float y) { velocity = new Vector2(x, y); }
    
    public void setVecX(float x) { velocity.x = x; }
    
    public void setVecY(float y) { velocity.y = y; }
    
    public void setAccel(float x, float y) { accel = new Vector2(x, y); }
    
    public void setAccelX(float x) { accel.x = x; }
    
    public void setAccelY(float y) { accel.y = y; }
}
