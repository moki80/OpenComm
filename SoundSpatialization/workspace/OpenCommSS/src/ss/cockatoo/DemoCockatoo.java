package ss.cockatoo;

import android.app.Activity;
import android.media.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class DemoCockatoo extends Activity {
	/** Called when the activity is first created. */
    AudioPlayFile track;
    byte[] music;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        int bs1 = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int bs = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        int bs2 = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int bs3 = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
     
        Log.i("Hello", "Min Buffer Size is " + bs1 + "; " + bs + "; " + bs2 + "; " + bs3);
        final ToggleButton control = (ToggleButton) findViewById(R.id.control);
		final Button one = (Button) findViewById(R.id.one);
		final Button two = (Button) findViewById(R.id.two);
		final Button three = (Button) findViewById(R.id.three);
		final Button four = (Button) findViewById(R.id.four);
		final Button five = (Button) findViewById(R.id.five);
		final Button six = (Button) findViewById(R.id.six);
		final Button seven = (Button) findViewById(R.id.seven);
		final Button eight = (Button) findViewById(R.id.eight);
		control.setChecked(true);
		/** new Thread(new Runnable() {
        	public void run() {
        		track = new AudioPlayFile(getResources().openRawResource(R.raw.testone), 400, 480);
        	}
        }).start(); */


		
		control.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// play the music if it's on
        		if (control.isChecked()) {
        			track.play();
        		}
        		// if it's off
        		else {
        			track.pause();
        		}
        	} // end onClick method
	        });
	      one.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(25, 25);
	        	}
	        });
	        two.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(400, 25);
	        	}
	        });
	        three.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(775, 25);
	        	}
	        });
	        four.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(25, 240);
	        	}
	        });
	        five.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(400, 240);
	        	}
	        });
	        six.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(775, 240);
	        	}
	        });
	        seven.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(25, 480);
	        	}
	        });
	        eight.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		track.setPosition(775, 480);
	        	}
	        });
	}
} // end class DemoCockatoo