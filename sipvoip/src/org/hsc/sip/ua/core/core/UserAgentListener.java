/*
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 * 
 * This file is part of MjSip (http://www.mjsip.org)
 * 
 * MjSip is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * MjSip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MjSip; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 * Nitin Khanna, Hughes Systique Corp. (Reason: Android specific change, optmization, bug fix) 
 */

package org.hsc.sip.ua.core.core;

import org.zoolu.sip.address.NameAddress;

import android.content.Context;

/** Listener of UserAgent */
public interface UserAgentListener {
	/** When a new call is incoming */
	public void onUaCallIncoming(UserAgent ua, NameAddress callee,
			NameAddress caller);

	/** When an incoming call is cancelled */
	public void onUaCallCancelled(UserAgent ua);

	/** When an ougoing call is remotly ringing */
	public void onUaCallRinging(UserAgent ua);

	/** When an ougoing call has been accepted */
	public void onUaCallAccepted(UserAgent ua);

	/** When a call has been trasferred */
	public void onUaCallTrasferred(UserAgent ua);

	/** When an ougoing call has been refused or timeout */
	public void onUaCallFailed(UserAgent ua);

	/** When a call has been locally or remotly closed */
	public void onUaCallClosed(UserAgent ua);

	public Context getUIContext();

}