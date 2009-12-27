/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hsc.media;


//import org.sipdroid.net.SipdroidSocket;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.zoolu.sip.provider.SipStack;
import org.zoolu.tools.Log;
import org.zoolu.tools.LogLevel;

import android.R;
import android.content.Context;


/** Audio launcher based on javax.sound  */
public class JAudioLauncher implements MediaLauncher
{  
   /** Event logger. */
   Log log=null;

   /** Payload type */
   int payload_type=8;
   /** Sample rate [bytes] */
   int sample_rate=8000;
   /** Sample size [bytes] */
   int sample_size=1;
   /** Frame size [bytes] */
   int frame_size=160;
   /** Frame rate [frames per second] */
   int frame_rate=50; //=sample_rate/(frame_size/sample_size);
   boolean signed=false; 
   boolean big_endian=false;

   //String filename="audio.wav"; 

   /** Test tone */
   public static final String TONE="TONE";

   /** Test tone frequency [Hz] */
   public static int tone_freq=100;
   /** Test tone ampliture (from 0.0 to 1.0) */
   public static double tone_amp=1.0;

   /** Runtime media process */
   Process media_process=null;
   
   int dir; // duplex= 0, recv-only= -1, send-only= +1; 

   DatagramSocket  socket=null;
   RtpStreamSender sender=null;
   RtpStreamReceiver receiver=null;
   
   HashMap<String, RtpStreamSender> senders;
   HashMap<String, RtpStreamReceiver> receivers;
   Mixer mixer;
   
   /** Costructs the audio launcher */
   public JAudioLauncher(RtpStreamSender rtp_sender, RtpStreamReceiver rtp_receiver, Log logger)
   {  log=logger;
      sender=rtp_sender;
      receiver=rtp_receiver;
   }

   /** Costructs the audio launcher */
   public JAudioLauncher(int local_port,int sample_rate, int sample_size, int frame_size, Log logger) {
	   log=logger;
	  frame_rate=sample_rate/frame_size;
	  try {
		socket=new DatagramSocket(local_port);
	  } catch (SocketException e) {
		  printException(e,LogLevel.HIGH);
	  }
	  
	  senders = new HashMap<String, RtpStreamSender> ();
	  receivers = new HashMap<String, RtpStreamReceiver> ();
	  mixer = new Mixer();
   }
   
   public void addLines (String id, String remote_addr, int remote_port, int direction) {
	   if (dir>=0) { // sender
		   printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.HIGH);
		   InputStream stream = null;
		   try {
			   stream = mixer.newOutputLine(id);
		   } catch (IOException e) {
			   printException(e,LogLevel.HIGH);
		   }
		   
		   senders.put(id, new RtpStreamSender(stream, true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port));
		   senders.get(id).setSyncAdj(2);
       }
	   
	   if (dir<=0) { // receiver
		   printLog("new audio receiver: "+remote_port,LogLevel.HIGH);
		   OutputStream stream = null;
		   try {
			   stream = mixer.newInputLine(id);
		   } catch (IOException e) {
				printException(e,LogLevel.HIGH);
			}
		   
		   receivers.put(id, new RtpStreamReceiver(stream, socket));
	   }
   }
   
   public boolean startLineMedia(String key) {
	   
	   sender = senders.get(key);
	   receiver = receivers.get(key);
	  
	   if (sender!=null)
	   {  printLog("start sending",LogLevel.HIGH);
	      sender.start();
	   }
	   if (receiver!=null)
	   {  printLog("start receiving",LogLevel.HIGH);
          receiver.start();
	   }
	      
	   return true;
   }
   
   public boolean stopLineMedia(String key) {
	  
	   sender = senders.get(key);
	   receiver = receivers.get(key);
	   
	   if (sender!=null) 
       {  sender.halt(); sender=null;
         printLog("sender halted",LogLevel.LOW);
       }      
       if (receiver!=null)
       {  receiver.halt(); receiver=null;
         printLog("receiver halted",LogLevel.LOW);
       }
      
       return true;
   }
   
   public boolean removeLineMedia (String key) {
	   
	   mixer.removeInputLine(key);
	   mixer.removeOutputLine(key);
	   return true;
   }
   
   public JAudioLauncher(int local_port, String remote_addr, int remote_port, int direction, String audiofile_in, String audiofile_out, int sample_rate, int sample_size, int frame_size, Log logger)
   {  log=logger;
      frame_rate=sample_rate/frame_size;
      try
      {  socket=new DatagramSocket(local_port);
         dir=direction;
         // sender
          if (dir>=0)
         {  printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.HIGH);
            //audio_input=new AudioInput();
	        
//         	audiofile_in = "http://74.125.67.100";
//         	//InetAddress ip_in = InetAddress.getByName(audiofile_in);
//         	HttpURLConnection cn = (HttpURLConnection) new URL(audiofile_in).openConnection();
//	        cn.connect();
//	        InputStream stream = cn.getInputStream();
	     
//         //InputStream stream = this.getClass().getResourceAsStream("\t.txt");
//         //InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("");
//         Context ctx;
//         InputStream stream = ctx.getResources().openRawResource(R.raw.ring);

         
         	InputStream stream = null;
            sender=new RtpStreamSender(stream, true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
            sender.setSyncAdj(2);
         }
         
         // receiver
          if (dir<=0)
         {  printLog("new audio receiver on "+local_port,LogLevel.HIGH);
         	OutputStream opstream = null;
            receiver=new RtpStreamReceiver(opstream, socket);
         }
      }
      catch (Exception e) {  printException(e,LogLevel.HIGH);  }
   }

   /** Starts media application */
   public boolean startMedia()
   {  printLog("starting java audio..",LogLevel.HIGH);

      if (sender!=null)
      {  printLog("start sending",LogLevel.HIGH);
         sender.start();
      }
      if (receiver!=null)
      {  printLog("start receiving",LogLevel.HIGH);
         receiver.start();
      }
      
      return true;      
   }

   /** Stops media application */
   public boolean stopMedia()
   {  printLog("halting java audio..",LogLevel.HIGH);    
      if (sender!=null)
      {  sender.halt(); sender=null;
         printLog("sender halted",LogLevel.LOW);
      }      
       if (receiver!=null)
      {  receiver.halt(); receiver=null;
         printLog("receiver halted",LogLevel.LOW);
      }      
      socket.close();
      return true;
   }

   /*public boolean muteMedia()
   {
	   if (sender != null)
		   return sender.mute();
	   return false;
   }
   
   public int speakerMedia(int mode)
   {
	   if (receiver != null)
		   return receiver.speaker(mode);
	   return 0;
   }*/

   // ****************************** Logs *****************************

   /** Adds a new string to the default Log */
   private void printLog(String str)
   {  printLog(str,LogLevel.HIGH);
   }

   /** Adds a new string to the default Log */
   private void printLog(String str, int level)
   {
	  //if (Sipdroid.release) return;
	  if (log!=null) log.println("AudioLauncher: "+str,level+SipStack.LOG_LEVEL_UA);  
      if (level<=LogLevel.HIGH) System.out.println("AudioLauncher: "+str);
   }

   /** Adds the Exception message to the default Log */
   void printException(Exception e,int level)
   { 
	  //if (Sipdroid.release) return;
	  if (log!=null) log.printException(e,level+SipStack.LOG_LEVEL_UA);
      if (level<=LogLevel.HIGH) e.printStackTrace();
   }

}