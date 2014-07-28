package com.oplibs.message;

import java.util.Date;

/*
 * message 基础
 * 包含时间、from、to、信息
 */
public class MessageBase 
{
	public String message;
	public Date time;
	public String from;
	public String to;
	public String resource;
	@Override
	public String toString()
	{
		return message;
	}
}
