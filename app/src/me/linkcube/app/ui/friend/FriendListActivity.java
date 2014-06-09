package me.linkcube.app.ui.friend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;

import me.linkcube.app.R;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.ui.BaseActivity;
import me.linkcube.app.ui.friend.FriendListView.OnFriendListViewClickListener;
import me.linkcube.app.widget.AlertUtils;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.entity.FriendRequestEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.persistable.PersistableFriendRequest;
import me.linkcube.app.core.user.UserManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;

public class FriendListActivity extends BaseActivity implements
		OnFriendListViewClickListener {

	private static FriendListView friendlistView;

	private static BaseAdapter friendsListAdapter;

	private static List<FriendEntity> friendEntities;

	private static DeleteFriendInChat deleteFriendInChat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_activity);
		configureActionBar(R.string.friend_list);
		friendlistView = (FriendListView) findViewById(R.id.friend_lv);
		friendlistView.setOnFriendListViewClickListener(this);
		UserManager.getInstance().getAllfriends(mActivity);// 更新好友信息
		UserManager.getInstance().setFirstLogin(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
		friendsListAdapter.notifyDataSetChanged();
	}

	private void initData() {
		friendEntities = new ArrayList<FriendEntity>();
		for (int i = 0; i < UserManager.getInstance().getDBAllFriends().size(); i++) {
			friendEntities.add(UserManager.getInstance().getDBAllFriends()
					.get(i));
		}
		friendsListAdapter = new FriendListAdapter(mActivity, friendEntities);
		friendlistView.setAdapter(friendsListAdapter);
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		TextView itemFriendTV = (TextView) view
				.findViewById(R.id.friend_name_tv);
		// String itemFriendName = itemFriendTV.getText().toString();
		Intent intent = new Intent(mActivity, FriendInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("isFriend", "bothFriend");
		bundle.putString("friendname", ASmackUtils
				.deleteServerAddress(friendEntities.get(position)
						.getFriendJid()));
		intent.putExtras(bundle);
		startActivity(intent);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, final View view,
			final int position, long id) {

		AlertUtils.showAlert(mActivity, getResources().getString(R.string.toast_delete_friend), null, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final Handler handler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						TextView itemFriendTV = (TextView) view
								.findViewById(R.id.friend_name_tv);
						String itemFriendNickname = itemFriendTV.getText()
								.toString();

						List<FriendEntity> friend = new ArrayList<FriendEntity>();
						PersistableFriend perFriend = new PersistableFriend();
						try {
							friend = DataManager.getInstance().query(
									perFriend,
									USER_JID + "=? and " + NICK_NAME + "=?",
									new String[] { ASmackUtils.getUserJID(),
											itemFriendNickname }, null, null,
									null);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						if (!friend.isEmpty() && friend != null) {
							// 服务器删除
							try {
								Roster roster = ASmackManager.getInstance()
										.getXMPPConnection().getRoster();
								RosterEntry removeRoster = roster
										.getEntry(friend.get(0).getFriendJid());
								if (removeRoster != null) {
									roster.removeEntry(removeRoster);
								}
							} catch (XMPPException e) {
								e.printStackTrace();
							}
							// 好友表里删除
							FriendEntity friendEntity = new FriendEntity();
							friendEntity.setUserJid(ASmackUtils.getUserJID());
							friendEntity.setFriendJid(friend.get(0)
									.getFriendJid());
							DataManager.getInstance().delete(perFriend,
									friendEntity);
							// 如果请求列表里有，也删除了
							FriendRequestEntity friendRequestEntity = new FriendRequestEntity();
							friendRequestEntity.setUserName(ASmackUtils
									.getRosterName());
							friendRequestEntity.setFriendName(ASmackUtils
									.deleteServerAddress(friend.get(0)
											.getFriendJid()));
							PersistableFriendRequest perFriendRequest = new PersistableFriendRequest();
							DataManager.getInstance().delete(perFriendRequest,
									friendRequestEntity);
						}
						// 列表删除
						friendEntities.remove(position);
						// 聊天列表删除
						deleteFriendInChat.deleteFriendInChat(ASmackUtils
								.deleteServerAddress(friend.get(0)
										.getFriendJid()));

						friendsListAdapter.notifyDataSetChanged();
					}

				};

				Thread thread = new Thread() {

					@Override
					public void run() {
						handler.sendEmptyMessage(0);
					}

				};
				thread.start();

			}
		}, null);
		return true;
	}

	public static class UpdateFriendListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

				Timber.d("friendsListAdapter.notifyDataSetChanged");
				friendEntities.clear();
				for (int i = 0; i < UserManager.getInstance().getDBAllFriends()
						.size(); i++) {
					friendEntities.add(UserManager.getInstance()
							.getDBAllFriends().get(i));
				}
				friendsListAdapter.notifyDataSetChanged();

		}
	}

	public static void setDeleteFriendInChat(DeleteFriendInChat deleteFriendChat) {
		deleteFriendInChat = deleteFriendChat;
	}

	public interface DeleteFriendInChat {
		public void deleteFriendInChat(String friendNickname);
	}

}
