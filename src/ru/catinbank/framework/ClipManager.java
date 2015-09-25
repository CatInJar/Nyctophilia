package ru.catinbank.framework;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;

public class ClipManager 
{
	private Map<String, Rectangle> itemRects;
	private Map<String, Rectangle> lightRects;
	
	public ClipManager()
	{
		itemRects = new HashMap<String, Rectangle>();
		lightRects = new HashMap<String, Rectangle>();
	}
	
	public void addItemRect(String key, Rectangle r) { itemRects.put(key, r); }
	
	public Map<String, Rectangle> getItemRects() { return itemRects; }
	
	public void addLightRect(String key, Rectangle r) { lightRects.put(key, r); }
	
	public Map<String, Rectangle> getLightRects() { return lightRects; }
}
