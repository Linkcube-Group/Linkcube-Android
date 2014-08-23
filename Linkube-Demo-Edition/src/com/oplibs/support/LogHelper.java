package com.oplibs.support;

/*
 * 
 * 日志信息相关
 * 
 */
public class LogHelper
{
	public static LogLevel Level = LogLevel.Message;
	
	public static void LogMessage(String message)
	{
		Log(LogLevel.Message, message);
	}
	
	public static void LogWarning(String message)
	{
		Log(LogLevel.Warning, message);
	}

	public static void LogException(String message, Exception exception)
	{
		Log(LogLevel.Error, message + " \r\n " + exception.getLocalizedMessage());
	}
	
	public static void LogException(Exception exception)
	{
		Log(LogLevel.Error, exception.getLocalizedMessage());
	}
	
	public static void LogError(String message)
	{
		Log(LogLevel.Error, message);
	}
	
	private static void Log(LogLevel level, String message)
	{
		if (level.ordinal() > Level.ordinal())
		{
			return;
		}
		
		switch(level.ordinal())
		{
			case 1: // Error
				android.util.Log.e("OnCall Error", message);
				break;
			case 2: // Warning
				android.util.Log.w("OnCall Warning", message);
				break;
			case 3: // Message
				android.util.Log.i("OnCall Message", message);
				break;				
		}		
	}
	
	public enum LogLevel
	{
		None,
		Error,
		Warning,
		Message	
	};

}
