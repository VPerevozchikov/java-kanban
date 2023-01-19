import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import server.HttpTaskServer;
import server.KVServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();


        Task task = taskManager.createOrUpdateTask(null, "Task_Http", "Task_Description_Http",
                Status.NEW,
                90L,
                LocalDateTime.now());

        Task task2 = taskManager.createOrUpdateTask(null, "Task_Http_2", "Task_Description_Http_2",
                Status.NEW,
                90L,
                LocalDateTime.now().plusMinutes(100));
        Epic epic1 = taskManager.createOrUpdateEpic(null, "Построить дом", "Двухэтажный_деревянный", Status.NEW);

        SubTask subTask1 = taskManager.createOrUpdateSubTask(null, "Вырыть фундамент", "глубина 1.8 м",
                Status.DONE, 8000L,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0).plusMinutes(10),
                3);

        SubTask subTask2 = taskManager.createOrUpdateSubTask(null, "Навязать арматуру", "диаметр 10мм_шаг 200мм",
                Status.DONE, 10000L,
                LocalDateTime.of(2100, 1, 1, 0, 0, 0, 0).plusMinutes(400),
                3);

        taskManager.getTaskById(2);
        taskManager.getTaskById(4);


        taskServer.stop();


        TaskManager taskManagerDub = Managers.getDefault();
        HttpTaskServer taskServerDub = new HttpTaskServer(taskManagerDub);
        taskServerDub.start();
        taskManagerDub.loadAllTasksFromServer();

        taskManagerDub.getListAllTasks();

        Task task3 = taskManagerDub.createOrUpdateTask(null, "Task_Http_NEWWW", "Task_Description_Http_NEWWW",
                Status.NEW,
                90L,
                LocalDateTime.now());

        taskManagerDub.getListAllTasks();

    }
}


