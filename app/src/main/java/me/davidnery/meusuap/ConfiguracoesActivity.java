package me.davidnery.meusuap;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import me.davidnery.meusuap.services.VerificarNovasNotasService;
import me.davidnery.meusuap.utils.ConfiguracoesUtils;
import me.davidnery.meusuap.utils.NotificationManager;

public class ConfiguracoesActivity extends AppCompatActivity {

    private ConfiguracoesUtils configuracoesUtils;

    private EditText anualpeso1, anualpeso2, anualpeso3, anualpeso4, sempeso1, sempeso2;
    private Switch vnn;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        this.configuracoesUtils = new ConfiguracoesUtils(this);

        this.anualpeso1 = (EditText) findViewById(R.id.anualpeso1);
        this.anualpeso2 = (EditText) findViewById(R.id.anualpeso2);
        this.anualpeso3 = (EditText) findViewById(R.id.anualpeso3);
        this.anualpeso4 = (EditText) findViewById(R.id.anualpeso4);
        this.sempeso1 = (EditText) findViewById(R.id.sempeso1);
        this.sempeso2 = (EditText) findViewById(R.id.sempeso2);
        this.vnn = (Switch) findViewById(R.id.vnn);

        // Anuais
        anualpeso1.setText(configuracoesUtils.getAnualPeso1() + "");
        anualpeso2.setText(configuracoesUtils.getAnualPeso2() + "");
        anualpeso3.setText(configuracoesUtils.getAnualPeso3() + "");
        anualpeso4.setText(configuracoesUtils.getAnualPeso4() + "");
        // Semestrais
        sempeso1.setText(configuracoesUtils.getSemPeso1() + "");
        sempeso2.setText(configuracoesUtils.getSemPeso2() + "");
        // Verificar novas notas
        vnn.setChecked(configuracoesUtils.getVNN());

        findViewById(R.id.btnsalvaranual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText[] pesos = new EditText[]{anualpeso1, anualpeso2, anualpeso3, anualpeso4};

                for (int i = 0; i < 4; i++) {
                    if (pesos[i].getText().toString().equals("")) {
                        showToast("Informe o peso do " + (i + 1) + "º bimestre!", Toast.LENGTH_LONG);
                        return;
                    }

                    Integer peso = Integer.parseInt(pesos[i].getText().toString());
                    if (peso <= 0 || peso > 100) {
                        showToast("Os pesos tem de estar entre 1 e 100!", Toast.LENGTH_LONG);
                        return;
                    }
                }

                configuracoesUtils.setAnualPeso1(Integer.parseInt(anualpeso1.getText().toString()));
                configuracoesUtils.setAnualPeso2(Integer.parseInt(anualpeso2.getText().toString()));
                configuracoesUtils.setAnualPeso3(Integer.parseInt(anualpeso3.getText().toString()));
                configuracoesUtils.setAnualPeso4(Integer.parseInt(anualpeso4.getText().toString()));

                showToast("Pesos anuais atualizados!",
                        Toast.LENGTH_LONG);

            }
        });

        findViewById(R.id.btnsalvarsem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText[] pesos = new EditText[]{sempeso1, sempeso2};

                for (int i = 0; i < 2; i++) {
                    if (pesos[i].getText().toString().equals("")) {
                        showToast("Informe o peso do " + (i + 1) + "º bimestre!", Toast.LENGTH_LONG);
                        return;
                    }

                    Integer peso = Integer.parseInt(pesos[i].getText().toString());
                    if (peso <= 0 || peso > 100) {
                        showToast("Os pesos tem de estar entre 1 e 100!", Toast.LENGTH_LONG);
                        return;
                    }
                }

                configuracoesUtils.setSemPeso1(Integer.parseInt(sempeso1.getText().toString()));
                configuracoesUtils.setSemPeso2(Integer.parseInt(sempeso2.getText().toString()));

                showToast("Pesos semestrais atualizados!", Toast.LENGTH_LONG);

            }
        });

        findViewById(R.id.btnsalvarextra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configuracoesUtils.setVNN(vnn.isChecked());
                showToast(vnn.isChecked() ? "Verificando novas notas..." : "Não verificarei mais se existem novas notas!",
                        Toast.LENGTH_LONG);

                verifyService();

            }
        });

        findViewById(R.id.btnsalvartudo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveAll())
                    showToast("Tudo salvo!", Toast.LENGTH_SHORT);
            }
        });

    }

    private boolean saveAll() {
        EditText[] pesos = new EditText[]{anualpeso1, anualpeso2, anualpeso3, anualpeso4, sempeso1, sempeso2};

        for (int i = 0; i < 6; i++) {
            if (pesos[i].getText().toString().equals("")) {
                showToast("Peso do " + (i >= 4 ? i - 3 : i + 1) + "º bimestre das matérias " +
                        (i >= 4 ? "semestrais vazio" : "anuais vazio")
                        + "!", Toast.LENGTH_LONG);
                return false;
            }

            Integer peso = Integer.parseInt(pesos[i].getText().toString());
            if (peso <= 0 || peso > 100) {
                showToast("Os pesos tem de estar entre 1 e 100!", Toast.LENGTH_LONG);
                return false;
            }
        }

        configuracoesUtils.setAnualPeso1(Integer.parseInt(anualpeso1.getText().toString()));
        configuracoesUtils.setAnualPeso2(Integer.parseInt(anualpeso2.getText().toString()));
        configuracoesUtils.setAnualPeso3(Integer.parseInt(anualpeso3.getText().toString()));
        configuracoesUtils.setAnualPeso4(Integer.parseInt(anualpeso4.getText().toString()));
        configuracoesUtils.setSemPeso1(Integer.parseInt(sempeso1.getText().toString()));
        configuracoesUtils.setSemPeso2(Integer.parseInt(sempeso2.getText().toString()));
        configuracoesUtils.setVNN(vnn.isChecked());

        verifyService();

        return true;
    }

    public void showToast(String mensagem, int time) {
        if (toast != null && toast.getView().isShown())
            toast.cancel();

        toast = Toast.makeText(getApplicationContext(), mensagem, time);
        toast.show();
    }

    private void verifyService() {
        if (vnn.isChecked()) {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("me.davidnery.meusuap.services.VerificarNovasNotasService".equals(service.service.getClassName())) {
                    return;
                }
            }

            startService(new Intent(getApplicationContext(), VerificarNovasNotasService.class));

            new NotificationManager(ConfiguracoesActivity.this)
                    .createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                            "MeuSUAP",
                            "Estou verificando novas notas!",
                            MainActivity.class, new Intent(getApplicationContext(), MainActivity.class));

        } else {
            stopService(new Intent(getApplicationContext(), VerificarNovasNotasService.class));
        }
    }
}
