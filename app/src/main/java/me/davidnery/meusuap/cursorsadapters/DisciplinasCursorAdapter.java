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

public class DisciplinasCursorAdapter extends CursorAdapter {

    public DisciplinasCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.disciplinas_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView matricula = view.findViewById(R.id.disciplinasnome);
        TextView nome = view.findViewById(R.id.disciplinasmedia);

        matricula.setText(cursor.getString(0)); // nome
        nome.setText("Média: " + (cursor.getInt(1) == -1 ? "Sem média" : cursor.getInt(1)));

    }
}
