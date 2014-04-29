package me.linkcube.app.ui.main.multi;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.ChatEntity;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.entity.OffLineMsgEntity;
import me.linkcube.app.core.game.GameManager;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableChat;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.chat.ChatMessageListener;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.GetMessageReceiver;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.ui.BaseFragment;
import me.linkcube.app.ui.bluetooth.BluetoothSettingActivity;
import me.linkcube.app.ui.chat.ChatActivity;
import me.linkcube.app.ui.friend.FriendAddedActivity;
import me.linkcube.app.ui.friend.FriendInfoActivity;
import me.linkcube.app.ui.friend.FriendListActivity;
import me.linkcube.app.ui.friend.SearchFriendActivity;
import me.linkcube.app.ui.friend.FriendListActivity.DeleteFriendInChat;
import me.linkcube.app.ui.main.multi.ChatListView.OnChatListViewClickListener;
import me.linkcube.app.ui.main.multi.MultiStatusBarView.OnMultiStatusBarClickListener;
import me.linkcube.app.ui.setting.SettingActivity;
import me.linkcube.app.ui.user.UserInfoActivity;
import me.linkcube.app.util.FormatUtils;
import me.linkcube.app.util.TimeUtils;
import me.linkcube.app.widget.AlertUtils;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Entity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;
import static me.linkcube.app.core.Const.GameInviteMsg.*;

