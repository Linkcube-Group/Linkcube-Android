package me.linkcube.app.core.toy;


import me.linkcube.app.sync.core.ASmackRequestCallBack;
import android.annotation.SuppressLint;
import android.media.audiofx.Visualizer;
import android.os.Build;

@SuppressLint("NewApi")
public class VoiceSensor {

	private Visualizer mVisualizer = null;
	private OnVoiceCaptureListener voiceCaptureListener;
	private int captureRate;

	public VoiceSensor() {

		if (Build.VERSION.SDK_INT >= 9) {
			mVisualizer = new Visualizer(0);
			captureRate = Visualizer.getMaxCaptureRate();
			int capSize = Visualizer.getCaptureSizeRange()[1];
//			mVisualizer.setCaptureSize(128);
			mVisualizer.setCaptureSize(capSize);
			voiceCaptureListener = new OnVoiceCaptureListener();
			mVisualizer.setDataCaptureListener(voiceCaptureListener,
					captureRate / 2, false, true);
			mVisualizer.setEnabled(false);
		} 
	}

	public void setVoiceLevel(int level) {
		if (Build.VERSION.SDK_INT >= 9) {
			voiceCaptureListener.setLevel(level);
		} else {
			AudioRecorder.getInstance().setLevel(level);
			if(level>0){
				AudioRecorder.getInstance().startAudioRecorder(new ASmackRequestCallBack() {
					
					@Override
					public void responseSuccess(Object object) {
						
					}
					
					@Override
					public void responseFailure(int reflag) {
						
					}
				});
			}else{
				AudioRecorder.getInstance().stopAudioRecorder();
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
