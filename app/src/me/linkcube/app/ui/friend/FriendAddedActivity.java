package me.linkcube.app.ui.friend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Presence;


import me.linkcube.app.R;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.entity.FriendRequestEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.persistable.PersistableFriendRequest;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.ui.DialogActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND_REQUEST.*;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;

public class FriendAddedActivity extends DialogActivity implements OnClickListener{

	private Button actionbarFirstBtn;
	
	private ListView friendRequestLv;
	private BaseAdapter friendRequestLvAdapter;
	private List<FriendRequestEntity> friendRequestEntities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_request_activity);
		configureActionBar(R.string.new_friend_request);
		initView();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
		friendRequestLvAdapter.notifyDataSetChanged();
	}

	private void initView() {
		friendRequestLv = (ListView) findViewById(R.id.friend_request_lv);
		
		actionbarFirstBtn=(Button)actionbarView.findViewById(R.id.actionbar_first_btn);
		actionbarFirstBtn.setVisibility(View.VISIBLE);
		actionbarFirstBtn.setText(R.string.friend_add_clear);
		actionbarFirstBtn.setOnClickListener(this);
	}

	private void initData() {
		friendRequestEntities = new ArrayList<FriendRequestEntity>();
		PersistableFriendRequest perFriendRequest = new PersistableFriendRequest();
		try {
			friendRequestEntities = DataManager.getInstance().query(
					perFriendRequest, USER_NAME + "=?",
					new String[] { ASmackUtils.getRosterName() }, null, null,
					null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		friendRequestLv.setAdapter(friendRequestLvAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(FriendAddedActivity.this)
						.inflate(R.layout.friend_request_listview_cell, null);
				TextView friendNameTv = (TextView) convertView
						.findViewById(R.id.friend_request_lv_friendname_tv);
				TextView friendFlagTv = (TextView) convertView
						.findViewById(R.id.request_add_friend_flag_tv);

				String friendName = friendRequestEntities.get(position)
						.getFriendName();
				friendNameTv.setText(ASmackUtils.userNameDecode(friendName));

				if (friendRequestEntities.get(position).getSubscription()
						.equals("both")) {
					friendFlagTv.setText("已添加为好友");
				}

				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public int getCount() {
				return friendRequestEntities.size();
			}
		});
		// 跳转到用户个人信息界面
		friendRequestLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				TextView friendNameTv = (TextView) view
						.findViewById(R.id.friend_request_lv_friendname_tv);
				String friendName = friendNameTv.getText().toString();
				Intent intent;
				Bundle bundle = new Bundle();

				List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
				PersistableFriend perFriend = new PersistableFriend();
				try {
					friendEntities = DataManager.getInstance().query(
							perFriend,
							FRIEND_JID + "=? and " + USER_JID + "=? and "+ IS_FRIEND + "=? ",
							new String[] {
									ASmackUtils.getFriendJid(ASmackUtils.userNameEncode(friendName)),
									UserManager.getInstance().getUserInfo()
											.getJID(),"both" }, null, null, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (friendEntities != null && !friendEntities.isEmpty()) {
					bundle.putString("friendname", friendEntities.get(0)
							.getNickName());
					bundle.putString("isFriend", "bothFriend");
					intent = new Intent(FriendAddedActivity.this,
							FriendInfoActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					bundle.putString("friendname", ASmackUtils
							.userNameEncode(friendNameTv.getText().toString()));
					bundle.putString("isFriend", "fromFriend");
					intent = new Intent(FriendAddedActivity.this,
							StrangerInfoActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}

			}
		});
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem addItem = menu.add(0, Menu.FIRST + 1, 1, "清空");
		addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			
			break;
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return false;
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_first_btn:
			//TODO 未处理的好友请求就拒绝
			
			for (FriendRequestEntity friendRequestEntity : friendRequestEntities) {
				if(friendRequestEntity.getSubscription().equals("from")){
					Presence refusePresence = new Presence(Presence.Type.unsubscribe);
					refusePresence.setTo(ASmackUtils.getFriendJid(friendRequestEntity.getFriendName()));
					ASmackManager.getInstance().getXMPPConnection()
							.sendPacket(refusePresence);
				}
				PersistableFriendRequest perFriendRequest = new PersistableFriendRequest();
				DataManager.getInstance().delete(perFriendRequest,
						friendRequestEntity);
			}
			initData();
			friendRequestLvAdapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}
}
