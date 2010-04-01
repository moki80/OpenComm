package com.ming8832.xmpp.smack.jingle.common;
//Author: Ming Lin Date: Feb 23 Version 1.0
//This is class implements the payload-type
//The payload-type for jingle can be found in XMPP documentation XEP-0167
//http://xmpp.org/extensions/xep-0167.html


public class PayLoadType {
	
	private String channels;
	private String clockrate;
	private String id;
	private String maxptime;
	private String name;
	private String ptime;
	
	//Constructor of PayLaodType, only id and name is required
	public PayLoadType(){
		this.id = null;
		this.name = null;
		
		//Default to the optional attribute
		//will not be include in XML if not changed
		this.channels = null;
		this.clockrate = null;
		this.maxptime = null;
		this.ptime = null;
	}
	

	public void setchannel(String channels) {
		if (!channels.startsWith("\"")) {
			this.channels = "\"" + channels + "\"";
		}
		else {
			this.channels = channels;
		}
	}
	
	public void setclockrate (String clockrate){
		if (!clockrate.startsWith("\"")) {
			this.clockrate = "\"" + clockrate + "\"";
		}
		else {
			this.clockrate = clockrate;
		}
	}

	public void setid (String id)  {
		if (!id.startsWith("\"")) {
			this.id = "\"" + id + "\"";
		}
		else {
			this.id = id;
		}
	}
	
	public void setmaxptime (String maxptime){
		if (!maxptime.startsWith("\"")) {
			this.maxptime = "\"" + maxptime + "\"";
		}
		else {
			this.maxptime = maxptime;
		}
	}
	
	public void setname (String name) {
		if (!name.startsWith("\"")) {
			this.name = "\"" + name + "\"";
		}
		else {
			this.name = name;
		}
	}
	
	public void setptime (String ptime){
		if (!ptime.startsWith("\"")) {
			this.ptime = "\"" + ptime + "\"";
		}
		else {
			this.ptime = ptime;
		}
	}
	
	public String getchannel() {
		return this.channels;
	}
	
	public String getclockrate() {
		return this.clockrate;
	}
	
	public String getid() {
		return this.id;
	}
	
	public String getmaxptime() {
		return this.maxptime;
	}
	
	public String getname() {
		return this.name;
	}
	
	public String getptime() {
		return this.ptime;
	}
	
	public String toXML(){
		StringBuilder buf = new StringBuilder();
		buf.append("<payload-type id=" ).append(this.id)
		   .append(" name=").append(this.name);
		
		if (this.clockrate != null){
			buf.append(" clockrate=").append(this.clockrate);
		}
		if (this.channels != null){
			buf.append(" channels=").append(this.channels);
		}
		if (this.ptime != null){
			buf.append(" ptime=").append(this.ptime);
		}
		if(this.maxptime != null){
			buf.append(" maxptime").append(this.maxptime);
		}
		buf.append("/>");
		return buf.toString();
	}
}

