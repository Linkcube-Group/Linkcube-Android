package me.linkcube.app.ui.chat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import static me.linkcube.app.core.persistable.DBConst.TABLE_CHAT.*;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;
import static me.linkcube.app.core.Const.GameInviteMsg.*;
import static me.linkcube.app.core.Const.DeleteAfterRead.COUNT_DOWN;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.R;
import me.linkcube.app.common.ui.DialogActivity;
import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.common.util.TimeUtils;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.bluetooth.BluetoothUtils;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.core.entity.ChatEntity;
import me.linkcube.app.core.entity.ChatMsgEntity;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.game.GameManager;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableChat;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.toy.ShakeSensor;
import me.linkcube.app.core.user.FriendManager;
import me.linkcube.app.core.user.UserUtils;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.chat.SingleChat;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.ui.bluetooth.BluetoothSettingActivity;
import me.linkcube.app.ui.chat.ChatPanelView.OnGameListener;
import me.linkcube.app.ui.chat.ChatPanelView.OnUpdateChatListListener;
import me.linkcube.app.widget.ChatPullDownListView;
import me.linkcube.app.widget.ChatPullDownListView.OnListViewBottomListener;
import me.linkcube.app.widget.ChatPullDownListView.OnListViewTopListener;
import me.linkcube.app.widget.ChatPullDownListView.OnRefreshAdapterDataListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ChatActivity extends DialogActivity implements OnClickListener,
		OnUpdateChatListListener, OnGameListener {

	private String friendName;
	private ChatPanelView chatPanelView;
	private RelativeLayout singleChatRl;
	private Button mSendBtn;
	private ImageButton showSlideBtn;
	private EditText sendMsgEt;
	private ListView singleChatLv;
	private View listViewHeadView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> dbDataArrays;
	private List<ChatMsgEntity> mDataArrays;
	private int refreshCount;
	private SingleChat singleChat;
	private boolean ischatPopupRlPop = false;
	private ShakeSensor mShakeSensor;
	private int sendSpeedCount = 0;
	private long sendSpeed = 0;
	InputMethodManager inputManager = null;
	private boolean isStartGame = false;
	private MenuItem disconnectItem, onBlueToothItem;
	private boolean isFriend = true;
	private List<Timer> timers = new ArrayList<Timer>();
	private List<Integer> countDowmList = new ArrayList<Integer>();
	private static Map<String, List<Integer>> chatForFriendMap=new HashMap<String, List<Integer>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		initView();
		initData();
		//setupWidget();设置listview下拉刷新
		configureActionBar(FriendManager.getInstance()
				.getFriendNicknameByFriendName(friendName));

		if (UserManager.getInstance().isMultiPlaying()) {
			isStartGame = true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		chatPanelView.unRegisteMsgReceiver();
	}

	private void initView() {
		singleChatRl = (RelativeLayout) findViewById(R.id.single_chat_rl);

		singleChatLv = (ListView) findViewById(R.id.chat_activity_lv);
		mSendBtn = (Button) findViewById(R.id.btn_send);
		mSendBtn.setOnClickListener(this);
		showSlideBtn = (ImageButton) findViewById(R.id.img_btn_control_toy);
		showSlideBtn.setOnClickListener(this);
		sendMsgEt = (EditText) findViewById(R.id.send_msg_et);
		sendMsgEt.setOnClickListener(this);

		chatPanelView = (ChatPanelView) findViewById(R.id.chat_panel_view);
		mShakeSensor = new ShakeSensor(this);
		mShakeSensor.setIsMultiGame(true);
		mShakeSensor.setiCallBack(new ASmackRequestCallBack() {

			@Override
			public void responseSuccess(Object object) {
				long speed = (Long) object;
				sendSpeedCount++;
				if (sendSpeedCount < 200 && sendSpeed < speed) {
					sendSpeed = speed;
				}
				if (sendSpeedCount == 200) {
					singleChat.sendMsg(friendName, Const.Game.SHAKESPEEDCMD
							+ sendSpeed);
					Timber.d("SHAKESPEEDCMD:" + Const.Game.SHAKESPEEDCMD
							+ sendSpeed);
					sendSpeed = 0;
					sendSpeedCount = 0;
				}
			}

			@Override
			public void responseFailure(int reflag) {

			}
		});
	}

	private void initData() {
		mDataArrays = new ArrayList<ChatMsgEntity>();
		dbDataArrays = new ArrayList<ChatMsgEntity>();
		Intent intent = getIntent();
		friendName = intent.getStringExtra("friendName");
		Timber.d("friendName:" + friendName);
		listViewHeadView = getLayoutInflater().inflate(
				R.layout.chatting_list_header, null);
		singleChatLv.addHeaderView(listViewHeadView);
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays, friendName);
		singleChatLv.setAdapter(mAdapter);
		singleChat = new SingleChat();

		chatPanelView.setSingleChat(singleChat);
		chatPanelView.setmShakeSensor(mShakeSensor);
		chatPanelView.setOnUpdateChatListListener(this);
		chatPanelView.setOnGameListener(this);

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
		try {

			friendName = ASmackUtils.deleteServerAddress(friendEntities.get(0)
					.getFriendJid());
			chatPanelView.setFriendName(friendName);

			if (friendEntities.get(0).getIsFriend().equals("none")) {
				isFriend = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 判断是否正在游戏中
		if (UserManager.getInstance().getPlayingTarget() != null) {
			FriendEntity friendEntity = UserManager.getInstance()
					.getPlayingTarget();
			Timber.d("正在游戏中的FriendJid:" + friendEntity.getFriendJid());
			if (friendEntity != null
					&& friendEntity.getFriendJid().equals(
							ASmackUtils.getFriendJid(friendName))) {
				chatPanelView.keepGaming();
			}
		}

		List<ChatEntity> chats = new ArrayList<ChatEntity>();
		try {
			PersistableChat perChat = new PersistableChat();
			chats = DataManager.getInstance().query(perChat,
					USER_NAME + "=? and " + FRIEND_NAME + "=?",
					new String[] { ASmackUtils.getRosterName(), friendName },
					null, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(chatForFriendMap.get(friendName)!=null){
			countDowmList=chatForFriendMap.get(friendName);
		}else{
			chatForFriendMap.put(friendName, countDowmList);
		}
		if(chats.size()==0){
			countDowmList.clear();
		}
		for (int i = 0; i < chats.size(); i++) {
			try {
				ChatEntity chat = chats.get(i);
				Timber.d("IsAfterRead:" + chat.getIsAfterRead());
				ChatMsgEntity entity = new ChatMsgEntity();
				entity.setDate(chat.getMsgTime());// TimeUtils.toNowTime()
				if (chat.getMsgFlag().equals("get")) {
					entity.setName(chat.getFriendNickname());
					entity.setMsgType(true);
				} else if (chat.getMsgFlag().equals("send")) {
					entity.setName(UserManager.getInstance().getUserInfo()
							.getNickName());
					entity.setMsgType(false);
				}
				if (i < countDowmList.size()) {
					entity.setCountDown(countDowmList.get(i));
				} else {
					entity.setCountDown(COUNT_DOWN);
					countDowmList.add(COUNT_DOWN);
				}
				if (chat.getIsAfterRead() == 1) {
					entity.setText(getResources().getString(
							R.string.del_after_read));
					entity.setCountDown(1);
				} else {
					entity.setText(chat.getMessage());
				}
				dbDataArrays.add(entity);
				delMsgAfterRead(dbDataArrays.size(), entity);// handler处理阅后即焚消息
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dataCount = dbDataArrays.size();
		if (dbDataArrays.size() != 0) {
			refreshCount = dbDataArrays.size();
			addData();
		}

		mAdapter.notifyDataSetChanged();
		singleChatLv.setSelection(singleChatLv.getCount() - 1);

		// 如果最近一条消息是游戏邀请，且时间少于30s，则弹出接受拒绝界面
		ChatMsgEntity chatMsgEntity = GameManager.getInstance()
				.getGameInviteMsgs(friendName);
		if (chatMsgEntity != null) {
			SimpleDateFormat simpleFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			// TimeUtils.getNowTime()-chatMsgEntity.getDate()
			Date nowTime = new Date();
			Date gameInviteTime = new Date();
			int acceptSecond = 0;
			try {
				nowTime = simpleFormat.parse(TimeUtils.getNowDateAndTime());
				gameInviteTime = simpleFormat.parse(chatMsgEntity.getDate());
				acceptSecond = (int) (30 - (nowTime.getTime() - gameInviteTime
						.getTime()) / 1000);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (chatMsgEntity.getText().equals(GAME_INVITE_FROM)) {
				chatPanelView.fromGameInviteMsg(acceptSecond);
				popUpPanel();
			} else if (chatMsgEntity.getText().equals(GAME_INVITE_TO)) {
				chatPanelView.toGameInviteMsg(acceptSecond);
				popUpPanel();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:// 发送普通消息
			if (!isFriend) {
				Toast.makeText(this, R.string.toast_not_my_friend_add_again,
						Toast.LENGTH_SHORT).show();
			}
			String sendMsg = sendMsgEt.getText().toString();
			if (sendMsg.equals("")) {

			} else {
				ChatMsgEntity entity = UserUtils.sendToFriendMsg(sendMsg,
						friendName);
				mDataArrays.add(entity);
				mAdapter.notifyDataSetChanged();
				singleChat.sendMsg(friendName, sendMsg);
				sendMsgEt.setText("");
				singleChatLv.setSelection(singleChatLv.getCount() - 1);
				delMsgAfterRead(mDataArrays.size(), entity);// handler处理阅后即焚消息
				countDowmList.add(COUNT_DOWN);
			}
			break;

		case R.id.img_btn_control_toy:// 点开显示聊天界面游戏层，准备发起游戏
			// 打开多人游戏控制界面
			inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (ischatPopupRlPop == false) {
				inputManager.hideSoftInputFromWindow(
						sendMsgEt.getWindowToken(), 0);
				singleChatRl.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						2.0f));
				ischatPopupRlPop = true;
			} else {
				singleChatRl.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				ischatPopupRlPop = false;
			}
			break;

		case R.id.send_msg_et:// 发送消息edittext
			sendMsgEt.setFocusableInTouchMode(true);
			sendMsgEt.requestFocus();
			singleChatRl.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			break;
		}
	}

	private void delMsgAfterRead(final int size, final ChatMsgEntity entity) {
		if(PreferenceUtils.getBoolean("DELETE_AFTER_READ", false)){
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = size - 1;
					msg.obj = entity;
					countDownHandler.sendMessage(msg);
				}
			}, 0, 60000);
			timers.add(timer);
		}
	}

	private Handler countDownHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				int countDown = mDataArrays.get(msg.what).getCountDown();
				mDataArrays.get(msg.what).setCountDown(countDown - 1);
				countDowmList.set(msg.what, countDown);
				if (countDown == 1) {
					Timer tempTimer = timers.get(msg.what);
					if (tempTimer != null) {
						tempTimer.cancel();
						tempTimer = null;
					}
					ChatMsgEntity entity = (ChatMsgEntity) msg.obj;
					ChatMsgEntity afterReadEntity = UserUtils.deleteMsgAfterRead(
							getResources().getString(R.string.del_after_read),
							friendName, entity);
					mDataArrays.set(msg.what, afterReadEntity);
					mAdapter.notifyDataSetChanged();
				}
				Timber.d("countDown:" + countDown);
				mAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void updateChatList(ChatMsgEntity entity) {
		mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();
		singleChatLv.setSelection(singleChatLv.getCount() - 1);
		delMsgAfterRead(mDataArrays.size(), entity);// handler处理阅后即焚消息
		countDowmList.add(COUNT_DOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (DeviceConnectionManager.getInstance().isConnected()) {
			onBlueToothItem = menu.add(0, Menu.FIRST + 1, 1, "连接蓝牙").setIcon(
					R.drawable.btn_connect_indicator_on);
		} else {
			onBlueToothItem = menu.add(0, Menu.FIRST + 1, 1, "").setIcon(
					R.drawable.btn_connect_indicator_off);
		}
		onBlueToothItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		if (isStartGame) {

			disconnectItem = menu.add(0, Menu.FIRST + 2, 1, "关闭连接").setIcon(
					R.drawable.stop_multi_game);
			disconnectItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			if (BluetoothUtils.isBluetoothEnabled()) {
				item.setIcon(R.drawable.btn_connect_indicator_on);
			} else {
				item.setIcon(R.drawable.btn_connect_indicator_off);
			}
			startActivity(new Intent(ChatActivity.this,
					BluetoothSettingActivity.class));
			break;
		case Menu.FIRST + 2:
			mShakeSensor.setIsMultiGame(false);
			mShakeSensor.unRegisterListener();
			singleChat.sendMsg(friendName, Const.Game.REQUESTCMD
					+ Const.Game.DISCONNECTCMD);
			singleChatRl.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			try {
				LinkcubeApplication.toyServiceCall.cacheSexPositionMode(0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			chatPanelView.stopGame();
			ChatMsgEntity lastEntity = UserUtils.sendToFriendMsg(getResources()
					.getString(R.string.you_stop_this_game), friendName);
			mDataArrays.add(lastEntity);
			mAdapter.notifyDataSetChanged();
			singleChatLv.setSelection(singleChatLv.getCount() - 1);

			UserManager.getInstance().setMultiPlaying(false);
			UserManager.getInstance().setPlayingTarget(null);
			isStartGame = false;
			ischatPopupRlPop = false;
			supportInvalidateOptionsMenu();
			Toast.makeText(this, R.string.toast_close_game, Toast.LENGTH_LONG)
					.show();
			break;
		case android.R.id.home:
			//chatForFriendMap.put(friendName, countDowmList);
			this.finish();
			break;
		default:
			break;
		}
		return false;
	}

	private void stopGame() {
		mShakeSensor.setIsMultiGame(false);
		mShakeSensor.unRegisterListener();
		singleChatRl.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		try {
			LinkcubeApplication.toyServiceCall.cacheSexPositionMode(0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		UserManager.getInstance().setMultiPlaying(false);
		UserManager.getInstance().setPlayingTarget(null);
		isStartGame = false;
		supportInvalidateOptionsMenu();
		Toast.makeText(this, R.string.toast_close_game, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void gameStart() {
		isStartGame = true;
		supportInvalidateOptionsMenu();
	}

	@Override
	public void gameStop() {
		stopGame();
	}

	@Override
	public void popUpPanel() {
		// 打开多人游戏控制界面
		ischatPopupRlPop = false;
		inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (ischatPopupRlPop == false) {
			inputManager.hideSoftInputFromWindow(sendMsgEt.getWindowToken(), 0);
			singleChatRl
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT, 2.0f));
			ischatPopupRlPop = true;
		} else {
			singleChatRl.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			ischatPopupRlPop = false;
		}
	}

	private int dataCount;
	private ChatPullDownListView mmPullDownView;

	//TODO 设置listview下拉刷新，为了阅后即焚暂时取消
	private void setupWidget() {
		singleChatLv.setOnScrollListener(mOnScrollListener);
		singleChatLv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
		singleChatLv.setOnTouchListener(mOnTouchListener);
		singleChatLv.setOnItemClickListener(mItemClickListener);
		singleChatLv.setKeepScreenOn(true);
		singleChatLv.post(new Runnable() {
			@Override
			public void run() {
				singleChatLv.setSelection(singleChatLv.getCount());
			}
		});
		registerForContextMenu(singleChatLv);

		mmPullDownView = (ChatPullDownListView) findViewById(R.id.chat_list_pulldown_view);
		mmPullDownView.setTopViewInitialize(true);
		mmPullDownView.setIsCloseTopAllowRefersh(false);
		mmPullDownView.setHasbottomViewWithoutscroll(false);
		mmPullDownView
				.setOnRefreshAdapterDataListener(mOnRefreshAdapterDataListener);
		mmPullDownView.setOnListViewTopListener(mOnListViewTopListener);
		mmPullDownView.setOnListViewBottomListener(mOnListViewBottomListener);
	}

	private OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				View topView = singleChatLv.getChildAt(singleChatLv
						.getFirstVisiblePosition());
				if ((topView != null) && (topView.getTop() == 0)) {
					mmPullDownView.startTopScroll();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (mmPullDownView != null) {
				if (singleChatLv.getCount() < dataCount) {
					mmPullDownView.setIsCloseTopAllowRefersh(false);
				} else {
					mmPullDownView.setIsCloseTopAllowRefersh(true);
				}
			}
		}
	};

	private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return false;
		}
	};

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// listviewitem点击之后需要做的事情
		}
	};

	private OnListViewTopListener mOnListViewTopListener = new OnListViewTopListener() {

		@Override
		public boolean getIsListViewToTop() {
			View topListView = singleChatLv.getChildAt(singleChatLv
					.getFirstVisiblePosition());
			if ((topListView == null) || (topListView.getTop() != 0)) {
				return false;
			} else {
				return true;
			}
		}
	};

	private OnListViewBottomListener mOnListViewBottomListener = new OnListViewBottomListener() {

		@Override
		public boolean getIsListViewToBottom() {
			if ((singleChatLv.getChildAt(-1 + singleChatLv.getChildCount())
					.getBottom() > singleChatLv.getHeight())
					|| (singleChatLv.getLastVisiblePosition() != -1
							+ singleChatLv.getAdapter().getCount())) {

				return false;
			} else {

				return true;
			}
		}
	};
	private OnRefreshAdapterDataListener mOnRefreshAdapterDataListener = new OnRefreshAdapterDataListener() {

		@Override
		public void refreshData() {
			int onLoadDataCount = addData();
			if (onLoadDataCount > 0) {
				mAdapter.notifyDataSetChanged();
				singleChatLv.setSelectionFromTop(
						onLoadDataCount + 1,
						listViewHeadView.getHeight()
								+ mmPullDownView.getTopViewHeight());
			} else {
				singleChatLv.setSelectionFromTop(1,
						mmPullDownView.getTopViewHeight());
			}
		}
	};

	private int addData() {
		//TODO 下拉刷新聊天listview列表，为了阅后即焚暂时取消
		/*if (refreshCount == 0) {
			return 0;
		} else if (refreshCount - 10 < 0) {
			for (int i = refreshCount; i > 0; i--) {
				mDataArrays.add(0, dbDataArrays.get(i - 1));
			}
			int count = refreshCount;
			refreshCount = 0;
			return count;
		} else if (refreshCount >= dbDataArrays.size() - 10) {
			for (int i = refreshCount; i > refreshCount - 10; i--) {
				mDataArrays.add(0, dbDataArrays.get(i - 1));
			}
			refreshCount -= 10;
			return 10;
		} else {
			return 0;
		}*/
		for (int i = 0; i < dbDataArrays.size(); i++) {
			mDataArrays.add(dbDataArrays.get(i));
		}
		return 0;
	}

}