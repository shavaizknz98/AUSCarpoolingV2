package com.example.auscarpoolingv2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.getSystemService;


public class ProvidingNotification {
    private Context mContext;
    private NotificationCompat.Builder builder;
    private NotificationChannel channel;
    private NotificationManager notificationManager;
    private String CHANNEL_ID = "Provide Notification Channel";
    private String CONTENT_TITLE = "AUS Carpooling";
    private String CONTENT_TEXT = "You are currently providing a ride";
    private String BIG_TEXT ="To stop providing rides, click on the App and click on \"Stop Providing\"";
    private String CHANNEL_NAME = "Providing a ride notification";
    private String CHANNEL_DESCRIPTION ="to remind users that they are still providing rides on the application";

    public ProvidingNotification(Context context){
        this.mContext = context;
        Intent intent = new Intent(mContext, UserMainPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_choosethisloc)
                .setContentTitle(CONTENT_TITLE)
                .setAutoCancel(true)
                .setContentText(CONTENT_TEXT)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(BIG_TEXT))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel();

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription("To remind users that they are still providing rides on the application");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = (NotificationManager) getSystemService(mContext, NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void showNotification(){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(001, builder.build());
    }

    public void deleteNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(mContext, NotificationManager.class);
            notificationManager.deleteNotificationChannel(CHANNEL_ID);
        }

    }

}
