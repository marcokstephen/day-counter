package com.sm.daysuntilcards;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class CardsService extends IntentService {

	public CardsService() {
		super("My service");
	}

	@Override
	public void onHandleIntent(Intent intent){
		String name = intent.getStringExtra("com.sm.daysuntilcards.EVENTNAME");
	    NotificationCompat.Builder notify = new NotificationCompat.Builder(this)
	    	.setContentTitle(name)
	    	.setContentText("just occurred!")
	    	.setSmallIcon(R.drawable.ic_action_alarms_white)
	    	.setAutoCancel(true);
	    Intent notifIntent = new Intent(this, MainActivity.class);
	    notifIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		 TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		 stackBuilder.addParentStack(MainActivity.class);
		 stackBuilder.addNextIntent(notifIntent);
		 PendingIntent resultPendingIntent =
		         stackBuilder.getPendingIntent(
		             0,
		             PendingIntent.FLAG_UPDATE_CURRENT
		         );
		 notify.setContentIntent(resultPendingIntent);
		 NotificationManager mNotificationManager =
		     (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 mNotificationManager.notify(0, notify.build());
		 stopSelf();
	}
}