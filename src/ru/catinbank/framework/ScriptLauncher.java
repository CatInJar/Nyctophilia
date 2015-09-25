package ru.catinbank.framework;

import java.util.concurrent.TimeUnit;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import ru.catinbank.nyctophilia.World;

import com.badlogic.gdx.Gdx;

public class ScriptLauncher implements Runnable
{
	private ScriptEngine engine;
	private Invocable inv;
	private String function;
	
	public ScriptLauncher(World world)
	{
		engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.put("world", world);
		engine.put("thread", TimeUnit.MILLISECONDS);
		inv = (Invocable) engine;
	}
	
	@Override
	public void run() 
	{
		try {
			inv.invokeFunction(function);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	public void launch(String function)
	{
		if(function != null)	
			this.function = function;
			Thread thread = new Thread(this);
			thread.start();
	}
	
	public void setFile(String file)
	{
		try {
			engine.eval(Gdx.files.internal("scripts/" + file + ".js").readString());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
