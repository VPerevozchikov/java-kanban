package server;

import com.google.gson.Gson;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    private HttpTaskServer taskServer;

    private Gson gson = Managers.getGson();
    private TaskManager taskManager;

    private Task task;

    @BeforeEach
    void init() throws IOException {
        taskManager = Managers.getFileBackedTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();

        Task task = taskManager.createOrUpdateTask(null,"TASK_HTTP", "TASK_DESCRIPTION_HTTP",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Epic epic = taskManager.createOrUpdateEpic(null,"EPIC_HTTP", "EPIC_DESCRIPTION_HTTP", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SUBTASK_HTTP", "SUBTASK_DESCRIPTION_HTTP",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                2);
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
    }


    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/task/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }
}
