package me.linkcube.app.core.toy;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {
	
	private AudioRecord audioRecord;
	private int bs;
	private static int SAMPLE_RATE_IN_HZ=8000;
	private boolean isRun=false;
	private int readdata;
	private int level;

	public AudioRecorder(){
		bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    	audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bs);
	}
	
	public void startAudioRecorder(){
		audioRecord.startRecording();
    	byte[] buffer=new byte[bs];
    	isRun=true;
    	System.out.println(buffer);
    	while(isRun){
    		readdata=audioRecord.read(buffer,0, bs);
    		long sound = ToyUtils.computeWaveLevel(buffer) * level;
    		long waveng = ToyUtils.computeFFTLevel(buffer) * level;
    		Log.i("AudioRecorderListener", "sound:"+sound+"--waveng:"+waveng);
    	} 
	}
	public void stopAudioRecorder(){
		isRun=false;
		audioRecord.stop();
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
