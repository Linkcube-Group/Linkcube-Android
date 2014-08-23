package com.oplibs.message;

import org.jivesoftware.smack.packet.Message;

import com.oplibs.syncore.*;

/*
 * 文本消息
 * from 、to 、消息内容
 */
public class TextMessage extends MessageBase 
{
	public TextMessage(Message message)
	{
		String fromLong = message.getFrom();
		int separater = fromLong.indexOf("/");
		
		if (separater >= 0)
		{
			super.from = fromLong.substring(0, separater);
			super.resource = fromLong.substring(separater + 1);
		}
		else
		{
			super.from = fromLong;
			super.resource = "";
		}
		
		//We should get time from message;
		time = new java.util.Date();
		
		super.message = message.getBody();
		
		SyncMgr user = SyncMgr.GetInstance();
		super.to = user.GetUserName();
	}
}
