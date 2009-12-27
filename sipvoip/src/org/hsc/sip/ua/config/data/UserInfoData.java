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

public class UserInfoData {
	private String m_Name;
	private String m_Email;
	private String m_Location;
	private String m_Comments;

	public UserInfoData() {
		setName("");
		setEmail("");
		setLocation("");
		setComments("");
	}

	public void InitializeDefaultConfig() {
		setName("My Name");
		setEmail("your_email@mydomain.com");
	}

	public void VerifyConfig() {
		if (this.getName() == null || this.getName().trim().length() == 0) {
			this.setName("My Name");
		}
	}

	public String getName() {
		return m_Name;
	}

	public void setName(String name) {
		m_Name = name;
	}

	public String getEmail() {
		return m_Email;
	}

	public void setEmail(String email) {
		m_Email = email;
	}

	public String getLocation() {
		return m_Location;
	}

	public void setLocation(String location) {
		m_Location = location;
	}

	public String getComments() {
		return m_Comments;
	}

	public void setComments(String comments) {
		m_Comments = comments;
	}

}
