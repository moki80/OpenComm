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
import com.cornell.opencomm.jingleimpl.SessionHandler;

public class TestXMPPClient extends Activity {

    private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();
    private XMPPClientSettings settingsDialog;
    private InvitationReceivedDialog invitationRecvdDialog;
    private XMPPRoomCreateDialog roomCreateDialog;
    private XMPPInviteDialog inviteDialog;
    private EditText mRecipient;
    private EditText mSendText;
    private ListView mList;
    private XMPPConnection connection;
    private MultiUserChat muc;
    private TestXMPPClient xmppClientInstance = null;
    private String loggedInJID = null;
    private Hashtable<String, MUCBuddy> ongoingChatBuddies = new Hashtable<String, MUCBuddy>();

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i("TestXMPPClient", "onCreate called");
        setContentView(R.layout.main);

        xmppClientInstance = this;
        
        //mRecipient = (EditText) this.findViewById(R.id.recipient);
        //Log.i("TestXMPPClient", "mRecipient = " + mRecipient);
        mSendText = (EditText) this.findViewById(R.id.sendText);
        Log.i("TestXMPPClient", "mSendText = " + mSendText);
        mList = (ListView) this.findViewById(R.id.listMessages);
        Log.i("TestXMPPClient", "mList = " + mList);
        setListAdapter();
        
        // Have the invitations received dialog ready
        invitationRecvdDialog = new InvitationReceivedDialog(getTestXMPPClientInstance());

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
        
        // Set the listener.
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

        // Set a listener to send a chat text message
        Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // String to = mRecipient.getText().toString();
            	String to = roomCreateDialog.getRoomName();
                String text = mSendText.getText().toString();

                Log.i("TestXMPPClient", "Sending text [" + text + "] to [" + to + "]");
                Message msg = muc.createMessage();
                msg.setBody(text);
                // connection.sendPacket(msg);
                try {
                	muc.sendMessage(msg);
                } catch (XMPPException ex){
                	Log.e("TestXMPPClient", ex.getMessage());
                }
                
                messages.add(connection.getUser() + ":");
                messages.add(text);
                setListAdapter();
            }
        });
    }

    /**
     * Called by Settings dialog when a connection is establised with the XMPP server
     *
     * @param connection
     */
    public void setConnection
            (XMPPConnection
                    connection) {
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
                        Log.i("TestXMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
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
                        Log.i("TestXMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
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
        
        // Setup the Jingle Session handler.
        SessionHandler.setXMPPClient(xmppClientInstance);
        SessionHandler.setup(connection);
    }
    
    public XMPPConnection getConnection(){    	
    	return this.connection;
    }
    
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
                        Log.i("TestXMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
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
    
    public MultiUserChat getMUC(){
    	return this.muc;
    }

    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.multi_line_list_item,
                messages);
        mList.setAdapter(adapter);
    }
    
    public TestXMPPClient getTestXMPPClientInstance(){
    	return xmppClientInstance;
    }
    
    public void setLoggedInJID(String jid){
    	loggedInJID = jid;
    }
    
    public String getLoggedInJID(){
    	return loggedInJID;
    }
    
    public Hashtable<String, MUCBuddy> getOngoingChatBuddyList(){
    	return ongoingChatBuddies;
    }
    
}
