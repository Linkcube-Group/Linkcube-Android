package me.linkcube.app.sync.friend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.RosterGroup;

import android.os.Handler;
import android.os.Message;
/**
 * 获取当前用户所有的分组
 * @author Rodriguez-xin
 *
 */
public class GetAllGroups {

	private ASmackRequestCallBack getAllGroupscaCallBack;
	public GetAllGroups(ASmackRequestCallBack iCallBack){
		getAllGroupscaCallBack=iCallBack;
		callGetAllGroups();
	}
	public void callGetAllGroups(){
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.obj==null){
					getAllGroupscaCallBack.responseFailure(-1);
				}
				else if (msg.obj!=null) {
					getAllGroupscaCallBack.responseSuccess(msg.obj);
				}
			}
		};
		Thread thread=new Thread(){
			@Override
			public void run() {
				List<RosterGroup> groupsList = new ArrayList<RosterGroup>();
				
				groupsList = getAllGroups();
				Message msg=new Message();
				msg.obj=groupsList;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}
	/**
	 * 获取所有组
	 * @return
	 */
	public List<RosterGroup> getAllGroups(){
		if(! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return null;
		List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroups= ASmackManager.getInstance().getXMPPConnection().getRoster().getGroups();
		Iterator<RosterGroup> iterator=rosterGroups.iterator();
		while (iterator.hasNext()) {
			grouplist.add(iterator.next());
		}
		return grouplist;
	}
}
