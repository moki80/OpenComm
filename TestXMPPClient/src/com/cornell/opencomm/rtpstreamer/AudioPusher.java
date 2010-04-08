package com.cornell.opencomm.rtpstreamer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import android.os.Environment;

public class AudioPusher extends Thread{
	
	private final BlockingQueue<short[]> queue;
	private final String filepath;
	private boolean running;
	
	public AudioPusher(String path, BlockingQueue<short[]> q)
	{
		filepath = path;
		queue = q;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void halt() {
		running = false;
	}
	
	public void run() {
		
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
        G711.alaw2linear(music, decode, musicLength-12);
        running = true;
        short[] sample = new short [160];
        while (running) {
        	
        	try {
				Thread.sleep(18);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			  for (int i = 0; i < 160; i++)
  			  {
  				  bigcounter = (bigcounter % musicLength);
  				  sample[i] = decode[bigcounter];
  				  bigcounter++;
  			  }
			queue.add(sample);
			  
        }
        
	}

}
