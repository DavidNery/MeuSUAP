package me.davidnery.meusuap.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by david on 18/07/2018.
 */

public class SQLiteMaker extends SQLiteOpenHelper {

    public SQLiteMaker(Context context) {
        super(context, "informacoes.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS alunos (" +
                "matricula BIGINT PRIMARY KEY, " +
                "senha VARCHAR(255) NOT NULL, " +
                "nome VARCHAR(255) NOT NULL, " +
                "token TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS materias (" +
                "codigo INT NOT NULL, " +
                "aluno BIGINT NOT NULL, " +
                "nome VARCHAR(255) NOT NULL, " +
                "cargahoraria INT NOT NULL DEFAULT 0, " +
                "cargahorariacumprida INT NOT NULL DEFAULT 0, " +
                "nota1 INT NOT NULL DEFAULT 0, " +
                "faltas1 INT NOT NULL DEFAULT 0, " +
                "nota2 INT NOT NULL DEFAULT 0, " +
                "faltas2 INT NOT NULL DEFAULT 0, " +
                "nota3 INT NOT NULL DEFAULT 0, " +
                "faltas3 INT NOT NULL DEFAULT 0, " +
                "nota4 INT NOT NULL DEFAULT 0, " +
                "faltas4 INT NOT NULL DEFAULT 0, " +
                "totalFaltas INT NOT NULL DEFAULT 0, " +
                "situacao VARCHAR(15) NOT NULL, " +
                "final INT NOT NULL DEFAULT 0, " +
                "media_final_disciplina INT NOT NULL DEFAULT 0, " +
                "PRIMARY KEY(codigo, aluno), " +
                "FOREIGN KEY(aluno) REFERENCES alunos(matricula)" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS tarefas (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome VARCHAR(50) NOT NULL, " +
                "descricao TEXT, " +
                "data DATE NOT NULL, " +
                "mostrou DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "aluno BIGINT NOT NULL, " +
                "FOREIGN KEY(aluno) REFERENCES alunos(matricula)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(newVersion);
        onCreate(db);
    }

}
