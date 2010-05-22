package com.cornell.opencomm.client;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cornell.opencomm.buddies.MUCBuddy;
import com.cornell.opencomm.jingleimpl.sessionmgmt.JingleIQBuddyPacketRouter;

/**
 * Main Android Activity Class for the XMPPClient implementation.
 * 
 * This class is the point of entry of the XMPPClient application. This class is responsible for
 * registering various Presence, Packet and Invitation listeners. It is also responsible for
 * registering and defining the onClick activity for various buttons on the main screen of the
 * GUI of the XMPPClient.
 * 
 * @author Abhishek
 *
 */
public class XMPPClient extends Activity {

	// List to hold all MUC and P2P chat messages for display.
    private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();
    private XMPPClientSettings settingsDialog;
    private XMPPInvitationReceivedDialog invitationRecvdDialog;
    private XMPPRoomCreateDialog roomCreateDialog;
    private XMPPInviteDialog inviteDialog;
    private XMPPLogOff logoffDialog;
    private EditText mRecipient;
    private EditText mSendText;
    private ListView mList;
    private XMPPConnection connection;
    private MultiUserChat muc;
    private XMPPClient xmppClientInstance = null;    
    private String loggedInJID = null;
    // This is a list of all MUCBuddy objects representing the participants of an MUC Chat to which this user has joined.
    private Hashtable<String, MUCBuddy> ongoingChatBuddies = new Hashtable<String, MUCBuddy>();

