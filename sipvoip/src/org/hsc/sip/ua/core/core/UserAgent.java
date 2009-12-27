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
 * 
 * Nitin Khanna (nitin.khanna@hsc.com), changes done for Android implementation 
 */

package org.hsc.sip.ua.core.core;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import org.hsc.media.JAudioLauncher;
import org.hsc.media.MediaLauncher;
import org.hsc.sip.ua.Constants;
import org.zoolu.sdp.AttributeField;
import org.zoolu.sdp.ConnectionField;
import org.zoolu.sdp.MediaDescriptor;
import org.zoolu.sdp.MediaField;
import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sdp.TimeField;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.Call;
import org.zoolu.sip.call.CallListenerAdapter;
import org.zoolu.sip.call.ExtendedCall;
import org.zoolu.sip.call.SdpTools;
import org.zoolu.sip.header.StatusLine;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack; //import org.zoolu.tools.Log;
import org.zoolu.tools.LogLevel;
import org.zoolu.tools.Parser;

import android.content.Context;

import android.util.Log;

/**
 * Simple SIP user agent (UA). It includes audio/video applications.
 * <p>
 * It can use external audio/video tools as media applications. Currently only
 * RAT (Robust Audio Tool) and VIC are supported as external applications.
 */
public class UserAgent extends CallListenerAdapter {
	/** Event logger. */
	org.zoolu.tools.Log log;

	/** UserAgentProfile */
	public UserAgentProfile user_profile;

	/** SipProvider */
	protected SipProvider sip_provider;

	/** Call */
	// Call call;
	protected ExtendedCall call;

	/** Call transfer */
	protected ExtendedCall call_transfer;

	/** Audio application */
	protected JAudioLauncher audio_app = null;

	/** Local sdp */
	protected String local_session = null;

	/** UserAgent listener */
	protected UserAgentListener listener = null;

	/** Media file path */
	final String MEDIA_PATH = "media/local/ua/";
	
	/* Conference members */
	HashSet<String> confMembers;
	String target_url_new;

	/** On wav file */
	final String CLIP_ON = MEDIA_PATH + "on.wav";
	/** Off wav file */
	final String CLIP_OFF = MEDIA_PATH + "off.wav";
	/** Ring wav file */
	final String CLIP_RING = MEDIA_PATH + "ring.wav";

	/** Ring sound */
	// private MediaPlayer clip_ring;
	/** On sound */
	// private MediaPlayer clip_on;
	/** Off sound */
	// private MediaPlayer //clip_off;
	// *********************** Startup Configuration ***********************
	static final int UA_STATE_IDLE = 0;
	static final int UA_STATE_INCOMING_CALL = 1;
	static final int UA_STATE_OUTGOING_CALL = 2;
	static final int UA_STATE_INCALL = 3;

	int call_state = UA_STATE_IDLE;

	// *********************** Service Route ***********************
	public String serviceRoute;

	// *************************** Basic methods ***************************

	// *************************** Basic methods ***************************

	/** Changes the call state */
	protected void changeStatus(int state) {
		call_state = state;
	}

	public String getCallStateDescription() {
		String result = "";
		switch (call_state) {
		case UA_STATE_IDLE:
			result = "Ready";
			break;
		case UA_STATE_INCOMING_CALL:
			result = "Handling an incoming Call";
			break;
		case UA_STATE_OUTGOING_CALL:
			result = "Making an outgoing call";
			break;
		case UA_STATE_INCALL:
			result = "In call";
			break;
		}

		return result;
	}

	/** Checks the call state */
	protected boolean statusIs(int state) {
		return (call_state == state);
	}

	/** Gets the call state */
	protected int getStatus() {
		return call_state;
	}

	/**
	 * Sets the automatic answer time (default is -1 that means no auto accept
	 * mode)
	 */
	public void setAcceptTime(int accept_time) {
		user_profile.accept_time = accept_time;
	}

	/**
	 * Sets the automatic hangup time (default is 0, that corresponds to manual
	 * hangup mode)
	 */
	public void setHangupTime(int time) {
		user_profile.hangup_time = time;
	}

	/** Sets the redirection url (default is null, that is no redircetion) */
	public void setRedirection(String url) {
		user_profile.redirect_to = url;
	}

