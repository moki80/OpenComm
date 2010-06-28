package com.opencomm.uifirst;

import android.content.Context;
import android.widget.ToggleButton;
import android.widget.ImageView;

public class ImageToggleButton extends ToggleButton {

	public ImageToggleButton(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

	private final ImageView imageTB = new ImageView(null) {
	// Class2 "view" of this class, this inner class
	// has access to all fields/methods of YourClass
	};

	public ImageView asClass2() {
		return imageTB;
	}


}