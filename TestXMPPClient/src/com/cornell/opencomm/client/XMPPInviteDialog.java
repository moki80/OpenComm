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

public class XMPPInviteDialog extends Dialog implements OnClickListener {

	private TestXMPPClient xmppClient;

    public XMPPInviteDialog(TestXMPPClient xmppClient) {
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

    public void onClick(View v) {
        String userid = getText(R.id.userid);
        
        MultiUserChat muc = xmppClient.getMUC();
        if(muc != null){
        	muc.invite(userid, "Life is at stake!!");
        }
        
        dismiss();
    }

    private String getText(int id) {
        EditText widget = (EditText) this.findViewById(id);
        return widget.getText().toString();
    }

}
