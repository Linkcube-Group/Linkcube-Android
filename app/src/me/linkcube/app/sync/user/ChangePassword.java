package me.linkcube.app.sync.user;

import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;
import android.os.Handler;
import android.os.Message;

/**
 * 修改用户密码
 * 
 * @author Rodriguez-xin
 * 
 */
public class ChangePassword {

	private ASmackRequestCallBack changePasswordCallBack;

	public ChangePassword(String _password, ASmackRequestCallBack _iCallBack) {
		changePasswordCallBack = _iCallBack;
		callChangePassword(_password);

	}

	public void callChangePassword(final String password) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what != 0) {
					changePasswordCallBack.responseFailure(msg.what);
				} else if (msg.what == 0) {
					changePasswordCallBack.responseSuccess(msg.what);
				}
			}
		};
		Thread thread = new Thread() {
			@Override
			public void run() {
				int reflag = changePassword(password);
				Message msg = new Message();
				msg.what = reflag;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}

	/**
	 * 修改用户密码
	 * 
	 * @param pwd
	 * @return
	 */
	public int changePassword(String pwd) {
		if (!ASmackManager.getInstance().getXMPPConnection().isConnected())
			return -1;
		try {
			ASmackManager.getInstance().getXMPPConnection().getAccountManager()
					.changePassword(pwd);
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
}
