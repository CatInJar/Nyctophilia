package ru.catinbank.framework;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class OverlapTester
{
	 public static boolean pointInRectangle(Rectangle r, Vector3 p) 
	 {
	        return r.x <= p.x && r.x + r.width >= p.x &&
	               r.y <= p.y && r.y + r.height >= p.y;
	 }
}