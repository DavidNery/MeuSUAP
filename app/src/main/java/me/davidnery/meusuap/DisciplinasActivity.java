package me.davidnery.meusuap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import me.davidnery.meusuap.cursorsadapters.DisciplinasCursorAdapter;
import me.davidnery.meusuap.sqlite.SQLiteController;

public class DisciplinasActivity extends AppCompatActivity {

    private String matricula;

    private ListView disciplinasCadastradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplinas);

        this.matricula = getIntent().getExtras().getString("matricula");

        this.disciplinasCadastradas = (ListView) findViewById(R.id.disciplinascadastradas);

        final SQLiteController db = new SQLiteController(getApplicationContext());
        Cursor cursor = db.getMaterias(matricula);

        DisciplinasCursorAdapter adapter = new DisciplinasCursorAdapter(this, cursor);

        disciplinasCadastradas.setAdapter(adapter);

        disciplinasCadastradas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Cursor materia = db.getMateria(((TextView) view.findViewById(R.id.disciplinasnome)).getText().toString(), matricula);

                final Object n1 = (materia.getInt(materia.getColumnIndex("nota1")) == -1 ? "Sem nota" : materia.getInt(materia.getColumnIndex("nota1"))),
                        n2 = (materia.getInt(materia.getColumnIndex("nota2")) == -1 ? "Sem nota" : materia.getInt(materia.getColumnIndex("nota2"))),
                        n3 = (materia.getInt(materia.getColumnIndex("nota3")) == -1 ? "Sem nota" : materia.getInt(materia.getColumnIndex("nota3"))),
                        n4 = (materia.getInt(materia.getColumnIndex("nota4")) == -1 ? "Sem nota" : materia.getInt(materia.getColumnIndex("nota4")));

                String info = "Nome: " + materia.getString(materia.getColumnIndex("nome"))
                        + "\nCarga horária: " + materia.getInt(materia.getColumnIndex("cargahoraria")) + " aulas"
                        + "\nCarga horária cumprida: " + materia.getInt(materia.getColumnIndex("cargahorariacumprida")) + " aulas"
                        + "\nNota 1º Bim: " + n1
                        + "\nFaltas 1º Bim: " + materia.getInt(materia.getColumnIndex("faltas1"))
                        + "\nNota 2º Bim: " + n2
                        + "\nFaltas 2º Bim: " + materia.getInt(materia.getColumnIndex("faltas2"));
                if (materia.getInt(materia.getColumnIndex("cargahoraria")) > 45)
                    info += "\nNota 3º Bim: " + n3
                            + "\nFaltas 3º Bim: " + materia.getInt(materia.getColumnIndex("faltas3"))
                            + "\nNota 4º Bim: " + n4
                            + "\nFaltas 4º Bim: " + materia.getInt(materia.getColumnIndex("faltas4"));

                info += "\nTotal Faltas: " + materia.getInt(materia.getColumnIndex("totalFaltas"))
                        + "\nSituação: " + materia.getString(materia.getColumnIndex("situacao"))
                        + "\nFinal: " + (materia.getInt(materia.getColumnIndex("final")) == -1 ? "Sem nota" : materia.getInt(materia.getColumnIndex("final")))
                        + "\nMédia final: " + (materia.getInt(materia.getColumnIndex("media_final_disciplina")) == -1 ? "Sem nota" : materia.getInt(materia.getColumnIndex("media_final_disciplina")));

                final AlertDialog dialog = new AlertDialog.Builder(DisciplinasActivity.this)
                        .setTitle("Info")
                        .setMessage(info)
                        .setNeutralButton("Calcular", null)
                        .setPositiveButton("Já vi!", null)
                        .create();

                dialog.setOnShowListener(new AlertDialog.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        final Button positivebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positivebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        final Button neutralbutton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                        neutralbutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        positivebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        neutralbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                if (materia.getInt(materia.getColumnIndex("cargahoraria")) <= 45) {
                                    if (!(n1 instanceof String))
                                        bundle.putInt("n1_sem", (int) n1);
                                    if (!(n2 instanceof String))
                                        bundle.putInt("n2_sem", (int) n2);
                                } else {
                                    if (!(n1 instanceof String))
                                        bundle.putInt("n1_anual", (int) n1);
                                    if (!(n2 instanceof String))
                                        bundle.putInt("n2_anual", (int) n2);
                                }
                                if (!(n3 instanceof String))
                                    bundle.putInt("n3", (int) n3);
                                if (!(n4 instanceof String))
                                    bundle.putInt("n4", (int) n4);

                                Intent intent = new Intent(getApplicationContext(), CalcularNota.class);
                                intent.putExtras(bundle);

                                startActivity(intent);
                            }
                        });

                    }
                });

                dialog.show();

            }
        });
    }
}