public class MultiPlayerFragment extends BaseFragment implements
		OnMultiStatusBarClickListener, OnChatListViewClickListener,
		DeleteFriendInChat {

	private MultiStatusBarView mStatusBarView;

	private ChatListView chatListView;

	private BaseAdapter chatListAdapter = null;

	private static Map<String, List<String>> msgMap = new HashMap<String, List<String>>();

	private List<ChatEntity> chatEntities;// 存放当前用户与每个人的聊天记录

	private static String gotoItemFriendName;

	private static GetMessageReceiver msgReceiver;

	private IntentFilter filter;

	private static boolean isRegister = false;

	private PersistableChat perChat;

	private String MALE = "男";

	private List<OffLineMsgEntity> offLineMsgs = new ArrayList<OffLineMsgEntity>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.multi_player_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mStatusBarView = (MultiStatusBarView) view
				.findViewById(R.id.status_bar);
		chatListView = (ChatListView) view.findViewById(R.id.chat_lv);
		chatListView.setOnChatListViewClickListener(this);
		mStatusBarView.setMultiStatusBarClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Timber.d("ChatListFragment--onResume");
		handlerMsg();
		initData();
		chatListAdapter.notifyDataSetInvalidated();
		UserManager.getInstance().isMultiPlaying();
		FriendEntity friendEntity = UserManager.getInstance()
				.getPlayingTarget();
		if (friendEntity != null) {
			Timber.d("friendEntity:" + friendEntity.getFriendJid() + "---"
					+ friendEntity.getNickName());
		} else {
			Timber.d("friendEntity:null");
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		Timber.d("ChatListFragment--onStop");
		// getActivity().unregisterReceiver(msgReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Timber.d("ChatListFragment--onDestroy");
		if (isRegister) {
			getActivity().unregisterReceiver(msgReceiver);
			Timber.d("unregisterReceiver");
			isRegister = false;
		}

	}

	private void initData() {
		mStatusBarView.setUserInfo(UserManager.getInstance().getUserInfo());
		mStatusBarView.setTargetInfo(UserManager.getInstance()
				.getPlayingTarget());
		chatEntities = new ArrayList<ChatEntity>();
		gotoItemFriendName = null;
		perChat = new PersistableChat();

		try {
			chatEntities = DataManager.getInstance().query(perChat,
					"userName='" + ASmackUtils.getRosterName() + "'", null,
					"friendName", null, "msgTime desc");
		} catch (IOException e) {
			e.printStackTrace();
		}

		FriendListActivity.setDeleteFriendInChat(this);

		chatListView.setAdapter(chatListAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.chat_listview_cell, null);
				ImageView friendAvatar = (ImageView) convertView
						.findViewById(R.id.chat_friend_avatar);
				ImageView friendGenderIV = (ImageView) convertView
						.findViewById(R.id.item_chat_friend_gender_iv);
				TextView friendNameTV = (TextView) convertView
						.findViewById(R.id.item_chat_friend_name);
				TextView friendMsgTV = (TextView) convertView
						.findViewById(R.id.item_chat_msg);
				TextView msgTimeTV = (TextView) convertView
						.findViewById(R.id.item_chat_msg_date);
				ImageView newItemMsgIv = (ImageView) convertView
						.findViewById(R.id.new_item_msg_iv);

				List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
				PersistableFriend perFriend = new PersistableFriend();
				try {
					friendEntities = DataManager.getInstance().query(
							perFriend,
							USER_JID + "=? and " + FRIEND_JID + "=?",
							new String[] {
									ASmackUtils.getUserJID(),
									ASmackUtils.getFriendJid(chatEntities.get(
											position).getFriendName()) }, null,
							null, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {

					Timber.d(ASmackUtils.getUserJID() + "---"
							+ chatEntities.get(position).getFriendName());

					Timber.d("friendEntities:" + friendEntities.size());

					if (friendEntities != null && !friendEntities.isEmpty()) {
						Timber.d("friendEntities.nickname:"
								+ friendEntities.get(0).getNickName());
						Drawable friendAvatarDrawable = null;
						if (friendEntities.get(0).getUserAvatar() == null) {
							friendAvatarDrawable = getActivity()
									.getResources()
									.getDrawable(
											UserManager
													.getInstance()
													.setUserDefaultAvatar(
															friendEntities
																	.get(0)
																	.getUserGender()));
						} else {
							friendAvatarDrawable = FormatUtils
									.Bytes2Drawable(friendEntities.get(0)
											.getUserAvatar());
						}

						friendAvatar.setImageDrawable(friendAvatarDrawable);
						if (friendEntities.get(0).getUserGender().equals(MALE)) {
							friendGenderIV
									.setBackgroundResource(R.drawable.ic_male);
						} else {
							friendGenderIV
									.setBackgroundResource(R.drawable.ic_female);
						}
					}

					ChatEntity chatEntity = chatEntities.get(position);
					String friendName = chatEntity.getFriendName();
					String friendNickname = chatEntity.getFriendNickname();
					String message = chatEntity.getMessage();
					String msgTime = TimeUtils.toNowTime(chatEntity
							.getMsgTime());

					friendNameTV.setText(friendNickname);
					if (message.length() > 13) {
						String shortMsg = message.substring(0, 13);
						friendMsgTV.setText(shortMsg + "...");
					} else {
						friendMsgTV.setText(message);
					}
					Timber.d("message:" + message);

					msgTimeTV.setText(msgTime);

					if (message != null) {

						if (msgMap.containsKey(friendName)) {
							List<String> showMsgList = new ArrayList<String>();
							showMsgList = msgMap.get(friendName);
							if (showMsgList != null && showMsgList.size() > 0) {
								newItemMsgIv.setVisibility(View.VISIBLE);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return chatEntities.get(position);
			}

			@Override
			public int getCount() {
				return chatEntities.size();
			}
		});

		offLineMsgs = ChatMessageListener.getInstance().getOffLineMsgs();
		for (OffLineMsgEntity offLineMsgEntity : offLineMsgs) {
			saveMsg(offLineMsgEntity.getFrom(), offLineMsgEntity.getBody());
			chatListAdapter.notifyDataSetChanged();
		}
		ChatMessageListener.getInstance().getOffLineMsgs().clear();
		ChatMessageListener.getInstance().setOffLineMsgFlag(false);

	}

	/**
	 * 对收到的信息的操作
	 */
	public void handlerMsg() {
		if (isRegister == false) {// 判断是否已经注册过广播
			msgReceiver = new GetMessageReceiver();
			msgReceiver.setiCallBack(new ASmackRequestCallBack() {

				@Override
				public void responseSuccess(Object object) {
					Message msg = (Message) object;
					switch (msg.what) {
					case 0: {
						Message message = msg;
						String from = message.getData().getString("from");
						String body = message.getData().getString("body");
						String cmdData = message.getData().getString("cmdData");
						if (cmdData.equals(Const.Game.REQUESTCONNECTCMD)) {// 判断收到的是不是游戏请求消息
							GameManager.getInstance().addGameInviteMsgs(from,
									GAME_INVITE_FROM);
						}
						Timber.d("chatListFragment--handlerMsg:" + from + "---"
								+ body);
						saveMsg(from, body);
						Timber.d("chatListAdapter:" + chatListAdapter);
						chatListAdapter.notifyDataSetChanged();
					}
						break;
					default:
						break;
					}
				}

				@Override
				public void responseFailure(int reflag) {

				}
			});
			filter = new IntentFilter();
			filter.addAction("com.linkcube.message");
			filter.addCategory(Intent.CATEGORY_DEFAULT);
			Timber.d("registerReceiver");
			getActivity().registerReceiver(msgReceiver, filter);
			isRegister = true;
		}

	}

	private void saveMsg(String friendName, String body) {
		// 需要将from转换成nickname
		List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
		PersistableFriend perFriend = new PersistableFriend();
		try {
			friendEntities = DataManager.getInstance().query(
					perFriend,
					FRIEND_JID + "=? and " + USER_JID + "=?",
					new String[] { ASmackUtils.getFriendJid(friendName),
							ASmackUtils.getUserJID() }, null, null, null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (!friendEntities.isEmpty() && friendEntities != null) {
			ChatEntity chatEntity = new ChatEntity();
			chatEntity.setUserName(ASmackUtils.getRosterName());
			chatEntity.setFriendName(friendName);
			chatEntity.setFriendNickname(friendEntities.get(0).getNickName());
			chatEntity.setMsgFlag("get");
			chatEntity.setMessage(body);
			chatEntity.setMsgTime(TimeUtils.getNowDateAndTime());

			DataManager.getInstance().insert(perChat, chatEntity);
			if (!friendName.equals(gotoItemFriendName)) {
				if (msgMap.containsKey(friendName)) {// 已经包含发送者
					msgMap.get(friendName).add(body);
				} else {// 第一次收到，不包含发送者
					List<String> msgList = new ArrayList<String>();
					msgList.add(body);
					msgMap.put(friendName, msgList);
				}
			}
		}

		// 选择出与每个用户聊天的最后一条信息
		try {
			chatEntities = DataManager.getInstance().query(perChat,
					"userName='" + ASmackUtils.getRosterName() + "'", null,
					"friendName", null, "msgTime desc");
			Timber.d("chatEntities:" + chatEntities.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		TextView itemFriendTV = (TextView) view
				.findViewById(R.id.item_chat_friend_name);
		String itemFriendName = chatEntities.get(position - 1).getFriendName();
		gotoItemFriendName = itemFriendName;
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("friendName", itemFriendName);
		bundle.putSerializable("msgMap", (Serializable) msgMap);
		intent.putExtras(bundle);
		if (msgMap.containsKey(itemFriendName)) {
			msgMap.get(itemFriendName).clear();
		}
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int position, long arg3) {
		final String friendName = chatEntities.get(position).getFriendName();
		AlertUtils.showAlert(mActivity, "是否删除与对方的聊天纪录", null,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 从数据库中删除与对方的聊天信息
						ChatEntity chatEntity = new ChatEntity();
						chatEntity.setFriendName(friendName);
						DataManager.getInstance().delete(perChat, chatEntity);
						// 从msgMap中删除
						if (msgMap.containsKey(friendName)) {
							msgMap.remove(friendName);
						}
						// 从聊天展示列表中删除对方
						chatEntities.remove(position);
						chatListAdapter.notifyDataSetChanged();
					}
				}, null);
		return true;
	}

	@Override
	public void deleteFriendInChat(String friendName) {
		// 从数据库中删除与对方的聊天信息
		ChatEntity chatEntity = new ChatEntity();
		chatEntity.setFriendName(friendName);
		DataManager.getInstance().delete(perChat, chatEntity);
		// 从msgMap中删除
		if (msgMap.containsKey(friendName)) {
			msgMap.remove(friendName);
		}
		// 从聊天展示列表中删除对方
		// chatEntities.lastIndexOf(object)
		int position = -1;
		for (int i = 0; i < chatEntities.size(); i++) {
			if (friendName.equals(chatEntities.get(i).getFriendName())) {
				position = i;
				break;
			}
		}
		Timber.d("position:" + position);
		if (position != -1) {
			chatEntities.remove(position);
			chatListAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void showUserInfoActivity() {
		startActivity(new Intent(mActivity, UserInfoActivity.class));
	}

	@Override
	public void showTargetInfoActivity() {
		Intent intent = new Intent(mActivity, FriendInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("isFriend", "bothFriend");
		bundle.putString(
				"friendname",
				ASmackUtils.deleteServerAddress(UserManager.getInstance()
						.getPlayingTarget().getFriendJid()));
		intent.putExtras(bundle);
		startActivity(intent);

	}

	@Override
	public void showSettingActivity() {
		startActivityForResult(new Intent(mActivity, SettingActivity.class), 0);
	}

	@Override
	public void showBluetoothSettingActivity() {
		startActivity(new Intent(mActivity, BluetoothSettingActivity.class));
	}

	@Override
	public void showFriendAddedActivity() {
		startActivity(new Intent(mActivity, FriendAddedActivity.class));
	}

	@Override
	public void showAddFriendActivity() {
		startActivity(new Intent(mActivity, SearchFriendActivity.class));
	}

	@Override
	public void showFriendListActivity() {
		startActivity(new Intent(mActivity, FriendListActivity.class));
	}

}
