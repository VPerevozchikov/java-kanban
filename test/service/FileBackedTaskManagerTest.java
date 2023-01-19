package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager("resources/ListAndHistoryOfTasksForTest.txt");
    }

    @Test
    void shouldSaveTasksInTxtAndLoadTasksFromTxt() {
        System.out.println("\n\n***** shouldSaveTasksInTxtAndLoadTasksFromTxt *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);

        FileBackedTaskManager doubleFBTM = new FileBackedTaskManager("resources/ListAndHistoryOfTasksForTest.txt");
        doubleFBTM.load("resources/ListAndHistoryOfTasksForTest.txt");
        List<Task> tasksLoadFromTxt = doubleFBTM.getListAllTasks();

        assertNotNull(tasksLoadFromTxt, "Задачи на возвращаются.");
        assertEquals(3, tasksLoadFromTxt.size(), "Неверное количество задач.");
        assertEquals(task, tasksLoadFromTxt.get(0), "Задачи не совпадают.");
        assertEquals(epic, tasksLoadFromTxt.get(1), "Задачи не совпадают.");
        assertEquals(subTask, tasksLoadFromTxt.get(2), "Задачи не совпадают.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldThrowExceptionWhenSaveTasksInMistakeFile() {
        System.out.println("\n\n***** shouldThrowExceptionWhenSaveTasksInMistakeFile *****");
        FileBackedTaskManager doubleFBTM = new FileBackedTaskManager("MISTAKE/ListAndHistoryOfTasksForTest_MISTAKE.txt");

        ManagerSaveException ex = Assertions.assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        Task task = doubleFBTM.createOrUpdateTask(null, "Test TASK",
                                "Test TASK description", NEW, 50L,
                                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
                    }
                }
        );
        Assertions.assertEquals("Ошибка при попытки сохранить данные в файл", ex.getMessage());

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldThrowExceptionWhenLoadTasksFromMistakeFile() {
        System.out.println("\n\n***** shouldThrowExceptionWhenLoadTasksFromMistakeFile *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        taskManager.getTaskById(1);

        FileBackedTaskManager doubleFBTM = new FileBackedTaskManager("resources/ListAndHistoryOfTasksForTest.txt");

        ManagerSaveException ex = Assertions.assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        doubleFBTM.load("MISTAKE/ListAndHistoryOfTasksForTest_MISTAKE.txt");
                    }
                }
        );
        Assertions.assertEquals("Ошибка при попытке считать данные из файла", ex.getMessage());

        System.out.println("-------------------------------------------------\n\n");
    }
}

