/*
 * Copyright (C) 2008 Hughes Systique Corporation, USA (http://www.hsc.com)
 * 
 * This file is part of Sipdroid, sample SIP UI on Android
 * 
 * Sipdroid is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Sipdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Sipdroid; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Nitin Khanna, for Hughes Systique Corporation
 */

package org.hsc.sip.ua.core.core;

import org.hsc.sip.ua.Constants;
import org.hsc.sip.ua.R;
import org.hsc.sip.ua.config.data.UAConfig;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

public class SipdroidEngine implements UserAgentListener, RegisterAgentListener {

	public static final int UNINITIALIZED = 0x0;
	public static final int INITIALIZED = 0x2;

	private UAConfig m_UAConfig;
	private int m_CurrentState = UNINITIALIZED;
	private SipdroidEngineListener m_EngineListener = null;

	/** User Agent */
	private UserAgent ua;

	/** Register Agent */
	private RegisterAgent ra;

	/** UserAgentProfile */
	private UserAgentProfile user_profile;

	private NameAddress contact;

	private AlertDialog m_RegAlertDialog = null;

	public SipdroidEngine(SipdroidEngineListener a_EngineListener) {
		m_UAConfig = new UAConfig();
		m_EngineListener = a_EngineListener;
	}

	public int GetState() {
		return m_CurrentState;
	}

	public boolean StartEngine() {
		try {

			m_UAConfig.VerifyConfig();

			String opt_via_addr = m_UAConfig.getUserProfileData().getLocalIP();
			/*
			 * try {
			 * 
			 * 
			 * 
			 * Enumeration<NetworkInterface>
			 * netInterfaces=NetworkInterface.getNetworkInterfaces();
			 * while(netInterfaces.hasMoreElements()) { NetworkInterface
			 * ni=(NetworkInterface)netInterfaces.nextElement();
			 * 
			 * InetAddress ip=(InetAddress) ni.getInetAddresses().nextElement();
			 * if( ip != null && !ip.isSiteLocalAddress() &&
			 * !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1)
			 * { opt_via_addr = ip.getHostAddress(); break; }else { ip=null; } }
			 * } catch (Exception E) {
			 * 
			 * }
			 */

			SipStack.init(null);
			SipStack.debug_level = 1;
			SipStack.log_path = "//data//data//org.hsc.sip.ua//files";

			SipProvider sip_provider;
			sip_provider = new SipProvider(opt_via_addr, m_UAConfig
					.getUserProfileData().getLocalPort());

			UserAgentProfile user_profile = new UserAgentProfile(null);

			user_profile.do_register = m_UAConfig.getProxyRegistrationData()
					.isRegisterOnStart();
			user_profile.expires = m_UAConfig.getProxyRegistrationData()
					.getSuggestedRegistrationExpDur();

			user_profile.keepalive_time = m_UAConfig.getCallOptionsData()
					.getNATRefreshParams().getNATTimeOut();
			user_profile.no_offer = false;

			user_profile.from_url = m_UAConfig.getUserProfileData()
					.getUserName()
					+ "@"
					+ m_UAConfig.getProxyRegistrationData().getDomainOrRealm();

			user_profile.contact_url = "sip:"
					+ m_UAConfig.getUserProfileData().getUserName() + "@"
					+ opt_via_addr + ":"
					+ m_UAConfig.getUserProfileData().getLocalPort();
			
			/***********/
			user_profile.realm = m_UAConfig.getProxyRegistrationData()
					.getDomainOrRealm();
			user_profile.username = m_UAConfig.getUserProfileData()
					.getUserName()
					+ "@" + user_profile.realm;
			user_profile.passwd = m_UAConfig.getUserProfileData().getPassword();
			Log.v(Constants.LogConstants.TAG, "details:" + user_profile.from_url + " "
					+ user_profile.contact_url + " " + user_profile.realm);

			user_profile.route = m_UAConfig.getProxyRegistrationData()
					.getProxyURI();
			Log.v(Constants.LogConstants.TAG, "Route: " + user_profile.route);
			
			// Enable audio
			user_profile.audio=true;
			/***********/

			this.user_profile = user_profile;

			ua = new UserAgent(sip_provider, user_profile, this);
			ra = new RegisterAgent(sip_provider, user_profile.from_url,
					user_profile.contact_url, user_profile.username,
					user_profile.realm, user_profile.passwd,
					user_profile.route, this);

			m_CurrentState = INITIALIZED;
			Log.v(Constants.LogConstants.TAG, "Registrar init");
			if (m_UAConfig.getProxyRegistrationData().isRegisterOnStart()) {
				Log.v(Constants.LogConstants.TAG, "Registrar on start");

				if (m_UAConfig.getCallOptionsData().getNATRefreshParams()
						.isUseNATRefresh()) {
					Log.v(Constants.LogConstants.TAG, "NAT refresh");

					loopRegister(m_UAConfig.getProxyRegistrationData()
							.getSuggestedRegistrationExpDur(), m_UAConfig
							.getProxyRegistrationData()
							.getSuggestedRegistrationExpDur() - 10, m_UAConfig
							.getCallOptionsData().getNATRefreshParams()
							.getNATTimeOut());
				} else {
					Log.v(Constants.LogConstants.TAG,
							"Proxy data for registrar");

					register(m_UAConfig.getProxyRegistrationData()
							.getSuggestedRegistrationExpDur());
				}
			} else {
				// Go into call listening mode
				listen();
			}
		} catch (Exception e) {
			Log.v(Constants.LogConstants.TAG, "Exception occured\n", e);
			// pass a callback that initialization failed
		}
		Log.v(Constants.LogConstants.TAG, "All pass");
		return true;
	}

