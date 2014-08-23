package me.linkcube.app.sync.friend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.RosterPacket;

import android.os.Handler;
import android.os.Message;

/**
 * 获取当前用户所有好友
 * 
 * @author Rodriguez-xin
 * 
 */
public class GetAllFriends {

	private ASmackRequestCallBack getAllFriendsCallBack;

	public GetAllFriends(ASmackRequestCallBack iCallBack) {
		getAllFriendsCallBack = iCallBack;
		callGetAllFriends();
	}

	private void callGetAllFriends() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.obj == null) {
					getAllFriendsCallBack.responseFailure(-1);
				} else if (msg.obj != null) {
					getAllFriendsCallBack.responseSuccess(msg.obj);
				}
			}
		};
		Thread thread = new Thread() {
			@Override
			public void run() {
				List<RosterEntry> friendsList = new ArrayList<RosterEntry>();

				friendsList = getAllFriends();
				Message msg = new Message();
				msg.obj = friendsList;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}

	/**
	 * 获取所有好友的信息
	 * 
	 * @return
	 */
	private List<RosterEntry> getAllFriends() {
		if (! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return null;
		List<RosterEntry> entrieslist = new ArrayList<RosterEntry>();
		Roster roster =  ASmackManager.getInstance().getXMPPConnection().getRoster();
		Collection<RosterEntry> rosterEntry = roster.getEntries();

		for (RosterEntry entry : rosterEntry) {
			if (entry.getType() == RosterPacket.ItemType.none
					|| entry.getType() == RosterPacket.ItemType.from|| entry.getType() == RosterPacket.ItemType.to) {
				// Ignore, since the new user is pending to be added.
				Timber.d("noBoth"+entry.getName() + "::" + entry.getType());

			} else {
				Timber.d("both:"+entry.getName() + "::" + entry.getType());
				entrieslist.add(entry);
			}
		}
		Timber.d("all::" + rosterEntry.size());
		Timber.d("friend::" + entrieslist.size());
		return entrieslist;
	}
}
