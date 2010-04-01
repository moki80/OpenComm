package com.ming8832.xmpp.smack.jingle.common;

public class CallStateMachine {
	private String call_state;
	
	public CallStateMachine(){
		call_state = "No_call";
	}
	
	public String getstate(){
		return call_state;
	}
	
	public void setinitiated(){
		call_state = "call_initiated";
		System.out.println("Call is initaited!");
	}
	
	public void setringing(){
		call_state = "call_ringing";
		System.out.println("Call is ringing and waiting for accept!");
	}
	
	public void setaccepted(){
		call_state = "call_accepted";
		System.out.println("Call is accepted and wating for connection establishment!");
	}
	
	public void setoncall(){
		call_state = "call_ongoing";
		System.out.println("Have a nice conversation!");
	}
	
	public void setterminated(){
		call_state = "call_terminated";
		System.out.println("I am hanging up!");
	}
	
	public void setdone(){
		call_state = "No_call";
		System.out.println("Bye!");
	}
}
