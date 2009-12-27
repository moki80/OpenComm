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

import org.zoolu.sip.address.SipURL;

public class ProxyRegistrationData {
	private SipURL m_DomainOrRealm;
	private boolean m_UseProxy;
	private SipURL m_ProxyURI;
	private boolean m_RegisterOnStart;
	private short m_SuggestedRegistrationExpDur;
	private SipURL m_Registrar;

	public ProxyRegistrationData() {
		// setting the host name to empty for now
		setDomainOrRealm("", (short) 0);
		setUseProxy(false);
		setProxyURI("", (short) 0);
		setRegisterOnStart(true);
		setSuggestedRegistrationExpDur((short) 3600);
		setRegistrar("", (short) 0);
	}

	public void InitializeDefaultConfig() {
		setDomainOrRealm("192.168.1.135", (short) 0);
	}

	public void VerifyConfig() {
		if (getDomainOrRealm().trim().length() == 0) {
			setDomainOrRealm("iptel.org", (short) 0);
		}

		if (isUseProxy()) {
			if (this.getProxyURI().getHost().trim().length() == 0) {
				this.setProxyURI("iptel.org", this.getProxyURI().getPort());
			}

			if (this.getProxyURI().getPort() == 0) {
				this.setProxyURI(this.getProxyURI().getHost(), 5060);
			}
		}

		if (this.getSuggestedRegistrationExpDur() <= 15) {
			this.setSuggestedRegistrationExpDur((short) 300);
		}

	}

	public String getDomainOrRealm() {
		String result = m_DomainOrRealm.getHost();

		if (m_DomainOrRealm.getPort() > 0) {
			result = result + ":" + m_DomainOrRealm.getPort();
		}
		return result;
	}

	public void setDomainOrRealm(String domainOrRealm, int port) {
		m_DomainOrRealm = new SipURL(domainOrRealm, port);
	}

	public boolean isUseProxy() {
		return m_UseProxy;
	}

	public void setUseProxy(boolean useProxy) {
		m_UseProxy = useProxy;
	}

	public SipURL getProxyURI() {
		return m_ProxyURI;
	}

	public void setProxyURI(String proxyURI, int port) {
		m_ProxyURI = new SipURL(proxyURI, port);
	}

	public boolean isRegisterOnStart() {
		return m_RegisterOnStart;
	}

	public void setRegisterOnStart(boolean registerOnStart) {
		m_RegisterOnStart = registerOnStart;
	}

	public short getSuggestedRegistrationExpDur() {
		return m_SuggestedRegistrationExpDur;
	}

	public void setSuggestedRegistrationExpDur(short suggestedRegistrationExpDur) {
		m_SuggestedRegistrationExpDur = suggestedRegistrationExpDur;
	}

	public SipURL getRegistrar() {
		return m_Registrar;
	}

	public void setRegistrar(String registrar, short port) {
		m_Registrar = new SipURL(registrar, port);
	}
}
