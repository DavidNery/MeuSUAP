package me.davidnery.meusuap.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.davidnery.meusuap.R;
import me.davidnery.meusuap.utils.NotaUtils;

/**
 * Created by david on 17/11/2018.
 */

public class FragmentSemestral extends Fragment {

    private Toast toastVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NotaUtils notaUtils = new NotaUtils(getContext());

        View view = inflater.inflate(R.layout.fragment_calcular_sem, container, false);
        Bundle bundle = getArguments();

        final EditText nota1 = view.findViewById(R.id.nota_sem_1);
        final EditText nota2 = view.findViewById(R.id.nota_sem_2);
        final Button btnCalcular = view.findViewById(R.id.btnCalcular_sem);
        final TextView notaNecessaria = view.findViewById(R.id.notanecessaria_sem);

        if (bundle != null) {
            if (bundle.containsKey("n1_sem"))
                nota1.setText(bundle.getInt("n1_sem") + "");
            if (bundle.containsKey("n2_sem"))
                nota2.setText(bundle.getInt("n2_sem") + "");
        }

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notaNecessaria.setVisibility(View.VISIBLE);

                if (nota1.getText().toString().equals("") && nota2.getText().toString().equals("")) {
                    notaNecessaria.setText("Você precisa tirar 60 no 1º e 2ª bimestre para passar!");
                    return;
                } else if (nota1.getText().toString().equals("")) {
                    toastVisible = Toast.makeText(getContext(),
                            "Informe a primeira nota!", Toast.LENGTH_SHORT);
                    toastVisible.show();
                    return;
                } else if (Integer.parseInt(nota1.getText().toString()) < 0 || Integer.parseInt(nota1.getText().toString()) > 100) {
                    toastVisible = Toast.makeText(getContext(),
                            "Informe apenas notas entre 0 e 100!", Toast.LENGTH_SHORT);
                    toastVisible.show();
                    return;
                }

                if (nota2.getText().toString().equals("")) {
                    double media = notaUtils.realMedia((Integer.parseInt(nota1.getText().toString()) * 2) / 5);
                    double nota = notaUtils.calcularNecessarioSemestral(Integer.parseInt(nota1.getText().toString()), -1);

                    if (nota > 100)
                        notaNecessaria.setText("Sua média é " + media + "!"
                                + "\nVocê precisa tirar " + nota + " no 2º bimestre para poder passar!"
                                + "\nPortanto, você está na recuperação!");
                    else
                        notaNecessaria.setText("Sua média é " + media + "!"
                                + "\nVocê precisa tirar " + nota + " no 2º bimestre para poder passar!");
                } else {
                    if (!nota2.getText().toString().equals("")
                            && (Integer.parseInt(nota1.getText().toString()) < 0 || Integer.parseInt(nota1.getText().toString()) > 100)) {
                        toastVisible = Toast.makeText(getContext(),
                                "Informe apenas notas entre 0 e 100!", Toast.LENGTH_SHORT);
                        toastVisible.show();
                        return;
                    }

                    double n1 = Integer.parseInt(nota1.getText().toString()),
                            n2 = Integer.parseInt(nota2.getText().toString());

                    double media = notaUtils.realMedia((n1 * 2 + n2 * 3) / 5.0);

                    if (media < 60 && media >= 20) {
                        notaNecessaria.setText("Sua média é " + media + "!"
                                + "\nVocê está na recuperação!");

                        double nM1 = 150.0 - ((3 * n2) / 2.0);
                        double nM2 = 100.0 - ((2 * n1) / 3.0);
                        double ret = Math.min(nM1, nM2);

                        nM1 = ((n1 * 2) + (n2 * 3)) / 5.0;
                        nM2 = 120 - nM1;

                        notaNecessaria.setText(notaNecessaria.getText()
                                + "\nVocê precisa tirar " + notaUtils.realMedia(Math.min(ret, nM2)) + " na recuperação para poder ser aprovado!");
                    } else if (media >= 60) {
                        notaNecessaria.setText("Sua média é " + media + "!"
                                + "\nParabéns, você foi aprovado (a)!");
                    } else {
                        notaNecessaria.setText("Sua média é " + media + "!"
                                + "\nInfelizmente você foi reprovado!");
                    }

                }
            }
        });

        return view;
    }
}
