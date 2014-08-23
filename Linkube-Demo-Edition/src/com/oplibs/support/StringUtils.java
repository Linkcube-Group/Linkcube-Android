package com.oplibs.support;

public class StringUtils
{
	public static Boolean IsStringNullOrEmpty(String s)
	{
		return s == null || s.compareTo("") == 0;
	}
}
