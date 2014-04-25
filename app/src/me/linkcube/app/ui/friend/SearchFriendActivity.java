package me.linkcube.app.ui.friend;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.USER_JID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.linkcube.app.R;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.friend.SearchUser;
import me.linkcube.app.ui.DialogActivity;
import me.linkcube.app.widget.CWClearEditText;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchFriendActivity extends DialogActivity implements
		OnClickListener {

	private Button actionbarFirstBtn;

	private CWClearEditText searchUserEt;

	private ListView showSearchUserLv;

	private String searchUserName;

	private List<Map<String, String>> userList;

	private List<String> userNameList;

	private BaseAdapter showSeachedUserAdapter;

	private List<FriendEntity> friendEntities;

	private boolean isFriend = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serach_user_activity);
		configureActionBar(R.string.search_user);
		initView();
		initData();
	}

	private void initView() {

		actionbarFirstBtn = (Button) actionbarView
				.findViewById(R.id.actionbar_first_btn);
		actionbarFirstBtn.setVisibility(View.VISIBLE);
		actionbarFirstBtn.setText(R.string.search_friend);
		actionbarFirstBtn.setOnClickListener(this);

		searchUserEt = (CWClearEditText) findViewById(R.id.search_user_activity_et);
		showSearchUserLv = (ListView) findViewById(R.id.show_search_user_name_lv);
		showSearchUserLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				TextView strangerNameTv = (TextView) view
						.findViewById(R.id.friend_request_lv_friendname_tv);
				String strangerName = strangerNameTv.getText().toString();
				Bundle bundle = new Bundle();

				for (FriendEntity friendEntity : friendEntities) {
					Timber.d("FriendJid:"
							+ friendEntity.getFriendJid()
							+ "--strangerName:"
							+ ASmackUtils.getFriendJid(ASmackUtils
									.userNameEncode(strangerName)));
					Timber.d("getIsFriend:"+friendEntity.getIsFriend());
					if (friendEntity.getFriendJid().equals(
							ASmackUtils.getFriendJid(ASmackUtils
									.userNameEncode(strangerName)))&&friendEntity.getIsFriend().equals("both")) {
						//Timber.d("getIsFriend:"+friendEntity.getIsFriend());
						isFriend = true;
						break;
					}
				}
				if (isFriend == true) {
					bundle.putString("friendname", ASmackUtils.userNameEncode(strangerName));
					bundle.putString("isFriend", "bothFriend");
					Intent intent = new Intent(SearchFriendActivity.this,
							FriendInfoActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					bundle.putString("friendname",
							ASmackUtils.userNameEncode(strangerName));
					bundle.putString("isFriend", "toFriend");
					Intent intent = new Intent(SearchFriendActivity.this,
							StrangerInfoActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}

			}
		});
	}

	private void initData() {
		userList = new ArrayList<Map<String, String>>();
		userNameList = new ArrayList<String>();

		PersistableFriend perFriend = new PersistableFriend();
		try {
			friendEntities = DataManager.getInstance().query(perFriend,
					USER_JID + "=?", new String[] { ASmackUtils.getUserJID() },
					null, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		showSearchUserLv.setAdapter(showSeachedUserAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(SearchFriendActivity.this)
						.inflate(R.layout.friend_request_listview_cell, null);
				TextView strangerNameTv = (TextView) convertView
						.findViewById(R.id.friend_request_lv_friendname_tv);
				TextView requestAddFriendTv = (TextView) convertView
						.findViewById(R.id.request_add_friend_flag_tv);
				Timber.d("SearchUser--userName:" + userNameList.get(position));
				strangerNameTv.setText(userNameList.get(position));
				requestAddFriendTv.setVisibility(View.INVISIBLE);
				return convertView;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				return arg0;
			}

			@Override
			public int getCount() {
				return userNameList.size();
			}
		});
	}

	private void searchUser() {
		searchUserName = ASmackUtils.userNameEncode(searchUserEt.getText()
				.toString());
		new SearchUser(searchUserName, new ASmackRequestCallBack() {

			@Override
			public void responseSuccess(Object object) {
				userList = (List<Map<String, String>>) object;
				for (Map<String, String> userMap : userList) {
					userMap.put(userMap.get("Username"), userMap.get("Email"));
					userNameList.add(ASmackUtils.userNameDecode(userMap
							.get("Username")));
				}
				Collections.sort(userNameList);
				showSeachedUserAdapter.notifyDataSetChanged();
				dismissProgressDialog();
			}

			@Override
			public void responseFailure(int reflag) {

			}
		});
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { MenuItem
	 * addItem = menu.add(0, Menu.FIRST + 1, 1, "搜索");
	 * addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case Menu.FIRST + 1: showProgressDialog("正在获取中。。");
	 * userNameList.clear(); searchUser(); break; case android.R.id.home: Intent
	 * intent = new Intent(this, MainActivity.class);
	 * intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); this.finish();
	 * startActivity(intent); break; default: break; } return false; }
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_first_btn:
			showProgressDialog("正在获取中。。。");
			userNameList.clear();
			searchUser();
			break;

		default:
			break;
		}
	}
}
