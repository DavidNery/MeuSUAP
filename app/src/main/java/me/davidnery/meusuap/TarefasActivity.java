package me.davidnery.meusuap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.davidnery.meusuap.cursorsadapters.TarefasCursorAdapter;
import me.davidnery.meusuap.sqlite.SQLiteController;

public class TarefasActivity extends AppCompatActivity {

    private ListView tarefas;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefas);

        this.tarefas = (ListView) findViewById(R.id.tarefascadastradas);

        final String matricula = getIntent().getExtras().getString("matricula");

        final SQLiteController db = new SQLiteController(getApplicationContext());
        Cursor cursor = db.getTarefas(matricula);

        if (cursor.getCount() == 0)
            findViewById(R.id.semtarefas).setVisibility(View.VISIBLE);

        TarefasCursorAdapter adapter = new TarefasCursorAdapter(this, cursor);

        tarefas.setAdapter(adapter);

        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        tarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TarefasActivity.this);
                final AlertDialog apagar = builder.setTitle("Apagar tarefa")
                        .setMessage("Deseja realmente apagar essa tarefa?")
                        .setPositiveButton(R.string.btnapagar, null)
                        .setNegativeButton(R.string.btncancelar, null)
                        .create();

                apagar.setOnShowListener(new AlertDialog.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        final Button positivebutton = apagar.getButton(AlertDialog.BUTTON_POSITIVE);
                        positivebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        final Button negativebutton = apagar.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        positivebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                db.deleteTarefa(((TextView) view.findViewById(R.id.tarefaid)).getText().toString());

                                showToast("Tarefa apagada!", Toast.LENGTH_SHORT);
                                apagar.dismiss();

                                Cursor cursor = db.getTarefas(matricula);

                                if (cursor.getCount() > 0)
                                    findViewById(R.id.semtarefas).setVisibility(View.GONE);

                                TarefasCursorAdapter adapter = new TarefasCursorAdapter(TarefasActivity.this, cursor);

                                tarefas.setAdapter(adapter);
                            }
                        });

                        negativebutton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                apagar.dismiss();
                            }
                        });

                    }
                });

                apagar.show();

                return true;
            }
        });

        tarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = db.getTarefa(((TextView) view.findViewById(R.id.tarefaid)).getText().toString());

                String[] dt = cursor.getString(cursor.getColumnIndex("data")).split("-");

                AlertDialog.Builder builder = new AlertDialog.Builder(TarefasActivity.this);
                final AlertDialog tarefa = builder.setTitle("Tarefa")
                        .setMessage("Nome: " + cursor.getString(cursor.getColumnIndex("nome")) +
                                "\nDescricao: " + cursor.getString(cursor.getColumnIndex("descricao")) +
                                "\nPara o dia " + dt[2] + "/" + dt[1] + "/" + dt[0]
                        )
                        .setPositiveButton(R.string.btnok, null)
                        .create();

                tarefa.setOnShowListener(new AlertDialog.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        final Button positivebutton = tarefa.getButton(AlertDialog.BUTTON_POSITIVE);
                        positivebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        final Button negativebutton = tarefa.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    }
                });

                tarefa.show();
            }
        });

        findViewById(R.id.fabtarefas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TarefasActivity.this);
                final AlertDialog criartarefa = builder.setTitle("Criando nova tarefa")
                        .setView(getLayoutInflater().inflate(R.layout.dialog_cadastrartarefa_layout, null))
                        .setPositiveButton(R.string.btncriar, null)
                        .setNegativeButton(R.string.btncancelar, null)
                        .create();

                criartarefa.setOnShowListener(new AlertDialog.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        final Calendar c = Calendar.getInstance();

                        final EditText tarefaNome = (EditText) criartarefa.findViewById(R.id.tarefanome);
                        final EditText tarefaDescricao = (EditText) criartarefa.findViewById(R.id.tarefadescricao);
                        final EditText tarefaData = (EditText) criartarefa.findViewById(R.id.tarefadata);

                        final Button positivebutton = criartarefa.getButton(AlertDialog.BUTTON_POSITIVE);
                        positivebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        final Button negativebutton = criartarefa.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        tarefaData.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DatePickerDialog(TarefasActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        c.set(Calendar.YEAR, year);
                                        c.set(Calendar.MONTH, month);
                                        c.set(Calendar.DAY_OF_MONTH, day);

                                        tarefaData.setText(sdf.format(c.getTime()));
                                    }
                                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });

                        positivebutton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (tarefaNome.getText().toString().trim().equals("")) {
                                    showToast("Informe um nome para tarefa!", Toast.LENGTH_SHORT);
                                    return;
                                }
                                if (tarefaData.getText().toString().trim().equals("")) {
                                    showToast("Informe uma data para tarefa!", Toast.LENGTH_SHORT);
                                    return;
                                }

                                db.cadastrarTarefa(tarefaNome.getText().toString().trim(),
                                        tarefaDescricao.getText().toString().trim(), c, matricula);

                                Cursor cursor = db.getTarefas(matricula);

                                if (cursor.getCount() > 0)
                                    findViewById(R.id.semtarefas).setVisibility(View.GONE);

                                TarefasCursorAdapter adapter = new TarefasCursorAdapter(TarefasActivity.this, cursor);

                                tarefas.setAdapter(adapter);

                                criartarefa.dismiss();
                                showToast("Tarefa registrada para o dia " + tarefaData.getText().toString().trim() + "!",
                                        Toast.LENGTH_LONG);
                            }
                        });

                        negativebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                criartarefa.dismiss();
                            }
                        });

                    }
                });

                criartarefa.show();
            }
        });
    }

    public void showToast(String mensagem, int time) {
        if (toast != null && toast.getView().isShown())
            toast.cancel();

        toast = Toast.makeText(getApplicationContext(), mensagem, time);
        toast.show();
    }

}
