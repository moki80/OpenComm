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
 * Dialog to get the inputs from the user to log in to a Jabber server.
 * 
 * @author Abhishek
 *
 */
public class XMPPClientSettings extends Dialog implements android.view.View.OnClickListener {
	
	private XMPPClient xmppClient;

	/**
	 * Instantiates the XMPPClientSettings with a reference to the main Activity class XMPPClient
	 * @param xmppClient instance of the main Activity class
	 */
    public XMPPClientSettings(XMPPClient xmppClient) {
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

    /**
     * Handles the bulk of processing when the user interaces with the XMPPClientSettingsDialog
     * 
     * Establishes connection with the jabber server and logs the user in to the Jabber Server
     */
    public void onClick(View v) {
    	
    	// IF YOU FEEL LAZY TO TYPE STUFF ON THE EMULATOR, FILL IN DEFAULTS HERE!!
        String host = getText(R.id.host) == null || getText(R.id.host).equals("") ? "128.84.68.83" : getText(R.id.host);
        String port = getText(R.id.port) == null || getText(R.id.port).equals("") ? "5222" : getText(R.id.port);
        String service = getText(R.id.service) == null || getText(R.id.service).equals("") ? "rebeldom" : getText(R.id.service);
        String username = getText(R.id.userid) == null || getText(R.id.userid).equals("") ? "abhishek" : getText(R.id.userid);
        String password = getText(R.id.password) == null || getText(R.id.password).equals("") ? "test" : getText(R.id.password);

        Log.i(XMPPClientLogger.TAG, "[XMPPClientSettings] - " + host + ":" + port + "\t" + service + "\t" + username + "\t" + password );
        
        // Before we create connection, lets register some providers.
        configureProviders();
        
        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(host, Integer.parseInt(port), service);
        XMPPConnection connection = new XMPPConnection(connConfig);

        try {
            connection.connect();
            Log.i(XMPPClientLogger.TAG, "[SettingsDialog] Connected to " + connection.getHost());
        } catch (XMPPException ex) {
            Log.e(XMPPClientLogger.TAG, "[SettingsDialog] Failed to connect to " + connection.getHost());
            Log.e(XMPPClientLogger.TAG, ex.toString());
            xmppClient.setConnection(null);
        }
        try {
        	SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        	
            connection.login(username, password);
            Log.i(XMPPClientLogger.TAG, "Logged in as " + connection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            
            xmppClient.setConnection(connection);            
            xmppClient.setLoggedInJID(connection.getUser());
            
        } catch (XMPPException ex) {
            Log.e(XMPPClientLogger.TAG, "[SettingsDialog] Failed to log in as " + username);
            Log.e(XMPPClientLogger.TAG, ex.toString());
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
    		Log.i(XMPPClientLogger.TAG, "Got ProviderManager!!");
    		
    		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
    	} else {
    		Log.e(XMPPClientLogger.TAG, "ProviderManager is null!!!");
    	}
    }
}


