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

public class XMPPRoomCreateDialog extends Dialog implements OnClickListener {

	private TestXMPPClient xmppClient;
	private String FQRoomName;

    public XMPPRoomCreateDialog(TestXMPPClient xmppClient) {
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
        FQRoomName = getText(R.id.room_name) == null || getText(R.id.room_name).equals("") ? "CUTestRoom@conference.EUROMACH" : getText(R.id.room_name);
        String nickname = getText(R.id.nickname) == null || getText(R.id.nickname).equals("") ? "curtis" : getText(R.id.nickname);
        
        MultiUserChat muc = new MultiUserChat(xmppClient.getConnection(), FQRoomName);
        // MultiUserChat muc = new MultiUserChat(xmppClient.getConnection(), room_name + "@talk.google.com");
        
        try{
        	if (muc != null){
        		Log.i("TestXMPPClient", "MUC is not null!");
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
        		Log.i("TestXMPPClient", "Failed to create MUC");
        	}
        	
        } catch(XMPPException ex){
        	Log.e("TestXMPPClient", "[RoomCreateDialog] Failed to create room " + FQRoomName);
        	// Log.e("TestXMPPClient", "Exception", ex);
        	Log.e("TestXMPPClient", ex.toString());
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
