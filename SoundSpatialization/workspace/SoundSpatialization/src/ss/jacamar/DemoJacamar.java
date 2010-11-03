package ss.jacamar;

import java.io.*;

import ss.Jacamar.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class DemoJacamar extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final AudioPlayFile test = new AudioPlayFile(400, 480);
        test.write(prepFile("test.wav"));
        final ToggleButton control = (ToggleButton) findViewById(R.id.control);
        final Button one = (Button) findViewById(R.id.one);
        final Button two = (Button) findViewById(R.id.two);
        final Button three = (Button) findViewById(R.id.three);
        final Button four = (Button) findViewById(R.id.four);
        final Button five = (Button) findViewById(R.id.five);
        final Button six = (Button) findViewById(R.id.six);
        final Button seven = (Button) findViewById(R.id.seven);
        final Button eight = (Button) findViewById(R.id.eight);
        control.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// play the music if it's on
        		if (control.isChecked()) {
        			test.play();
        		}
        		// if it's off
        		else {
        			test.pause();
        		}
        	} // end onClick method
        });
        one.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(25, 25);
        	}
        });
        two.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(400, 25);
        	}
        });
        three.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(775, 25);
        	}
        });
        four.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(25, 240);
        	}
        });
        five.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(400, 240);
        	}
        });
        six.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(775, 240);
        	}
        });
        seven.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(25, 480);
        	}
        });
        eight.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		test.setPosition(775, 480);
        	}
        });
    }
    
    /** Prepare file "id" to read into AudioPlayFile */
    public byte[] prepFile(String id) {
    	// read file into array
    	File file = new File(id);
    	// get length of audio stored and create array to store recorded audio
    	int musicLength = (int)file.length();
    	byte[] music = new byte[musicLength];
    	// read audio data back from saved file
    	try {
    		InputStream is = new FileInputStream(file);
    		/** BufferedInputStream bis = new BufferedInputStream(is);
    		DataInputStream dis = new DataInputStream(bis);
    		// read file into music array
    		int i = 0;
    		while (dis.available() > 0) {
    			music[i] = dis.readByte();
    			i++;
    		}
    		// close input stream
    		dis.close(); */
    		is.read(music, 0, music.length);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return music;
    } // end prepFile method
} // end class DemoJacamar