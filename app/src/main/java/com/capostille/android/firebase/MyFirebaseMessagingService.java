package com.capostille.android.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.capostille.android.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    int m = 0;
    int channel_id = 0;
   // PreferenceUtils preferenceUtils;
    String language = "", thread_id = "", country = "", state = "", navigationUrl = "";
    JSONObject object;
    Context ctx;
    String channelId = "default_channel_id";
    String channelDescription = "Default Channel";
    private NotificationManager notifManager;
    private String title;
    private String text;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
       // preferenceUtils = new PreferenceUtils(this);
      //  Log.e("Remote",""+remoteMessage.getData().toString());


        try {
            if (remoteMessage != null) {
              //  preferenceUtils = new PreferenceUtils(this);
                Random random = new Random();
                channel_id = random.nextInt(9999 - 1000) + 1000;
                m = random.nextInt(9999 - 1000) + 1000;
                if (remoteMessage.getData().size() > 0) {
                    String data = remoteMessage.getData().toString();
                    Log.e("data", data);
                    Map<String, String> params = remoteMessage.getData();
                    object = new JSONObject(params);
                    country = object.getString("country");
                    state = object.getString("state");
                    navigationUrl = object.getString("navigationUrl");
                    Log.d("JSON_OBJECT", object.toString());
                    //sendNotification(object.getString("notification_title_en"));
                    notification_handle();
                }if (remoteMessage.getNotification() != null)
                {
                    Map<String, String> params = remoteMessage.getData();
                    object = new JSONObject(params);
                    Log.d("JSON_OBJECT", object.toString());
                    country = object.getString("country");
                    state = object.getString("state");
                    navigationUrl = object.getString("navigationUrl");
                    notification_handle();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onMessageReceived: ", e.getMessage());
        }
    }

    private void notification_handle() {
        try {


            title = object.getString("title");
            text = object.getString("body");
//                Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
//                intent.putExtra("action","map");
//                intent.putExtra("map_key",object.getString("navigationUrl"));
//                handleNotification(intent, object.getString("body"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleNotification(Intent intent, String message) {
        if (isAppIsInBackground(this)) {
            createNotification(intent, message);
        } else {
            // createNotification(intent, message);
            createElseNotification(intent, message);
        }
    }
    private void createElseNotification(Intent intent, String message) {
        intent.putExtra("message", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        Notification notification;
        String CHANNEL_ID = "my_channel_02";
        CharSequence name = getApplicationContext().getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notification = mBuilder.setSmallIcon(R.drawable.app_icon).setTicker("").setWhen(0)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(inboxStyle)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(m, notification);
    }
    private void createNotification(Intent intent, String message)
    {
        intent.putExtra("message", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, channel_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        Notification notification;
        String CHANNEL_ID = "my_channel_02";
        CharSequence name = getApplicationContext().getString(R.string.app_name);
        notification = mBuilder.setSmallIcon(R.drawable.app_icon).setTicker("").setWhen(0)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(inboxStyle)
                .setSmallIcon(R.drawable.app_icon)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(channel_id, notification);
    }

    public boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            assert am != null;
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            assert am != null;
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                componentInfo = taskInfo.get(0).topActivity;
            }
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }



}