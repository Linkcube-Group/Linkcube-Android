package me.linkcube.app.core.toy;

/**
 * 音乐频谱识别工具类
 * 
 * @author Rodriguez-xin
 * 
 */
public class ToyUtils {
	private final static int KModeNum = 8;

	/**
	 * 波形信号
	 * 
	 * @param waveform
	 *            获取到的波形数据
	 * @return long
	 */
	public static long computeWaveLevel(byte[] waveform) {
		// return s == null || s.compareTo("") == 0;
		int v = 0;
		long sound = 0;
		for (int nc = 0; nc < waveform.length; nc++) {
			int tmp = (int) waveform[nc] + (int) 128;
			// int tmp = Math.abs((int)waveform[nc]);
			sound += tmp;
			/*
			 * byte bv = (byte)waveform[nc]; short sv = (short)waveform[nc]; int
			 * iv = (int)waveform[nc]; if(iv==-128) { iv++; } else { iv--; }
			 */
			// v/=waveform.length;
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
		// return s == null || s.compareTo("") == 0;
		long waveng = 0;
		double energy = 0.f;

		for (int nc = 0; nc < fftform.length; nc++) {
			/*
			 * int num = nc/64; value[num] += fftform[nc]*fftform[nc];
			 */
			int value;
			value = (int) fftform[nc];
			value = Math.abs(value);
			// waveng+=Math.abs(fftform[nc]);
			waveng += value;
			energy += (double) value;
		}
		// waveng/=fftform.length;
		// energy/=(double)fftform.length;
		return waveng;

		/*
		 * long[] value = {0,0,0,0,0,0,0,0}; long tmp; int nc,nmax;
		 * 
		 * for(nc=0;nc<fftform.length;nc++) { int num = nc/64; value[num] +=
		 * fftform[nc]*fftform[nc]; }
		 * 
		 * nmax=nc=0; tmp=value[nc]; for(nc=0;nc<KModeNum;nc++) {
		 * if(value[nc]>tmp) { tmp = value[nc]; nmax = nc; } }
		 * 
		 * if(nmax==KModeNum-1) { nmax=40; } else if(nmax==0) { nmax=1; } else {
		 * nmax=nmax*5+5; } return nmax;
		 */
	}
}
