package com.ming8832.xmpp.smack.jingle.common;

public class Wait {
	
	@SuppressWarnings("static-access")
	public static void waitforsec(long s){
		try {
			Thread.currentThread().sleep(s*1000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}

}