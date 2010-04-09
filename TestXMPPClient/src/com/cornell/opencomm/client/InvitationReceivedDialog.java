package com.cornell.opencomm.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

import com.cornell.opencomm.buddies.MUCBuddy;
import com.cornell.opencomm.jingleimpl.JingleIQProcess;
import com.cornell.opencomm.jingleimpl.Wait;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InvitationReceivedDialog extends Dialog {

	private TestXMPPClient xmppClient;
	private String room;
	private String inviter;
	private String reason;
	private String password;

	public InvitationReceivedDialog(TestXMPPClient xmppClient){
		super(xmppClient);
		this.xmppClient = xmppClient;
	}
	
    public InvitationReceivedDialog(TestXMPPClient xmppClient, String room, String inviter,
    		String reason, String password) {
        super(xmppClient);
        this.xmppClient = xmppClient;
        this.room = room;
        this.inviter = inviter;
        this.reason = reason;
        this.password = password;
    }

    public void setReceivedParameters(String room, String inviter, String reason, String password){
    	this.room = room;       
        this.reason = reason;
        this.password = password;
        this.inviter = inviter;
        Log.i("TestXMPPClient", "Inviter in setReceivedParams is " + inviter);
    }
    
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.invite_received);
        getWindow().setFlags(4, 4);
        setTitle("Received Invitation");
        setText(R.id.invitation);
        Button accept = (Button) findViewById(R.id.accept);
        Button decline = (Button) findViewById(R.id.decline);
        
        accept.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				MultiUserChat muc = new MultiUserChat(xmppClient.getConnection(), room);
				xmppClient.setMUC(muc);
				try {
					muc.join(xmppClient.getConnection().getUser(), password);
					
					// Sleep for sometime .. 
					// Wait.waitforsec(5);
					
					// ArrayList<String> occupantsList = getMUCOccupants();
					ArrayList<String> occupantsList = new ArrayList<String>();
					occupantsList.add(inviter);
					Iterator<String> occupants = occupantsList.iterator();
					
					// Iterator<String> occupants = getMUCOccupants();					
					
					// List<Affiliate> members = (ArrayList<Affiliate>)muc.getMembers();
					// Iterator<String> occupants = muc.getOccupants();
					
					// Initiate Jingle session with all members of the chat room.
					if(occupants == null || !occupants.hasNext()){
						Log.i("TestXMPPClient", "MUC has no occupants!!");
					} else {
						while(occupants.hasNext()){
							// String occupant = StringUtils.parseBareAddress(occupants.next());
							String occupant = occupants.next();
							// Log.i("TestXMPPClient", "Occupant name = " + occupant);
							MUCBuddy buddy = null;
							if(!xmppClient.getOngoingChatBuddyList().containsKey(occupant)){
								buddy = new MUCBuddy(occupant, xmppClient.getConnection(), xmppClient.getLoggedInJID());
								xmppClient.getOngoingChatBuddyList().put(occupant, buddy);
								Log.i("TestXMPPClient", "Inserted buddy: " + occupant);
								
								// TODO: Trim inviter.
								Log.i("TestXMPPClient", "LoggedInJID is " + xmppClient.getLoggedInJID() + " Occupant is " + occupant);
								buddy.getJingleIQProcess().sessionInitiate(xmppClient.getLoggedInJID(), occupant);
								buddy.getSessionState().setinitiated();
							}
						}						
					}
					
					/*
					ArrayList<Affiliate> admins = new ArrayList<Affiliate>(muc.getAdmins());
					if(admins == null || admins.isEmpty()){
						Log.i("TestXMPPClient", "MUC has no admins!!");
					} else {
						for(Affiliate admin: admins){
							MUCBuddy buddy = null;
							if(!xmppClient.getOngoingChatBuddyList().containsKey(admin.getJid())){
								buddy = new MUCBuddy(admin.getJid(), xmppClient.getConnection(), xmppClient.getLoggedInJID());
								xmppClient.getOngoingChatBuddyList().put(admin.getJid(), buddy);
								
								// TODO: Trim inviter.
								buddy.getJingleIQProcess().sessionInitiate(xmppClient.getLoggedInJID(), inviter);
								buddy.getSessionState().setinitiated();
							}
						}						
					}*/
					
				} catch (XMPPException ex){
					Log.e("TestXMPPClient", "[InvitationReceived]" + "Failed to join!!");
					Log.e("TestXMPPClient", ex.toString());
				}
				
				dismissDialog();
			}        	
        });
        
        decline.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View v){
        		MultiUserChat.decline(xmppClient.getConnection(), room, inviter, "Busy!");
        		dismissDialog();
        	}
        });
    }

    private void dismissDialog(){
    	dismiss();
    }
    
    private String getStrippedRoom(String room){
    	String[] tokens1 = room.split("@");
    	String[] tokens2 = tokens1[1].split("\\.");
    	return tokens1[0] + "@" + tokens2[1];
    }
    
    private Iterator<String> getMUCOccupants(){
    	ArrayList<String> list = new ArrayList<String>();
    	String strippedRoom = getStrippedRoom(room);
    	Log.i("TestXMPPCLient", "Stripped room is " + strippedRoom);
    	try {
    		ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(xmppClient.getConnection());    	
        	DiscoverItems discoverItems = sdm.discoverItems(strippedRoom); 
        	Iterator<Item> items = discoverItems.getItems();
        	while(items.hasNext()){
        		Item item = items.next();        		
        		String occupant = item.getEntityID();
        		list.add(occupant);
        	}
        	return list.iterator();
    	} catch (XMPPException ex){
    		Log.e("TestXMPPClient", "DIscovery manager method failed", ex);
    		return null;
    	}
    	
    }
    
    private void setText(int id) {
        TextView widget = (TextView) this.findViewById(id);
        widget.setText(this.inviter + " sends you a group chat invitation \n Room:  " + this.room + "\n Reason: " + reason + "\n");
    }

}
