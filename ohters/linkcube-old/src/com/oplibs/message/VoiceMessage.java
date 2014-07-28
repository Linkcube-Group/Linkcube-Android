package com.oplibs.message;

import java.io.File;

//import com.richfit.rim.support.AudioWrapper;

/*
 * 声音消息
 * 播放、锁定、同步等
 * 
 */
public class VoiceMessage extends MessageBase 
{
	private File file;
	
	public VoiceMessage(File messageFile)
	{
		file = messageFile;
	}
	
	public void Play()
	{
		//AudioWrapper audioWrapper = AudioWrapper.GetInstance();
		//audioWrapper.PlayFile(file);
	}
	
	public boolean IsLocked()
	{
		return false;
	}
	
	public boolean Lock()
	{
		return true;
	}
	
	public boolean Unlock()
	{
		return true;
	}
}
