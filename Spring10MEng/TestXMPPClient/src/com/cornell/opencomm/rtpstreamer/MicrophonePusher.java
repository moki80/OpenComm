package com.cornell.opencomm.rtpstreamer;

import java.util.HashMap;
//import java.util.Random;
import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


/**
 * Reads frames of audio input from the microphone and pushes them onto
 * designated queues.
 * @author Curtis
 * Based off of r473 of Sipdroid's RTPStreamSender class (sipdroid.org).
 */
public class MicrophonePusher extends Thread{

	private static MicrophonePusher micPusher;
	private final HashMap<String, BlockingQueue<short[]>> queues;
	private boolean running;
	//frame size may be adjustable in the future
	private int frame_size = 160;
	//set to send out microphone packets faster than the frame rate. measured in ms. 
	private int sync_adj = 0;

	private double smin = 200,s;
	private int nearend;
	//private Random random;

	/**
	 * Constructor.
	 * @param id identifying value (jingle id, port, etc.) of who the queue belongs to
	 * @param q queue of audio frames.
	 */
	public MicrophonePusher(String id, BlockingQueue<short[]> q)
	{
		queues = new HashMap<String, BlockingQueue<short[]>>();
		queues.put(id, q);
	}


	/**
	 * Gets the single running instance of MicrophonePusher with or adding a new queue.
	 * @param id identifying value (jingle id, port, etc.) of who the queue belongs to
	 * @param q queue of audio frames.
	 * @return the new or currently active instance
	 */
	public static MicrophonePusher getInstance(String id, BlockingQueue<short[]> q) {
		if (micPusher == null)
		{
			micPusher = new MicrophonePusher(id, q);
		}
		else
		{
			micPusher.addQueue(id, q);
		}
		return micPusher;
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
		Log.i("MicrophonePusher", "started");

		running = true;
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		//set up initial values
		short[] lin = new short[frame_size*11];
		short[] sample = new short[frame_size];
		int num = 0;
		int ring = 0;
		long last_tx_time = 0;
		long next_tx_delay;
		long now;
		//random = new Random();
		//assumed values for G.711
		int sample_rate = 8000;
		int frame_rate = sample_rate/frame_size;
		long frame_period = 1000 / frame_rate;
		int delay = 0;
		//double p = 0;

		AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 
				AudioRecord.getMinBufferSize(8000, 
						AudioFormat.CHANNEL_CONFIGURATION_MONO, 
						AudioFormat.ENCODING_PCM_16BIT));

		record.startRecording();
		Log.i("MicrophonePusher", "streaming...");
		while (running) {
			//wait for a full frame before grabbing recorded audio
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

			//grab recorded audio into lin
			num = record.read(lin,(ring+delay*frame_size)%(frame_size*11),frame_size);
			if (num <= 0)
				continue;

			//mystery math happens here (pulled directly from sipdroid no questions asked)
			calc(lin,(ring+delay*frame_size)%(frame_size*11),num);
			/*if (nearend != 0)
				noise(lin,(ring+delay*frame_size)%(frame_size*11),num,p);
			else if (nearend == 0)
				p = 0.9*p + 0.1*s;
			 */
			//copy samples read into single frame
			for (int i = 0; i < num; i++)
				sample[i] = lin[i+ring%(frame_size*11)];

			ring += frame_size;

			//put frame on all outgoing queues
			for (BlockingQueue<short[]> q : queues.values())
			{
				q.add(sample);
			}
		}

		Log.i("MicrophonePusher", "terminating...");
		queues.clear();
	}

	/*private void noise(short[] lin,int off,int len,double power) {
		int i,r = (int)(power*2);
		short ran;

		if (r == 0) r = 1;
		for (i = 0; i < len; i += 4) {
			ran = (short)(random.nextInt(r*2)-r);
			lin[i+off] = ran;
			lin[i+off+1] = ran;
			lin[i+off+2] = ran;
			lin[i+off+3] = ran;
		}
	}*/

	private void calc(short[] lin,int off,int len) {
		int i,j;
		double sm = 30000,r;

		for (i = 0; i < len; i += 5) {
			j = lin[i+off];
			s = 0.03*Math.abs(j) + 0.97*s;
			if (s < sm) sm = s;
			if (s > smin) nearend = 3000/5;
			else if (nearend > 0) nearend--;
		}
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			if (j > 6550)
				lin[i+off] = 6550*5;
			else if (j < -6550)
				lin[i+off] = -6550*5;
			else
				lin[i+off] = (short)(j*5);
		}
		r = (double)len/100000;
		smin = sm*r + smin*(1-r);
	}

}