	/** Sets the no offer mode for the invite (default is false) */
	public void setNoOfferMode(boolean nooffer) {
		user_profile.no_offer = nooffer;
	}

	/** Enables audio */
	public void setAudio(boolean enable) {
		user_profile.audio = enable;
	}

	/** Enables video */
	/*
	 * public void setVideo(boolean enable) { user_profile.video = enable; }
	 */

	/** Sets the receive only mode */
	public void setReceiveOnlyMode(boolean r_only) {
		user_profile.recv_only = r_only;
	}

	/** Sets the send only mode */
	public void setSendOnlyMode(boolean s_only) {
		user_profile.send_only = s_only;
	}

	/** Sets the send tone mode */
	public void setSendToneMode(boolean s_tone) {
		user_profile.send_tone = s_tone;
	}

	/** Sets the send file */

	public void setSendFile(String file_name) {
		user_profile.send_file = file_name;
	}

	/** Sets the recv file */

	public void setRecvFile(String file_name) {
		user_profile.recv_file = file_name;
	}

	/** Gets the local SDP */
	public String getSessionDescriptor() {
		return local_session;
	}

	/** Sets the local SDP */
	public void setSessionDescriptor(String sdp) {
		local_session = sdp;
	}

	/** Inits the local SDP (no media spec) */
	public void initSessionDescriptor() {
		SessionDescriptor sdp = new SessionDescriptor(user_profile.from_url,
				sip_provider.getViaAddress());

		local_session = sdp.toString();
	}

	/** Adds a media to the SDP */
	public void addMediaDescriptor(String media, int port, int avp,
			String codec, int rate) {

		if (local_session == null) {
			initSessionDescriptor();
		}

		SessionDescriptor sdp = new SessionDescriptor(local_session);

		String attr_param = String.valueOf(avp);

		if (codec != null) {
			attr_param += " " + codec + "/" + rate;
		}
		sdp.addMedia(new MediaField(media, port, 0, "RTP/AVP", String
				.valueOf(avp)), new AttributeField("rtpmap", attr_param));

		local_session = sdp.toString();
	}

	// *************************** Public Methods **************************

	public Context getUIContext() {
		return listener.getUIContext();
	}

	/** Costructs a UA with a default media port */
	public UserAgent(SipProvider sip_provider, UserAgentProfile user_profile,
			UserAgentListener listener) {
		this.sip_provider = sip_provider;
		// log = sip_provider.getLog();
		this.listener = listener;
		this.user_profile = user_profile;
		this.user_profile.video = false;

		// if no contact_url and/or from_url has been set, create it now
		user_profile.initContactAddress(sip_provider);

		// clip_ring = MediaPlayer.create(getUIContext(), R.raw.ring);
		// clip_ring.setLooping(1);

		// clip_on = MediaPlayer.create(getUIContext(), R.raw.on);

		// clip_off = MediaPlayer.create(getUIContext(), R.raw.off);

		// set local sdp
		initSessionDescriptor();

		// We will have atleast one media line, and it will be
		// audio
		if (user_profile.audio || !user_profile.video) {
			addMediaDescriptor("audio", user_profile.audio_port,
					user_profile.audio_avp, user_profile.audio_codec,
					user_profile.audio_sample_rate);
		}

		if (user_profile.video) {
			// This has no significance as of now as we do not have a
			// release ready for video.
			addMediaDescriptor("video", user_profile.video_port,
					user_profile.video_avp, null, 0);
		}
		
		confMembers = new HashSet<String> ();
	}

	/** Makes a new call (acting as UAC). */
	public boolean call(String target_url, boolean send_anonymous) {

		if (!this.statusIs(UA_STATE_IDLE) && !this.statusIs(UA_STATE_INCALL)) {
			// We can initiate or terminate a call only when
			// we are in an idle state
			printLog("Call attempted in State " + this.getStatus() +"\n"+ this.getSessionDescriptor()
					+ " : Failing Request", SipStack.LOG_LEVEL_UA);
			return false;
		}
		Log.v(Constants.LogConstants.TAG, "Service route:" + serviceRoute);

		changeStatus(UA_STATE_OUTGOING_CALL);

		String from_url;

		if (!send_anonymous) {
			from_url = user_profile.from_url;
		} else {
			from_url = "sip:anonymous@anonymous.com";
		}

		call = new ExtendedCall(sip_provider, from_url,
				user_profile.contact_url, user_profile.username,
				user_profile.realm, user_profile.passwd, this);

		// in case of incomplete url (e.g. only 'user' is present), try to
		// complete it
		target_url = sip_provider.completeNameAddress(target_url).toString();
		target_url_new = target_url;

		if (user_profile.no_offer) {
			call.call(target_url);
		} else {
			call.call(target_url, local_session, serviceRoute);
		}

		return true;
	}

