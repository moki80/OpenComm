package com.example.rtpstreamer;

import java.io.IOException;
import java.net.SocketException;

import org.sipdroid.net.RtpPacket;
import org.sipdroid.net.RtpSocket;
import org.sipdroid.net.SipdroidSocket;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;

/**
 * RtpStreamReceiver is a generic stream receiver. It receives packets from RTP
 * and writes them into an OutputStream.
 */
public class ReceiverThread extends Thread {

	/** Whether working in debug mode. */
	public static boolean DEBUG = true;


	static String codec = "";

	/** Size of the read buffer */
	public static final int BUFFER_SIZE = 1024;

	/** Maximum blocking time, spent waiting for reading new bytes [milliseconds] */
	public static final int SO_TIMEOUT = 200;

	/** The RtpSocket */
	RtpSocket rtp_socket = null;

	/** Whether it is running */
	boolean running;
	AudioManager am;
	ContentResolver cr;
	public static int speakermode;
	
	/**
	 * Constructs a RtpStreamReceiver.
	 * 
	 * @param output_stream
	 *            the stream sink
	 * @param socket
	 *            the local receiver SipdroidSocket
	 */
	public ReceiverThread(SipdroidSocket socket) {
		init(socket);
	}

	/** Inits the RtpStreamReceiver */
	private void init(SipdroidSocket socket) {
		if (socket != null)
			rtp_socket = new RtpSocket(socket);
	}

	/** Whether is running */
	public boolean isRunning() {
		return running;
	}

	/** Stops running */
	public void halt() {
		running = false;
	}
	

	static ToneGenerator ringbackPlayer;

	
	double smin = 200,s;
	public static int nearend;
	

	
	static boolean restored;


	public static float good, late, lost, loss;
	public static int timeout;
	
	void empty() {
		try {
			rtp_socket.getDatagramSocket().setSoTimeout(1);
			for (;;)
				rtp_socket.receive(rtp_packet);
		} catch (SocketException e2) {
			 e2.printStackTrace();
		} catch (IOException e) {
		}
		try {
			rtp_socket.getDatagramSocket().setSoTimeout(1000);
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
	}
	
	RtpPacket rtp_packet;
	AudioTrack track;
	
	/** Runs it in a new Thread. */
	public void run() {
		
		
		if (rtp_socket == null) {
			if (DEBUG)
				println("ERROR: RTP socket is null");
			return;
		}

		byte[] buffer = new byte[BUFFER_SIZE+12];
		rtp_packet = new RtpPacket(buffer, 0);

		if (DEBUG)
			println("Reading blocks of max " + buffer.length + " bytes");

		running = true;
		restored = false;

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

		track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
				BUFFER_SIZE*2*2, AudioTrack.MODE_STREAM);
		short lin[] = new short[BUFFER_SIZE];
		short lin2[] = new short[BUFFER_SIZE];
		int user, server, lserver, luser, cnt, todo, headroom, len = 0, seq = 0, cnt2 = 0, m = 1,
			expseq, getseq, vm = 1, gap, gseq;
		timeout = 1;
		boolean islate;
		user = 0;
		lserver = 0;
		luser = -8000;
		cnt = 0;
		G711.init();
		ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC,(int)(ToneGenerator.MAX_VOLUME));
		track.play();
		empty();
		System.gc();
		while (running) {
			try {
				rtp_socket.receive(rtp_packet);
				if (timeout != 0) {
					tg.stopTone();
					track.pause();
					user += track.write(lin2,0,BUFFER_SIZE);
					user += track.write(lin2,0,BUFFER_SIZE);
					track.play();
					cnt += 2*BUFFER_SIZE;
					empty();
				}
				timeout = 0;
			} catch (IOException e) {
				if (timeout == 0) {
					tg.startTone(ToneGenerator.TONE_SUP_RINGTONE);
				}
				rtp_socket.getDatagramSocket().disconnect();
				if (++timeout > 22) {
					break;
				}
			}
			if (running && timeout == 0) {		
				 gseq = rtp_packet.getSequenceNumber();
				 if (seq == gseq) {
					 m++;
					 continue;
				 }
				 server = track.getPlaybackHeadPosition();
				 headroom = user-server;
				 
				 if (headroom > 1500)
					 cnt += len;
				 else
					 cnt = 0;
				 
				 if (lserver == server)
					 cnt2++;
				 else
					 cnt2 = 0;

				 if (cnt <= 500 || cnt2 >= 2 || headroom - 875 < len) {
					 len = rtp_packet.getPayloadLength();
					 G711.alaw2linear(buffer, lin, rtp_packet.getPayloadLength());
					 
				 }
				 
	 			 if (headroom < 250) { 
					todo = 875 - headroom;
					println("insert "+todo);
					islate = true;
					user += track.write(lin2,0,todo);
				 } else
					islate = false;

				 if (cnt > 500 && cnt2 < 2) {
					 todo = headroom - 875;
					 println("cut "+todo);
					 if (todo < len)
						 user += track.write(lin,todo,len-todo);
				 } else
					 user += track.write(lin,0,len);
				 
				 if (seq != 0) {
					 getseq = gseq&0xff;
					 expseq = ++seq&0xff;
					 gap = (getseq - expseq) & 0xff;
					 if (gap > 0) {
						 if (gap > 100) gap = 1;
						 loss += gap;
						 lost += gap;
						 good += gap - 1;
					 } else {
						 if (m < vm)
							 loss++;
						 if (islate)
							 late++;
					 }
					 good++;
					 if (good > 100) {
						 good *= 0.99;
						 lost *= 0.99;
						 loss *= 0.99;
						 late *= 0.99;
					 }
				 }
				 m = 1;
				 seq = gseq;
				 

				 lserver = server;
			}
		}
		track.stop();
		track.release();
		tg.stopTone();
		tg.release();

		tg = new ToneGenerator(AudioManager.STREAM_RING,ToneGenerator.MAX_VOLUME/4*3);
		tg.startTone(ToneGenerator.TONE_PROP_PROMPT);
		try {
			sleep(500);
		} catch (InterruptedException e) {
		}
		tg.stopTone();
		tg.release();

		rtp_socket.close();
		rtp_socket = null;
		codec = "";

		if (DEBUG)
			println("rtp receiver terminated");
	}

	/** Debug output */
	private static void println(String str) {
		System.out.println("RtpStreamReceiver: " + str);
	}

	public static int byte2int(byte b) { // return (b>=0)? b : -((b^0xFF)+1);
		// return (b>=0)? b : b+0x100;
		return (b + 0x100) % 0x100;
	}

	public static int byte2int(byte b1, byte b2) {
		return (((b1 + 0x100) % 0x100) << 8) + (b2 + 0x100) % 0x100;
	}

	public static String getCodec() {
		return codec;
	}
}
