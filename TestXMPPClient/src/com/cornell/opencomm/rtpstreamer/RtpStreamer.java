package com.cornell.opencomm.rtpstreamer;

//import java.io.BufferedInputStream;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.InetAddress;
//
//import org.sipdroid.net.RtpPacket;
//import org.sipdroid.net.RtpSocket;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.sipdroid.net.SipdroidSocket;

import android.app.Activity;
//import android.content.Context;
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioTrack;
//import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
//import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

//import com.example.rtpstreamer.G711;

public class RtpStreamer extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("how do i make anything work");
        setContentView(tv);
        // Get the file we want to playback.


        tv.setText("i have file");

        try {

          
          /*SENDING STUFF HERE*/
          //J Audio Launcher RTPStreamSender init code Start
          int sample_rate = 8000;
          int frame_size = 160;
          int frame_rate = sample_rate/frame_size;
          SipdroidSocket socket = new SipdroidSocket(5004);
          SipdroidSocket recv_socket = new SipdroidSocket(6004);
//          SipdroidSocket socket2 = new SipdroidSocket(5006);
//
//          SipdroidSocket recv_socket2 = new SipdroidSocket(6006);
          boolean do_sync = true;
          
//          fun fact: 10.0.2.2 is the host's loopback, 127.0.0.1 is the emulator's       
          BlockingQueue<short[]> queue = new LinkedBlockingQueue<short[]>();
          
          SenderThread sender = new SenderThread(do_sync, frame_rate, frame_size, socket, "10.0.2.2", 33333, queue);
          //sender.setFilePath("/test2.wav");
//          SenderThread sender2 = new SenderThread(do_sync, frame_rate, frame_size, socket2, "127.0.0.1", 6006);
          ReceiverThread receiver = new ReceiverThread(recv_socket);
          AudioPusher pusher = new AudioPusher("/test3.wav", queue);
//          ReceiverThread receiver2 = new ReceiverThread(recv_socket2);
          sender.start();
          pusher.start();
//          sender2.start();
          receiver.start();
//          receiver2.start();
          boolean running = true;
          long time = System.currentTimeMillis();
  		
  		
          while (running)
          {
//        	  if (System.currentTimeMillis() - time > 20000) running = false;
        	  Thread.sleep(frame_size);
        	  
        	  
          }
          sender.halt();
////          sender2.halt();
          receiver.halt();
//          receiver2.halt();
          
        } catch (Throwable t) {
          Log.e("AudioTrack","Playback Failed");
          tv.setText(t.getMessage());
          
        }
    }
}