	/** Waits for an incoming call (acting as UAS). */
	public boolean listen() {

		if (!this.statusIs(UA_STATE_IDLE)) {
			// We can listen for a call only when
			// we are in an idle state
			printLog("Call listening mode intiated in "
					+ this.getSessionDescriptor() + " : Failing Request",
					SipStack.LOG_LEVEL_UA);
			return false;
		}

		hangup();

		call = new ExtendedCall(sip_provider, user_profile.from_url,
				user_profile.contact_url, user_profile.username,
				user_profile.realm, user_profile.passwd, this);
		call.listen();

		return true;
	}

	/** Closes an ongoing, incoming, or pending call */
	public void hangup() {
		// if (clip_ring != null)
		{
			// clip_ring.stop();
		}

		closeMediaApplication();

		if (call != null) {
			call.hangup();
		}

		changeStatus(UA_STATE_IDLE);
	}

	/** Accepts an incoming call */
	public boolean accept() {

		// if (clip_ring != null)
		{
			// clip_ring.stop();
		}

		if (call == null) {
			return false;
		}

		call.accept(local_session);

		changeStatus(UA_STATE_INCALL);
		
		return true;
	}

	/** Redirects an incoming call */
	public void redirect(String redirection) {
		// if (clip_ring != null)
		{
			// clip_ring.stop();
		}
		if (call != null) {
			call.redirect(redirection);
		}
	}

	/** Launches the Media Application (currently, the RAT audio tool) */
	protected void launchMediaApplication() {
		// exit if the Media Application is already running
		
		  if (audio_app != null) {
		  printLog("DEBUG: media application is already running",
		  LogLevel.HIGH); return; } 
		  SessionDescriptor local_sdp = 
			  new SessionDescriptor(call .getLocalSessionDescriptor()); 
		  String local_media_address = (new Parser(local_sdp.getConnection().toString())).skipString().skipString().getString(); 
		  int local_audio_port = 0; int local_video_port = 0;
		  
		  // parse local sdp 
		  for (Enumeration<MediaDescriptor> e = local_sdp.getMediaDescriptors() .elements(); e.hasMoreElements();) {
		  MediaField media = e.nextElement().getMedia(); 
		  if (media.getMedia().equals("audio")) 
			  local_audio_port = media.getPort(); 
		  if (media.getMedia().equals("video"))
			  local_video_port = media.getPort(); } // parse remote sdp
		  SessionDescriptor remote_sdp = new SessionDescriptor(
				  call.getRemoteSessionDescriptor()); 
		  String remote_media_address = (new Parser(remote_sdp.getConnection().toString())).skipString().skipString().getString(); 
		  int remote_audio_port = 0; 
		  int remote_video_port = 0; 
		  for (Enumeration<MediaDescriptor> e = remote_sdp.getMediaDescriptors().elements(); e.hasMoreElements();) { 
			  MediaField media = e.nextElement().getMedia(); 
			  if (media.getMedia().equals("audio"))
				  remote_audio_port = media.getPort(); 
			  if (media.getMedia().equals("video")) 
				  remote_video_port = media.getPort(); 
		  }
		  
		  // select the media direction (send_only, recv_ony, fullduplex) 
		  int dir = 0; 
		  if (user_profile.recv_only) 
			  dir = -1; 
		  else if
		  	(user_profile.send_only) dir = 1;
		  
		  if (user_profile.audio && local_audio_port != 0 && remote_audio_port != 0) { // create an audio_app and start // it
		  
			  if (audio_app == null) { // for testing.. 
				  String audio_in = null; 
				  if (user_profile.send_tone) 
				  { audio_in = JAudioLauncher.TONE; } 
				  else if (user_profile.send_file != null) { audio_in = user_profile.send_file; } 
				  String audio_out = null; 
				  if (user_profile.recv_file != null) {audio_out = user_profile.recv_file; }
			  
				  audio_app = new JAudioLauncher(local_audio_port,
				  remote_media_address, remote_audio_port, dir, audio_in, audio_out,
				  user_profile.audio_sample_rate, user_profile.audio_sample_size,
				  user_profile.audio_frame_size, log); 
			  } 
			  
			  
			  //audio_app.startMedia(); 
		  }
		 
	}

