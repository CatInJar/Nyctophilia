package ru.catinbank.nyctophilia;

public class Item extends Note
{
	private String function;
	private int x, y;
	
	public Item(String name, String function,  int x, int y)
	{
		super(name);
		this.function = function;
		this.x = x;
		this.y = y; 
	}

	public String getFunction() { return function; }

	public void setFunction(String function) { this.function = function; }
	
	public int getX() { return x; }
	public int getY() { return y; }
}