package me.linkcube.app.core.user;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.FRIEND_JID;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.USER_JID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.sync.core.ASmackUtils;

public class FriendManager {

	private static FriendManager instance = null;
	
	public static FriendManager getInstance() {
		if (instance == null) {
			synchronized (FriendManager.class) {
				if (instance == null) {
					instance = new FriendManager();
				}
			}
		}

		return instance;
	}
	
	public String getFriendNicknameByFriendName(String _friendName){
		PersistableFriend perFriend = new PersistableFriend();
		List<FriendEntity> friendEntities=new ArrayList<FriendEntity>();
		try {
			friendEntities=DataManager.getInstance().query(
					perFriend,
					USER_JID + "=? and " + FRIEND_JID + "=?",
					new String[] { ASmackUtils.getUserJID(),
							ASmackUtils.getFriendJid(_friendName) }, null, null,
					null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return friendEntities.get(0).getNickName();
	}
}
