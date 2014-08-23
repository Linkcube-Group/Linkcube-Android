package me.linkcube.app.sync.friend;


import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import android.os.Handler;
import android.os.Message;
/**
 * 添加好友公共类
 * @author Rodriguez-xin
 *
 */
public class AddFriend {

	private ASmackRequestCallBack addFriendcaCallBack;
	public AddFriend(String friendName,String nickName,ASmackRequestCallBack iCallBack){
		addFriendcaCallBack=iCallBack;
		callAddUser(friendName,nickName);
	}
	private void callAddUser(final String friendName,final String nickName) {
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==-1){
					addFriendcaCallBack.responseFailure(msg.what);
				}
				else if (msg.what==0) {
					addFriendcaCallBack.responseSuccess(msg.what);
				}
			}
		};
		Thread thread=new Thread(){
			@Override
			public void run() {
				int reFlag= addUser(friendName,nickName);
				Message msg=new Message();
				msg.what=reFlag;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}
	/**
	 * 无分组添加好友
	 * @param userName 用户名
	 * @param friendName 好友名
	 * @return '-1' 网络错误   '0' 发送请求成功
	 */
	private int addUser(String friendName,String nickName){
		if(! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return -1;
		try {
			ASmackManager
					.getInstance()
					.getXMPPConnection()
					.getRoster()
					.createEntry(ASmackUtils.getFriendJid(friendName), nickName, null);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
