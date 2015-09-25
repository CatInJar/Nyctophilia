package ru.catinbank.nyctophilia;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import ru.catinbank.framework.ClipManager;
import ru.catinbank.framework.MusicFader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.I18NBundle;

public class Assets 
{
	public static Sound sound;
	public static Sound sound_step;
	public static String soundName = "";
	public static String soundStepName = "";
	public static int stepNumber = 1;
	public static Music music;
	public static MusicFader fader;
	
	public static ClipManager clips;
	public static Texture items;
	public static Texture light;
	public static Texture stuff;
	public static TextureRegion note;
	public static Animation[][] player;
	public static TextureRegion player_light;
	
	public static Texture panelBack;
	public static Texture UI;
	public static NinePatch[] frame;
	public static TextureRegion blueAlpha;
	public static TextureRegion whiteAlpha;
	public static TextureRegion blackAlpha;
	public static TextureRegion[] flag;
	public static Texture logo;
	public static Texture background_menu;
	public static TextureRegion background;
	public static TextureRegion back_light;
	
	public static Texture level_items;
	public static Map<String, TextureRegion> entities;
	public static Map<String, TextureRegion> item_entities;
	public static Texture level_lights;
	public static Map<String, TextureRegion> lights;
	
	public static ShaderProgram defaultShader;
	public static ShaderProgram lightShader;
	
	public static BitmapFont font;
	public static BitmapFont font_menu;
	public static I18NBundle bundle;
	public static I18NBundle level_bundle;
	public static I18NBundle text_bundle;
	
