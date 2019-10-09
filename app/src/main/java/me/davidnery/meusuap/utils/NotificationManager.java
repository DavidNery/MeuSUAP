package me.davidnery.meusuap.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import me.davidnery.meusuap.receivers.NotificationReceiver;

/**
 * Created by david on 08/11/2018.
 */

public class NotificationManager {

    public static int notificationId = 0;

    private final Context context;

    public NotificationManager(Context context) {
        this.context = context;
    }

    public Notification createNotification(int icon, String title, String content, Class resultClass, Intent result) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(icon)
                .setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(resultClass);
        stackBuilder.addNextIntent(result);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        Intent intent = new Intent(context, NotificationReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt("notificationId", ++notificationId);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        builder.setDeleteIntent(pendingIntent);

        return builder.build();
    }

    public void showNotification(Notification notification) {
        android.app.NotificationManager nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(notificationId, notification);
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
    }

    public void createAndShowNotification(int icon, String title, String content, Class resultClass, Intent result) {
        showNotification(createNotification(icon, title, content, resultClass, result));
    }

}
