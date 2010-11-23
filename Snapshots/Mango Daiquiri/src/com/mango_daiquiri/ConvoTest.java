package com.mango_daiquiri;

import junit.framework.TestCase;

public class ConvoTest extends TestCase {
	public void testPerson(){
		System.out.println("woooho");
		User User1= new User("NoratheExplora", "Dora");
        User1.changePicture(R.drawable.bol_groen);
        User User2= new User("RisaTheBeasta", "Raaawr");
        User1.changePicture(R.drawable.bol_rood);
        User User3= new User("Makotoroni", "Cheese");
        User1.changePicture(R.drawable.bol_blauw);
        User User4= new User("Jason", "IWantNobodyButChu");
        User1.changePicture(R.drawable.bol_geel);
        User User5= new User("GraemeCracka", "NoEmailsPlease");
        User1.changePicture(R.drawable.bol_paars);
        
        UserView view= (UserView)User1.getUserViews().get(0); // user1's first UserView
        User friend1= view.findFriend("RisaTheBeasta"); // find a friend from your UserView
        assertEquals(User2, friend1);
        /**
        view.joinRequest(friend1); // invite person to this specific PrivateSpace
        friend1.acceptSpaceRequest((PrivateSpace)friend1.getSpaceRequests().get(0)); // other friend accept PrivateSpace
        User friend2= view.findFriend("Makotoroni"); // find a friend from your UserView
        view.joinRequest(friend2); // invite person to this specific PrivateSpace
        friend2.acceptSpaceRequest((PrivateSpace)friend2.getSpaceRequests().get(0)); // other friend accept PrivateSpace */
        
		
	}
}
