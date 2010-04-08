package com.cornell.opencomm.rtpstreamer;
import com.cornell.opencomm.rtpstreamer.G711;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
//import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import org.sipdroid.net.RtpPacket;
import org.sipdroid.net.RtpSocket;
import org.sipdroid.net.SipdroidSocket;

//import android.content.Context;
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
import android.os.Environment;
//import android.preference.PreferenceManager;
//import android.telephony.TelephonyManager;

public class SenderThread extends Thread {
	/** Whether working in debug mode. */
	public static boolean DEBUG = true;

	/** The RtpSocket */
	RtpSocket rtp_socket = null;

	/** Number of frame per second */
	long frame_rate;

	/** Number of bytes per frame */
	int frame_size;
	
	private final BlockingQueue<short[]> queue;

	/**
	 * Whether it works synchronously with a local clock, or it it acts as slave
	 * of the InputStream
	 */
	boolean do_sync = true;
	

	/**
	 * Synchronization correction value, in milliseconds. It accellarates the
	 * sending rate respect to the nominal value, in order to compensate program
	 * latencies.
	 */
	int sync_adj = 0;

	/** Whether it is running */
	boolean running = false;
	boolean muted = false;
	
//	private static HashMap<Character, Byte> rtpEventMap = new HashMap<Character,Byte>(){{
//		put('0',(byte)0);
//		put('1',(byte)1);
//		put('2',(byte)2);
//		put('3',(byte)3);
//		put('4',(byte)4);
//		put('5',(byte)5);
//		put('6',(byte)6);
//		put('7',(byte)7);
//		put('8',(byte)8);
//		put('9',(byte)9);
//		put('*',(byte)10);
//		put('#',(byte)11);
//		put('A',(byte)12);
//		put('B',(byte)13);
//		put('C',(byte)14);
//		put('D',(byte)15);
//	}};
	
	/**
	 * Constructs a RtpStreamSender.
	 * 
	 * @param input_stream
	 *            the stream to be sent
	 * @param do_sync
	 *            whether time synchronization must be performed by the
	 *            RtpStreamSender, or it is performed by the InputStream (e.g.
	 *            the system audio input)
	 * @param payload_type
	 *            the payload type
	 * @param frame_rate
	 *            the frame rate, i.e. the number of frames that should be sent
	 *            per second; it is used to calculate the nominal packet time
	 *            and,in case of do_sync==true, the next departure time
	 * @param frame_size
	 *            the size of the payload
	 * @param src_socket
	 *            the socket used to send the RTP packet
	 * @param dest_addr
	 *            the destination address
	 * @param dest_port
	 *            the destination port
	 */
	public SenderThread(boolean do_sync,
			       long frame_rate, int frame_size,
			       SipdroidSocket src_socket, String dest_addr,
			       int dest_port, BlockingQueue<short[]> que) {
		this.queue = que;
		init(do_sync, frame_rate, frame_size,
				src_socket, dest_addr, dest_port);
	}

	/** Inits the RtpStreamSender */
	private void init(boolean do_sync, 
			  long frame_rate, int frame_size,
			  SipdroidSocket src_socket, String dest_addr,
			  int dest_port) {
		this.frame_rate = frame_rate;
		this.frame_size = frame_size;
		this.do_sync = do_sync;
		try {
			rtp_socket = new RtpSocket(src_socket, InetAddress
					.getByName(dest_addr), dest_port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Sets the synchronization adjustment time (in milliseconds). */
	public void setSyncAdj(int millisecs) {
		sync_adj = millisecs;
	}

	/** Whether is running */
	public boolean isRunning() {
		return running;
	}
	
	public boolean mute() {
		return muted = !muted;
	}

	public static int delay = 0;
	
	/** Stops running */
	public void halt() {
		running = false;
	}
	

	Random random;

	
	public static int m;
	
	/** Runs it in a new Thread. */
	public void run() {
		if (rtp_socket == null)
			return;
		byte[] buffer = new byte[frame_size + 12];
		RtpPacket rtp_packet = new RtpPacket(buffer, 0);
		rtp_packet.setPayloadType(8);
		int seqn = 0;
		long time = 0;
//		double p = 0;
		long frame_period = 1000 / frame_rate;
		long last_tx_time = 0;
		long next_tx_delay;
		long now;
		running = true;
		m = 1;
//		int dtframesize = 4;

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

//		short[] lin = new short[frame_size*11];
//		int num = 0;
		int ring = 0;
		random = new Random();
        
        short[] sample = new short[frame_size];

		while (running) {

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
			 
 			 try {
				sample = queue.take();
			 } catch (InterruptedException e1) {
				e1.printStackTrace();
			 }
 			 G711.linear2alaw(sample, 0, buffer, frame_size);

 			 ring += frame_size;
 			 rtp_packet.setSequenceNumber(seqn++);
 			 rtp_packet.setTimestamp(time);
 			 rtp_packet.setPayloadLength(frame_size);
 			 try {
 				 rtp_socket.send(rtp_packet);
 				 if (m == 2)
 					 rtp_socket.send(rtp_packet);
 			 } catch (IOException e) {
 			 }
			 time += frame_size;
			 m = 1;
		}

		m = 0;
		
		rtp_socket.close();
		rtp_socket = null;

	}

}
