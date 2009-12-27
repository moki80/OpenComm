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
import android.widget.EditText;
import android.widget.ImageView;

public class UserInfo extends Activity {

	ImageView background_image_view = null;

	EditText m_UserName;
	EditText m_Location;
	EditText m_Email;
	EditText m_Comments;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		this.setContentView(R.layout.user_info_layout);

		background_image_view = (ImageView) findViewById(R.id.background_image);
		if (background_image_view != null) {
			background_image_view.setImageResource(R.drawable.options_backend);
		}

		m_UserName = (EditText) findViewById(R.id.txt_user_name);
		m_Email = (EditText) findViewById(R.id.txt_email_address);
		m_Location = (EditText) findViewById(R.id.txt_location);
		m_Comments = (EditText) findViewById(R.id.txt_comments);

		Intent intent = this.getIntent();

		m_UserName.setText(intent.getStringExtra("UserName"));
		m_Email.setText(intent.getStringExtra("Email"));
		m_Location.setText(intent.getStringExtra("Location"));
		m_Comments.setText(intent.getStringExtra("Comments"));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean result = super.onCreateOptionsMenu(menu);

		MenuItem m = menu.add(0, Menu.FIRST, 0,"Save");
		m.setIcon(R.drawable.save);
		m = menu.add(0, Menu.FIRST + 1, 0, "Back");
		m.setIcon(R.drawable.goback);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		boolean result = super.onOptionsItemSelected(item);

		int itemResult = item.getItemId();
		switch (itemResult) {
		case Menu.FIRST:

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("UserName", m_UserName.getText().toString());
			bundle.putString("Email", m_Email.getText().toString());
			bundle.putString("Location", m_Location.getText().toString());
			bundle.putString("Comments", m_Comments.getText().toString());

			intent.putExtras(bundle);
			setResult(1, intent);
			finish();
			break;
		case (Menu.FIRST + 1):
			setResult(0);
			finish();
		}

		return result;
	}

}
