package ru.catinbank.nyctophilia;

public class ActionList
{
	private String[] actions;
	private int selected = 0;
	
	public ActionList()
	{

	}
	
	public void nextAction()
	{
		selected++;
		
		if(selected == actions.length)
			selected = 0;
	}
	
	public void previousAction()
	{
		selected--;
		
		if(selected == -1)
			selected = actions.length - 1;
	}
	
	public void setActions(String[] args)
	{
		actions = args;
	}
	
	public boolean isEmpty() { return actions == null; }
	
	public void setEmpty() { actions = null; selected = 0;}
	
	public String getSelected() { return actions[selected]; }
	
	public int getSelectedNumber() { return selected; }
	
	public String[] getActions() { return actions; }
}