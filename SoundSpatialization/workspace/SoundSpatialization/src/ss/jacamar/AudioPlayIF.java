package ss.jacamar;
/** This interface specifies the basic functions
 * required in integrating sound spatialization */
public interface AudioPlayIF {
	/** plays both audio objects with corresponding interval in between */
	void play();
	
	/** pauses both audio objects with corresponding interval in between */
	void pause();
	
	/** stops both audio objects with corresponding interval in between */
	void stop();
	
	/** writes PCM audio data (in short array form) into both audio objects */
	void write(short[] data);
	
	/** writes PCM audio data (in short array form) into both audio objects */
	void write(byte[] data);
}
