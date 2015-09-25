package ru.catinbank.nyctophilia;

import java.util.Map;
import java.util.Map.Entry;

import ru.catinbank.framework.Entity;
import ru.catinbank.framework.Fader;
import ru.catinbank.framework.LevelLoader;
import ru.catinbank.framework.ScriptLauncher;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;

public class World 
{
	private final String[] menu = {"continue", "options", "saveexit"};
	private ScriptLauncher scripts;
	private MessageManager messages;
	private Notification notifications;
	private ActionList actions;
	private Level level;
	private Player player;
	private Inventory inventory;
	private Fader fader;
	
	private Entry<String, Entity> selected = null;
	private String nextScene;
	private String nextLevel;
	private State state = State.RUNNING;
	private int selectedMenu = 0;
	private int selectedOption = 0;
	private boolean pauseBlocked = false;

	public enum State
	{
		RUNNING,
		CHANGING_SCENE,
		CHANGING_LEVEL,
		READING,
		EDITING_ENTITY,
		EDITING_LIGHT,
		PAUSED_MENU,
		PAUSED_OPTIONS
	}
	
	public World(SpriteBatch batch)
	{
		scripts = new ScriptLauncher(this);
		messages = new MessageManager();
		notifications = new Notification();
		actions = new ActionList();
		
		player = new Player(30);
		inventory = new Inventory(notifications);
		fader = new Fader(batch);
	}
	
	public World(SpriteBatch batch, String levelName)
	{
		scripts = new ScriptLauncher(this);
		messages = new MessageManager();
		notifications = new Notification();
		actions = new ActionList();
		
		player = new Player(30);
		inventory = new Inventory(notifications);
		fader = new Fader(batch);
		
		if(levelName.compareTo("load") != 0)
			setLevel(levelName);
		else
		{
			level = Settings.loadLevel(level, scripts);
			Assets.clips = Settings.loadClips(Assets.clips, level.getName());
			inventory = Settings.loadPlayer(inventory, player);
		}
	}

	public void update(float delta)
	{
		if((Nyctophilia.developerMode && !isEditing()) || !Nyctophilia.developerMode)
		{
			if(state == State.CHANGING_SCENE)
				updateChanging();
			else if(state == State.CHANGING_LEVEL)
				updateChangingLevel();
			else
			{	
				if(!player.getLocked() && messages.isEmpty() && actions.isEmpty() && !inventory.isVisible())
					player.update(delta);
				if(!inventory.isVisible() && messages.isEmpty())
					notifications.update(delta);
				checkCollision(delta);
			}
			fader.update();
			Assets.fader.update();
		}
	}
	
	private void checkCollision(float delta)
	{
		if(isLoaded())
		{
			if(player.getX() + player.getWidth() < level.getScene().getMinX() || player.getX() + player.getWidth() > level.getScene().getMaxX())
				player.reverse(delta);
			
			selected = null;
			Map<String, Entity> entities = level.getScene().getEntities();
			Entity e;
			for(Entry<String, Entity> entry: entities.entrySet())
			{
				e = entry.getValue();
				if(e.isCollide() && e.isVisible())
				{
					if(Intersector.overlaps(player.getBounds(), e.getBounds()))
						player.reverse(delta);
				}
				
				if(e.isUsable())
				{
					if(Math.abs(player.getCenterX() - e.getCenterX()) <= Math.abs(player.getWidth()/2) + Math.abs(e.getWidth()/2) + 3)
					{
						if(e.isEvent())
							scripts.launch(e.getFunction());
						else if(e.isVisible())
							selected = entry;
					}
				}
			}
		}
	}
	
	private void updateChanging()
	{
		if(fader.getState() == Fader.State.FADED_OUT && level.getSceneName() != nextScene)
			fader.fadeIn();
		
		if(fader.getState() == Fader.State.FADED_IN)
		{
			level.setScene(nextScene);
			fader.fadeOut();
		}
		
		if(fader.getState() == Fader.State.FADED_OUT && level.getSceneName() == nextScene)
			state = State.RUNNING;
	}
	
	public void changeCharacterScene()
	{
		if(player.getCharacter() == 0)
			changeScene(level.getSceneName() + "2");
		else
			changeScene(level.getSceneName().substring(0, level.getSceneName().length() - 1));
	}
	
