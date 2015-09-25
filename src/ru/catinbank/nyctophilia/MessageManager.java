package ru.catinbank.nyctophilia;

import java.util.ArrayList;
import java.util.List;

public class MessageManager
{
	protected List<String> messages;
	
	public MessageManager()
	{
		messages = new ArrayList<String>();
	}
	
	public void put(String[] args)
	{
		for(String s: args)
			messages.add(Assets.level_bundle.get(s));
		Assets.playSound("write");
	}
	
	public void deleteCurrent() 
	{
		if(!messages.isEmpty())
		{
			messages.remove(0);
			Assets.playSound("write");
		}
	}
	
	public String getCurrent() { return messages.get(0); }
	
	public boolean isEmpty() { return messages.isEmpty(); }
}
