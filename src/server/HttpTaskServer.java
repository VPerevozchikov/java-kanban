package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    private final Gson gson;
    TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/tasks/task/$", path)) {
                        String response = gson.toJson(taskManager.getListAllTasks());
                        sendSuccessText(httpExchange, response);
                        break;
                    }

                    if (Pattern.matches("^/api/v1/tasks/task/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/tasks/task/", "");
                        int id = parsePathId(pathId);
                        List<Task> listOfAllTasks = taskManager.getListAllTasks();
                        boolean isIdContainInList = false;
                        for (Task task : listOfAllTasks) {
                            if (task.getId().equals(id)) {
                                isIdContainInList = true;
                                break;
                            }
                        }
                        if (isIdContainInList) {
                            String response = gson.toJson(taskManager.getTaskById(id));
                            System.out.println(taskManager.getTaskById(id));
                            sendSuccessText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный id " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/api/v1/tasks/subtask/epic/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/tasks/subtask/epic/", "");
                        int id = parsePathId(pathId);
                        List<Epic> listOfEpics = taskManager.getListOfEpics();
                        boolean isIdContainInList = false;
                        for (Epic epic : listOfEpics) {
                            if (epic.getId().equals(id)) {
                                isIdContainInList = true;
                                break;
                            }
                        }
                        if (isIdContainInList) {
                            String response = gson.toJson(taskManager.getMapSubTasksOfEpic(id));
                            sendSuccessText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный id " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/api/v1/tasks/history$", path)) {
                        String response = gson.toJson(taskManager.getHistory());
                        sendSuccessText(httpExchange, response);
                        break;
                    }

                    if (Pattern.matches("^/api/v1/tasks/$", path)) {
                        String response = gson.toJson(taskManager.getAllTasksAndSubTasksSortedByStartTime());
                        sendSuccessText(httpExchange, response);
                        break;
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/api/v1/tasks/task/$", path)) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        System.out.println();
                        Task taskFromRequest = gson.fromJson(body, Task.class);
                        Integer idTaskFromRequest = taskFromRequest.getId();

                        try {
                            if (idTaskFromRequest == null) {
                                int numberOfTasksBeforeAdd = taskManager.getListAllTasks().size();
                                Task newTask = createOrUpdateTaskFromWeb(taskFromRequest);
                                int numberOfTasksAfterAdd = taskManager.getListAllTasks().size();
                                if (numberOfTasksAfterAdd > numberOfTasksBeforeAdd) {
                                    sendSuccessText(httpExchange,
                                            "Задача Task с id " + newTask.getId() + " создана");
                                } else {
                                    sendFailText(httpExchange,
                                            "Задача не создана. Ошибка в введенных данных.");
                                }
                            } else {
                                List<Task> listOfTasks = taskManager.getListOfTasks();
                                boolean isIdContainInList = false;
                                for (Task task : listOfTasks) {
                                    if (task.getId().equals(idTaskFromRequest)) {
                                        isIdContainInList = true;
                                        break;
                                    }
                                }
                                if (isIdContainInList) {
                                    Task taskBeforeUpdate = taskManager.getTaskById(idTaskFromRequest);
                                    Task updateTask = createOrUpdateTaskFromWeb(taskFromRequest);
                                    if (updateTask != null) {
                                        if (!taskBeforeUpdate.getName().equals(updateTask.getName())
                                                || !taskBeforeUpdate
                                                .getDescription().equals(updateTask.getDescription())
                                                || !taskBeforeUpdate
                                                .getStatus().equals(updateTask.getStatus())
                                                || !taskBeforeUpdate
                                                .getDuration().equals(updateTask.getDuration())
                                                || !taskBeforeUpdate
                                                .getStartTime().isEqual(updateTask.getStartTime())) {
                                            sendSuccessText(httpExchange,
                                                    "Задача Task с id " + idTaskFromRequest + " обновлена");
                                        } else {
                                            sendFailText(httpExchange,
                                                    "Задача не обновлена. Ошибка в введенных данных.");
                                        }
                                    } else {
                                        sendFailText(httpExchange,
                                                "Задача не обновлена. Ошибка в введенных данных.");
                                    }
                                    break;
                                } else {
                                    sendFailText(httpExchange,
                                            "Задача не обновлена, введенный id "
                                                    + idTaskFromRequest + " не найден.");
                                }
                            }
                            break;
                        } catch (JsonSyntaxException exception) {
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }

                    if (Pattern.matches("^/api/v1/tasks/subtask/$", path)) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        SubTask subTaskFromRequest = gson.fromJson(body, SubTask.class);
                        Integer idSubTaskFromRequest = subTaskFromRequest.getId();

                        try {
                            if (idSubTaskFromRequest == null) {
                                int numberOfTasksBeforeAdd = taskManager.getListAllTasks().size();
                                SubTask newSubTask = createOrUpdateSubTaskFromWeb(subTaskFromRequest);
                                int numberOfTasksAfterAdd = taskManager.getListAllTasks().size();
                                if (numberOfTasksAfterAdd > numberOfTasksBeforeAdd) {
                                    sendSuccessText(httpExchange,
                                            "Задача SubTask с id " + newSubTask.getId() + " создана");
                                } else {
                                    sendFailText(httpExchange,
                                            "Задача не создана. Ошибка в введенных данных.");
                                }
                            } else {
                                List<SubTask> listOfSubTasks = taskManager.getListOfSubTask();
                                boolean isIdContainInList = false;

                                for (SubTask subTask : listOfSubTasks)
                                    if (subTask.getId().equals(idSubTaskFromRequest)) {
                                        isIdContainInList = true;
                                        break;
                                    }
                                if (isIdContainInList) {
                                    SubTask subTaskBeforeUpdate = (SubTask) taskManager
                                            .getTaskById(idSubTaskFromRequest);
                                    SubTask updateSubTask = createOrUpdateSubTaskFromWeb(subTaskFromRequest);
                                    if (updateSubTask != null) {
                                        if (!subTaskBeforeUpdate.getName().equals(updateSubTask.getName())
                                                || !subTaskBeforeUpdate
                                                .getDescription().equals(updateSubTask.getDescription())
                                                || !subTaskBeforeUpdate
                                                .getStatus().equals(updateSubTask.getStatus())
                                                || !subTaskBeforeUpdate
                                                .getDuration().equals(updateSubTask.getDuration())
                                                || !subTaskBeforeUpdate
                                                .getStartTime().isEqual(updateSubTask.getStartTime())) {
                                            sendSuccessText(httpExchange,
                                                    "Задача SubTask с id "
                                                            + idSubTaskFromRequest + " обновлена");
                                        } else {
                                            sendFailText(httpExchange,
                                                    "Задача не обновлена. Ошибка в введенных данных.");
                                        }
                                    } else {
                                        sendFailText(httpExchange,
                                                "Задача не обновлена. Ошибка в введенных данных.");
                                    }
                                    break;
                                } else {
                                    sendFailText(httpExchange,
                                            "Задача не обновлена, введенный id "
                                                    + idSubTaskFromRequest + " не найден.");
                                }
                            }
                            break;
                        } catch (JsonSyntaxException exception) {
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }

                    if (Pattern.matches("^/api/v1/tasks/epic/$", path)) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        Epic epicFromRequest = gson.fromJson(body, Epic.class);
                        Integer idEpicFromRequest = epicFromRequest.getId();

                        try {
                            if (idEpicFromRequest == null) {
                                int numberOfTasksBeforeAdd = taskManager.getListAllTasks().size();
                                Epic newEpic = createOrUpdateEpicFromWeb(epicFromRequest);
                                int numberOfTasksAfterAdd = taskManager.getListAllTasks().size();
                                if (numberOfTasksAfterAdd > numberOfTasksBeforeAdd) {
                                    sendSuccessText(httpExchange,
                                            "Задача Epic с id " + newEpic.getId() + " создана");
                                } else {
                                    sendFailText(httpExchange,
                                            "Задача не создана. Ошибка в введенных данных.");
                                }
                            } else {
                                List<Epic> listOfAllEpics = taskManager.getListOfEpics();
                                boolean isIdContainInList = false;
                                for (Epic epic : listOfAllEpics) {
                                    if (epic.getId().equals(idEpicFromRequest)) {
                                        isIdContainInList = true;
                                        break;
                                    }
                                }
                                if (isIdContainInList) {
                                    Epic epicBeforeUpdate = (Epic) taskManager
                                            .getTaskById(idEpicFromRequest);
                                    Epic updateEpic = createOrUpdateEpicFromWeb(epicFromRequest);
                                    if (updateEpic != null) {
                                        if (!epicBeforeUpdate.getName().equals(updateEpic.getName())
                                                || !epicBeforeUpdate
                                                .getDescription().equals(updateEpic.getDescription())
                                                || !epicBeforeUpdate
                                                .getStatus().equals(updateEpic.getStatus())) {
                                            sendSuccessText(httpExchange,
                                                    "Задача Epic с id " + idEpicFromRequest + " обновлена");
                                        } else {
                                            sendFailText(httpExchange,
                                                    "Задача не обновлена. Ошибка в введенных данных.");
                                        }
                                    } else {
                                        sendFailText(httpExchange,
                                                "Задача не обновлена. Ошибка в введенных данных.");
                                    }
                                    break;
                                } else {
                                    sendFailText(httpExchange,
                                            "Задача не обновлена, введенный id "
                                                    + idEpicFromRequest + " не найден.");
                                }
                            }
                            break;
                        } catch (JsonSyntaxException exception) {
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/api/v1/tasks/task/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/tasks/task/", "");
                        int id = parsePathId(pathId);
                        List<Task> listOfAllTasks = taskManager.getListAllTasks();
                        boolean isIdContainInList = false;
                        for (Task task : listOfAllTasks) {
                            if (task.getId().equals(id)) {
                                isIdContainInList = true;
                                break;
                            }
                        }

                        if (isIdContainInList) {
                            taskManager.deleteTaskById(id);
                            System.out.println("Удалили задачу по id " + id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Получен некорректный id " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/api/v1/tasks/task/$", path)) {
                        taskManager.deleteAllTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }
                    break;
                }
                default: {
                    System.out.println("Ждем GET или POST или DELETE запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            sendFailText(httpExchange, "Ошибка в запросе.");
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
        System.out.println("Запускаем HttpTaskServer сервер на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendSuccessText(HttpExchange h, String text) throws IOException {
        h.sendResponseHeaders(200, 0);
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.getResponseBody().write(resp);
    }

    protected void sendFailText(HttpExchange h, String text) throws IOException {
        h.sendResponseHeaders(400, 0);
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.getResponseBody().write(resp);
    }

    public Task createOrUpdateTaskFromWeb(Task task) throws IOException {
        Task newTask = taskManager.createOrUpdateTask(task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getDuration(),
                task.getStartTime());
        return newTask;
    }

    public SubTask createOrUpdateSubTaskFromWeb(SubTask subTask) throws IOException {
        SubTask newSubTask = taskManager.createOrUpdateSubTask(subTask.getId(),
                subTask.getName(),
                subTask.getDescription(),
                subTask.getStatus(),
                subTask.getDuration(),
                subTask.getStartTime(),
                subTask.getIdOfEpic());
        return newSubTask;
    }

    public Epic createOrUpdateEpicFromWeb(Epic epic) throws IOException {
        Epic newEpic = taskManager.createOrUpdateEpic(epic.getId(),
                epic.getName(),
                epic.getDescription(),
                epic.getStatus());
        return newEpic;
    }
}