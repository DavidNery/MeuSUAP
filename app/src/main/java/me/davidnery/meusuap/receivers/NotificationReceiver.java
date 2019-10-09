package me.davidnery.meusuap.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.davidnery.meusuap.utils.NotificationManager;

/**
 * Created by david on 08/11/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras().getInt("notificationId") == NotificationManager.notificationId)
            NotificationManager.notificationId--;

    }

}
