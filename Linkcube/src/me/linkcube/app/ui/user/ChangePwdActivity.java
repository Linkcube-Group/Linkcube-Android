package me.linkcube.app.ui.user;

import me.linkcube.app.R;
import me.linkcube.app.common.ui.BaseActivity;
import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Const;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.user.ChangePassword;
import me.linkcube.app.widget.CWClearEditText;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ChangePwdActivity extends BaseActivity implements OnClickListener {

	private CWClearEditText oldPwdEt;
	private CWClearEditText newPwdEt;
	private CWClearEditText confirmPwdEt;

	private Button actionbarFirstBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pwd_activity);
		
		configureActionBar(R.string.change_pwd);

		initView();

	}

	private void initView() {
		oldPwdEt = (CWClearEditText) findViewById(R.id.old_pwd_et);
		newPwdEt = (CWClearEditText) findViewById(R.id.new_pwd_et);
		confirmPwdEt = (CWClearEditText) findViewById(R.id.confirm_new_pwd_et);

		actionbarFirstBtn = (Button) actionbarView
				.findViewById(R.id.actionbar_first_btn);
		actionbarFirstBtn.setVisibility(View.VISIBLE);
		actionbarFirstBtn.setText(R.string.confirm);
		actionbarFirstBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_first_btn:
			
			String oldPwd = oldPwdEt.getText().toString();
			String newPwd = newPwdEt.getText().toString();
			String confirmPwd = confirmPwdEt.getText().toString();
			if(!oldPwd.equals(PreferenceUtils.getString(Const.Preference.OLD_USER_PWD, ""))){
				Toast.makeText(ChangePwdActivity.this, R.string.toast_original_psw_wrong,
						Toast.LENGTH_SHORT).show();
			}else if (newPwd.length() < 6) {
				Toast.makeText(ChangePwdActivity.this, R.string.toast_new_psw_too_short,
						Toast.LENGTH_SHORT).show();
			} else if (ASmackUtils.containWhiteSpace(newPwd)) {
				Toast.makeText(ChangePwdActivity.this, R.string.toast_psw_contain_space,
						Toast.LENGTH_SHORT).show();
			} else if (!newPwd.equals(confirmPwd)) {
				Toast.makeText(ChangePwdActivity.this, R.string.toast_psw_inconsistente_reinput,
						Toast.LENGTH_SHORT).show();
			} else {
				new ChangePassword(newPwd, new ASmackRequestCallBack() {

					@Override
					public void responseSuccess(Object object) {
						Toast.makeText(ChangePwdActivity.this, R.string.toast_change_psw_success,
								Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void responseFailure(int reflag) {
						Toast.makeText(ChangePwdActivity.this, R.string.toast_change_psw_failure,
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			break;

		default:
			break;
		}
	}

}
