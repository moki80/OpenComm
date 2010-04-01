package com.ming8832.xmpp.smack.jingle.common;
//Author: Ming Lin Date: Feb 23 Version 1.0
//This is class implements the transport candidates
//The transport candidates attribute different from tranport method
//For raw-UDP, a candidate must include the 'generation', 'id', 'ip' and 'port' attribute
//For ICE-UDP, it requires extra attributes, which is implemented here
//Attribute definition can be found at: http://xmpp.org/extensions/xep-0167.html
//Attribute can be set to null while a candidate is used for simpler transport method.

public class TransportCandidate {
	
	private String component;
	private String foundation;
	private String generation;
	private String id;
	private String ip;
	private String network;
	private String port;
	private String priority;
	private String protocol;
	private String rel_addr;
	private String rel_port;
	private String type;
	
	public TransportCandidate(){
		this.component = null;
		this.foundation = null;
		this.generation = null;
		this.id = null;
		this.ip = null;
		this.network = null;
		this.port = null;
		this.priority = null;
		this.protocol = null;
		this.rel_port = null;
		this.rel_addr = null;
		this.type = null;
	}
	
	public void setcomponent(String component){
		if (!component.startsWith("\"")){
			this.component = "\"" + component + "\"";
		}
		else {
			this.component = component;
		}
	}
	
	public void setfoundation(String foundation){
		if (!foundation.startsWith("\"")){
			this.foundation = "\"" + foundation + "\"";
		}
		else {
			this.foundation = foundation;
		}
	}
	
	public void setgeneration(String generation){
		if (!generation.startsWith("\"")){
			this.generation = "\"" + generation + "\"";
		}
		else {
			this.generation = generation;
		}
	}

	public void setid(String id){
		if (!id.startsWith("\"")){
			this.id = "\"" + id + "\"";
		}
		else {
			this.id = id;
		}
	}
	
	public void setip(String ip){
		if (!ip.startsWith("\"")){
			this.ip = "\"" + ip + "\"";
		}
		else {
			this.ip = ip;
		}
	}
	
	public void setnetwork(String network){
		if (!network.startsWith("\"")){
			this.network = "\"" + network + "\"";
		}
		else {
			this.network = network;
		}
	}
	
	public void setport(String port){
		if (!port.startsWith("\"")){
			this.port = "\"" + port + "\"";
		}
		else {
			this.port = port;
		}
	}
	
	public void setpriority(String priority){
		if (!priority.startsWith("\"")){
			this.priority = "\"" + priority + "\"";
		}
		else {
			this.priority = priority;
		}
	}
	
	public void setprotocol(String protocol){
		if (!protocol.startsWith("\"")){
			this.protocol = "\"" + protocol + "\"";
		}
		else {
			this.protocol = protocol;
		}
	}
	
	public void setrel_addr(String rel_addr){
		if (!rel_addr.startsWith("\"")){
			this.rel_addr = "\"" + rel_addr + "\"";
		}
		else {
			this.rel_addr = rel_addr;
		}
	}
	
	public void setrel_port(String rel_port){
		if (!rel_port.startsWith("\"")){
			this.rel_port = "\"" + rel_port + "\"";
		}
		else {
			this.rel_port = rel_port;
		}
	}
	
	public void settype(String type){
		if (!type.startsWith("\"")){
			this.type = "\"" + type + "\"";
		}
		else {
			this.type = type;
		}
	}
	
	public String getcomponent() {
		return this.component;
	}

	public String getfoundation(){
		return this.foundation;
	}
	
	public String getgeneration(){
		return this.generation;
	}
	
	public String getid(){
		return this.id;
	}
	
	public String getip(){
		return this.ip;
	}
	
	public String getnetwork(){
		return this.network;
	}
	
	public String getport(){
		return this.port;
	}
	
	public String getpriority(){
		return this.priority;
	}
	
	public String getprotocol(){
		return this.protocol;
	}
	
	public String getrel_addr(){
		return this.rel_addr;
	}
	
	public String getrel_port(){
		return this.rel_port;
	}
	
	public String gettype(){
		return this.type;
	}
	
	public String toXML(){
		StringBuilder buf = new StringBuilder();
		buf.append("<candidate");
		if(this.component != null) {
			buf.append(" component=").append(this.component);
		}
		if (this.foundation != null){
			buf.append(" foundation=").append(this.foundation);
		}
		if (this.generation != null){
			buf.append(" generation=").append(this.generation);
		}
		if (this.id != null){
			buf.append(" id=").append(this.id);
		}
		if (this.ip != null){
			buf.append(" ip=").append(this.ip);
		}
		if (this.network != null){
			buf.append(" network=").append(this.network);
		}
		if (this.port != null){
			buf.append(" port=").append(this.port);
		}
		if (this.priority != null){
			buf.append(" priority=").append(this.priority);
		}
		if (this.protocol != null){
			buf.append(" protocol=").append(this.protocol);
		}
		if (this.rel_addr != null){
			buf.append(" rel-addr=").append(this.rel_addr);
		}
		if (this.rel_port != null){
			buf.append(" rel-port=").append(this.rel_port);
		}
		if (this.type != null){
			buf.append(" type=").append(this.type);
		}
		buf.append("/>");
		return buf.toString();
	}
}
