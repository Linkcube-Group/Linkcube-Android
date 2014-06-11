package me.linkcube.app.ui.setting;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.update.DownloadNewApkHttpGet;
import me.linkcube.app.core.update.UpdateManager;
import me.linkcube.app.core.update.DownloadNewApkHttpGet.AppUpdateCallback;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.chat.ChatMessageListener;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.friend.AddFriendListener;
import me.linkcube.app.ui.DialogActivity;
import me.linkcube.app.ui.bluetooth.BluetoothSettingActivity;
import me.linkcube.app.ui.user.LoginActivity;
import me.linkcube.app.ui.user.UserInfoActivity;
import me.linkcube.app.util.PreferenceUtils;
import me.linkcube.app.widget.AlertUtils;

public class SettingActivity extends DialogActivity implements OnClickListener {

	private TextView connectToyTv, personalInfoTv, purchaseToyTv, helpTv,
			feedbackTv, aboutUsTv, checkUpdateTv, setLanguageTv;

	private Button loginOrRegisterBtn;

	private ImageView newVertionTipIv;

	private int LOGOUT__RESULT = 3;

	private int flieMaxSize = 0;

	private int addUpdateFlag = -1;

	private Intent msgIntent;
	private static PendingIntent msgPendingIntent;

	private int msgNotificationID = 1100;
	private Notification msgNotification;
	private NotificationManager msgNotificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		configureActionBar(R.string.setting);

		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateLoginOrRegisterBtn(UserManager.getInstance().isAuthenticated());
	}

	private void initView() {
		connectToyTv = (TextView) findViewById(R.id.connect_toy_tv);
		personalInfoTv = (TextView) findViewById(R.id.personal_info_tv);
		purchaseToyTv = (TextView) findViewById(R.id.purchase_toy_tv);
		helpTv = (TextView) findViewById(R.id.help_tv);
		feedbackTv = (TextView) findViewById(R.id.feedback_tv);
		aboutUsTv = (TextView) findViewById(R.id.about_us_tv);
		checkUpdateTv = (TextView) findViewById(R.id.checkupdate_tv);
		loginOrRegisterBtn = (Button) findViewById(R.id.login_or_register_btn);
		newVertionTipIv = (ImageView) findViewById(R.id.new_version_tip_iv);
		setLanguageTv=(TextView)findViewById(R.id.set_language_tv);
		connectToyTv.setOnClickListener(this);
		personalInfoTv.setOnClickListener(this);
		purchaseToyTv.setOnClickListener(this);
		helpTv.setOnClickListener(this);
		feedbackTv.setOnClickListener(this);
		aboutUsTv.setOnClickListener(this);
		loginOrRegisterBtn.setOnClickListener(this);
		checkUpdateTv.setOnClickListener(this);
		setLanguageTv.setOnClickListener(this);

		addUpdateFlag = PreferenceUtils.getInt(Const.AppUpdate.APK_UPDATE_FLAG,
				0);
		Timber.d("addUpdateFlag:" + addUpdateFlag);
		if (addUpdateFlag == 2) {
			newVertionTipIv.setVisibility(View.VISIBLE);
		}
	}

	private void updateLoginOrRegisterBtn(boolean isAuthenticated) {
		if (!isAuthenticated) {
			loginOrRegisterBtn.setText(R.string.login_or_register);
			loginOrRegisterBtn.setBackgroundResource(R.drawable.btn_pink);
		} else {
			loginOrRegisterBtn.setText(R.string.logout);
			loginOrRegisterBtn.setBackgroundResource(R.drawable.btn_dark);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.connect_toy_tv:
			startActivity(new Intent(mActivity, BluetoothSettingActivity.class));
			break;
		case R.id.personal_info_tv:
			if (!UserManager.getInstance().isAuthenticated()) {
				startActivity(new Intent(mActivity, LoginActivity.class));
			} else {
				startActivity(new Intent(mActivity, UserInfoActivity.class));
			}
			break;
		case R.id.purchase_toy_tv:

			break;
		case R.id.help_tv:

			break;
		case R.id.feedback_tv:
			startActivity(new Intent(SettingActivity.this,
					FeedbackActivity.class));
			break;
		case R.id.about_us_tv:
			startActivity(new Intent(this, AboutUsActivity.class));
			break;
		case R.id.login_or_register_btn:
			if (UserManager.getInstance().isAuthenticated()) {
				logout();
			} else {
				startActivity(new Intent(SettingActivity.this,
						LoginActivity.class));
			}
			break;
		case R.id.checkupdate_tv:
			if (addUpdateFlag == 2) {
				showUpdateDialog();
			}
			break;
		case R.id.set_language_tv:
			
			new AlertDialog.Builder(this)
			.setTitle("语言设置")
			.setSingleChoiceItems(new String[] {"中文简体","english"}, 0, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Resources resources=getResources();
					Configuration config=resources.getConfiguration();
					DisplayMetrics dm= resources.getDisplayMetrics();
					switch (which) {
					case 0:
						config.locale = Locale.SIMPLIFIED_CHINESE;
						resources.updateConfiguration(config, dm);
						PreferenceUtils.setInt("app_language", 0);
						dialog.dismiss();
						//刷新界面
						Intent intent = new Intent();
						intent.setClass(SettingActivity.this,SettingActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						SettingActivity.this.startActivity(intent);
						break;
					case 1:
						config.locale = Locale.ENGLISH;
						resources.updateConfiguration(config, dm);
						PreferenceUtils.setInt("app_language", 1);
						dialog.dismiss();
						//刷新界面
						Intent intent2 = new Intent();
						intent2.setClass(SettingActivity.this,SettingActivity.class);
						intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						SettingActivity.this.startActivity(intent2);
						break;
					default:
						break;
					}
				}
			})
			.setNegativeButton("取消", null)
			.show();
			break;
		default:
			break;
		}

	}

	private void logout() {
		ASmackManager.getInstance().closeConnection();
		ASmackUtils.ROSTER_NAME = null;
		ChatMessageListener.getInstance().setChatManager(null);
		AddFriendListener.getInstance().setAddFriendStatus(false);
		UserManager.getInstance().setFirstLogin(true);
		setResult(LOGOUT__RESULT);
		finish();
	}

	/*
	 * //退出登录清空用户数据 public interface clearUserData{ public void deleteData(); }
	 */
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
				new DialogInterface.OnClickListener() {

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
										msgNotificationManager
												.cancel(msgNotificationID);
									}

									@Override
									public void FailureApkDownload(int reFlag) {
										failureUpdateHandler
												.sendEmptyMessage(0);
										msgNotificationManager
												.cancel(msgNotificationID);
									}
								});
						downloadNewApkHttpGet.downloadNewApkFile(mActivity,
								PreferenceUtils.getString(
										Const.AppUpdate.APK_DOWNLOAD_URL, null));
						UpdateManager.getInstance().setUpdate(false);
					}
				}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						PreferenceUtils.setInt(Const.AppUpdate.APK_UPDATE_FLAG,
								2);
						UpdateManager.getInstance().setUpdate(false);
					}

				});
	}

	private Handler failureUpdateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(SettingActivity.this, R.string.toast_network_error_download_file_failure,
					Toast.LENGTH_SHORT).show();
		}

	};

	private void initNotification(Context context) {
		msgNotification = new Notification();
		msgNotification.icon = R.drawable.ic_launcher;
		// msgNotification.defaults = Notification.DEFAULT_SOUND;
		msgNotification.flags = Notification.FLAG_NO_CLEAR;
		msgNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		msgIntent = new Intent();// Class.forName("com.oplibs.controll.test.TestMainActivity")
		msgIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		msgPendingIntent = PendingIntent
				.getActivity(mActivity, 0, msgIntent, 0);

		msgNotification.setLatestEventInfo(mActivity, getResources().getString(R.string.app_name), getResources().getString(R.string.update_download_flie),
				msgPendingIntent);

		msgNotificationManager.notify(msgNotificationID, msgNotification);

	}
}
