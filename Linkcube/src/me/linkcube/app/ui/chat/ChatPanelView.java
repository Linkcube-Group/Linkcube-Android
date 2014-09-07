package me.linkcube.app.ui.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.linkcube.app.R;
import me.linkcube.app.common.ui.BasePagerAdapter;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.ChatMsgEntity;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.game.GameManager;
import me.linkcube.app.core.toy.ShakeSensor;
import me.linkcube.app.core.user.FriendManager;
import me.linkcube.app.core.user.UserUtils;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.chat.SingleChat;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.GetMessageReceiver;
import me.linkcube.app.widget.CirclePageIndicator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import static me.linkcube.app.core.Const.GameInviteMsg.*;

@SuppressLint("HandlerLeak")
public class ChatPanelView extends RelativeLayout implements OnClickListener {

	private Context mContext;
	private RelativeLayout multiGameConnectRl;// 发送请求时的显示relativelayout
	private RelativeLayout multiGameAcceptConnectRl;// 是否接收请求的relativelayout

	private ImageView multiGameConnectIv;
	private TextView multiGameConnectTv;

	private Button acceptConnectBtn;
	private Button refuseConnectBtn;
	private TextView acceptTimeTv;

	private CirclePageIndicator indicator;
	private ViewPager popUpViewPager;
	private View multiSexPositionModePage;
	private View multiSpeedPage;

	private ImageView multiShakeIv;
	private ImageView multiSexPositionModeIv;

	private SingleChat singleChat = null;
	private static GetMessageReceiver gameMsgReceiver;

	private Timer timer;
	private int acceptSecond = 30;

	private String friendName;

	private ShakeSensor mShakeSensor;
	private int sexPositionMode = 0;
	private int speedLevel = 0;
	private OnUpdateChatListListener onUpdateChatListListener;
	private OnGameListener onGameListener;

