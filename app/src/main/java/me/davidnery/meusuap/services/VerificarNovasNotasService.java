package me.davidnery.meusuap.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import me.davidnery.meusuap.AlunosActivity;
import me.davidnery.meusuap.DisciplinasActivity;
import me.davidnery.meusuap.R;
import me.davidnery.meusuap.auth.AuthCheck;
import me.davidnery.meusuap.sqlite.SQLiteController;
import me.davidnery.meusuap.utils.NotificationManager;

/**
 * Created by david on 01/11/2018.
 */

public class VerificarNovasNotasService extends Service {

    private Thread thread;

    private AuthCheck authCheck;
    private SQLiteController sqLiteController;

    private boolean canNext, running;

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        authCheck = new AuthCheck();
        sqLiteController = new SQLiteController(getApplicationContext());

        canNext = true;
        running = true;

        notificationManager = new NotificationManager(getApplicationContext());

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {
                    if (canNext) {
                        canNext = false;

                        Cursor alunos = sqLiteController.getAlunos();
                        if (alunos.moveToFirst()) {
                            do {
                                String matricula = alunos.getString(0);
                                String token = alunos.getString(5);

                                JSONArray diarios = null;
                                try {
                                    diarios = authCheck.getDiarios(token);

                                    if (diarios.getJSONObject(0).has("responseCode")) {
                                        if (diarios.getJSONObject(0).getInt("responseCode") == 401) {
                                            // Provavelmente o token expirou
                                            JSONObject json = authCheck.getToken(matricula, alunos.getString(1));
                                            if (json.has("responseCode")) {
                                                // usuário alterou senha
                                                if (json.getInt("responseCode") == 401) {
                                                    notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                                            alunos.getString(alunos.getColumnIndex("nome")),
                                                            "Sua senha do SUAP foi atualiza, atualize-a no app!",
                                                            AlunosActivity.class, new Intent(getApplicationContext(), AlunosActivity.class));
                                                    continue;
                                                } else {
                                                    break;
                                                }
                                            } else {
                                                // Token só havia expirado
                                                token = json.getString("token");

                                                try {
                                                    sqLiteController.cadastrarAluno(
                                                            matricula,
                                                            alunos.getString(alunos.getColumnIndex("nome")),
                                                            alunos.getString(alunos.getColumnIndex("senha")),
                                                            token
                                                    );
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    continue;
                                                }

                                                diarios = authCheck.getDiarios(token);
                                            }
                                        } else {
                                            break;
                                        }

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    break;
                                }

                                // Salvando
                                int faltas = 0;

                                String notasAtualizadas = "", faltasGanhas = "", faltasPerdidas = "";

                                Cursor materias = sqLiteController.getAllMaterias(matricula);
                                if (materias.moveToFirst()) {
                                    do {
                                        for (int i = 0; i < diarios.length(); i++) {
                                            try {
                                                JSONObject materia = diarios.getJSONObject(i);

                                                String nome = materia.getString("disciplina").split("- ")[1].split("\\(")[0];
                                                if (nome.equals(materias.getString(materias.getColumnIndex("nome")))) {

                                                    JSONObject etapa1 = materia.getJSONObject("nota_etapa_1");
                                                    Object nota1 = etapa1.get("nota");
                                                    int n1 = !(nota1 instanceof Integer) ? -1 : Integer.parseInt(nota1.toString());
                                                    JSONObject etapa2 = materia.getJSONObject("nota_etapa_2");
                                                    Object nota2 = etapa2.get("nota");
                                                    int n2 = !(nota2 instanceof Integer) ? -1 : Integer.parseInt(nota2.toString());
                                                    JSONObject etapa3 = materia.getJSONObject("nota_etapa_3");
                                                    Object nota3 = etapa3.get("nota");
                                                    int n3 = !(nota3 instanceof Integer) ? -1 : Integer.parseInt(nota3.toString());
                                                    JSONObject etapa4 = materia.getJSONObject("nota_etapa_4");
                                                    Object nota4 = etapa4.get("nota");
                                                    int n4 = !(nota4 instanceof Integer) ? -1 : Integer.parseInt(nota4.toString());
                                                    JSONObject avaliacaofinal = materia.getJSONObject("nota_avaliacao_final");
                                                    Object notafinal = avaliacaofinal.get("nota");
                                                    Object media = materia.get("media_final_disciplina");

                                                    if (materias.getInt(materias.getColumnIndex("totalFaltas")) > materia.getInt("numero_faltas"))
                                                        faltasGanhas += nome + "(" +
                                                                (materia.getInt("numero_faltas") - materias.getInt(materias.getColumnIndex("totalFaltas")))
                                                                + "), ";
                                                    else if (materias.getInt(materias.getColumnIndex("totalFaltas")) < materia.getInt("numero_faltas"))
                                                        faltasPerdidas += nome + "(" +
                                                                (materias.getInt(materias.getColumnIndex("totalFaltas")) - materia.getInt("numero_faltas"))
                                                                + "), ";

                                                    if (n1 != materias.getInt(materias.getColumnIndex("nota1"))
                                                            || n2 != materias.getInt(materias.getColumnIndex("nota2"))
                                                            || n3 != materias.getInt(materias.getColumnIndex("nota3"))
                                                            || n4 != materias.getInt(materias.getColumnIndex("nota4"))
                                                            || materias.getInt(materias.getColumnIndex("totalFaltas")) != materia.getInt("numero_faltas")) {

                                                        notasAtualizadas += nome + ", ";

                                                        sqLiteController.inserirMateria(
                                                                materia.getInt("codigo_diario"), matricula,
                                                                nome,
                                                                materia.getInt("carga_horaria"), materia.getInt("carga_horaria_cumprida"),
                                                                n1, etapa1.getInt("faltas"),
                                                                n2, etapa2.getInt("faltas"),
                                                                n3, etapa3.getInt("faltas"),
                                                                n4, etapa4.getInt("faltas"),
                                                                materia.getInt("numero_faltas"),
                                                                materia.getString("situacao"),
                                                                !(notafinal instanceof Integer) ? -1 : Integer.parseInt(notafinal.toString()),
                                                                String.valueOf(media).equals("None") ? -1 : Integer.parseInt(materia.getString("media_final_disciplina"))
                                                        );
                                                    }

                                                    break;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } while (materias.moveToNext());
                                }
                                materias.close();

                                if (!notasAtualizadas.equals("")) {
                                    notasAtualizadas = notasAtualizadas.substring(0, notasAtualizadas.length() - 2);

                                    Intent disciplinas = new Intent(getApplicationContext(), DisciplinasActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("matricula", alunos.getString(alunos.getColumnIndex("_id")));
                                    disciplinas.putExtras(bundle);

                                    if (notasAtualizadas.split(", ").length > 1)
                                        notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                                alunos.getString(alunos.getColumnIndex("nome")),
                                                "Novas notas encontradas nas matérias " + notasAtualizadas + "!",
                                                DisciplinasActivity.class, disciplinas);
                                    else
                                        notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                                alunos.getString(alunos.getColumnIndex("nome")),
                                                "Novas notas encontradas na matéria " + notasAtualizadas + "!",
                                                DisciplinasActivity.class, disciplinas);
                                }

                                if (!faltasGanhas.equals("") || !faltasPerdidas.equals("")) {
                                    Intent disciplinas = new Intent(getApplicationContext(), DisciplinasActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("matricula", alunos.getString(alunos.getColumnIndex("_id")));
                                    disciplinas.putExtras(bundle);

                                    if (!faltasGanhas.equals("")) {
                                        faltasGanhas = faltasGanhas.substring(0, faltasGanhas.length() - 2);

                                        if (faltasGanhas.split(", ").length > 1)
                                            notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                                    alunos.getString(alunos.getColumnIndex("nome")),
                                                    "Você tem " + faltas + " novas faltas na matéria " + faltasGanhas + "!",
                                                    DisciplinasActivity.class, disciplinas);
                                        else
                                            notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                                    alunos.getString(alunos.getColumnIndex("nome")),
                                                    "Você tem " + faltas + " novas faltas nas matérias " + faltasGanhas + "!",
                                                    DisciplinasActivity.class, disciplinas);
                                    }

                                    if (!faltasPerdidas.equals("")) {
                                        faltasPerdidas = faltasPerdidas.substring(0, faltasPerdidas.length() - 2);

                                        if (faltasPerdidas.split(", ").length > 1)
                                            notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                                    alunos.getString(alunos.getColumnIndex("nome")),
                                                    "Foram retiradas " + faltas + " faltas existentes na matéria " + faltasPerdidas + "!",
                                                    DisciplinasActivity.class, disciplinas);
                                        else
                                            notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                                    alunos.getString(alunos.getColumnIndex("nome")),
                                                    "Foram retiradas " + faltas + " faltas existentes nas matérias " + faltasPerdidas + "!",
                                                    DisciplinasActivity.class, disciplinas);
                                    }
                                }

                            } while (alunos.moveToNext());
                        }
                        alunos.close();

                        canNext = true;
                    }

                    try {
                        Thread.sleep(1 * 1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
