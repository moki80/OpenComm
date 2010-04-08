package com.cornell.opencomm.jingleimpl;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class JingleIQProvider implements IQProvider {
	
	public JingleIQProvider() {
	}
	
	public IQ parseIQ(XmlPullParser Parser) throws Exception {
		//System.out.println("Inside parseIQ");
		JingleIQPacket p = new JingleIQPacket();
		int eventType = Parser.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && Parser.getName().equals("jingle"))) {
			
			if (eventType == XmlPullParser.START_TAG){
				if (Parser.getName().equals("jingle")){
					//System.out.println("In jingle");
					for (int i = 0; i < Parser.getAttributeCount(); i++){
						if (Parser.getAttributeName(i).equals("action")) p.setaction(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("sid")) p.setsid(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("initiator")) p.setinitiator(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("responder")) p.setresponder(Parser.getAttributeValue(i));
					}
				}
				if (Parser.getName().equals("payload-type")){
					//System.out.println("In payload");
					PayLoadType pl = new PayLoadType();
					for (int i = 0; i < Parser.getAttributeCount(); i++){
						if (Parser.getAttributeName(i).equals("id")) pl.setid(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("name")) pl.setname(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("clockrate")) pl.setclockrate(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("channels")) pl.setchannel(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("ptime")) pl.setptime(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("maxptime")) pl.setmaxptime(Parser.getAttributeValue(i));
					}
					p.addPayLoadType(pl);
				}
				if (Parser.getName().equals("candidate")){
					//System.out.println("In candidate");
					TransportCandidate c = new TransportCandidate();
					for (int i = 0; i < Parser.getAttributeCount(); i++){
						if (Parser.getAttributeName(i).equals("component")) c.setcomponent(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("generation")) c.setgeneration(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("foundation")) c.setfoundation(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("id")) c.setid(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("ip")) c.setip(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("port")) c.setport(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("priority")) c.setpriority(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("network")) c.setnetwork(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("rel-addr")) c.setrel_addr(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("rel-port")) c.setrel_port(Parser.getAttributeValue(i));
						if (Parser.getAttributeName(i).equals("type")) c.settype(Parser.getAttributeValue(i));		
					}
					//System.out.println("Adding Transport Candidate");
					p.addTransportCandidate(c);
				}
				if (Parser.getName().equals("reason")){
					eventType = Parser.next();
					p.settermination_reason(Parser.getName());
					System.out.println(p.gettermination_reason());
				}
			}
			eventType = Parser.next();
			//System.out.println(eventType);
		}
		return p;
	}

}