	public UAConfig getSipdroidConfig() {
		return m_UAConfig;
	}

	public String getMyNumber() {
		String opt_via_addr = m_UAConfig.getUserProfileData().getLocalIP();
		/*
		 * try { opt_via_addr =
		 * java.net.InetAddress.getLocalHost().getHostAddress(); } catch
		 * (Exception E) {
		 * 
		 * }
		 */
		return m_UAConfig.getUserProfileData().getUserName() + "@"
				+ opt_via_addr + ":"
				+ m_UAConfig.getUserProfileData().getLocalPort();
	}

	public void InitializeDefaultConfig() {
		m_UAConfig.getCallOptionsData().InitializeDefaultConfig();
		m_UAConfig.getProxyRegistrationData().InitializeDefaultConfig();
		m_UAConfig.getUserInfoData().InitializeDefaultConfig();
		m_UAConfig.getUserProfileData().InitializeDefaultConfig();
	}

	public Context getUIContext() {
		return m_EngineListener.getUIContext();
	}

	/* ------------- CODE RELATED TO REGISTRATION STARTS HERE ----------- */

	private OnClickListener RegCancelClickHandler = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			ra.halt();
			m_RegAlertDialog.cancel();
			m_RegAlertDialog = null;
		}
	};
	private String serviceRoute;

	/**
	 * Register with the registrar server.
	 * 
	 * @param expire_time
	 *            expiration time in seconds
	 */
	public void register(int expire_time) {

		if (expire_time < 0) {
			expire_time = m_UAConfig.getProxyRegistrationData()
					.getSuggestedRegistrationExpDur();
		}
		if (ra.isRegisterationLooping()) {
			ra.halt();
		}

		m_RegAlertDialog = new AlertDialog.Builder(getUIContext()).setMessage(
				"Registering").setTitle("Sipdroid").setIcon(R.drawable.icon22)
				.setCancelable(false).setNegativeButton("Cancel",
						RegCancelClickHandler).show();

		/*
		 * m_RegAlertDialog = (AlertDialog)getUIContext().showAlert("Sipdroid",
		 * R.drawable.icon22, , "Cancel", RegCancelClickHandler, false, null);
		 */
		Log.v(Constants.LogConstants.TAG, "Registering now");
		boolean result = ra.register(expire_time);

		if (!result) {
			m_RegAlertDialog.cancel();
			m_RegAlertDialog = null;

			/*
			 * getUIContext().showAlert("Sipdroid", R.drawable.icon22,
			 * "Could not send registration request.", "Ok", true);
			 */
		}
	}

	/**
	 * Periodically registers the contact address with the registrar server.
	 * 
	 * @param expire_time
	 *            expiration time in seconds
	 * @param renew_time
	 *            renew time in seconds
	 * @param keepalive_time
	 *            keep-alive packet rate (inter-arrival time) in milliseconds
	 */
	public void loopRegister(int expire_time, int renew_time,
			long keepalive_time) {
		if (ra.isRegisterationLooping()) {
			ra.halt();
		}

		m_RegAlertDialog = new AlertDialog.Builder(getUIContext()).setMessage(
				"Registering").setTitle("Sipdroid").setIcon(R.drawable.icon22)
				.setCancelable(false).setNegativeButton("Cancel",
						RegCancelClickHandler).show();
		/*
		 * m_RegAlertDialog = (AlertDialog)getUIContext().showAlert("Sipdroid",
		 * R.drawable.icon22, "Registering", "Cancel", RegCancelClickHandler,
		 * false, null);
		 */
		boolean result = ra.loopRegister(expire_time, renew_time,
				keepalive_time);

		if (!result) {
			m_RegAlertDialog.cancel();
			m_RegAlertDialog = null;

			new AlertDialog.Builder(getUIContext()).setMessage(
					"Could not send registration request.")
					.setTitle("Sipdroid").setIcon(R.drawable.icon22)
					.setCancelable(true).show();
			/*
			 * getUIContext().showAlert("Sipdroid", R.drawable.icon22,
			 * "Could not send registration request.", "Ok", true);
			 */
		}
	}

	/** Unregister with the registrar server */
	public void unregister() {
		if (ra.isRegisterationLooping()) {
			ra.halt();
		}

		m_RegAlertDialog = new AlertDialog.Builder(getUIContext()).setMessage(
				"DeRegistering").setTitle("Sipdroid")
				.setIcon(R.drawable.icon22).setCancelable(false)
				.setNegativeButton("Cancel", RegCancelClickHandler).show();
		/*
		 * m_RegAlertDialog = (AlertDialog)getUIContext().showAlert("Sipdroid",
		 * R.drawable.icon22, "De-Registering from network", "Cancel",
		 * RegCancelClickHandler, false, null);
		 */
		boolean result = ra.unregister();

		if (!result) {
			m_RegAlertDialog.cancel();
			m_RegAlertDialog = null;

			new AlertDialog.Builder(getUIContext()).setMessage(
					"Could not send de-registration request.").setTitle(
					"Sipdroid").setIcon(R.drawable.icon22).setCancelable(true)
					.show();

			/*
			 * getUIContext().showAlert("Sipdroid", R.drawable.icon22,
			 * "Could not send de-registration request.", "Ok", true);
			 */
		}
	}

	public boolean isRegistered() {
		if (ra == null) {
			return false;
		}
		return ra.isRegistered();
	}

	public boolean isUnRegistered() {
		if (ra == null) {
			return false;
		}
		return ra.isUnRegistered();
	}

	public void onUaRegistrationSuccess(RegisterAgent ra, NameAddress target,
			NameAddress contact, String result) {
		this.contact = contact;
		m_RegAlertDialog.cancel();
		m_RegAlertDialog = null;

		// we set the state to listening for incoming calls, if we are not
		// already in a call
		listen();
	}

	/** When a UA failed on (un)registering. */
	public void onUaRegistrationFailure(RegisterAgent ra, NameAddress target,
			NameAddress contact, String result) {

		m_RegAlertDialog.cancel();
		m_RegAlertDialog = null;

		new AlertDialog.Builder(getUIContext()).setMessage(
				"Registration failed.").setTitle("Sipdroid").setIcon(
				R.drawable.icon22).setCancelable(true).show();
		/*
		 * getUIContext().showAlert("Sipdroid", R.drawable.icon22,
		 * "Registration failed.", "Ok", true);
		 */
		// we set the state to listening for incoming calls, if we are not
		// already in a call
		listen();
	}

	/* ------------- CODE RELATED TO REGISTRATION ENDS HERE ----------- */

	/*
	 * ------------- CODE RELATED TO CALL RELATED PRODEDURES START HERE
	 * -----------
	 */

	/** Receives incoming calls (auto accept) */
	public void listen() {
		ua.printLog("UAS: WAITING FOR INCOMING CALL");

		if (!ua.user_profile.audio && !ua.user_profile.video) {
			ua.printLog("ONLY SIGNALING, NO MEDIA");
		}

		ua.listen();

	}

	public boolean isInCall() {
		if (ua == null) {
			return false;
		}
		return (ua.getStatus() != UserAgent.UA_STATE_IDLE);
	}

	/** Makes a new call */
	public void call(String target_url) {

		ua.printLog("UAC: CALLING " + target_url);

		if (!ua.user_profile.audio && !ua.user_profile.video) {
			ua.printLog("ONLY SIGNALING, NO MEDIA");
		}

		ua.call(target_url, m_UAConfig.getCallOptionsData().isBlockCallerId());
	}

	public void answercall() {
		ua.accept();
	}

	public void rejectcall() {
		ua.hangup();
		listen();
	}

	/** When a new call is incoming */
	public void onUaCallIncoming(UserAgent ua, NameAddress callee,
			NameAddress caller) {
		if (m_UAConfig.getCallOptionsData().isAutoAnswer()) {
			answercall();
		} else if (m_UAConfig.getCallOptionsData().isDoNotDisturbMode()) {
			rejectcall();
		} else {
			m_EngineListener.onUaCallIncoming(ua, callee, caller);
			if (m_UAConfig.getCallOptionsData().getAutoIgnore().isAutoIgnore()) {
				// Start the Auto-Ignore Timer Here
			}
		}
	}

	/** When an ougoing call is remotly ringing */
	public void onUaCallRinging(UserAgent ua) {
		m_EngineListener.onUaCallRinging(ua);
	}

	/** When an incoming call is cancelled */
	public void onUaCallCancelled(UserAgent ua) {
		m_EngineListener.onUaCallCancelled(ua);
		listen();

	}

	/** When an ougoing call has been accepted */
	public void onUaCallAccepted(UserAgent ua) {
		m_EngineListener.onUaCallAccepted(ua);
	}

	/** When a call has been trasferred */
	public void onUaCallTrasferred(UserAgent ua) {
		m_EngineListener.onUaCallTrasferred(ua);
		listen();
	}

	/** When an ougoing call has been refused or timeout */
	public void onUaCallFailed(UserAgent ua) {

		m_EngineListener.onUaCallFailed(ua);
		listen();
	}

	/** When a call has been locally or remotly closed */
	public void onUaCallClosed(UserAgent ua) {
		m_EngineListener.onUaCallClosed(ua);
		listen();
	}

	@Override
	public void onUaRegistrationSuccess(RegisterAgent ra, NameAddress target,
			NameAddress contact, String result, String serviceRoute) {
		// TODO Auto-generated method stub
		Log.v(Constants.LogConstants.TAG, "REGISTER success + " + serviceRoute);
		this.serviceRoute = serviceRoute;
		this.contact = contact;
		ua.setServiceRoute(serviceRoute);
		m_RegAlertDialog.cancel();
		m_RegAlertDialog = null;

		// we set the state to listening for incoming calls, if we are not
		// already in a call
		listen();
	}

	/*
	 * ------------- CODE RELATED TO CALL RELATED PRODEDURES END HERE
	 * -----------
	 */
}
