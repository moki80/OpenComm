package com.cornell.opencomm.jingleimpl.sessionmgmt;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

import org.jivesoftware.smack.XMPPConnection;
import org.sipdroid.net.SipdroidSocket;

import android.util.Log;

import com.cornell.opencomm.buddies.MUCBuddy;
import com.cornell.opencomm.client.XMPPClientLogger;
import com.cornell.opencomm.jingleimpl.CandidateElementType;
import com.cornell.opencomm.jingleimpl.ContentElementType;
import com.cornell.opencomm.jingleimpl.DescriptionElementType;
import com.cornell.opencomm.jingleimpl.JingleIQPacket;
import com.cornell.opencomm.jingleimpl.PayloadElementType;
import com.cornell.opencomm.jingleimpl.ReasonElementType;
import com.cornell.opencomm.jingleimpl.TransportElementType;
import com.cornell.opencomm.networking.IPAddresses;
import com.cornell.opencomm.networking.PortHandler;
import com.cornell.opencomm.rtpstreamer.ReceiverThread;

/**
 * This class contains all the actions that one Jingle entity sends to another as 
 * part of a session.
 * 
 * @author Abhishek 
 * @author Ming
 *
 */
public class JingleIQActionMessages {

	private XMPPConnection connection;
	
	/**
	 * Constructor
	 */
	public JingleIQActionMessages(){
	}
	
	/**
	 * Allows access to the <code>XMPPConnection</code> object to send IQ packets
	 * 
	 * @param conn the connection object 
	 */
	public void setConnection(XMPPConnection conn){
		connection = conn;
	}
	
