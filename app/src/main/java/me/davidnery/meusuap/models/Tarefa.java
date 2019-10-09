package me.davidnery.meusuap.models;

/**
 * Created by david on 19/11/2018.
 */

public class Tarefa {

    private final String nome;
    private final String aluno;
    private final String alunoNome;
    private final String to;

    public Tarefa(String nome, String aluno, String alunoNome, String to) {
        this.nome = nome;
        this.aluno = aluno;
        this.alunoNome = alunoNome;
        this.to = to;
    }

    public String getNome() {
        return nome;
    }

    public String getAluno() {
        return aluno;
    }

    public String getTo() {
        return to;
    }

    public String getAlunoNome() {
        return alunoNome;
    }
}
