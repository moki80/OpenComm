package com.cornell.opencomm.jingleimpl;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class JingleIQProcess {
	
	private XMPPConnection connection;
	//private static JingleIQPacket incomingIQ;

	
	public JingleIQProcess(){
	}
	
	/*
	public JingleIQProcess(JingleIQPacket jiq) {
		this.incomingIQ = jiq;
	}
	*/
	
	public void setConnection(XMPPConnection c){
		connection = c;
	}
	
	// TODO: CURTIS: This is where you actually get the IP and send!
	public void sessionInitiate(String jidFrom, String jidTo){
		JingleIQPacket jp = new JingleIQPacket(jidFrom, jidTo, "session-initiate");
		
		//Adding content to the IQPacket
		PayLoadType p1 = new PayLoadType();
		PayLoadType p2 = new PayLoadType();
	
		//Payload-Type of G.711 A-Law can be found from RFC 5391
		p1.setid("8");
		p1.setname("PCMA");
		p2.setid("0");
		p2.setname("PCMU");
	
		//Adding a transportation candidate for raw-UDP
		TransportCandidate c1 = new TransportCandidate();
		c1.setcomponent("1");
		c1.setgeneration("0");
		c1.setid("'e10747gf11'");
		c1.setip("192.168.1.101");
		c1.setport("8998");
	
		jp.addPayLoadType(p1);
		jp.addPayLoadType(p2);
		jp.addTransportCandidate(c1);
		
		Log.i("TestXMPPClient", "In session initiate: " + "From: "  + jp.getFrom()+ "To: " + jp.getTo() + 
				 "Initiator: " + jp.getinitiator() + "Responder: " + jp.getresponder());
		
		connection.sendPacket(jp);
	}
	
	public void sessionIncoming(JingleIQPacket incomingIQ){
		
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
	
	// TODO: CURTIS: This is where you actually get the IP and send!
	public void sessionAccept(JingleIQPacket incomingIQ, String initiator, String responder) {
		
		System.out.println("Now sending out accepting packat!");
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-accept");
		jp.setinitiator(initiator);
		jp.setresponder(responder);
		
		//Adding content to the IQPacket
		PayLoadType p1 = new PayLoadType();
		PayLoadType p2 = new PayLoadType();
		
		//Payload-Type of G.711 A-Law can be found from RFC 5391
		p1.setid("8");
		p1.setname("PCMA");
		p2.setid("0");
		p2.setname("PCMU");
		
		//Adding a transportation candidate for raw-UDP
		TransportCandidate c1 = new TransportCandidate();
		c1.setcomponent("1");
		c1.setgeneration("0");
		c1.setid("'e10747gf11'");
		c1.setip("192.168.1.102");
		c1.setport("8999");
		
		jp.addPayLoadType(p1);
		jp.addPayLoadType(p2);
		jp.addTransportCandidate(c1);
		
		Log.i("TestXMPPClient", "In session accept: " + "From: "  + jp.getFrom()+ "To: " + jp.getTo() + 
				 "Initiator: " + jp.getinitiator() + "Responder: " + jp.getresponder());
		
		connection.sendPacket(jp);		
	}
	
	public void sessionCallAccepted(JingleIQPacket incomingIQ){
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
	
	public void sessionTerminate(JingleIQPacket incomingIQ){
		System.out.println("Now sending terminating package");
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-terminate");
		jp.setinitiator(incomingIQ.getinitiator());
		jp.setresponder(incomingIQ.getresponder());
		jp.setsid(incomingIQ.getsid());
		jp.settermination_reason("success");
		//System.out.println(jp.toXML());
		connection.sendPacket(jp);
	}
	
	public void sessionDecline(JingleIQPacket incomingIQ){
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-terminate");
		jp.setinitiator(incomingIQ.getinitiator());
		jp.setresponder(incomingIQ.getresponder());
		jp.setsid(incomingIQ.getsid());
		jp.settermination_reason("decline");
		//System.out.println(jp.toXML());
		connection.sendPacket(jp);
	}
	
	public void sessionEndcall(JingleIQPacket incomingIQ){
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
