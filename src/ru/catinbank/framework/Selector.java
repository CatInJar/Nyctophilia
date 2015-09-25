package ru.catinbank.framework;

public class Selector 
{
	private int selected, shift, size, max;
	
	public Selector()
	{
		selected = 0;
		shift = 0;
		size = 0;
		max = 0;
	}
	
	public Selector(int size, int max)
	{
		selected = 0;
		shift = 0;
		this.size = size;
		this.max = max;
	}
	
	public void next()
	{
		if(selected < size - 1)
		{
			selected++;
			if(selected > shift + max - 1)
				shift++;
		}
		else
		{
			selected = 0;
			shift = 0;
		}
	}
	
	public void previous()
	{
		if(selected > 0)
		{
			selected--;
			if(selected < shift)
				shift--;
		}
		else
		{
			selected = size - 1;
			shift = size - max;
			if(shift < 0)
				shift = 0;
		}
	}
	
	public void setSize(int size)
	{ 
		this.size = size;
		if(selected > size)
			selected = size - 1;
	}
	
	public void setSelected(int s) { selected = s; }
	
	public int getShift() { return shift; }
	
	public int getMax() { return max; }
	
	public int getSize() { return size; }
	
	public int getSelected() { return selected; }
}