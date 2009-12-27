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

public class UAConfig {
	private UserProfileData m_UsrProfileData;
	private UserInfoData m_UsrInfoData;
	private ProxyRegistrationData m_ProxyRegistrationData;
	private CallOptionsData m_CallOptionsData;

	public UAConfig() {
		m_UsrProfileData = new UserProfileData();
		m_UsrInfoData = new UserInfoData();
		m_ProxyRegistrationData = new ProxyRegistrationData();
		m_CallOptionsData = new CallOptionsData();
	}

	public UserProfileData getUserProfileData() {
		return m_UsrProfileData;
	}

	public UserInfoData getUserInfoData() {
		return m_UsrInfoData;
	}

	public ProxyRegistrationData getProxyRegistrationData() {
		return m_ProxyRegistrationData;
	}

	public CallOptionsData getCallOptionsData() {
		return m_CallOptionsData;
	}

	public void VerifyConfig() {
		getCallOptionsData().VerifyConfig();
		getProxyRegistrationData().VerifyConfig();
		getUserInfoData().VerifyConfig();
		getUserProfileData().VerifyConfig();
	}
}