package com.cornell.opencomm.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cornell.opencomm.buddies.MUCBuddy;
import com.cornell.opencomm.jingleimpl.JingleIQPacket;

/**
 * Handles the processing of the event where an invite to a MUC chat session is received
 * @author Abhishek
 *
 */
public class XMPPInvitationReceivedDialog extends Dialog {

	private XMPPClient xmppClient;
	private String room;
	private String inviter;
	private String reason;
	private String password;

	/**
	 * Instantiates the XMPPClientSettings with a reference to the main Activity class XMPPClient
	 * 
	 * @param xmppClient instance of the main Activity class
	 */
	public XMPPInvitationReceivedDialog(XMPPClient xmppClient){
		super(xmppClient);
		this.xmppClient = xmppClient;
	}
	
	/**
	 * Instantiates the XMPPClientSettings with a reference to the main Activity class XMPPClient
	 * The remainig parameters are all the parameters derived from a recieved invitation.
	 * @param xmppClient the XMPPClient instance
	 * @param room for which the invite was sent
	 * @param inviter the user that sent the invite
	 * @param reason reason for the invitation
	 * @param password password to be used to join the chat room, if provided.
	 */
    public XMPPInvitationReceivedDialog(XMPPClient xmppClient, String room, String inviter,
    		String reason, String password) {
        super(xmppClient);
        this.xmppClient = xmppClient;
        this.room = room;
        this.inviter = inviter;
        this.reason = reason;
        this.password = password;
    }

    /**
     * Convenience function to set required parameters if not available at the time of XMPPInvitationReceivedDialog creation
     * @param room
     * @param inviter
     * @param reason
     * @param password
     */
    public void setReceivedParameters(String room, String inviter, String reason, String password){
    	this.room = room;       
        this.reason = reason;
        this.password = password;
        this.inviter = inviter;
        Log.i("XMPPClient", "Inviter in setReceivedParams is " + inviter);
    }
    
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.invite_received);
        getWindow().setFlags(4, 4);
        setTitle("Received Invitation");
        setText(R.id.invitation);
        Button accept = (Button) findViewById(R.id.accept);
        Button decline = (Button) findViewById(R.id.decline);
        
        // in case the user accepts the invitation
        accept.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				// Get the instance of the MultiUserChat to be joined.
				MultiUserChat muc = new MultiUserChat(xmppClient.getConnection(), room);
				xmppClient.setMUC(muc);
				try {
					// join the chat
					muc.join(xmppClient.getConnection().getUser(), password);
					
					// For now, we build occupant lists with only the inviter because the getOccupant/getParticipant
					// API in asmack seems to be broken. This should be changed later to utilize the getMUCOccupants()
					// method
					ArrayList<String> occupantsList = new ArrayList<String>();
					occupantsList.add(inviter);
					Iterator<String> occupants = occupantsList.iterator();
					
					// Initiate Jingle session with all members of the chat room.
					if(occupants == null || !occupants.hasNext()){
						Log.i("XMPPClient", "MUC has no occupants!!");
					} else {
						while(occupants.hasNext()){
							String occupant = occupants.next();
							
							MUCBuddy buddy = null;
							
							if(!xmppClient.getOngoingChatBuddyList().containsKey(occupant)){
								// If there is no MUCBuddy to represent this participant, then we create one and
								// add it to the ongoingChatBuddyList
								buddy = new MUCBuddy(occupant, xmppClient.getConnection());
								xmppClient.getOngoingChatBuddyList().put(occupant, buddy);
								Log.i(XMPPClientLogger.TAG, "Inserted buddy: " + occupant);								
								Log.i(XMPPClientLogger.TAG, "LoggedInJID is " + xmppClient.getLoggedInJID() + " Occupant is " + occupant);
								
								// Send session-initiate to the buddy.
								buddy.getJiqActionMessageSender().sendSessionInitiate(xmppClient.getLoggedInJID(), occupant, buddy);
								buddy.getSessionState().changeSessionState(JingleIQPacket.AttributeActionValues.SESSION_INITIATE);
							}
						}						
					}
					
				} catch (XMPPException ex){
					Log.e(XMPPClientLogger.TAG, "[InvitationReceived]" + "Failed to join!!");
					Log.e(XMPPClientLogger.TAG, ex.toString());
				}
				
				dismissDialog();
			}        	
        });
        
        // In case the user declines the invitation
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
    		Log.e(XMPPClientLogger.TAG, "DIscovery manager method failed", ex);
    		return null;
    	}
    	
    }
    
    private void setText(int id) {
        TextView widget = (TextView) this.findViewById(id);
        widget.setText(this.inviter + " sends you a group chat invitation \n Room:  " + this.room + "\n Reason: " + reason + "\n");
    }

}
