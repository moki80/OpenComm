package com.cornell.opencomm.networking;

import java.net.BindException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

import android.util.Log;

import com.jstun.demo.DiscoveryTest;

/**
 * This class will encapsulate all the methods needed to deal with networking issues
 * like acting as a STUN Client and getting the public NAT IP Address, as well
 * as determining all IPs corresponding to local interfaces, etc. For now, this class
 * does not take into account the type of NAT behind which the client sits. 
 *
 * @author Abhishek
 *
 */
public class IPAddresses {

	private static Vector<String> localInterfaceIPs = new Vector<String>();
	private static Vector<String> natIPs = new Vector<String>();
	
	/**
	 * Constructor
	 */
	public IPAddresses(){
		localInterfaceIPs = new Vector<String>();
		natIPs = new Vector<String>();
	}
	
	/**
	 * Discovers the IPs of this client returned by the local interfaces and initializes local structures to store this information
	 */
	public static void discoverLocalInterfaceIPs(){
		try {
    		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    			NetworkInterface intf = en.nextElement();
    			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
    				InetAddress inetAddress = enumIpAddr.nextElement();

    				if (!inetAddress.isLoopbackAddress()) { 
    					try {
    						localInterfaceIPs.add(inetAddress.getHostAddress());
    					} catch (Exception e) {
    						System.out.println(e.getMessage());
    						e.printStackTrace();
    					} 
    				}				
    			}
    		}
    	} catch (SocketException ex) {
    		// do nothing
    	}
	}
	
	/**
	 * Discovers the public IPs exposed by a NAT, and stores the IPs into local structrues
	 */
	public static void discoverNATedIPs(){
		try {
    		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    			NetworkInterface intf = en.nextElement();
    			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
    				InetAddress inetAddress = enumIpAddr.nextElement();

    				if (!inetAddress.isLoopbackAddress()) { 
    					try {
    						//no matter what an external server is involved to get public IP, use jstun's server
    						String StunServer = "jstun.javawi.de";
    						int StunServerPort = 3478;

    						DiscoveryTest StunDiscover = new DiscoveryTest(inetAddress, StunServer, StunServerPort);

    						// call out to stun server 
    						StunDiscover.test();
    						Log.i("XMPPClient", "Public ip is:" + StunDiscover.di.getPublicIP().getHostAddress());
    						String publicIP = StunDiscover.di.getPublicIP().getHostAddress();
    						if(publicIP != null || !publicIP.equals("")){
    							natIPs.add(publicIP);
    						}
    					} catch (BindException be) {
    						System.out.println(inetAddress.toString() + ": " + be.getMessage());
    					} catch (Exception e) {
    						System.out.println(e.getMessage());
    						e.printStackTrace();
    					} 
    				}				
    			}
    		}
    	} catch (SocketException ex) {
    		// do nothing
    	}
	}

	/**
	 * Returns the list of all local interface IPs
	 * @return list of all local interface IPs
	 */
	public static Vector<String> getLocalInterfaceIPs(){
		return localInterfaceIPs;
	}
	
	/**
	 * Returns the list of all public NATed IPs
	 * @return list of all public NATed IPs
	 */
	public static Vector<String> getNATedIPs(){
		return natIPs;
	}
	
}
