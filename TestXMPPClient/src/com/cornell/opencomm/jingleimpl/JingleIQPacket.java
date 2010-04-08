package com.cornell.opencomm.jingleimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;



public class JingleIQPacket extends IQ {
	
	public static String elementname = "jingle";
	public static String namespace = "\"urn:xmpp:jingle:1\"";

	
	private String JIDfrom =null;
	private String JIDto = null;
	private String action = null;
	private String sid = null;
	private String initiator = null;
	private String responder = null;
	private String description_xmlns = null;
	private String transport_xmlns = null;
	private String termination_reason = null;
	private List<PayLoadType> PayLoadTypeList= new ArrayList<PayLoadType>();
	private List<TransportCandidate> CandidateList = new ArrayList<TransportCandidate>();
	
	public JingleIQPacket() {
		this.description_xmlns = "\"urn:xmpp:jingle:apps:rtp:1\"";
		this.transport_xmlns = "\"urn:xmpp:jingle:transports:raw-udp:1\"";
	}
	
	public JingleIQPacket(String from, String to, String action){

		this.JIDfrom = from;
		this.JIDto = to;
		this.setaction(action);
		//Description and Transport's xmlns is predefine at this stage;
		this.description_xmlns = "\"urn:xmpp:jingle:apps:rtp:1\"";
		this.transport_xmlns = "\"urn:xmpp:jingle:transports:raw-udp:1\"";
		
		setType(IQ.Type.SET);
		setFrom(JIDfrom);
		setTo(JIDto);
	}
	
	public void setaction(String action){
		if (!action.startsWith("\"")){
			this.action = "\"" + action + "\"";
		}
		else {
			this.action = action;
		}
	}
	
	public String getaction(){
		return this.action.split("\"")[1];
	}
	
	public void setsid(String sid){
		if (!sid.startsWith("\"")){
			this.sid = "\"" + sid + "\"";
		}
		else {
			this.sid = sid;
		}
	}
	
	public String getsid(){
		return this.sid;
	}
	
	public void setinitiator(String initiator){
		if (!initiator.startsWith("\"")){
			this.initiator = "\"" + initiator + "\"";
		}
		else {
			this.initiator = initiator;
		}
	}

	public String getinitiator(){
		return this.initiator.split("\"")[1];
	}
	
	public void setresponder(String responder){
		if (!responder.startsWith("\"")){
			this.responder = "\"" + responder + "\"";
		}
		else {
			this.responder = responder;
		}
	}
	
	public String getresponder(){
		return this.responder.split("\"")[1];
	}
	
	public void settermination_reason(String reason){
		this.termination_reason = reason;
	}
	
	public String gettermination_reason(){
		return this.termination_reason;
	}
	
	
	public void setdescription_xmlns(String xmlns) {
		if (!xmlns.startsWith("\"")) {
			this.description_xmlns ="\"" + xmlns + "\"";
		}
		else {
			this.description_xmlns = xmlns;
		}
	}
	
	
	public void settransport_xmlns (String xmlns) {
		if (!xmlns.startsWith("\"")) {
			this.description_xmlns = "\"" + xmlns + "\"";
		}
		else {
			this.transport_xmlns = xmlns;
		}
	}
	
	public void addPayLoadType(PayLoadType p){
		PayLoadTypeList.add(p);
	}
	
	public void addTransportCandidate(TransportCandidate c){
		CandidateList.add(c);
	}
	
	public String get_jingle_headerXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(elementname).append(" xmlns=").append(namespace);
		buf.append(" action=").append(this.action);
		
		//the Initiator in this case is the JID of the SENDER
		if (this.initiator == null) {
			if (this.getaction().equals("session-initiate")) {
				setinitiator(this.JIDfrom);
				buf.append(" initiator=").append(this.initiator);
			}
			if (this.getaction().equals("session-accept")){
				//the Initiator in this case is the JID of the RECIEVER
				//the Responder in this case is the JID of the SENDER
				setinitiator(this.JIDto);
				setresponder(this.JIDfrom);
				buf.append(" initiator=").append(this.initiator);
				buf.append(" responder=").append(this.responder);
			}
		}
		else {
			buf.append(" initiator=").append(this.initiator);
			if (this.responder != null) {
				buf.append(" responder=").append(this.responder);
			}
		}
		
		if (sid != null){
			buf.append(" sid=").append(this.sid).append(">");
		}
		else {
			//Here we are using a static session ID '0000000000000000'
			//generation of unique session ID will be implemented soon
			this.setsid("0000000000000000");
			buf.append(" sid=\"0000000000000000\">");
		}
		
		return buf.toString();
	}		

	public String getdescriptionXML(List<PayLoadType> pl){
		StringBuilder buf = new StringBuilder();
		buf.append("<description xmlns=").append(this.description_xmlns).append(" media=\"audio\">");
		
		//This portion might be modify further to handle multiple payload-types
		Iterator<PayLoadType> it = pl.iterator();
		while (it.hasNext()){
			PayLoadType p = it.next();
			buf.append(p.toXML());
		}
		
		buf.append("</description>");
		return buf.toString();
	}
	
	//
	//Argument for now is a single TransportCandidate object,
	//need to modify in the future to handle multiple candidates
	//
	public String gettransportXML(List<TransportCandidate> cl){
		StringBuilder buf = new StringBuilder();
		
		//Transport method is set to jingle RAW-UDP for now
		//pwd and ufrag are both pre-define
		//Transport will probably be model into an object into the future
		buf.append("<transport xmlns=").append(this.transport_xmlns).append(">");
		//buf.append(" pwd=\"asd88fgpdd777uzjYhagZa\"");
		//buf.append(" ufrag=\"8hhy\">");
		
		Iterator<TransportCandidate> it = cl.iterator();
		while (it.hasNext()){
			TransportCandidate c = it.next();
			buf.append(c.toXML());
		}
		buf.append("</transport>");
		
		return buf.toString();	
	}
	
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		
		if (this.getaction().equals("session-terminate")){
			buf.append(this.get_jingle_headerXML());
			buf.append("<reason><");
			buf.append(this.gettermination_reason());
			buf.append("/></reason></jingle>");
			return buf.toString();
		}
		
		buf.append(this.get_jingle_headerXML());
		buf.append("<content creator=\"initiator\" name=\"this-is-the-audio-content\">");
		buf.append(this.getdescriptionXML(this.PayLoadTypeList));
		buf.append(this.gettransportXML(this.CandidateList));
		buf.append("</content>");
		buf.append("</jingle>");
		return buf.toString();
	}
	
	public String getIPAddress(){
		String ip = null;		
		ip = CandidateList.get(0).getip();
		
		String[] tokens = ip.split("\"");
		ip = tokens[1];
		return ip;
	}
	
	public int getPort(){
		String port = null;
		port = CandidateList.get(0).getport();
		String[] tokens = port.split("\"");
		return (tokens[1] == null) || tokens[1].equals("") ? -1 : Integer.parseInt(tokens[1]);
	}
}

