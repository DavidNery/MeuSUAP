package me.davidnery.meusuap.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.davidnery.meusuap.MainActivity;
import me.davidnery.meusuap.R;
import me.davidnery.meusuap.services.VerificarNovasNotasService;
import me.davidnery.meusuap.services.VerifyTarefasService;
import me.davidnery.meusuap.utils.ConfiguracoesUtils;
import me.davidnery.meusuap.utils.NotificationManager;

/**
 * Created by david on 08/11/2018.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        boolean enable = true;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("me.davidnery.meusuap.services.VerifyTarefasService".equals(service.service.getClassName())) {
                enable = false;
                break;
            }
        }
        if (enable)
            context.startService(new Intent(context, VerifyTarefasService.class));

        ConfiguracoesUtils configuracoesUtils = new ConfiguracoesUtils(context);

        if (configuracoesUtils.getVNN()) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("me.davidnery.meusuap.services.VerificarNovasNotasService".equals(service.service.getClassName())) {
                    return;
                }
            }

            context.startService(new Intent(context, VerificarNovasNotasService.class));

            new NotificationManager(context)
                    .createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                            "MeuSUAP",
                            "Estou verificando novas notas!",
                            MainActivity.class, new Intent(context, MainActivity.class));
        }
    }

}
