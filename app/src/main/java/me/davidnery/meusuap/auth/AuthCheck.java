package me.davidnery.meusuap.auth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AuthCheck {

    public JSONObject getToken(String usuario, String senha) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://suap.ifrn.edu.br/api/v2/autenticacao/token/").openConnection();

        String content = new JSONObject().put("username", usuario).put("password", senha).toString();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        connection.setConnectTimeout(5000);

        connection.setDoOutput(true);

        OutputStream write = connection.getOutputStream();
        write.write(content.getBytes());
        write.flush();
        write.close();

        // 401 = login/senha incorreto
        // 502 = suap em manutenção
        if (connection.getResponseCode() != 200)
            return new JSONObject().put("responseCode", connection.getResponseCode());

        Scanner in = new Scanner(connection.getInputStream());

        String result = "";
        while (in.hasNext())
            result += in.nextLine();

        in.close();

        return new JSONObject(result);
    }

    public JSONArray getDiarios(String token) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://suap.ifrn.edu.br/api/v2/minhas-informacoes/boletim/2018/1").openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        connection.setRequestProperty("Authorization", "JWT " + token);
        connection.setConnectTimeout(5000);

        if (connection.getResponseCode() != 200)
            return new JSONArray().put(new JSONObject().put("responseCode", connection.getResponseCode()));

        Scanner in = new Scanner(connection.getInputStream());

        String result = "";

        while (in.hasNext())
            result += in.nextLine();

        in.close();

        return new JSONArray(result);
    }

    public JSONObject getInformacao(String token) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://suap.ifrn.edu.br/api/v2/minhas-informacoes/meus-dados/").openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        connection.setRequestProperty("Authorization", "JWT " + token);
        connection.setConnectTimeout(5000);

        if (connection.getResponseCode() != 200)
            return new JSONObject().put("responseCode", connection.getResponseCode());

        Scanner in = new Scanner(connection.getInputStream());

        String result = "";

        while (in.hasNext())
            result += in.nextLine();

        in.close();

        return new JSONObject(result);
    }

}
