package me.linkcube.app.sync.user;

import java.io.ByteArrayInputStream;

import me.linkcube.app.common.util.FormatUtils;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class GetAvatar {

	private String TAG = "GetAvatar";
	private ASmackRequestCallBack getAvatarCallBack;
	private String username;

	public GetAvatar(String jid, ASmackRequestCallBack iCallBack) {
		getAvatarCallBack = iCallBack;
		username = jid;
		callGetAvatar();
	}

	private void callGetAvatar() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.obj == null) {
					getAvatarCallBack.responseFailure(-1);
				} else if (msg.obj != null) {
					getAvatarCallBack.responseSuccess(msg.obj);
				}
			}
		};
		Thread thread = new Thread() {
			@Override
			public void run() {
				Drawable drawable = getAvatar(username);
				Message msg = new Message();
				msg.obj = drawable;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}

	/**
	 * 获取用户头像信息
	 * 
	 * @param user
	 * @return
	 */
	private Drawable getAvatar(String user) {
		if (!ASmackManager.getInstance().getXMPPConnection().isConnected())
			return null;// 没有连接上
		ByteArrayInputStream bais = null;
		try {
			VCard vCard = new VCard();
			if (user == "" || user == null || user.trim().length() <= 0) {
				return null;
			}
			vCard.load(ASmackManager.getInstance().getXMPPConnection());
			if (vCard == null || vCard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vCard.getAvatar());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return FormatUtils.InputStream2Drawable(bais);
	}
}
