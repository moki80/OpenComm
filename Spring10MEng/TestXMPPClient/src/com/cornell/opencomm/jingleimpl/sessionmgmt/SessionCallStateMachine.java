package com.cornell.opencomm.jingleimpl.sessionmgmt;

import android.util.Log;

import com.cornell.opencomm.client.XMPPClientLogger;
import com.cornell.opencomm.jingleimpl.JingleIQPacket;

/**
 * This class defines all the states that a Jingle session can be in at
 * any point in time.
 * 
 * @author Abhishek
 * 
 */
public class SessionCallStateMachine {

	/**
	 * Denotes a Session that has not yet started.
	 */
	public static final int STATE_ENDED = 0;
	/**
	 * Denotes a Session that is waiting for a definitive response from the other entity
	 */
	public static final int STATE_PENDING = 1;
	/**
	 * Denotes a Session is now established for data transfer
	 */
	public static final int STATE_ACTIVE = 2;
	
	// String states for easier logging
	public static final String STRING_STATE_ENDED = "STATE_ENDED";
	public static final String STRING_STATE_PENDING = "STATE_PENDING";
	public static final String STRING_STATE_ACTIVE = "STATE_ACTIVE";
	public static final String STRING_STATE_UNDEFINED = "STATE_UNDEFINED";
	
	private int sessionState = STATE_ENDED;
	
	/**
	 * Constructor
	 */
	public SessionCallStateMachine(){
		
	}
	
	/**
	 * Returns the current state of this session
	 * @return the state of this session
	 */
	public int getSessionState(){
		return sessionState;
	}
	
	/**
	 * Changes the <code>sessionState</code> based on the <code>attributeAction</code> of
	 * the received OR outgoing <code>JingleIQPacket</code> . Returns success/failure of the state change
	 * based on whether it is legal to receive the particular attributeAction
	 * for a particular sessionState.
	 * 
	 * @param action The <code>attributeAction</code> of the received or outgoing <code>JingleIQPacket</code>
	 * @return true on success, false on failure
	 */
	public boolean changeSessionState(String action){
		boolean success = true;
		
		switch(sessionState){
		case STATE_ENDED:
			if(action.equals(JingleIQPacket.AttributeActionValues.SESSION_INITIATE)){
				sessionState = STATE_PENDING;
				success = true;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			} else {
				success = false;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			}
			break;
		case STATE_PENDING:
			if(action.equals(JingleIQPacket.AttributeActionValues.SESSION_ACCEPT)){
				sessionState = STATE_ACTIVE;
				success = true;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			} else if(action.equals(JingleIQPacket.AttributeActionValues.SESSION_TERMINATE)){
				sessionState = STATE_ENDED;
				success = true;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			} else if(action.equals(JingleIQPacket.AttributeActionValues.CONTENT_ACCEPT) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_ADD) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_MODIFY) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_REJECT) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_REMOVE) ||
					action.equals(JingleIQPacket.AttributeActionValues.DESCRIPTION_INFO) ||
					action.equals(JingleIQPacket.AttributeActionValues.SESSION_INFO) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_ACCEPT) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_INFO) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_REJECT) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_REPLACE)){
				// No change in sessionState
				success = true;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			} else {
				success = false;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			}
			break;
		case STATE_ACTIVE:
			if(action.equals(JingleIQPacket.AttributeActionValues.SESSION_TERMINATE)){
				sessionState = STATE_ENDED;
				success = true;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			} else if(action.equals(JingleIQPacket.AttributeActionValues.CONTENT_ACCEPT) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_ADD) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_MODIFY) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_REJECT) ||
					action.equals(JingleIQPacket.AttributeActionValues.CONTENT_REMOVE) ||
					action.equals(JingleIQPacket.AttributeActionValues.DESCRIPTION_INFO) ||
					action.equals(JingleIQPacket.AttributeActionValues.SESSION_INFO) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_ACCEPT) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_INFO) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_REJECT) ||
					action.equals(JingleIQPacket.AttributeActionValues.TRANSPORT_REPLACE)){
				// No change in sessionState
				success = true;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			} else {
				success = false;
				Log.i(XMPPClientLogger.TAG, 
						"changeSessionState succeeded: " + new Boolean(success).toString() + 
						"sessiontState: " + getSessionStateString());
			}
			break;		
		}
		return success;
	}
	
	/**
	 * Sets the sessionState to a desired value. This method is called when setting the state
	 * based on a <code>RESULT</code> ack <code>IQ</code> packet as there is no Action attribute in such an IQ Packet
	 * 
	 * @param state the new state
	 */
	public void setSessionState(int state){
		sessionState = state;
	}
	
	/**
	 * Returns the String form of the current sessionState to facilitate Logging
	 * 
	 * @return String representation of <code>sessionState</code>
	 */
	public String getSessionStateString() {
		switch(sessionState){
		case STATE_ACTIVE:
			return STRING_STATE_ACTIVE;
		case STATE_ENDED:
			return STRING_STATE_ENDED;
		case STATE_PENDING:
			return STRING_STATE_PENDING;
		default:
			return STRING_STATE_UNDEFINED;
		}
	}
}
