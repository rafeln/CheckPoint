package com.nunesrafael.android.checkpoint.notification;

import java.util.Date;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.activity.MainActivity;

public class AlarmNotification {

	public static void sendTimeToGoNotification(Date date, Context context) {
		
		int mId = 0;
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_stat_notification)
		        .setContentTitle(context.getString(R.string.app_name))
		        .setContentText(context.getString(R.string.notification_time_to_go));
		
		// Hide the notification after its selected
		mBuilder.setAutoCancel(true);
		
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultIntent.putExtra(MainActivity.SHOWING_DATE_LONG_PARAMETER, date.getTime());

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
		
	}
	
	public static void sendReminderExitTimeNotification(Date date, Context context) {
		
		int mId = 1;
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_stat_notification)
		        .setContentTitle(context.getString(R.string.app_name))
		        .setContentText(context.getString(R.string.notification_remind_exit_time));
		
		// Hide the notification after its selected
		mBuilder.setAutoCancel(true);
		
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultIntent.putExtra(MainActivity.SHOWING_DATE_LONG_PARAMETER, date.getTime());

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
	}
}