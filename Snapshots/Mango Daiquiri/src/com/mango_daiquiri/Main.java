package com.mango_daiquiri;

// 1) PrivateSpace = are a group of people in one room
/* 2) UserView = each unique view of the same private Space
 * 2ex) If Makoto, Risa, and Nora are in one Private Space, then Nora's UserView will have Makoto and Risa, Risa' UserView will have Makoto and Nora, etc.
 * So UserView has one less total people in it than the private space
 * 3) Main is the main activity.
 * 4) UVview is useless right now
 * 5) When you create a person, the User constructor automatically creates a private space and UserView with only that User in it
 */


import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class Main extends Activity {
	private User mainUser; // user who is using this program, whoever's phone this is
	private UserView currentUV; // the UserView that is being shown on screen
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        // a) if b) gives you problems and you just wish to see the icons moving 
        // then comment b) out, uncomment this below, uncomment initializeIcons() under UserView,
        
        setContentView(new UserView(this));
        
        // b) create new users, will automatically make a UserViews for each
        User User1= new User(this, "NoratheExplora", "Dora"); // Nora
        User1.changePicture(R.drawable.bol_groen);
        User User2= new User(this, "RisaTheBeasta", "Raaawr"); // Risa
        User1.changePicture(R.drawable.bol_rood);
        User User3= new User(this, "Makotoroni", "Cheese"); // Makoto
        User1.changePicture(R.drawable.bol_blauw);
        User User4= new User(this, "Jason", "IWantNobodyButChu"); // Jason
        User1.changePicture(R.drawable.bol_geel);
        User User5= new User(this, "GraemeCracka", "NoEmailsPlease"); // Graeme Bailey
        User1.changePicture(R.drawable.bol_paars);  
        
        
        //CHECK Nora's initial PS and UV
        UserView nUV1= (UserView)User1.getUserViews().get(0); // Nora's first UserView
        PrivateSpace ps1= nUV1.getPrivateSpace(); // Nora's ps
        boolean hasU1= ps1.getPSUsers().contains(User1); // Nora's ps has nora
        String hasU1main= (nUV1.getMainUser()).getUsername(); // is Nora the main user of the UV?
        boolean hasU1reg= nUV1.getPeopleIconHash().containsKey(User1); // Does UV contain Nora's icon?
        int sizePS1= ps1.getPSUsers().size();
        int sizeUV1= nUV1.getPeopleIconHash().size();
        
        //setUserView(nUV1);
        // CHECK Risa's initial PS and UV
        UserView rUV1= (UserView)User2.getUserViews().get(0); // Risa's first UserView
        PrivateSpace ps2= rUV1.getPrivateSpace(); // Risa's ps
        boolean hasU2= ps2.getPSUsers().contains(User2); // Risa's ps has nora
        String hasU2main= (rUV1.getMainUser()).getUsername(); // is Risa the main user of the UV?
        boolean hasU2reg= rUV1.getPeopleIconHash().containsKey(User2); // Does UV contain Risa's icon?
        int sizePS2= ps2.getPSUsers().size(); 
        int sizeUV2= rUV1.getPeopleIconHash().size();
       
        // Nora invties Risa to join her only PS 
        User findRisa= nUV1.findFriend("RisaTheBeasta"); // find a friend from your UserView
        nUV1.joinRequest(findRisa); // invite person to this specific PrivateSpace, puts person in Private Space's request list
        
        //CHECK: PrivateSpace has requested Risa
        boolean RisaPSRequest= findRisa.getSpaceRequests().contains(ps1); // CHECK: PrivateSpace that requested friend1
        boolean PSRisaReq= ps1.getPeopleRequested().contains(findRisa);
        
        // Risa accepts the PS request
        findRisa.acceptSpaceRequest(ps1);
        
        //CHECK: 
        boolean NowRisaPSReq= findRisa.getSpaceRequests().contains(ps1);
        boolean NowPSRisaReq= ps1.getPeopleRequested().contains(findRisa);
        int nowSizePS1= ps1.getPSUsers().size();
        int nowSizeUV1= nUV1.getPeopleIconHash().size();
        
        // CHECK: Nora's UV
        boolean ps1hasNora= ps1.getPSUsers().contains(User1);
        boolean ps1hasRisa= ps1.getPSUsers().contains(User2);
        boolean nUV1hasRisa= nUV1.getPeopleIconHash().containsKey(User2);
        int nUV1numIcon= nUV1.getAllIcons().size();
        Icon nUV1iconList= nUV1.getAllIcons().get(0);
        User nUV1icon= nUV1.getPeopleIconHash().get(nUV1iconList);
         
        //CHECK: Risa's new UV
        UserView rUV2= User2.getUserViews().get(1);
        String rUV2owner= rUV2.getMainUser().getUsername();
        PrivateSpace rUV2ps= rUV2.getPrivateSpace();
        int sizeRUV2= rUV2.getPeopleIconHash().size();
        int sizerPS1= rUV2ps.getPSUsers().size();
        boolean rUV2hasNora= rUV2.getPeopleIconHash().containsKey(User1);
        int rUV2numIcon= rUV2.getAllIcons().size();
        Icon rUV2iconList= rUV2.getAllIcons().get(0); 
        String rUV2icon= rUV2.getPeopleIconHash().get(rUV2iconList).getUsername();
        
       // setContentView(rUV2);
        
        // Nora invites Makoto
        User findMak= nUV1.findFriend("Makotoroni"); // find a friend from your UserView
        nUV1.joinRequest(findMak);
        findMak.acceptSpaceRequest(ps1);
        UserView mUV1= User3.getUserViews().get(0);
        UserView mUV2= User3.getUserViews().get(1);
        
        //setContentView(mUV2);
        
        // CHECK: Makoto's new UV
        String mUV2owner= mUV2.getMainUser().getUsername();
        PrivateSpace mUV2ps= mUV2.getPrivateSpace();
        int sizeMUV2= mUV2.getPeopleIconHash().size();
        int sizemPS1= mUV2ps.getPSUsers().size();
        boolean mUV2hasNora= mUV2.getPeopleIconHash().containsValue(User1);
        boolean mUV2hasRisa= mUV2.getPeopleIconHash().containsValue(User2);
        int mUV2numIcon= mUV2.getAllIcons().size();
        Icon mUV2NoraiconList= mUV2.getAllIcons().get(0); 
        String mUV2Noraicon= mUV2.getPeopleIconHash().get(mUV2NoraiconList).getUsername();
        Icon mUV2RisaiconList= mUV2.getAllIcons().get(1); 
        String mUV2Risaicon= mUV2.getPeopleIconHash().get(mUV2RisaiconList).getUsername();
        
        
        
        
        
        /*
        User findJason= nUV1.findFriend("Jason"); // find a friend from your UserView
        nUV1.joinRequest(findJason);
        findJason.acceptSpaceRequest(ps1);
        User findGraeme= nUV1.findFriend("GraemeCracka"); // find a friend from your UserView
        nUV1.joinRequest(findGraeme);
        findGraeme.acceptSpaceRequest(ps1);
        
        UserView gUV2= User5.getUserViews().get(1);
        setUserView(gUV2); */
        
        TextView tv = new TextView(this);
        //tv.setText(
        	   /* "-----------Nora-------------"
        		+ ". Nora: " + User1.getUsername() 
        		+ ". Nora's UserView: " + nUV1
        		+ ". Nora's UserView has PS: " + ps1
        		+ ". PS has Nora: " + hasU1
        		+ ". Nora's UV has Nora (MainUser): " + hasU1main
        		+ ". Noras's UV has Nora (regUser): " + hasU1reg
        		+ ". Num ppl in PS: " + sizePS1
        		+ ". Num ppl in UV: " + sizeUV1
        		+ " ------------Risa--------------"
        		+ ". Risa: " + User2.getUsername() 
        		+ ". Risa's UserView: " + rUV1
        		+ ". Risa's UserView has PS: " + ps2
        		+ ". PS has Risa: " + hasU2
        		+ ". Risa's UV has Risa (MainUser): " + hasU2main
        		+ ". Risa's UV has Risa (regUser): " + hasU2reg
        		+ ". Num ppl in PS: " + sizePS2
        		+ ". Num ppl in UV: " + sizeUV2 
        		+ */ /*" ---------Nora invites Risa to her PS--------------"
        		+ ". Risa invited by Nora's PS: " + RisaPSRequest
        		+ ". Nora's PS invited Risa: " + PSRisaReq
        		+ "----------Risa accepts Nora's PS invite-----------"
        		+ ". Risa still on invite list: " + NowRisaPSReq
        		+ ". PS still on Risa's req list: " + NowPSRisaReq
        		+ ". Num ppl in PS: " + nowSizePS1
        		+ ". Num ppl in UV: " + nowSizeUV1 
        		+ ". Nora's PS has Nora: " + ps1hasNora
        		+ ". Nora's PS has Risa: " + ps1hasRisa
        		+ ". Nora's UV has Risa: " + nUV1hasRisa 
        		+ ". Nora's UV has Risa's icon: " + nUV1numIcon
        		+ ". Risa's icon in hash: " + nUV1icon
        		+ ". Risa's icon in List: " + nUV1iconList
        		+ *//*"--------Risa's new UV---------------"
        		+ ". Risa's new UV: " + rUV2
        		+ ". Risa's new UV has PS: " + rUV2ps
        		+ ". Risa owns this UV: " + rUV2owner
        		+ ". Size of Risa's UV: " + sizeRUV2
        		+ ". Size of this UV's PS: " + sizerPS1
        		+ ". Risa's UV num icon: " + rUV2numIcon
        		+ ". Risa's UV has Nora's icon:" + rUV2hasNora
        		+ ". Nora's icon in hash: " + rUV2icon
        		+ ". Nora's icon in List: " + rUV2iconList 
        		+ "--------Makoto's new UV---------------"
        		+ ". Num UV's Mak has: " + User3.getUserViews().size()
        		+ ". Mak's new UV: " + rUV2
        		+ ". Mak's new UV has PS: " + mUV2ps
        		+ ". Mak owns this UV: " + mUV2owner
        		+ ". Size of Mak's UV: " + sizeMUV2
        		+ ". Size of this UV's PS: " + sizemPS1
        		+ ". Mak's UV num icon: " + mUV2numIcon
        		+ ". Mak's UV has Nora's icon:" + mUV2hasNora
        		+ ". Mak's UV has Risa's icon:" + mUV2hasRisa
        		+ ". Nora's icon in hash: " + mUV2Noraicon
        		+ ". Nora's icon in List: " + mUV2NoraiconList 
        		+ ". Risa's icon in hash: " + mUV2Risaicon
        		+ ". Risa's icon in List: " + mUV2RisaiconList */
        	//	);
         //this.setContentView(tv);   
         // uncomment this to do do a phone system.out.println
    }
    
    /** Set the content of the window to a specific UserView */
    public void setUserView(UserView uv){
    	setContentView(uv);
    }
    
    /** Who is running this program? Who's phone is this? */
    public User getMainUser(){
    	return mainUser;
    }
    
    public void setMainUser(User user){
    	mainUser= user;
    }
}