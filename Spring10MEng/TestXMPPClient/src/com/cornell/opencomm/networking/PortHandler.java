package com.cornell.opencomm.networking;

/**
 * Generates new ports to use for RTP streams
 * @author Curtis
 */
public class PortHandler {
	
	private static PortHandler portHandler = null;
	private static int recvPortNo;
	private static int sendPortNo;
	
	private PortHandler() {
		recvPortNo = 7000;
		sendPortNo = 8000;
	}
	
	/**
	 * Gets the running instance of the PortHandler, creating one if it doesn't exist yet.
	 * @return the current instance of PortHandler
	 */
	public static PortHandler getInstance() {
		if (portHandler == null)
			portHandler = new PortHandler();
		return portHandler;
	}
	
	/**
	 * @return the next port to use for an RTP ReceiverThread
	 */
	public int getRecvPort() {
		int toReturn = recvPortNo++;
		return toReturn;
	}
	
	/**
	 * @return the next port to use for an RTP SenderThread
	 */
	public int getSendPort() {
		int toReturn = sendPortNo++;
		return toReturn;
	}
	

}
