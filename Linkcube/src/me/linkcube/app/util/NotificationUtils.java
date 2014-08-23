package me.linkcube.app.util;

import me.linkcube.app.R;
import me.linkcube.app.ui.main.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class NotificationUtils {
	private static Intent msgIntent;
	private static PendingIntent msgPendingIntent;

	private static Notification appIconNotification;
	private static NotificationManager msgNotificationManager;

	public static void appPauseNotification(Context context,
			int msgNotificationID, String Text) {
		appIconNotification = new Notification();
		appIconNotification.icon = R.drawable.ic_launcher;

		appIconNotification.flags = Notification.FLAG_AUTO_CANCEL;
		msgNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		msgIntent = new Intent(context, MainActivity.class);
		msgIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		msgPendingIntent = PendingIntent.getActivity(context, 0, msgIntent, 0);

		// 自定义界面
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.app_pause_notification);
		rv.setImageViewResource(R.id.app_pause_icon_iv, R.drawable.ic_launcher);
		rv.setTextViewText(R.id.app_pause_name_tv, "连酷Linkcube");
		//rv.setOnClickPendingIntent(R.id.app_pause_rl, msgPendingIntent);
		appIconNotification.contentView = rv;
		appIconNotification.contentIntent = msgPendingIntent;

		// appIconNotification.setLatestEventInfo(context, contentTitle,
		// contentText, contentIntent)

		msgNotificationManager.notify(msgNotificationID, appIconNotification);
	}

}