	protected void addMediaLines(SessionDescriptor remote_sdp) {
		//SessionDescriptor remote_sdp = new SessionDescriptor(
			//	  call.getRemoteSessionDescriptor()); 
		  String remote_media_address = (new Parser(remote_sdp.getConnection().toString())).skipString().skipString().getString(); 
		  int remote_audio_port = 0; 
		  int remote_video_port = 0; 
		  for (Enumeration<MediaDescriptor> e = remote_sdp.getMediaDescriptors().elements(); e.hasMoreElements();) { 
			  MediaField media = e.nextElement().getMedia(); 
			  if (media.getMedia().equals("audio"))
				  remote_audio_port = media.getPort(); 
			  if (media.getMedia().equals("video")) 
				  remote_video_port = media.getPort(); 
		  }
		  
		  int dir = 0; 
		  if (user_profile.recv_only) 
			  dir = -1; 
		  else if
		  	(user_profile.send_only) dir = 1;
		  
		  String key = remote_media_address;
		  audio_app.addLines(key, remote_media_address, remote_audio_port, dir);
		  audio_app.startLineMedia(key);
	}
	
	/** Close the Media Application */
	protected void closeMediaApplication() {
		/*
		 * if (audio_app != null) { audio_app.stopMedia(); audio_app = null; }
		 */
	}

	// ********************** Call callback functions **********************

