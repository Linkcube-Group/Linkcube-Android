package me.linkcube.app.sync.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.linkcube.app.R;
import me.linkcube.app.common.util.ActivityUtils;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.ui.main.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;

public class MsgNotificationReceiver extends BroadcastReceiver {

	private Intent msgIntent;
	private static PendingIntent msgPendingIntent;

	private int msgNotificationID = 1000;
	private Notification msgNotification;
	private NotificationManager msgNotificationManager;

	private String from = null;

	private String friendNickname = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!ActivityUtils.isRunningForeground(context)) {

			Bundle bundle = intent.getExtras();
			Message msg = new Message();
			msg.setData(bundle);

			from = msg.getData().getString("from");
			String body = msg.getData().getString("body");
			Timber.d(from + "---" + body);

			PersistableFriend perFriend = new PersistableFriend();
			List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
			try {
				friendEntities = DataManager.getInstance().query(
						perFriend,
						USER_JID + "=? and " + FRIEND_JID + "=?",
						new String[] { ASmackUtils.getUserJID(),
								ASmackUtils.getFriendJid(from) }, null, null,
						null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			friendNickname = friendEntities.get(0).getNickName();

			initNotification(context);

			msgIntent = new Intent(context, MainActivity.class);// Class.forName("com.oplibs.controll.test.TestMainActivity")
			msgIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			msgPendingIntent = PendingIntent.getActivity(context, 0, msgIntent,
					0);

			msgNotification.setLatestEventInfo(context, friendNickname
					+ "给您发来一条新消息", body, msgPendingIntent);

			msgNotificationManager.notify(msgNotificationID, msgNotification);
			msgNotificationID++;
		}
	}

	private void initNotification(Context context) {
		msgNotification = new Notification();
		msgNotification.icon = R.drawable.ic_launcher;
		msgNotification.tickerText = friendNickname + "给您发来一条新消息";
		msgNotification.defaults = Notification.DEFAULT_SOUND;
		msgNotification.flags = Notification.FLAG_AUTO_CANCEL;
		msgNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

	}
}
