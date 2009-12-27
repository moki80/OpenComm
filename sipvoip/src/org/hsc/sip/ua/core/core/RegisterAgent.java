/*
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 * 
 * This source code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package org.hsc.sip.ua.core.core;

import java.util.Vector;

import org.hsc.net.KeepAliveSip;
import org.hsc.sip.ua.Constants;
import org.hsc.sip.ua.config.data.ProxyRegistrationData;
import org.hsc.sip.ua.config.data.UAConfig;
import org.zoolu.net.SocketAddress;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.authentication.DigestAuthentication;
import org.zoolu.sip.header.AuthorizationHeader;
import org.zoolu.sip.header.ContactHeader;
import org.zoolu.sip.header.ExpiresHeader;
import org.zoolu.sip.header.Header;
import org.zoolu.sip.header.MultipleHeader;
import org.zoolu.sip.header.StatusLine;
import org.zoolu.sip.header.WwwAuthenticateHeader;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.MessageFactory;
import org.zoolu.sip.message.SipMethods;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.sip.transaction.TransactionClient;
import org.zoolu.sip.transaction.TransactionClientListener;
import android.util.Log;

/**
 * Register User Agent. It registers (one time or periodically) a contact
 * address with a registrar server.
 */
public class RegisterAgent implements Runnable, TransactionClientListener {
	/** Max number of registration attempts. */
	static final int MAX_ATTEMPTS = 3;

	/* States for the RegisterAgent Module */
	public static final int UNREGISTERED = 0;
	public static final int REGISTERING = 1;
	public static final int REGISTERED = 2;
	public static final int DEREGISTERING = 3;
	public static String mjsip = "MJSIP";

	/** RegisterAgent listener */
	RegisterAgentListener listener;

	/** SipProvider */
	SipProvider sip_provider;

	/** Route header */
	SipURL route;
	/** User's URI with the fully qualified domain name of the registrar server. */
	NameAddress target;

	/** User name. */
	String username;

	/** User realm. */
	String realm;

	/** User's passwd. */
	String passwd;

	/** Nonce for the next authentication. */
	String next_nonce;

	/** Qop for the next authentication. */
	String qop;

	/** User's contact address. */
	NameAddress contact;

	/** Expiration time. */
	int expire_time;

	/** Renew time. */
	int renew_time;

	/** Whether keep on registering. */
	boolean loop;

	/** Whether the thread is running. */
	boolean is_running;

	/** Event logger. */
	Log log;

	/** Number of registration attempts. */
	int attempts;

	/** KeepAliveSip daemon. */
	KeepAliveSip keep_alive;

	/** Current State of the registrar component */
	int CurrentState = UNREGISTERED;

	/** Creates a new RegisterAgent. */
	public RegisterAgent(SipProvider sip_provider, String target_url,
			String contact_url, RegisterAgentListener listener) {
		init(sip_provider, target_url, contact_url, route, listener);
	}

	/**
	 * Creates a new RegisterAgent with authentication credentials (i.e.
	 * username, realm, and passwd).
	 */
	public RegisterAgent(SipProvider sip_provider, String target_url,
			String contact_url, String username, String realm, String passwd,
			SipURL route, RegisterAgentListener listener) {
		Log.v("MJSIP", "ROUTE IN REGISTER " + route);
		init(sip_provider, target_url, contact_url, route, listener);

		// authentication specific parameters
		this.username = username;
		this.realm = realm;
		this.passwd = passwd;
	}

	/** Inits the RegisterAgent. */
	private void init(SipProvider sip_provider, String target_url,
			String contact_url, SipURL route, RegisterAgentListener listener) {
		Log.v("MJSIP", "In INIT");
		this.listener = listener;
		this.sip_provider = sip_provider;
		// this.log = sip_provider.getLog();
		this.target = new NameAddress(target_url);
		this.contact = new NameAddress(contact_url);
		this.expire_time = SipStack.default_expires;
		this.renew_time = 0;
		this.is_running = false;
		this.keep_alive = null;

		this.route = route;
		// authentication
		this.username = null;
		this.realm = null;
		this.passwd = null;
		this.next_nonce = null;
		this.qop = null;
		this.attempts = 0;
	}

	/** Whether it is periodically registering. */
	public boolean isRegisterationLooping() {
		return is_running;
	}

	/** Whether it is periodically registering. */
	public boolean isRegistered() {
		return (CurrentState == REGISTERED);
	}

	public boolean isUnRegistered() {
		return (CurrentState == UNREGISTERED);
	}

	public int getCurrentState() {
		return CurrentState;
	}

	/** Registers with the registrar server. */
	public boolean register() {
		return register(expire_time);
	}

