package ss.kakapo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/** an instance of this class is a sound source.
 * It can be manipulated for sound spatialization;
 * Interaural time difference (ITD) and volume difference (vol)
 * is updated with every update of the source's position */
public class AudioSS {
	private AudioTrack audio; // this source's audio track
	private int region; // region of the sound source
	private int itd; // interaural time delay in bytes
	private float[] vol; // volume of sound source
	private boolean[] pspace; // whether this user is in the specific pspace
	private static final String TAG = "AudioSS";
	private static final int NoPSPACE = 4; // number of private spaces
	private static final int BUFFER_SIZE = 1484 * 2; // minimum buffer size for a stereo, 8000kHz, 16-bit sound = 1484
	
	/** = buffer size */
	public static int getBufferSize() {
		return AudioSS.BUFFER_SIZE;
	} // end getBufferSize method
	/** = volume */
	public float[] getVolume() {
		return vol;
	} // end getVolume method
	
	/** private:: set region of sound source based on position */
	private void setRegion(int x, int y) {
		x = x * 100/800; // find 
		y = y * 100/480; 
		// region 1: (x, y) = (0 - 20% inclusive, 0 - 25% inclusive)
		if ((x >= 0) && (x <= 20) && (y >= 0) && (y <= 25)) {
			this.region = 1;
		}
		// region 2: (x, y) = (20%exclusive - 40%, 0 - 25% inclusive)
		if ((x > 20) && (x <= 40) && (y >= 0) && (y <= 25)) {
			this.region = 2;
		}
		// region 3: (x, y) = (40%exclusive - 60%, 0 - 25% inclusive)
		if ((x > 40) && (x <= 60) && (y >= 0) && (y <= 25)) {
			this.region = 3;
		}
		// region 4: (x, y) = (60%exclusive - 80%, 0 - 25% inclusive)
		if ((x > 60) && (x <= 80) && (y >= 0) && (y <= 25)) {
			this.region = 4;
		}
		// region 5: (x, y) = (80%exclusive - 100%, 0 - 25% inclusive)
		if ((x > 80) && (x <= 100) && (y >= 0) && (y <= 25)) {
			this.region = 5;
		}
		// region 6: (x, y) = (0 - 20% inclusive, 25%exclusive - 50% inclusive)
		if ((x >= 0) && (x <= 20) && (y > 25) && (y <= 50)) {
			this.region = 6;
		}
		// region 7: (x, y) = (20%exclusive - 40%, 25%exclusive - 50% inclusive)
		if ((x > 20) && (x <= 40) && (y > 25) && (y <= 50)) {
			this.region = 7;
		}
		// region 8: (x, y) = (40%exclusive - 60%, 25%exclusive - 50% inclusive)
		if ((x > 40) && (x <= 60) && (y > 25) && (y <= 50)) {
			this.region = 8;
		}
		// region 9: (x, y) = (60%exclusive - 80%, 25%exclusive - 50% inclusive)
		if ((x > 60) && (x <= 80) && (y > 25) && (y <= 50)) {
			this.region = 9;
		}
		// region 10: (x, y) = (80%exclusive - 100%, 25%exclusive - 50% inclusive)
		if ((x > 80) && (x <= 100) && (y > 25) && (y <= 50)) {
			this.region = 10;
		}
		// region 11: (x, y) = (0 - 20% inclusive, 50%exclusive - 75% inclusive)
		if ((x >= 0) && (x <= 20) && (y > 50) && (y <= 75)) {
			this.region = 11;
		}
		// region 12: (x, y) = (20%exclusive - 40%, 25%exclusive - 50% inclusive)
		if ((x > 20) && (x <= 40) && (y > 50) && (y <= 75)) {
			this.region = 12;
		}
		// region 13: (x, y) = (40%exclusive - 60%, 25%exclusive - 50% inclusive)
		if ((x > 40) && (x <= 60) && (y > 50) && (y <= 75)) {
			this.region = 13;
		}
		// region 14: (x, y) = (60%exclusive - 80%, 25%exclusive - 50% inclusive)
		if ((x > 60) && (x <= 80) && (y > 50) && (y <= 75)) {
			this.region = 14;
		}
		// region 15: (x, y) = (80%exclusive - 100%, 25%exclusive - 50% inclusive)
		if ((x > 80) && (x <= 100) && (y > 50) && (y <= 75)) {
			this.region = 15;
		}
		// region 16: (x, y) = (0 - 20% inclusive, 75%exclusive - 100% inclusive)
		if ((x >= 0) && (x <= 20) && (y > 75) && (y <= 100)) {
			this.region = 16;
		}
		// region 17: (x, y) = (20%exclusive - 40%, 25%exclusive - 50% inclusive)
		if ((x > 20) && (x <= 40) && (y > 75) && (y <= 100)) {
			this.region = 17;
		}
		// region 18: (x, y) = (40%exclusive - 60%, 25%exclusive - 50% inclusive)
		if ((x > 40) && (x <= 60) && (y > 75) && (y <= 100)) {
			this.region = 18;
		}
		// region 19: (x, y) = (60%exclusive - 80%, 25%exclusive - 50% inclusive)
		if ((x > 60) && (x <= 80) && (y > 75) && (y <= 100)) {
			this.region = 19;
		}
		// region 20: (x, y) = (80%exclusive - 100%, 25%exclusive - 50% inclusive)
		if ((x > 80) && (x <= 100) && (y > 75) && (y <= 100)) {
			this.region = 20;
		}
	} // end setRegion method

