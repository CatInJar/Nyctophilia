package ru.catinbank.nyctophilia;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector3;

import ru.catinbank.framework.Entity;
import ru.catinbank.framework.Light;

public class Scene 
{
	private Map<String, Entity> entities;
	private Map<String, Light> lights;
	private float startX = 30, maxX = 320, minX = 0;
	private float intensity = 0.7f;
	private Vector3 color = new Vector3(0.3f, 0.3f, 0.7f);
	
	public Scene()
	{
		entities = new LinkedHashMap<String, Entity>();
		lights = new LinkedHashMap<String, Light>();
	}
	
	public Map<String, Entity> getEntities() { return entities; }
	
	public Map<String, Light> getLights() { return lights; }
	
	public Entity getEntity(String key) { return entities.get(key); }
	
	public Light getLight(String key) { return lights.get(key); }
	
	public void addEntity(String key, Entity e) { entities.put(key, e); }
	
	public void addLight(String key, Light l) { lights.put(key, l); }
	
	public float getIntensity() { return intensity; }
	
	public void setIntensity(float intensity) { this.intensity = intensity; }
	
	public Vector3 getColor() { return color; }
	
	public void setColor(Vector3 color) { this.color = color; }
	
	public float getStartX() { return startX; }
	
	public void setStartX(float startX) { this.startX = startX; }
	
	public float getMaxX() { return maxX; }
	
	public void setMaxX(float maxX) { this.maxX = maxX; }
	
	public float getMinX() { return minX; }
	
	public void setMinX(float minX) { this.minX = minX; }
}
