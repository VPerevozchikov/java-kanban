package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import service.UserManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpUserServer {
    public static final int PORT = 8080;

    private HttpServer server;
    private Gson gson;

    private TaskManager taskManager;
    private UserManager userManager;

    public HttpUserServer() throws IOException {
        this(Managers.getUserDefault());
    }

    public HttpUserServer(UserManager userManager) throws IOException {

        this.userManager = userManager;
        this.taskManager = userManager.getTaskManager();

        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/users", this::handleUsers);
    }

    private void handleUsers(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/users$", path)) {
                        String response = gson.toJson(userManager.getAll());
                        sendText(httpExchange, response);
                        return;

                    }

                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/users/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(userManager.getById(id));
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный id " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/api/v1/users/\\d+/tasks$", path)) {
                        String pathId = path.replaceFirst("/api/v1/users/", "")
                                .replaceFirst("/tasks", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(userManager.getUserTask(id));
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный id " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }





                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/users/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.deleteTaskById(id);
                            System.out.println("Удалили задачу по id " + id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Получен некорректный id " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Ждем GET или DELETE запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
