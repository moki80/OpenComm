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

package org.hsc.sip.ua.core.ui;

import org.hsc.sip.ua.R;
import org.hsc.sip.ua.config.reader.ConfigReader;
import org.hsc.sip.ua.config.ui.CallOptions;
import org.hsc.sip.ua.config.ui.ProxyRegistration;
import org.hsc.sip.ua.config.ui.UserInfo;
import org.hsc.sip.ua.config.ui.UserProfile;
import org.hsc.sip.ua.config.writer.ConfigWriter;
import org.hsc.sip.ua.core.core.SipdroidEngine;
import org.hsc.sip.ua.core.core.SipdroidEngineListener;
import org.hsc.sip.ua.core.core.UserAgent;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.tools.Timer;
import org.zoolu.tools.TimerListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Sipdroid extends Activity implements TimerListener,
		SipdroidEngineListener {
	/** Called when the activity is first created. */
	ImageView background_image_view;
	TextView status_label;
	AutoCompleteTextView sip_uri_box;
	ImageButton m_CallButton;
	ImageButton m_DisconnectButton;

	public static final int CONFIG_USER_INFO = 1;
	public static final int CONFIG_USER_PROFILE = 2;
	public static final int CONFIG_CALL_OPTIONS = 3;
	public static final int CONFIG_PROXY_REG = 4;

	int m_CurrentTimer = 0;

	/* Following the menu item constants which will be used for menu creation */
	public static final int FIRST_MENU_ID = Menu.FIRST;
	public static final int CONFIGURE_MENU_ITEM = FIRST_MENU_ID + 1;
	public static final int ABOUT_MENU_ITEM = FIRST_MENU_ID + 2;
	public static final int EXIT_MENU_ITEM = FIRST_MENU_ID + 3;
	public static final int CONFIGURE_MENU_USER_INFO_MENU_ITEM = FIRST_MENU_ID + 4;
	public static final int CONFIGURE_MENU_USER_PROFILE_MENU_ITEM = FIRST_MENU_ID + 5;
	public static final int CONFIGURE_MENU_PROXY_SETTINGS_MENU_ITEM = FIRST_MENU_ID + 6;
	public static final int CONFIGURE_MENU_CALL_SETTINGS_MENU_ITEM = FIRST_MENU_ID + 7;
	public static final int RE_INITIALIZE_MENU_ITEM = FIRST_MENU_ID + 8;
	public static final int ANSWER_MENU_ITEM = FIRST_MENU_ID + 9;
	public static final int CALL_MENU_ITEM = FIRST_MENU_ID + 10;
	public static final int HANG_UP_MENU_ITEM = FIRST_MENU_ID + 11;
	public static final int REGISTER_MENU_ITEM = FIRST_MENU_ID + 12;
	public static final int DEREGISTER_MENU_ITEM = FIRST_MENU_ID + 13;
	
	ConfigReader m_ConfigReader;
	SipdroidEngine m_SipdroidEngine;

	AlertDialog m_AlertDlg = null;
	AlertDialog m_CallAlertDialog = null;

	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();

	// Create runnable for posting
	final Runnable mTimerCallback = new Runnable() {
		public void run() {
			TimerCallback();
		}
	};
	
	public void onTimeout(Timer t) {
		t.halt();
		mHandler.post(mTimerCallback);
	}

	// This function executes the timer expiry handler. There are two timers
	// defined as of now in the application layer. The first is for reading and
	// processing the configuration data, while the second is to launch and 
	// initialize the UA
	private void TimerCallback() {
		if (m_CurrentTimer == 1) {
			boolean result = ConfigReader.ReadConfigData("sipdroid_config.xml",
					this, m_SipdroidEngine.getSipdroidConfig());

			m_AlertDlg.cancel();
			m_AlertDlg = null;
			if (result == false) {
				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Error reading configuration file. Using default configuration.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(false)
				.show();
				
				try
				{
				m_AlertDlg.wait(10);	
				}
				catch (Exception e)
				{
					
				}
					
				m_SipdroidEngine.InitializeDefaultConfig();

			} else {
				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Applying Configuration")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(false)
				.show();

				try
				{
				m_AlertDlg.wait(10);	
				}
				catch (Exception e)
				{
					
				}	
			}
			m_CurrentTimer = 2;
			Timer TimerT1 = new Timer(2000, this);
			TimerT1.start();
		} else if (m_CurrentTimer == 2) {
			if (m_AlertDlg != null) {
				m_AlertDlg.cancel();
				m_AlertDlg = null;
			}
			m_CurrentTimer = 0;
			m_SipdroidEngine.StartEngine();
			
			status_label.setText(m_SipdroidEngine.getMyNumber());
		}

	}

	public Context getUIContext() {
		return this;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sipdroid);

		status_label = (TextView) findViewById(R.id.txt_my_number);
		sip_uri_box = (AutoCompleteTextView) findViewById(R.id.txt_callee);

		sip_uri_box.setTextSize(15);
		sip_uri_box.setSingleLine();
		sip_uri_box.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					AutoCompleteTextView sip_uri_box = (AutoCompleteTextView) v;
					String st = sip_uri_box.getText().toString().trim();
					if (st.compareToIgnoreCase("Called Party Address") == 0
							|| st.compareToIgnoreCase("AutoComplete") == 0) {
						sip_uri_box.setText("");
					}
				} else {
					AutoCompleteTextView sip_uri_box = (AutoCompleteTextView) v;
					if (sip_uri_box.getText().toString().trim().length() == 0) {
						sip_uri_box.setText("Called Party Address");
					}
				}
			}
		});

		background_image_view = (ImageView) findViewById(R.id.background_image);
		if (background_image_view != null) {
			background_image_view.setImageResource(R.drawable.backend);
		}

		m_CallButton = (ImageButton) findViewById(R.id.img_button_call);
		if (m_CallButton != null) {
			m_CallButton.setImageResource(R.drawable.ic_incall_answer);
		}

		m_DisconnectButton = (ImageButton) findViewById(R.id.img_button_discon);
		if (m_DisconnectButton != null) {
			m_DisconnectButton.setImageResource(R.drawable.ic_incall_hangup);
		}

		m_CallButton.requestFocus();
		m_DisconnectButton.setEnabled(false);
		m_CallButton.setVisibility(android.view.View.INVISIBLE);
		m_DisconnectButton.setVisibility(android.view.View.INVISIBLE);

		m_AlertDlg = new AlertDialog.Builder(this)
		.setMessage("Initializing OpenComm. Please wait")
		.setTitle("OpenComm")
		.setIcon(R.drawable.icon22)
		.setCancelable(false)
		.show();

		try
		{
		m_AlertDlg.wait(10);	
		}
		catch (Exception e)
		{
			
		};	

		m_CurrentTimer = 1;

		m_SipdroidEngine = new SipdroidEngine(this);

		Timer TimerT1 = new Timer(1000, this);
		TimerT1.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			 Intent extras) {
		// TODO Auto-generated method stub
		if (resultCode != 0) {
			switch (requestCode) {
			case CONFIG_USER_INFO: {
				m_SipdroidEngine.getSipdroidConfig().getUserInfoData().setName(
						extras.getStringExtra("UserName"));
				m_SipdroidEngine.getSipdroidConfig().getUserInfoData()
						.setEmail(extras.getStringExtra("Email"));
				m_SipdroidEngine.getSipdroidConfig().getUserInfoData()
						.setLocation(extras.getStringExtra("Location"));
				m_SipdroidEngine.getSipdroidConfig().getUserInfoData()
						.setComments(extras.getStringExtra("Comments"));

				ConfigWriter.WriteConfigData("sipdroid_config.xml", this,
						m_SipdroidEngine.getSipdroidConfig());
			}
				break;
			case CONFIG_USER_PROFILE: {
				m_SipdroidEngine.getSipdroidConfig().getUserProfileData()
						.setUserName(extras.getStringExtra("UserName"));
				m_SipdroidEngine.getSipdroidConfig().getUserProfileData()
						.setPassword(extras.getStringExtra("Password"));
				
				m_SipdroidEngine.getSipdroidConfig().getUserProfileData()
				.setLocalIP(extras.getStringExtra("LocalIP"));
				
				m_SipdroidEngine
						.getSipdroidConfig()
						.getUserProfileData()
						.setLocalPort(
								Short.parseShort(extras.getStringExtra("LocalPort")));
				/*
				 * only UDP supported for now
				 * 
				m_SipdroidEngine
						.getSipdroidConfig()
						.getUserProfileData()
						.setTransport(
								Short.parseShort(extras.getStringExtra("Transport")));
				*/

				ConfigWriter.WriteConfigData("sipdroid_config.xml", this,
						m_SipdroidEngine.getSipdroidConfig());

				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Restart application for changes to take effect.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();

				try
				{
				m_AlertDlg.wait(10);	
				}
				catch (Exception e)
				{
					
				};	

				/*
				showAlert("OpenComm", R.drawable.icon22,
						,
						"Ok", true);
						*/
			}
				break;
			case CONFIG_CALL_OPTIONS: {
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.setAutoAnswer(extras.getBooleanExtra("AutoAnswer", false));
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.getAutoIgnore().setAutoIgnore(
								extras.getBooleanExtra("AutoIgnore", false));
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.getAutoIgnore().setAfterDuration(
								extras.getShortExtra("AutoIgnoreTimeOut", (short)15));
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.setDoNotDisturbMode(extras.getBooleanExtra("DoNotDisturb", false));
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.setBlockCallerId(extras.getBooleanExtra("BlockCallerID", false));
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.getNATRefreshParams().setUseNATRefresh(
								extras.getBooleanExtra("NATRefresh", false));
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.getNATRefreshParams().setM_NATRefreshType(
								extras.getByteExtra("NATRefreshType", (byte)0));
				m_SipdroidEngine.getSipdroidConfig().getCallOptionsData()
						.getNATRefreshParams().setNATTimeOut(
								extras.getShortExtra("NATRefreshTimeout", (short)15));

				ConfigWriter.WriteConfigData("sipdroid_config.xml", this,
						m_SipdroidEngine.getSipdroidConfig());

				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Restart application for changes to take effect.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();

				try
				{
				m_AlertDlg.wait(10);	
				}
				catch (Exception e)
				{
					
				};	

				/*
				showAlert("OpenComm", R.drawable.icon22,
						"Restart application for changes to take effect.",
						"Ok", true);
						
						*/
			}

				break;
			case CONFIG_PROXY_REG: {
				m_SipdroidEngine.getSipdroidConfig().getProxyRegistrationData()
						.setDomainOrRealm(
								extras.getStringExtra("Domain").toString(),
								(short) 0);
				m_SipdroidEngine.getSipdroidConfig().getProxyRegistrationData()
						.setUseProxy(extras.getBooleanExtra("UseOutboundProxy", false));
				m_SipdroidEngine.getSipdroidConfig().getProxyRegistrationData()
						.setProxyURI(extras.getStringExtra("ProxyHost"),
								(short)extras.getIntExtra("ProxyPort", 5060));
				m_SipdroidEngine.getSipdroidConfig().getProxyRegistrationData()
						.setRegisterOnStart(
								extras.getBooleanExtra("RegisterOnStart", false));
				m_SipdroidEngine.getSipdroidConfig().getProxyRegistrationData()
						.setSuggestedRegistrationExpDur(
								extras.getShortExtra("SuggestedExpTime", (short)3600));
				m_SipdroidEngine.getSipdroidConfig().getProxyRegistrationData()
						.setRegistrar(extras.getStringExtra("RegistrarHost"),
								(short)extras.getIntExtra("RegistrarPort", 5060));

				ConfigWriter.WriteConfigData("sipdroid_config.xml", this,
						m_SipdroidEngine.getSipdroidConfig());

				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Restart application for changes to take effect.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();

				try
				{
				m_AlertDlg.wait(10);	
				}
				catch (Exception e)
				{
					
				};	

				/*
				showAlert("OpenComm", R.drawable.icon22,
						"Restart application for changes to take effect.",
						"Ok", true);
						*/

			}
				break;
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuItem m = menu.add(0, CALL_MENU_ITEM, 0, "Call");
		m.setIcon(R.drawable.sym_call);
		m = menu.add(0, HANG_UP_MENU_ITEM, 0, "Hang up");
		m.setIcon(R.drawable.sym_call_end);
		m = menu.add(0, RE_INITIALIZE_MENU_ITEM, 0, "Re-Initialize");
		m.setIcon(R.drawable.sym_presence_available);

		SubMenu sub = menu.addSubMenu(0, CONFIGURE_MENU_ITEM, 0, "Configure");
		sub.setIcon(R.drawable.configure);
		
		sub.add(0, CONFIGURE_MENU_USER_INFO_MENU_ITEM, 0, "User Information");
		sub.add(0, CONFIGURE_MENU_USER_PROFILE_MENU_ITEM, 0, "User Profile");
		sub.add(0, CONFIGURE_MENU_PROXY_SETTINGS_MENU_ITEM, 0, "Proxy Settings");
		sub.add(0, CONFIGURE_MENU_CALL_SETTINGS_MENU_ITEM, 0, "Call Settings");
		
		menu.add(0, REGISTER_MENU_ITEM, 0, "Register");
		menu.add(0, DEREGISTER_MENU_ITEM, 0, "End Registration");

		m = menu.add(0, EXIT_MENU_ITEM, 0, "Exit");
		m.setIcon(R.drawable.exit);

		return result;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean result = super.onPrepareOptionsMenu(menu);

		if (m_SipdroidEngine.GetState() == SipdroidEngine.INITIALIZED)
		{
			menu.findItem(RE_INITIALIZE_MENU_ITEM).setVisible(false);
			if (m_SipdroidEngine.isInCall()) 
			{
				menu.findItem(CALL_MENU_ITEM).setVisible(true);//CHN
				menu.findItem(HANG_UP_MENU_ITEM).setVisible(true);
			} 
			else 
			{
				menu.findItem(CALL_MENU_ITEM).setVisible(true);
				menu.findItem(HANG_UP_MENU_ITEM).setVisible(false);
			}
		}
		else
		{
			menu.findItem(RE_INITIALIZE_MENU_ITEM).setVisible(true);
			menu.findItem(CALL_MENU_ITEM).setVisible(false);
			menu.findItem(HANG_UP_MENU_ITEM).setVisible(false);
		}
		
		
		if (m_SipdroidEngine.isRegistered())
		{
			menu.findItem(REGISTER_MENU_ITEM).setVisible(false);
			menu.findItem(DEREGISTER_MENU_ITEM).setVisible(true);
		}
		else if (m_SipdroidEngine.isUnRegistered())
		{
			menu.findItem(DEREGISTER_MENU_ITEM).setVisible(false);
			menu.findItem(REGISTER_MENU_ITEM).setVisible(true);
		}
		else
		{
			menu.findItem(REGISTER_MENU_ITEM).setVisible(false);
			menu.findItem(DEREGISTER_MENU_ITEM).setVisible(false);
		}

		return result;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}
	

	private OnClickListener onCallHangupButtonClick = new OnClickListener() {
		public void onClick(DialogInterface dlg, int button) 
		{
			if (m_CallAlertDialog != null) 
			{
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
			m_SipdroidEngine.rejectcall();
			m_SipdroidEngine.listen();
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		Intent intent = null;

		switch (item.getItemId()) {
		case EXIT_MENU_ITEM:
		{
			this.finish();
			return true;
		}
		
		case CALL_MENU_ITEM: 
			{
				String target = this.sip_uri_box.getText().toString();
				if (target.length() != 0
						&& target.compareToIgnoreCase("Called Party Address") != 0) {
					
					m_CallAlertDialog = new AlertDialog.Builder(this)
					.setMessage("Calling : " + target)
					.setTitle("OpenComm")
					.setIcon(R.drawable.icon22)
					.setCancelable(false)
					.setNegativeButton("Hang-Up", onCallHangupButtonClick)
					.show();

					try
					{
						m_CallAlertDialog.wait(10);	
					}
					catch (Exception e)
					{
						
					};	

					
					m_SipdroidEngine.call(target);
				}	 
				else 
				{
					if (m_AlertDlg != null) 
					{
						m_AlertDlg.cancel();
						m_AlertDlg = null;
					}
					
					m_AlertDlg = new AlertDialog.Builder(this)
					.setMessage("Called party address empty")
					.setTitle("OpenComm")
					.setIcon(R.drawable.icon22)
					.setCancelable(false)
					.show();

					try
					{
						m_AlertDlg.wait(10);	
					}
					catch (Exception e)
					{
						
					};;	
				
					/*
					m_AlertDlg = (AlertDialog) showAlert("OpenComm",
							R.drawable.icon22, "Called party address empty", "Ok",
							true);
							*/
				}
			}
			break;
			
		case HANG_UP_MENU_ITEM:
			m_SipdroidEngine.rejectcall();
			
			break;
		
		case REGISTER_MENU_ITEM:
		{
			m_SipdroidEngine.register(-1);
		}
			break;
			
		case DEREGISTER_MENU_ITEM:
		{
			m_SipdroidEngine.unregister();
		}
			break;

		case CONFIGURE_MENU_USER_INFO_MENU_ITEM: {
			try {
				intent = new Intent(this, UserInfo.class);

				intent.putExtra("UserName", m_SipdroidEngine
						.getSipdroidConfig().getUserInfoData().getName());
				intent.putExtra("Email", m_SipdroidEngine.getSipdroidConfig()
						.getUserInfoData().getEmail());
				intent.putExtra("Location", m_SipdroidEngine
						.getSipdroidConfig().getUserInfoData().getLocation());
				intent.putExtra("Comments", m_SipdroidEngine
						.getSipdroidConfig().getUserInfoData().getComments());

				startActivityForResult(intent, CONFIG_USER_INFO);
			} catch (ActivityNotFoundException e) {
				
				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Problem launching configuration interface.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();

				try
				{
					m_AlertDlg.wait(10);	
				}
				catch (Exception e1)
				{
					
				};
				
				/*
				showAlert("Error", R.drawable.icon22,
						"Problem launching configuration interface.", "Ok",
						true);
						*/
			}
		}
			break;

		case CONFIGURE_MENU_USER_PROFILE_MENU_ITEM: {
			try {
				intent = new Intent(this, UserProfile.class);

				intent
						.putExtra("UserName", m_SipdroidEngine
								.getSipdroidConfig().getUserProfileData()
								.getUserName());
				intent
						.putExtra("Password", m_SipdroidEngine
								.getSipdroidConfig().getUserProfileData()
								.getPassword());
				
				intent
				.putExtra("LocalIP", m_SipdroidEngine
						.getSipdroidConfig().getUserProfileData()
						.getLocalIP());
				
				intent.putExtra("LocalPort", m_SipdroidEngine
						.getSipdroidConfig().getUserProfileData()
						.getLocalPort());
				intent.putExtra("Transport", m_SipdroidEngine
						.getSipdroidConfig().getUserProfileData()
						.getTransport());

				startActivityForResult(intent, CONFIG_USER_PROFILE);
			} catch (ActivityNotFoundException e) {
				
				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Problem launching configuration interface.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();

				try
				{
					m_AlertDlg.wait(10);	
				}
				catch (Exception e2)
				{
					
				};

				/*
				showAlert("Error", R.drawable.icon22,
						"Problem launching configuration interface.", "Ok",
						true);
						*/
			}
		}
			break;

		case CONFIGURE_MENU_PROXY_SETTINGS_MENU_ITEM: {
			try {
				intent = new Intent(this, ProxyRegistration.class);

				intent.putExtra("Domain", m_SipdroidEngine.getSipdroidConfig()
						.getProxyRegistrationData().getDomainOrRealm());
				intent.putExtra("UseOutboundProxy", m_SipdroidEngine
						.getSipdroidConfig().getProxyRegistrationData()
						.isUseProxy());
				intent.putExtra("ProxyHost", m_SipdroidEngine
						.getSipdroidConfig().getProxyRegistrationData()
						.getProxyURI().getHost());
				intent.putExtra("ProxyPort", m_SipdroidEngine
						.getSipdroidConfig().getProxyRegistrationData()
						.getProxyURI().getPort());
				intent.putExtra("RegisterOnStart", m_SipdroidEngine
						.getSipdroidConfig().getProxyRegistrationData()
						.isRegisterOnStart());
				intent.putExtra("SuggestedExpTime", m_SipdroidEngine
						.getSipdroidConfig().getProxyRegistrationData()
						.getSuggestedRegistrationExpDur());
				intent.putExtra("RegistrarHost", m_SipdroidEngine
						.getSipdroidConfig().getProxyRegistrationData()
						.getRegistrar().getHost());
				intent.putExtra("RegistrarPort", m_SipdroidEngine
						.getSipdroidConfig().getProxyRegistrationData()
						.getRegistrar().getPort());

				startActivityForResult(intent, CONFIG_PROXY_REG);
			} catch (ActivityNotFoundException e) {
				
				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Problem launching configuration interface.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();

				try
				{
					m_AlertDlg.wait(10);	
				}
				catch (Exception e3)
				{
					
				};

				
				/*
				showAlert("Error", R.drawable.icon22,
						"Problem launching configuration interface.", "Ok",
						true);
						*/
			}
		}
			break;
		case CONFIGURE_MENU_CALL_SETTINGS_MENU_ITEM: {
			try {
				intent = new Intent(this, CallOptions.class);

				intent.putExtra("AutoAnswer", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.isAutoAnswer());
				intent.putExtra("AutoIgnore", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.getAutoIgnore().isAutoIgnore());
				intent.putExtra("AutoIgnoreTimeOut", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.getAutoIgnore().getAfterDuration());
				intent.putExtra("DoNotDisturb", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.isDoNotDisturbMode());
				intent.putExtra("BlockCallerID", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.isBlockCallerId());
				intent.putExtra("NATRefresh", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.getNATRefreshParams().isUseNATRefresh());
				intent.putExtra("NATRefreshType", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.getNATRefreshParams().getNATRefreshType());
				intent.putExtra("NATRefreshTimeout", m_SipdroidEngine
						.getSipdroidConfig().getCallOptionsData()
						.getNATRefreshParams().getNATTimeOut());

				startActivityForResult(intent, CONFIG_CALL_OPTIONS);
			} catch (ActivityNotFoundException e) {
				
				m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage("Problem launching configuration interface.")
				.setTitle("OpenComm")
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();

				try
				{
					m_AlertDlg.wait(10);	
				}
				catch (Exception e4)
				{
					
				};

				/*
				showAlert("Error", R.drawable.icon22,
						"Problem launching configuration interface.", "Ok",
						true);
						*/
			}
		}
			break;
		}

		return result;
	}
	
	
	private OnClickListener onAcceptListener = new OnClickListener() {
		public void onClick(DialogInterface dlg, int resp) {
			m_SipdroidEngine.answercall();
		}
	};

	private OnClickListener onRejectListener = new OnClickListener() {
		public void onClick(DialogInterface dlg, int resp) {
			m_SipdroidEngine.rejectcall();
		}
	};

	
	final Handler mIncomingCallHandler = new Handler();
	NameAddress callee;
	NameAddress caller;
	final Runnable mIncomingCallCallback = new Runnable()
	{
		public void run()
		{
			if (m_CallAlertDialog != null) {
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
			
			m_CallAlertDialog = new AlertDialog.Builder(getUIContext())
			.setMessage("Incoming call from "+ caller.toString() + "!")
			.setTitle("OpenComm")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.setPositiveButton("Answer", onAcceptListener)
			.setNegativeButton("Hangup", onRejectListener)
			.show();

			try
			{
				m_CallAlertDialog.wait(10);	
			}
			catch (Exception e)
			{
				
			}

			/*
			m_CallAlertDialog = (AlertDialog) getUIContext().showAlert("OpenComm", R.drawable.icon22,
					"Incoming call from "+ caller.toString() + "!", "Answer", onAcceptListener,"Hangup", onRejectListener, false, null);
					*/
		}
	};
	
	
	/** When a new call is incoming */
	public void onUaCallIncoming(UserAgent ua, NameAddress callee,
			NameAddress caller) {
		this.callee = callee;
		this.caller = caller;
		mIncomingCallHandler.post(mIncomingCallCallback);
		
	}
	
	private OnClickListener OnCancelListener = new OnClickListener() {
		public void onClick(DialogInterface dlg, int resp) {
		m_SipdroidEngine.rejectcall();
		}
	};
	
	final Runnable mRingingCallback = new Runnable()
	{
		public void run()
		{
			if (m_CallAlertDialog != null) {
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
			
			m_CallAlertDialog = new AlertDialog.Builder(getUIContext())
			.setMessage("Ringing at peer")
			.setTitle("OpenComm")
			.setIcon(R.drawable.icon22)
			.setCancelable(false)
			.setNegativeButton("Hangup", onRejectListener)
			.show();

			try
			{
				m_CallAlertDialog.wait(10);	
			}
			catch (Exception e)
			{
				
			}
			
			/*
			m_CallAlertDialog = (AlertDialog) getUIContext().showAlert("Sipdroid", R.drawable.icon22,
					"Ringing at peer", "Hangup", OnCancelListener, false, null);
					*/
		}
	};

	
	final Handler mRingingHandler = new Handler();
	
	/** When an ougoing call is remotly ringing */
	public void onUaCallRinging(UserAgent ua) {
		mRingingHandler.post(mRingingCallback);
	}

	final Runnable mCancelCallback = new Runnable()
	{
		public void run()
		{
			if (m_CallAlertDialog != null) {
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
			
			m_CallAlertDialog = new AlertDialog.Builder(getUIContext())
			.setMessage("Call Cancelled")
			.setTitle("OpenComm")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();

			try
			{
				m_CallAlertDialog.wait(10);	
			}
			catch (Exception e)
			{
				
			};

			/*
			m_CallAlertDialog = (AlertDialog) getUIContext().showAlert("Sipdroid", R.drawable.icon22,
					"Call Cancelled", "Ok", true);
					*/
		}
	};

	
	final Handler mCancelHandler = new Handler();


	/** When an incoming call is cancelled */
	public void onUaCallCancelled(UserAgent ua) {
		mCancelHandler.post(mCancelCallback);
	}
	
	final Runnable mAcceptedCallback = new Runnable()
	{
		public void run()
		{
			if (m_CallAlertDialog != null) {
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
		}
	};

	
	final Handler mAcceptedHandler = new Handler();

	
	/** When an ougoing call has been accepted */
	public void onUaCallAccepted(UserAgent ua) {
		mAcceptedHandler.post(mAcceptedCallback);
	}

	final Runnable mTransferCallback = new Runnable()
	{
		public void run()
		{
			if (m_CallAlertDialog != null) {
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
			
			m_CallAlertDialog = new AlertDialog.Builder(getUIContext())
			.setMessage("Call Transfered")
			.setTitle("OpenComm")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();

			try
			{
				m_CallAlertDialog.wait(10);	
			}
			catch (Exception e)
			{
				
			};

			/*
			m_CallAlertDialog = (AlertDialog) getUIContext().showAlert(
					"OpenComm", R.drawable.icon22, "Call Transfered", "Ok", true);
					*/
		}
	};

	
	final Handler mTransferHandler = new Handler();

	/** When a call has been transferred */
	public void onUaCallTrasferred(UserAgent ua) {
		mTransferHandler.post(mTransferCallback);
	}
	
	final Runnable mFailedCallback = new Runnable()
	{
		public void run()
		{
			if (m_CallAlertDialog != null) {
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
			
			m_CallAlertDialog = new AlertDialog.Builder(getUIContext())
			.setMessage("Call Failed")
			.setTitle("OpenComm")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();

			try
			{
				m_CallAlertDialog.wait(10);	
			}
			catch (Exception e)
			{
				
			};

			
			/*
			m_CallAlertDialog = (AlertDialog) getUIContext().showAlert(
					"OpenComm", R.drawable.icon22, "Call Failed", "Ok", true);
					
					*/
		}
	};

	
	final Handler mFailedHandler = new Handler();


	/** When an ougoing call has been refused or timeout */
	public void onUaCallFailed(UserAgent ua) {
		mFailedHandler.post(mFailedCallback);
	}
	
	final Runnable mClosedCallback = new Runnable()
	{
		public void run()
		{
			if (m_CallAlertDialog != null) {
				m_CallAlertDialog.cancel();
				m_CallAlertDialog = null;
			}
			
			m_CallAlertDialog = new AlertDialog.Builder(getUIContext())
			.setMessage("Call Terminated")
			.setTitle("OpenComm")
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();

			try
			{
				m_CallAlertDialog.wait(10);	
			}
			catch (Exception e)
			{
				
			};


			
			/*
			m_CallAlertDialog = (AlertDialog) getUIContext().showAlert(
					"OpenComm", R.drawable.icon22, "Call Terminated", "Ok", true);
					*/
		}
	};

	
	final Handler mClosedHandler = new Handler();


	/** When a call has been locally or remotly closed */
	public void onUaCallClosed(UserAgent ua) {
		if (m_CallAlertDialog != null) {
			m_CallAlertDialog.cancel();
			m_CallAlertDialog = null;
		}
		
		mClosedHandler.post(mClosedCallback);
	}
}