	/** Registers with the registrar server for <i>expire_time</i> seconds. */
	public boolean register(int expire_time) {
		attempts = 0;
		if (expire_time > 0) {
			// Update this to be the default registration duration for next
			// instances as well.

			if (CurrentState != UNREGISTERED && CurrentState != REGISTERED) {
				return false;
			}
			this.expire_time = expire_time;

		} else {
			if (CurrentState != REGISTERED) {
				// This is an error condition we must exit, we should not
				// de-register if
				// we have not registered at all
				return false;
			}
			// this is the case for de-registration
			expire_time = 0;
		}

		// Create message re
		Message req = MessageFactory.createRegisterRequest(sip_provider,
				target, target, contact);
		SocketAddress sockAddr = new SocketAddress(route.getHost(), route
				.getPort());
		sip_provider.setOutboundProxy(sockAddr);

		req.setExpiresHeader(new ExpiresHeader(String.valueOf(expire_time)));

		// create and fill the authentication params this is done when
		// the UA has been challenged by the registrar or intermediate UA
		Log.v(Constants.LogConstants.TAG, "Nonce is:" + next_nonce);
		if (next_nonce != null) {
			Log.v(Constants.LogConstants.TAG, "Setting Auth Header");
			AuthorizationHeader ah = new AuthorizationHeader("Digest");

			ah.addUsernameParam(username);
			ah.addRealmParam(realm);
			ah.addNonceParam(next_nonce);
			ah.addUriParam(req.getRequestLine().getAddress().toString());
			ah.addQopParam(qop);
			String response = (new DigestAuthentication(SipMethods.REGISTER,
					ah, null, passwd)).getResponse();
			ah.addResponseParam(response);
			req.setAuthorizationHeader(ah);
		}

		if (expire_time > 0) {
			// printLog("Registering contact " + contact + " (it expires in "
			// + expire_time + " secs)", LogLevel.HIGH);

		} else {
			// printLog("Unregistering contact " + contact, LogLevel.HIGH);
		}
		Log.v("MJSIP", "Request is:\n" + req);
		TransactionClient t = new TransactionClient(sip_provider, req, this);
		t.request();

		// Update the current state based on the operation
		if (expire_time > 0) {
			CurrentState = REGISTERING;
		} else {
			CurrentState = DEREGISTERING;
		}

		return true;
	}

	/** Unregister with the registrar server */
	public boolean unregister() {
		return register(0);
	}

	/**
	 * Periodically registers with the registrar server.
	 * 
	 * @param expire_time
	 *            expiration time in seconds
	 * @param renew_time
	 *            renew time in seconds
	 */
	public boolean loopRegister(int expire_time, int renew_time) {
		// The return value suggests that whether registration request
		// was accepted or not. In case you have an ongoing registration
		// or de-registration we simply ignore the request, the UI should
		// handle this scenario appropriately
		this.expire_time = expire_time;
		this.renew_time = renew_time;
		loop = true;
		if (!is_running) {
			(new Thread(this)).start();
			return true;
		}

		return false;
	}

	/**
	 * Periodically registers with the registrar server.
	 * 
	 * @param expire_time
	 *            expiration time in seconds
	 * @param renew_time
	 *            renew time in seconds
	 * @param keepalive_time
	 *            keep-alive packet rate (inter-arrival time) in milliseconds
	 */
	public boolean loopRegister(int expire_time, int renew_time,
			long keepalive_time) {

		if (!loopRegister(expire_time, renew_time)) {
			// if registration could not be started then we must exit
			return false;
		}

		// keep-alive
		if (keepalive_time > 0) {
			SipURL target_url = target.getAddress();
			String target_host = target_url.getHost();
			int targe_port = target_url.getPort();
			if (targe_port < 0)
				targe_port = SipStack.default_port;
			new KeepAliveSip(sip_provider, new SocketAddress(target_host,
					targe_port), null, keepalive_time);
		}

		return true;
	}

	/** Halts the periodic registration. */
	public void halt() {
		if (is_running) {
			loop = false;
		}

		if (keep_alive != null) {
			keep_alive.halt();
		}
	}

	// ***************************** run() *****************************

	/** Run method */
	public void run() {
		is_running = true;
		try {
			while (loop) {
				if (!register()) {
					loop = false;
				} else {
					Thread.sleep(renew_time * 1000);
				}
			}
		} catch (Exception e) {
			// printException(e, LogLevel.HIGH);
		}
		is_running = false;
	}

	// **************** Transaction callback functions *****************

	/** Callback function called when client sends back a failure response. */

	/** Callback function called when client sends back a provisional response. */
	public void onTransProvisionalResponse(TransactionClient transaction,
			Message resp) { // do nothing..
	}

