package me.linkcube.app.sync.friend;

import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.os.Handler;
import android.os.Message;

public class GetFriendVCard {

	private ASmackRequestCallBack getFriendVCardCallBack;
	public GetFriendVCard(String friendname,ASmackRequestCallBack iCallBack){
		getFriendVCardCallBack=iCallBack;
		callGetfriendVCard(friendname);
	}
	
	private void callGetfriendVCard(final String friendName){
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.obj==null){
					getFriendVCardCallBack.responseFailure(-1);
				}
				else if (msg.obj!=null) {
					getFriendVCardCallBack.responseSuccess(msg.obj);
				}
			}
		};
		Thread thread=new Thread(){
			@Override
			public void run() {
				VCard vCard=new VCard();
				vCard=getfriendVCard(friendName);
				Message msg=new Message();
				msg.obj=vCard;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}
	/**
	 * 获取用户VCard信息
	 * @param friendname
	 * @return
	 */
	private VCard getfriendVCard(String friendName){
		/*if(!NetUtils.isNetConnected()){
			Log.i(TAG, "Internet error,can't connected to server..");
			return null;
		}*/
		if(! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return null;//没有连接上
		VCard vCard=new VCard();
		try {
			vCard.load( ASmackManager.getInstance().getXMPPConnection(), friendName);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return vCard;
	}

}
