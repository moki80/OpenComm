package com.cornell.opencomm.jingleDesktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;

public class client {
	private static String SID;
	private static String INITIATOR;
	private static String RESPONDER;
	
	public static void main(String[] args){
		try{
			
			//set debugging enabled
			XMPPConnection.DEBUG_ENABLED = true;
			
			//Setup connection
			XMPPConnection connection = new XMPPConnection("128.84.69.237");//"128.84.221.90"
			final CallStateMachine callstate = new CallStateMachine();
			connection.connect();
			
			JingleIQProcess.setConnection(connection);
			
			JingleIQProvider jingleiqprovider = new JingleIQProvider();
			ProviderManager.getInstance().addIQProvider("jingle", "urn:xmpp:jingle:1", jingleiqprovider);
			
			
			//add packet listener for JingleIQPacket
			connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet p){
					if (p instanceof JingleIQPacket){
						//System.out.println("Received a jingle packet!");
						JingleIQPacket jiq = (JingleIQPacket)p;
						if (callstate.getstate().equals("No_call") && jiq.getAttributeAction().equals("session-initiate")){
							try {
								System.out.println(jiq.toXML());
								callstate.setinitiated();
								JingleIQProcess.sessionIncoming(jiq);
								callstate.setringing();
								
								System.out.println(jiq.getFrom() + " wants to start an audio coversation with you.");
								System.out.println("If you want to accept this inviation, please type: ACCEPT");
								System.out.println("If you want to decline this inviation, please type: DECLINE");
																
								BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
								String input = br.readLine();
								if (input.equals("ACCEPT"))
								{
									JingleIQProcess.sessionAccept(jiq);
									client.setsessioninfo(jiq.getAttributeSID(), jiq.getFrom(), jiq.getTo());
									callstate.setaccepted();
								}
								else if (input.equals("DECLINE"))
								{
									callstate.setdeclined();
									client.setsessioninfo(jiq.getAttributeSID(),jiq.getFrom(), jiq.getTo());
									JingleIQPacket dummy = new JingleIQPacket(jiq.getFrom(),jiq.getTo(),"session-terminate");
									dummy.setAttributeSID(client.getsid());
									dummy.setAttributeInitiator(client.getinitiator());
									dummy.setAtributeResponder(client.getresponder());
									JingleIQProcess.sessionDecline(dummy);
								}	
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if (callstate.getstate().equals("call_ringing") && jiq.getAttributeAction().equals("session-accept")){
							JingleIQProcess.sessionCallAccepted(jiq);
							client.setsessioninfo(jiq.getAttributeSID(), jiq.getAttributeInitiator(), jiq.getAttributeResponder());
							callstate.setoncall();
							System.out.println("When finished, please type: DONE to hang up!");
							try{
								BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
								while (!(br.readLine()).equals("DONE"))
								{
									System.out.println("Please type DONE to hang up");
								}
								callstate.setterminated();
								JingleIQPacket dummy = new JingleIQPacket(jiq.getFrom(),jiq.getTo(),"session-terminate");
								dummy.setAttributeSID(client.getsid());
								dummy.setAttributeInitiator(client.getinitiator());
								dummy.setAtributeResponder(client.getresponder());
								JingleIQProcess.sessionTerminate(jiq);
							}catch (IOException e) {
								e.printStackTrace();
							}	
						}
						else if (callstate.getstate().equals("call_ringing") && jiq.getAttributeAction().equals("session-terminate")){
							callstate.setdeclined();
							JingleIQProcess.sessionEndcall(jiq);
							callstate.setdone();
						}
						else if (callstate.getstate().equals("call_ongoing") && jiq.getAttributeAction().equals("session-terminate")){
							System.out.println("Recieved session-terminate packet!");
							JingleIQProcess.sessionEndcall(jiq);
							callstate.setdone();
						}
						else {
							System.out.println("Unhandle case (JINGLEIQ): " + callstate.getstate());
							System.out.println(jiq.toXML());
						}
					}
					else if (p instanceof IQ){
						//System.out.println("Received an IQ packet!");
						IQ iq = (IQ)p;
						if (iq.getType()==IQ.Type.RESULT){
							if (callstate.getstate().equals("call_initiated") && iq.getFrom().equals(getresponder())){
								callstate.setringing();
								System.out.println("Ringing...");
							}
							else if (callstate.getstate().equals("call_accepted") && iq.getFrom().equals(getinitiator())){
								callstate.setoncall();
								System.out.println("When finished, please type: DONE to hang up!");
								try{
									BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
									while (!(br.readLine()).equals("DONE"))
									{
										System.out.println("Please type DONE to hang up");
									}
									callstate.setterminated();
									JingleIQPacket dummy = new JingleIQPacket(iq.getFrom(),iq.getTo(),"session-terminate");
									dummy.setAttributeSID(client.getsid());
									dummy.setAttributeInitiator(client.getinitiator());
									dummy.setAtributeResponder(client.getresponder());
									JingleIQProcess.sessionTerminate(dummy);
								}catch (IOException e) {
									e.printStackTrace();
								}
							}
							else if (callstate.getstate().equals("call_terminated") && iq.getFrom().equals(getresponder())){
								callstate.setdone();
							}
							else if (callstate.getstate().equals("call_declined") && iq.getFrom().equals(getinitiator())){
								callstate.setdone();
							}
							else {
								System.out.println("Unhandle case (IQ): " + callstate.getstate());
							}
						}
					}
				}
			}, new PacketTypeFilter(IQ.class));
			
			System.out.println("Jingle Negociation Demo");
			System.out.println("Username:");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String name = br.readLine();
			
			//connection.login(name,pwd); 
			connection.login(name,name);
			Presence presence = new  Presence(Presence.Type.available);
			connection.sendPacket(presence);
			
			Wait.waitforsec(2);
			
			// This session prints out the buddy list!
			Roster roster =connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			System.out.println("You have " + entries.size() + " contacts!");
			for (RosterEntry r:entries)
			{
				System.out.println(r.getName() + "(" + r.getUser() +") is: " );
				Presence p = roster.getPresence(r.getUser());
				System.out.println("Presence: " + p);
				System.out.println("Presence type: " + p.getType());
				System.out.println("Presence mode: " + p.getMode());
			}		

			
			System.out.println("Waiting for calls.....");

			//To test send invitation, uncomment the following code!
			/*
			System.out.println("To start audio conversation, type INVITE:jid");
			String jidto = br.readLine();
			while (!jidto.startsWith("INVITE")){
				System.out.println("To start audio conversation, type INVITE:jid");
				jidto = br.readLine();
			}
			jidto = jidto.split(":")[1];
			String jidfrom = name + "@ming8832";
			
			JingleIQProcess.sessionInitiate(jidfrom, jidto);
			client.setsessioninfo(null, jidfrom, jidto);
			callstate.setinitiated();
			*/

		
		}catch (Exception e) {
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
