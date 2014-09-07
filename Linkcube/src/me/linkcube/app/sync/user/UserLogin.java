package me.linkcube.app.sync.user;

import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.os.Handler;
import android.os.Message;

/**
 * 用户登录类
 * 
 * @author Rodriguez-xin
 * 
 */
public class UserLogin {

	private ASmackRequestCallBack userLoginCallBack;

	private String username;

	private String passWord;

	private VCard vCard;

	public UserLogin(String jid, String pwd, ASmackRequestCallBack iCallBack) {
		userLoginCallBack = iCallBack;
		username = jid;
		passWord = pwd;
		vCard = new VCard();
		userLoginCallback();
	}

	/**
	 * 用户登录回调方法
	 */
	private void userLoginCallback() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					PreferenceUtils.setString(Const.Preference.OLD_USER_PWD, passWord);
					userLoginCallBack.responseSuccess(vCard);
				} else {
					userLoginCallBack.responseFailure(msg.what);
				}
			}
		};
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int reflag = userLogin(username, passWord);
				Message msg = new Message();
				msg.what = reflag;

				handler.sendMessage(msg);
			}
		});
		thread.start();
	}

	/**
	 * 用户登录
	 * 
	 * @param jidToLogin
	 *            用户名
	 * @param pswToLogin
	 *            密码
	 * @return int '-1'网络错误, '0'表示成功, '1'表示已经登录
	 */
	private int userLogin(String jidToLogin, String pwdToLogin) {
		/*
		 * if(!isNetConnected()){ Log.i(TAG,
		 * "login error,can't connected to server.."); return -1; }
		 */
		try {
			if (!ASmackManager.getInstance().getXMPPConnection().isConnected())
				return -1;
			if (ASmackManager.getInstance().getXMPPConnection().isAuthenticated()) {
				return 1;
			} else {
				ASmackManager.getInstance().getXMPPConnection()
						.login(jidToLogin, pwdToLogin);
				return 0;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
			if (e!=null) {
				/*if(e.getXMPPError().toString().equalsIgnoreCase("not-authorized(401)")){
					Timber.e(e, "login error,username or password wrong..");
					return -2;
				}*/
				return -2;
			}
			return -1;
		}
	}
}
