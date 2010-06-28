package com.opencomm.uifirst;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ToggleButton;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return mThumbIds[position];
    }
    
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ToggleButton imageButton;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	imageButton = new ToggleButton(mContext);
        	imageButton.setLayoutParams(new GridView.LayoutParams(110, 110));
        	/**imageButton.setScaleType(ToggleButton.ScaleType.CENTER_CROP);
        	imageButton.setPadding(8, 8, 8, 8);*/
        } else {
        	imageButton = (ToggleButton) convertView;
        }
        
        imageButton.setBackgroundDrawable(null);
        imageButton.setButtonDrawable(mThumbIds[position]);
        return imageButton;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.user1, R.drawable.user2,
            R.drawable.user3, R.drawable.user4,
            R.drawable.user5, R.drawable.user6,
            R.drawable.user7, R.drawable.user8,
            R.drawable.user9, R.drawable.user10,
            R.drawable.user11, R.drawable.user12,
            R.drawable.user13, R.drawable.user14,
            R.drawable.user15, R.drawable.user16,
            R.drawable.user17, R.drawable.user18,
            R.drawable.icon, R.drawable.logo_oc
    };
}