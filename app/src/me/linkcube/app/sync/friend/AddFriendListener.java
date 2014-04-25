package me.linkcube.app.sync.friend;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 对好友请求的监听
 * 
 * @author Rodriguez-xin
 * 
 */
public class AddFriendListener {

	private static AddFriendListener instance = null;

	private AddFriendListener() {
	}

	public static AddFriendListener getInstance() {
		if (instance == null) {
			synchronized (AddFriendListener.class) {
				if (instance == null) {
					instance = new AddFriendListener();
				}
			}
		}

		return instance;
	}

	public void addFriendListener(final Context context) {
		PacketFilter filter = new AndFilter(
				new PacketTypeFilter(Presence.class));
		PacketListener listener = null;
		if (listener == null) {
			listener = new PacketListener() {

				@Override
				public void processPacket(Packet packet) {
					Timber.d("start listener!" + packet.toXML());
					if (packet instanceof Presence) {

						Presence presence = (Presence) packet;

						if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
							String friendJid = presence.getFrom();
							Timber.d("friendname:" + friendJid);
							Roster roster = ASmackManager.getInstance()
									.getXMPPConnection().getRoster();
							if (roster.contains(friendJid)) {// 如果他们已经成为了好友
								// 服务器已自动处理为both了
								Timber.d("subscribed:" + friendJid);
								// TODO 插入好友的所有数据
								
								PersistableFriend perFriend = new PersistableFriend();
								List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();

								try {
									friendEntities = DataManager.getInstance().query(
											perFriend,
											USER_JID + "=? and " + FRIEND_JID + "=? ",
											new String[] { ASmackUtils.getUserJID(),friendJid }, null,
											null, null);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								
									VCard vCard = new VCard();
									try {
										vCard.load(ASmackManager.getInstance()
												.getXMPPConnection(), friendJid);
									} catch (XMPPException e) {
										e.printStackTrace();
									}
									FriendEntity friendEntity = new FriendEntity();
									friendEntity.setUserJid(ASmackUtils
											.getUserJID());
									friendEntity.setFriendJid(friendJid);
									friendEntity.setNickName(vCard.getNickName());
									friendEntity.setUserAvatar(vCard.getAvatar());
									friendEntity.setUserGender(vCard
											.getField(Const.VCard.GENDER));
									friendEntity.setBirthday(vCard
											.getField(Const.VCard.BIRTHDAY));
									friendEntity.setPersonState(vCard
											.getField(Const.VCard.PERSONSTATE));
									String userAge = ASmackUtils.getUserAge(vCard
											.getField(Const.VCard.BIRTHDAY));
									if (userAge == null) {
										friendEntity.setUserAge("23");
									} else {
										friendEntity.setUserAge(userAge);
									}
									friendEntity.setIsFriend("both");
									if (friendEntities != null && !friendEntities.isEmpty()) {
										DataManager.getInstance().update(perFriend, friendEntity);
									}else{
										DataManager.getInstance().insert(perFriend,
												friendEntity);
									}
									
									Intent intent = new Intent(
											"com.linkcube.addfriend");
									intent.putExtra("addFriendFlag", "both");
									intent.putExtra("friendName", friendJid);
									context.sendBroadcast(intent);
								
							} else {// 第一次接收到，相互还不是好友
								Timber.d("first:" + friendJid);
								Intent intent = new Intent(
										"com.linkcube.addfriend");
								intent.putExtra("addFriendFlag", "from");
								intent.putExtra("friendName", friendJid);
								context.sendBroadcast(intent);
							}
						} else if (presence.getType().equals(
								Presence.Type.subscribed)) {// 同意添加好友

						} else if (presence.getType().equals(
								Presence.Type.unsubscribe)) {// 拒绝添加好友 和 删除好友
							try {
								String friendJid = presence.getFrom();
								Timber.d("unsubscribe-friendJid:" + friendJid);
								RosterEntry friendEntry = ASmackManager
										.getInstance().getXMPPConnection()
										.getRoster().getEntry(friendJid);
								ASmackManager.getInstance().getXMPPConnection()
										.getRoster().removeEntry(friendEntry);
								PersistableFriend perFriend = new PersistableFriend();
								FriendEntity friendEntity = new FriendEntity();
								List<FriendEntity> friendEntities=new ArrayList<FriendEntity>();
								try {
									friendEntities=DataManager.getInstance().query(
											perFriend,
											USER_JID + "=? and " + FRIEND_JID
													+ "=?",
											new String[] {
													ASmackUtils.getUserJID(),
													friendJid }, null, null,
											null);
								} catch (IOException e) {
									e.printStackTrace();
								}
								friendEntity=friendEntities.get(0);
								Timber.d("friendEntities:"+friendEntity.getFriendJid());
								friendEntity.setIsFriend("none");

								DataManager.getInstance().update(perFriend,
										friendEntity);

							} catch (XMPPException e) {
								e.printStackTrace();
							}
						} else if (presence.getType().equals(
								Presence.Type.unsubscribed)) {

						} else if (presence.getType().equals(
								Presence.Type.unavailable)) { // 好友下线
																// 要更新好友列表，可以在这收到包后，发广播到指定页面
																// 更新列表

						} else {// 好友上线

						}
					}

				}
			};
			ASmackManager.getInstance().getXMPPConnection()
					.addPacketListener(listener, filter);
		}

		Roster roster = ASmackManager.getInstance().getXMPPConnection()
				.getRoster();
		roster.addRosterListener(rosterListener);
	}

	private RosterListener rosterListener = new RosterListener() {

		@Override
		public void presenceChanged(Presence arg0) {

		}

		@Override
		public void entriesUpdated(Collection<String> arg0) {
			Log.w("test entries Updated ", "" + arg0.size());
			for (String s : arg0) {
				Log.w("test entries Updated ", "" + s);
			}

		}

		@Override
		public void entriesDeleted(Collection<String> arg0) {
			Log.w("test entries Deleted ", "" + arg0.size());
			for (String s : arg0) {
				Log.w("test entries Deleted ", "" + s);
			}

		}

		@Override
		public void entriesAdded(Collection<String> arg0) {
			Log.w("test entries Added ", "" + arg0.size());
			for (String s : arg0) {
				Log.w("test entries Added ", "" + s);
			}

		}
	};
}
