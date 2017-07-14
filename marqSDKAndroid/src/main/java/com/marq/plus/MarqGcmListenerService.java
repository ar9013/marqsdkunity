package com.marq.plus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.marq.plus.unity.MarqPlus;


public class MarqGcmListenerService extends GcmListenerService {

    private static final String TAG = "MarqGcmListenerService";
	// Request code for launching unity activity
	private static final int REQUEST_CODE_UNITY_ACTIVITY = 1001;
	// ID of notification
	private static final int ID_NOTIFICATION = 1;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //sendNotification(message);
        sendNotification(data);
        // [END_EXCLUDE]
    }
    // [END receive_message]
    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(Bundle data) {
		// Intent 
       
    	String packageName = getPackageName();
    	Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
    	
    	if(data == null) data = new Bundle();    	
    	data.putString("Command", "OpenWebView");
    	
		String message = data.getString("message");
		if(message == null) message="";
		
		String url = data.getString("url");
		if(url == null) url="http://my.marq.com.tw/webview/";
		
    	data.putString("Url", url);		
    	
    	launchIntent.putExtras(data);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
         	
        Log.d(TAG, "LaunchIntent: " + launchIntent.getComponent().getClassName());
		PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE_UNITY_ACTIVITY, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		
		//�@Show notification in status bar
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentIntent(contentIntent);

		
		builder.setContentText(message);
		//builder.setContentTitle(contentTitle);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(false);
		

		builder.setSmallIcon(R.drawable.icon_app_marq);
		
		builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
		
		 NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 notificationManager.notify(ID_NOTIFICATION, builder.build());
    }
    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
		// Intent 
    	String packageName = getPackageName();
    	Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
    	
        Log.d(TAG, "LaunchIntent: " + launchIntent.getComponent().getClassName());
    	//Class<?> c = launchIntent.getComponent().getClass();
		//Intent intent = new Intent(this, c);
		PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE_UNITY_ACTIVITY, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//�@Show notification in status bar
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentIntent(contentIntent);
		//builder.setTicker(ticker);
		builder.setContentText(message);
		//builder.setContentTitle(contentTitle);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(false);
		
		//Resources res = MarqPlus.GetCurrentActivity().getResources();
		builder.setSmallIcon(R.drawable.icon_app_marq);
		
		builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
		
		//NotificationManager nm = (NotificationManager)MarqPlus.GetCurrentActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		 //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 notificationManager.notify(ID_NOTIFICATION, builder.build());

    }
}