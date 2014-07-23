package me.linkcube.app.sync.chat;

import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 * 单对单聊天类
 * 
 * @author Rodriguez-xin
 * 
 */
public class SingleChat {

	private Chat newchat;
	private ChatManager chatManager;

	public SingleChat() {
		chatManager = ASmackManager.getInstance().getXMPPConnection()
				.getChatManager();
	}

	/**
	 * 发送给其他用户消息
	 * 
	 * @param friendName
	 *            对方用户名
	 * @param sendMsg
	 *            消息内容
	 */
	public void sendMsg(String friendName, final String sendMsg) {
		friendName=ASmackUtils.getFriendJid(friendName);
		if (newchat == null) {
			newchat = chatManager.createChat(friendName, new MessageListener() {
				@Override
				public void processMessage(Chat chat, Message message) {

				}
			});
			try {
				newchat.sendMessage(sendMsg);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		} else {
			try {
				newchat.sendMessage(sendMsg);
				System.out.println("sendMsg:"+sendMsg);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}
}
