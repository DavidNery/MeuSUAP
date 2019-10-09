package me.davidnery.meusuap.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.davidnery.meusuap.R;
import me.davidnery.meusuap.TarefasActivity;
import me.davidnery.meusuap.models.Tarefa;
import me.davidnery.meusuap.sqlite.SQLiteController;
import me.davidnery.meusuap.utils.NotificationManager;

/**
 * Created by david on 17/11/2018.
 */

public class VerifyTarefasService extends Service {

    private Thread thread;

    private boolean running;

    private SQLiteController sqLiteController;

    private NotificationManager notificationManager;
    private SimpleDateFormat formattedDate, date, datetime;

    @Override
    public void onCreate() {
        sqLiteController = new SQLiteController(getApplicationContext());
        notificationManager = new NotificationManager(getApplicationContext());
        formattedDate = new SimpleDateFormat("dd/MM/yyyy");
        date = new SimpleDateFormat("yyyy-MM-dd");
        datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        running = true;

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {

                    List<Tarefa> t = new ArrayList<>();

                    Calendar c = Calendar.getInstance();
                    Cursor tarefas = sqLiteController.getTarefas(null);
                    if (tarefas.moveToFirst()) {
                        do {
                            String mostrou = tarefas.getString(tarefas.getColumnIndex("mostrou"));

                            //long diffDays = 0;
                            long diffMostrou = 0;
                            try {
                                diffMostrou = TimeUnit.MILLISECONDS.toHours(c.getTime().getTime() - datetime.parse(mostrou).getTime());
                                //diffDays = TimeUnit.MILLISECONDS.toDays(date.parse(data).getTime()-c.getTime().getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (diffMostrou < 24) continue;

                            String tarefaNome = tarefas.getString(tarefas.getColumnIndex("nome"));
                            try {
                                t.add(new Tarefa(
                                        tarefaNome,
                                        tarefas.getString(tarefas.getColumnIndex("aluno")),
                                        tarefas.getString(tarefas.getColumnIndex("alunonome")),
                                        formattedDate.format(date.parse(tarefas.getString(tarefas.getColumnIndex("data"))))));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            sqLiteController.setTarefaMostrou(tarefas.getInt(tarefas.getColumnIndex("_id")));

                        } while (tarefas.moveToNext());
                    }

                    if (t.size() >= 1) {
                        String aluno = "", alunoNome = "";
                        List<String> notifications = new ArrayList<>();
                        for (Tarefa tarefa : t) {
                            if (!aluno.equals(tarefa.getAluno())) {
                                if (notifications.size() > 0) {
                                    String tl = "";
                                    for (String s : notifications)
                                        tl += s + "\n";

                                    tl = tl.substring(0, tl.length() - 2);

                                    Intent ta = new Intent(getApplicationContext(), TarefasActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("matricula", aluno);
                                    ta.putExtras(bundle);

                                    notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                            alunoNome,
                                            "Eiii!! Não esquece dessas tarefas para os próximos sete dias:\n" + tl,
                                            TarefasActivity.class, ta);

                                    notifications.clear();
                                }
                            }

                            aluno = tarefa.getAluno();
                            alunoNome = tarefa.getAlunoNome();

                            Intent ta = new Intent(getApplicationContext(), TarefasActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("matricula", aluno);
                            ta.putExtras(bundle);

                            notifications.add(" - " + tarefa.getNome() + " (" + tarefa.getTo() + "),");
                        }

                        if (notifications.size() > 0) {
                            String tl = "";
                            for (String s : notifications)
                                tl += s + "\n";

                            tl = tl.substring(0, tl.length() - 2);

                            Intent ta = new Intent(getApplicationContext(), TarefasActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("matricula", aluno);
                            ta.putExtras(bundle);

                            notificationManager.createAndShowNotification(R.drawable.ic_notifications_none_black_24dp,
                                    alunoNome,
                                    "Eiii!! Não esquece dessas tarefas para os próximos sete dias:\n" + tl,
                                    TarefasActivity.class, ta);
                        }
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
