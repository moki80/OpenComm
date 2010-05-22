package com.cornell.opencomm.jingleDesktop;

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