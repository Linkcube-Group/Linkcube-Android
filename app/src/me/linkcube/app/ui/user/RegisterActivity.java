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
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.user.UserLogin;
import me.linkcube.app.sync.user.UserRegister;
import me.linkcube.app.ui.DialogActivity;
import me.linkcube.app.util.RegexUtils;
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
		confirmPasswordEt= (CWClearEditText) findViewById(R.id.login_confirm_password_et);
		registerProtocolTv=(TextView) findViewById(R.id.register_protocol_tv);
		registerProtocolTv.setOnClickListener(this);
	}
	
	private void initData(){
		registerProtocolTv.setTextColor(Color.GRAY);
		registerProtocolTv.setText(Html.fromHtml("<u>《连酷软件许可及服务协议》</u>"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_btn:

			//邮箱验证
			userName = usernameEt.getText().toString();
			Password = passwordEt.getText().toString();
			String confirmPwd = confirmPasswordEt.getText().toString();
			if (!RegexUtils.isEmailAddress(userName)) {
				Toast.makeText(RegisterActivity.this, "用户名必须为邮箱",
						Toast.LENGTH_SHORT).show();
			} else if (Password.length() < 6) {
				Toast.makeText(RegisterActivity.this, "密码长度太短",
						Toast.LENGTH_SHORT).show();
			} else if (ASmackUtils.containWhiteSpace(Password)) {
				Toast.makeText(RegisterActivity.this, "密码中不能包含空格",
						Toast.LENGTH_SHORT).show();
			} else if (!Password.equals(confirmPwd)) {
				Toast.makeText(RegisterActivity.this, "密码不一致，请重新输入",
						Toast.LENGTH_SHORT).show();
			} else {
				userName = ASmackUtils.userNameEncode(userName);
				Timber.d("userName:" + userName);
				
				if (ASmackManager.getInstance().getXMPPConnection() == null
						|| !ASmackManager.getInstance().getXMPPConnection()
								.isConnected()) {
					ASmackManager.getInstance().setupConnection(new ASmackRequestCallBack() {
						
						@Override
						public void responseSuccess(Object object) {
							UserManager.getInstance().initListener(
									mActivity);
							
							new UserRegister(userName, Password,
									new ASmackRequestCallBack() {
										@Override
										public void responseSuccess(Object object) {

											Toast.makeText(RegisterActivity.this, "注册成功",
													Toast.LENGTH_SHORT).show();
											// 注册成功后直接登陆
											showProgressDialog("正在登陆哦，请稍后...");
											new UserLogin(
													ASmackUtils.userNameEncode(usernameEt
															.getText().toString()),
													passwordEt.getText().toString(),
													new ASmackRequestCallBack() {

														@Override
														public void responseSuccess(
																Object object) {
															final VCard vCard = (VCard) object;
															try {
																vCard.load(ASmackManager
																		.getInstance()
																		.getXMPPConnection());
															} catch (XMPPException e) {
																e.printStackTrace();
															}

															UserManager
																	.getInstance()
																	.initListener(mActivity);
															//UserManager.getInstance().getAllfriends(mActivity);注册的时候没有好友

															Thread thread = new Thread() {
																@Override
																public void run() {
																	try {
																		sleep(3000);
																	} catch (InterruptedException e) {
																		e.printStackTrace();
																	}
																	UserManager
																			.getInstance()
																			.setUserStateAvailable();

																	Timber.d("userInfo:"
																			+ vCard.toXML());

																	dismissProgressDialog();
																	Intent intent = new Intent(
																			RegisterActivity.this,
																			InitUserInfoActivity.class);
																	startActivity(intent);
																}

															};

															thread.start();
															Toast.makeText(
																	RegisterActivity.this,
																	"登陆成功",
																	Toast.LENGTH_SHORT)
																	.show();
														}

														@Override
														public void responseFailure(
																int reflag) {
															if (reflag == 1) {
																dismissProgressDialog();
																Toast.makeText(
																		RegisterActivity.this,
																		"已经登录",
																		Toast.LENGTH_SHORT)
																		.show();
															} else if (reflag == -1) {
																dismissProgressDialog();
																Toast.makeText(
																		RegisterActivity.this,
																		"网络错误请检查",
																		Toast.LENGTH_SHORT)
																		.show();
															}

														}
													});
										}

										@Override
										public void responseFailure(int reflag) {
											Timber.d("reflag:"+reflag);
											if(reflag==1){
												Toast.makeText(RegisterActivity.this, "服务器无响应",
														Toast.LENGTH_SHORT).show();
											}else if(reflag==2){
												Toast.makeText(RegisterActivity.this, "当前邮箱已被使用",
														Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(RegisterActivity.this, "网络异常，请重试",
														Toast.LENGTH_SHORT).show();
											}
										}
									});
							
						}
						
						@Override
						public void responseFailure(int reflag) {
							
						}
					});
				}
				
			}
			break;
		case R.id.register_protocol_tv:
			startActivity(new Intent(RegisterActivity.this,UserAgreementActivity.class));
			break;
		default:
			break;
		}
	}

}
