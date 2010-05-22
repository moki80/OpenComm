package com.cornell.opencomm.client;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Simple Dialog to create a MultiUserChat room
 * 
 * @author Abhishek
 *
 */
public class XMPPRoomCreateDialog extends Dialog implements OnClickListener {

	private XMPPClient xmppClient;
	private String FQRoomName; // A fully qualified room name of the form CUTestRoom@conference.rebeldom

	/**
	 * Instantiates the XMPPClientSettings with a reference to the main Activity class XMPPClient
	 * @param xmppClient instance of the main Activity class
	 */
    public XMPPRoomCreateDialog(XMPPClient xmppClient) {
        super(xmppClient);
        this.xmppClient = xmppClient;
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.room_create);
        getWindow().setFlags(4, 4);
        setTitle("Create MUC Room");
        Button ok = (Button) findViewById(R.id.create);
        ok.setOnClickListener(this);
    }

    public void onClick(View v) {
        FQRoomName = getText(R.id.room_name) == null || getText(R.id.room_name).equals("") ? "CUTestRoom@conference.rebeldom" : getText(R.id.room_name);
        String nickname = getText(R.id.nickname) == null || getText(R.id.nickname).equals("") ? "curtis" : getText(R.id.nickname);
        
        MultiUserChat muc = new MultiUserChat(xmppClient.getConnection(), FQRoomName);
        // MultiUserChat muc = new MultiUserChat(xmppClient.getConnection(), room_name + "@talk.google.com");
        
        try{
        	if (muc != null){
        		Log.i(XMPPClientLogger.TAG, "MUC is not null!");
        		muc.create(nickname);
            	muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
            	xmppClient.setMUC(muc);
            	
            	if(!muc.isJoined())
            		muc.join(nickname);
            	
            	if(!muc.isJoined())
            		throw new XMPPException("Failed to join room!");
            	else {
            		//
            	}
        	} else {
        		Log.i(XMPPClientLogger.TAG, "Failed to create MUC");
        	}
        	
        } catch(XMPPException ex){
        	Log.e(XMPPClientLogger.TAG, "[RoomCreateDialog] Failed to create room " + FQRoomName);
        	// Log.e(XMPPClientLogger.TAG, "Exception", ex);
        	Log.e(XMPPClientLogger.TAG, ex.toString());
        	xmppClient.setMUC(null);
        }        
        
        dismiss();
    }

    private String getText(int id) {
        EditText widget = (EditText) this.findViewById(id);
        return widget.getText().toString();
    }
    
    public String getRoomName(){
    	return FQRoomName;
    }

}
