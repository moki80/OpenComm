Risa Naka-- rn96 -- OpenComm Desktop Sound Spatialization

############################
############################

Crude Audio Terminology

	balance: relative balance of a stereo signal between two stereo speakers
			-1.0 (left channel only) -> 1.0 (right channel only)
	gain: overall volume of the line
	interaural time difference (itd): difference in time of sound 
			reaching left and right ear
			< 0: left ear hears it first
			> 0: right ear hears it first

############################
############################

AudioSS.java

	Version 1 calculates relative position, balance, gain, and itd 
			based off of given source position. Source position's (0, 0)
			is assumed to be top-left window. Speed of sound, head radius,
			# bytes per millisecond (8000Hz, signed 16-bit, stereo), and dimension
			of conference window is predetermined by class.

############################
############################

Integration
	
	When user first joins conversation: create instances of AudioSS for each Tx thread
	Spatializing a source: use method spatialize(byte[])
	Moving source position: use method moveTo(int, int)
	Obtaining balance and gain: use respective getter methods
	Changing balance and gain: in Tx thread, change balance and gain through
		Gain: FloatControl gainCtrl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
			gainCtrl.setValue(newValue);
		Balance: FloatControl balCtrl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.BALANCE);
			balCtrl.setValue(newValue);

############################
############################

Non-implemented changes

	AudioSS.java
		- offer option of ss factors when changing conference window
		- offer option of altering ss factors when changing audio format
		- offer option of manually changing gain and balance
		- change gain when entering private spaces