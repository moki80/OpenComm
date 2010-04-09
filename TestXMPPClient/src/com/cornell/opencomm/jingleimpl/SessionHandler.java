package com.cornell.opencomm.jingleimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;

import android.util.Log;

import com.cornell.opencomm.buddies.MUCBuddy;
import com.cornell.opencomm.client.TestXMPPClient;
import com.cornell.opencomm.client.XMPPClientSettings;

public class SessionHandler {

	private static TestXMPPClient xmppClient = null;
	
	public static void setXMPPClient(TestXMPPClient client){
		xmppClient = client;
	}
	
	public static void setup(XMPPConnection connection) {
		try {
			// set debugging enabled
			XMPPConnection.DEBUG_ENABLED = true;

			Log.i("TestXMPPClient", "Enabled Debug!");
			
			JingleIQProvider jingleiqprovider = new JingleIQProvider();
			ProviderManager.getInstance().addIQProvider("jingle",
					"urn:xmpp:jingle:1", jingleiqprovider);

			// add packet listener for JingleIQPacket
			connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet p) {
					Log.i("TestXMPPClient", "Received a packet");
					if (p instanceof JingleIQPacket) {
						Log.i("TestXMPPClient", "Received a JingleIQ packet");
						JingleIQPacket jiq = (JingleIQPacket) p;
						Log.i("TestXMPPClient", "From: "  + jiq.getFrom()+ "To: " + jiq.getTo() + 
								 "Initiator: " + jiq.getinitiator() + "Responder: " + jiq.getresponder());
						MUCBuddy buddy = null;
						Log.i("TestXMPPClient", "Looking for Buddy: " + jiq.getFrom());
						if(!xmppClient.getOngoingChatBuddyList().containsKey(jiq.getFrom())){
							buddy = new MUCBuddy(jiq.getFrom(), xmppClient.getConnection(), xmppClient.getLoggedInJID());
							xmppClient.getOngoingChatBuddyList().put(jiq.getFrom(), buddy);
							
							buddy.processPacket(jiq);
						} else {
							xmppClient.getOngoingChatBuddyList().get(jiq.getFrom()).processPacket(jiq);
						}
						
					} else if (p instanceof IQ) {
						Log.i("TestXMPPClient", "Received an IQ packet");
						IQ iq = (IQ) p;
						Log.i("TestXMPPClient", "From: "  + iq.getFrom()+ "To: " + iq.getTo());
						if(xmppClient.getOngoingChatBuddyList().containsKey(iq.getFrom())){
							xmppClient.getOngoingChatBuddyList().get(iq.getFrom()).processPacket(iq);
						}
					}
				}
			}, new PacketTypeFilter(IQ.class));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
