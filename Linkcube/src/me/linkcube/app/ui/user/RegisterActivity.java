package me.linkcube.app.ui.user;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import me.linkcube.app.R;
import me.linkcube.app.common.ui.DialogActivity;
import me.linkcube.app.common.util.RegexUtils;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.user.UserLogin;
import me.linkcube.app.sync.user.UserRegister;
import me.linkcube.app.widget.CWClearEditText;

public class RegisterActivity extends DialogActivity implements OnClickListener {

	private CWClearEditText usernameEt;

	private CWClearEditText passwordEt;

	private CWClearEditText confirmPasswordEt;

	private Button registerBtn;

	private TextView registerProtocolTv;

	private String userName;
	private String Password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		configureActionBar(R.string.register);
		initView();
		initData();
	}

	private void initView() {
		registerBtn = (Button) findViewById(R.id.register_btn);
		registerBtn.setOnClickListener(this);
		usernameEt = (CWClearEditText) findViewById(R.id.login_email_et);
		passwordEt = (CWClearEditText) findViewById(R.id.login_password_et);
		confirmPasswordEt = (CWClearEditText) findViewById(R.id.login_confirm_password_et);
		registerProtocolTv = (TextView) findViewById(R.id.register_protocol_tv);
		registerProtocolTv.setOnClickListener(this);
	}

	private void initData() {
		registerProtocolTv.setTextColor(Color.GRAY);
		registerProtocolTv.setText(Html.fromHtml("<u>《"
				+ getResources().getString(R.string.linkcube_terms_and_conditions) + "》</u>"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_btn:

			// 邮箱验证
			userName = usernameEt.getText().toString();
			Password = passwordEt.getText().toString();
			String confirmPwd = confirmPasswordEt.getText().toString();
			if (!RegexUtils.isEmailAddress(userName)) {
				Toast.makeText(RegisterActivity.this,
						R.string.toast_username_hasbeen_email,
						Toast.LENGTH_SHORT).show();
			} else if (Password.length() < 6) {
				Toast.makeText(RegisterActivity.this,
						R.string.toast_psw_too_short, Toast.LENGTH_SHORT)
						.show();
			} else if (ASmackUtils.containWhiteSpace(Password)) {
				Toast.makeText(RegisterActivity.this,
						R.string.toast_psw_couldnot_contain_space,
						Toast.LENGTH_SHORT).show();
			} else if (!Password.equals(confirmPwd)) {
				Toast.makeText(RegisterActivity.this,
						R.string.toast_psw_not_match, Toast.LENGTH_SHORT)
						.show();
			} else {
				userName = ASmackUtils.userNameEncode(userName);
				Timber.d("userName:" + userName);

				if (ASmackManager.getInstance().getXMPPConnection() == null
						|| !ASmackManager.getInstance().getXMPPConnection()
								.isConnected()) {
					ASmackManager.getInstance().setupConnection(
							new ASmackRequestCallBack() {

								@Override
								public void responseSuccess(Object object) {
									UserManager.getInstance().initListener(
											mActivity);
									userRegisterEvent();
								}

								@Override
								public void responseFailure(int reflag) {

								}
							});
				} else {
					userRegisterEvent();
				}

			}
			break;
		case R.id.register_protocol_tv:
			startActivity(new Intent(RegisterActivity.this,
					UserAgreementActivity.class));
			break;
		default:
			break;
		}
	}

	public void userRegisterEvent() {
		new UserRegister(userName, Password, new ASmackRequestCallBack() {
			@Override
			public void responseSuccess(Object object) {

				// Toast.makeText(RegisterActivity.this,
				// "注册成功",Toast.LENGTH_SHORT).show();
				// 注册成功后直接登陆
				// showProgressDialog("正在登陆哦，请稍后...");
				new UserLogin(ASmackUtils.userNameEncode(usernameEt.getText()
						.toString()), passwordEt.getText().toString(),
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

								UserManager.getInstance().initListener(
										mActivity);
								// UserManager.getInstance().getAllfriends(mActivity);注册的时候没有好友

								Thread thread = new Thread() {
									@Override
									public void run() {
										try {
											sleep(3000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										UserManager.getInstance()
												.setUserStateAvailable();

										Timber.d("userInfo:" + vCard.toXML());

										// dismissProgressDialog();
										Intent intent = new Intent(
												RegisterActivity.this,
												InitUserInfoActivity.class);
										startActivity(intent);
									}

								};

								thread.start();
								Toast.makeText(RegisterActivity.this,
										R.string.toast_register_success,
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void responseFailure(int reflag) {
								if (reflag == 1) {
									// dismissProgressDialog();
									Toast.makeText(RegisterActivity.this,
											R.string.toast_has_login,
											Toast.LENGTH_SHORT).show();
								} else if (reflag == -1) {
									// dismissProgressDialog();
									Toast.makeText(RegisterActivity.this,
											R.string.toast_check_network,
											Toast.LENGTH_SHORT).show();
								}

							}
						});
			}

			@Override
			public void responseFailure(int reflag) {
				Timber.d("reflag:" + reflag);
				if (reflag == 1) {
					Toast.makeText(RegisterActivity.this,
							R.string.toast_server_noresponse,
							Toast.LENGTH_SHORT).show();
				} else if (reflag == 2) {
					Toast.makeText(RegisterActivity.this,
							R.string.toast_email_has_used, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(RegisterActivity.this,
							R.string.toast_network_wrong_try_again,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
