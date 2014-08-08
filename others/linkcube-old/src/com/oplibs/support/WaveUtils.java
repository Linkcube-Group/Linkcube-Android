package com.oplibs.support;


/*
 * 
 * 
 * 
 * 
 */
public class WaveUtils
{
	private final static int KModeNum=8;

	public static long CalWaveLevel(byte[] waveform)
	{
		//return s == null || s.compareTo("") == 0;
		int v=0;
		long sound = 0;
		for(int nc=0;nc<waveform.length;nc++)
		{
			int tmp = waveform[nc]+128;
			//int tmp = Math.abs((int)waveform[nc]);
			sound+=tmp;
			/*
			byte bv = (byte)waveform[nc];
			short sv = (short)waveform[nc];
			int iv = (int)waveform[nc];
			if(iv==-128)
			{
				iv++;
			}
			else
			{
				iv--;
			}
			*/
			//v/=waveform.length;
		}
		sound/=waveform.length;
		return sound;
	}
	public static long CalFFTLevel(byte[] fftform)
	{
		//return s == null || s.compareTo("") == 0;
		long waveng=0;
		double energy=0.f;
		
		for(int nc=0;nc<fftform.length;nc++)
		{
			/*
			int num = nc/64;
			value[num] += fftform[nc]*fftform[nc];
			*/
			int value;
			value = fftform[nc];
			value = Math.abs(value);
			//waveng+=Math.abs(fftform[nc]);
			waveng+=value;
			energy+=value;
		}
		//waveng/=fftform.length;
		//energy/=(double)fftform.length;
		return waveng;

		/*
		long[] value = {0,0,0,0,0,0,0,0};
		long tmp;
		int nc,nmax;
		
		for(nc=0;nc<fftform.length;nc++)
		{
			int num = nc/64;
			value[num] += fftform[nc]*fftform[nc];
		}
		
		nmax=nc=0;
		tmp=value[nc];
		for(nc=0;nc<KModeNum;nc++)
		{
			if(value[nc]>tmp)
			{
				tmp = value[nc];
				nmax = nc;
			}
		}
		
		if(nmax==KModeNum-1)
		{
			nmax=40;
		}
		else if(nmax==0)
		{
			nmax=1;
		}
		else
		{
			nmax=nmax*5+5;
		}
		return nmax;
		*/
	}
}
