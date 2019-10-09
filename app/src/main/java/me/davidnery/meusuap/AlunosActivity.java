package me.davidnery.meusuap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.davidnery.meusuap.cursorsadapters.AlunosCursorAdapter;
import me.davidnery.meusuap.sqlite.SQLiteController;
import me.davidnery.meusuap.utils.ImportDisciplinasTask;

public class AlunosActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ListView alunos;

    private Toast toast;

    private View dialogLogin;
    private ProgressBar importProgress;
    private TextView importStatus;
    private String matricula, senha;
    private AlertDialog login;

    private ImportDisciplinasTask importDisciplinasTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alunos);

        this.alunos = (ListView) findViewById(R.id.alunoscadastrados);

        final SQLiteController db = new SQLiteController(getApplicationContext());
        Cursor cursor = db.getAlunos();

        if (cursor.getCount() == 0)
            findViewById(R.id.semalunos).setVisibility(View.VISIBLE);

        AlunosCursorAdapter adapter = new AlunosCursorAdapter(this, cursor);

        alunos.setAdapter(adapter);

        alunos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AlunosActivity.this);
                final AlertDialog apagar = builder.setTitle("Apagar aluno")
                        .setMessage("Deseja realmente apagar esse aluno?")
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

                                db.deleteAluno(((TextView) view.findViewById(R.id.aluno_matricula)).getText().toString());

                                showToast("Aluno apagado!", Toast.LENGTH_SHORT);
                                apagar.dismiss();

                                finish();
                                startActivity(new Intent(getApplicationContext(), AlunosActivity.class));
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

        alunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent disciplinas = new Intent(AlunosActivity.this, DadosAlunoActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("matricula", ((TextView) view.findViewById(R.id.aluno_matricula)).getText().toString());
                disciplinas.putExtras(bundle);

                startActivity(disciplinas);
                return;
            }
        });

        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialogLogin = getLayoutInflater().inflate(R.layout.dialog_login_layout, null);
                importProgress = dialogLogin.findViewById(R.id.importProgress);
                importStatus = dialogLogin.findViewById(R.id.importStatus);

                AlertDialog.Builder builder = new AlertDialog.Builder(AlunosActivity.this);
                login = builder.setTitle("Informe sua matricula e senha")
                        .setView(dialogLogin)
                        .setPositiveButton(R.string.btnlogar, null)
                        .setNegativeButton(R.string.btncancelar, null)
                        .create();

                login.setOnShowListener(new AlertDialog.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        final Button positivebutton = login.getButton(AlertDialog.BUTTON_POSITIVE);
                        positivebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        final Button negativebutton = login.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativebutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        positivebutton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                matricula = ((EditText) dialogLogin.findViewById(R.id.loginmatricula)).getText().toString().trim();
                                senha = ((EditText) dialogLogin.findViewById(R.id.loginsenha)).getText().toString().trim();

                                if (matricula.equals("") || senha.equals("")) {
                                    showToast("Informe sua matricula e a senha!", Toast.LENGTH_LONG);
                                    return;
                                }

                                positivebutton.setEnabled(false);
                                positivebutton.setTextColor(getResources().getColor(R.color.colorGrey));
                                negativebutton.setEnabled(false);
                                negativebutton.setTextColor(getResources().getColor(R.color.colorGrey));
                                login.setCancelable(false);

                                importDisciplinasTask = new ImportDisciplinasTask();
                                importDisciplinasTask.execute(AlunosActivity.this);
                            }
                        });

                        negativebutton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                login.dismiss();
                            }
                        });

                    }
                });

                login.show();
            }
        });
    }

    public void showToast(String mensagem, int time) {
        if (toast != null && toast.getView().isShown())
            toast.cancel();

        toast = Toast.makeText(getApplicationContext(), mensagem, time);
        toast.show();
    }

    public View getDialogLogin() {
        return dialogLogin;
    }

    public void saveDisciplinas(JSONArray diarios, JSONObject info, String token) {
        try {
            if (diarios.getJSONObject(0).has("detail")) {
                if (diarios.getJSONObject(0).getString("detail").equals("Matricula/senha incorreta!")) {
                    login.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    login.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    login.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
                    login.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    importStatus.setText("Matricula/senha incorreta!");
                } else {
                    showToast(diarios.getJSONObject(0).getString("detail"), Toast.LENGTH_LONG);
                    dismissLogin();
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Não foi possível importar suas disciplinas!", Toast.LENGTH_LONG);
            dismissLogin();
            return;
        }

        SQLiteController sqlite = new SQLiteController(getApplicationContext());

        try {
            sqlite.cadastrarAluno(
                    info.getJSONObject("vinculo").getString("matricula"),
                    senha,
                    info.getJSONObject("vinculo").getString("nome"),
                    token
            );
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Não foi possível salvar esse aluno!", Toast.LENGTH_LONG);
            dismissLogin();
            return;
        }

        JSONObject materia;
        for (int i = 0; i < diarios.length(); i++) {
            try {
                materia = diarios.getJSONObject(i);

                JSONObject etapa1 = materia.getJSONObject("nota_etapa_1");
                Object nota1 = etapa1.get("nota");
                JSONObject etapa2 = materia.getJSONObject("nota_etapa_2");
                Object nota2 = etapa2.get("nota");
                JSONObject etapa3 = materia.getJSONObject("nota_etapa_3");
                Object nota3 = etapa3.get("nota");
                JSONObject etapa4 = materia.getJSONObject("nota_etapa_4");
                Object nota4 = etapa4.get("nota");
                JSONObject avaliacaofinal = materia.getJSONObject("nota_avaliacao_final");
                Object notafinal = avaliacaofinal.get("nota");
                Object media = materia.get("media_final_disciplina");

                sqlite.inserirMateria(
                        materia.getInt("codigo_diario"),
                        getMatricula(),
                        materia.getString("disciplina").split("- ")[1].split("\\(")[0],
                        materia.getInt("carga_horaria"), materia.getInt("carga_horaria_cumprida"),
                        !(nota1 instanceof Integer) ? -1 : Integer.parseInt(nota1.toString()), etapa1.getInt("faltas"),
                        !(nota2 instanceof Integer) ? -1 : Integer.parseInt(nota2.toString()), etapa2.getInt("faltas"),
                        !(nota3 instanceof Integer) ? -1 : Integer.parseInt(nota3.toString()), etapa3.getInt("faltas"),
                        !(nota4 instanceof Integer) ? -1 : Integer.parseInt(nota4.toString()), etapa4.getInt("faltas"),
                        materia.getInt("numero_faltas"),
                        materia.getString("situacao"),
                        !(notafinal instanceof Integer) ? -1 : Integer.parseInt(notafinal.toString()),
                        String.valueOf(media).equals("None") ? -1 : Integer.parseInt(materia.getString("media_final_disciplina"))
                );
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Não foi possível salvar suas matérias!", Toast.LENGTH_LONG);
                dismissLogin();
            }
        }

        finish();
        startActivity(new Intent(getApplicationContext(), AlunosActivity.class));
    }

    public void setImportProgress(int progress) {
        switch (progress) {
            case 0:
                importStatus.setText(R.string.solicitandotoken);
                break;
            case 25:
                importStatus.setText(R.string.solicitandoinfo);
                break;
            case 50:
                importStatus.setText(R.string.solicitandodiarios);
                break;
            case 75:
                importStatus.setText(R.string.salvandomaterias);
                break;
            default:
                break;
        }
        importProgress.setProgress(progress);
    }

    public String getMatricula() {
        return matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void dismissLogin() {
        login.dismiss();
    }

}
