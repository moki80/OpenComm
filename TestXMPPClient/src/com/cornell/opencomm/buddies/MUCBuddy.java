package com.cornell.opencomm.buddies;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.sipdroid.net.SipdroidSocket;

import android.util.Log;

import com.cornell.opencomm.jingleimpl.CallStateMachine;
import com.cornell.opencomm.jingleimpl.JingleIQPacket;
import com.cornell.opencomm.jingleimpl.JingleIQProcess;
import com.cornell.opencomm.rtpstreamer.AudioPusher;
import com.cornell.opencomm.rtpstreamer.ReceiverThread;
import com.cornell.opencomm.rtpstreamer.SenderThread;

public class MUCBuddy {

	private String jingleID = null;
	private String ipAddress = null;
	private int port;
	private CallStateMachine state = null;
	private JingleIQProcess jiqProcess = null;
	
	private String SID = null;
	private String initiator = null;
	
	private XMPPConnection connection = null;
	
	// Other RTP parameters go here.
	
	public MUCBuddy(String jid, XMPPConnection connection, String initiator){
		
		jingleID = jid;
		this.initiator = initiator;
		
		state = new CallStateMachine();		
		// This means idle, i.e. no call is in progress.
		state.setterminated();
		this.connection = connection;
		
		jiqProcess = new JingleIQProcess();
		jiqProcess.setConnection(connection);
	}
	
	public void processPacket(IQ incomingPacket){
		// Check what type of packet is incomingPacket and handle case according to
		// SessionHandler.
		if (incomingPacket instanceof JingleIQPacket) {
			// System.out.println("Recieved a jingle packet!");
			JingleIQPacket jiq = (JingleIQPacket) incomingPacket;
			if (state.getstate().equals("No_call")
					&& jiq.getaction().equals("session-initiate")) {

				state.setinitiated();
				Log.i("TestXMPPClient", "State = ");
				jiqProcess.sessionIncoming(jiq);
				state.setringing();
				

				// TODO: Later on, we want to create the Acceptance packet here itself.
				jiqProcess.sessionAccept(jiq);
				SID = jiq.getsid();
				state.setaccepted();

			} else if (state.getstate().equals("call_ringing")
					&& jiq.getaction().equals("session-accept")) {
				jiqProcess.sessionCallAccepted(jiq);
				SID = jiq.getsid();
				state.setoncall();
				
				ipAddress = jiq.getIPAddress();
				port = jiq.getPort();
				
				// TODO: CURTIS: Now, RTP sockets can be created.
				//we may need to create a few more ports here and keep track of them, but for now i'm just tossing
				//in the code needed to start up a sender and reciever
				int sample_rate = 8000;
		        int frame_size = 160;
		        int frame_rate = sample_rate/frame_size;
		        SipdroidSocket socket;
		        SipdroidSocket recv_socket;
		        try {
		        	socket = new SipdroidSocket(port);
		        	recv_socket = new SipdroidSocket(port+1);
		        	boolean do_sync = true;
		        	BlockingQueue<short[]> queue = new LinkedBlockingQueue<short[]>();
		        	//host loopback
		        	SenderThread sender = new SenderThread(do_sync, frame_rate, frame_size, socket, "10.0.2.2", 33333, queue);
		        	ReceiverThread receiver = new ReceiverThread(recv_socket);
		        	AudioPusher pusher = new AudioPusher("/test3.wav", queue);
		        	sender.start();
		        	pusher.start();
		        	receiver.start();
		        } catch (SocketException e) {
		        	e.printStackTrace();
		        } catch (UnknownHostException e) {
		        	e.printStackTrace();
		        }
				
			} else if (state.getstate().equals("call_ringing")
					&& jiq.getaction().equals("session-terminate")) {
				state.setdeclined();
				jiqProcess.sessionEndcall(jiq);
				state.setdone();
			} else if (state.getstate().equals("call_ongoing")
					&& jiq.getaction().equals("session-terminate")) {
				System.out
						.println("Recieved session-terminate packet!");
				jiqProcess.sessionEndcall(jiq);
				state.setdone();
			} else {
				System.out.println("Unhandle case (JINGLEIQ): "
						+ state.getstate());
			}
		} else if (incomingPacket instanceof IQ) {
			// System.out.println("Received an IQ packet!");
			IQ iq = (IQ) incomingPacket;
			if (iq.getType() == IQ.Type.RESULT) {
				if (state.getstate().equals("call_initiated")
						&& iq.getFrom().equals(getresponder())) {
					state.setringing();
					System.out.println("Ringing...");
				} else if (state.getstate().equals(
						"call_accepted")
						&& iq.getFrom().equals(getinitiator())) {
					state.setoncall();
					
				} else if (state.getstate().equals(
						"call_terminated")
						&& iq.getFrom().equals(getresponder())) {
					state.setdone();
				} else if (state.getstate().equals(
						"call_declined")
						&& iq.getFrom().equals(getinitiator())) {
					state.setdone();
				} else {
					System.out.println("Unhandle case (IQ): "
							+ state.getstate());
				}
			}
		}
	}
	
	private String getinitiator() {
		if (initiator.startsWith("\""))
			return initiator.split("\"")[1];
		else
			return initiator;
	}

	private String getresponder() {
		if (jingleID.startsWith("\""))
			return jingleID.split("\"")[1];
		else
			return jingleID;
	}
	
	public JingleIQProcess getJingleIQProcess(){
		return jiqProcess;
	}
	
	public CallStateMachine getSessionState(){
		return state;
	}
}
