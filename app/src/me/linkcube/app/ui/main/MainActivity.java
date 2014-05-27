package me.linkcube.app.ui.main;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager.CheckConnectionCallback;
import me.linkcube.app.core.toy.ShakeSensor;
import me.linkcube.app.core.toy.VoiceSensor;
import me.linkcube.app.core.update.AppManager;
import me.linkcube.app.core.update.DownloadNewApkHttpGet;
import me.linkcube.app.core.update.DownloadNewApkHttpGet.AppUpdateCallback;
import me.linkcube.app.core.update.UpdateHttpGet;
import me.linkcube.app.core.update.UpdateManager;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.service.ToyServiceConnection;
import me.linkcube.app.sync.chat.ChatMessageListener;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ReconnectionCallBack;
import me.linkcube.app.sync.core.ReconnectionListener;
import me.linkcube.app.ui.BaseFragment;
import me.linkcube.app.ui.BaseFragmentActivity;
import me.linkcube.app.ui.main.TabIndicatorView.OnTabIndicatorClickListener;
import me.linkcube.app.ui.main.multi.MultiPlayerFragment;
import me.linkcube.app.ui.main.single.SinglePalyerFragment;
import me.linkcube.app.ui.setting.SettingActivity;
import me.linkcube.app.ui.user.LoginActivity;
import me.linkcube.app.util.PreferenceUtils;
import me.linkcube.app.widget.AlertUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.widget.Toast;

/**
 * 主页面Activity
 * 
 * @author Orange
 * 
 */