	/**
	 * Callback function called when arriving a new INVITE method (incoming
	 * call)
	 */
	public void onCallIncoming(Call call, NameAddress callee,
			NameAddress caller, String sdp, Message invite) {
		printLog("onCallIncoming()", LogLevel.LOW);
		Log.v(Constants.LogConstants.TAG, "Incoming call");
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("INCOMING", LogLevel.HIGH);

		changeStatus(UA_STATE_INCOMING_CALL);

		call.ring();

		if (sdp != null) {
			// Create the new SDP
			SessionDescriptor remote_sdp = new SessionDescriptor(sdp);
			SessionDescriptor local_sdp = new SessionDescriptor(local_session);
			SessionDescriptor new_sdp = new SessionDescriptor(remote_sdp
					.getOrigin(), remote_sdp.getSessionName(), local_sdp
					.getConnection(), local_sdp.getTime());
			new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp
					.getMediaDescriptors());
			new_sdp = SdpTools.sdpAttirbuteSelection(new_sdp, "rtpmap");
			local_session = new_sdp.toString();
		}
		// play "ring" sound
		// if (clip_ring != null)
		{
			// clip_ring.start();
		}
		if (listener != null) {
			listener.onUaCallIncoming(this, callee, caller);
		}
	}

	/**
	 * Callback function called when arriving a new Re-INVITE method
	 * (re-inviting/call modify)
	 */
	public void onCallModifying(Call call, String sdp, Message invite) {
		printLog("onCallModifying()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("RE-INVITE/MODIFY", LogLevel.HIGH);

		// to be implemented.
		// currently it simply accepts the session changes (see method
		// onCallModifying() in CallListenerAdapter)
		super.onCallModifying(call, sdp, invite);
	}

	/**
	 * Callback function that may be overloaded (extended). Called when arriving
	 * a 180 Ringing
	 */
	public void onCallRinging(Call call, Message resp) {
		printLog("onCallRinging()", LogLevel.LOW);
		Log.v(Constants.LogConstants.TAG, "Ringing response\n" + resp);
		if (call != this.call && call != call_transfer) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("RINGING", LogLevel.HIGH);

		// play "on" sound
		// if (clip_on != null)
		{
			// clip_on.start();
		}
		if (listener != null) {
			listener.onUaCallRinging(this);
		}
	}

	/**
	 * Callback function called when incoming response is 183
	 */
	public void onCallSessInProgress(Call call, String sdp, Message resp) {
		Log.v(Constants.LogConstants.TAG, "183 Response:\n" + resp);
		if (call != this.call && call != call_transfer) {
			Log.v(Constants.LogConstants.TAG, "Not a part of this call" + resp);
			return;
		}
		Log.v(Constants.LogConstants.TAG, "Offer?\n" + user_profile.no_offer);
		if (user_profile.no_offer) {
			// Create the new SDP
			SessionDescriptor remote_sdp = new SessionDescriptor(sdp);
			SessionDescriptor local_sdp = new SessionDescriptor(local_session);
			SessionDescriptor new_sdp = new SessionDescriptor(remote_sdp
					.getOrigin(), remote_sdp.getSessionName(), local_sdp
					.getConnection(), local_sdp.getTime());
			new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp
					.getMediaDescriptors());
			new_sdp = SdpTools.sdpAttirbuteSelection(new_sdp, "rtpmap");

			// update the local SDP
			local_session = new_sdp.toString();
			// answer with the local sdp
			Log.v(Constants.LogConstants.TAG, "Invoking prack handling");
			call.prackWithAnswer(local_session);
		}
		else {
			Log.v(Constants.LogConstants.TAG, "Invoking prack handling with offer");
			call.prackWithoutAnswer();
		}
	}

	/** Callback function called when arriving a 2xx (call accepted) */
	public void onCallAccepted(Call call, String sdp, Message resp) {
		printLog("onCallAccepted()", LogLevel.LOW);

		if (call != this.call && call != call_transfer) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}

		printLog("ACCEPTED/CALL", LogLevel.HIGH);

		changeStatus(UA_STATE_INCALL);

		// add target_url to conference set
		confMembers.add(target_url_new);

		SessionDescriptor remote_sdp = null;
		if (user_profile.no_offer) {
			// Create the new SDP
			remote_sdp = new SessionDescriptor(sdp);
			SessionDescriptor local_sdp = new SessionDescriptor(local_session);
			SessionDescriptor new_sdp = new SessionDescriptor(remote_sdp
					.getOrigin(), remote_sdp.getSessionName(), local_sdp
					.getConnection(), local_sdp.getTime());
			new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp
					.getMediaDescriptors());
			new_sdp = SdpTools.sdpAttirbuteSelection(new_sdp, "rtpmap");

			// update the local SDP
			local_session = new_sdp.toString();
			// answer with the local sdp
			call.ackWithAnswer(local_session);
		}
		// play "on" sound
		// if (clip_on != null)
		{
			// clip_on.start();
		}

		if (listener != null) {
			listener.onUaCallAccepted(this);
		}

		launchMediaApplication();
		SessionDescriptor tmp = new SessionDescriptor(sdp);
		addMediaLines(tmp);

		if (call == call_transfer) {
			StatusLine status_line = resp.getStatusLine();
			int code = status_line.getCode();
			String reason = status_line.getReason();
			this.call.notify(code, reason);
		}
	}

	/** Callback function called when arriving an ACK method (call confirmed) */
	public void onCallConfirmed(Call call, String sdp, Message ack) {
		printLog("onCallConfirmed()", LogLevel.LOW);

		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}

		printLog("CONFIRMED/CALL", LogLevel.HIGH);

		changeStatus(UA_STATE_INCALL);

		// if (clip_on != null)
		{
			// clip_on.start();
		}

		launchMediaApplication();
		SessionDescriptor remote_sdp = new SessionDescriptor(sdp);
		addMediaLines (remote_sdp);

		if (user_profile.hangup_time > 0) {
			this.automaticHangup(user_profile.hangup_time);
		}
	}

	/** Callback function called when arriving a 2xx (re-invite/modify accepted) */
	public void onCallReInviteAccepted(Call call, String sdp, Message resp) {
		printLog("onCallReInviteAccepted()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("RE-INVITE-ACCEPTED/CALL", LogLevel.HIGH);
	}

	/** Callback function called when arriving a 4xx (re-invite/modify failure) */
	public void onCallReInviteRefused(Call call, String reason, Message resp) {
		printLog("onCallReInviteRefused()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("RE-INVITE-REFUSED (" + reason + ")/CALL", LogLevel.HIGH);
		if (listener != null)
			listener.onUaCallFailed(this);
	}

	/** Callback function called when arriving a 4xx (call failure) */
	public void onCallRefused(Call call, String reason, Message resp) {
		printLog("onCallRefused()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("REFUSED (" + reason + ")", LogLevel.HIGH);
		changeStatus(UA_STATE_IDLE);

		if (call == call_transfer) {
			StatusLine status_line = resp.getStatusLine();
			int code = status_line.getCode();
			// String reason=status_line.getReason();
			this.call.notify(code, reason);
			call_transfer = null;
		}
		// play "off" sound
		// if (clip_off != null)
		{
			// clip_off.start();
		}
		if (listener != null) {
			listener.onUaCallFailed(this);
		}
	}

	/** Callback function called when arriving a 3xx (call redirection) */
	public void onCallRedirection(Call call, String reason,
			Vector<String> contact_list, Message resp) {
		printLog("onCallRedirection()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("REDIRECTION (" + reason + ")", LogLevel.HIGH);
		call.call(((String) contact_list.elementAt(0)));
	}

	/**
	 * Callback function that may be overloaded (extended). Called when arriving
	 * a CANCEL request
	 */
	public void onCallCanceling(Call call, Message cancel) {
		printLog("onCallCanceling()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("CANCEL", LogLevel.HIGH);
		changeStatus(UA_STATE_IDLE);
		// stop ringing
		// if (clip_ring != null)
		{
			// clip_ring.stop();
		}
		// play "off" sound
		// if (clip_off != null)
		{
			// clip_off.start();
		}
		if (listener != null) {
			listener.onUaCallCancelled(this);
		}
	}

	/** Callback function called when arriving a BYE request */
	public void onCallClosing(Call call, Message bye) {
		printLog("onCallClosing()", LogLevel.LOW);
		if (call != this.call && call != call_transfer) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}

		if (call != call_transfer && call_transfer != null) {
			printLog("CLOSE PREVIOUS CALL", LogLevel.HIGH);
			this.call = call_transfer;
			call_transfer = null;
			return;
		}
		// else
		printLog("CLOSE", LogLevel.HIGH);
		closeMediaApplication();
		// play "off" sound
		// if (clip_off != null)
		// clip_off.start();
		changeStatus(UA_STATE_IDLE);

		if (listener != null)
			listener.onUaCallClosed(this);

	}

	/**
	 * Callback function called when arriving a response after a BYE request
	 * (call closed)
	 */
	public void onCallClosed(Call call, Message resp) {
		printLog("onCallClosed()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("CLOSE/OK", LogLevel.HIGH);

		changeStatus(UA_STATE_IDLE);

		if (listener != null)
			listener.onUaCallClosed(this);
	}

	/** Callback function called when the invite expires */
	public void onCallTimeout(Call call) {
		printLog("onCallTimeout()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("NOT FOUND/TIMEOUT", LogLevel.HIGH);
		changeStatus(UA_STATE_IDLE);
		if (call == call_transfer) {
			int code = 408;
			String reason = "Request Timeout";
			this.call.notify(code, reason);
			call_transfer = null;
		}
		// play "off" sound
		// if (clip_off != null)
		// clip_off.start();
		if (listener != null)
			listener.onUaCallFailed(this);
	}

	// ****************** ExtendedCall callback functions ******************

	/**
	 * Callback function called when arriving a new REFER method (transfer
	 * request)
	 */
	public void onCallTransfer(ExtendedCall call, NameAddress refer_to,
			NameAddress refered_by, Message refer) {
		printLog("onCallTransfer()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer to " + refer_to.toString(), LogLevel.HIGH);
		call.acceptTransfer();
		call_transfer = new ExtendedCall(sip_provider, user_profile.from_url,
				user_profile.contact_url, this);
		call_transfer.call(refer_to.toString(), local_session, serviceRoute);
	}

	/** Callback function called when a call transfer is accepted. */
	public void onCallTransferAccepted(ExtendedCall call, Message resp) {
		printLog("onCallTransferAccepted()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer accepted", LogLevel.HIGH);
	}

	/** Callback function called when a call transfer is refused. */
	public void onCallTransferRefused(ExtendedCall call, String reason,
			Message resp) {
		printLog("onCallTransferRefused()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer refused", LogLevel.HIGH);
	}

	/** Callback function called when a call transfer is successfully completed */
	public void onCallTransferSuccess(ExtendedCall call, Message notify) {
		printLog("onCallTransferSuccess()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer successed", LogLevel.HIGH);
		call.hangup();
		if (listener != null)
			listener.onUaCallTrasferred(this);
	}

	/**
	 * Callback function called when a call transfer is NOT sucessfully
	 * completed
	 */
	public void onCallTransferFailure(ExtendedCall call, String reason,
			Message notify) {
		printLog("onCallTransferFailure()", LogLevel.LOW);
		if (call != this.call) {
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer failed", LogLevel.HIGH);
	}

	// ************************* Schedule events ***********************

	/** Schedules a re-inviting event after <i>delay_time</i> secs. */
	void reInvite(final String contact_url, final int delay_time) {
		SessionDescriptor sdp = new SessionDescriptor(local_session);
		final SessionDescriptor new_sdp = new SessionDescriptor(
				sdp.getOrigin(), sdp.getSessionName(), new ConnectionField(
						"IP4", "0.0.0.0"), new TimeField());
		new_sdp.addMediaDescriptors(sdp.getMediaDescriptors());
		(new Thread() {
			public void run() {
				runReInvite(contact_url, new_sdp.toString(), delay_time);
			}
		}).start();
	}

	/** Re-invite. */
	private void runReInvite(String contact, String body, int delay_time) {
		try {
			if (delay_time > 0)
				Thread.sleep(delay_time * 1000);
			printLog("RE-INVITING/MODIFING");
			if (call != null && call.isOnCall()) {
				printLog("REFER/TRANSFER");
				call.modify(contact, body);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Schedules a call-transfer event after <i>delay_time</i> secs. */
	void callTransfer(final String transfer_to, final int delay_time) {
		(new Thread() {
			public void run() {
				runCallTransfer(transfer_to, delay_time);
			}
		}).start();
	}

	/** Call-transfer. */
	private void runCallTransfer(String transfer_to, int delay_time) {
		try {
			if (delay_time > 0)
				Thread.sleep(delay_time * 1000);
			if (call != null && call.isOnCall()) {
				printLog("REFER/TRANSFER");
				call.transfer(transfer_to);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Schedules an automatic answer event after <i>delay_time</i> secs. */
	void automaticAccept(final int delay_time) {
		(new Thread() {
			public void run() {
				runAutomaticAccept(delay_time);
			}
		}).start();
	}

	/** Automatic answer. */
	private void runAutomaticAccept(int delay_time) {
		try {
			if (delay_time > 0)
				Thread.sleep(delay_time * 1000);
			if (call != null) {
				printLog("AUTOMATIC-ANSWER");
				accept();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Schedules an automatic hangup event after <i>delay_time</i> secs. */
	void automaticHangup(final int delay_time) {
		(new Thread() {
			public void run() {
				runAutomaticHangup(delay_time);
			}
		}).start();
	}

	/** Automatic hangup. */
	private void runAutomaticHangup(int delay_time) {
		try {
			if (delay_time > 0)
				Thread.sleep(delay_time * 1000);
			if (call != null && call.isOnCall()) {
				printLog("AUTOMATIC-HANGUP");
				hangup();
				listen();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ****************************** Logs *****************************

	/** Adds a new string to the default Log */
	void printLog(String str) {
		printLog(str, LogLevel.HIGH);
	}

	/** Adds a new string to the default Log */
	void printLog(String str, int level) {
		if (log != null)
			 log.println("UA: " + str, level + SipStack.LOG_LEVEL_UA);
			if ((user_profile == null || !user_profile.no_prompt)
					&& level <= LogLevel.HIGH)
				System.out.println("UA: " + str);
			
	}

	/** Adds the Exception message to the default Log */
	void printException(Exception e, int level) {
		// if (log != null)
		// log.printException(e, level + SipStack.LOG_LEVEL_UA);
	}

	public String getServiceRoute() {
		return serviceRoute;
	}

	public void setServiceRoute(String service_route) {
		serviceRoute = service_route;
	}

}
