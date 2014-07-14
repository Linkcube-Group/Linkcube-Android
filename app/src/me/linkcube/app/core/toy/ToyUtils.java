package me.linkcube.app.core.toy;

/**
 * 音乐频谱识别工具类
 * 
 * @author Rodriguez-xin
 * 
 */
public class ToyUtils {
	/**
	 * 波形信号
	 * 
	 * @param waveform
	 *            获取到的波形数据
	 * @return long
	 */
	public static long computeWaveLevel(byte[] waveform) {
		// return s == null || s.compareTo("") == 0;
		long sound = 0;
		for (int nc = 0; nc < waveform.length; nc++) {
			int tmp = (int) waveform[nc] + (int) 128;
			sound += tmp;
		}
		sound /= waveform.length;
		return sound;
	}

	/**
	 * fft频域信号
	 * 
	 * @param fftform
	 *            获取到的fft信号数据
	 * @return long
	 */
	public static long computeFFTLevel(byte[] fftform) {
		long waveng = 0;
		for (int nc = 0; nc < fftform.length; nc++) {
			int value;
			value = (int) fftform[nc];
			value = Math.abs(value);
			waveng += value;
		}
		return waveng;
	}
}
