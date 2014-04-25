package me.linkcube.app.sync.user;

import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.os.Handler;
import android.os.Message;

/**
 * 获取用户vcard信息类
 * 
 * @author Rodriguez-xin
 * 
 */
public class GetUserVCard {

	private String TAG = "GetUserVCard";
	private ASmackRequestCallBack getUserVCardCallBack;
	private String userName;

	public GetUserVCard(String username, ASmackRequestCallBack iCallBack) {
		userName = username;
		getUserVCardCallBack = iCallBack;
		callGetUserVCard();
	}

	private void callGetUserVCard() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.obj == null) {
					getUserVCardCallBack.responseFailure(-1);
				} else if (msg.obj != null) {
					getUserVCardCallBack.responseSuccess(msg.obj);
				}
			}
		};
		Thread thread = new Thread() {
			@Override
			public void run() {
				VCard vCard = new VCard();
				vCard = getUserVCard(userName);
				Message msg = new Message();
				msg.obj = vCard;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}

	/**
	 * 获取用户VCard信息
	 * 
	 * @param username
	 * @return
	 */
	private VCard getUserVCard(String username) {
		/*
		 * if(!NetUtils.isNetConnected()){ Log.i(TAG,
		 * "Internet error,can't connected to server.."); return null; }
		 */
		if (!ASmackManager.getInstance().getXMPPConnection().isConnected())
			return null;// 没有连接上
		VCard vCard = new VCard();
		try {
			vCard.load(ASmackManager.getInstance().getXMPPConnection(), username);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return vCard;
	}
}