	public void changeScene(String key)
	{
		nextScene = key;
		state = State.CHANGING_SCENE;
	}
	
	private void updateChangingLevel()
	{
		if(fader.getState() == Fader.State.FADED_OUT && level.getName().compareTo(nextLevel) < 0)
			fader.fadeIn();
		
		if(fader.getState() == Fader.State.FADED_IN)
		{
			setLevel(nextLevel);
			fader.fadeOut();
		}
		
		if(fader.getState() == Fader.State.FADED_OUT && level.getName().compareTo(nextLevel) == 0)
			state = State.RUNNING;
	}
	
	public void changeLevel(String key)
	{
		nextLevel = key;
		state = State.CHANGING_LEVEL;
	}
	
	public void setScene(String key)
	{
		level.setScene(key);
	}
	
	public void setLevel(String name)
	{
		level = LevelLoader.loadLevel(name);
		Assets.clips = LevelLoader.loadClips(name);
		level.setScene(level.getSceneName());
		scripts.setFile(name);
		player.setX(level.getScene().getStartX());
		scripts.launch("start");
	}
	
	public boolean isEditing()
	{
		if(state == State.EDITING_ENTITY || state == State.EDITING_LIGHT)
			return true;
		return false;
	}
	
	public boolean isLoaded()
	{
		if(level != null && Assets.clips != null)
			return true;
		return false;
	}
	
	public void launchSelectedScript() 
	{ 
		if(!inventory.isVisible())
		{
			if(selected != null)
			{
				scripts.launch(selected.getValue().getFunction());
				selected = null;
			}
		}
		else
		{
			if(inventory.getSectionIndex() == 0 && inventory.getSelector().getSize() != 0)
				scripts.launch(inventory.getItem(inventory.getSelector().getSelected()).getFunction());
			else if(inventory.getSectionIndex() == 1 && inventory.getSelector().getSize() != 0)
			{
				state = State.READING;
				Assets.playSound("read");
			}
			
			inventory.setVisible();
		}
	}
	
	public void readNote()
	{ 
		state = State.READING;
		Assets.playSound("read");
	}
	
	public Entity getSelected() 
	{ 
		if(selected != null)
			return selected.getValue();
		return null;
	}
	
	public String getSelectedKey() 
	{ 
		if(selected != null)
			return selected.getKey();
		return "";
	}
	
	public void settingsAction() 
	{
		Assets.playSound("action");
	}
	
	public void nullSave() 
	{ 
		Settings.actions = 0;
		Settings.savedGame = false;
	}
	
	public int getFuck() { return Settings.actions; }
	
	public void playSound(String file) { Assets.playSound(file); }
	
	public void playMusic() { Assets.fader.fadeOut(); }
	
	public void stopMusic() { Assets.fader.fadeIn(); }
	
	public void setMusic(String file) { Assets.fader.loadMusic(file); }
	
	public void setStepSound(String file) { Assets.setStepSound(file); }
	
	public void Save() { Settings.saveGame(level, Assets.clips, inventory, player); }
	
	public int getSettings() { return Settings.actions; }
	
	public int getSelectedMenu() { return selectedMenu; }
	
	public boolean isPauseBlocked() { return pauseBlocked; }

	public void setPauseBlocked(boolean pauseBlocked) { this.pauseBlocked = pauseBlocked; }
	
	public String[] getMenu() { return menu; }

	public void setSelectedMenu(int selectedMenu) { this.selectedMenu = selectedMenu; }

	public int getSelectedOption() { return selectedOption; }

	public void setSelectedOption(int selectedOption) { this.selectedOption = selectedOption; }
	
	public ActionList getActions() { return actions; }
	
	public void launchScript(String function) { scripts.launch(function); }
	
	public boolean getLanguage() { return Settings.language; }
	
	public void fadeIn() { fader.fadeIn(); };
	
	public void fadeOut() { fader.fadeOut(); };
	
	public Inventory getInventory() { return inventory; }
	
	public MessageManager getMessages() { return messages; }
	
	public Notification getNotifications() { return notifications; }
	
	public State getState() { return state; }
	
	public void setState(State state) { this.state = state; }
	
	public Level getLevel() { return level; }
	
	public Player getPlayer() { return player; }
}