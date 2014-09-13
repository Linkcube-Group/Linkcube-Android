package com.oplibs.message;

import android.os.Environment;

/*
 * VM连接管理
 * 发送、接受、删除、锁定等
 */
public class VMContactManager 
{
	private String name;

	public VMContactManager(String contact) throws Exception
	{
		name = contact;
		
		if (name == null || name == "")
		{
			throw new Exception("Empty or null contact name.");
		}
	}
	
	public String GetStorePath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	public Boolean Send()
	{
		return true;
	}
	
	public Boolean Receive()
	{
		return true;
	}
	
	public Boolean Delete()
	{
		return true;
	}
	
	public Boolean Lock()
	{
		return true;
	}
	
	public Boolean Sync()
	{
		return true;
	}
}