	/** private:: set interaural time delay (bytes) based on the region of sound source */
	private void setSS() {
		switch (this.region) {
			case 1: 
				this.itd = 104;
				this.vol[0] = 0.1043f;
				this.vol[1] = 0.3117f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 1");
				break;
			case 2: 
				this.itd = 60;
				this.vol[0] = 0.2664f;
				this.vol[1] = 0.3852f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 2");
				break;	
			case 3: 
				this.itd = 0;
				this.vol[0] = 0.37f;
				this.vol[1] = 0.37f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 3");
				break;
			case 4: 
				this.itd = -60;
				this.vol[0] = 0.3852f;
				this.vol[1] = 0.2664f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 4");
				break;
			case 5: 
				this.itd = -104;
				this.vol[0] = 0.3117f;
				this.vol[1] = 0.1043f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 5");
				break;
			case 6: 
				this.itd = 128;
				this.vol[0] = 0.2144f;
				this.vol[1] = 0.4697f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 6");
				break;
			case 7: 
				this.itd = 80;
				this.vol[0] = 0.4108f;
				this.vol[1] = 0.5692f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 7");
				break;	
			case 8: 
				this.itd = 0;
				this.vol[0] = 0.55f;
				this.vol[1] = 0.55f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 8");
				break;
			case 9: 
				this.itd = -80;
				this.vol[0] = 0.5692f;
				this.vol[1] = 0.4108f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 9");
				break;
			case 10: 
				this.itd = -128;
				this.vol[0] = 0.4697f;
				this.vol[1] = 0.2144f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 10");
				break;
			case 11: 
				this.itd = 158;
				this.vol[0] = 0.2901f;
				this.vol[1] = 0.6085f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 11");
				break;
			case 12: 
				this.itd = 114;
				this.vol[0] = 0.5240f;
				this.vol[1] = 0.75351f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 12");
				break;	
			case 13: 
				this.itd = 0;
				this.vol[0] = 0.73f;
				this.vol[1] = 0.73f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 13");
				break;
			case 14: 
				this.itd = 114;
				this.vol[0] = 0.7535f;
				this.vol[1] = 0.5240f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 14");
				break;
			case 15: 
				this.itd = 158;
				this.vol[0] = 0.6085f;
				this.vol[1] = 0.2901f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 15");
				break;
			case 16: 
				this.itd = 194;
				this.vol[0] = 0.3162f;
				this.vol[1] = 0.7070f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 16");
				break;
			case 17: 
				this.itd = 176;
				this.vol[0] = 0.5664f;
				this.vol[1] = 0.9209f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 17");
				break;	
			case 18: 
				this.itd = 0;
				this.vol[0] = 0.91f;
				this.vol[1] = 0.91f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 18");
				break;
			case 19: 
				this.itd = -176;
				this.vol[0] = 0.9209f;
				this.vol[1] = 0.5664f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 19");
				break;
			case 20: 
				this.itd = -194;
				this.vol[0] = 0.7070f;
				this.vol[1] = 0.3162f;
				this.audio.setStereoVolume(this.vol[0], this.vol[1]);
				Log.i(AudioSS.TAG, "ITD and Vol set for region 20");
				break;
		}
	} // end setITD method

	/** = private:: a byte array with the itd added before the source */
	private byte[] addITDBefore(byte[] source) {
		int itdVal = Math.abs(this.itd);
		int len = source.length + itdVal;
		// create new channel
		byte[] added = new byte[len];
		// add silence before source
		for (int i = 0; i < itdVal; i++) {
			added[i] = 0; // value of silence in signed 16bit: 0
			Log.i(AudioSS.TAG, "Silence added before source");
		}
		// add source
		for (int i = 0; i < source.length; i++) {
			added[i+itdVal] = source[i];
			Log.i(AudioSS.TAG, "Source modified with ITD before source");
		}
		return added;
	} // end addITDBefore method
	
	/** = private:: a byte array with the itd added after the source */
	private byte[] addITDAfter(byte[] source) {
		int itdVal = Math.abs(this.itd);
		int len = source.length + itdVal;
		// create new channel
		byte[] added = new byte[len];
		// add silence after source
		for (int i = 0; i < itdVal; i++) {
			added[i + source.length] = 0; // value of silence in signed 16bit: 0
			Log.i(AudioSS.TAG, "Silence added after source");
		}
		// add source
		for (int i = 0; i < source.length; i++) {
			added[i] = source[i];
			Log.i(AudioSS.TAG, "Source modified with ITD after source");
		}
		return added;
	} // end addITDBefore method
	
