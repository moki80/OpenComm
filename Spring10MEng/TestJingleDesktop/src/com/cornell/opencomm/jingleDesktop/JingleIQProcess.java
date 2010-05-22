package com.cornell.opencomm.jingleDesktop;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;

public class JingleIQProcess {
	
	private static XMPPConnection connection;
	//private static JingleIQPacket incomingIQ;

	/*
	public JingleIQProcess(){
	}
	
	public JingleIQProcess(JingleIQPacket jiq) {
		this.incomingIQ = jiq;
	}
	*/
	
	public static void setConnection(XMPPConnection c){
		connection = c;
	}
	
	public static void sessionInitiate(String jidFrom, String jidTo){
JingleIQPacket jp = new JingleIQPacket(jidFrom, jidTo, "session-initiate");
		
		//Adding content to the IQPacket
		
		//Description with default namespace="urn:xmpp:jingle:apps:rtp:1"
		DescriptionElementType descript = new DescriptionElementType();
		descript.setAttributeMedia("audio");
		
		
		//Payload-Type of G.711 A-Law can be found from RFC 5391
		PayloadElementType p1 = new PayloadElementType();
		PayloadElementType p2 = new PayloadElementType();
		p1.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLAOD_PCMA_ID);
		p1.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMA_NAME);
		p2.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_ID);
		p2.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_NAME);
		
		descript.addPayload(p1);
		descript.addPayload(p2);
	
		//Adding a transportation candidate for raw-UDP
		TransportElementType transport = new TransportElementType(TransportElementType.TYPE_RAW_UDP);
		CandidateElementType c = new CandidateElementType(CandidateElementType.TYPE_LOCAL_CANDIDATE);
		c.setAttributeComponent((short)1);
		c.setAttributeGeneration((short)0);
		//TODO: random generate 10 digit candidate ID
		c.setAttributeID("0000000000");
		c.setAttributeIP("192.168.1.103");
		c.setAttributePort((short)8998);
		
		transport.addCandidate(c);
		
		ContentElementType content = new ContentElementType();
		content.setAttributeCreator("initiator");
		content.setAttributeName("audio-content");
		content.setAttributeSenders("both");
		content.setElementDescription(descript);
		content.setElementTransport(transport);
		
		jp.addContentType(content);	
	
		
		
		System.out.println(jp.toXML());
		
		connection.sendPacket(jp);
	}
	
	public static void sessionIncoming(JingleIQPacket incomingIQ){
		
		IQ ackIQ = new IQ(){
			@Override
			public String getChildElementXML(){
				return null;
			}
		};
		ackIQ.setType(IQ.Type.RESULT);
		ackIQ.setTo(incomingIQ.getFrom());
		ackIQ.setFrom(incomingIQ.getTo());
		connection.sendPacket(ackIQ);
	}
	
	public static void sessionAccept(JingleIQPacket incomingIQ) {
		
		System.out.println("Now sending out accepting packat!");
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-accept");
		jp.setAttributeSID(incomingIQ.getAttributeSID());
		
		//Adding content to the IQPacket
		
		//Description with default namespace="urn:xmpp:jingle:apps:rtp:1"
		DescriptionElementType descript = new DescriptionElementType();
		descript.setAttributeMedia("audio");
		
		
		//Payload-Type of G.711 A-Law can be found from RFC 5391
		PayloadElementType p1 = new PayloadElementType();
		PayloadElementType p2 = new PayloadElementType();
		p1.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLAOD_PCMA_ID);
		p1.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMA_NAME);
		p2.setAttributeID(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_ID);
		p2.setAttributeName(PayloadElementType.SupportedPayloadType.PAYLOAD_PCMU_NAME);
		
		descript.addPayload(p1);
		descript.addPayload(p2);
		
		//Adding a transportation candidate for raw-UDP
		TransportElementType transport = new TransportElementType(TransportElementType.TYPE_RAW_UDP);
		CandidateElementType c = new CandidateElementType(CandidateElementType.TYPE_LOCAL_CANDIDATE);
		c.setAttributeComponent((short)1);
		c.setAttributeGeneration((short)0);
		//TODO: random generate 10 digit candidate ID
		c.setAttributeID("0000000001");
		c.setAttributeIP("192.168.1.102");
		c.setAttributePort((short)8998);
		
		transport.addCandidate(c);
		
		ContentElementType content = new ContentElementType();
		content.setAttributeCreator("initiator");
		content.setAttributeName("audio-content");
		content.setAttributeSenders("both");
		content.setElementDescription(descript);
		content.setElementTransport(transport);
		
		jp.addContentType(content);
		
		
		
		//Log.i("TestXMPPClient", "In session accept: " + "From: "  + jp.getFrom()+ "To: " + jp.getTo() + 
		//		 "Initiator: " + jp.getinitiator() + "Responder: " + jp.getresponder());
		
		connection.sendPacket(jp);		
	}
	
	public static void sessionCallAccepted(JingleIQPacket incomingIQ){
		IQ ackIQ = new IQ(){
			@Override
			public String getChildElementXML(){
				return null;
			}
		};
		ackIQ.setType(IQ.Type.RESULT);
		ackIQ.setTo(incomingIQ.getFrom());
		ackIQ.setFrom(incomingIQ.getTo());
		connection.sendPacket(ackIQ);
	}
	
	public static void sessionTerminate(JingleIQPacket incomingIQ){
		System.out.println("Now sending terminating package");
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-terminate");
		jp.setAttributeSID(incomingIQ.getAttributeSID());
		jp.setElementReason(new ReasonElementType(ReasonElementType.TYPE_SUCCESS,null));
		connection.sendPacket(jp);
	}
	
	public static void sessionDecline(JingleIQPacket incomingIQ){
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-terminate");
		jp.setAttributeSID(incomingIQ.getAttributeSID());
		jp.setElementReason(new ReasonElementType(ReasonElementType.TYPE_DECLINE, null));
		connection.sendPacket(jp);
	}
	
	public static void sessionEndcall(JingleIQPacket incomingIQ){
		IQ ackIQ = new IQ(){
			@Override
			public String getChildElementXML(){
				return null;
			}
		};
		ackIQ.setType(IQ.Type.RESULT);
		ackIQ.setTo(incomingIQ.getFrom());
		ackIQ.setFrom(incomingIQ.getTo());
		connection.sendPacket(ackIQ);
	}
}
