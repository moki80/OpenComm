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

	private String buddyJID = null;
	private String ipAddress = null;
	private int port;
	private CallStateMachine state = null;
	private JingleIQProcess jiqProcess = null;
	
	//To do: session ID generation and recording
	//private String SID = null;
	
	private String initiator = null;
	private String responder = null;
	
	private XMPPConnection connection = null;
	
	// Other RTP parameters go here.
	
	
	//Ming's Note: after reviewing, the third field is really not used
	//             please get rip of it
	public MUCBuddy(String buddyJID, XMPPConnection connection, String initiator){
		
		//MUC Buddy's JingleID
		this.buddyJID = buddyJID;
		
		
		state = new CallStateMachine();		
		// This means idle, i.e. no call is in progress.
		state.setdone();
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
			Log.i("TestXMPPClient", "JIQ Received in MUCBuddy " + "From: "  + jiq.getFrom()+ "To: " + jiq.getTo() + 
					 "Initiator: " + jiq.getinitiator() + "Responder: " + jiq.getresponder() + "Action: " + jiq.getaction());
			if (state.getstate().equals("No_call")
					&& jiq.getaction().equals("session-initiate")) {

				state.setinitiated();				
				jiqProcess.sessionIncoming(jiq);
				state.setringing();				
				
				//Added by Ming Lin
				//While receiving a "session-initiation" packet
				//the session Initiator is the FROM field of the received packet
				//the session Responder is the TO   field of the received packet
				setInitiatorResponder(jiq.getFrom(),jiq.getTo());
				
				jiqProcess.sessionAccept(jiq, this.initiator, this.responder);
				
				state.setaccepted();

			} else if (state.getstate().equals("call_ringing")
					&& jiq.getaction().equals("session-accept")) {
				jiqProcess.sessionCallAccepted(jiq);
				
				//Added by Ming Lin
				//While receiving a "session-accept" packet
				//the Initiator & Responder should already been indicated
				//thus we copy them from the received packet
				setInitiatorResponder(jiq.getinitiator(),jiq.getresponder());
				
				state.setoncall();
				
				ipAddress = jiq.getIPAddress();
				port = jiq.getPort();
				
				// TODO: CURTIS: Now, RTP sockets can be created.	
				//we may need to create a few more ports here and keep track of them, but for now i'm just tossing
				//in the code needed to start up a sender and reciever
				Log.i("MUCBuddy", "trying to create sockets");
				int sample_rate = 8000;
		        int frame_size = 160;
		        int frame_rate = sample_rate/frame_size;
		        SipdroidSocket socket;
		        SipdroidSocket recv_socket;
		        try {
		        	socket = new SipdroidSocket(6004);
		        	recv_socket = new SipdroidSocket(6006);
		        	Log.i("MUCBuddy", "sockets created, starting threads");
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
			Log.i("TestXMPPClient", "IQ Received in MUCBuddy " + "From: "  + iq.getFrom()+ 
					"To: " + iq.getTo() + "Type: " + iq.getType());
			if (iq.getType() == IQ.Type.RESULT) {
				if (state.getstate().equals("call_initiated")) {
					state.setringing();
					System.out.println("Ringing...");
				} else if (state.getstate().equals(
						"call_accepted")) {
					state.setoncall();
					Log.i("MUCBuddy", "trying to create sockets");
					int sample_rate = 8000;
			        int frame_size = 160;
			        int frame_rate = sample_rate/frame_size;
			        SipdroidSocket socket;
			        SipdroidSocket recv_socket;
			        try {
			        	socket = new SipdroidSocket(6004);
			        	recv_socket = new SipdroidSocket(6006);
			        	Log.i("MUCBuddy", "sockets created, starting threads");
			        	boolean do_sync = true;
			        	BlockingQueue<short[]> queue = new LinkedBlockingQueue<short[]>();
			        	//host loopback
			        	SenderThread sender = new SenderThread(do_sync, frame_rate, frame_size, socket, "10.0.2.2", 33334, queue);
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
				} else if (state.getstate().equals("call_terminated")) {
					state.setdone();
				} else if (state.getstate().equals("call_declined")) {
					state.setdone();
				} else {
					System.out.println("Unhandle case (IQ): "
							+ state.getstate());
				}
			}
		}
	}
	
	private void setInitiatorResponder(String initiator, String responder){
		this.initiator = initiator;
		this.responder = responder;	
	}
	
	private String getbuddyJID(){
		return this.buddyJID;
	}
	
	public JingleIQProcess getJingleIQProcess(){
		return jiqProcess;
	}
	
	public CallStateMachine getSessionState(){
		return state;
	}
}
