package com.mango_daiquiri;
//TODO: get existing people in their private space, and put their icons in your linked list and such
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

//TODO need to change to official icon width/height parameters, can only be done when can resize icon images
//TODO if you join someone else's private space, then make an icon for that new person
public class UserView extends View{
	private Context context; // Android activity
	private LinkedList<Icon> allIcons= new LinkedList<Icon>(); // LinkedList of all icons in this UserView (so can iterate through to draw Icons)
	private int iconID=0; // variable to know which ball is being dragged
	private User MainUser; // The user that owns this UserView
	private PrivateSpace ViewPS; // The PrivateSpace this UserView is representing
	private HashMap<Icon,User> PeopleIconHash; // A hashmap, give User object, returns icon associated with this person 
	
	int screenWidth=280;
	int screenHeight=400; 
	int originalX=50;
	int originalY=50;
	private double iconLength= Math.min(0.104*screenWidth, 0.156*screenHeight);
	
	/** Create a UserView using the information from the private space, and catering it toward a user */
	public UserView(Context context, User user, PrivateSpace user_ps) {
		super(context);
		setFocusable(true); 
		this.context= context;
		MainUser= user;
		ViewPS= user_ps;
		PeopleIconHash= new HashMap<Icon,User>();
		getExistingPeople(); // get icons from people already in this PS
		//setUVtoScreen(this);
	}
	
	/** Get existing people in PrivateSpace, and get their icons */
	public void getExistingPeople(){
		LinkedList<User> People= ViewPS.getPSUsers();
		ListIterator i= People.listIterator();
		while(i.hasNext()==true){
			User person= (User)i.next();
			initializeOneIcon(context, person); // make an icon for them
			i=People.listIterator(i.nextIndex());
		}
	}
	
	/** 1) Create an icon for a person, 2) Put both icon and person in Hashmap
	 * 3) Add icon to list of icons */
	public void initializeOneIcon(Context context, User newPerson){
		Icon icon= new Icon(context, newPerson.getPicture(), randomPositions());
		PeopleIconHash.put(icon, newPerson); // add to hashmap
		allIcons.add(icon); // add to LinkedList
	}
	
	/** This constructor only necessary for part b) under Main onCreate() */
	public UserView(Context context){
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
	
	public Point randomPositions(){
		Point point=new Point();
		point.x= (int)(Math.floor(screenWidth*Math.random())); // random x position
		point.y=(int)(Math.floor(screenHeight*Math.random())); // random y position
		return point;
	}
	
	/** Assign this icon an initial xy position in Point form based on # of people already in this UserView
	 * TODO for now just randomly picked positions, but later will calculate where on GUI to put */
	public Point assignPositions(int size){
		Point point= new Point();
		point.x=(int)(Math.floor(screenWidth*Math.random())); // get x position
		point.y=(int)(Math.floor(screenHeight*Math.random())); // get y position
		return point;
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
		//int originalX;
		//int originalY;
		switch(eventaction){
		case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on a ball
			iconID=0;
			ListIterator<Icon> i= allIcons.listIterator();
			while(i.hasNext()==true){
				Icon icon= (Icon)i.next();
				// check if inside the bounds of the icon (circle)
				//get the center for the ball
				int centerX= icon.getX()+icon.getRadius()+3; 
				int centerY= icon.getY() + icon.getRadius()+3;
				// calculate the radius from the touch to the center of the ball
				double radCircle= Math.sqrt((double)(((centerX-X)*(centerX-X))+((centerY-Y)*(centerY-Y))));
				// if the radius is smaller than 23 (radius of the icon is 22, then it must be on the icon
				if(radCircle<icon.getRadius()+1){
					originalX=icon.getX(); // note the original position
					originalY=icon.getY();// note the original position
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
				Icon icon= allIcons.get(iconID-1);
				int iconRadius= icon.getRadius()+3;
				int posx= X-25;
				int posy= Y-25;
				icon.setX(X-iconRadius);
				icon.setY(Y-iconRadius);
				/**allIcons[iconID-1].setX(X-25);
				allIcons[iconID-1].setY(Y-25); */
			}
			break;
		case MotionEvent.ACTION_UP:
			//touch drop - just do things here after dropping
			if(iconID>0){
				Icon icon= allIcons.get(iconID-1);
				if(icon.getX()<2*icon.getRadius()){ // if close to left
					icon.setX(originalX);
					icon.setY(originalY);
					// Invite new person to a new privatespace
					/*MainUser.createSpace(MainUser);
					PrivateSpace newPS= new PrivateSpace();
					UserView newUV= newPS.getPeopleUVHash().get(MainUser);  
					newUV.joinRequest(PeopleIconHash.get(icon)); */
				}
				if(icon.getX()>screenWidth-2*icon.getRadius()){ // if close to right
					icon.setX(originalX);
					icon.setY(originalY);
					// Invite new person to a new privatespace
					/*.createSpace(MainUser);
					PrivateSpace newPS= new PrivateSpace();
					UserView newUV= newPS.getPeopleUVHash().get(MainUser); 
					newUV.joinRequest(PeopleIconHash.get(icon)); */
				} 
			}
			break;
		}
			//redraw the canvas
		invalidate();
		return true;
	}
	
	
	/** Find a person you wish to invite to this PrivateSpace */
	public User findFriend(String username){
		User friend= User.findUser(username);
		return friend;
	}

	/** Invite a person you found to this PrivateSpace
	 * this will trigger a method in PrivateSpace that will invite the person */
	public void joinRequest(User friend){
		ViewPS.addToPeopleRequested(friend);
	}

	/** A person joined this PrivateSpace, add his/her icon to page */
	public void addFriend(User newFriend){
		MainUser.addCurrentBuddy(newFriend); // add buddy to this UserView
		initializeOneIcon(context, newFriend); // create an icon for this person, add to this UserView
	}

	/** A person left this PrivateSpace, remove his/her icon */
	public void deleteFriend(User friend){
		PeopleIconHash.remove(friend);
		MainUser.removeCurrentBuddy(friend);
	}

	/** You are leaving this UserView/PrivateSpace */
	public void youLeaveSpace(){
		MainUser.deleteUserView(this);
		ViewPS.userLeft(MainUser); //notify PrivateSpace
	}

	/** Returns the PrivateSpace this UserView represents */
	public PrivateSpace getPrivateSpace(){
		return ViewPS;
	}
	
	/** Get who's UserView this is */
	public User getMainUser(){
		return MainUser;
	}
	
	public HashMap<Icon,User> getPeopleIconHash(){
		return PeopleIconHash;
	}
	
	private void setUVtoScreen(UserView userview){
		if(MainUser==((Main)context).getMainUser())
			((Main)context).setUserView(userview);
	}
	
	public LinkedList<Icon> getAllIcons(){
		return allIcons;
	}
}