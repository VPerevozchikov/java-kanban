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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    private HttpTaskServer taskServer;

    private Gson gson = Managers.getGson();
    private TaskManager taskManager;

    private Task task;
    KVServer kvServer = new KVServer();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    void init() throws IOException {
        kvServer.start();
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {

        Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SubTask_Http", "SubTask_Description_Http",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {

        Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/task/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNotNull(taskManager.getTaskById(1));
        assertEquals(task, taskManager.getTaskById(1), "Задачи не совпадают");
    }

    @Test
    void shouldCreateOrUpdateTask() throws IOException, InterruptedException {

         Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

         Task updateTask = new Task(task.getId(), "Task_Http_Update", "Task_Description_Http_Update",
                 task.getStatus(),
                 task.getDuration(),
                 task.getStartTime());

        String jsonString = gson.toJson(updateTask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNotNull(taskManager.getTaskById(1));
        assertEquals(updateTask, taskManager.getTaskById(1), "Задачи не совпадают");
    }

    @Test
    void shouldCreateOrUpdateEpic() throws IOException, InterruptedException {

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http",
                Status.NEW);

        Epic updateEpic = new Epic(epic.getId(), "Epic_Http_Update", "Epic_Description_Http_Update",
                Status.NEW);

        String jsonString = gson.toJson(updateEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNotNull(taskManager.getTaskById(1));
        assertEquals(updateEpic, taskManager.getTaskById(1), "Задачи не совпадают");
    }

    @Test
    void shouldCreateOrUpdateSubTask() throws IOException, InterruptedException {

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SubTask_Http", "SubTask_Description_Http",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                1);

        SubTask updateSubTask = new SubTask(subTask.getId(),"SubTask_Http_Update", "SubTask_Description_Http_Update",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                1);

        String jsonString = gson.toJson(updateSubTask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNotNull(taskManager.getTaskById(2));
        assertEquals(updateSubTask, taskManager.getTaskById(2), "Задачи не совпадают");
    }

    @Test
    void shouldGetSubtasksOfEpicById() throws IOException, InterruptedException {

        Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SubTask_Http", "SubTask_Description_Http",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                2);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/subtask/epic/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNotNull(taskManager.getTaskById(2));
        assertEquals(epic, taskManager.getTaskById(2), "Задачи не совпадают");
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {

        Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SubTask_Http", "SubTask_Description_Http",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                2);

        taskManager.getTaskById(3);
        taskManager.getTaskById(1);

        List<Task> listOfHistory = new ArrayList<>();
        listOfHistory.add(subTask);
        listOfHistory.add(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNotNull(listOfHistory);
        assertEquals(listOfHistory, taskManager.getHistory(), "История не совпадает");
    }

    @Test
    void shouldGetPrioritizedTasks() throws IOException, InterruptedException {

        Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SubTask_Http", "SubTask_Description_Http",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                2);

        taskManager.getTaskById(3);
        taskManager.getTaskById(1);

        Set<Task> setOfPrioritizedTasksDub = new HashSet<>();
        setOfPrioritizedTasksDub.add(subTask);
        setOfPrioritizedTasksDub.add(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        taskManager.getAllTasksAndSubTasksSortedByStartTime();


        assertNotNull(taskManager.getAllTasksAndSubTasksSortedByStartTime());
        assertEquals(taskManager.getAllTasksAndSubTasksSortedByStartTime(), setOfPrioritizedTasksDub, "Списки задач по приоритету не совпадают");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {

        Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SubTask_Http", "SubTask_Description_Http",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/task/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void shouldDeleteAllTasks() throws IOException, InterruptedException {

        Task task = taskManager.createOrUpdateTask(null,"Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Epic epic = taskManager.createOrUpdateEpic(null,"Epic_Http", "Epic_Description_Http", Status.NEW);

        SubTask subTask = taskManager.createOrUpdateSubTask(null,"SubTask_Http", "SubTask_Description_Http",
                Status.DONE, 8000L,
                LocalDateTime.of(2015, 1, 1, 0, 0, 0, 0),
                2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> tasks = taskManager.getListAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }
}
