package com.cornell.opencomm.jingleimpl.sessionmgmt;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import android.util.Log;
import com.cornell.opencomm.buddies.MUCBuddy;
import com.cornell.opencomm.client.XMPPClient;
import com.cornell.opencomm.client.XMPPClientLogger;
import com.cornell.opencomm.jingleimpl.JingleIQPacket;
import com.cornell.opencomm.jingleimpl.JingleIQProvider;

/**
 * This class is a router class for incoming <code>IQ</code> packets. It forwards the packet
 * to the concerned <code>MUCBuddy</code> object in order to handle the packet.
 * 
 * @author Abhishek
 *
 */
public class JingleIQBuddyPacketRouter {

	private static XMPPClient xmppClient = null;
	
	/**
	 * Provides easy access to the <code>XMPPClient</code> object in order to fetch the <code>ongoingChatBuddyList</code>
	 * @param client the instance of the XMPPClient object
	 */
	public static void setXMPPClient(XMPPClient client){
		xmppClient = client;
	}
	
	/**
	 * Registers listeners with proper filters in order to listen for incoming <code>IQ</code> packets
	 * On examining the sender of the packet, it forwards the packet onto the appropriate <code>MUCBuddy</code> object
	 * @param connection the <code>XMPPConnection</code> object
	 */
	public static void setup(XMPPConnection connection) {
		try {
			
			JingleIQProvider jiqProvider = new JingleIQProvider();
			
			ProviderManager.getInstance().addIQProvider(JingleIQPacket.ELEMENT_NAME_JINGLE,
					JingleIQPacket.NAMESPACE, jiqProvider);

			// add packet listener for JingleIQPacket
			connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet p) {
					Log.i(XMPPClientLogger.TAG, "Received a packet");
					if (p instanceof JingleIQPacket) {
						Log.i(XMPPClientLogger.TAG, "PacketType: JingleIQPacket");
						JingleIQPacket jiq = (JingleIQPacket) p;
						
						Log.i(XMPPClientLogger.TAG, "From: "  + jiq.getFrom()+ "To: " + jiq.getTo() + 
								 "Initiator: " + jiq.getAttributeInitiator() + "Responder: " + jiq.getAttributeResponder());
						
						MUCBuddy buddy = null;
						Log.i(XMPPClientLogger.TAG, "Looking for Buddy: " + jiq.getFrom() + " in OngoingChatBuddyList");
						if(!xmppClient.getOngoingChatBuddyList().containsKey(jiq.getFrom())){
							Log.i(XMPPClientLogger.TAG, "Does not contain key" + jiq.getFrom());
							buddy = new MUCBuddy(jiq.getJIDFrom(), xmppClient.getConnection());
							buddy.setSID(jiq.getAttributeSID());
							xmppClient.getOngoingChatBuddyList().put(jiq.getFrom(), buddy);
							Log.i(XMPPClientLogger.TAG, "Created buddy: BuddyJID = " + buddy.getbuddyJID() + 
									" Session SID: " + buddy.getSID());
							buddy.processPacket(jiq);
						} else {
							buddy = xmppClient.getOngoingChatBuddyList().get(jiq.getFrom());
							Log.i(XMPPClientLogger.TAG, "Found buddy: BuddyJID = " + buddy.getbuddyJID());
							buddy.processPacket(jiq);
						}
						
					} else if (p instanceof IQ) {
						Log.i("XMPPClient", "PacketType: IQ packet");
						IQ iq = (IQ) p;
						Log.i("XMPPClient", "From: "  + iq.getFrom()+ "To: " + iq.getTo());
						if(xmppClient.getOngoingChatBuddyList().containsKey(iq.getFrom())){
							xmppClient.getOngoingChatBuddyList().get(iq.getFrom()).processPacket(iq);
						}
					}
				}
			}, new PacketTypeFilter(IQ.class));
			
		} catch (Exception e) {
			Log.e(XMPPClientLogger.TAG, e.getMessage());
		}
	}

}
