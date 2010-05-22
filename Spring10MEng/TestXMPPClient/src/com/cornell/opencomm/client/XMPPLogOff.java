package com.cornell.opencomm.client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarInputStream;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

import com.cornell.opencomm.buddies.MUCBuddy;
import com.cornell.opencomm.jingleimpl.JingleIQPacket;
import com.cornell.opencomm.jingleimpl.ReasonElementType;
import com.cornell.opencomm.jingleimpl.sessionmgmt.JingleIQActionMessages;
import com.cornell.opencomm.jingleimpl.sessionmgmt.SessionCallStateMachine;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Simple Dialog to enable a logged in user to log off from the Jabber session.
 * 
 * When a user logs off, all its ongoing sessions are terminated and various listeners
 * are deregistered.
 * 
 * @author Abhishek
 *
 */
public class XMPPLogOff extends Dialog {

	private XMPPClient xmppClient;

	/**
	 * Instantiates the XMPPClientSettings with a reference to the main Activity class XMPPClient
	 * @param xmppClient instance of the main Activity class
	 */
	public XMPPLogOff(XMPPClient xmppClient){
		super(xmppClient);
		this.xmppClient = xmppClient;
	}
	
	protected void onStart() {
        super.onStart();
        setContentView(R.layout.log_off);
        getWindow().setFlags(4, 4);
        setTitle("Log Off");
        setText(R.id.logging_off);
        
        logoff();
        
    }
    
    private void logoff(){
    	
    	Hashtable<String, MUCBuddy> ongoingChatBuddies = xmppClient.getOngoingChatBuddyList();
    	// Initiate jingle and RTP termination
    	Enumeration<MUCBuddy> buddies = ongoingChatBuddies.elements();
    	while(buddies.hasMoreElements()){
    		MUCBuddy buddy = buddies.nextElement();
    		Log.i(XMPPClientLogger.TAG, "Logoff: Looking for buddies");
    		if(buddy != null){
    			Log.i(XMPPClientLogger.TAG, "Buddy: " + buddy.getbuddyJID());
    			
    			ReasonElementType reason = new ReasonElementType(ReasonElementType.TYPE_SUCCESS, "Done, Logging Off!");
				reason.setAttributeSID(buddy.getSID());
				
				buddy.getJiqActionMessageSender().sendSessionTerminate(xmppClient.getLoggedInJID(), 
    					buddy.getbuddyJID(), buddy.getSID(), reason, buddy);
				buddy.getSessionState().changeSessionState(JingleIQPacket.AttributeActionValues.SESSION_TERMINATE);				
				
    		}
    	}
    	
    	// Leave MUC
    	MultiUserChat muc = xmppClient.getMUC();
    	if(muc != null) {
    		// This is temporary until the getOccupant/getParticiapnt api gets fixed.
    		try {
				muc.revokeOwnership(xmppClient.getLoggedInJID());
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		muc.leave();
    	}
    	
    	// Disconnect XMPPConnection
    	XMPPConnection connection = xmppClient.getConnection();
    	if(connection != null)
    		connection.disconnect();
    	
    	boolean done = false;
    	while(!Thread.interrupted() && !done){
    		try {
    			Thread.sleep(500);
    		} catch(InterruptedException ie){
    			break;
    		}
    		done = true;
    		buddies = ongoingChatBuddies.elements();
    		while(buddies.hasMoreElements()){
    			MUCBuddy buddy = buddies.nextElement();
    			if(!(buddy.getSessionState().getSessionState() == SessionCallStateMachine.STATE_ENDED)){
    				done = false;
    				break;
    			}    				
    		}
    	}
    	
    	// At the end dismiss dialog .. 
    	dismissDialog();
    }

    private void dismissDialog(){
    	dismiss();
    }
            
    private void setText(int id) {
        TextView widget = (TextView) this.findViewById(id);
        widget.setText("Logging off .. ");
    }

}
