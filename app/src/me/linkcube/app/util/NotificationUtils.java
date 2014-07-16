package me.linkcube.app.util;

import me.linkcube.app.R;
import me.linkcube.app.ui.main.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationUtils {
	private static Intent msgIntent;
	private static PendingIntent msgPendingIntent;

	private static Notification msgNotification;
	private static NotificationManager msgNotificationManager;

	public static void initNotification(Context context,int msgNotificationID,String Text) {
		msgNotification = new Notification();
		msgNotification.icon = R.drawable.ic_launcher;
		//msgNotification.tickerText = Text;
		//msgNotification.defaults = Notification.DEFAULT_SOUND;
		msgNotification.flags = Notification.FLAG_AUTO_CANCEL;
		msgNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		msgIntent = new Intent(context, MainActivity.class);
		msgIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		msgPendingIntent = PendingIntent.getActivity(context, 0, msgIntent,
				0);

		msgNotification.setLatestEventInfo(context,Text,"", msgPendingIntent);

		msgNotificationManager.notify(msgNotificationID, msgNotification);
		msgNotificationID++;
	}

}
