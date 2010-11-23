package com.mango_daiquiri;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
/** A MapView is a specific User's point of view of all the PrivateSpaces s/he is currently in
 * and all the Buddies s/he is currently talking with */

/** Author: Nora Ng-Quinn 
 * last updated: 10/13/10 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

// TODO: haven't been working on this as much, work on it 
public class MapView {
	private User MapUser;
	
	private int maxBuddies=10;
	private int maxSpaces=3;
	private int numSpaces=0;
	
	private int screenWidth=300;
	private int screenHeight=200;
	private double iconLength=Math.min(0.104*screenWidth, 0.156*screenHeight);
	private double iconWSpace=(screenWidth-((maxBuddies/2)*iconLength))/((maxBuddies/2)+1); //Space in between icons in x-direction
	private double iconEdgeSpace= 10; // space in bettween top of icon and top of screen
	private int PSWidth=50;
	private int PSHeight=30;
	private int PSWSpace= (screenWidth-(numSpaces*PSWidth))/2; // Space on sides of PrivateSpaces on screen


	private LinkedList buddyPlacing; // Slots on screen for buddies
	private LinkedList spacePlacing; // Slots on screen for private spaces
	

	
	/** Constructor: MapView */
	public MapView(User user){
		MapUser= user;
		buddyPlacing= new LinkedList();
		spacePlacing= new LinkedList();
	}
	
	/** Click a person's icon, and light up which private spaces that person is in */
	public void findPrivateSpaces(int slotPerson){
		//TODO: lightUpSpace(slotPerson);
		// Find which person is in which slot
		User person = (User)buddyPlacing.get(slotPerson);
		ListIterator i= spacePlacing.listIterator(0);
		int k=0;
		while(i.hasNext()==true){
			UserView space= (UserView)i.next();
			PrivateSpace p= space.getPrivateSpace();
			LinkedList people= p.getPSUsers();
			// check if person is contained withint this linkedlist of people
			boolean inThisPS= people.contains(person);
			//if(inThisPS==true)
				// LightUpSpace(k);
			i= spacePlacing.listIterator(i.nextIndex());
			k++;
		}
		
	}
	
	/** Click a PrivateSpace icon, and light up all the people in that space
	 * Only input a the slot number of the PrivateSpace only when it is touched */
	public void findUsers(int slotSpace){
		//TODO: lightUpSpace(slotSpace);
		// Find people in the touched PrivateSpace
		UserView u= (UserView)spacePlacing.get(slotSpace);
		PrivateSpace theSpace= (PrivateSpace)u.getPrivateSpace();
		LinkedList people= theSpace.getPSUsers();
		// Light up people's icons, find the spot on screen they are in
		ListIterator i= people.listIterator(0);
		while(i.hasNext()!=false){
			User person= (User)i.next();
			int slot= buddyPlacing.indexOf(person);
			//if(slot!=-1)
			// TODO: lightUpPerson(slot);
			i= people.listIterator(i.nextIndex());
		}
	}
	
	/** 1) Get User's UserViews and display on GUI 2) Get User's list of currentBuddies 3) Display on GUI */
	private void makeGUI(){
		placeBuddies();
		// PrivateSpace/UserView placing on screen
		LinkedList cspaces= MapUser.getUserViews();
		ListIterator ii= cspaces.listIterator(0);
		while(ii.next()!=null){
			UserView u= (UserView)ii.next();
			// assign location x and y values for each UserView square for GUI
			ii= cspaces.listIterator(ii.nextIndex());
		}
	}
	
	/** Get User's list of people talking to and place their icons in People slots */ 
	private void placeBuddies(){
		// Get list of current buddies
		// Iterate through them, and place them in your list of people
		LinkedList cbuddies= MapUser.getCurrentBuddies();
		ListIterator i= cbuddies.listIterator(0); 
		int k=0;
		while(k<maxBuddies & i.next()!=null){
			User u= (User)i.next();
			int picture= u.getPicture();
			buddyPlacing.add(picture);
			k++;
			i= cbuddies.listIterator(k);
		}
	}
	
	/** Go through list of friend talking to, and draw their icons on the screen TODO: FIX THIS */
	/** NOT DONE */
	public void drawPeople(){
		/**
		ListIterator i= buddyPlacing.listIterator(0); 
		double x= iconWSpace;
		double y= iconEdgeSpace;
		for()
		
		while(i.next()!=null){
		} */
	}
	
	/** Go through Space slots, and display the PrivateSpace on screen */
	public void drawSpaces(){
		
	}
}

