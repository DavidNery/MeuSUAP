package me.davidnery.meusuap.utils;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.davidnery.meusuap.AlunosActivity;
import me.davidnery.meusuap.auth.AuthCheck;

/**
 * Created by david on 21/10/2018.
 */

public class ImportDisciplinasTask extends AsyncTask<AlunosActivity, Integer, JSONArray> {

    private AlunosActivity alunosActivity;

    private ProgressBar importProgress;

    private String token;
    private JSONObject info;
    private JSONObject json;

    @Override
    protected JSONArray doInBackground(AlunosActivity... disciplinasSalvasActivities) {
        alunosActivity = disciplinasSalvasActivities[0];

        AuthCheck authCheck = new AuthCheck();

        JSONArray diarios = null;

        try {
            publishProgress(0);
            json = authCheck.getToken(alunosActivity.getMatricula(), alunosActivity.getSenha());

            if (json.has("responseCode")) {
                try {
                    if (json.getInt("responseCode") == 401)
                        diarios = new JSONArray("[{\"detail\":\"Matricula/senha incorreta!\"}]");
                    else
                        diarios = new JSONArray("[{\"detail\":\"SUAP em manutenção!\"}]");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return diarios;
            }
        } catch (final Exception e) {
            try {
                diarios = new JSONArray("[{\"detail\": \"Ocorreu um erro ao tentar solicitar seu token!\"}]");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return diarios;
        }

        try {
            if (json.has("detail")) {
                try {
                    diarios = new JSONArray("[{\"detail\": \"" + json.getString("detail") + "\"}]");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return diarios;
            } else {
                token = json.getString("token");

                publishProgress(25);

                info = authCheck.getInformacao(token);

                if (info.has("responseCode") && info.getInt("responseCode") == 502) {
                    try {
                        diarios = new JSONArray("[{\"detail\": \"SUAP em manutenção!\"}]");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    return diarios;
                } else {
                    publishProgress(50);

                    diarios = authCheck.getDiarios(token);

                    if (diarios.get(0) instanceof JSONObject && diarios.getJSONObject(0).has("responseCode")) {
                        try {
                            diarios = new JSONArray("[{\"detail\": \"Não foi possível solicitar seus diários!\"}]");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        return diarios;
                    } else {
                        publishProgress(75);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                diarios = new JSONArray("[{\"detail\": \"Ocorreu um erro ao tentar baixar suas matérias!\"}]");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return diarios;
        }

        alunosActivity.dismissLogin();
        return diarios;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        alunosActivity.setImportProgress(values[0]);
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        alunosActivity.saveDisciplinas(jsonArray, info, token);
    }
}
