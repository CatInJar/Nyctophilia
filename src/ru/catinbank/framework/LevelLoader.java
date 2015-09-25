package ru.catinbank.framework;

import ru.catinbank.nyctophilia.Assets;
import ru.catinbank.nyctophilia.Level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LevelLoader 
{
	public static Level loadLevel(String key)
	{
		FileHandle handler = Gdx.files.local("levels/" + key + ".txt");
		Gson gson = new Gson();
		String file = handler.readString();
		Level level = gson.fromJson(file, Level.class);
		return level;
	}
	
	public static ClipManager loadClips(String key)
	{
		FileHandle handler = Gdx.files.local("levels/" + key + "_items" + ".txt");
		String file = handler.readString();
		Gson gson = new Gson();
		ClipManager clips = gson.fromJson(file, ClipManager.class);
		Assets.loadLevel(clips, key);
		return clips;
	}
	
	public static void saveLevel(Level level, ClipManager clips)
	{
		FileHandle handler = Gdx.files.local("levels/" + level.getName() + ".txt");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String file = gson.toJson(level);
		handler.writeString(file, false);
		handler = Gdx.files.local("levels/" + level.getName() + "_items" + ".txt");
		file = gson.toJson(clips);
		handler.writeString(file, false);
	}
}