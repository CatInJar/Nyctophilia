package ru.catinbank.nyctophilia;

public class Goal extends Note
{
	boolean done = false;
	
	public Goal(String name)
	{
		super(name);
	}
	
	public boolean isDone() { return done; }
	
	public void setDone() { done = true; }
}