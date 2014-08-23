package me.linkcube.app.core.toy;

import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.core.Timber;
import android.media.audiofx.Visualizer;
import android.os.RemoteException;

public class OnVoiceCaptureListener implements Visualizer.OnDataCaptureListener {

	public int level;
	
	private int count=0;

	@Override
	public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
			int samplingRate) {
		long sound = VoiceUtils.computeWaveLevel(waveform) * level;
		try {
			LinkcubeApplication.toyServiceCall.setWave(sound);// 根据声音设置玩具速度
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return;

	}

	@Override
	public void onFftDataCapture(Visualizer visualizer, byte[] fft,
			int samplingRate) {
		count++;
		count=count%8;
		if(count==1){
			return;
		}
		long waveng = VoiceUtils.computeFFTLevel(fft) * level;
		
		try {
			LinkcubeApplication.toyServiceCall.setVoiceSensitivity(level);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Timber.d("WaveEng:" + waveng);

		try {
			LinkcubeApplication.toyServiceCall.setWave(waveng);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
