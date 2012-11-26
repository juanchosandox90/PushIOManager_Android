package com.pushio.basic;

import com.pushio.manager.PushIOActivityLauncher;
import com.pushio.manager.PushIOManager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


public class NotificationBroadcastReceiver extends BroadcastReceiver {

	  private NotificationManager mNotificationManager;
	  private int SIMPLE_NOTIFICATION_ID;
	
	
	public void onReceive(Context context, Intent intent) {
		  mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

	    	Intent newIntent = new Intent(context, PushIOActivityLauncher.class);
          newIntent.putExtras(intent);
          PendingIntent lPendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0); 
          
    	  NotificationCompat.Builder notify = new NotificationCompat.Builder(context);
          notify.setSmallIcon(R.drawable.ic_launcher)
          .setWhen(System.currentTimeMillis())
          .setTicker(intent.getStringExtra("alert"))
          .setContentIntent(lPendingIntent)
          .setContentTitle(intent.getStringExtra("alert"))
          .setContentText(intent.getStringExtra("details"))
          .setAutoCancel(true);
          
          mNotificationManager.notify(SIMPLE_NOTIFICATION_ID, notify.build());
	        Bundle extras = getResultExtras(true);
	        extras.putInt(PushIOManager.PUSH_STATUS, PushIOManager.PUSH_HANDLED_NOTIFICATION);
	        setResultExtras(extras);
	        
	    }

	
}


