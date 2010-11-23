package com.mango_daiquiri;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/** TODO 
 * 1) change screen dimensions to 480 x 800 for presentation 
 * 2) resize images that are too big 
 * 3) Balls can go more left than they can right, depends on size of image, finish 2) first then this */
public class Icon {
	private Bitmap image; // image of the icon
	private int xpos; // current x position on screen
	private int ypos; // current y position on screen
	private int id; // give every icon its own id, for now not necessary
	private static int count = 1;
	private boolean goRight= true;
	private boolean goDown= true;
	int screenWidth=270;
	int screenHeight=400; 
	int radius=22;
	
	/** An icon */
	public Icon(Context context, int drawable){
		this.xpos=0;
		this.ypos=0;
		BitmapFactory.Options opts= new BitmapFactory.Options();
		opts.inJustDecodeBounds= true;
		image= BitmapFactory.decodeResource(context.getResources(),drawable);
		id=count;
		count++;
	}
	
	/** An icon with an initial position */
	public Icon(Context context, int drawable, Point point){
		xpos= point.x;
		ypos= point.y;
		BitmapFactory.Options opts= new BitmapFactory.Options();
		opts.inJustDecodeBounds= true;
		image= BitmapFactory.decodeResource(context.getResources(), drawable);
		id=count;
		count++;
	}
	
	/** Change coordinates of this icon on the screen */
	public void moveBall(int goX, int goY){
		// check the borders, and set the direction if the border has reached
		if(xpos>screenWidth)
			goRight=false;
		if(xpos<0)
			goRight=true;
		if(ypos>screenHeight)
			goDown=false;
		if(ypos<0)
			goDown=true;
		// move the x and y
		if(goRight)
			xpos+= goX;
		else
			xpos-= goX;
		if(goDown)
			ypos+= goY;
		else
			ypos-= goY;
	}
	
	public int getX(){
		return xpos;
	}
	
	public void setX(int newValue){
		xpos= newValue;
	}
	
	public int getY(){
		return ypos;
	}
	
	public void setY(int newValue){
		ypos= newValue;
	}
	
	public int getID(){
		return id;
	}
	
	public static int getCount(){
		return count;
	}

	public Bitmap getBitmap(){
		return image;
	}
	
	public int getRadius(){
		return radius;
	}
}