	/** = private:: stereo stream created from two mono streams left and right */
	private static byte[] monoToStereo(byte[] left, byte[] right) {
		// check that both left and right mono streams are of equal length
		if (left.length != right.length) {
			Log.e(AudioSS.TAG, "Left and right mono streams are not equal in length!");
			return null;
		}
		byte[] output = new byte[left.length * 2];
		// put mono channels into respective left and right channel
		for (int i = 0; i < left.length; i++) {
			output[(i * 2)] = left[i]; // put mono channel into left stereo channel
			output[(i * 2) + 1] = right[i]; // put mono channel into right stereo channel
		}
		Log.i(AudioSS.TAG, "2 mono streams inserted into 1 stereo stream");
		return output;
	} // end monoToStereo method
	
	/** update region, ITD, and vol based on new position */
	public void moveTo(int nx, int ny) {
		// update region
		this.setRegion(nx, ny);
		// update ITD and volume
		this.setSS();
		Log.i(AudioSS.TAG, "SS values updated");
	} // end moveTo method
	
	/** change volume of the audiotrack */
	public void setStereoVolume(float left, float right) {
		this.audio.setStereoVolume(left, right);
	} // end setStereoVolume
	/** enter this user into private space no. n (#1 - 4) */
	public void enterPSpace(int n) {
		// if it's already in pspace no.n
		if (this.pspace[n]) {
			Log.e(AudioSS.TAG, "This user is already in pspace no." + n);
			return;
		}
		else {
			// set pspace involvement as true
			this.pspace[n] = true;
			Log.i(AudioSS.TAG, "This user has entered pspace no." + n);
		}
	} // end enterPSpace method
	
	/** remove this user from private space no. n */
	public void leavePSpace(int n) {
		// if it's already in pspace no.n
		if (!this.pspace[n]) {
			Log.e(AudioSS.TAG, "This user isn't in pspace no." + n);
			return;
		}
		else {
			// set pspace involvement as true
			this.pspace[n] = true;
			Log.i(AudioSS.TAG, "This user has left pspace no." + n);
		}
	} // end leavePSpace method
	
	/** change volume to PSpace no. n mode;
	 * if the user is in the pspace, keep volume at full level;
	 * if the user isn't, lower volume by half */
	public void enterPSpaceMode(int n) {
		// if the user is part of the pspace
		if (this.pspace[n]) {
			this.audio.setStereoVolume(this.vol[0], this.vol[1]);
		}
		// if the user is not part of the pspace
		else {
			this.audio.setStereoVolume((this.vol[0]/2.0f), (this.vol[1]/2.0f));
		}
	} // end enterPSpaceMode method
	
	/** change volume at full conference mode */
	public void enterFullConference() {
		this.audio.setStereoVolume(this.vol[0], this.vol[1]);
	} // end enterFullConference method
	
	/** play the track */
	public void play() {
		this.audio.play();
	}
	
	/** write a SS-modified source to the track */
	public void write(byte[] source) {
		byte[] left = new byte[0];
		byte[] right = new byte[0];
		// if the sound source is left of the user
		if (this.itd > 0) {
			left = this.addITDAfter(source);
			right = this.addITDBefore(source);
		}
		// if the sound source is right of the user
		else if (this.itd < 0) {
			left = this.addITDAfter(source);
			right = this.addITDBefore(source);
		}
		// if the sound source is right in front of the user; no itd
		else {
			left = source.clone();
			right = source.clone();
		}
		Log.i(AudioSS.TAG, "Left and right mono streams have been modified.");
		// create stereo stream
		byte[] stereo = AudioSS.monoToStereo(left, right);
		// write the stereo channel into the audiotrack
		this.audio.write(stereo, 0, stereo.length);
	} // end write method
	
	/** Constructor: a new instance of AudioSS with AudioTrack input as well as
	 * initial position (px, py) in pixels;
	 * the region of the user and ITD/vol based on the initial position is updated;
	 * it is assumed that the user is not in any private space */
	public AudioSS(int px, int py) {
		// initialize everything
		this.region = 0;
		this.itd = 0;
		this.vol = new float[2];
		this.pspace = new boolean[AudioSS.NoPSPACE];
		this.audio = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				AudioSS.BUFFER_SIZE * 2 * 2, AudioTrack.MODE_STREAM);
		// set region based on coordinates
		this.setRegion(px, py);
		// set itd and vol based on coordinates
		this.setSS();
		Log.i(AudioSS.TAG, "AudioSS constructed for region " + this.region + ", ITD: " + this.itd + ", Volume: " + this.vol[0] + ", " + this.vol[1]);
	} // end AudioSS method
} // end class AudioSS

