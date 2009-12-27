package org.hsc.sip.ua.config.reader;

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
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hsc.sip.ua.Constants;
import org.hsc.sip.ua.config.data.UAConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class ConfigReader {
	public static boolean ReadConfigData(String aFile, Context aUI,
			UAConfig aUAConfigData) {
		Document doc = null;
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			FileInputStream input = aUI.openFileInput(aFile);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			doc = db.parse(input);
			doc.normalize();
		} catch (IOException ioe) {
			return false;

		} catch (ParserConfigurationException pce) {
			return false;
		} catch (SAXException se) {
			return false;
		} catch (Exception ex) {
			return false;
		}

		Element NodeIterator = doc.getDocumentElement();
		// this is the sipdroid element in XML

		boolean result = true;
		int index = 0;
		int count = NodeIterator.getChildNodes().getLength();

		NodeIterator = (Element) NodeIterator.getFirstChild();
		while (NodeIterator != null && result == true) {
			if (NodeIterator.getNodeName().compareTo("Config") == 0) {
				result = ProcessConfigurationData(NodeIterator, aUAConfigData);
			} else {
				result = false;
			}
			index++;
			if (index < count) {
				NodeIterator = (Element) NodeIterator.getNextSibling();
			} else {
				NodeIterator = null;
			}
		}

		return result;
	}

	private static boolean ProcessConfigurationData(Node CurrentNode,
			UAConfig aUAConfigData) {
		boolean result = true;

		Node NodeIterator = CurrentNode.getFirstChild();
		int index = 0;
		int count = CurrentNode.getChildNodes().getLength();
		while (NodeIterator != null && result == true) {
			if (NodeIterator.getNodeName().compareTo("UserInfo") == 0) {
				result = ProcessUserInfo(NodeIterator, aUAConfigData);
			} else if (NodeIterator.getNodeName().compareTo("UserProfile") == 0) {
				result = ProcessUserProfile(NodeIterator, aUAConfigData);
			} else if (NodeIterator.getNodeName().compareTo("CallOptions") == 0) {
				result = ProcessCallOptions(NodeIterator, aUAConfigData);
			} else if (NodeIterator.getNodeName().compareTo("Proxy_Reg") == 0) {
				result = ProcessProxyReg(NodeIterator, aUAConfigData);
			} else {
				result = false;
			}
			index++;
			if (index < count) {
				NodeIterator = NodeIterator.getNextSibling();
			} else {
				NodeIterator = null;
			}
		}

		return result;
	}

	private static boolean ProcessUserInfo(Node CurrentNode,
			UAConfig aUAConfigData) {
		boolean result = true;
		int index = 0;

		Element NodeIterator = (Element) CurrentNode.getFirstChild();

		while (NodeIterator != null && result == true) {
			if (NodeIterator.getNodeName().compareTo("Name") == 0) {
				aUAConfigData.getUserInfoData().setName(
						NodeIterator.getAttribute("value"));
			} else if (NodeIterator.getNodeName().compareTo("Email") == 0) {
				aUAConfigData.getUserInfoData().setEmail(
						NodeIterator.getAttribute("value"));
			} else if (NodeIterator.getNodeName().compareTo("Location") == 0) {
				aUAConfigData.getUserInfoData().setLocation(
						NodeIterator.getAttribute("value"));
			} else if (NodeIterator.getNodeName().compareTo("Comments") == 0) {
				aUAConfigData.getUserInfoData().setComments(
						NodeIterator.getAttribute("value"));
			} else {
				result = false;
			}
			index++;
			if (index == CurrentNode.getChildNodes().getLength()) {
				NodeIterator = null;
			} else {
				NodeIterator = (Element) NodeIterator.getNextSibling();
			}

		}
		return result;
	}

	private static boolean ProcessUserProfile(Node CurrentNode,
			UAConfig aUAConfigData) {
		boolean result = true;
		int index = 0;
		Element NodeIterator = (Element) CurrentNode.getFirstChild();

		while (NodeIterator != null && result == true) {
			if (NodeIterator.getNodeName().compareTo("Name") == 0) {
				aUAConfigData.getUserProfileData().setUserName(
						NodeIterator.getAttribute("value"));
			} else if (NodeIterator.getNodeName().compareTo("Password") == 0) {
				aUAConfigData.getUserProfileData().setPassword(
						NodeIterator.getAttribute("value"));
			} else if (NodeIterator.getNodeName().compareTo("LocalPort") == 0) {
				aUAConfigData.getUserProfileData().setLocalPort(
						Short.parseShort(NodeIterator.getAttribute("value")));
			} else if (NodeIterator.getNodeName().compareTo("LocalIP") == 0) {
				aUAConfigData.getUserProfileData().setLocalIP(
						NodeIterator.getAttribute("value"));
			} else if (NodeIterator.getNodeName().compareTo("Transport") == 0) {
				aUAConfigData.getUserProfileData().setTransport(
						Short.parseShort(NodeIterator.getAttribute("value")));
			} else {
				result = false;
			}
			index++;
			if (index == CurrentNode.getChildNodes().getLength()) {
				NodeIterator = null;
			} else {
				NodeIterator = (Element) NodeIterator.getNextSibling();
			}
		}
		return result;
	}

	private static boolean ProcessCallOptions(Node CurrentNode,
			UAConfig aUAConfigData) {
		boolean result = true;
		int index = 0;
		Element NodeIterator = (Element) CurrentNode.getFirstChild();

		while (NodeIterator != null && result == true) {
			if (NodeIterator.getNodeName().compareTo("AutoAccept") == 0) {
				aUAConfigData.getCallOptionsData().setAutoAnswer(
						Boolean
								.parseBoolean(NodeIterator
										.getAttribute("value")));
			} else if (NodeIterator.getNodeName().compareTo("AutoIgnore") == 0) {
				aUAConfigData.getCallOptionsData().getAutoIgnore()
						.setAutoIgnore(
								Boolean.parseBoolean(NodeIterator
										.getAttribute("value")));
				aUAConfigData.getCallOptionsData().getAutoIgnore()
						.setAfterDuration(
								Short.parseShort(NodeIterator
										.getAttribute("timeout")));
			} else if (NodeIterator.getNodeName().compareTo("DoNotDisturb") == 0) {
				aUAConfigData.getCallOptionsData().setDoNotDisturbMode(
						Boolean
								.parseBoolean(NodeIterator
										.getAttribute("value")));
			} else if (NodeIterator.getNodeName().compareTo("BlockCallerId") == 0) {
				aUAConfigData.getCallOptionsData().setBlockCallerId(
						Boolean
								.parseBoolean(NodeIterator
										.getAttribute("value")));
			} else if (NodeIterator.getNodeName()
					.compareTo("NATMappingRefresh") == 0) {
				aUAConfigData.getCallOptionsData().getNATRefreshParams()
						.setNATTimeOut(
								Short.parseShort(NodeIterator
										.getAttribute("timeout")));
				aUAConfigData.getCallOptionsData().getNATRefreshParams()
						.setM_NATRefreshType(
								Byte.parseByte(NodeIterator
										.getAttribute("type")));
				aUAConfigData.getCallOptionsData().getNATRefreshParams()
						.setUseNATRefresh(
								Boolean.parseBoolean(NodeIterator
										.getAttribute("use")));
			} else {
				result = false;
			}
			index++;
			if (index == CurrentNode.getChildNodes().getLength()) {
				NodeIterator = null;
			} else {
				NodeIterator = (Element) NodeIterator.getNextSibling();
			}
		}
		return result;
	}

	private static boolean ProcessProxyReg(Node CurrentNode,
			UAConfig aUAConfigData) {
		boolean result = true;
		int index = 0;
		Element NodeIterator = (Element) CurrentNode.getFirstChild();

		while (NodeIterator != null && result == true) {
			if (NodeIterator.getNodeName().compareTo("Domain") == 0) {
				aUAConfigData.getProxyRegistrationData().setDomainOrRealm(
						NodeIterator.getAttribute("value"), (short) 0);
			} else if (NodeIterator.getNodeName().compareTo("OutboundProxy") == 0) {
				Log.v(Constants.LogConstants.TAG, "Use proxy is:\n"
						+ NodeIterator.getAttribute("use"));
				aUAConfigData.getProxyRegistrationData().setUseProxy(
						Boolean.parseBoolean(NodeIterator.getAttribute("use")));
				aUAConfigData.getProxyRegistrationData().setProxyURI(
						NodeIterator.getAttribute("host"),
						Short.parseShort(NodeIterator.getAttribute("port")));
			} else if (NodeIterator.getNodeName().compareTo("RegisterOnStart") == 0) {
				aUAConfigData.getProxyRegistrationData().setRegisterOnStart(
						Boolean
								.parseBoolean(NodeIterator
										.getAttribute("value")));
			} else if (NodeIterator.getNodeName().compareTo("SuggestedExpDur") == 0) {
				aUAConfigData.getProxyRegistrationData()
						.setSuggestedRegistrationExpDur(
								Short.parseShort(NodeIterator
										.getAttribute("value")));
			} else if (NodeIterator.getNodeName().compareTo("Registrar") == 0) {
				aUAConfigData.getProxyRegistrationData().setRegistrar(
						NodeIterator.getAttribute("host"),
						Short.parseShort(NodeIterator.getAttribute("port")));
			} else {
				result = false;
			}
			index++;
			if (index == CurrentNode.getChildNodes().getLength()) {
				NodeIterator = null;
			} else {
				NodeIterator = (Element) NodeIterator.getNextSibling();
			}
		}
		return result;
	}
}