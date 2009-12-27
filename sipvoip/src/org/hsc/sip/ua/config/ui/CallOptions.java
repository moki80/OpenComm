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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

// Implements the UI for Call Options
public class CallOptions extends Activity {
	ImageView background_image_view = null;

	CheckBox m_AutoAnswer = null;
	CheckBox m_AutoIgnore = null;
	EditText m_AutoIgnoreTimeout = null;
	CheckBox m_DoNotDisturb = null;
	CheckBox m_BlockCallerId = null;
	CheckBox m_UseNATRefresh = null;
	Spinner m_NATRefreshType = null;
	EditText m_NATRefreshTimeout = null;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView(R.layout.call_options);

		background_image_view = (ImageView) findViewById(R.id.background_image);
		if (background_image_view != null) {
			background_image_view.setImageResource(R.drawable.options_backend);
		}

		m_AutoAnswer = (CheckBox) findViewById(R.id.chk_call_opt_auto_accept);
		m_AutoIgnore = (CheckBox) findViewById(R.id.chk_call_opt_auto_ignore);
		m_AutoIgnoreTimeout = (EditText) findViewById(R.id.txt_call_opt_ing_tmr);
		m_DoNotDisturb = (CheckBox) findViewById(R.id.chk_call_opt_dnd);
		m_BlockCallerId = (CheckBox) findViewById(R.id.chk_call_opt_block_caller_id);
		m_UseNATRefresh = (CheckBox) findViewById(R.id.chk_use_nat_refresh);
		m_NATRefreshType = (Spinner) findViewById(R.id.spn_call_opt_nat_ref_tech);
		m_NATRefreshTimeout = (EditText) findViewById(R.id.txt_call_opts_nat_ref_timeout);

		Intent intent = this.getIntent();

		m_AutoAnswer.setChecked(intent.getBooleanExtra("AutoAnswer", false));
		m_AutoIgnore.setChecked(intent.getBooleanExtra("AutoIgnore", false));

		short lAutoIgnoreTimeOut = intent.getShortExtra("AutoIgnoreTimeOut",
				(short) 0);
		if (lAutoIgnoreTimeOut < 0) {
			lAutoIgnoreTimeOut = 0;
			m_AutoIgnore.setChecked(false);
		}
		m_AutoIgnoreTimeout.setText(Short.toString(lAutoIgnoreTimeOut));
		m_DoNotDisturb
				.setChecked(intent.getBooleanExtra("DoNotDisturb", false));
		m_BlockCallerId.setChecked(intent.getBooleanExtra("BlockCallerID",
				false));
		m_UseNATRefresh.setChecked(intent.getBooleanExtra("NATRefresh", false));

		short lNATRefreshTimeOut = intent.getShortExtra("NATRefreshTimeout",
				(short) 0);

		if (lNATRefreshTimeOut < 0) {
			lNATRefreshTimeOut = 0;
			m_UseNATRefresh.setChecked(false);
		}

		m_NATRefreshTimeout.setText(Short.toString(lNATRefreshTimeOut));

		String data[] = new String[] { "Options", "Dummy" };

		ArrayAdapter<String> RefreshTypeAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, data);

		RefreshTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_NATRefreshType.setAdapter(RefreshTypeAdapter);

		short lNATRefreshType = intent.getShortExtra("NATRefreshType",
				(short) 1);
		if (lNATRefreshType < 1 && lNATRefreshType > 2) {
			lNATRefreshType = 1;
		}
		m_NATRefreshType.setSelection(lNATRefreshType);
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

			bundle.putBoolean("AutoAnswer", m_AutoAnswer.isChecked());
			bundle.putBoolean("AutoIgnore", m_AutoIgnore.isChecked());
			bundle.putShort("AutoIgnoreTimeOut", Short
					.parseShort(m_AutoIgnoreTimeout.getText().toString()));
			bundle.putBoolean("DoNotDisturb", m_DoNotDisturb.isChecked());
			bundle.putBoolean("BlockCallerID", m_BlockCallerId.isChecked());
			bundle.putBoolean("NATRefresh", m_UseNATRefresh.isChecked());
			/*bundle.putByte("NATRefreshType", Byte.parseByte(m_NATRefreshType
					.getSelectedItem().toString())); */
			bundle.putShort("NATRefreshTimeout", Short
					.parseShort("0" + m_NATRefreshTimeout.getText().toString()));

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
