package com.cornell.opencomm.rtpstreamer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import android.os.Environment;
import android.util.Log;

/**
 * Reads frames of audio input from a file and pushes them onto
 * designated queues. Generally for use with the emulator since it can't
 * instantiate the microphone.
 * @author Curtis
 * Based off of r473 of Sipdroid's RTPStreamSender class (sipdroid.org).
 */
public class AudioPusher extends Thread{

	private static AudioPusher audioPusher;
	private final HashMap<String, BlockingQueue<short[]>> queues;
	private final String filepath;
	private boolean running;
	//frame size may be adjustable in the future
	private int frame_size = 160;
	//set to send out audio packets faster than the frame rate. measured in ms. 
	private int sync_adj = 0;

	/**
	 * Constructor.
	 * @param path filepath of the file to be used as input
	 * @param id identifying value (jingle id, port, etc.) of who the queue belongs to
	 * @param q queue of audio frames.
	 */
	public AudioPusher(String path, String id, BlockingQueue<short[]> q)
	{
		filepath = path;
		queues = new HashMap<String, BlockingQueue<short[]>>();
		queues.put(id, q);
	}

	/**
	 * Gets the single running instance of AudioPusher with or adding a new queue.
	 * @param path filepath of the file to be used as input
	 * @param id identifying value (jingle id, port, etc.) of who the queue belongs to
	 * @param q queue of audio frames.
	 * @return the new or currently active instance
	 */
	public static AudioPusher getInstance(String path, String id, BlockingQueue<short[]> q) {
		if (audioPusher == null)
		{
			audioPusher = new AudioPusher(path, id, q);
		}
		else
		{
			audioPusher.addQueue(id, q);
		}
		return audioPusher;
	}

	/**
	 * @return whether the recording thread is running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Adds queue to the sender.
	 * @param id identifying value (jingle id, port, etc.) of who the queue belongs to
	 * @param q queue of audio frames.
	 */
	public void addQueue(String id, BlockingQueue<short[]> q)
	{
		queues.put(id, q);
	}

	/**
	 * Removes queue from the sender.
	 * @param id identifying value (jingle id, port, etc.) of who the queue belongs to
	 */
	public void removeQueue(String id)
	{
		queues.remove(id);
	}

	/**
	 * @return the number of queues currently being pushed to.
	 */
	public int numQueues() {
		return queues.size();
	}

	/**
	 * Stops the recording thread.
	 */
	public void halt() {
		running = false;
	}

	public void run() {
		Log.i("Audiopusher", "started");
		int bigcounter = 0;
		//read file into array
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + filepath);
		// Get the length of the audio stored in the file (16 bit so 2 bytes per short)
		// and create a short array to store the recorded audio.
		int musicLength = (int)file.length();

		byte[] music = new byte[musicLength];
		// Create a DataInputStream to read the audio data back from the saved file.

		try {

			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			DataInputStream dis = new DataInputStream(bis);

			// Read the file into the music array.
			int i = 0;
			while (dis.available() > 0) {

				music[i] = dis.readByte();
				i++;
			}

			// Close the input streams.
			dis.close();    
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		short[] decode = new short [musicLength];
		//our test files are in alaw, convert to raw pcm
		G711.alaw2linear(music, decode, musicLength-12);
		running = true;
		long last_tx_time = 0;
		long next_tx_delay;
		long now;
		int sample_rate = 8000;
		int frame_rate = sample_rate/frame_size;
		long frame_period = 1000 / frame_rate;
		short[] sample = new short [160];
		Log.i("Audiopusher", "file read, streaming...");
		while (running) {
			//wait for a full frame before grabbing next chunk of audio
			if (frame_size == 160) {
				now = System.currentTimeMillis();
				next_tx_delay = frame_period - (now - last_tx_time);
				last_tx_time = now;
				if (next_tx_delay > 0) {
					try {
						sleep(next_tx_delay);
					} catch (InterruptedException e1) {
					}
					last_tx_time += next_tx_delay-sync_adj;
				}
			}

			//put next chunk of audio into a sample
			for (int i = 0; i < 160; i++)
			{
				bigcounter = (bigcounter % musicLength);
				sample[i] = decode[bigcounter];
				bigcounter++;
			}
			//put sample on all outgoing queues
			for (BlockingQueue<short[]> q : queues.values())
			{
				q.add(sample);
			}

		}
		Log.i("Audiopusher", "terminating...");
		queues.clear();
	}
}
