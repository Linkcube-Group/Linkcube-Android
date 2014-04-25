package me.linkcube.app.core.toy;


import android.annotation.SuppressLint;
import android.media.audiofx.Visualizer;
import android.os.Build;

@SuppressLint("NewApi")
public class VoiceSensor {

	private Visualizer mVisualizer = null;
	private OnVoiceCaptureListener voiceCaptureListener;
	private AudioRecorder audioRecorder;
	private int captureRate;

	public VoiceSensor() {

		if (Build.VERSION.SDK_INT >= 9) {
			captureRate = Visualizer.getMaxCaptureRate();
			mVisualizer = new Visualizer(0);
			mVisualizer.setCaptureSize(128);
			voiceCaptureListener = new OnVoiceCaptureListener();
			mVisualizer.setDataCaptureListener(voiceCaptureListener,
					captureRate / 2, false, true);
			mVisualizer.setEnabled(false);
		} else {
			audioRecorder=new AudioRecorder();
		}
	}

	public void setVoiceLevel(int level) {
		if (Build.VERSION.SDK_INT >= 9) {
			voiceCaptureListener.setLevel(level);
		} else {
			audioRecorder.setLevel(level);
			if(level>0){
				audioRecorder.startAudioRecorder();
			}else{
				audioRecorder.stopAudioRecorder();
			}
		}
	}

	public void registerVoiceListener() {
		mVisualizer.setEnabled(true);

	}

	public void unregisterVoiceListener() {
		if (mVisualizer.getEnabled())
			mVisualizer.setEnabled(false);
	}

}
