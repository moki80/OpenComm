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

package org.hsc.sip.ua.config.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.hsc.sip.ua.R;
import org.hsc.sip.ua.config.data.CallOptionsData;
import org.hsc.sip.ua.config.data.ProxyRegistrationData;
import org.hsc.sip.ua.config.data.UAConfig;
import org.hsc.sip.ua.config.data.UserInfoData;
import org.hsc.sip.ua.config.data.UserProfileData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.app.AlertDialog;
import android.content.Context;

public class ConfigWriter {
	public static boolean WriteConfigData(String FileName, Context aUI,
			UAConfig ConfigData) {
		DocumentBuilderFactory domFactory = null;
		DocumentBuilder domBuilder = null;
		boolean result = false;

		try {
			domFactory = DocumentBuilderFactory.newInstance();
			domBuilder = domFactory.newDocumentBuilder();
		} catch (FactoryConfigurationError exp) {
			System.err.println(exp.toString());
			return result;
		} catch (ParserConfigurationException exp) {
			System.err.println(exp.toString());
			return result;
		} catch (Exception exp) {
			System.err.println(exp.toString());
			return result;
		}

		try {
			Document newDoc = domBuilder.newDocument();

			// Root element
			Element rootElement = newDoc.createElement("Sipdroid");
			Element configRootElement = newDoc.createElement("Config");
			rootElement.appendChild(configRootElement);

			ProcessUserInfoConfigData(ConfigData.getUserInfoData(), aUI,
					newDoc, configRootElement);
			ProcessUserProfileConfigData(ConfigData.getUserProfileData(), aUI,
					newDoc, configRootElement);
			ProcessCallOptionsConfigData(ConfigData.getCallOptionsData(), aUI,
					newDoc, configRootElement);
			ProcessProxyRegConfigData(ConfigData.getProxyRegistrationData(),
					aUI, newDoc, configRootElement);

			newDoc.appendChild(rootElement);

			result = writeXMLFile(FileName, aUI, newDoc);

		} catch (Exception E) {
			return result;
		}
		return result;
	}

	private static void WriteElement(Node node, Context aUI,
			OutputStreamWriter osw) throws IOException {
		try {

			Element elem = (Element) node;

			osw.write("<" + elem.getNodeName() + " ");

			if (elem.getNodeName().compareTo("Name") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("Email") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("Location") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("Password") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");

			} else if (elem.getNodeName().compareTo("Comments") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");

			} else if (elem.getNodeName().compareTo("LocalPort") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("LocalIP") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			}  
			else if (elem.getNodeName().compareTo("Transport") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("AutoAccept") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("AutoIgnore") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
				osw.write("timeout=\"" + elem.getAttribute("timeout") + "\" ");

			} else if (elem.getNodeName().compareTo("DoNoDisturb") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("BlockCallerId") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("NATMappingRefresh") == 0) {
				osw.write("use=\"" + elem.getAttribute("use") + "\" ");
				osw.write("type=\"" + elem.getAttribute("type") + "\" ");
				osw.write("timeout=\"" + elem.getAttribute("timeout") + "\" ");
			} else if (elem.getNodeName().compareTo("Domain") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("OutboundProxy") == 0) {
				osw.write("use=\"" + elem.getAttribute("use") + "\" ");
				osw.write("host=\"" + elem.getAttribute("host") + "\" ");
				osw.write("port=\"" + elem.getAttribute("port") + "\" ");
			} else if (elem.getNodeName().compareTo("SuggestedExpDur") == 0) {
				osw.write("value=\"" + elem.getAttribute("value") + "\" ");
			} else if (elem.getNodeName().compareTo("Registrar") == 0) {
				osw.write("host=\"" + elem.getAttribute("host") + "\" ");
				osw.write("port=\"" + elem.getAttribute("port") + "\" ");
			}

			/*
			 * if (elem.hasAttributes()) { NamedNodeMap attrs =
			 * elem.getAttributes(); for(int index = 0; index <
			 * attrs.getLength(); index ++ ) { Node nd = attrs.item(index);
			 * osw.write(nd.getNodeName() + " \"" + nd.getNodeValue() + "\""); } }
			 */
			osw.write(">");
			// osw.write(System.getProperty("line.separator", ""));

			if (elem.getNodeValue() != null) {
				osw.write(elem.getNodeValue());
				// osw.write(System.getProperty("line.separator", ""));
			}

			Node el = elem.getFirstChild();

			int ubound = elem.getChildNodes().getLength();
			int index = 0;

			while (el != null) {
				WriteElement(el, aUI, osw);
				index++;
				if (index < ubound) {
					el = el.getNextSibling();
				} else {
					el = null;
				}
			}

			osw.write("</" + elem.getNodeName() + ">");
			// osw.write(System.getProperty("line.separator", ""));
		} catch (Exception E) {
			new AlertDialog.Builder(aUI)
			.setMessage(E.toString())
			.setTitle("Sipdroid")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();
		}
	}

