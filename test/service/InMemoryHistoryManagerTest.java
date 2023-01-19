package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddTaskInHistory() throws IOException {
        System.out.println("\n\n***** shouldAddTaskInHistory *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        historyManager.add(task);

        List<Task> tasksFromHistory = historyManager.getHistory();
        Task taskFromHistory = tasksFromHistory.get(0);

        assertNotNull(taskFromHistory, "Задача не найдена.");
        assertEquals(task, taskFromHistory, "Задачи не совпадают.");

        assertNotNull(tasksFromHistory, "Задачи на возвращаются.");
        assertEquals(1, tasksFromHistory.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldPrintListWhenGetHistoryWithPrint() throws IOException {
        System.out.println("\n\n***** shouldPrintListWhenGetHistoryWithPrint *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        List<Task> tasksFromHistory = historyManager.getHistory();
        Task taskFromHistory = tasksFromHistory.get(0);
        Task epicFromHistory = tasksFromHistory.get(1);
        Task subTaskFromHistory = tasksFromHistory.get(2);


        assertNotNull(taskFromHistory, "Задача не найдена.");
        assertEquals(task, taskFromHistory, "Задачи не совпадают.");
        assertEquals(epic, epicFromHistory, "Задачи не совпадают.");
        assertEquals(subTask, subTaskFromHistory, "Задачи не совпадают.");

        assertNotNull(tasksFromHistory, "Задачи на возвращаются.");
        assertEquals(3, tasksFromHistory.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldNotPutDoubleTaskInHistory() throws IOException {
        System.out.println("\n\n***** shouldNotPutDoubleTaskInHistory *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        List<Task> tasksFromHistory = historyManager.getHistory();
        Task taskFromHistory = tasksFromHistory.get(0);
        Task epicFromHistory = tasksFromHistory.get(1);
        Task subTaskFromHistory = tasksFromHistory.get(2);


        assertEquals(task, taskFromHistory, "Задачи не совпадают.");
        assertEquals(epic, epicFromHistory, "Задачи не совпадают.");
        assertEquals(subTask, subTaskFromHistory, "Задачи не совпадают.");

        assertNotNull(tasksFromHistory, "Задачи на возвращаются.");
        assertEquals(3, tasksFromHistory.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldDeleteTaskFromHistoryWhenDeleteTaskById() throws IOException {
        System.out.println("\n\n***** shouldDeleteTaskFromHistoryWhenDeleteTaskById *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        historyManager.remove(1);

        List<Task> tasksFromHistory = historyManager.getHistory();

        Task epicFromHistory = tasksFromHistory.get(0);
        Task subTaskFromHistory = tasksFromHistory.get(1);

        assertEquals(epic, epicFromHistory, "Задачи не совпадают.");
        assertEquals(subTask, subTaskFromHistory, "Задачи не совпадают.");

        assertNotNull(tasksFromHistory, "Задачи на возвращаются.");
        assertEquals(2, tasksFromHistory.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

}
