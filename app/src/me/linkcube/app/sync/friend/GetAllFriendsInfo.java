package me.linkcube.app.sync.friend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class GetAllFriendsInfo {

	private Context context;
	private ASmackRequestCallBack updateFriendInfoCallBack;
	private PersistableFriend perFriend;

	public GetAllFriendsInfo(Context _context, List<RosterEntry> _friendsList,
			ASmackRequestCallBack _updateFriendInfoCallBack) {
		context = _context;
		updateFriendInfoCallBack = _updateFriendInfoCallBack;
		checkAllFriend(_friendsList);
		callUpdateAllFriendInfo();
	}

	@SuppressLint("HandlerLeak")
	private void callUpdateAllFriendInfo() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == -1) {
					updateFriendInfoCallBack.responseFailure(msg.what);
				} else if (msg.what == 0) {
					updateFriendInfoCallBack.responseSuccess(msg.what);
				}
			}
		};
		Thread thread = new Thread() {
			@Override
			public void run() {
				int reFlag = updateAllFriendsInfo();
				Message msg = new Message();
				msg.what = reFlag;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}

	private int updateAllFriendsInfo() {

		List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
		try {
			friendEntities = DataManager.getInstance().query(perFriend,
					USER_JID + "=? ",
					new String[] { ASmackUtils.getUserJID() }, null, null,
					null);
			DataManager.getInstance().clear(perFriend);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//Timber.d("friendEntities::"+friendEntities.size());
		//Timber.d("getFriendJid::"+friendEntities.get(0).getIsFriend());
		if (!ASmackManager.getInstance().getXMPPConnection().isConnected())
			return -1;
		try {
			for (FriendEntity friendEntity : friendEntities) {
				VCard vCard = new VCard();
				vCard.load(ASmackManager.getInstance().getXMPPConnection(),
						friendEntity.getFriendJid());
				FriendEntity addFriendEntity = new FriendEntity();
				addFriendEntity.setUserJid(ASmackUtils.getUserJID());
				addFriendEntity.setFriendJid(friendEntity.getFriendJid());
				addFriendEntity.setNickName(vCard.getNickName());
				addFriendEntity.setUserAvatar(vCard.getAvatar());
				addFriendEntity.setUserGender(vCard
						.getField(Const.VCard.GENDER));
				addFriendEntity.setPersonState(vCard
						.getField(Const.VCard.PERSONSTATE));
				addFriendEntity.setBirthday(vCard
						.getField(Const.VCard.BIRTHDAY));
				String userAge = ASmackUtils.getUserAge(vCard
						.getField(Const.VCard.BIRTHDAY));
				addFriendEntity.setUserAge(userAge);
				if (friendEntity.getIsFriend().equals("both")) {
					addFriendEntity.setIsFriend("both");
				}else{
					addFriendEntity.setIsFriend("none");
				}
				Timber.d("nickname:" + addFriendEntity.getNickName());
				DataManager.getInstance().insert(perFriend, addFriendEntity);
			}
			return 0;
		} catch (XMPPException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 与数据库中的所有好友进行对比，如果新增好友则添加进去，已经删除的好友就删除掉。
	 * 
	 * @param friendList
	 */

	private void checkAllFriend(List<RosterEntry> friendList) {
		List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
		perFriend = new PersistableFriend();
		try {
			friendEntities = DataManager.getInstance().query(perFriend,
					USER_JID + "=?",
					new String[] { ASmackUtils.getUserJID() }, null, null,
					null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Timber.d("friendEntities.size():" + friendEntities.size());
		if (friendEntities == null || friendEntities.isEmpty()) {
			for (RosterEntry iterator : friendList) {
				String JID = iterator.getUser();
				String nickName = iterator.getName();
				Timber.i("checkAllFriend--jid:" + JID);
				FriendEntity friendEntity = new FriendEntity();
				friendEntity.setUserJid(ASmackUtils.getUserJID());
				friendEntity.setFriendJid(JID);
				friendEntity.setNickName(nickName);
				friendEntity.setIsFriend("both");
				DataManager.getInstance().insert(perFriend, friendEntity);
			}
		} else {
			// 服务器中没有的就删除
			/*for (FriendEntity friendEntity : friendEntities) {
				for (int i = 0; i < friendList.size(); i++) {
					if (friendEntity.getFriendJid().equals(
							friendList.get(i).getUser())) {
						break;
					} else if (i == friendEntities.size() - 1) {
						Timber.i("checkAllFriend--delnickName:"
								+ friendEntity.getNickName());
						DataManager.getInstance().delete(perFriend,
								friendEntity);
					}
				}
			}*/
			// 新增的添加进来
			for (RosterEntry iterator : friendList) {
				for (int i = 0; i < friendEntities.size(); i++) {
					Timber.d("friendEntities.get(i).getUserJid():"
							+ friendEntities.get(i).getUserJid()
							+ "--iterator.getUser():" + iterator.getUser());
					if (friendEntities.get(i).getFriendJid()
							.equals(iterator.getUser())) {
						break;
					} else if (i == friendEntities.size() - 1) {
						String JID = iterator.getUser();
						String nickName = iterator.getName();
						Timber.d("checkAllFriend--addnickName:" + nickName);
						FriendEntity friendEntity = new FriendEntity();
						friendEntity.setUserJid(ASmackUtils.getUserJID());
						friendEntity.setFriendJid(JID);
						friendEntity.setNickName(nickName);
						friendEntity.setIsFriend("both");
						DataManager.getInstance().insert(perFriend,
								friendEntity);
					}
				}
			}
		}
	}
}