	public ChatPanelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
		initData();
	}

	public ChatPanelView(Context context) {
		super(context);
		this.mContext = context;
		initView();
		initData();
	}

	private void initView() {
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.chat_panel_view, this, true);

		multiGameConnectRl = (RelativeLayout) view
				.findViewById(R.id.multi_game_connect_rl);
		multiGameAcceptConnectRl = (RelativeLayout) view
				.findViewById(R.id.multi_game_accept_connect_rl);
		// 发起连接界面
		multiGameConnectTv = (TextView) view
				.findViewById(R.id.multi_game_connect_tv);
		multiGameConnectIv = (ImageView) view
				.findViewById(R.id.multi_game_connect_iv);
		multiGameConnectIv.setOnClickListener(this);
		// 同意拒绝已收到连接界面
		acceptTimeTv = (TextView) view.findViewById(R.id.accept_time_second_tv);
		acceptConnectBtn = (Button) view.findViewById(R.id.accept_connect_btn);
		acceptConnectBtn.setOnClickListener(this);
		refuseConnectBtn = (Button) view.findViewById(R.id.refuse_connect_btn);
		refuseConnectBtn.setOnClickListener(this);
		// 游戏中界面
		popUpViewPager = (ViewPager) view
				.findViewById(R.id.popup_gaming_view_vp);

		LayoutInflater lInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		multiSexPositionModePage = lInflater.inflate(
				R.layout.multi_sex_position_mode_page, null);
		multiSpeedPage = lInflater.inflate(R.layout.multi_speed_page, null);
		List<View> viewList = new ArrayList<View>();
		viewList.add(multiSexPositionModePage);
		viewList.add(multiSpeedPage);
		BasePagerAdapter viewPagerAdapter = new BasePagerAdapter(mContext,
				viewList);
		indicator = (CirclePageIndicator) view
				.findViewById(R.id.chat_panel_indicator);
		popUpViewPager.setAdapter(viewPagerAdapter);
		popUpViewPager.setCurrentItem(0);
		popUpViewPager.setOnPageChangeListener(popupPageChangeListener);
		indicator.setViewPager(popUpViewPager);
		// 多人游戏设置multi_sex_position_mode_iv
		multiSexPositionModeIv = (ImageView) multiSexPositionModePage
				.findViewById(R.id.multi_sex_position_mode_iv);
		multiSexPositionModeIv.setOnClickListener(this);
		multiShakeIv = (ImageView) multiSpeedPage
				.findViewById(R.id.multi_speed_mode_iv);
		multiShakeIv.setOnClickListener(this);

	}

	private void initData() {
		handlerMsg();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.multi_game_connect_iv:// 发送游戏邀请界面
			FriendEntity friendEntity = UserManager.getInstance()
					.getPlayingTarget();
			if (friendEntity != null) {
				Toast.makeText(
						mContext,
						getResources().getString(R.string.is_playing)
								+ friendEntity.getNickName()
								+ getResources().getString(R.string.game_with),
						Toast.LENGTH_SHORT).show();
			} else {
				GameManager.getInstance().addGameInviteMsgs(friendName,
						GAME_INVITE_TO);

				singleChat.sendMsg(friendName, Const.Game.REQUESTCMD
						+ Const.Game.REQUESTCONNECTCMD);
				ChatMsgEntity entity = UserUtils.sendToFriendMsg(
						getResources().getString(R.string.you_send_invitation_to_others), friendName);
				onUpdateChatListListener.updateChatList(entity);
				acceptSecond = 30;
				toGameInviteMsg(acceptSecond);
			}

			break;

		case R.id.accept_connect_btn:// 接受游戏邀请按钮
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			acceptSecond = 30;
			singleChat.sendMsg(friendName, Const.Game.REQUESTCMD
					+ Const.Game.ACCEPTCONNECTCMD);

			popUpViewPager.setVisibility(View.VISIBLE);
			indicator.setVisibility(View.VISIBLE);
			multiGameAcceptConnectRl.setVisibility(View.INVISIBLE);
			ChatMsgEntity acceptEntity = UserUtils.sendToFriendMsg(
					getResources().getString(R.string.you_accept_others_invitation), friendName);
			onUpdateChatListListener.updateChatList(acceptEntity);
			onGameListener.gameStart();
			UserManager.getInstance().setMultiPlaying(true);
			UserManager.getInstance().setPlayingTarget(friendName);
			GameManager.getInstance().clearGameInviteMsg();
			break;

		case R.id.refuse_connect_btn:// 拒绝游戏邀请按钮
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			acceptSecond = 30;
			multiGameConnectRl.setVisibility(View.VISIBLE);
			multiGameAcceptConnectRl.setVisibility(View.INVISIBLE);
			singleChat.sendMsg(friendName, Const.Game.REQUESTCMD
					+ Const.Game.REFUSECONNECTCMD);
			ChatMsgEntity refuseEntity = UserUtils.sendToFriendMsg(
					getResources().getString(R.string.you_refused_others_invitation), friendName);
			onUpdateChatListListener.updateChatList(refuseEntity);
			GameManager.getInstance().delGameInviteMsg(friendName);
			break;
		case R.id.multi_speed_mode_iv:// 摇一摇游戏界面，控制档位大小
			// speedLevel++;
			// if (speedLevel == 5)
			// speedLevel = 0;
			switch (speedLevel) {
			/*
			 * case 0:
			 * multiShakeIv.setBackgroundResource(R.drawable.shake_mode_0);
			 * break; case 1:
			 * multiShakeIv.setBackgroundResource(R.drawable.shake_mode_1);
			 * break; case 2:
			 * multiShakeIv.setBackgroundResource(R.drawable.shake_mode_2);
			 * break; case 3:
			 * multiShakeIv.setBackgroundResource(R.drawable.shake_mode_3);
			 * break; case 4:
			 * multiShakeIv.setBackgroundResource(R.drawable.shake_mode_4);
			 * break;
			 */
			case 0:
				speedLevel = 2;
				multiShakeIv.setBackgroundResource(R.drawable.shake_mode_4);
				break;
			case 2:
				speedLevel = 0;
				multiShakeIv.setBackgroundResource(R.drawable.shake_mode_0);
				break;
			default:
				break;
			}
			sendSexPositionMode(0);
			sexPositionMode = 0;
			startShaken(speedLevel);
			break;

		case R.id.multi_sex_position_mode_iv:// 七种模式游戏界面按钮
			multiShakeIv.setBackgroundResource(R.drawable.shake_mode_0);
			mShakeSensor.unRegisterListener();
			speedLevel = 0;
			sexPositionMode++;
			if (sexPositionMode > 7) {
				sexPositionMode = 0;
			}
			sendSexPositionMode(sexPositionMode);
			break;
		default:
			break;
		}
	}

	public void stopGame() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		acceptSecond = 30;
		multiShakeIv.setBackgroundResource(R.drawable.shake_mode_0);
		multiGameConnectIv.clearAnimation();
		multiGameConnectIv.setImageResource(R.drawable.start_game_circle);
		multiGameConnectTv.setText(getResources().getString(
				R.string.click_and_send_game_msg));
		multiGameConnectIv.setClickable(true);
		multiGameConnectRl.setVisibility(View.VISIBLE);
		multiGameAcceptConnectRl.setVisibility(View.INVISIBLE);
		popUpViewPager.setVisibility(View.INVISIBLE);
		indicator.setVisibility(View.INVISIBLE);

	}

	public void keepGaming() {
		multiGameConnectRl.setVisibility(View.INVISIBLE);
		multiGameAcceptConnectRl.setVisibility(View.INVISIBLE);
		popUpViewPager.setVisibility(View.VISIBLE);
		indicator.setVisibility(View.VISIBLE);
	}

	/**
	 * 发送请求后等待
	 */
	private Handler waitingTimerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			multiGameConnectTv.setText(getResources().getString(
					R.string.request_has_been_send)
					+ acceptSecond + getResources().getString(R.string.second));
			acceptSecond--;
			ChatMsgEntity chatMsgEntity = GameManager.getInstance()
					.getGameInviteMsgs(friendName);
			if (chatMsgEntity != null) {
				if (chatMsgEntity.getText().equals(GAME_INVITE_BOTH)) {
					Message gameMsg = new Message();
					gameMsg.what = 1;
					handler.sendMessage(gameMsg);
					timer.cancel();
					timer = null;
					acceptSecond = 30;
				}
			}
			if (acceptSecond < 0 && timer != null) {
				timer.cancel();
				timer = null;
				multiGameConnectIv.clearAnimation();
				multiGameConnectIv
						.setImageResource(R.drawable.start_game_circle);
				acceptSecond = 30;
				multiGameConnectTv.setText(getResources().getString(
						R.string.click_and_send_game_msg));
				multiGameConnectIv.setClickable(true);
				GameManager.getInstance().delGameInviteMsg(friendName);
			}
		}
	};
	/**
	 * 收到请求后等待处理
	 */
	private Handler acceptTimerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			acceptTimeTv.setText(getResources().getString(R.string.wait_time)
					+ acceptSecond + getResources().getString(R.string.second));
			acceptSecond--;
			if (acceptSecond < 0) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				multiGameConnectRl.setVisibility(View.VISIBLE);
				multiGameAcceptConnectRl.setVisibility(View.INVISIBLE);
				acceptSecond = 30;
				acceptTimeTv.setText(getResources().getString(
						R.string.pls_wait_thirty_second));
				GameManager.getInstance().delGameInviteMsg(friendName);
			}
		}

	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {// 接收到游戏请求
				// 弹出底部panel
				acceptSecond = 30;
				fromGameInviteMsg(acceptSecond);

			} else if (msg.what == 1) {

				acceptGameInviteMsg();

			} else if (msg.what == -1) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				multiGameConnectIv.clearAnimation();
				multiGameConnectIv
						.setImageResource(R.drawable.start_game_circle);
				acceptSecond = 30;
				multiGameConnectTv.setText(getResources().getString(
						R.string.click_and_send_game_msg));
				multiGameConnectIv.setClickable(true);
			} else if (msg.what == -2) {
				mShakeSensor.setIsMultiGame(false);
				mShakeSensor.unRegisterListener();
				multiSexPositionModeIv
						.setBackgroundResource(sexPositionModeDrawable[0]);

				// singleChat.sendMsg(friendName, Const.Game.POSITIONMODECMD+0);
				stopGame();
				onGameListener.gameStop();

				// TODO 结束游戏,关闭玩具
				/*
				 * try { LinkcubeApplication.toyServiceCall.closeToy(); } catch
				 * (RemoteException e) { e.printStackTrace(); }
				 */

			}
		}

	};

	public void fromGameInviteMsg(int acceptSecond) {
		this.acceptSecond = acceptSecond;
		onGameListener.popUpPanel();
		multiGameAcceptConnectRl.setVisibility(View.VISIBLE);
		multiGameConnectRl.setVisibility(View.INVISIBLE);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				acceptTimerHandler.sendEmptyMessage(0);
			}
		}, 1000, 1000);
	}

	public void toGameInviteMsg(int _acceptSecond) {
		this.acceptSecond = _acceptSecond;
		multiGameConnectIv.setImageResource(R.drawable.waiting_game_circle);
		Animation rotateCircleAnim = AnimationUtils.loadAnimation(mContext,
				R.anim.waiting_game_rotate);
		LinearInterpolator lfn = new LinearInterpolator();
		rotateCircleAnim.setInterpolator(lfn);
		multiGameConnectTv.setText(getResources().getString(
				R.string.request_has_been_send)
				+ acceptSecond + getResources().getString(R.string.second));
		multiGameConnectIv.startAnimation(rotateCircleAnim);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				waitingTimerHandler.sendEmptyMessage(0);
			}
		}, 1000, 1000);
		multiGameConnectIv.setClickable(false);
	}

	private void acceptGameInviteMsg() {
		multiGameConnectRl.setVisibility(View.INVISIBLE);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		acceptSecond = 30;
		multiGameConnectTv.setText(getResources().getString(
				R.string.click_and_send_game_msg));
		multiGameConnectIv.setClickable(true);

		List<View> viewList = new ArrayList<View>();
		viewList.add(multiSexPositionModePage);
		viewList.add(multiSpeedPage);
		popUpViewPager.setVisibility(View.VISIBLE);
		indicator.setVisibility(View.VISIBLE);

		onGameListener.gameStart();

		UserManager.getInstance().setMultiPlaying(true);

		UserManager.getInstance().setPlayingTarget(friendName);
	}

	/**
	 * 操作收取到的信息
	 */
	public void handlerMsg() {

		gameMsgReceiver = new GetMessageReceiver();
		gameMsgReceiver.setiCallBack(new ASmackRequestCallBack() {

			@Override
			public void responseSuccess(Object object) {
				Message msg = (Message) object;
				switch (msg.what) {
				case 0: {

					Message message = msg;
					String from = message.getData().getString("from");
					String body = message.getData().getString("body");
					String cmdData = message.getData().getString("cmdData");
					Timber.d("from:" + from + "--body:" + body
							+ "cmdData:" + cmdData);
					Message gameMsg = new Message();
					if (from.equals(friendName)) {
						if (cmdData.equals(Const.Game.REQUESTCONNECTCMD)) {// 判断收到的是不是游戏请求消息
							// 如果此时已经发送过邀请给相对应的好友，则直接可以游戏
							ChatMsgEntity chatMsgEntity = GameManager
									.getInstance()
									.getGameInviteMsgs(friendName);
							if (chatMsgEntity != null
									&& chatMsgEntity.getText().equals(
											GAME_INVITE_BOTH)) {
								gameMsg.what = 1;
								handler.sendMessage(gameMsg);
							} else {
								gameMsg.what = 0;
								handler.sendMessage(gameMsg);
							}

						} else if (cmdData.equals(Const.Game.ACCEPTCONNECTCMD)) {// 判断收到的是不是同意连接消息
							gameMsg.what = 1;
							handler.sendMessage(gameMsg);
							GameManager.getInstance().clearGameInviteMsg();
						} else if (cmdData.equals(Const.Game.REFUSECONNECTCMD)) {// 判断收到的是不是拒绝连接消息
							gameMsg.what = -1;
							handler.sendMessage(gameMsg);
							GameManager.getInstance().delGameInviteMsg(
									friendName);
						} else if (cmdData.equals(Const.Game.DISCONNECTCMD)) {// 判断收到的是不是断开连接
							gameMsg.what = -2;
							handler.sendMessage(gameMsg);
						}
						// 其他普通消息
						ChatMsgEntity entity = UserUtils.setChatMsgEntity(
								body,
								FriendManager.getInstance()
										.getFriendNicknameByFriendName(
												friendName), true);
						onUpdateChatListListener.updateChatList(entity);
					}
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

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.linkcube.message");
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		mContext.registerReceiver(gameMsgReceiver, filter);
	}

	protected boolean startShaken(int speedLevel) {
		if (mShakeSensor.registerListener()) {
			mShakeSensor.setIsMultiGame(true);
			singleChat.sendMsg(friendName, Const.Game.SHAKESPEEDLEVELCMD
					+ speedLevel);
			return true;
		}
		return false;
	}

	private int[] sexPositionModeDrawable = { R.drawable.sex_position_mode_0,
			R.drawable.sex_position_mode_1, R.drawable.sex_position_mode_2,
			R.drawable.sex_position_mode_3, R.drawable.sex_position_mode_4,
			R.drawable.sex_position_mode_5, R.drawable.sex_position_mode_6,
			R.drawable.sex_position_mode_7 };

	/**
	 * 多人游戏发送七种模式信号
	 * 
	 * @param level
	 */
	private void sendSexPositionMode(int level) {
		multiSexPositionModeIv
				.setBackgroundResource(sexPositionModeDrawable[level]);
		String sendStr = Const.Game.POSITIONMODECMD + level;
		singleChat.sendMsg(friendName, sendStr);

	}

	private OnPageChangeListener popupPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			Timber.i("Page-position:" + position);
			popUpViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	public void unRegisteMsgReceiver() {
		mContext.unregisterReceiver(gameMsgReceiver);
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public void setSingleChat(SingleChat singleChat) {
		this.singleChat = singleChat;
	}

	public void setmShakeSensor(ShakeSensor mShakeSensor) {
		this.mShakeSensor = mShakeSensor;
	}

	public void setOnUpdateChatListListener(
			OnUpdateChatListListener onUpdateChatListListener) {
		this.onUpdateChatListListener = onUpdateChatListListener;
	}

	public void setOnGameListener(OnGameListener onGameListener) {
		this.onGameListener = onGameListener;
	}

	public interface OnUpdateChatListListener {
		public void updateChatList(ChatMsgEntity entity);
	}

	public interface OnGameListener {
		public void gameStart();

		public void gameStop();

		public void popUpPanel();
	}
}
