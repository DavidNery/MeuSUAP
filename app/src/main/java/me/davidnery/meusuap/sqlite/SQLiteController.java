package me.davidnery.meusuap.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SQLiteController {

    private SQLiteDatabase db;
    private final SQLiteMaker maker;

    private final SimpleDateFormat sdf;

    public SQLiteController(Context context) {
        this.maker = new SQLiteMaker(context);
        this.sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    public void cadastrarAluno(String matricula, String senha, String nome, String token) {

        db = maker.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("matricula", matricula);
        values.put("senha", senha);
        values.put("nome", nome);
        values.put("token", token);

        db.insertWithOnConflict("alunos", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();

    }

    public void inserirMateria(int codigo, String matricula, String nome, int cargahoraria, int cargahorariacumprida,
                               int nota1, int faltas1,
                               int nota2, int faltas2,
                               int nota3, int faltas3,
                               int nota4, int faltas4,
                               int totalFaltas, String situacao, int notafinal, int media) {

        db = maker.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("codigo", codigo);
        values.put("aluno", matricula);
        values.put("nome", nome);
        values.put("cargahoraria", cargahoraria);
        values.put("cargahorariacumprida", cargahorariacumprida);
        values.put("nota1", nota1);
        values.put("faltas1", faltas1);
        values.put("nota2", nota2);
        values.put("faltas2", faltas2);
        values.put("nota3", nota3);
        values.put("faltas3", faltas3);
        values.put("nota4", nota4);
        values.put("faltas4", faltas4);
        values.put("totalFaltas", totalFaltas);
        values.put("situacao", situacao);
        values.put("final", notafinal);
        values.put("media_final_disciplina", media);

        db.insertWithOnConflict("materias", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Cursor getAllMaterias(String matricula) {
        db = maker.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM materias WHERE aluno=?",
                new String[]{matricula});

        if (cursor != null) cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getMaterias(String matricula) {
        db = maker.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nome AS _id, media_final_disciplina media FROM materias WHERE aluno=?",
                new String[]{matricula});

        if (cursor != null) cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getMateria(String nome, String matricula) {
        db = maker.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM materias WHERE aluno=? AND nome=?",
                new String[]{matricula, nome});

        if (cursor != null) cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getAlunos() {
        db = maker.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT a.matricula AS _id, a.senha AS senha, a.nome AS nome, " +
                "(SELECT SUM(m.faltas1 + m.faltas2 + m.faltas3 + m.faltas4) FROM materias m WHERE m.aluno=a.matricula) AS faltas, " +
                "(SELECT SUM(m.cargahoraria) FROM materias m WHERE m.aluno=a.matricula) AS cargahoraria, " +
                "a.token AS token " +
                "FROM alunos a", null);

        if (cursor != null) cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public void deleteAluno(String matricula) {
        db = maker.getWritableDatabase();

        db.delete("materias", "aluno = ?", new String[]{matricula});
        db.delete("alunos", "matricula = ?", new String[]{matricula});

        db.close();
    }

    public void cadastrarTarefa(String nome, String descricao, Calendar data, String matricula) {
        db = maker.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome", nome);
        values.put("descricao", descricao);
        values.put("data", sdf.format(data.getTime()));
        values.put("aluno", matricula);

        db.insert("tarefas", null, values);
        db.close();

    }

    public Cursor getTarefas(String matricula) {
        db = maker.getReadableDatabase();
        Cursor cursor;
        if (matricula == null)
            cursor = db.rawQuery("SELECT t.*, a.nome alunonome FROM tarefas t, alunos a " +
                    "WHERE t.data>=CURRENT_DATE AND (DATE(t.data, '-7 days'))<=CURRENT_DATE AND " +
                    "t.aluno = a.matricula " +
                    "ORDER BY t.aluno, t.data", null);
        else
            cursor = db.rawQuery("SELECT * FROM tarefas WHERE aluno=? ORDER BY data", new String[]{matricula});

        if (cursor != null) cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getTarefa(String id) {
        db = maker.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tarefas WHERE _id=?", new String[]{id});

        if (cursor != null) cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public void setTarefaMostrou(int id) {
        db = maker.getWritableDatabase();
        db.execSQL("UPDATE tarefas SET mostrou=DATETIME('now', 'localtime') WHERE _id=" + id);
        db.close();
    }

    public void deleteTarefa(String id) {
        db = maker.getWritableDatabase();

        db.delete("tarefas", "_id = ?", new String[]{id});

        db.close();
    }

}
