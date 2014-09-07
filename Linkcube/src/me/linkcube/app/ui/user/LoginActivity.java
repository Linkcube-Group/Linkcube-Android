package me.linkcube.app.ui.user;

import me.linkcube.app.R;
import me.linkcube.app.common.ui.DialogActivity;
import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ReconnectionListener;
import me.linkcube.app.sync.user.UserLogin;
import me.linkcube.app.ui.main.MainActivity;
import me.linkcube.app.widget.CWClearEditText;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class LoginActivity extends DialogActivity implements OnClickListener {

	private Button loginBtn;

	private Button registerBtn;

	private CWClearEditText usernameEt;

	private CWClearEditText passwordEt;

	private CheckBox rememberMeCheckbox;

	// private TextView findPasswordTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		configureActionBar(R.string.login);
		initView();
		initData();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		usernameEt = (CWClearEditText) findViewById(R.id.user_name);
		passwordEt = (CWClearEditText) findViewById(R.id.pass_word);
		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(this);
		registerBtn = (Button) findViewById(R.id.register_btn);
		registerBtn.setOnClickListener(this);
		rememberMeCheckbox = (CheckBox) findViewById(R.id.remember_me_checkbox);
		rememberMeCheckbox.setOnClickListener(this);
		// findPasswordTv = (TextView) findViewById(R.id.find_password_tv);
		// findPasswordTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.login_btn:
			showProgressDialog(getResources().getString(
					R.string.toast_login_and_waiting));
			if (usernameEt.getText().toString().equals("")
					|| passwordEt.getText().toString().equals("")) {
				Toast.makeText(LoginActivity.this,
						R.string.toast_name_psw_not_null, Toast.LENGTH_SHORT)
						.show();
				dismissProgressDialog();
			} else {
				if (rememberMeCheckbox.isChecked()) {
					PreferenceUtils.setString(Const.Preference.USER_NAME,
							usernameEt.getText().toString());
					PreferenceUtils.setString(Const.Preference.USER_PWD,
							passwordEt.getText().toString());
					PreferenceUtils.setBoolean(Const.Preference.AUTO_LOGIN,
							true);
				} else {
					PreferenceUtils.removeData(Const.Preference.USER_NAME);
					PreferenceUtils.removeData(Const.Preference.USER_PWD);
					PreferenceUtils.setBoolean(Const.Preference.AUTO_LOGIN,
							false);
				}
				if (ASmackManager.getInstance().getXMPPConnection() == null
						|| !ASmackManager.getInstance().getXMPPConnection()
								.isConnected()) {
					ASmackManager.getInstance().setupConnection(
							new ASmackRequestCallBack() {

								@Override
								public void responseSuccess(Object object) {
									UserManager.getInstance().initListener(
											mActivity);
									loginEvent(usernameEt.getText().toString(),
											passwordEt.getText().toString());
								}

								@Override
								public void responseFailure(int reflag) {
									dismissProgressDialog();
									Toast.makeText(LoginActivity.this,
											R.string.toast_check_network,
											Toast.LENGTH_SHORT).show();
								}
							});
				} else {
					loginEvent(usernameEt.getText().toString(), passwordEt
							.getText().toString());
				}
			}

			break;
		case R.id.register_btn:
			startActivity(new Intent(mActivity, RegisterActivity.class));
			break;
		case R.id.remember_me_checkbox:
			if (rememberMeCheckbox.isChecked()) {
				rememberMeCheckbox.setChecked(true);
			} else if (!rememberMeCheckbox.isChecked()) {
				PreferenceUtils.removeData(Const.Preference.USER_NAME);
				PreferenceUtils.removeData(Const.Preference.USER_PWD);
				rememberMeCheckbox.setChecked(false);
			}
			break;
		/*
		 * case R.id.find_password_tv: Intent intent = new
		 * Intent(LoginActivity.this, FindPasswordActivity.class);
		 * startActivity(intent); break;
		 */
		default:
			break;
		}
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		try {
			if (PreferenceUtils.contains(Const.Preference.USER_NAME)
					&& PreferenceUtils.contains(Const.Preference.USER_PWD)) {
				rememberMeCheckbox.setChecked(true);
				String userName = PreferenceUtils.getString(
						Const.Preference.USER_NAME, "");
				String passWord = PreferenceUtils.getString(
						Const.Preference.USER_PWD, "");
				usernameEt.setText(userName);
				passwordEt.setText(passWord);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 登陆事件
	 */
	private void loginEvent(String userName, String passWord) {
		new UserLogin(ASmackUtils.userNameEncode(userName), passWord,
				new ASmackRequestCallBack() {

					@Override
					public void responseSuccess(Object object) {
						final VCard vCard = (VCard) object;
						try {
							vCard.load(ASmackManager.getInstance()
									.getXMPPConnection());
						} catch (XMPPException e) {
							e.printStackTrace();
						}

						UserManager.getInstance().firstLoginGetAllfriends(
								mActivity);

						Thread thread = new Thread() {
							@Override
							public void run() {
								try {
									sleep(5000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								UserManager.getInstance()
										.setUserStateAvailable();

								Timber.d("userInfo:" + vCard.toXML());

								UserManager.getInstance().saveUserInfo(
										mActivity, vCard);

								dismissProgressDialog();
								ReconnectionListener.getInstance()
										.onReconnectionListener();

								startActivity(new Intent(LoginActivity.this,
										MainActivity.class));
								finish();
								// overridePendingTransition(android.R.anim.slide_in_left,
								// android.R.anim.slide_out_right);

							}

						};
						thread.start();

					}

					@Override
					public void responseFailure(int reflag) {
						if (reflag == 1) {
							dismissProgressDialog();
							Toast.makeText(LoginActivity.this,
									R.string.toast_has_login,
									Toast.LENGTH_SHORT).show();
						} else if (reflag == -1) {
							dismissProgressDialog();
							Toast.makeText(LoginActivity.this,
									R.string.toast_check_network,
									Toast.LENGTH_SHORT).show();
						} else if (reflag == -2) {
							dismissProgressDialog();
							Toast.makeText(LoginActivity.this,
									R.string.toast_name_psw_wrong,
									Toast.LENGTH_SHORT).show();
						}

					}
				});
	}

}
