package me.davidnery.meusuap.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.davidnery.meusuap.R;
import me.davidnery.meusuap.utils.NotaUtils;

/**
 * Created by david on 17/11/2018.
 */

public class FragmentAnual extends Fragment {

    private Toast toastVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NotaUtils notaUtils = new NotaUtils(getContext());

        View view = inflater.inflate(R.layout.fragment_calcular_anual, container, false);
        Bundle bundle = getArguments();

        final EditText nota1 = view.findViewById(R.id.nota_anual_1);
        final EditText nota2 = view.findViewById(R.id.nota_anual_2);
        final EditText nota3 = view.findViewById(R.id.nota_anual_3);
        final EditText nota4 = view.findViewById(R.id.nota_anual_4);
        final Button btnCalcular = view.findViewById(R.id.btnCalcular_anual);
        final TextView notaNecessaria = view.findViewById(R.id.notanecessaria_anual);
        final LinearLayout activity = view.findViewById(R.id.calcularNotaAnualActivity);

        if (bundle != null) {
            if (bundle.containsKey("n1_anual"))
                nota1.setText(bundle.getInt("n1_anual") + "");
            if (bundle.containsKey("n2_anual"))
                nota2.setText(bundle.getInt("n2_anual") + "");
            if (bundle.containsKey("n3"))
                nota3.setText(bundle.getInt("n3") + "");
            if (bundle.containsKey("n4"))
                nota4.setText(bundle.getInt("n4") + "");
        }

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nota1.getText().toString().equals("")
                        || nota2.getText().toString().equals("")) {
                    toastVisible = Toast.makeText(getContext(), "Informe ao menos as notas do 1º e 2º bimestres!", Toast.LENGTH_LONG);
                    toastVisible.show();
                    return;
                }

                for (int i = 0; i < activity.getChildCount(); i++) {
                    if (activity.getChildAt(i) instanceof EditText) {
                        EditText editText = ((EditText) activity.getChildAt(i));
                        if (!editText.getText().toString().equals("") &&
                                (Integer.parseInt(editText.getText().toString()) < 0 || Integer.parseInt(editText.getText().toString()) > 100)) {
                            toastVisible = Toast.makeText(getContext(),
                                    "Informe apenas notas entre 0 e 100!", Toast.LENGTH_SHORT);
                            toastVisible.show();
                            return;
                        }
                    }
                }

                notaNecessaria.setVisibility(View.VISIBLE);

                if (!nota3.getText().toString().equals("")) {
                    if (nota4.getText().toString().equals("")) {
                        // Calcular nota para passar no 4º bimestre
                        int n1 = Integer.parseInt(nota1.getText().toString()),
                                n2 = Integer.parseInt(nota2.getText().toString()),
                                n3 = Integer.parseInt(nota3.getText().toString());

                        double nota = notaUtils.calcularNecessarioAnual(n1, n2, n3, -1);
                        double media = notaUtils.realMedia((n1 * 2 + n2 * 2 + n3 * 3) / 10.0);

                        if (nota > 100) {
                            notaNecessaria.setText("Sua média é " + media + "!"
                                    + "\nVocê precisa tirar " + nota + " para passar, portanto, está na recuperação!");
                        } else {
                            if (media < 60)
                                notaNecessaria.setText("Sua média é " + media + "!"
                                        + "\nVocê precisa tirar " + nota + " para passar no 4º bimestre!");
                            else
                                notaNecessaria.setText("Sua média é " + media + "!"
                                        + "\nParabéns, você foi aprovado (a)!");
                        }
                    } else {
                        int n1 = Integer.parseInt(nota1.getText().toString()),
                                n2 = Integer.parseInt(nota2.getText().toString()),
                                n3 = Integer.parseInt(nota3.getText().toString()),
                                n4 = Integer.parseInt(nota4.getText().toString());

                        double media = notaUtils.realMedia((n1 * 2 + n2 * 2 + n3 * 3 + n4 * 3) / 10.0);

                        if (media < 60) {
                            notaNecessaria.setText("Sua média é " + media + "!"
                                    + "\nVocê está na recuperação!");

                            if (media >= 20) {
                                double nM1 = 300.0 - n2 - ((3 * n3) / 2.0) - ((3 * n4) / 2.0);
                                double nM2 = 300.0 - n1 - ((3 * n3) / 2.0) - ((3 * n4) / 2.0);
                                double nM3 = 200.0 - n4 - ((2 * n1) / 3.0) - ((2 * n2) / 3.0);
                                double nM4 = 200.0 - n3 - ((2 * n1) / 3.0) - ((2 * n2) / 3.0);
                                double ret = Math.min(Math.min(nM1, nM2), Math.min(nM3, nM4));

                                nM1 = ((n1 * 2) + (n2 * 2) + (n3 * 3) + (n4 * 3)) / 10.0;
                                nM2 = 120 - nM1;

                                notaNecessaria.setText(notaNecessaria.getText()
                                        + "\nVocê precisa tirar " + notaUtils.realMedia(Math.min(ret, nM2)) + " na recuperação para poder ser aprovado!");
                            } else {
                                notaNecessaria.setText("Sua média é " + media + "!"
                                        + "\nInfelizmente você foi reprovado!");
                            }

                        } else {
                            notaNecessaria.setText("Sua média é " + media + "!"
                                    + "\nParabéns, você foi aprovado (a)!");
                        }
                    }
                } else {
                    int n1 = Integer.parseInt(nota1.getText().toString()),
                            n2 = Integer.parseInt(nota2.getText().toString());

                    // Calcular nota para passar com as notas do 3º e 4º bimestre
                    double p1 = notaUtils.calcularNecessarioAnual(n1, n2, -1, -1);
                    // Calcular no para passar no 3º bimestre
                    double p2 = notaUtils.calcularNecessarioAnual(n1, n2, -2, -1);

                    double media = notaUtils.realMedia((n1 * 2 + n2 * 2) / 10);

                    notaNecessaria.setText("Sua média é " + media + "!"
                            + "\nVocê precisa tirar " + p1 + " no 3º e 4º bimestre para poder passar!");

                    if (p2 <= 100)
                        notaNecessaria.setText(notaNecessaria.getText().toString()
                                + "\nCaso seja esforçado, se você tirar " + p2 + " no 3º bimestre, você passa!");

                }
            }
        });

        return view;
    }
}
