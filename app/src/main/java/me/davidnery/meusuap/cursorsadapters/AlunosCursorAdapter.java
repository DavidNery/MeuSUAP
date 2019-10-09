package me.davidnery.meusuap.cursorsadapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import me.davidnery.meusuap.R;

/**
 * Created by david on 26/10/2018.
 */

public class AlunosCursorAdapter extends CursorAdapter {

    public AlunosCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.alunos_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView matricula = view.findViewById(R.id.aluno_matricula);
        TextView nome = view.findViewById(R.id.aluno_nome);
        TextView faltas = view.findViewById(R.id.aluno_faltas);

        matricula.setText(cursor.getString(cursor.getColumnIndex("_id"))); // matricula
        nome.setText(cursor.getString(cursor.getColumnIndex("nome")));

        int faltasrestantes = (int) Math.floor(cursor.getInt(cursor.getColumnIndex("cargahoraria")) * 0.25 - cursor.getInt(cursor.getColumnIndex("faltas")));
        String resto = "";

        if (faltasrestantes < 0)
            resto = "Você está reprovado por falta!";
        else if (faltasrestantes == 0)
            resto = "Você não pode mais faltar nenhuma aula!";
        else
            resto = "Caso goste de matar aula, você ainda pode faltar " + faltasrestantes + " aulas!";

        faltas.setText("Você tem " + cursor.getInt(cursor.getColumnIndex("faltas")) + " faltas!\n" + resto);

    }

}
