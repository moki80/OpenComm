package com.ming8832.xmpp.smack.jingle.common;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;

public class JingleIQProcess {
	
	private static XMPPConnection connection;
	private JingleIQPacket incomingIQ;

	public JingleIQProcess(JingleIQPacket jiq) {
		this.incomingIQ = jiq;
	}
	
	public static void setConnection(XMPPConnection c){
		connection = c;
	}
	
	public void INCOMINGCALL(){
		
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
	
	public void ACCEPTING() {
		
		
		System.out.println("Now sending out accepting packat!");
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-accept");
		
		//Adding content to the IQPacket
		PayLoadType p1 = new PayLoadType();
		PayLoadType p2 = new PayLoadType();
		
		//Payload-Type of G.711 A-Law can be found from RFC 5391
		p1.setid("8");
		p1.setname("PCMA");
		p2.setid("0");
		p2.setname("PCMU");
		
		//Adding a transportation candiate for raw-UDP
		TransportCandidate c1 = new TransportCandidate();
		c1.setcomponent("1");
		c1.setgeneration("0");
		c1.setid("'e10747gf11'");
		c1.setip("192.168.1.101");
		c1.setport("8998");
		
		jp.addPayLoadType(p1);
		jp.addPayLoadType(p2);
		jp.addTransportCandidate(c1);
		
		connection.sendPacket(jp);		
	}
	
	public void CALLACCEPTED(){
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
	
	public void CALLTERMINATING(){
		System.out.println("Now sending terminating package");
		JingleIQPacket jp = new JingleIQPacket(incomingIQ.getTo(), incomingIQ.getFrom(), "session-terminate");
		jp.setinitiator(incomingIQ.getinitiator());
		jp.setresponder(incomingIQ.getresponder());
		jp.setsid(incomingIQ.getsid());
		//System.out.println(jp.toXML());
		connection.sendPacket(jp);
	}
	
	public void ENDCALL(){
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
