package me.davidnery.meusuap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DadosAlunoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_aluno);

        findViewById(R.id.carddisciplinas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent disciplinas = new Intent(DadosAlunoActivity.this, DisciplinasActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("matricula", getIntent().getExtras().getString("matricula"));
                disciplinas.putExtras(bundle);

                startActivity(disciplinas);
            }
        });

        findViewById(R.id.cardtarefas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent disciplinas = new Intent(DadosAlunoActivity.this, TarefasActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("matricula", getIntent().getExtras().getString("matricula"));
                disciplinas.putExtras(bundle);

                startActivity(disciplinas);
            }
        });
    }
}
