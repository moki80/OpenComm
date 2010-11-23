package com.mango_daiquiri;

/** A User represents a person who has an account in our program and can partake in conference calls */

/** Author: Nora Ng-Quinn 
 * last updated: 10/13/10 */

/** How it works 
 *  
 */
// TODO: private phone model? private phone server? Resize icon image and figure out format
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;


//TODO: static user profiles
public class User {
	private String username=null;
	private String password=null;
	public Context context; // Android activity host (Main)
	private String description=""; // Description of self
	private int icon; // Drawable int source such as R.drawable.bol_groen 
	
	private static HashMap<String,User> findWithUsername= new HashMap<String,User>(); // Hashmap: can find User by searching Username
	private MapView userMapView; // This user's MapView
	private LinkedList<UserView> UserViewsList; // List of all UserViews this person has
	private LinkedList<User> buddies; // List of all official friends
	
	private LinkedList<User> allCurrentBuddies; // List of all people talking to, not limited to official friends
	private LinkedList<User> currentBuddies; // List of all people talking to, but only one instance per person
	//private LinkedList<User> buddyHistory; // List of all people you have talked to ever
	private LinkedList<PrivateSpace> spaceRequests; // PrivateSpaces that have invited you to join, you haven't responded yet
	private LinkedList<User> friendRequests; // People that have requested you as a friend, you have not responded yet
	private LinkedList<User> friendsYouRequested; // People you have requested as a friend, s/he has not responded yet
	
	/** Constructor: Input desired username. Automatically creates a PrivateSpace and UserView with only you in it */
	public User(Context context, String username, String password){
		this.context= context;
		this.username= username;
		this.password= password;
		// initiate linked lists
		findWithUsername.put(username, this); 
		userMapView= new MapView(this);
		UserViewsList=new LinkedList<UserView>();
		buddies= new LinkedList<User>(); 
		currentBuddies= new LinkedList<User>();
		spaceRequests= new LinkedList<PrivateSpace>(); 
		friendRequests= new LinkedList<User>(); 
		friendsYouRequested= new LinkedList<User>();
		allCurrentBuddies= new LinkedList<User>();
		// Create a new PrivateSpace and UserView, put this person in it TODO put this under main
		createSpace(this); 
	}
	
	/** Constructor: Input desired username. Automatically creates a PrivateSpace and UserView with only you in it */
	public User(/*Context context, */String username, String password){
		//this.context= context;
		this.username= username;
		this.password= password;
		// initiate linked lists
		findWithUsername.put(username, this); 
		userMapView= new MapView(this);
		UserViewsList=new LinkedList<UserView>();
		buddies= new LinkedList<User>(); 
		currentBuddies= new LinkedList<User>();
		spaceRequests= new LinkedList<PrivateSpace>(); 
		friendRequests= new LinkedList<User>(); 
		friendsYouRequested= new LinkedList<User>();
		allCurrentBuddies= new LinkedList<User>();
		// Create a new PrivateSpace and UserView, put this person in it TODO put this under main
		createSpace(this); 
	}
	
	public Context getContext(){
		return context;
	}
	
	/** Create a new PrivateSpace and UserView with only this User in it
	 * set this UserView to the screen */
	public void createSpace(User creator){
		PrivateSpace newPrivateSpace= new PrivateSpace(); // create new PrivateSpace
		UserView newUserView= new UserView(context, creator, newPrivateSpace); // create new UserView
		newPrivateSpace.addCreator(creator,newUserView); // add this person and the UserView to the new PrivateSpace 
		UserViewsList.add(newUserView); // add to this User's list of UserViews
		
		// set this UserView up onscreen if you are the person running this phone TODO: fix
		if(this==((Main)context).getMainUser())
			((Main)context).setUserView(newUserView); 
	}

	/** Returns LinkedList of people whom this User is talking to */
	public LinkedList<User> getCurrentBuddies(){
		return currentBuddies;
	}
	
	/** Returns LinkedList of User's official friends */
	public LinkedList<User> getBuddies(){
		return buddies;
	}
	
	/** Find a user with his/her username */ 
	public static User findUser(String username){
		User friend= findWithUsername.get(username);
		//showUserProfile(friend); TODO: do this later
		return friend;
	}
	
	/** Send a friend request to this User 
	 * Meant to be used when you friend-request this person directly from your private space*/
	public void friendRequest(User friend){
		friend.friendRequests.add(this); // add to that user's linked list of request
		this.friendsYouRequested.add(friend); // add to your linked list of people you requested
	}
	
	/** Accept a friend request */
	private void acceptFriendRequest(User friend){
		friendRequests.remove(friend);
		buddies.add(friend);
		friend.friendsYouRequested.remove(this);
		friend.buddies.add(this);
	}
	
	/** Reject a friend request */
	private void rejectFriendRequest(User friend){
		friendRequests.remove(friend);
		friend.friendsYouRequested.remove(this);
	}
	
	/** Someone has invited you to join their PrivateSpace! 
	 * Add this private Space to the list of groups that have invited you to join */
	public void addSpaceRequest(PrivateSpace space){
		spaceRequests.add(space);
	}
	
	/** Accept an invite to join an already created PrivateSpace */
	public void acceptSpaceRequest(PrivateSpace space){
		UserView newUserView= new UserView(context, this, space); // create a new UserView for you
		// Tell the PrivateSpace that you have joined, so it can notify other people in PrivateSPace and update
		space.userJoined(this, newUserView); 
		space.removeToPeopleRequested(this); // take off PrivateSpace request list
		spaceRequests.remove(space); // remove from your list of PrivateSpace invites
		UserViewsList.add(newUserView); // Add this UserView to your list of UserViews
	}
	
	/** Decline an invite to join a PrivateSpace */
	public void declineSpaceRequest(PrivateSpace space){
		space.removeToPeopleRequested(this);
		spaceRequests.remove(space);
	}
	
	public LinkedList<PrivateSpace> getSpaceRequests(){
		return spaceRequests;
	}
	
	/** Add a person you are talking to your current buddies list, if they are not already there */
	public void addCurrentBuddy(User friend){
		/* TODO: add this part later
		if(buddyHistory.contains(friend)==false){
			buddyHistory.add(friend);
		} */
		if(currentBuddies.contains(friend)==false){
			currentBuddies.add(friend);
		} 
		allCurrentBuddies.add(friend);
	}
	
	/** Remove a person from your allCurrentBuddies list, but check to make sure if s/he is in any other of your PrivateSpaces */
	public void removeCurrentBuddy(User friend){
		allCurrentBuddies.remove(friend);
		if(allCurrentBuddies.contains(friend)==false){
			currentBuddies.remove(friend);
		}
	}

	/** Remove a UserView from this User's UserView List, You have left a PrivateSpace */
	public void deleteUserView(UserView space){
		UserViewsList.remove(space);
	}

	/** Retrieve LinkedList of this User's UserViews */
	public LinkedList<UserView> getUserViews(){
		return UserViewsList;
	}

	/** Get Username */
	public String getUsername(){
		return username;
	}

	/** Change username */
	private void changeUsername(String newname){
		findWithUsername.remove(username);
		findWithUsername.put(newname, this);
		this.username=newname;
	}

	/** Change password */
	private void changePassword(String newPword){
		password= newPword;
	}

	/** Get person's self-description */
	public String getDescription(){
		return description;
	}

	/** Add personal description */
	private void changeDescription(String description){
		this.description= description;
	}

	/** Get person's icon "R" class source */
	public int getPicture(){
		return icon;
	}

	/** Change picture */
	public void changePicture(int picture){
		this.icon= picture;
	}
	
	
}

