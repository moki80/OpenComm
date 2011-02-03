/** An instance of this class spatializes the source based on the 
 * position of the source. radius of the head, # bytes per millisecond,
 * speed of sound, height and width of conference window is predetermined
 */
public class AudioSS {
	private int xPos; // source's x-coord relative to listener
	private int yPos; // source's y-coord relative to listener
	private float balance; // balance between the 2 stereo speakers; -1.0 (L) to 1.0 (R)
	private float gain; // overall volume
	private int itd; // interaural time delay in bytes
	private static int msToByte = 16; // # bytes per millisecond
	private static double headRad = 0.085; // radius of a head in meters
	private static double speedSound = 343.42; // speed of sound
	private static int height = 480; // height of conference screen
	private static int width = 800; // width of conference screen
	

	/** = source position's x-coord relative to listener */
	private static int getRelXPos(int x) {
		int rX = x - AudioSS.width/2;
		return rX;
	} // end getRelXPos method
	
	/** = source position's y-coord relative to listener */
	private static int getRelYPos(int y) {
		int rY = AudioSS.height - y;
		return rY;
	} // end getRelXPos method
	
	/** set the interaural time difference in bytes */
	private void setITD() {
		double d = Math.sqrt(Math.pow(this.xPos, 2) + Math.pow(this.yPos, 2));
		double theta = Math.PI/2 - Math.acos(this.xPos/d);
		// determine itd in milliseconds
		double itdMS = 1000 * AudioSS.headRad/AudioSS.speedSound * (theta + Math.sin(theta));
		int itdByte = (int) (itdMS * AudioSS.msToByte);
		// set this audioSS's itd to itdByte
		this.itd = itdByte;
	} // end getITD method
	
	/** calculate balance between the 2 speakers */
	private void setBalance() {
		// find distance difference between left and right ear in m
		double dd = (((double)this.itd)/AudioSS.msToByte/1000 * AudioSS.speedSound);
		// multiply dd by 4
		float bal = (float) (dd * 4);
		this.balance = bal;
	} // end calcBalance method
	
	/** set gain of the audio */
	private void setGain() {
		// find distance from source
		double d = Math.sqrt(Math.pow(this.xPos, 2) + Math.pow(this.yPos, 2));
		// divide distance by 40
		float gn = (float) (d / - 40);
		this.gain = gn;
	} // end getGain method
	
	/** set spatialization parameters */
	private void setSS() {
		this.setITD();
		this.setBalance();
		this.setGain();
	} // end setSS method
	
	/** = private:: a byte array with the itd added before the source */
	private byte[] addITDBefore(byte[] source) {
		int itdVal = Math.abs(this.itd);
		int len = source.length + itdVal;
		// create new channel
		byte[] added = new byte[len];
		// add silence before source
		for (int i = 0; i < itdVal; i++) {
			added[i] = 0; // value of silence in signed 16bit: 0
		}
		// add source
		for (int i = 0; i < source.length; i++) {
			added[i+itdVal] = source[i];
		}
		return added;
	} // end addITDBefore method
	
	/** = private:: a short array with the itd added after the source */
	private byte[] addITDAfter(byte[] source) {
		int itdVal = Math.abs(this.itd);
		int len = source.length + itdVal;
		// create new channel
		byte[] added = new byte[len];
		// add silence after source
		for (int i = 0; i < itdVal; i++) {
			added[i + source.length] = 0; // value of silence in signed 16bit: 0
		}
		// add source
		for (int i = 0; i < source.length; i++) {
			added[i] = source[i];
		}
		return added;
	} // end addITDBefore method
	
	/** = private:: stereo stream created from two mono streams left and right */
	private static byte[] monoToStereo(byte[] left, byte[] right) {
		// check that both left and right mono streams are of equal length
		if (left.length != right.length) {
			return null;
		}
		byte[] output = new byte[left.length * 2];
		// put mono channels into respective left and right channel
		for (int i = 0; i < left.length; i = i + 2) {
			output[(i * 2)] = left[i]; // put mono channel into left stereo channel
			output[(i * 2) + 1] = left[i + 1]; // put mono channel into left stereo channel
			output[(i * 2) + 2] = right[i]; // put mono channel into left stereo channel
			output[(i * 2) + 3] = right[i + 1]; // put mono channel into left stereo channel
		}
		return output;
	} // end monoToStereo method
	
	/** = move source to coordinate (x, y) */
	public void moveTo(int x, int y) {
		this.xPos = x;
		this.yPos = y;
		// update ITD and Volume
		this.setSS();
	} // end moveTo method
	
	/** = this AudioSS's balance */
	public float getBalance() {
		return this.balance;
	}
	
	/** = this AudioSS's gain */
	public float getGain() {
		return this.gain;
	}
	
	/** = a SS-modified stereo source */
	public byte[] spatialize(byte[] source) {
		byte[] left = new byte[0];
		byte[] right = new byte[0];
		// if the sound source is left of the user
		if (this.itd < 0) {
			left = this.addITDAfter(source);
			right = this.addITDBefore(source);
		}
		// if the sound source is right of the user
		else if (this.itd > 0) {
			left = this.addITDAfter(source);
			right = this.addITDBefore(source);
		}
		// if the sound source is right in front of the user; no itd
		else {
			left = source.clone();
			right = source.clone();
		}
		// create stereo stream
		byte[] stereo = AudioSS.monoToStereo(left, right);
		return stereo;
	} // end spatialize method

	/** = String representation of this AudioSS
	 * including source position, ITD, balance, gain, head radius, and speed of sound
	 */
	public String toString() {
		String state = "AudioSS:\n";
		// add source position info
		state+= "\tsource position: (" + (this.xPos + AudioSS.width/2) + ", " + (AudioSS.height - this.yPos) + ")\n";
		// add relative position info
		state+= "\trelative position: (" + this.xPos + ", " + this.yPos + ")\n";
		// add ITD info in bytes and millisecond
		state += "\titd: " + this.itd + " bytes/ " + ((float)this.itd/16) + " (ms)\n";
		// add Balance and Gain info
		state += "\tbalance: " + this.balance + ", gain: " + this.gain + "dB\n";
		// add Predetermined Factors
		state += "\t\tHead Radius: " + AudioSS.headRad * 100 + 
		"cm, Speed of Sound: " + AudioSS.speedSound + "m/s\n";
		state += "\t\t8000Hz, signed 16-bit, stereo";
		return state;
	}
	
	/** Constructor: creates an instance of AudioSS based off of
	 * source position (x, y) :: (0,0) is located top left of
	 * conference window
	 */
	public AudioSS(int x, int y) {
		this.balance = 0.0f;
		this.gain = 0.0f;
		this.itd = 0;	
		this.xPos = AudioSS.getRelXPos(x);
		this.yPos = AudioSS.getRelYPos(y);
		this.setSS();		
	} // end AudioSS method
} // end Class AudioSS