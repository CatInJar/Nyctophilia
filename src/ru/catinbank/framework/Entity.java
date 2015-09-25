package ru.catinbank.framework;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Entity 
{
    protected Vector2 position;
    protected Rectangle bounds;
    private String function = null;
    protected boolean visible;
    protected boolean collision = false;
    private boolean usable = false;
	private boolean event = false;
    
    public Entity() 
    {
        position = new Vector2();
        bounds = new Rectangle();
    }
    
    public Entity(float x, float y, float width, float height, 
			boolean visible, boolean collision, 
			boolean usable, boolean event, String function)
    {
        position = new Vector2(x, y);
        bounds = new Rectangle(x, y, width, height);
        this.visible = visible;
        this.collision = collision;
        this.usable = usable;
		this.event = event;
		this.function = function;
    }
    
    public Entity(float x, float y, float width, float height, boolean visible) 
    {
        position = new Vector2(x, y);
        bounds = new Rectangle(x, y, width, height);
        this.visible = visible;
    }
    
    public void setPosition(float x, float y) 
    { 
    	position = new Vector2((int) x, (int) y); 
    	bounds.setPosition((int) x, (int) y);
    }
    
    public boolean isEvent() { return event; }

	public void setEvent(boolean event) { this.event = event; }

	public void setFunction(String function) { this.function = function; }

	public String getFunction() { return function; }
	
	public void setUsable(boolean usable) { this.usable = usable; } 
	
	public boolean isUsable() { return usable; }
    
    public void setCollide(boolean collision) { this.collision = collision; } 
	
	public boolean isCollide() { return collision; }
    
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public void setVisible() { this.visible = !visible; }
	
	public boolean isVisible() { return visible; }
    
    public float getCenterX() { return position.x + bounds.width/2; }
    
    public float getCenterY() { return position.y + bounds.height/2; }
    
    public float getX() { return position.x; }
    
    public float getY() { return position.y; }
    
    public float getWidth() { return bounds.width; }
    
    public float getHeight() { return bounds.height; }
    
    public Rectangle getBounds() { return bounds; }
    
    public Vector2 getPosition() { return position; }
    
    public void setX(float x) { position.x = x; }
    
    public void setY(float y) { position.y = y; }
    
    public void setWidth(float width) { bounds.width = width; }
    
    public void setHeight(float height) { bounds.height = height; }
    
    public void setBounds(float x, float y, float width, float height) { bounds = new Rectangle(x, y, width, height); }
}
