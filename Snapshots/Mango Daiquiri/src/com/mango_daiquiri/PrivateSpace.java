package com.mango_daiquiri;

/** A PrivateSpace represents a conference room with a number of Users in it */

/** Author: Nora Ng-Quinn 
 * last updated: 10/13/10 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;


public class PrivateSpace {
	private LinkedList<User> PSUsers; // People talking in this PrivateSpace
	private HashMap<User,UserView> PeopleUViewHash; // Hashmap: input User to get corresponding UserView that represents this Private Space 	
	private LinkedList<User> peopleRequested; // List of Users who have been requested to join this PrivateSpace
	
	/** Create a new private space, and put the creator in the private space */
	public PrivateSpace(){
		// initiate PrivateSpace 
		PSUsers= new LinkedList<User>();
		PeopleUViewHash= new HashMap<User,UserView>();
		peopleRequested= new LinkedList<User>();
	}
	
	/** add creator and UserView to this empty PrivateSpace */
	public void addCreator(User creator, UserView userview){
		PSUsers.add(creator); 
		PeopleUViewHash.put(creator, userview); 
	}
	
	
	public LinkedList<User> getPSUsers(){
		return PSUsers;
	}
	
	/** Invite a person to join this PrivateSpace */
	public void addToPeopleRequested(User user){
		peopleRequested.add(user); // add to list of people who have been invited to group, but have not replied yet
		user.addSpaceRequest(this); 
		// TODO: pop up or something sent to friend
	}
	
	/** The person has either accepted or rejected their private space request */
	public void removeToPeopleRequested(User user){
		peopleRequested.remove(user);
	}
	
	public LinkedList<User> getPeopleRequested(){
		return peopleRequested;
	}
	
	/** Add a user to this private space */ 
	public void userJoined(User newUser, UserView userV){
		// Add person to everyone else's UserView (before adding the new person)
		ListIterator<User> i= PSUsers.listIterator(0);
		while(i.hasNext()==true){
			User person= i.next();
			
			UserView uv= PeopleUViewHash.get(person); 
			uv.addFriend(newUser);
			
			i= PSUsers.listIterator(i.nextIndex()); // next index
		}
		// Add person to Private Space
		PSUsers.add(newUser); // add users to list of people in this private space
		PeopleUViewHash.put(newUser, userV); // add user and corresponding UserView to Hashmap
	}

	/** Delete a user from this private space */ 
	public void userLeft(User user){
		// Remove person from Private Space
		PSUsers.remove(user);
		PeopleUViewHash.remove(user);
		// Remove person from everyone else's UserView
		ListIterator<User> i= PSUsers.listIterator(0);
		while(i.next()!=null){
			User person= i.next();
			UserView uv= (UserView)PeopleUViewHash.get(person);
			uv.deleteFriend(user);
			i= PSUsers.listIterator(i.nextIndex()); // next index
		}
	}
	
	public HashMap<User,UserView> getPeopleUVHash(){
		return PeopleUViewHash;
	}
}

