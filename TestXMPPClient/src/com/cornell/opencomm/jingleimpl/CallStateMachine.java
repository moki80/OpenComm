package com.cornell.opencomm.jingleimpl;

import android.util.Log;

public class CallStateMachine {
	private String call_state;
	
	public CallStateMachine(){
		Log.i("TestXMPPClient", "State = NO CALL");
		call_state = "No_call";
	}
	
	public String getstate(){
		return call_state;
	}
	
	public void setinitiated(){
		Log.i("TestXMPPClient", "State = CALL INITIATED");
		call_state = "call_initiated";
	}
	
	public void setringing(){
		Log.i("TestXMPPClient", "State = CALL RINGING");
		call_state = "call_ringing";		
	}
	
	public void setaccepted(){
		Log.i("TestXMPPClient", "State = CALL ACCEPTED");
		call_state = "call_accepted";
		// System.out.println("Call is accepted and wating for connection establishment!");
	}
	
	public void setoncall(){
		Log.i("TestXMPPClient", "State = CALL ONGOING");
		call_state = "call_ongoing";
		// System.out.println("Have a nice conversation!");
	}
	
	public void setterminated(){
		Log.i("TestXMPPClient", "State = CALL TERMINATED");
		call_state = "call_terminated";
		// System.out.println("I am hanging up!");
	}
	
	public void setdone(){
		Log.i("TestXMPPClient", "State = CALL DONE");
		call_state = "No_call";
		// System.out.println("Bye!");
	}
	
	public void setdeclined(){
		Log.i("TestXMPPClient", "State = CALL DECLINED");
		call_state = "call_declined";
		// System.out.println("Invitation is declined");
	}
}
