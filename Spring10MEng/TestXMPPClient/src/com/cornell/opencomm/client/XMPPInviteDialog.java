package com.cornell.opencomm.client;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Simple Dialog to enable a user to invite another user to join a MUC chat room
 * @author Abhishek
 *
 */
public class XMPPInviteDialog extends Dialog implements OnClickListener {

	private XMPPClient xmppClient;

	/**
	 * Instantiates the XMPPClientSettings with a reference to the main Activity class XMPPClient
	 * @param xmppClient instance of the main Activity class
	 */
    public XMPPInviteDialog(XMPPClient xmppClient) {
        super(xmppClient);
        this.xmppClient = xmppClient;
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.invite);
        getWindow().setFlags(4, 4);
        setTitle("Invite User");
        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }

    /**
     * Sends out an invite to the desired user
     */
    public void onClick(View v) {
        String userid = getText(R.id.userid);
        
        MultiUserChat muc = xmppClient.getMUC();
        if(muc != null){
        	muc.invite(userid, "Life is at stake!!");
    		// This is temporary because the getOccupant/getParticipant API seems to be broken in smack.
        	// If not granted ownership, a user cannot invite anyone else into the chat.
        	try {
				muc.grantOwnership(userid);
			} catch (XMPPException e) {
			}
        }
        
        dismiss();
    }

    private String getText(int id) {
        EditText widget = (EditText) this.findViewById(id);
        return widget.getText().toString();
    }

}
