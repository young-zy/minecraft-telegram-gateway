package fi.felixbade.TelegramBotClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HTTPJsonClient {

    private static final Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));

    public static JsonObject get(String url) throws IOException {
        String response = rawGet(url);
        JsonParser parser = new JsonParser();
        return parser.parse(response).getAsJsonObject();
    }

    public static JsonObject post(String url, JsonObject data) throws IOException {
        Gson gson = new Gson();
        String body = gson.toJson(data);
        String response = rawPost(url, body);
        JsonParser parser = new JsonParser();
        return parser.parse(response).getAsJsonObject();
    }

    private static String rawGet(String url) throws IOException {
        URL url2;
        try {
            url2 = new URL(url);
        } catch (MalformedURLException e) {
            return "";
        }

        URLConnection connection = url2.openConnection(proxy);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }

    private static String rawPost(String url, String body) throws IOException {
        URL url2;
        try {
            url2 = new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }

        HttpURLConnection connection = (HttpURLConnection) url2.openConnection(proxy);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(body.length()));
        connection.setRequestProperty("Accept", "application/json");

        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.write(body);
        writer.close();
        out.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
}
