package ru.catinbank.nyctophilia;

import java.util.ArrayList;
import java.util.List;

import ru.catinbank.framework.Selector;

import com.badlogic.gdx.math.Rectangle;

public class Inventory 
{
	private Rectangle[] sections;
	private List<Item> items;
	private List<Item> items_guy;
	private List<Item> items_man;
	private List<Goal> goals;
	private List<Note> notes;
	private boolean visible = false;
	private Selector sectionSel;
	private Selector[] stuffSel;
	private Notification n;

	public Inventory(Notification n)
	{
		sections = new Rectangle[3];
		if(Settings.language)
		{
			sections[0] = new Rectangle(59, 151, 45, 20);
			sections[1] = new Rectangle(139, 151, 45, 20);
			sections[2] = new Rectangle(219, 151, 45, 20);
		}
		else
		{
			sections[0] = new Rectangle(44, 151, 75, 20);
			sections[1] = new Rectangle(139, 151, 60, 20);
			sections[2] = new Rectangle(219, 151, 50, 20);
		}
		
		items_guy = new ArrayList<Item>();
		items_man = new ArrayList<Item>();
		items = items_guy;
		goals = new ArrayList<Goal>();
		notes = new ArrayList<Note>();
		
		sectionSel = new Selector(3, 3);
		stuffSel = new Selector[3];
		stuffSel[0] = new Selector(items.size(), 4);
		stuffSel[1] = new Selector(notes.size(), 4);
		stuffSel[2] = new Selector(goals.size(), 8);
		
		this.n = n;
	}
	
	public void setItems(int c)
	{
		if(c == 1)
			items = items_man;
		else
			items = items_guy;
	}
	
	public void changeItems()
	{
		if(items.equals(items_guy))
			items = items_man;
		else
			items = items_guy;
		
		stuffSel[0].setSize(items.size());
		stuffSel[0].setSelected(0);
	}

	public void addItem(String name, String function, int x, int y)  
	{ 
		items.add(0, new Item(name, function, x, y));
		stuffSel[0].setSize(items.size());
		Assets.addStuff(name, x, y);
	}
	
	public void deleteItem(String name)
	{
		for(Item i: items)
		{
			if(i.getName() == name)
			{
				items.remove(i);
				break;
			}
		}
	}
	
	public void addGoal(String name)  
	{ 
		goals.add(0, new Goal(name));
		stuffSel[2].setSize(goals.size());
		n.put(new String[] {"newgoal"});
	}
	
	public void addNote(String name) 
	{
		notes.add(0, new Note(name));
		stuffSel[1].setSize(notes.size());
		sectionSel.setSelected(1);
		stuffSel[1].setSelected(0);
	}
	
	public void goalDone(String name)
	{
		for(Goal g: goals)
		{
			if(g.getName().compareTo(name) == 0)
			{
				g.setDone();
				Assets.playSound("mission");
			}
		}
	}
	
	public boolean isGoalDone(String name)
	{
		for(Goal g: goals)
		{
			if(g.getName().compareTo(name) == 0 && g.isDone())
				return true;
		}
		return false;
	}
	
	public boolean isThereGoal(String name)
	{
		for(Goal g: goals)
		{
			if(g.getName().compareTo(name) == 0)
				return true;
		}
		return false;
	}
	
	public void clearGoals() { goals.clear(); }
	
	public Note getStuff(int index) 
	{
		switch(sectionSel.getSelected())
		{
		case 0:
			return items.get(index);
		case 1:
			return notes.get(index);
		case 2:
			return goals.get(index);
		}
		
		return null;
	}
	
	public Item getItem(int index) { return items.get(index); }
	
	public List<Item> getItems() { return items; }
	
	public void nextSection() 
	{
		if(visible)
		{
			sectionSel.next();
			Assets.playSound("click");
		}
	}
	
	public void previousSection() 
	{ 
		if(visible)
		{
			sectionSel.previous();
			Assets.playSound("click");
		}
	}
	
	public void nextStuff() 
	{
		if(visible)
		{
			stuffSel[sectionSel.getSelected()].next();
			Assets.playSound("click");
		}
	}
	
	public void previousStuff() 
	{ 
		if(visible)
		{
			stuffSel[sectionSel.getSelected()].previous();
			Assets.playSound("click");
		}
	}
	
	public int getStuffSize()
	{
		switch(sectionSel.getSelected())
		{
		case 0:
			return items.size();
		case 1:
			return notes.size();
		case 2:
			return goals.size();
		}
		
		return 0;
	}
	
	public int getSectionIndex() { return sectionSel.getSelected(); }
	
	public Rectangle getSection(int index) { return sections[index]; }
	
	public Selector getSelector() { return stuffSel[sectionSel.getSelected()]; }

	public boolean isVisible() { return visible; }

	public void setVisible() { this.visible = !visible; }
}