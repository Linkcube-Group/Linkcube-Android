package com.oplibs.message;

import java.util.ArrayList;

/*
 * 消息管理
 * 当前用户 、消息列表
 */
public class MessageManager
{
	private static MessageManager instance;
	
	private String currentUser;
	private ArrayList<TextMessage> messageList;
	
	private MessageManager()
	{
		messageList = new ArrayList<TextMessage>();
	}
	
	public static MessageManager GetInstance()
	{
		if (instance == null)
		{
			instance = new MessageManager();
		}
		
		return instance;
	}

	public Boolean LoadContact(String userId)
	{
		if (userId.compareTo(currentUser) == 0)
		{
			return true;
		}

		return true;
	}
	
	public void AddMessage(TextMessage message)
	{
		synchronized (messageList)
		{
			messageList.add(message);
		}
	}
	
	public TextMessage GetLastMessage()
	{
		synchronized (messageList)
		{
			if (messageList.isEmpty())
			{
				return null;
			}
			
			return messageList.get(messageList.size() - 1);
		}
	}
	
	public TextMessage[] GetMessages()
	{
		synchronized(messageList)
		{
			return (TextMessage[])messageList.toArray();
		}
	}
}
