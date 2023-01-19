package service;


import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTest {

    @Test
    void shouldSaveTasksOnServerAndLoadTasksFromServer() throws IOException {

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
        List<Task> listOfOriginalTasks = taskManager.getListAllTasks();
        Set<Task> historyOriginal = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(kvServer.data, "Задачи и история не сохраняются на сервере.");

        taskServer.stop();

//проверка факта восстановления

        TaskManager taskManagerDub = Managers.getDefault();
        HttpTaskServer taskServerDub = new HttpTaskServer(taskManagerDub);
        taskServerDub.start();
        taskManagerDub.loadAllTasksFromServer();
//        taskManagerDub.deleteTaskById(2);

        List<Task> listOfRecoveryTask = taskManagerDub.getListAllTasks();
//        taskManagerDub.deleteTaskById(2);
        Set<Task> historyRecovery = taskManagerDub.getAllTasksAndSubTasksSortedByStartTime();
        assertEquals(listOfOriginalTasks, listOfRecoveryTask, "Задачи не совпадают.");
        assertEquals(historyOriginal, historyRecovery, "История не совпадают.");
        taskServerDub.stop();
        kvServer.stop();
    }
}
