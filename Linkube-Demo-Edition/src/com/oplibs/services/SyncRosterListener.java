package com.oplibs.services;

import java.util.Collection;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;


/*
 * 
 * Roster监听
 * 
 */
public class SyncRosterListener implements RosterListener
{

	@Override
	public void entriesAdded(Collection<String> addresses)
	{
		// TODO Auto-generated method stub
		Log.w("RosterListener:", "add!");
	}

	@Override
	public void entriesUpdated(Collection<String> addresses)
	{
		// TODO Auto-generated method stub
		Log.w("RosterListener:", "udpate!");
	}

	@Override
	public void entriesDeleted(Collection<String> addresses)
	{
		// TODO Auto-generated method stub
		Log.w("RosterListener:", "deleted!");
	}

	@Override
	public void presenceChanged(Presence presence)
	{
		// TODO Auto-generated method stub
		Log.w("RosterListener:", "changed!");
	}

}