    /**
     * Called when the Activity gets created first.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i(XMPPClientLogger.TAG, "onCreate called");
        setContentView(R.layout.main);

        xmppClientInstance = this;
        
        mSendText = (EditText) this.findViewById(R.id.sendText);
        Log.i(XMPPClientLogger.TAG, "mSendText = " + mSendText);
        mList = (ListView) this.findViewById(R.id.listMessages);
        Log.i(XMPPClientLogger.TAG, "mList = " + mList);
        setListAdapter();
        
        // Have the invitations received dialog ready
        invitationRecvdDialog = new XMPPInvitationReceivedDialog(getTestXMPPClientInstance());

        // Dialog for getting the xmpp settings
        settingsDialog = new XMPPClientSettings(this);      

        // Set a listener to show the settings dialog
        Button setup = (Button) this.findViewById(R.id.setup);
        setup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mHandler.post(new Runnable() {
                    public void run() {
                        settingsDialog.show();
                    }
                });
            }
        });
        
        // Dialog for gettin room creation params
        roomCreateDialog = new XMPPRoomCreateDialog(this);
        
        // Set the listener for room create dialog
        Button roomCreate = (Button) this.findViewById(R.id.room_create);
        roomCreate.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		mHandler.post(new Runnable() {
        			public void run(){
        				roomCreateDialog.show();
        			}
        		});
        	}
        });
        
        // Dialog for inviting users.
        inviteDialog = new XMPPInviteDialog(this);
        
        // Set the listener.
        Button inviteUser = (Button) this.findViewById(R.id.invite);
        inviteUser.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		mHandler.post(new Runnable() {
        			public void run(){
        				inviteDialog.show();
        			}
        		});
        	}
        });
        
        // Dialog for inviting users.
        logoffDialog = new XMPPLogOff(this);
        
        // Set the listener.
        Button logOff = (Button) this.findViewById(R.id.logoff);
        logOff.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		mHandler.post(new Runnable() {
        			public void run(){
        				logoffDialog.show();
        			}
        		});
        	}
        });

        // Set a listener to send a chat text message
        Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // String to = mRecipient.getText().toString();
            	String to = roomCreateDialog.getRoomName();
                String text = mSendText.getText().toString();

                Log.i(XMPPClientLogger.TAG, "Sending text [" + text + "] to [" + to + "]");
                Message msg = muc.createMessage();
                msg.setBody(text);
                // connection.sendPacket(msg);
                try {
                	muc.sendMessage(msg);
                } catch (XMPPException ex){
                	Log.e(XMPPClientLogger.TAG, ex.getMessage());
                }
                
                messages.add(connection.getUser() + ":");
                messages.add(text);
                setListAdapter();
            }
        });
    }

    /**
     * Convenience method to allow accessibility to the XMPPConnection object that represents the connection with the Jabber server.
     * 
     * Called by Settings dialog when a connection is establised with the Jabber server.
     * Once a connection is established, listeners for Invitation and peer-2-peer or MUC chat
     * are registered in this method. 
     *
     * @param connection the XMPPConnection that represents jabber connection
     */
    public void setConnection (XMPPConnection connection) {
        this.connection = connection;
        if (connection != null) {
        	// Add an MUC invitation listener.
        	MultiUserChat.addInvitationListener(connection, new InvitationListener() {        		
				public void invitationReceived(Connection conn, String room,
						String inviter, String reason, String password,
						Message message) {
					invitationRecvdDialog.setReceivedParameters(room, inviter, reason, password);
					Log.i("TestXMPPCLient", "Invite received from: " + inviter);
					mHandler.post(new Runnable() {
	                    public void run() {	                    	
	                        invitationRecvdDialog.show();
	                    }
	                });
					
				}
        	});
            // Add a packet listener for p2p chat
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i(XMPPClientLogger.TAG, "Got text [" + message.getBody() + "] from [" + fromName + "]");
                        messages.add(fromName + ":");
                        messages.add(message.getBody());
                        // Add the incoming message to the list view
                        mHandler.post(new Runnable() {
                            public void run() {
                                setListAdapter();
                            }
                        });
                    }
                }
            }, filter);
            
            // Add packet listener for group chat.
            PacketFilter groupchat_filter = new MessageTypeFilter(Message.Type.groupchat);
            connection.addPacketListener(new PacketListener() {
            	public void processPacket(Packet packet){
            		Message message = (Message) packet;
            		if (message.getBody() != null){
            			String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i(XMPPClientLogger.TAG, "Got text [" + message.getBody() + "] from [" + fromName + "]");
                        messages.add(fromName + ":");
                        messages.add(message.getBody());
                        // Add the incoming message to the list view
                        mHandler.post(new Runnable() {
                            public void run() {
                                setListAdapter();
                            }
                        });
            		}
            	}
            }, groupchat_filter);
        }

        // Let our JingleIQPacketRouter have easy access to the XMPPClient instance and the XMPPConnection.
        JingleIQBuddyPacketRouter.setXMPPClient(xmppClientInstance);
        JingleIQBuddyPacketRouter.setup(connection);
    }
    
    /**
     * Returns the XMPPConnection object representing the connection with the Jabber Server
     * @return the connection object
     */
    public XMPPConnection getConnection(){    	
    	return this.connection;
    }
    
    /**
     * Sets the MultiUserChat that the logged in user has joined into.
     * Once set, the listeners for invitation rejection anc acceptance can be registered.
     * 
     * @param muc the MultiUserChat object 
     */
    public void setMUC(MultiUserChat muc){
    	this.muc = muc;
    	
    	if (muc != null){
    		// Set listener to listen for rejections.
        	this.muc.addInvitationRejectionListener(new InvitationRejectionListener() {
        		public void invitationDeclined(String invitee, String reason){
        			messages.add("Invite Rejected: By " + invitee + " Reason: " + reason);
        			// Add the incoming message to the list view
                    mHandler.post(new Runnable() {
                        public void run() {
                            setListAdapter();
                        }
                    });
        		}
        	});
        	
        	// Set listener for acceptance.
        	this.muc.addParticipantListener(new PacketListener() {
        		public void processPacket(Packet packet) {			
    				Message message = (Message)packet;
    				if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i(XMPPClientLogger.TAG, "Got text [" + message.getBody() + "] from [" + fromName + "]");
                        messages.add(fromName + ":");
                        messages.add(message.getBody());
                        // Add the incoming message to the list view
                        mHandler.post(new Runnable() {
                            public void run() {
                                setListAdapter();
                            }
                        });
                    }
    			}    		
        	});
    	}    	
    }
    
    /**
     * Returns the object representing the MultiUSerChat session
     * @return the multi user chat session object
     */
    public MultiUserChat getMUC(){
    	return this.muc;
    }
    
    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.multi_line_list_item,
                messages);
        mList.setAdapter(adapter);
    }
    
    /**
     * Convenience method to get easy accessbility to the XMPPClient instance.
     * @return the instance of the XMPPClient
     */
    public XMPPClient getTestXMPPClientInstance(){
    	return xmppClientInstance;
    }
    
    /**
     * Sets the value of the loggedInJID after a successful login
     * @param jid the logged in jid
     */
    public void setLoggedInJID(String jid){
    	loggedInJID = jid;
    }
    
    /**
     * Returns the loggedInJID
     * @return the logged in jid.
     */
    public String getLoggedInJID(){
    	return loggedInJID;
    }
 
    /**
     * Returns a collection of MUCBuddy objects representing all the participants of a MultiUserChat session
     * @return Hashtable of MUCBuddy objects keyed by MUCBuddy JID.
     */
    public Hashtable<String, MUCBuddy> getOngoingChatBuddyList(){
    	return ongoingChatBuddies;
    }
    
}
