package me.linkcube.app.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import me.linkcube.app.R;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.chat.ChatMessageListener;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.ui.BaseActivity;
import me.linkcube.app.ui.bluetooth.BluetoothSettingActivity;
import me.linkcube.app.ui.user.LoginActivity;
import me.linkcube.app.ui.user.UserInfoActivity;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private TextView connectToyTv,personalInfoTv, purchaseToyTv, helpTv, feedbackTv,
			aboutUsTv;

	private Button loginOrRegisterBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		configureActionBar(R.string.setting);
		connectToyTv=(TextView)findViewById(R.id.connect_toy_tv);
		personalInfoTv = (TextView) findViewById(R.id.personal_info_tv);
		purchaseToyTv = (TextView) findViewById(R.id.purchase_toy_tv);
		helpTv = (TextView) findViewById(R.id.help_tv);
		feedbackTv = (TextView) findViewById(R.id.feedback_tv);
		aboutUsTv = (TextView) findViewById(R.id.about_us_tv);
		loginOrRegisterBtn = (Button) findViewById(R.id.login_or_register_btn);
		connectToyTv.setOnClickListener(this);
		personalInfoTv.setOnClickListener(this);
		purchaseToyTv.setOnClickListener(this);
		helpTv.setOnClickListener(this);
		feedbackTv.setOnClickListener(this);
		aboutUsTv.setOnClickListener(this);
		loginOrRegisterBtn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateLoginOrRegisterBtn(UserManager.getInstance().isAuthenticated());
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
			startActivity(new Intent(mActivity,BluetoothSettingActivity.class));
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
		default:
			break;
		}

	}

	private void logout() {
		ASmackManager.getInstance().closeConnection();
		ASmackUtils.ROSTER_NAME = null;
		setResult(RESULT_OK);
		ChatMessageListener.getInstance().setChatManager(null);
		UserManager.getInstance().setFirstLogin(true);
		startActivity(new Intent(SettingActivity.this, LoginActivity.class));
		finish();
	}
	/*
	 * //退出登录清空用户数据 public interface clearUserData{ public void deleteData(); }
	 */
}
