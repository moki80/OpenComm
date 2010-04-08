package com.cornell.opencomm.client;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.provider.MUCUserProvider;

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class XMPPClientSettings extends Dialog implements android.view.View.OnClickListener {
	private TestXMPPClient xmppClient;

    public XMPPClientSettings(TestXMPPClient xmppClient) {
        super(xmppClient);
        this.xmppClient = xmppClient;
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.settings);
        getWindow().setFlags(4, 4);
        setTitle("XMPP Client Settings");
        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }

    public void onClick(View v) {
    	
    	// IF YOU FEEL LAZY TO TYPE STUFF ON THE EMULATOR, FILL IN DEFAULTS HERE!!
        String host = getText(R.id.host) == null || getText(R.id.host).equals("") ? "192.168.2.100" : getText(R.id.host);
        String port = getText(R.id.port) == null || getText(R.id.port).equals("") ? "5222" : getText(R.id.port);
        String service = getText(R.id.service) == null || getText(R.id.service).equals("") ? "EUROMACH" : getText(R.id.service);
        String username = getText(R.id.userid) == null || getText(R.id.userid).equals("") ? "curtis" : getText(R.id.userid);
        String password = getText(R.id.password) == null || getText(R.id.password).equals("") ? "test" : getText(R.id.password);

        Log.i("TestXMPPClient", "[XMPPClientSettings] - " + host + ":" + port + "\t" + service + "\t" + username + "\t" + password );
        
        // Before we create connection, lets register some providers.
        configureProviders();
        
        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(host, Integer.parseInt(port), service);
        XMPPConnection connection = new XMPPConnection(connConfig);

        try {
            connection.connect();
            Log.i("TestXMPPClient", "[SettingsDialog] Connected to " + connection.getHost());
        } catch (XMPPException ex) {
            Log.e("TestXMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
            Log.e("TestXMPPClient", ex.toString());
            xmppClient.setConnection(null);
        }
        try {
        	SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        	
            connection.login(username, password);
            Log.i("TestXMPPClient", "Logged in as " + connection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            
            xmppClient.setConnection(connection);            
            xmppClient.setLoggedInJID(username + "@" + service);
            
        } catch (XMPPException ex) {
            Log.e("TestXMPPClient", "[SettingsDialog] Failed to log in as " + username);
            Log.e("TestXMPPClient", ex.toString());
                xmppClient.setConnection(null);
        }
        dismiss();
    }

    private String getText(int id) {
        EditText widget = (EditText) this.findViewById(id);
        return widget.getText().toString();
    }
    
    private void configureProviders(){
    	ProviderManager pm = ProviderManager.getInstance();
    	if (pm != null){
    		Log.i("TestXMPPClient", "Got ProviderManager!!");
    		
    		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
    	} else {
    		Log.e("TestXMPPClient", "ProviderManager is null!!!");
    	}
    }
}


