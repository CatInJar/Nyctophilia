package ru.catinbank.nyctophilia;

import java.util.Map;
import java.util.Map.Entry;

import ru.catinbank.framework.Entity;
import ru.catinbank.framework.LevelLoader;
import ru.catinbank.framework.Light;
import ru.catinbank.framework.OverlapTester;
import ru.catinbank.framework.UIFactory;
import ru.catinbank.nyctophilia.World.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class UI 
{
	private World world;
	private WorldRenderer renderer;
	private Stage stage;
	private boolean visible = false;
	private Label selX, selY, selWidth, selHeight, selName, selVisible, Color, Intensity;
	private String selected = null;
	
	public UI(World world, WorldRenderer renderer, InputMultiplexer plex)
	{
		this.world = world;
		this.renderer = renderer;
		stage = new Stage();
		plex.addProcessor(stage);
		
		NinePatch np = new NinePatch(Assets.panelBack, 0, 0, 0, 0 );
		np.setColor( new Color( 0.3f, 0.3f, 0.3f, 1f ) );
		NinePatchDrawable npBack = new NinePatchDrawable( np );
		
		float width = Gdx.graphics.getWidth();
		
		Table panel = new Table();
		panel.setSize(width, 155);
		panel.setBackground(npBack);
		
		panel.add(buildFileWidgets());
		panel.add(buildSceneWidgets());
		panel.add(buildLightWidgets());
		panel.add(buildObjectWidgets());
		panel.add(buildInfoWidgets());
		
		stage.addActor(panel);
	}
	
	public Table buildFileWidgets()
	{
		final TextField imageName= UIFactory.newTextfield("");
		
		final TextButton saveButton = UIFactory.newButton("Save", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				LevelLoader.saveLevel(world.getLevel(), Assets.clips);
			}
		});
		
		final TextButton loadButton = UIFactory.newButton("Load", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				world.setLevel(imageName.getText());
				renderer.setLightOptions();
				world.setState(World.State.RUNNING);
				visible = false;
			}
		});
		
		final TextButton playButton = UIFactory.newButton("Play", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				world.setState(World.State.RUNNING);
				visible = false;
			}
		});
		
		Table t = new Table();
		t.add(UIFactory.newLabel("Level"));
		t.add(imageName);
		t.row();
		t.add(saveButton).left();
		t.row();
		t.add(loadButton).left();
		t.row();
		t.add(playButton).left();
		t.row();
		
		return t;
	}
	
	public Table buildSceneWidgets()
	{
		final TextField sceneName= UIFactory.newTextfield("");
		
		final TextButton addButton = UIFactory.newButton("Add", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				world.getLevel().addScene(sceneName.getText(), new Scene());
			}
		});
		
		final TextButton setButton = UIFactory.newButton("Set", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				world.getLevel().setScene(sceneName.getText());
				renderer.setLightOptions();
			}
		});
		
		final TextButton deleteButton = UIFactory.newButton("Delete", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				world.getLevel().removeScene(sceneName.getText());
			}
		});
		
		Table t = new Table();
		t.add(UIFactory.newLabel("Scene"));
		t.add(sceneName);
		t.row();
		t.add(addButton).left();
		t.row();
		t.add(setButton).left();
		t.row();
		t.add(deleteButton).left();
		t.row();
		
		return t;
	}
	
	public Table buildLightWidgets()
	{
		final TextField Red = UIFactory.newTextfield("");
		
		final TextField Green = UIFactory.newTextfield("");
		
		final TextField Blue = UIFactory.newTextfield("");
		
		final TextField Intensity = UIFactory.newTextfield("");
		
		final TextButton setButton = UIFactory.newButton("Set", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				world.getLevel().getScene().setColor(new Vector3(Float.parseFloat(Red.getText()), 
						Float.parseFloat(Green.getText()), Float.parseFloat(Blue.getText())));
				world.getLevel().getScene().setIntensity(Float.parseFloat(Intensity.getText()));
				renderer.setLightOptions();
			}
		});
		
		Table t = new Table();
		t.add(UIFactory.newLabel("Red:"));
		t.add(Red);
		t.row();
		t.add(UIFactory.newLabel("Green:"));
		t.add(Green);
		t.row();
		t.add(UIFactory.newLabel("Blue:"));
		t.add(Blue);
		t.row();
		t.add(UIFactory.newLabel("Intensity:"));
		t.add(Intensity);
		t.row();
		t.add(setButton).left();
		
		return t;
	}
	
	public Table buildObjectWidgets()
	{
		final TextField X = UIFactory.newTextfield("");
		
		final TextField Y = UIFactory.newTextfield("");
		
		final TextField Width = UIFactory.newTextfield("");
		
		final TextField Height = UIFactory.newTextfield("");
		
		final TextField srcX = UIFactory.newTextfield("");
		
		final TextField srcY = UIFactory.newTextfield("");
		
		final TextField srcWidth = UIFactory.newTextfield("");
		
		final TextField srcHeight = UIFactory.newTextfield("");	
		
		final TextField Key = UIFactory.newTextfield("");
		
		final TextField Function = UIFactory.newTextfield("");
		
		final CheckBox Visible = UIFactory.newCheckBox("Visible");
		Visible.setChecked(true);
		
		final CheckBox Usable = UIFactory.newCheckBox("Usable");
		Usable.setChecked(true);
		
		final CheckBox Collision = UIFactory.newCheckBox("Collision");
		
		final CheckBox Event = UIFactory.newCheckBox("Event");
		
		final SelectBox<String> objectType = UIFactory.newSelectBox(new String[] {"Object", "Light", "Scripted"}, new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				@SuppressWarnings( "unchecked" )
				SelectBox<String> source = (SelectBox<String>)actor;
				
				if(source.getSelectedIndex() == 0)
					world.setState(World.State.EDITING_ENTITY);
				
				if(source.getSelectedIndex() == 1)
					world.setState(World.State.EDITING_LIGHT);
			}
		});
		
		final TextButton addButton = UIFactory.newButton("Add", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				if(objectType.getSelected() != "Light")
				{
					world.getLevel().getScene().addEntity(Key.getText(), 
						new Entity(Float.parseFloat(X.getText()), Float.parseFloat(Y.getText()),
						Float.parseFloat(Width.getText()), Float.parseFloat(Height.getText()), 
						Visible.isChecked(), Collision.isChecked(), Usable.isChecked(), Event.isChecked(), Function.getText()));
					
					Assets.addEntity(Key.getText(), Integer.parseInt(srcX.getText()), Integer.parseInt(srcY.getText()), 
						Integer.parseInt(srcWidth.getText()), Integer.parseInt(srcHeight.getText()));
				}
				else
				{
					world.getLevel().getScene().addLight(Key.getText(), new Light(Float.parseFloat(X.getText()), Float.parseFloat(Y.getText()), 
							Float.parseFloat(Width.getText()), Float.parseFloat(Height.getText()), Visible.isChecked() ? true : false));
					Assets.addLight(Key.getText(), Integer.parseInt(srcX.getText()), Integer.parseInt(srcY.getText()), 
							Integer.parseInt(srcWidth.getText()), Integer.parseInt(srcHeight.getText()));
				}
			}
		});
		
		final TextField startX = UIFactory.newTextfield("");
		
		final TextField maxX = UIFactory.newTextfield("");
		
		final TextField minX = UIFactory.newTextfield("");
		
		final TextButton setButton = UIFactory.newButton("set", new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				if(world.isLoaded())
				{
					world.getLevel().getScene().setStartX(Float.parseFloat(startX.getText()));
					world.getLevel().getScene().setMaxX(Float.parseFloat(maxX.getText()));
					world.getLevel().getScene().setMinX(Float.parseFloat(minX.getText()));
				}
			}
		});
		
		Table t = new Table();
		
		Table t1 = new Table();
		t1.add(UIFactory.newLabel("X:"));
		t1.add(X);
		t1.row();
		t1.add(UIFactory.newLabel("Y:"));
		t1.add(Y);
		t1.row();
		t1.add(UIFactory.newLabel("Width:"));
		t1.add(Width);
		t1.row();
		t1.add(UIFactory.newLabel("Height:"));
		t1.add(Height);
		
		Table t2 = new Table();
		t2.add(UIFactory.newLabel("srcX:"));
		t2.add(srcX);
		t2.row();
		t2.add(UIFactory.newLabel("srcY:"));
		t2.add(srcY);
		t2.row();
		t2.add(UIFactory.newLabel("srcWidth:"));
		t2.add(srcWidth);
		t2.row();
		t2.add(UIFactory.newLabel("srcHeight:"));
		t2.add(srcHeight);
		
		Table t3 = new Table();
		t3.add(UIFactory.newLabel("Key:"));
		t3.add(Key);
		t3.row();
		t3.add(UIFactory.newLabel("Function:"));
		t3.add(Function);
		t3.row();
		t3.add(objectType);
		t3.row();
		t3.add(Visible).left();
		t3.add(Usable).left();
		t3.row();
		t3.add(Collision).left();
		t3.add(Event).left();
		
		Table t4 = new Table();
		t4.add(addButton);
		
		Table t5 = new Table();
		t5.add(UIFactory.newLabel("StartX:"));
		t5.add(startX);
		t5.row();
		t5.add(UIFactory.newLabel("MaxX:"));
		t5.add(maxX);
		t5.row();
		t5.add(UIFactory.newLabel("MinX:"));
		t5.add(minX);
		t5.row();
		t5.add(setButton);
		t5.row();
		
		t.add(t1);
		t.add(t2);
		t.add(t3);
		t.add(t4);
		t.add(t5);
		
		return t;
	}
	
	public Table buildInfoWidgets()
	{
		selX = UIFactory.newLabel("");
		
		selY = UIFactory.newLabel("");
		
		selWidth = UIFactory.newLabel("");
		
		selHeight = UIFactory.newLabel("");
		
		selName = UIFactory.newLabel("");
		
		selVisible = UIFactory.newLabel("");
		
		Color = UIFactory.newLabel("");
		
		Intensity = UIFactory.newLabel("");
		
		Table t = new Table();
		
		Table t1 = new Table();
		t1.add(selX).left();
		t1.row();
		t1.add(selY).left();
		t1.row();
		t1.add(selWidth).left();
		t1.row();
		t1.add(selHeight).left();
		t1.row();
		t1.add(selName).left();
		t1.row();
		t1.add(selVisible).left();
		
		Table t2 = new Table();
		t2.add(Color).left();
		t2.row().left();
		t2.add(Intensity);
		
		t.add(t1);
		t.add(t2);
		
		return t;
	}
	
	public void update(float delta)
	{
		if(visible)
		{
			stage.act(delta);
			stage.draw();
			
			Entity s = getSelected();
			if(s != null)
			{
				selX.setText("X: " + Float.toString(s.getX()));
				selY.setText("Y: " + Float.toString(s.getY()));
				selWidth.setText("Width: " + Float.toString(s.getWidth()));
				selHeight.setText("Height: " + Float.toString(s.getHeight()));
				selName.setText("Name: " + selected);
				selVisible.setText("Function: " + s.getFunction());
			}
			if(world.isLoaded())
			{
				Color.setText("Color: " + world.getLevel().getScene().getColor().toString());
				Intensity.setText("Intensity: " + Float.toString(world.getLevel().getScene().getIntensity()));
			}
		}
		else
			stage.unfocusAll();
	}
	
	public void checkSelected(Vector3 touchPoint)
	{
		if(world.isLoaded())
		{
			if(selected == null)
			{
				if(world.getState() == State.EDITING_ENTITY)
				{
					Map<String, Entity> entities = world.getLevel().getScene().getEntities();
					for(Entry<String, Entity> entry: entities.entrySet())
					{
						if(OverlapTester.pointInRectangle(entry.getValue().getBounds(), touchPoint))
						{
							if(touchPoint.y > 25.74f && visible || !visible)
								selected = entry.getKey();
						}
					}
				}
				else if(world.getState() == State.EDITING_LIGHT)
				{
					Map<String, Light> lights = world.getLevel().getScene().getLights();
					for(Entry<String, Light> entry: lights.entrySet())
					{
						if(OverlapTester.pointInRectangle(entry.getValue().getBounds(), touchPoint))
						{
							if((touchPoint.y > 25.74f && visible) || !visible)
								selected = entry.getKey();
						}
					}
				}
			}
		}
	}
	
	public void deleteSelected()
	{
		if(selected != null)
		{
			if(world.getState() == State.EDITING_ENTITY)
				world.getLevel().getScene().getEntities().remove(selected);
			else
				world.getLevel().getScene().getLights().remove(selected);
			selected = null;
		}
	}
	
	public void nullSelected() { selected = null; }
	
	public Entity getSelected() 
	{
		if(world.isLoaded())
		{
			if(world.getState() == State.EDITING_ENTITY)
				return world.getLevel().getScene().getEntities().get(selected);
			else if(world.getState() == State.EDITING_LIGHT)
				return world.getLevel().getScene().getLights().get(selected);
		}
		return null;
	}
	
	public void changeVisible() { visible = !visible; } 
	
	public void dispose() { stage.dispose(); }
}