	private static boolean writeXMLFile(String FileName, Context aUI,
			Document doc) {
		boolean result = false;

		try {

			FileOutputStream fOut = aUI.openFileOutput("sipdroid_config.xml",
					Context.MODE_WORLD_WRITEABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);

			// Write the string to the file
			osw.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
			// osw.write(System.getProperty("line.separator", ""));
			Node el = doc.getFirstChild();
			int index = 0;
			int ubound = doc.getChildNodes().getLength();
			while (el != null) {
				WriteElement(el, aUI, osw);
				index++;
				if (index < ubound) {
					el = el.getNextSibling();
				} else {
					el = null;
				}
			}
			osw.flush();
			osw.close();

			result = true;
		} catch (Exception E) {
			new AlertDialog.Builder(aUI)
			.setMessage(E.toString())
			.setTitle("Sipdroid")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();
		}
		return result;
	}

	private static void ProcessUserInfoConfigData(UserInfoData a_UIData,
			Context aUI, Document doc, Element rootElement) {

		try

		{
			Element el = null;
			Element elroot = doc.createElement("UserInfo");

			el = doc.createElement("Name");
			el.setAttribute("value", a_UIData.getName());
			elroot.appendChild(el);

			el = doc.createElement("Email");
			el.setAttribute("value", a_UIData.getEmail());
			elroot.appendChild(el);

			el = doc.createElement("Location");
			el.setAttribute("value", a_UIData.getLocation());
			elroot.appendChild(el);

			el = doc.createElement("Comments");
			el.setAttribute("value",a_UIData.getComments());
			elroot.appendChild(el);

			rootElement.appendChild(elroot);
		} catch (Exception E) {
			new AlertDialog.Builder(aUI)
			.setMessage(E.toString())
			.setTitle("Sipdroid")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();
		}
	}

	private static void ProcessUserProfileConfigData(UserProfileData a_UPData,
			Context aUI, Document doc, Element rootElement) {
		Element elroot = doc.createElement("UserProfile");
		Element el = doc.createElement("Name");
		el.setAttribute("value", a_UPData.getUserName());
		elroot.appendChild(el);

		el = doc.createElement("Password");
		el.setAttribute("value", a_UPData.getPassword());
		elroot.appendChild(el);

		el = doc.createElement("LocalIP");
		el.setAttribute("value", a_UPData.getLocalIP());
		elroot.appendChild(el);
		
		el = doc.createElement("LocalPort");
		el.setAttribute("value", Short.toString(a_UPData.getLocalPort()));
		elroot.appendChild(el);

		el = doc.createElement("Transport");
		el.setAttribute("value", Short.toString(a_UPData.getTransport()));

		elroot.appendChild(el);

		rootElement.appendChild(elroot);

	}

	private static void ProcessCallOptionsConfigData(CallOptionsData a_CPData,
			Context aUI, Document doc, Element rootElement) {
		Element el = null;
		Element elroot = doc.createElement("CallOptions");
		try {
			el = doc.createElement("AutoAccept");
			el.setAttribute("value", Boolean.toString(a_CPData.isAutoAnswer()));
			elroot.appendChild(el);

			el = doc.createElement("AutoIgnore");
			el.setAttribute("value", Boolean.toString(a_CPData.getAutoIgnore()
					.isAutoIgnore()));
			el.setAttribute("timeout", Short.toString(a_CPData.getAutoIgnore()
					.getAfterDuration()));
			elroot.appendChild(el);

			el = doc.createElement("DoNotDisturb");
			el.setAttribute("value", Boolean.toString(a_CPData
					.isDoNotDisturbMode()));
			elroot.appendChild(el);

			el = doc.createElement("NATMappingRefresh");
			el.setAttribute("use", Boolean.toString(a_CPData
					.getNATRefreshParams().isUseNATRefresh()));
			el.setAttribute("type", Byte.toString(a_CPData
					.getNATRefreshParams().getNATRefreshType()));
			el.setAttribute("timeout", Short.toString(a_CPData
					.getNATRefreshParams().getNATTimeOut()));
			elroot.appendChild(el);

			rootElement.appendChild(elroot);

		} catch (Exception E) {
			new AlertDialog.Builder(aUI)
			.setMessage(E.toString())
			.setTitle("Sipdroid")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();
		}

	}

	private static void ProcessProxyRegConfigData(
			ProxyRegistrationData a_PRData, Context aUI, Document doc,
			Element rootElement) {
		Element elroot = doc.createElement("Proxy_Reg");

		Element el = doc.createElement("Domain");

		el.setAttribute("value", a_PRData.getDomainOrRealm());
		elroot.appendChild(el);

		el = doc.createElement("OutboundProxy");
		el.setAttribute("use", Boolean.toString(a_PRData.isUseProxy()));
		el.setAttribute("host", a_PRData.getProxyURI().getHost());
		el.setAttribute("port", Integer.toString(a_PRData.getProxyURI()
				.getPort()));

		elroot.appendChild(el);

		el = doc.createElement("RegisterOnStart");
		el
				.setAttribute("value", Boolean.toString(a_PRData
						.isRegisterOnStart()));
		elroot.appendChild(el);

		el = doc.createElement("SuggestedExpDur");
		el.setAttribute("value", Short.toString(a_PRData
				.getSuggestedRegistrationExpDur()));
		elroot.appendChild(el);

		el = doc.createElement("Registrar");
		el.setAttribute("host", a_PRData.getRegistrar().getHost());
		el.setAttribute("port", Integer.toString(a_PRData.getRegistrar()
				.getPort()));
		elroot.appendChild(el);

		rootElement.appendChild(elroot);
	}

}
