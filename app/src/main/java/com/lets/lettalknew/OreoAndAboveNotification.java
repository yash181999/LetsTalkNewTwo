package com.lets.lettalknew;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class OreoAndAboveNotification extends ContextWrapper {

    private static  final String ID = "some_id";
    private static final  String NAME = "FirebaseApp";

    NotificationManager notificationManager;
    public OreoAndAboveNotification(Context base) {
        super ( base );

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            createChannel();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel ( ID,NAME, NotificationManager.IMPORTANCE_DEFAULT );
        notificationChannel.enableVibration ( true );
        notificationChannel.enableLights ( true );
        notificationChannel.setLightColor ( Notification.VISIBILITY_PRIVATE );
        getManager ().createNotificationChannel ( notificationChannel );
    }

    public NotificationManager getManager() {
        if(notificationManager==null) {
             notificationManager = (NotificationManager) getSystemService ( Context.NOTIFICATION_SERVICE );
        }

        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon) {
        return new Notification.Builder ( getApplicationContext (),ID )
                .setContentIntent ( pendingIntent )
                .setContentTitle (title)
                .setContentText ( body )
                .setSound ( soundUri )
                .setAutoCancel ( true )
                .setSmallIcon (  Integer.parseInt ( icon ) );
    }

}
