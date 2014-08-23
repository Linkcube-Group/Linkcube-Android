package me.linkcube.app.core.user;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.FRIEND_JID;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.USER_JID;
import static me.linkcube.app.core.persistable.DBConst.TABLE_USER.JID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.entity.UserEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.persistable.PersistableUser;
import me.linkcube.app.sync.chat.ChatMessageManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.friend.AddFriendListener;
import me.linkcube.app.sync.friend.GetAllFriends;
import me.linkcube.app.sync.friend.GetAllFriendsInfo;
import android.content.Context;
import android.content.Intent;

public class UserManager {

	private static UserManager instance = null;

	private boolean isMultiPlaying = false;

	private FriendEntity playingTarget = null;

	private UserEntity userEntity = null;

	private boolean firstLogin = true;

	public static UserManager getInstance() {
		if (instance == null) {
			synchronized (UserManager.class) {
				if (instance == null) {
					instance = new UserManager();
				}
			}
		}

		return instance;
	}

	private UserManager() {

	}

	public boolean isConnected() {
		if (ASmackManager.getInstance().getXMPPConnection() != null
				&& ASmackManager.getInstance().getXMPPConnection()
						.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断当前用户是否已经登陆
	 * 
	 * @return
	 */
	public boolean isAuthenticated() {
		if (ASmackManager.getInstance().getXMPPConnection() != null
				&& ASmackManager.getInstance().getXMPPConnection()
						.isAuthenticated()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户是否在游戏中
	 * 
	 * @return
	 */
	public boolean isMultiPlaying() {
		return isMultiPlaying;
	}

	public void setMultiPlaying(boolean isMultiPlaying) {
		Timber.i("setMultiPlaying:" + isMultiPlaying);
		this.isMultiPlaying = isMultiPlaying;
	}

	/**
	 * 得到与当前用户正在游戏的好友信息
	 * 
	 * @return
	 */
	public FriendEntity getPlayingTarget() {
		return playingTarget;
	}

	public void setPlayingTarget(String friendName) {

		if (friendName != null) {
			List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
			PersistableFriend perFriend = new PersistableFriend();
			try {
				friendEntities = DataManager.getInstance().query(
						perFriend,
						USER_JID + "=? and " + FRIEND_JID + "=?",
						new String[] { ASmackUtils.getUserJID(),
								ASmackUtils.getFriendJid(friendName) }, null,
						null, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.playingTarget = friendEntities.get(0);
		} else {
			this.playingTarget = null;
		}

	}

	/**
	 * 设置用户在线
	 */
	public void setUserStateAvailable() {
		Presence presence = new Presence(Presence.Type.available);
		ASmackManager.getInstance().getXMPPConnection().sendPacket(presence);
	}

	/**
	 * 登陆成功开启监听
	 */
	public void initListener(Context context) {
		// 添加好友的监听
		/*
		 * AddFriendListener addFrientListener = new AddFriendListener(context);
		 * addFrientListener.addFriendListener();
		 */
		AddFriendListener.getInstance().addFriendListener(context);
		// 接收消息的监听
		ChatMessageManager.getInstance().onMessageListener(context);
		// 获取离线消息
		/*
		 * GetOfflineMessage getOfflineMessage=new GetOfflineMessage(this);
		 * getOfflineMessage.getOfflineMsg();
		 */
	}

	/**
	 * 得到当前登录用户的信息
	 */
	public UserEntity getUserInfo() {
		List<UserEntity> userEntities = new ArrayList<UserEntity>();
		PersistableUser perUser = new PersistableUser();
		try {
			userEntities = DataManager.getInstance()
					.query(perUser, JID + "=?",
							new String[] { ASmackUtils.getUserJID() }, null,
							null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		userEntity = userEntities.get(0);
		return userEntity;
	}

	/**
	 * 保存当前登录用户的信息
	 */
	public void saveUserInfo(final Context context, final VCard vCard) {
		/*
		 * Thread thread = new Thread() {
		 * 
		 * @Override public void run() { }
		 * 
		 * }; thread.start();
		 */

		Timber.i(vCard.toXML());
		UserEntity userEntity = new UserEntity();
		userEntity.setJID(ASmackUtils.getUserJID());
		userEntity.setNickName(vCard.getNickName());
		userEntity.setUserAvatar(vCard.getAvatar());
		userEntity.setPersonState(vCard.getField(Const.VCard.PERSONSTATE));
		userEntity.setBirthday(vCard.getField(Const.VCard.BIRTHDAY));
		userEntity.setUserGender(vCard.getField(Const.VCard.GENDER));
		String userAge = ASmackUtils.getUserAge(vCard
				.getField(Const.VCard.BIRTHDAY));
		userEntity.setUserAge(userAge);
		List<UserEntity> userEntities = new ArrayList<UserEntity>();
		PersistableUser perUser = new PersistableUser();
		try {
			userEntities = DataManager.getInstance().query(perUser, JID + "=?",
					new String[] { userEntity.getJID() }, null, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (userEntities.size() == 0) {
			DataManager.getInstance().insert(perUser, userEntity);
		} else if (userEntities.size() != 0) {
			for (int i = 0; i < userEntities.size(); i++) {
				if (userEntities.get(i).getJID().equals(userEntity.getJID())) {
					Timber.i("gender:" + userEntity.getUserGender());
					DataManager.getInstance().update(perUser, userEntity);
					break;
				} else if (i == userEntities.size() - 1) {// 数据库中没有当前用户，则添加
					Timber.i("nickname:" + userEntity.getNickName());
					DataManager.getInstance().insert(perUser, userEntity);
				}
			}
		}
	}

	private List<RosterEntry> friendsList;

	public List<FriendEntity> getDBAllFriends() {
		List<FriendEntity> dbFriendEntities = new ArrayList<FriendEntity>();
		PersistableFriend perFriend = new PersistableFriend();
		try {
			dbFriendEntities = DataManager.getInstance().query(perFriend,
					USER_JID + "=?", new String[] { ASmackUtils.getUserJID() },
					null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbFriendEntities;
	}

	/**
	 * 获取所有好友信息
	 */
	@SuppressWarnings("unchecked")
	public void getAllfriends(final Context context) {
		if (firstLogin) {
			friendsList = new ArrayList<RosterEntry>();
			new GetAllFriends(new ASmackRequestCallBack() {
				@Override
				public void responseSuccess(Object object) {
					friendsList = (List<RosterEntry>) object;
					Timber.i("friendsList.size:" + friendsList);
					new GetAllFriendsInfo(context, friendsList,
							new ASmackRequestCallBack() {

								@Override
								public void responseSuccess(Object object) {

									Timber.i("getallfriendsinfo");
									Intent updateFriendListIntent = new Intent(
											"com.linkcube.updatefriendlist");
									context.sendBroadcast(updateFriendListIntent);
								}

								@Override
								public void responseFailure(int reflag) {

								}
							});
				}

				@Override
				public void responseFailure(int reflag) {

				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	public void firstLoginGetAllfriends(final Context context) {
		friendsList = new ArrayList<RosterEntry>();
		new GetAllFriends(new ASmackRequestCallBack() {
			@Override
			public void responseSuccess(Object object) {
				friendsList = (List<RosterEntry>) object;
				Timber.i("friendsList.size:" + friendsList);
				new GetAllFriendsInfo(context, friendsList,
						new ASmackRequestCallBack() {

							@Override
							public void responseSuccess(Object object) {

							}

							@Override
							public void responseFailure(int reflag) {

							}
						});
			}

			@Override
			public void responseFailure(int reflag) {

			}
		});
	}

	/**
	 * 没有头像时设置用户默认头像
	 * 
	 * @param Gender
	 */
	public int setUserDefaultAvatar(String Gender) {
		try {
			if (Gender.equals("男")) {
				return R.drawable.avatar_male_default;
			} else {
				return R.drawable.avatar_female_default;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return R.drawable.avatar_female_default;
		}
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

}
