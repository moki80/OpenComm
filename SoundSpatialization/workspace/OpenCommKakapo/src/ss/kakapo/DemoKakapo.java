package ss.kakapo;

import android.app.Activity;
import android.os.Bundle;
import java.io.*;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

	/** Demonstrates the ability to switch between private space mode and full conference mode with 8 sounds */
public class DemoKakapo extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demomulti);
		// set toggle buttons
		final ToggleButton rOne = (ToggleButton) findViewById(R.id.one);
		final ToggleButton rThree = (ToggleButton) findViewById(R.id.three);
		final ToggleButton rFive = (ToggleButton) findViewById(R.id.five);
		final ToggleButton rSixteen = (ToggleButton) findViewById(R.id.sixteen);
		final ToggleButton rEighteen = (ToggleButton) findViewById(R.id.eighteen);
		final ToggleButton rTwenty = (ToggleButton) findViewById(R.id.twenty);
		Log.i("DemoKakapo", "ToggleButtons set");
		
		// create SS manipulation for each sound
		final AudioSS aOne = new AudioSS(80, 60); // region 1
		final AudioSS aThree = new AudioSS(400, 60); // region 3
		final AudioSS aFive = new AudioSS(720, 60); // region 5
		final AudioSS aSixteen = new AudioSS(80, 420); // region 16
		final AudioSS aEighteen = new AudioSS(400, 420); // region 18
		final AudioSS aTwenty = new AudioSS(720, 420); // region 20
		
		// play and write each sound source
		aOne.play();
		aThree.play();
		aFive.play();
		aSixteen.play();
		aEighteen.play();
		aTwenty.play();
		
		Log.i("DemoKakapo", "AudioSS set");
		/** byte mOne[] = new byte[1000];
		byte mThree[] = new byte[1000];
		byte mFive[] = new byte[1000];
		byte mSixteen[] = new byte[1000];
		byte mEighteen[] = new byte[1000];
		byte mTwenty[] = new byte[1000];
		// gain access to each sound source
		try {
			InputStream isOne = getResources().openRawResource(R.raw.lowc);
			Log.i("DemoKakapo", "lowc.raw input stream");
			BufferedInputStream bisOne = new BufferedInputStream(isOne);
			Log.i("DemoKakapo", "lowc.raw buffered input stream");
			DataInputStream disOne = new DataInputStream(bisOne);
			Log.i("DemoKakapo", "lowc.raw data input stream");
			InputStream isThree = getResources().openRawResource(R.raw.lowe);
			BufferedInputStream bisThree = new BufferedInputStream(isThree);
			DataInputStream disThree = new DataInputStream(bisThree);
			InputStream isFive = getResources().openRawResource(R.raw.lowg);
			BufferedInputStream bisFive = new BufferedInputStream(isFive);
			DataInputStream disFive = new DataInputStream(bisFive);
			InputStream isSixteen = getResources().openRawResource(R.raw.highc);
			BufferedInputStream bisSixteen = new BufferedInputStream(isSixteen);
			DataInputStream disSixteen = new DataInputStream(bisSixteen);
			InputStream isEighteen = getResources().openRawResource(R.raw.highe);
			BufferedInputStream bisEighteen = new BufferedInputStream(isEighteen);
			DataInputStream disEighteen = new DataInputStream(bisEighteen);
			InputStream isTwenty = getResources().openRawResource(R.raw.highg);
			BufferedInputStream bisTwenty = new BufferedInputStream(isTwenty);
			DataInputStream disTwenty = new DataInputStream(bisTwenty);

			// Read the file into the music array.
			for (int i = 0; i < 640000; i = i+1000) {
				disOne.read(mOne, i, 1000);
				Log.i("DemoKakapo", "lowc.raw read");
				aOne.write(mOne);
				disThree.read(mThree, i, 1000);
				aThree.write(mThree);
				disFive.read(mFive, i, 1000);
				aFive.write(mFive);
				disSixteen.read(mSixteen, i, 1000);
				aSixteen.write(mSixteen);
				disEighteen.read(mEighteen, i, 1000);
				aEighteen.write(mEighteen);
				disTwenty.read(mTwenty, i, 1000);
				aTwenty.write(mTwenty);
			}
		}
		catch (Exception e) {
			Log.e("DemoKakapo", "lowc.raw not read");
		}

		// set click listener
		rOne.setOnClickListener(new OnClickListener() {
			// if the region is marked as part of the private space
			public void onClick(View v) {
				// in private space
				if (rOne.isChecked()) {
					aOne.setStereoVolume(aOne.getVolume()[0], aOne.getVolume()[1]);
				}
				else {
					aOne.setStereoVolume(aOne.getVolume()[0]/2, aOne.getVolume()[1]/2);
				}
			} // end onClick method
		});
		
		rThree.setOnClickListener(new OnClickListener() {
			// if the region is marked as part of the private space
			public void onClick(View v) {
				// in private space
				if (rThree.isChecked()) {
					aThree.setStereoVolume(aThree.getVolume()[0], aThree.getVolume()[1]);
				}
				else {
					aThree.setStereoVolume(aThree.getVolume()[0]/2, aThree.getVolume()[1]/2);
				}
			} // end onClick method
		});
		
		rFive.setOnClickListener(new OnClickListener() {
			// if the region is marked as part of the private space
			public void onClick(View v) {
				// in private space
				if (rFive.isChecked()) {
					aFive.setStereoVolume(aFive.getVolume()[0], aFive.getVolume()[1]);
				}
				else {
					aFive.setStereoVolume(aFive.getVolume()[0]/2, aFive.getVolume()[1]/2);
				}
			} // end onClick method
		});
		
		rSixteen.setOnClickListener(new OnClickListener() {
			// if the region is marked as part of the private space
			public void onClick(View v) {
				// in private space
				if (rSixteen.isChecked()) {
					aSixteen.setStereoVolume(aSixteen.getVolume()[0], aSixteen.getVolume()[1]);
				}
				else {
					aSixteen.setStereoVolume(aSixteen.getVolume()[0]/2, aSixteen.getVolume()[1]/2);
				}
			} // end onClick method
		});

		rEighteen.setOnClickListener(new OnClickListener() {
			// if the region is marked as part of the private space
			public void onClick(View v) {
				// in private space
				if (rEighteen.isChecked()) {
					aEighteen.setStereoVolume(aEighteen.getVolume()[0], aEighteen.getVolume()[1]);
				}
				else {
					aEighteen.setStereoVolume(aEighteen.getVolume()[0]/2, aEighteen.getVolume()[1]/2);
				}
			} // end onClick method
		});

		rTwenty.setOnClickListener(new OnClickListener() {
			// if the region is marked as part of the private space
			public void onClick(View v) {
				// in private space
				if (rTwenty.isChecked()) {
					aTwenty.setStereoVolume(aTwenty.getVolume()[0], aTwenty.getVolume()[1]);
				}
				else {
					aTwenty.setStereoVolume(aTwenty.getVolume()[0]/2, aTwenty.getVolume()[1]/2);
				}
			} // end onClick method
		}); */
    } // end onCreate method
} // end class DemoKakapo