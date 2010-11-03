package ss.jacamar;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/** This class demonstrates sound spatialization using a file */
public class AudioPlayFile implements AudioPlayIF {

	private int x0; // user's x coordinate
	private int y0; // user's y coordinate
	private int xS; // sound source's x coordinate
	private int yS; // sound source's y coordinate
	private double dist; // distance between user and sound source
	private double angle; // direction of sound source; 0 denotes directly in front of user
	private double delay; // interaural time delay in milliseconds
	private AudioTrack left; // audiotrack controlling left sound
	private AudioTrack right; // audiotrack controlling right sound
	private static int BUFFER_SIZE = 1024; // buffer size in bytes
	private static double HEAD_RADIUS = 0.11; // radius of head in m
	private static double SOUND_SPEED = 343.42; // speed of sound in m/s
	
	public void pause() {
		// if the source is left or in front of user
		if (this.delay > 0) {
			try {
				this.left.pause();
				Thread.sleep((long) this.delay);
			}
			catch (InterruptedException e) {
			}
			finally {
				this.right.pause();
			}
		}
		// if the source right of user
		else {
			try {
				this.right.pause();
				Thread.sleep((long) this.delay);
			}
			catch (InterruptedException e) {
			}
			finally {
				this.left.pause();
			}
		}
	} // end pause method

	public void play() {
		// if the source is left or in front of user
		if (this.delay > 0) {
			try {
				this.left.play();
				Thread.sleep((long) this.delay);
			}
			catch (InterruptedException e) {
			}
			finally {
				this.right.play();
			}
		}
		// if the source right of user
		else {
			try {
				this.right.play();
				Thread.sleep((long) this.delay);
			}
			catch (InterruptedException e) {
			}
			finally {
				this.left.play();
			}
		}
	} // end play method

	public void stop() {
		// if the source is left or in front of user
		if (this.delay > 0) {
			try {
				this.left.stop();
				Thread.sleep((long) this.delay);
			}
			catch (InterruptedException e) {
			}
			finally {
				this.right.stop();
			}
		}
		// if the source right of user
		else {
			try {
				this.right.stop();
				Thread.sleep((long) this.delay);
			}
			catch (InterruptedException e) {
			}
			finally {
				this.left.stop();
			}
		}
	} // end stop method

	/** feed PCM data (short form) to AudioTrack */
	public void write(short[] data) {
		this.left.write(data, 0, data.length);
		this.right.write(data, 0, data.length);
	} // end write method

	/** feed PCM data (byte form) to AudioTrack */
	public void write(byte[] data) {
		this.left.write(data, 0, data.length);
		this.right.write(data, 0, data.length);
	} // end write method
	
	/** = set (xP, yP) as new sound source position and update volume */
	public void setPosition(int xP, int yP) {
		// update position of sound source
		this.xS = xP;
		this.yS = yP;
		// update distance, angle, and interaural time delay
		this.dist = Math.sqrt((Math.pow(this.xS - this.x0, 2) + Math.pow(this.yS - this.y0, 2)));
		if (this.dist != 0) {
			this.angle = Math.asin((this.x0 - this.xS)/this.dist);
		}
		else {
			this.angle = 0;
		}
		this.delay = 1000 * AudioPlayFile.HEAD_RADIUS/AudioPlayFile.SOUND_SPEED * (this.angle + Math.sin(this.angle));
		// calculate distance difference between two ears
		double dd = this.delay * AudioPlayFile.SOUND_SPEED;
		// calculate distance from left ear
		double leftDist = this.dist + (dd/2);
		double rightDist = this.dist - (dd/2);
		// set volume based on distance from each ear
		// volume = - 0.001 * distance + 1
		this.left.setStereoVolume((float) (-0.001 * leftDist + 1), 0.0f);
		this.right.setStereoVolume(0.0f, (float) (-0.001 * rightDist + 1));
	} // end setPosition method
	
	/** Constructor: creates 2 AudioTrack objects with the user coordinate (x, y);
	 * default sound source is the same as the user */
	public AudioPlayFile(int x, int y) {
		this.x0 = x;
		this.y0 = y;
		this.xS = x;
		this.yS = y;
		this.dist = 0;
		this.angle = 0;
		this.delay = 0;
		this.left = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
				AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,
				BUFFER_SIZE*2*2, AudioTrack.MODE_STREAM);
		this.right = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
				AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,
				BUFFER_SIZE*2*2, AudioTrack.MODE_STREAM);
		this.left.setStereoVolume(1.0f, 0.0f);
		this.right.setStereoVolume(0.0f, 1.0f);
	} // end AudioPlayFile method

} // end AudioPlayFile class