	public static void load()
	{
		items = new Texture(Gdx.files.internal("stuff/items.png"));
		light = new Texture(Gdx.files.internal("stuff/light.png"));
		stuff = new Texture(Gdx.files.internal("stuff/inventory.png"));
		
		note = new TextureRegion(stuff, 0, 0, 25, 25);
		player = new Animation[2][2];
		player[0][0] = new Animation(5f, new TextureRegion(items, 0, 56, 16, 56), new TextureRegion(items, 16, 56, 16, 56));
		player[0][1] = new Animation(0.16f, new TextureRegion(items, 0, 0, 16, 56), new TextureRegion(items, 16, 0, 16, 56), 
									new TextureRegion(items, 32, 0, 16, 56), new TextureRegion(items, 48, 0, 16, 56), 
									new TextureRegion(items, 64, 0, 16, 56), new TextureRegion(items, 80, 0, 16, 56),
									new TextureRegion(items, 96, 0, 16, 56), new TextureRegion(items, 112, 0, 16, 56));
		player[1][0] = new Animation(5f, new TextureRegion(items, 0, 168, 16, 56), new TextureRegion(items, 16, 168, 16, 56));
		player[1][1] = new Animation(0.16f, new TextureRegion(items, 0, 112, 16, 56), new TextureRegion(items, 16, 112, 16, 56), 
									new TextureRegion(items, 32, 112, 16, 56), new TextureRegion(items, 48, 112, 16, 56), 
									new TextureRegion(items, 64, 112, 16, 56), new TextureRegion(items, 80, 112, 16, 56),
									new TextureRegion(items, 96, 112, 16, 56), new TextureRegion(items, 112, 112, 16, 56));
		player_light = new TextureRegion(light, 0, 0, 60, 60);
		
		panelBack = new Texture(Gdx.files.internal("stuff/brushed.png"));
		UI = new Texture(Gdx.files.internal("stuff/UI.png"));
		frame = new NinePatch[2];
		frame[0] = new NinePatch(new TextureRegion(UI, 0, 0, 10, 10), 2, 2, 2, 2);
		frame[1] = new NinePatch(new TextureRegion(UI, 10, 0, 10, 10), 2, 2, 2, 2);
		blueAlpha = new TextureRegion(UI, 12, 2, 1, 1);
		whiteAlpha = new TextureRegion(UI, 2, 0, 1, 1);
		blackAlpha = new TextureRegion(UI, 0, 10, 1, 1);
		flag = new TextureRegion[2];
		flag[0] = new TextureRegion(UI, 20, 0, 30, 20);
		flag[1] = new TextureRegion(UI, 20, 20, 30, 20);
		logo = new Texture(Gdx.files.internal("stuff/catinbank.png"));
		background_menu = new Texture(Gdx.files.internal("stuff/background.png"));
		background = new TextureRegion(background_menu, 0, 0, 160, 90);
		back_light = new TextureRegion(background_menu, 0, 90, 160, 90);
		
		defaultShader = new ShaderProgram(Gdx.files.internal("shaders/vertexShader.glsl").readString(), 
				Gdx.files.internal("shaders/defaultShader.glsl").readString());
		lightShader = new ShaderProgram(Gdx.files.internal("shaders/vertexShader.glsl").readString(), 
				Gdx.files.internal("shaders/pixelShader.glsl").readString());
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("stuff/Roboto-Black.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 200;
		font_menu = generator.generateFont(parameter);
		font_menu.setColor(0.95f, 0.95f, 0.95f, 1f);
		generator.dispose();
		
		loadLanguage();
		
		entities = new HashMap<String, TextureRegion>();
		item_entities = new HashMap<String, TextureRegion>();
		lights = new HashMap<String, TextureRegion>();
	}
	
	public static void setStepSound(String file)
	{
		soundStepName = file;
	}
	
	public static void playStepSound()
	{
		int i = 1 + (int) (Math.random() * 5);
		while(i == stepNumber)
			i = 1 + (int) (Math.random() * 5);
		
		stepNumber = i;
			
		sound_step = Gdx.audio.newSound(Gdx.files.internal("steps/" + soundStepName + Integer.toString(stepNumber) + ".mp3"));
		sound_step.play(0.2f);
	}
	
	public static void playSound(String file)
	{
		if(Settings.sound)
		{
			if(soundName.compareTo(file) != 0 || soundName.compareTo("write") == 0)
			{
				soundName = file;
				if(file.compareTo("write") == 0)
					sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + file + Integer.toString(1 + (int) (Math.random() * 5)) + ".mp3"));
				else
					sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + file + ".mp3"));
			}
			sound.play();
		}
	}
	
	public static void loadLanguage()
	{
		if(font != null)
			font.dispose();
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("stuff/Munro_" + (Settings.language ? "en" : "ru") + ".ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = Settings.language ? 10 : 8;
		font = generator.generateFont(parameter);
		font.setColor(0.95f, 0.95f, 0.95f, 1f);
		generator.dispose();
		
		FileHandle baseFileHandle = Gdx.files.internal("strings/bundle");
		Locale locale = new Locale(Settings.language ? "en" : "ru");
		bundle = I18NBundle.createBundle(baseFileHandle, locale);
		baseFileHandle = Gdx.files.classpath("bundle");
		locale = new Locale(Settings.language ? "en" : "ru", "text");
		text_bundle = I18NBundle.createBundle(baseFileHandle, locale);
	}
	
	public static void loadLevelLanguage(String name)
	{
		FileHandle baseFileHandle = Gdx.files.classpath("bundle");
		Locale locale = new Locale(Settings.language ? "en" : "ru", name);
		level_bundle = I18NBundle.createBundle(baseFileHandle, locale);
	}
	
	public static void loadLevel(ClipManager clips, String name)
	{
		Rectangle r;
		
		entities.clear();
		lights.clear();
		
		level_items = new Texture(Gdx.files.internal("level_items/" + name + "_level.png"));
		for(Entry<String, Rectangle> entry: clips.getItemRects().entrySet())
		{
			r = entry.getValue();
			entities.put(entry.getKey(), new TextureRegion(level_items, (int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight()));
		}	
		
		level_lights = new Texture(Gdx.files.internal("level_items/" + name + "_light.png"));
		for(Entry<String, Rectangle> entry: clips.getLightRects().entrySet())
		{
			r = entry.getValue();
			lights.put(entry.getKey(), new TextureRegion(level_lights, (int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight()));
		}
		
		FileHandle baseFileHandle = Gdx.files.classpath("bundle");
		Locale locale = new Locale(Settings.language ? "en" : "ru", name);
		level_bundle = I18NBundle.createBundle(baseFileHandle, locale);
	}
	
	public static void addEntity(String key, int x, int y, int width, int height) 
	{ 
		clips.addItemRect(key, new Rectangle(x, y, width, height));
		entities.put(key, new TextureRegion(level_items, x, y, width, height));
	}
	
	public static void addLight(String key, int x, int y, int width, int height) 
	{
		clips.addLightRect(key, new Rectangle(x, y, width, height));
		lights.put(key, new TextureRegion(level_lights, x, y, width, height)); 
	}
	
	public static void addStuff(String key, int x, int y) 
	{ 
		item_entities.put(key, new TextureRegion(stuff, x, y, 25, 25));
	}
	
	public static void removeEntity(String key) 
	{
		clips.getItemRects().remove(key);
		entities.remove(key); 
	}
	
	public static void removeLight(String key) 
	{
		clips.getLightRects().remove(key);
		lights.remove(key); 
	}
	
	public static void removeStuff(String key) 
	{
		clips.getItemRects().remove(key);
		entities.remove(key); 
	}
	
	public static void dispose() 
	{
		entities.clear();
		lights.clear();
	}
}