	/** Callback function called when client sends back a success response. */
	public void onTransSuccessResponse(TransactionClient transaction,
			Message resp) {
		if (transaction.getTransactionMethod().equals(SipMethods.REGISTER)) {
			Log.v(mjsip, "Response recvd:\n" + resp);
			Header serviceRouteHeader = resp.getHeader("Service-Route");
			String serviceRoute = null;
			if (serviceRouteHeader != null)
				serviceRoute = serviceRouteHeader.getValue();
			Log.v(mjsip, "Service-Route: " + serviceRoute);

			// sip_provider.setServiceRouteURL(serviceRoute);
			if (resp.hasAuthenticationInfoHeader()) {
				next_nonce = resp.getAuthenticationInfoHeader()
						.getNextnonceParam();
			}

			StatusLine status = resp.getStatusLine();
			String result = status.getCode() + " " + status.getReason();

			// update the renew_time
			int expires = 0;
			if (resp.hasExpiresHeader()) {
				expires = resp.getExpiresHeader().getDeltaSeconds();
			} else if (resp.hasContactHeader()) {
				Vector<Header> contacts = resp.getContacts().getHeaders();
				for (int i = 0; i < contacts.size(); i++) {
					int exp_i = (new ContactHeader((Header) contacts
							.elementAt(i))).getExpires();
					if (exp_i > 0 && (expires == 0 || exp_i < expires))
						expires = exp_i;
				}
			}
			if (expires > 0 && expires < renew_time) {
				renew_time = expires;
			}

			// printLog("Registration success: " + result, LogLevel.HIGH);

			if (CurrentState == REGISTERING) {
				Log.v(mjsip, "UA Registered");
				CurrentState = REGISTERED;
			} else {
				Log.v(mjsip, "UA Un-Registered");
				CurrentState = UNREGISTERED;
			}
			if (listener != null) {
				listener.onUaRegistrationSuccess(this, target, contact, result,
						serviceRoute);
			}
		}
	}

	/** Callback function called when client sends back a failure response. */
	public void onTransFailureResponse(TransactionClient transaction,
			Message resp) {
		Log.v(mjsip, "FAILURE recv for REGISTER");
		if (transaction.getTransactionMethod().equals(SipMethods.REGISTER)) {
			StatusLine status = resp.getStatusLine();
			int code = status.getCode();
			Log.v(mjsip, "Generating new request for response code:" + code);
			Log.v(mjsip, "Attempts: " + attempts + " has auth header?:"
					+ resp.hasWwwAuthenticateHeader() + " realm:"
					+ resp.getWwwAuthenticateHeader().getRealmParam());

			if (code == 401
					&& attempts < MAX_ATTEMPTS
					&& resp.hasWwwAuthenticateHeader()
					&& resp.getWwwAuthenticateHeader().getRealmParam()
							.equalsIgnoreCase(realm)) {
				Log.v(mjsip, "Generating new request");
				attempts++;
				Message req = transaction.getRequestMessage();
				req.setCSeqHeader(req.getCSeqHeader().incSequenceNumber());

				WwwAuthenticateHeader wah = resp.getWwwAuthenticateHeader();
				String qop_options = wah.getQopOptionsParam();

				// printLog("DEBUG: qop-options: " + qop_options,
				// LogLevel.MEDIUM);

				qop = (qop_options != null) ? "auth" : null;

				AuthorizationHeader ah = (new DigestAuthentication(
						SipMethods.REGISTER, req.getRequestLine().getAddress()
								.toString(), wah, qop, null, username, passwd))
						.getAuthorizationHeader();
				req.setAuthorizationHeader(ah);

				TransactionClient t = new TransactionClient(sip_provider, req,
						this);
				Log.v(mjsip, "NEW Register request is:\n" + req);
				t.request();

				// He we need not change the current state since, in case it was
				// a case of registration, we are registering and if it was a
				// case of de-registration then we are de-registering

			} else {
				String result = code + " " + status.getReason();

				// Since the transactions are atomic, we rollback to the
				// previous state
				if (CurrentState == REGISTERING) {
					CurrentState = UNREGISTERED;
				} else {
					CurrentState = REGISTERED;
				}

				// printLog("Registration failure: " + result, LogLevel.HIGH);
				if (listener != null) {
					listener.onUaRegistrationFailure(this, target, contact,
							result);
				}
			}
		}
	}

	/** Callback function called when client expires timeout. */
	public void onTransTimeout(TransactionClient transaction) {
		if (transaction.getTransactionMethod().equals(SipMethods.REGISTER)) {
			// printLog("Registration failure: No response from server.",
			// LogLevel.HIGH);

			// Since the transactions are atomic, we rollback to the
			// previous state

			if (CurrentState == REGISTERING) {
				CurrentState = UNREGISTERED;
			} else {
				CurrentState = REGISTERED;
			}

			if (listener != null) {
				listener.onUaRegistrationFailure(this, target, contact,
						"Timeout");
			}
		}
	}

	// ****************************** Logs *****************************

	/** Adds a new string to the default Log */
	void printLog(String str, int level) {
//		if (log != null)
//			log.println("RegisterAgent: " + str, level + SipStack.LOG_LEVEL_UA);
	}

	/** Adds the Exception message to the default Log */
	void printException(Exception e, int level) {
//		if (log != null)
//			log.printException(e, level + SipStack.LOG_LEVEL_UA);
	}

}
