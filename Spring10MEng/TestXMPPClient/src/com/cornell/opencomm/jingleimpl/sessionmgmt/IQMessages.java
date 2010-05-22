package com.cornell.opencomm.jingleimpl.sessionmgmt;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.XMPPError;

import com.cornell.opencomm.jingleimpl.JingleIQPacket;

/**
 * This class encapsulates the creation and sending of <code>RESULT</code> and <code>ERROR</code> {@link IQ} packets
 * 
 * @author Abhishek
 * @author Ming
 */
public class IQMessages {

	private XMPPConnection connection;
	
	/**
	 * Constructor, Does nothing
	 */
	public IQMessages(){
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
	 * Sends a <code>RESULT</code> type <code>IQ</code> packet
	 * 
	 * @param incomingIQ the <code>JingleIQPacket</code> in response to which this <code>IQ</code>
	 * 		  is being sent
	 */
	public void sendResultAck(JingleIQPacket incomingIQ){
		
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
	
	/**
	 * Sends a <code>ERROR</code> type <code>IQ</code> packet
	 * 
	 * @param incomingIQ the <code>JingleIQPacket</code> in response to which this <code>IQ</code>
	 * 		  is being sent
	 */
	public void sendErrorAck(JingleIQPacket incomingIQ, final XMPPError.Condition condition){
		
		IQ errorIQ = new IQ(){
			@Override
			public String getChildElementXML(){
				XMPPError error = new XMPPError(condition);
				return error.toXML();
			}
		};
		
		errorIQ.setType(IQ.Type.ERROR);
		errorIQ.setTo(incomingIQ.getFrom());
		errorIQ.setFrom(incomingIQ.getTo());
		
		connection.sendPacket(errorIQ);
	}
}
