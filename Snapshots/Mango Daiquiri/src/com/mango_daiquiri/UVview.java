package com.mango_daiquiri;

import java.util.LinkedList;
import java.util.ListIterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class UVview extends View{
	public LinkedList<Icon> allIcons= new LinkedList<Icon>(); // LinkedList of all icons in this UserView (so can iterate through to draw Icons)
	private int iconID=0; // variable to know which ball is being dragged

	/** This constructor only necessary for part b) under Main onCreate() */
	public UVview(Context context){
		super(context);
		setFocusable(true);
		initializeIcons(context);
	}

	/** Declaring icons and positions the old fashioned way */
	protected void initializeIcons(Context context){
		Point point1= new Point();
		point1.x=50;
		point1.y=20;
		Point point2= new Point();
		point2.x=100;
		point2.y=20;
		Point point3= new Point();
		point3.x=150;
		point3.y=20;
		Point point4= new Point();
		point4.x=70;
		point4.y=20;
		Point point5= new Point();
		point5.x=20;
		point5.y=20; 
		allIcons.add(new Icon(context, R.drawable.bol_groen, point1));
		allIcons.add(new Icon(context, R.drawable.bol_rood, point2));
		allIcons.add(new Icon(context, R.drawable.bol_blauw, point3));
		allIcons.add(new Icon(context, R.drawable.bol_geel, point4));
		allIcons.add(new Icon(context, R.drawable.bol_paars, point5)); 
	}
	
	/** Draw all icons to screen */
	protected void onDraw(Canvas canvas){
		canvas.drawColor(0xFFCCCCCC); // another background color
		// draw all balls on the canvas
		ListIterator<Icon> i= allIcons.listIterator(0);
		while(i.hasNext()==true){
			Icon icon= i.next();
			canvas.drawBitmap(icon.getBitmap(), icon.getX(), icon.getY(), null);
			i= allIcons.listIterator(i.nextIndex());
		}
	}
	

	/** Events when touching the screen */
	//TODO need to change to official icon width/height parameters, can only be done when can resize icon images
	public boolean onTouchEvent(MotionEvent event){
		int eventaction= event.getAction();
		int X= (int)event.getX();
		int Y= (int)event.getY();
		switch(eventaction){
		case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on a ball
			iconID=0;
			ListIterator<Icon> i= allIcons.listIterator();
			while(i.hasNext()==true){
				Icon icon= (Icon)i.next();
				// check if inside the bounds of the icon (circle)
				//get the center for the ball
				int centerX= icon.getX()+25; 
				int centerY= icon.getY() + 25;
				// calculate the radius from the touch to the center of the ball
				double radCircle= Math.sqrt((double)(((centerX-X)*(centerX-X))+((centerY-Y)*(centerY-Y))));
				// if the radius is smaller than 23 (radius of the icon is 22, then it must be on the icon
				if(radCircle<23){
					iconID= icon.getID();
					break;
				}
				// check all the bounds of the ball (square)
				/**if (X>icon.getX() && X<icon.getX()+50 && Y>icon.getY() && Y<icon.getY()+50{
					iconID=icon.getID();
					break;
				} */
				i= allIcons.listIterator(i.nextIndex());
		}
			break;
		case MotionEvent.ACTION_MOVE: // touch drag with the ball
			// move the balls the same as the finger
			if (iconID>0){
				(allIcons.get(iconID-1)).setX(X-25);
				(allIcons.get(iconID-1)).setY(Y-25);
				/**allIcons[iconID-1].setX(X-25);
				allIcons[iconID-1].setY(Y-25); */
			}
			break;
		case MotionEvent.ACTION_UP:
			//touch drop - just do things here after dropping
			break;
		}
			//redraw the canvas
		invalidate();
		return true;
	}
	
	public LinkedList<Icon> getAllIcons(){
		return allIcons;
	}
}
