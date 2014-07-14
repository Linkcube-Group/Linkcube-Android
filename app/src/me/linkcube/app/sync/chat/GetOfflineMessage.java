package me.linkcube.app.sync.chat;

import java.util.Iterator;

import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.content.Context;
import android.util.Log;

/**
 * 获取离线消息，目前没有用到2014-3-11
 * 
 * @author Rodriguez-xin
 * 
 */
public class GetOfflineMessage {

	private String TAG = "GetOfflineMessage";
	private Context context;
	private OfflineMessageManager msgManager;

	public GetOfflineMessage(Context _context) {
		this.context = _context;
		msgManager = new OfflineMessageManager(ASmackManager.getInstance()
				.getXMPPConnection());
	}

	public void getOfflineMsg() {
		String userName = ASmackUtils.getRosterName();
		try {
			// System.out.println(msgManager.supportsFlexibleRetrieval());
			Log.i(TAG, "msgcount:" + msgManager.getMessageCount());
			// System.out.println("msgcount:"+msgManager.getMessageCount());
			Iterator<Message> iterator = msgManager.getMessages();
			while (iterator.hasNext()) {
				Message msg = iterator.next();
				Log.i(TAG, "from:" + msg.getFrom() + "body:" + msg.getBody());
			}
			msgManager.deleteMessages();

		} catch (XMPPException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
