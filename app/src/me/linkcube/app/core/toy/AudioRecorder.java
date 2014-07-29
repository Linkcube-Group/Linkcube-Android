package me.linkcube.app.core.toy;

import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {
	
	private static AudioRecorder instance = null;
	
	private static AudioRecord audioRecord;
	private static int bs;
	private static int SAMPLE_RATE_IN_HZ=8000;
	private boolean isRun=false;
	private int readdata;
	private int level=1;
	private int count=0;
	
	public static AudioRecorder getInstance() {
		if (instance == null) {
			synchronized (AudioRecorder.class) {
				if (instance == null) {
					instance = new AudioRecorder();
					return instance;
				}
			}
		}
		return instance;
	}

	private AudioRecorder(){
		
	}
	static{
		bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    	audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bs);
	}
	
	public void startAudioRecorder(final ASmackRequestCallBack micSoundSetCallBack){
		
		Thread thread=new Thread(){
			@Override
			public void run() {
				audioRecord.startRecording();
		    	byte[] buffer=new byte[bs];
		    	isRun=true;
		    	while(isRun){
		    		readdata=audioRecord.read(buffer,0, bs);
		    		int v=0;
		    		long sound = ToyUtils.computeWaveLevel(buffer) * level;
		    		long waveng = ToyUtils.computeFFTLevel(buffer) * level;
		    		//Log.i("AudioRecorderListener", "sound:"+sound+"--waveng:"+waveng);
		    		for (int i = 0; i < buffer.length; i++) {  
		                // 这里没有做运算的优化，为了更加清晰的展示代码  
		                v += buffer[i] * buffer[i];  
		            } 
		    		count++;
		    		count=count%5;
		    		if (count==1&&readdata!=0) {
		    			Timber.d("mic.speed:"+String.valueOf((v/readdata)/128));
		    			micSoundSetCallBack.responseSuccess((v/readdata)/128);
					}
		    	} 
			}
		};
		thread.start();
		
	}
	public void stopAudioRecorder(){
		isRun=false;
		audioRecord.stop();
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
