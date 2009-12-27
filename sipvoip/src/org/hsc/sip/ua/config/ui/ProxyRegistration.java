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

package org.hsc.sip.ua.config.ui;

import org.hsc.sip.ua.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class ProxyRegistration extends Activity {

	ImageView background_image_view = null;
	EditText m_DomainOrRealm = null;
	CheckBox m_UseOutboundProxy = null;
	EditText m_ProxyHost = null;
	CheckBox m_RegisterOnStart = null;
	EditText m_SuggExpiryTime = null;
	EditText m_Registrar = null;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView(R.layout.proxy_registration);

		background_image_view = (ImageView) findViewById(R.id.background_image);
		if (background_image_view != null) {
			background_image_view.setImageResource(R.drawable.options_backend);
		}

		m_DomainOrRealm = (EditText) findViewById(R.id.txt_nw_opts_domain_realm);
		m_UseOutboundProxy = (CheckBox) findViewById(R.id.chk_nw_opts_use_proxy);
		m_ProxyHost = (EditText) findViewById(R.id.txt_nw_opts_proxy_address);
		m_RegisterOnStart = (CheckBox) findViewById(R.id.chk_nw_opts_reg_on_start);
		m_SuggExpiryTime = (EditText) findViewById(R.id.txt_nw_opts_sugg_exp_time);
		m_Registrar = (EditText) findViewById(R.id.txt_nw_opts_registrar);

		Intent intent = this.getIntent();

		String lDomain = intent.getStringExtra("Domain");
		if (lDomain.contains(":-1")) {
			// So if the default port for the domain is not specified, it will
			// be
			// stored as -1, we must not display it to the user
			lDomain = lDomain.substring(0, lDomain.indexOf(":-1"));
		} else if (lDomain.contains(":0")) {
			// So if the default port for the domain is not specified, it will
			// be
			// stored as 0, we must not display it to the user
			lDomain = lDomain.substring(0, lDomain.indexOf(":0"));
		}

		m_DomainOrRealm.setText(lDomain);

		m_UseOutboundProxy.setChecked((intent.getBooleanExtra(
				"UseOutboundProxy", true)));

		String proxy = intent.getStringExtra("ProxyHost");

		if (intent.getIntExtra("ProxyPort", 0) > 0) {
			proxy += (":" + intent.getIntExtra("ProxyPort", 0));
		}
		m_ProxyHost.setText(proxy);
		m_RegisterOnStart.setChecked((intent.getBooleanExtra("RegisterOnStart",
				true)));

		short lSuggestedExpiryTime = intent.getShortExtra("SuggestedExpTime",
				(short) 0);

		if (lSuggestedExpiryTime < 0) {
			lSuggestedExpiryTime = 3600;
		}
		m_SuggExpiryTime.setText(Short.toString(lSuggestedExpiryTime));

		String registrar = intent.getStringExtra("RegistrarHost");

		if (intent.getIntExtra("RegistrarPort", 0) > 0) {
			registrar += (":" + intent.getIntExtra("RegistrarPort", 0));
		}
		m_Registrar.setText(registrar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuItem m = menu.add(0, Menu.FIRST, 0, "Save");
		m.setIcon(R.drawable.save);
		m = menu.add(0, Menu.FIRST + 1, 0, "Back");
		m.setIcon(R.drawable.goback);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		boolean result = super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case Menu.FIRST:

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			
			bundle.putString("Domain", m_DomainOrRealm.getText().toString());
			bundle.putBoolean("UseOutboundProxy", m_UseOutboundProxy
					.isChecked());
			String proxy = m_ProxyHost.getText().toString();
			if (proxy.length() > 0) {
				if (proxy.indexOf(':') != -1)
				{
					String proxy_host = proxy.substring(0, proxy.indexOf(':') );
					bundle.putString("ProxyHost", proxy_host);
					String proxy_port = proxy.substring(proxy.indexOf(':') + 1);
					bundle.putInt("ProxyPort", Integer.parseInt("0" + proxy_port));
				}
				else
				{
					bundle.putString("ProxyHost", proxy);
					bundle.putInt("ProxyPort", 0);
				}
			} else {
				bundle.putString("ProxyHost", "");
				bundle.putInt("ProxyPort", 0);
			}

			bundle.putBoolean("RegisterOnStart", m_RegisterOnStart.isChecked());
			bundle.putShort("SuggestedExpTime", Short
					.parseShort(m_SuggExpiryTime.getText().toString()));

			String registrar = m_Registrar.getText().toString();
			if (registrar.length() > 0) {
				
				if (registrar.indexOf(':') != -1)
				{
					String registrar_host = registrar.substring(0, registrar
						.indexOf(':') );
				
					bundle.putString("RegistrarHost", registrar_host);
					String registrar_port = registrar
						.substring(registrar.indexOf(':') + 1);
					bundle.putInt("RegistrarPort",  Integer
								.parseInt("0" + registrar_port));
				}
				else
				{
					bundle.putString("RegistrarHost", registrar);
					bundle.putInt("RegistrarPort", 0);
				}
			} else {
				bundle.putString("RegistrarHost", "");
				bundle.putInt("RegistrarPort", 0);
			}

			intent.putExtras(bundle);
			setResult(1,  intent);
			finish();
			break;

		case (Menu.FIRST + 1):
			setResult(0);
			finish();
		}

		return result;
	}
}