public class MainActivity extends BaseFragmentActivity implements
		OnTabIndicatorClickListener, SensorProvider {

	private BaseFragment singleFragment, multiFragment;

	private ShakeSensor mShakeSensor;

	private VoiceSensor mVoiceSensor;

	private TabIndicatorView tabIndicatorView;

	private ToyServiceConnection toyServiceConnection;

	private int flieMaxSize = 0;

	private Intent msgIntent;
	private static PendingIntent msgPendingIntent;
	private int msgNotificationID = 1100;
	private Notification msgNotification;
	private NotificationManager msgNotificationManager;

	private int LOGOUT__RESULT = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		AppManager.getInstance().addActivity(this);
		tabIndicatorView = (TabIndicatorView) findViewById(R.id.tab_indicator);
		tabIndicatorView.setOnTabIndictorClickListener(this);
		singleFragment = new SinglePalyerFragment();
		multiFragment = new MultiPlayerFragment();
		replaceFragment(singleFragment);
		bindToyService();

		checkAppUpdate();

		CheckDeviceConnect();
		// 友盟统计
		MobclickAgent.onEvent(MainActivity.this, "用户进入应用");
		ReconnectionListener.getInstance().setReconnectionCallBack(
				reconnectionCallBack);

	}

	private void CheckDeviceConnect() {
		DeviceConnectionManager.getInstance().setCheckConnectionCallBack(
				new CheckConnectionCallback() {

					@Override
					public void stable() {

					}

					@Override
					public void interrupted() {
						checkDeviceHandler.sendEmptyMessage(0);

					}

					@Override
					public void disconnect() {

					}
				});

	}

	private Handler checkDeviceHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			((SinglePalyerFragment) singleFragment).resetToy();
			Toast.makeText(MainActivity.this, "玩具已断开，请重新连接", Toast.LENGTH_SHORT)
					.show();
		}

	};

	/**
	 * 检测更新
	 */
	private void checkAppUpdate() {
		int addUpdateFlag = PreferenceUtils.getInt(
				Const.AppUpdate.APK_UPDATE_FLAG, -1);
		Timber.d("MainActivity--addUpdateFlag:" + addUpdateFlag);
		if (addUpdateFlag == -1) {
			PreferenceUtils.setInt(Const.AppUpdate.APK_UPDATE_FLAG, 0);
		}
		// 检测是否需要更新
		UpdateHttpGet updateHttpGet = new UpdateHttpGet(
				new ASmackRequestCallBack() {

					@Override
					public void responseSuccess(Object object) {
						String jsonString = (String) object;
						if (jsonString != null
								&& jsonString.startsWith("\ufeff")) {
							jsonString = jsonString.substring(1);
						}
						try {
							// XXX 常量字符串 和 在MainActivity的检测机制可能需要修改
							JSONObject jsonObject = new JSONObject(jsonString)
									.getJSONObject("updateInfo");
							PreferenceUtils.setString(
									Const.AppUpdate.APK_VERSION,
									jsonObject.getString("version"));
							boolean force = jsonObject.getBoolean("force");
							PreferenceUtils.setString(Const.AppUpdate.APK_SIZE,
									jsonObject.getString("size"));
							PreferenceUtils.setString(
									Const.AppUpdate.APK_DOWNLOAD_URL,
									jsonObject.getString("downloadURL"));
							PreferenceUtils.setString(
									Const.AppUpdate.APK_DESCRIPTION,
									jsonObject.getString("description"));
							UpdateManager.getInstance().setForcedUpdate(force);// force
							UpdateManager.getInstance().checkNeedUpdate(
									MainActivity.this,
									PreferenceUtils.getString(
											Const.AppUpdate.APK_VERSION, null));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						int addUpdateFlag = PreferenceUtils.getInt(
								Const.AppUpdate.APK_UPDATE_FLAG, 0);
						Timber.d("addUpdateFlag:" + addUpdateFlag);

						if (UpdateManager.getInstance().isForcedUpdate()) {// UpdateManager.getInstance().isForcedUpdate()
							// 需要强制更新，取消则退出程序
							showForceUpdateDialog();
						} else if (UpdateManager.getInstance().isUpdate()
								&& addUpdateFlag == 1) {
							showUpdateDialog();
						}
					}

					@Override
					public void responseFailure(int reflag) {

					}
				});

	}

	private void bindToyService() {
		toyServiceConnection = new ToyServiceConnection();
		Intent toyintent = new Intent(this,
				me.linkcube.app.service.ToyService.class);
		this.startService(toyintent);
		bindService(toyintent, toyServiceConnection, Context.BIND_AUTO_CREATE);
		mShakeSensor = new ShakeSensor(this.getApplicationContext());
		mVoiceSensor = new VoiceSensor();
	}

	@Override
	public void onSelectSingleTab() {
		replaceFragment(singleFragment);

	}

	@Override
	public void onSelectMultiTab() {
		replaceFragment(multiFragment);
	}

	private void replaceFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragment_content, fragment);
		// 为了退出登录后返回单人界面此处做了修改(transaction.commit())
		transaction.commitAllowingStateLoss();
	}

	@Override
	public ShakeSensor getShakeSensor() {
		return mShakeSensor;
	}

	@Override
	public VoiceSensor getVoiceSensor() {
		return mVoiceSensor;
	}

	@Override
	public void showLoginActivity() {
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
	}

	private ReconnectionCallBack reconnectionCallBack = new ReconnectionCallBack() {

		@Override
		public void reconnectionSuccessful() {
			Message msg = new Message();
			msg.obj = "重新连接成功";
			handler.sendMessage(msg);
			ChatMessageListener.getInstance().onMessageListener(mActivity);
			UserManager.getInstance().setUserStateAvailable();
		}

		@Override
		public void reconnectionFailed() {
			// 重新连接失败
		}

		@Override
		public void reconnectingIn() {
			// 正在重新连接
		}

	};
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String showMsg = (String) msg.obj;
			Toast.makeText(MainActivity.this, showMsg, Toast.LENGTH_SHORT)
					.show();
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == LOGOUT__RESULT) {

			tabIndicatorView.setCurrentTab(0);
		}
	};

	/**
	 * 更新Dialog
	 */
	private void showUpdateDialog() {
		AlertUtils.showAlert(
				this,
				Html.fromHtml(
						"版本号："
								+ PreferenceUtils.getString(
										Const.AppUpdate.APK_VERSION, null)
								+ "<br>大小："
								+ PreferenceUtils.getString(
										Const.AppUpdate.APK_SIZE, null)
								+ "<br>描述：<br>"
								+ PreferenceUtils.getString(
										Const.AppUpdate.APK_DESCRIPTION, null))
						.toString(), "发现新版本", "马上更新", "下次再说",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						initNotification(mActivity);
						DownloadNewApkHttpGet downloadNewApkHttpGet = new DownloadNewApkHttpGet();
						downloadNewApkHttpGet
								.setAppUpdateCallback(new AppUpdateCallback() {

									@Override
									public void beforeApkDOwnload(int fileLength) {
										flieMaxSize = fileLength;
										Timber.d("flieMaxSize:" + flieMaxSize);
									}

									@Override
									public void inApkDownload(
											int downLoadFileSize) {
										float percent = (float) downLoadFileSize
												* 100 / flieMaxSize;
										Timber.d("percent:" + (int) percent);
									}

									@Override
									public void afterApkDownload(int reFlag) {
										msgNotificationManager.cancel(msgNotificationID);
									}

									@Override
									public void FailureApkDownload(int reFlag) {
										failureUpdateHandler.sendEmptyMessage(0);
										msgNotificationManager.cancel(msgNotificationID);
									}
								});
						downloadNewApkHttpGet.downloadNewApkFile(mActivity,
								PreferenceUtils.getString(
										Const.AppUpdate.APK_DOWNLOAD_URL, null));
						UpdateManager.getInstance().setUpdate(false);
					}
				}, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						PreferenceUtils.setInt(Const.AppUpdate.APK_UPDATE_FLAG,
								2);
						UpdateManager.getInstance().setUpdate(false);
					}

				});
	}

	/**
	 * 强制更新Dialog
	 */
	private void showForceUpdateDialog() {
		AlertUtils.showAlert(this, "因部分原因，当前软件需要强制更新。给您带来的不便深表歉意！", "提示", "更新",
				"取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						showProgressDialog("正在下载更新文件，请等待");
						DownloadNewApkHttpGet downloadNewApkHttpGet = new DownloadNewApkHttpGet();
						downloadNewApkHttpGet
								.setAppUpdateCallback(new AppUpdateCallback() {

									@Override
									public void beforeApkDOwnload(int fileLength) {
										flieMaxSize = fileLength;
										Timber.d("flieMaxSize:" + flieMaxSize);
									}

									@Override
									public void inApkDownload(
											int downLoadFileSize) {
										float percent = (float) downLoadFileSize
												* 100 / flieMaxSize;
										Timber.d("percent:" + percent);
									}

									@Override
									public void afterApkDownload(int reFlag) {
										dismissProgressDialog();
									}

									@Override
									public void FailureApkDownload(int reFlag) {
										dismissProgressDialog();
										AppManager.getInstance().AppExit(
												mActivity);
									}
								});

						downloadNewApkHttpGet.downloadNewApkFile(mActivity,
								PreferenceUtils.getString(
										Const.AppUpdate.APK_DOWNLOAD_URL, null));
						UpdateManager.getInstance().setUpdate(false);
					}
				}, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						UpdateManager.getInstance().setUpdate(false);
						AppManager.getInstance().AppExit(mActivity);
						dialog.dismiss();
					}

				});
	}
	
	private Handler failureUpdateHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(MainActivity.this,
					"网络异常，文件下载失败",
					Toast.LENGTH_SHORT).show();
		}
		
	};
	
	private void initNotification(Context context) {
		msgNotification = new Notification();
		msgNotification.icon = R.drawable.ic_launcher;
		//msgNotification.defaults = Notification.DEFAULT_SOUND;
		msgNotification.flags = Notification.FLAG_NO_CLEAR;
		msgNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		msgIntent = new Intent();// Class.forName("com.oplibs.controll.test.TestMainActivity")
		msgIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		msgPendingIntent = PendingIntent.getActivity(mActivity, 0, msgIntent,
				0);

		msgNotification.setLatestEventInfo(mActivity, "连酷","正在下载更新文件", msgPendingIntent);

		msgNotificationManager.notify(msgNotificationID, msgNotification);

	}

}
