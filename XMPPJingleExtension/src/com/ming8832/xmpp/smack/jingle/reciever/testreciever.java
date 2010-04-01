package com.ming8832.xmpp.smack.jingle.reciever;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;

import com.ming8832.xmpp.smack.jingle.common.CallStateMachine;
import com.ming8832.xmpp.smack.jingle.common.JingleIQPacket;
import com.ming8832.xmpp.smack.jingle.common.JingleIQProcess;
import com.ming8832.xmpp.smack.jingle.common.JingleIQProvider;

public class testreciever {
	
	private static String SID;
	private static String INITIATOR;
	private static String RESPONDER;

	public static void main(String[] args) {		
		try {
			XMPPConnection.DEBUG_ENABLED = true;
			
			XMPPConnection connection = new XMPPConnection("128.84.69.239");//192.168.1.101
			final CallStateMachine callstate = new CallStateMachine();
			connection.connect();
			
			JingleIQProcess.setConnection(connection);
			
			JingleIQProvider jingleiqprovider = new JingleIQProvider();
			ProviderManager.getInstance().addIQProvider("jingle", "urn:xmpp:jingle:1", jingleiqprovider);
			
			
			connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet p){
					if (p instanceof JingleIQPacket){
						JingleIQPacket jiq = (JingleIQPacket)p;
						JingleIQProcess IQprocess = new JingleIQProcess(jiq);
						System.out.println(callstate.getstate());
						if (callstate.getstate().equals("No_call")){
							try {
								callstate.setinitiated();
								IQprocess.INCOMINGCALL();
								callstate.setringing();
								
								System.out.println(jiq.getFrom() + " wants to start an audio coversation with you.");
								System.out.println("If you want to accept this inviation, please type: ACCEPT");
																
								BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
								while (!(br.readLine()).equals("ACCEPT"))
								{
									System.out.println("Unfortunately, at the current stage of development, you can ACCEPT the call!");
								}
								IQprocess.ACCEPTING();
								testreciever.setsessioninfo(jiq.getsid(), jiq.getFrom(), jiq.getTo());
								callstate.setaccepted();
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if (callstate.getstate().equals("call_ringing")){
							IQprocess.CALLACCEPTED();
							testreciever.setsessioninfo(jiq.getsid(), jiq.getinitiator(), jiq.getresponder());
							callstate.setoncall();
							System.out.println("When finished, please type: DONE to hang up!");
							try{
								BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
								while (!(br.readLine()).equals("DONE"))
								{
									System.out.println("Please type DONE to hang up");
								}
								JingleIQPacket dummy = new JingleIQPacket(jiq.getFrom(),jiq.getTo(),"session-terminate");
								dummy.setsid(testreciever.getsid());
								dummy.setinitiator(testreciever.getinitiator());
								dummy.setresponder(testreciever.getresponder());
								JingleIQProcess termination = new JingleIQProcess(dummy);
								termination.CALLTERMINATING();
								callstate.setterminated();
							}catch (IOException e) {
								e.printStackTrace();
							}	
						}
						else if (callstate.getstate().equals("on_call")){
							IQprocess.ENDCALL();
							callstate.setdone();
						}
					}
					else if (p instanceof IQ){
						IQ iq = (IQ)p;
						if (iq.getType()==IQ.Type.RESULT){
							if (callstate.getstate().equals("call_initiated")){
								callstate.setringing();
								System.out.println("Ringing...");
							}
							if (callstate.getstate().equals("call_accepted")){
								callstate.setoncall();
								System.out.println("When finished, please type: DONE to hang up!");
								try{
									BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
									while (!(br.readLine()).equals("DONE"))
									{
										System.out.println("Please type DONE to hang up");
									}
									JingleIQPacket dummy = new JingleIQPacket(iq.getFrom(),iq.getTo(),"session-terminate");
									dummy.setsid(testreciever.getsid());
									dummy.setinitiator(testreciever.getinitiator());
									dummy.setresponder(testreciever.getresponder());
									JingleIQProcess termination = new JingleIQProcess(dummy);
									termination.CALLTERMINATING();
								}catch (IOException e) {
									e.printStackTrace();
								}
							}
							if (callstate.getstate().equals("call_terminated")){
								callstate.setdone();
							}
						}
					}
				}
			}, new PacketTypeFilter(IQ.class));
			
			connection.login("tester2", "tester2");	
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setsessioninfo(String sid, String initiator, String responder){
		SID = sid;
		INITIATOR = initiator;
		RESPONDER = responder;
	}
	
	public static String getsid() {
		if (SID.startsWith("\""))
			return SID.split("\"")[1];
		else
			return SID;
	}
	
	public static String getinitiator() {
		if (INITIATOR.startsWith("\""))
			return INITIATOR.split("\"")[1];
		else
			return INITIATOR;
	}
	
	public static String getresponder() {
		if (RESPONDER.startsWith("\""))
			return RESPONDER.split("\"")[1];
		else
			return RESPONDER;
	}
	
}
