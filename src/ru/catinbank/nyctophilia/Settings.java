package ru.catinbank.nyctophilia;

import ru.catinbank.framework.ClipManager;
import ru.catinbank.framework.MusicFader;
import ru.catinbank.framework.ScriptLauncher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Settings 
{
	public static boolean first = true;
	public static boolean language = true;
	public static boolean sound = true;
	public static boolean savedGame = false;
	public static int actions = 0;
	public static String file1 = "";
	public static String file2 = "";
	public static String file3 = "";
	
	public static void save()
	{
		Preferences prefs = Gdx.app.getPreferences("nyctophilia");
		prefs.putBoolean("language", language);
		prefs.putBoolean("first", first);
		prefs.putBoolean("sound", sound);
		prefs.putBoolean("save", savedGame);
		prefs.putInteger("actions", actions);
		prefs.flush();
	}
	
	public static void load()
	{
		Preferences prefs = Gdx.app.getPreferences("nyctophilia");
		language = prefs.getBoolean("language", true);
		first = prefs.getBoolean("first", true);
		sound = prefs.getBoolean("sound", true);
		savedGame = prefs.getBoolean("save", false);
		actions = prefs.getInteger("actions", 0);
		file1 = prefs.getString("file1", "");
		prefs.flush();
	}
	
	public static void saveGame(Level level, ClipManager clips, Inventory inventory, Player player)
	{
		savedGame = true;
		
		save();
		
		Gson gson = new GsonBuilder().create();
		file1 = gson.toJson(level);
		file2 = gson.toJson(clips);
		file3 = gson.toJson(inventory);
		
		Preferences prefs = Gdx.app.getPreferences("nyctophilia");
		prefs.putString("file1", file1);
		prefs.putString("file2", file2);
		prefs.putString("file3", file3);
		if(Assets.fader.getState() != MusicFader.State.FADED_IN)
			prefs.putBoolean("stopped", false);
		else
			prefs.putBoolean("stopped", true);
		prefs.putString("music", Assets.fader.getMusicName());
		prefs.putFloat("x", player.getX());
		prefs.putInteger("character", player.getCharacter());
		prefs.putBoolean("light", player.getLight().isVisible());
		prefs.putString("step", Assets.soundStepName);
		prefs.flush();
	}
	
	public static Level loadLevel(Level level, ScriptLauncher scripts)
	{
		Preferences prefs = Gdx.app.getPreferences("nyctophilia");
		file1 = prefs.getString("file1", "");
		Gson gson = new Gson();
		level = gson.fromJson(file1, Level.class);
		scripts.setFile(level.getName());
		return level;
	}
	
	public static ClipManager loadClips(ClipManager clips, String key)
	{
		Preferences prefs = Gdx.app.getPreferences("nyctophilia");
		file2 = prefs.getString("file2", "");
		Gson gson = new Gson();
		clips = gson.fromJson(file2, ClipManager.class);
		Assets.loadLevel(clips, key);
		return clips;
	}
	
	public static Inventory loadPlayer(Inventory inventory, Player player)
	{
		Preferences prefs = Gdx.app.getPreferences("nyctophilia");
		player.setX(prefs.getFloat("x", 30));
		player.changeCharacter(prefs.getInteger("character", 0));
		player.getLight().setVisible(prefs.getBoolean("light"));
		Assets.setStepSound(prefs.getString("step"));
		if(!prefs.getBoolean("stopped", false))
			Assets.fader.loadMusic(prefs.getString("music"));
		file3 = prefs.getString("file3", "");
		Gson gson = new Gson();
		inventory = gson.fromJson(file3, Inventory.class);
		inventory.setItems(player.getCharacter());
		for(Item i: inventory.getItems())
			Assets.addStuff(i.getName(), i.getX(), i.getY());
		return inventory;
	}
}
