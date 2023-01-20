package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class KVTaskClient {
    public String apiToken;
    public HttpClient client;
    String url;

    public KVTaskClient(String url) {
        client = HttpClient.newHttpClient();
        this.url = url;
        apiToken = register();
    }

    public String register() {
        URI uri = URI.create(url + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        String apiTokenFromKVServer = null;

        try {
            final HttpResponse<String> response = client.send(request, handler);
            apiTokenFromKVServer = response.body();

            if (response.statusCode() == 200) {
                System.out.println("apiToken " + apiTokenFromKVServer + " присвоен клиенту");
            } else {
                System.out.println("Что-то пошло не так. KVServer вернул код состояния: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        return apiTokenFromKVServer;
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);

        if (key == null) {
            System.out.println("Значение key не должно быть null");
            return;
        } else if (json == null) {
            System.out.println("Значение value не должно быть null");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            final HttpResponse<String> response = client.send(request, handler);

            if (response.statusCode() == 200) {
                System.out.println("Данные записаны");
            } else {
                System.out.println("Что-то пошло не так. KVServer вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
    }

    public String load(String key) {

        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        String dataFromKVServer = null;

        try {
            final HttpResponse<String> response = client.send(request, handler);
            dataFromKVServer = response.body();

            if (response.statusCode() == 200) {
                System.out.println("Данные восстановлены с KVServer");
            } else {
                System.out.println("Что-то пошло не так. KVServer вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка");
        }
        return dataFromKVServer;
    }
}
