package me.davidnery.meusuap;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.davidnery.meusuap.services.VerificarNovasNotasService;
import me.davidnery.meusuap.services.VerifyTarefasService;
import me.davidnery.meusuap.utils.ConfiguracoesUtils;
import me.davidnery.meusuap.utils.NotificationManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPreferences();

        findViewById(R.id.card1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalcularNota.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.card2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlunosActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.card3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConfiguracoesActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.author).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreditosActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setupPreferences() {
        ConfiguracoesUtils configuracoesUtils = new ConfiguracoesUtils(this);

        if (!configuracoesUtils.hasKey("anualpeso1")) {
            configuracoesUtils.setAnualPeso1(2);
            configuracoesUtils.setAnualPeso2(2);
            configuracoesUtils.setAnualPeso3(3);
            configuracoesUtils.setAnualPeso4(3);
            configuracoesUtils.setSemPeso1(2);
            configuracoesUtils.setSemPeso2(3);
            configuracoesUtils.setVNN(true);
        }

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        boolean enable = true;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("me.davidnery.meusuap.services.VerifyTarefasService".equals(service.service.getClassName())) {
                enable = false;
                break;
            }
        }
        if (enable)
            getApplicationContext().startService(new Intent(getApplicationContext(), VerifyTarefasService.class));

        if (configuracoesUtils.getVNN()) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("me.davidnery.meusuap.services.VerificarNovasNotasService".equals(service.service.getClassName())) {
                    return;
                }
            }

            startService(new Intent(getApplicationContext(), VerificarNovasNotasService.class));

            new NotificationManager(MainActivity.this)
                    .createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                            "MeuSUAP",
                            "Estou verificando novas notas!",
                            MainActivity.class, new Intent(getApplicationContext(), MainActivity.class));
        }
    }


}
