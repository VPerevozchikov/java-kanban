package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import user.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpUserServerTest {

    private HttpUserServer userServer;
    private Gson gson = Managers.getGson();
    private TaskManager taskManager;
    private UserManager userManager;

    private Task task;
    private User user;



    @BeforeEach
    void init() throws IOException {
        userManager = Managers.getUserDefault();
        taskManager = userManager.getTaskManager();

        userServer = new HttpUserServer(userManager);

        user = new User("Тест Тесто");
        userManager.add(user);

        userServer.start();
    }

    @AfterEach
    void tearDown() {
        userServer.stop();
    }

    @Test
    void getUsers() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/users");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type userType = new TypeToken<ArrayList<User>>() {}.getType();
        List<User> actual = gson.fromJson(response.body(), userType);

        assertNotNull(actual, "Пользователи на возвращаются.");
        assertEquals(1, actual.size(), "Неверное количество.");
        assertEquals(user, actual.get(0), "Пользователи не совпадают.");

    }

    @Test
    void getUserById() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/users/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type userType = new TypeToken<User>() {}.getType();
        User actual = gson.fromJson(response.body(), userType);

        assertNotNull(actual, "Пользователи на возвращаются.");
        assertEquals(user, actual, "Пользователи не совпадают.");
    }

    @Test
    void deleteUserById() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/users/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }



}
