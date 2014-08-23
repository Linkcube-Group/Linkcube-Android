package me.linkcube.app.sync.friend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import android.os.Handler;
import android.os.Message;

public class GetFriendsByGroup {

	private ASmackRequestCallBack getfriendsByGroupCallBack;
	public GetFriendsByGroup(String groupName,ASmackRequestCallBack iCallBack){
		getfriendsByGroupCallBack=iCallBack;
		callGetFriendsByGroup(groupName);
	}
	public void callGetFriendsByGroup(final String groupName){
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.obj==null){
					getfriendsByGroupCallBack.responseFailure(-1);
				}
				else if (msg.obj!=null) {
					getfriendsByGroupCallBack.responseSuccess(msg.obj);
				}
			}
		};
		Thread thread=new Thread(){
			@Override
			public void run() {
				List<RosterEntry> groupFriendsList = new ArrayList<RosterEntry>();
				
				groupFriendsList = getFriendsByGroup(groupName);
				Message msg=new Message();
				msg.obj=groupFriendsList;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}
	
	/**
	 * 获取某个组里的所有好友
	 * @param groupName
	 * @return
	 */
	public List<RosterEntry> getFriendsByGroup(String groupName){
		if(! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return null;
		List<RosterEntry> entriesList=new ArrayList<RosterEntry>();
		RosterGroup rosterGroup= ASmackManager.getInstance().getXMPPConnection().getRoster().getGroup(groupName);
		Collection<RosterEntry> rosterEntry=rosterGroup.getEntries();
		Iterator<RosterEntry> iterator=rosterEntry.iterator();
		while(iterator.hasNext()){
			entriesList.add(iterator.next());
		}
		return entriesList;
	}
}
