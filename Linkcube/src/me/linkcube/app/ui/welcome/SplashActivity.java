package me.linkcube.app.ui.welcome;

import java.util.Locale;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import com.umeng.analytics.MobclickAgent;

import me.linkcube.app.common.ui.DialogActivity;
import me.linkcube.app.common.util.AlertUtils;
import me.linkcube.app.common.util.AppUtils;
import me.linkcube.app.common.util.NetworkUtils;
import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import static me.linkcube.app.core.Const.Preference.AUTO_LOGIN;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.update.AppManager;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ReconnectionListener;
import me.linkcube.app.sync.user.UserLogin;
import me.linkcube.app.ui.main.MainActivity;
import me.linkcube.app.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

/**
 * 欢迎页面
 * 
 * @author Ervin
 * 
 */
public class SplashActivity extends DialogActivity {

	private boolean isShowGuide;

	// private UserEntity user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_activity);
		Timber.d("数据库初始化");
		DataManager.getInstance().initDatabase(
				mActivity.getApplicationContext());
		Timber.d("键值存储初始化");
		PreferenceUtils.initDataShare(getApplicationContext());
		
		setLanguage();

		MobclickAgent.updateOnlineConfig(mActivity);
		MobclickAgent.openActivityDurationTrack(false);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				init();
			}
		}, 0);
	}

	private void init() {
		boolean status = NetworkUtils.isNetworkAvailable(this);
		isShowGuide = AppUtils.isShowGuide();
		Timber.d("检查本地网络");
		if (!status) {
			showNetworkDialog();
			return;
		} else {
			showProgressDialog(getResources().getString(R.string.toast_connect_network_wait));
			if (ASmackManager.getInstance().getXMPPConnection() == null
					|| !ASmackManager.getInstance().getXMPPConnection()
							.isConnected()) {
				ASmackManager.getInstance().setupConnection(
						new ASmackRequestCallBack() {

							@Override
							public void responseSuccess(Object object) {
								UserManager.getInstance().initListener(
										mActivity);
								if (PreferenceUtils.getBoolean(AUTO_LOGIN,
										false)) {
									dismissProgressDialog();
									if (PreferenceUtils
											.contains(Const.Preference.USER_NAME)
											&& PreferenceUtils
													.contains(Const.Preference.USER_PWD)) {
										autoLogin();
										Timber.d("正在自动登录");
									} else {
										startActivity(new Intent(mActivity,
												MainActivity.class));
										finish();
									}
								} else {
									dismissProgressDialog();
									if (isShowGuide) {
										startActivity(new Intent(mActivity,
												GuideActivity.class));
										finish();
									} else {
										startActivity(new Intent(
												SplashActivity.this,
												MainActivity.class));
										finish();
									}
								}
							}

							@Override
							public void responseFailure(int reflag) {
								dismissProgressDialog();
								Toast.makeText(SplashActivity.this, R.string.toast_network_wrong_try_again,
										Toast.LENGTH_SHORT).show();
								startActivity(new Intent(mActivity,
										MainActivity.class));
								finish();
							}
						});
			} else if (ASmackManager.getInstance().getXMPPConnection()
					.isConnected()) {

				startActivity(new Intent(mActivity, MainActivity.class));
				finish();
			}
		}

	}
	
	private void setLanguage(){
		Resources resources=getResources();
		Configuration config=resources.getConfiguration();
		DisplayMetrics dm= resources.getDisplayMetrics();
		
		switch (PreferenceUtils.getInt("app_language", 0)) {
		case 0:
			config.locale = Locale.SIMPLIFIED_CHINESE;
			break;
			
		case 1:
			config.locale = Locale.ENGLISH;
			break;

		default:
			break;
		}
		resources.updateConfiguration(config, dm);
	}

	private void showNetworkDialog() {
		AlertUtils.showAlert(this, "请确认您可以访问互联网，若有疑问请与客服人员联系。", "提示", "设置",
				"取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 打开wifi设置页面
						if (android.os.Build.VERSION.SDK_INT > 10) {
							// 3.0以上打开设置界面
							startActivity(new Intent(
									android.provider.Settings.ACTION_SETTINGS));
						} else {
							startActivity(new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
						finish();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(new Intent(mActivity, MainActivity.class));
						finish();
					}

				});
	}

	private void autoLogin() {
		showProgressDialog(getResources().getString(R.string.toast_auto_login_wait));
		new UserLogin(ASmackUtils.userNameEncode(PreferenceUtils.getString(
				Const.Preference.USER_NAME, "")), PreferenceUtils.getString(
				Const.Preference.USER_PWD, ""), new ASmackRequestCallBack() {

			@Override
			public void responseSuccess(Object object) {
				final VCard vCard = (VCard) object;
				try {
					vCard.load(ASmackManager.getInstance().getXMPPConnection());
				} catch (XMPPException e) {
					e.printStackTrace();
				}
				// UserManager.getInstance().getAllfriends(mActivity);

				UserManager.getInstance().setUserStateAvailable();

				Timber.d("userInfo:" + vCard.toXML());

				UserManager.getInstance().saveUserInfo(mActivity, vCard);
				//Toast.makeText(SplashActivity.this, "自动登录成功",Toast.LENGTH_SHORT).show();

				ReconnectionListener.getInstance().onReconnectionListener();
				dismissProgressDialog();
				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));
				finish();
			}

			@Override
			public void responseFailure(int reflag) {
				if (reflag == 1) {
					Toast.makeText(SplashActivity.this, R.string.toast_has_login,
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					dismissProgressDialog();
				} else if (reflag == -1) {
					Toast.makeText(SplashActivity.this, R.string.toast_check_network,
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					dismissProgressDialog();
				} else if (reflag == -2) {
					Toast.makeText(SplashActivity.this, R.string.toast_name_psw_wrong,
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					dismissProgressDialog();
				} else {
					Toast.makeText(SplashActivity.this, R.string.toast_not_exist_this_user,
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					dismissProgressDialog();
				}

			}
		});
	}

}
