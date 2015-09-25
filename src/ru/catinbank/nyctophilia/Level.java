package ru.catinbank.nyctophilia;

import java.util.HashMap;
import java.util.Map;

public class Level 
{
	private Map<String, Scene> scenes;
	private String currentSceneName;
	private String name;
	private boolean lightEnabled = true;
	private boolean effectEnabled = true;
	
	public Level()
	{
		scenes = new HashMap<String, Scene>();
	}
	
	public boolean notLoaded()
	{
		if(scenes.get(currentSceneName).getEntities() == null || scenes.get(currentSceneName).getLights() == null)
			return true;
		
		return false;
	}
	
	public void setScene(String nextScene) 
	{ 
		currentSceneName = nextScene;
	}
	
	public boolean isEffectEnabled() { return effectEnabled; }
	
	public void setEffectEnabled(boolean light) { this.effectEnabled = light; }
	
	public boolean isLightEnabled() { return lightEnabled; }

	public void setLightEnabled(boolean light) { this.lightEnabled = light; }
	
	public Scene getScene() { return scenes.get(currentSceneName); }
	
	public Scene getScene(String name) { return scenes.get(name); }
	
	public String getSceneName() { return currentSceneName; }
	
	public void addScene(String key, Scene newScene) { scenes.put(key, newScene); }
	
	public void removeScene(String key) { scenes.remove(key); }
	
	public String getName() { return name; }
}