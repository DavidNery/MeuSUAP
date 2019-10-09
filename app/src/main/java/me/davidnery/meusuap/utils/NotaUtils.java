package me.davidnery.meusuap.utils;

import android.content.Context;

/**
 * Created by david on 16/10/2018.
 */

public class NotaUtils {

    private final ConfiguracoesUtils cu;

    public NotaUtils(Context context) {
        this.cu = new ConfiguracoesUtils(context);
    }

    /**
     * Calcula a nota necessáriar para pasar de ano
     *
     * @param nota1 Nota do 1º Bimestre
     * @param nota2 Nota do 2º Bimestre
     * @param nota3 Nota do 3º Bimestre (Informe <b>-2</b> para calcular o restante para passar no 3º bimestre)
     * @param nota4 Nota do 4º Bimestre
     * @return
     */
    public double calcularNecessarioAnual(double nota1, double nota2, double nota3, double nota4) {
        double nota;

        if (nota3 != -1 && nota4 != -1) {
            nota = 600.0 - (nota1 * cu.getAnualPeso1() + nota2 * cu.getAnualPeso2() + nota3 * cu.getAnualPeso3() + nota4 * cu.getAnualPeso4());
            // nota = (60-(nota1*2+nota2*2+nota3*3+nota4*3)/10)*10;
        } else if (nota3 == -1) {
            nota = (600.0 - (nota1 * cu.getAnualPeso1() + nota2 * cu.getAnualPeso2())) / (cu.getAnualPeso3() + cu.getAnualPeso4());
            // nota = ((60-(nota1*2+nota2*2)/10)/6)*10;
        } else if (nota3 == -2) {
            nota = (600.0 - (nota1 * cu.getAnualPeso1() + nota2 * cu.getAnualPeso2())) / ((cu.getAnualPeso3() + cu.getAnualPeso4()) / 2);
            // nota = ((60-(nota1*2+nota2*2)/10)/3)*10;
        } else {
            nota = (600.0 - (nota1 * cu.getAnualPeso1() + nota2 * cu.getAnualPeso2() + nota3 * cu.getAnualPeso3())) / cu.getAnualPeso4();
            //nota = ((60-(nota1*2+nota2*2+nota3*3)/10)/3)*10;
        }

        return realMedia(nota);
    }

    public double calcularNecessarioSemestral(double nota1, double nota2) {
        double nota;

        if (nota2 == -1)
            nota = (300.0 - (nota1 * cu.getSemPeso1())) / cu.getSemPeso2();
        else
            nota = (300.0 - nota1 * cu.getSemPeso1() + nota2 * cu.getSemPeso2());

        return realMedia(nota);
    }

    public double realMedia(double media) {
        //return new BigDecimal(media).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
        return Math.round(media);
    }

}
