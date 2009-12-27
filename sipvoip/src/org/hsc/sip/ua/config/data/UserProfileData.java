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

package org.hsc.sip.ua.config.data;

import org.hsc.sip.ua.*;

public class UserProfileData {
	private String m_UserName;
	private String m_Password;
	private String m_LocalIP;
	private short m_LocalPort;
	private short m_Transport;

	public UserProfileData() {
		setUserName("");
		setPassword("");
		setLocalPort((short) 0);
		setLocalIP("127.0.0.1");
		setTransport(Constants.TransportConstants.UND_SOCK);
	}

	public void InitializeDefaultConfig() {
		setUserName("my_name");
		setLocalPort((short) 5060);
		setLocalIP("127.0.0.1");
		setTransport(Constants.TransportConstants.UND_SOCK);
	}

	public void VerifyConfig() {
		if (this.getUserName().trim().length() == 0) {
			setUserName("my_name");
		}
		
		if (this.getLocalIP().trim().length() == 0) {
			setUserName("127.0.0.1");
		}
		
		if (this.getLocalPort() <= 0) {
			this.setLocalPort((short) 5060);
		}

		if (0 == (this.getTransport() & Constants.TransportConstants.TCP_SOCK)
				&& 0 == (this.getTransport() & Constants.TransportConstants.UDP_SOCK)) {
			this.setTransport(Constants.TransportConstants.UDP_SOCK);
		}
	}

	public String getUserName() {
		return m_UserName;
	}

	public void setUserName(String userName) {
		m_UserName = userName;
	}

	public String getPassword() {
		return m_Password;
	}

	public void setPassword(String password) {
		m_Password = password;
	}

	public short getLocalPort() {
		return m_LocalPort;
	}

	public void setLocalPort(short localPort) {
		m_LocalPort = localPort;
	}

	public short getTransport() {
		return m_Transport;
	}

	public void setTransport(short transport) {
		m_Transport = transport;
	}
	
	public String getLocalIP() {
		return m_LocalIP;
	}

	public void setLocalIP(String localip) {
		m_LocalIP = localip;
	}

}
