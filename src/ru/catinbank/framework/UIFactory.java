package ru.catinbank.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class UIFactory 
{
	private static final Skin UISkin = new Skin( Gdx.files.internal("stuff/uiskin.json"));
	
	public static TextButton newButton(String text, ClickListener listener)
	{
		TextButton b = new TextButton(text, UISkin);
		b.addListener(listener);
		return b;
	}
	
	public static Label newLabel(String text) 
	{
		Label l = new Label( text, UISkin );
		return l;
	}
	
	public static TextField newTextfield(String text)
	{
		TextField t = new TextField(text, UISkin);
		return t;
	}
	
	public static CheckBox newCheckBox(String text)
	{
		CheckBox cb = new CheckBox(text, UISkin);
		return cb;
	}
	
	public static SelectBox<String> newSelectBox(String[] items, ChangeListener listener) 
	{
		SelectBox<String> sb = new SelectBox<String>(UISkin);
		sb.setItems(items);
		sb.addListener(listener);
		return sb;
	}
}