	/**
	 * Sends out a <code>JingleIQPacket</code> of type <code>JingleIQPacket.AttributeActionValues.SESSION_INITIATE</code>
	 * 
	 * @param jidFrom the "from" attribute of the IQ Packet
	 * @param jidTo the "to" attribute of the IQ Packet
	 * @param buddy <code>MUCBuddy</code> object to set transport information
	 */
	public void sendSessionInitiate(String jidFrom, String jidTo, MUCBuddy buddy){
		JingleIQPacket jp = new JingleIQPacket(jidFrom, jidTo, JingleIQPacket.AttributeActionValues.SESSION_INITIATE);
		buddy.setSID(jp.getAttributeSID());
		jp.setAttributeInitiator(jidFrom);
		jp.setAttributeResponder(jidTo);

		//Description with default namespace="urn:xmpp:jingle:apps:rtp:1"
		DescriptionElementType description = new DescriptionElementType();
		description.setAttributeMedia(DescriptionElementType.MediaTypes.TYPE_AUDIO);
		
		//Payload-Type of G.711 A-Law can be found from RFC 5391
		PayloadElementType p1 = new PayloadElementType();
		PayloadElementType p2 = new PayloadElementType();
		p1.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLAOD_PCMA_ID);
		p1.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMA_NAME);
		p2.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_ID);
		p2.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_NAME);
		
		description.addPayload(p1);
		description.addPayload(p2);
	
		//Adding a transportation candidate for raw-UDP		
		TransportElementType transport = new TransportElementType(TransportElementType.TYPE_RAW_UDP);
		CandidateElementType c = new CandidateElementType(CandidateElementType.TYPE_LOCAL_CANDIDATE);
		c.setAttributeComponent((short)1);
		c.setAttributeGeneration((short)0);
		//TODO: random generate 10 digit candidate ID
		c.setAttributeID("0000000000");
		IPAddresses.discoverLocalInterfaceIPs();
		IPAddresses.discoverNATedIPs();
		// Vector<String> localIPs = IPAddresses.getLocalInterfaceIPs();
		// c.setAttributeIP(localIPs.get(0));
		Vector<String> natIPs = IPAddresses.getNATedIPs();
		c.setAttributeIP(natIPs.get(0));
		
		
		Integer port = PortHandler.getInstance().getRecvPort();
		c.setAttributePort(port.shortValue());
		
		// buddy.setLocalIPAddress(localIPs.get(0));
		buddy.setLocalIPAddress(natIPs.get(0));
		buddy.setLocalPort(port.shortValue());
		
		transport.addCandidate(c);
		
		ContentElementType content = new ContentElementType();
		content.setAttributeCreator("initiator");
		content.setAttributeName("audio-content");
		content.setAttributeSenders("both");
		content.setElementDescription(description);
		content.setElementTransport(transport);
		
		jp.addContentType(content);	
	
		Log.i(XMPPClientLogger.TAG, "Sending SessionInitiate: " + "From: "  + jp.getJIDFrom()+ "To: " + jp.getJIDTo() + 
				 "Initiator: " + jp.getAttributeInitiator() + "Responder: " + jp.getAttributeResponder());
		
		Log.i(XMPPClientLogger.TAG, "***************************************************");
		Log.i(XMPPClientLogger.TAG, jp.getChildElementXML());
		Log.i(XMPPClientLogger.TAG, "***************************************************");
		
		connection.sendPacket(jp);
	}
	
	
	/**
	 * Sends out a <code>JingleIQPacket</code> of type <code>JingleIQPacket.AttributeActionValues.SESSION_TERMINATE</code>
	 * 
	 * @param incomingIQ the <code>JingleIQPacket</code> in response to which this packet is being sent
	 * @param buddy <code>MUCBuddy</code> object to set transport information
	 */
	public void sendSessionAccept(JingleIQPacket incomingIQ, MUCBuddy buddy) {
		
		System.out.println("Now sending out accepting packat!");
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), JingleIQPacket.AttributeActionValues.SESSION_ACCEPT);
		jp.setAttributeResponder(incomingIQ.getTo());
		
		//Description with default namespace="urn:xmpp:jingle:apps:rtp:1"
		DescriptionElementType description = new DescriptionElementType();
		description.setAttributeMedia(DescriptionElementType.MediaTypes.TYPE_AUDIO);
		
		
		//Payload-Type of G.711 A-Law can be found from RFC 5391
		PayloadElementType p1 = new PayloadElementType();
		PayloadElementType p2 = new PayloadElementType();
		p1.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLAOD_PCMA_ID);
		p1.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMA_NAME);
		p2.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_ID);
		p2.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_NAME);
		
		description.addPayload(p1);
		description.addPayload(p2);
		
		//Adding a transportation candidate for raw-UDP
		TransportElementType transport = new TransportElementType(TransportElementType.TYPE_RAW_UDP);
		CandidateElementType c = new CandidateElementType(CandidateElementType.TYPE_REMOTE_CANDIDATE);
		c.setAttributeComponent((short)1);
		c.setAttributeGeneration((short)0);
		//TODO: random generate 10 digit candidate ID
		c.setAttributeID("0000000001");
		IPAddresses.discoverLocalInterfaceIPs();
		IPAddresses.discoverNATedIPs();
		// Vector<String> localIPs = IPAddresses.getLocalInterfaceIPs();
		// c.setAttributeIP(localIPs.get(0));
		Vector<String> natIPs = IPAddresses.getNATedIPs();
		c.setAttributeIP(natIPs.get(0));
		
		
		Integer port = PortHandler.getInstance().getRecvPort();
		c.setAttributePort(port.shortValue());
		
		// buddy.setLocalIPAddress(localIPs.get(0));
		buddy.setLocalIPAddress(natIPs.get(0));
		buddy.setLocalPort(port.shortValue());
		
		transport.addCandidate(c);
		
		ContentElementType content = new ContentElementType();
		content.setAttributeCreator("initiator");
		content.setAttributeName("audio-content");
		content.setAttributeSenders("both");
		content.setElementDescription(description);
		content.setElementTransport(transport);
		
		jp.addContentType(content);
		
		Log.i(XMPPClientLogger.TAG, "Sending SessionInitiate: " + "From: "  + jp.getJIDFrom()+ "To: " + jp.getJIDTo() + 
				 "Initiator: " + jp.getAttributeInitiator() + "Responder: " + jp.getAttributeResponder());
		
		Log.i(XMPPClientLogger.TAG, "***************************************************");
		Log.i(XMPPClientLogger.TAG, jp.getChildElementXML());
		Log.i(XMPPClientLogger.TAG, "***************************************************");
		connection.sendPacket(jp);		
	}
	
	/**
	 * Sends out a <code>JingleIQPacket</code> of type <code>JingleIQPacket.AttributeActionValues.SESSION_INITIATE</code>
	 * 
	 * @param jidFrom the "from" attribute of the IQ Packet
	 * @param jidTo the "to" attribute of the IQ Packet
	 * @param sid the <code>SID</code> of the session to be terminated
	 * @param reason the reason for termination specified in a <code>ReasonElementType</code> object
	 * @param buddy <code>MUCBuddy</code> object to end RTP streams with.
	 */
	public void sendSessionTerminate(String from, String to, String sid, ReasonElementType reason, MUCBuddy buddy){
		JingleIQPacket jiqPacket = new JingleIQPacket(from, to, JingleIQPacket.AttributeActionValues.SESSION_TERMINATE);
		jiqPacket.setAttributeSID(sid);
		jiqPacket.setElementReason(reason);
		
		Log.i(XMPPClientLogger.TAG, "***************************************************");
		Log.i(XMPPClientLogger.TAG, jiqPacket.getChildElementXML());
		Log.i(XMPPClientLogger.TAG, "***************************************************");
		
		connection.sendPacket(jiqPacket);	
		
		//terminate RTP streams
		if (buddy.receiver.isRunning()) buddy.receiver.halt();
		if (buddy.sender.isRunning()) buddy.sender.halt();
		buddy.pusher.removeQueue(buddy.getbuddyJID());
		if (buddy.pusher.isRunning() && buddy.pusher.numQueues() == 0) buddy.pusher.halt();
	}
}
