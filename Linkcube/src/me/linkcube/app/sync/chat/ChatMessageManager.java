package me.linkcube.app.sync.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.OffLineMsgEntity;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ASmackUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

/**
 * 监听其他人发送来的聊天信息
 * 
 * @author Rodriguez-xin
 * 
 */
public class ChatMessageManager {

	private Context context;
	private ChatManager chatManager = null;
	private boolean offLineMsgFlag = true;
	private List<OffLineMsgEntity> offLineMsgs = new ArrayList<OffLineMsgEntity>();
	private static ChatMessageManager chatMessageManager;

	public static ChatMessageManager getInstance() {
		if (chatMessageManager == null) {
			chatMessageManager = new ChatMessageManager();
		}
		return chatMessageManager;
	}

	public void onMessageListener(Context context) {
		this.context = context;
		if (chatManager == null) {
			chatManager = ASmackManager.getInstance().getXMPPConnection()
					.getChatManager();
			chatManager.addChatListener(chatManagerListener);
		}
	}

	private ChatManagerListener chatManagerListener = new ChatManagerListener() {
		@Override
		public void chatCreated(Chat chat, boolean arg1) {

			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat arg0, Message message) {
					Timber.d("listener--from:" + message.getFrom() + "--body:"
							+ message.getBody());
					Log.d("addMessageListener", offLineMsgFlag+"");
					if (offLineMsgFlag) {
						Map<String, String> singleMsgMap = new HashMap<String, String>();
						singleMsgMap.put(ASmackUtils
								.deleteServerAddress(message.getFrom()),
								message.getBody());
						OffLineMsgEntity offLineMsgEntity = new OffLineMsgEntity();
						offLineMsgEntity.setFrom(ASmackUtils
								.deleteServerAddress(message.getFrom()));
						offLineMsgEntity.setBody(message.getBody());
						if (!message.getBody()
								.startsWith(Const.Game.REQUESTCMD)
								&& !message.getBody().startsWith(
										Const.Game.SHAKESPEEDCMD)
								&& !message.getBody().startsWith(
										Const.Game.POSITIONMODECMD)) {
							offLineMsgs.add(offLineMsgEntity);
						}
						Log.d("addMessageListener", "size:"+offLineMsgs.size());
					}
					isGameMsgOrNormalMsg(message);

				}
			});
		}
	};

	/**
	 * 判断是游戏邀请消息还是普通消息
	 * 
	 * @param body
	 */
	private void isGameMsgOrNormalMsg(Message message) {
		String body = message.getBody();
		String from = message.getFrom();
		// 在对方离线的时候，发送消息过去，等到对方上线了，会再返回一些null消息，目前采用屏蔽处理
		if (body != null) {
			Log.d("isGameMsgOrNormalMsg","cmddata1:" + message.getBody());
			if (body.startsWith(Const.Game.POSITIONMODECMD)) {// 多人模式七种姿势
				String[] cmdData = body.split(":");
				Log.d("isGameMsgOrNormalMsg","cmddata2:" + cmdData[1]);
				try {
					LinkcubeApplication.toyServiceCall
							.cacheSexPositionMode(Integer.parseInt(cmdData[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (body.startsWith(Const.Game.SHAKESPEEDLEVELCMD)) {
				String[] cmdData = body.split(":");
				Log.d("isGameMsgOrNormalMsg","cmddata3:" + cmdData[1]);
				try {
					LinkcubeApplication.toyServiceCall
							.setShakeSensitivity(Integer.parseInt(cmdData[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (body.startsWith(Const.Game.SHAKESPEEDCMD)) {// 多人模式摇一摇
				String[] cmdData = body.split(":");
				Log.d("isGameMsgOrNormalMsg","cmddata4:" + cmdData[1]);
				try {
					LinkcubeApplication.toyServiceCall.cacheShake(
							(long) Integer.parseInt(cmdData[1]), false);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (body.startsWith(Const.Game.REQUESTCMD)) {
				String[] cmdData = body.split(":");
				Log.d("isGameMsgOrNormalMsg","cmddata5:" + cmdData[1]);
				if (cmdData[1].equals(Const.Game.REQUESTCONNECTCMD)) {
					broadMsg(from,context.getResources().getString(R.string.others_send_invitation_to_you) , cmdData[1]);
				} else if (cmdData[1].equals(Const.Game.ACCEPTCONNECTCMD)) {
					broadMsg(from, context.getResources().getString(R.string.others_accept_your_invitation), cmdData[1]);
				} else if (cmdData[1].equals("refuseconnect")) {
					broadMsg(from, context.getResources().getString(R.string.others_refused_your_invitation), cmdData[1]);
				} else if (cmdData[1].equals(Const.Game.DISCONNECTCMD)) {
					broadMsg(from, context.getResources().getString(R.string.others_stop_this_game), cmdData[1]);
				}
			} else {// 其他消息
				broadMsg(from, body, "");
			}
		}
	}

	/**
	 * 发送普通消息
	 * 
	 * @param from
	 * @param body
	 * @param cmdData
	 */
	private void broadMsg(String from, String body, String cmdData) {
		Log.d("isGameMsgOrNormalMsg","cmddata6:" + body);
		Bundle bundle = new Bundle();
		bundle.putString("body", body);
		bundle.putString("from", ASmackUtils.deleteServerAddress(from));
		bundle.putString("cmdData", cmdData);
		Intent chatMsgIntent = new Intent("com.linkcube.message");// 获取到聊天消息,发送广播
		chatMsgIntent.putExtras(bundle);
		context.sendBroadcast(chatMsgIntent);
	}

	public boolean isOffLineMsgFlag() {
		return offLineMsgFlag;
	}

	public void setOffLineMsgFlag(boolean offLineMsgFlag) {
		this.offLineMsgFlag = offLineMsgFlag;
	}

	public List<OffLineMsgEntity> getOffLineMsgs() {
		return offLineMsgs;
	}

	public void setOffLineMsgs(List<OffLineMsgEntity> offLineMsgs) {
		this.offLineMsgs = offLineMsgs;
	}

	public void setChatManager(ChatManager chatManager) {
		this.chatManager = chatManager;
	}

}
