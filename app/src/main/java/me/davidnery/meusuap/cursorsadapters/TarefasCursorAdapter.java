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

public class TarefasCursorAdapter extends CursorAdapter {

    public TarefasCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.tarefas_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView id = view.findViewById(R.id.tarefaid);
        TextView nome = view.findViewById(R.id.tarefasnome);
        TextView data = view.findViewById(R.id.tarefasdata);

        id.setText(cursor.getInt(cursor.getColumnIndex("_id")) + "");
        nome.setText(cursor.getString(cursor.getColumnIndex("nome")));

        String[] date = cursor.getString(cursor.getColumnIndex("data")).split("-");
        data.setText("Para o dia " + date[2] + "/" + date[1] + "/" + date[0]);

    }
}
