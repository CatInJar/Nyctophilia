package ru.catinbank.nyctophilia;

public class Notification extends MessageManager
{
	private final float interval = 3.0f;
	private float time = 0;
	
	public Notification()
	{
		super();
	}
	
	@Override
	public void put(String[] args)
	{
		for(String s: args)
		{
			if(s.compareTo("newgoal") == 0)
			{
				messages.add(Assets.bundle.get("newgoal"));
				break;
			}
			messages.add(Assets.level_bundle.get(s));
		}
	}
	
	@Override
	public void deleteCurrent() 
	{
		if(!messages.isEmpty())
			messages.remove(0);
	}
	
	public void update(float delta)
	{
		if(!isEmpty())
			time += delta;
		
		if(time > interval)
		{
			time = 0;
			deleteCurrent();
		}
	}
}