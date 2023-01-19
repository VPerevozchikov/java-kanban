package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;

    @Test
    void shouldCreateAndPutTaskInDataTask() throws IOException {
        System.out.println("\n\n***** shouldCreateAndPutTaskInDataTask *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldCreateAndPutEpicInDataEpic() throws IOException {
        System.out.println("\n\n***** shouldCreateAndPutEpicInDataEpic *****");
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);

        Task savedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.get(0), "Задачи не совпадают.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldCreateAndPutSubTaskInDataSubTask() throws IOException {
        System.out.println("\n\n***** shouldCreateAndPutSubTaskInDataSubTask *****");
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);

        Task savedSubTask = taskManager.getTaskById(subTask.getId());

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.get(0), "Задачи не совпадают.");
        assertEquals(savedSubTask, tasks.get(1), "Задачи не совпадают.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldUpdateStatusAndPutTaskInDataTask() throws IOException {
        System.out.println("\n\n***** shouldUpdateStatusAndPutTaskInDataTask *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        Task updateTask = taskManager.createOrUpdateTask(1, "Test UPDATE_TASK",
                "Test UPDATE_TASK description", IN_PROGRESS, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(updateTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updateTask, tasks.get(0), "Задачи не совпадают.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldReturnNullAndPrintMessageWhenUpdateStatusEpic() throws IOException {
        System.out.println("\n\n***** shouldReturnNullAndPrintMessageWhenUpdateStatusEpic *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);

        Epic updateEpic = taskManager.createOrUpdateEpic(1, "Test EPIC",
                "Test EPIC description", IN_PROGRESS);

        Task savedTask = taskManager.getTaskById(epic.getId());

        assertNull(updateEpic, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldUpdateStatusAndPutSubTaskInDataTask() throws IOException {
        System.out.println("\n\n***** shouldUpdateStatusAndPutSubTaskInDataTask *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);

        SubTask updateSubTask = taskManager.createOrUpdateSubTask(2, "Test SUBTASK",
                "Test SUBTASK description", DONE, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);

        Task savedSubTask = taskManager.getTaskById(subTask.getId());

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(savedSubTask.getStatus(), DONE, "Статус задач не совпадает.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldCheckStatusEpicWhenStatusSubTasksNew() throws IOException {
        System.out.println("\n\n***** shouldCheckStatusEpicWhenStatusSubTasksNew *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", DONE, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);

        SubTask updateSubTask = taskManager.createOrUpdateSubTask(2, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);

        Task savedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(savedEpic.getStatus(), NEW, "Статус задач не совпадает.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldCheckStatusEpicWhenStatusSubTasksInProgress() throws IOException {
        System.out.println("\n\n***** shouldCheckStatusEpicWhenStatusSubTasksInProgress *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", DONE, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        SubTask subTask2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK_2",
                "Test SUBTASK_2 description", DONE, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);
        SubTask updateSubTask2 = taskManager.createOrUpdateSubTask(3, "Test SUBTASK_2",
                "Test SUBTASK_2 description", IN_PROGRESS, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);

        Task savedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(savedEpic.getStatus(), IN_PROGRESS, "Статус задач не совпадает.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldCheckStatusEpicWhenStatusSubTasksDone() throws IOException {
        System.out.println("\n\n***** shouldCheckStatusEpicWhenStatusSubTasksDone *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", DONE, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        SubTask subTask2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK_2",
                "Test SUBTASK_2 description", NEW, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);
        SubTask updateSubTask = taskManager.createOrUpdateSubTask(3, "Test SUBTASK_2",
                "Test SUBTASK_2 description", DONE, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);

        Task savedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(savedEpic.getStatus(), DONE, "Статус задач не совпадает.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldCheckStatusEpicWhenStatusSubTasksNewAndDone() throws IOException {
        System.out.println("\n\n***** shouldCheckStatusEpicWhenStatusSubTasksNewAndDone *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        SubTask subTask2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK_2",
                "Test SUBTASK_2 description", NEW, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);

        SubTask updateSubTask = taskManager.createOrUpdateSubTask(3, "Test SUBTASK_2",
                "Test SUBTASK_2 description", DONE, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);

        Task savedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(savedEpic.getStatus(), NEW, "Статус задач не совпадает.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldReturnEmptyArrayListWhenNumbersOfTasksZero() {
        System.out.println("\n\n***** shouldReturnEmptyArrayListWhenNumbersOfTasksZero *****");

        List<Task> tasks = taskManager.getListAllTasks();

        assertTrue(tasks.isEmpty());

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldReturnArrayListWithAllTasks() throws IOException {
        System.out.println("\n\n***** shouldReturnArrayListWithAllTasks *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                2);

        List<Task> tasks = taskManager.getListAllTasks();

        assertEquals(3, tasks.size(), "Неверное количество задач.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldNotErrorWhenDeleteAllTasksWhenNumberOfTasksZero() {
        System.out.println("\n\n***** shouldNotErrorWhenDeleteAllTasksWhenNumberOfTasksZero *****");

        taskManager.deleteAllTasks();

        List<Task> tasks = taskManager.getListAllTasks();

        assertTrue(tasks.isEmpty());

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldDeleteAllTasks() throws IOException {
        System.out.println("\n\n***** shouldDeleteAllTasks *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                2);

        taskManager.deleteAllTasks();

        List<Task> tasks = taskManager.getListAllTasks();
        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertTrue(tasks.isEmpty());
        assertTrue(tasksSortedByTime.isEmpty());

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldReturnNullWhenGetMistakeId() throws IOException {
        System.out.println("\n\n***** shouldReturnNullWhenGetMistakeId *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Task getTask = taskManager.getTaskById(5);

        assertNull(getTask);

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldReturnTaskById() throws IOException {
        System.out.println("\n\n***** shouldReturnTaskById *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Task getTask = taskManager.getTaskById(1);

        assertNotNull(getTask, "Задачи на возвращаются.");
        assertEquals(getTask, task, "Задачи не совпадают.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldReturnEpicById() throws IOException {
        System.out.println("\n\n***** shouldReturnEpicById *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        Task getTask = taskManager.getTaskById(1);

        assertNotNull(getTask, "Задачи на возвращаются.");
        assertEquals(getTask, epic, "Задачи не совпадают.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldReturnSubTaskById() throws IOException {
        System.out.println("\n\n***** shouldReturnSubTaskById *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        Task getTask = taskManager.getTaskById(2);

        assertNotNull(getTask, "Задачи на возвращаются.");
        assertEquals(getTask, subTask, "Задачи не совпадают.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldNotErrorWhenDeleteTaskByMistakeId() throws IOException {
        System.out.println("\n\n***** shouldNotErrorWhenDeleteTaskByMistakeId *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        taskManager.deleteTaskById(5);


        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldDeleteTaskById() throws IOException {
        System.out.println("\n\n***** shouldDeleteTaskById *****");

        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        taskManager.deleteTaskById(1);

        Task getTask = taskManager.getTaskById(1);
        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNull(getTask);
        assertFalse(tasksSortedByTime.contains(task));

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldDeleteEpicById() throws IOException {
        System.out.println("\n\n***** shouldDeleteTaskById *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);


        taskManager.deleteTaskById(1);

        Task getTask = taskManager.getTaskById(1);
        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNull(getTask);
        assertFalse(tasksSortedByTime.contains(subTask));

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldDeleteSubTaskById() throws IOException {
        System.out.println("\n\n***** shouldDeleteTaskById *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);

        taskManager.deleteTaskById(2);

        Task getTask = taskManager.getTaskById(2);
        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNull(getTask);
        assertFalse(tasksSortedByTime.contains(subTask));

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void updateStatusEpicWhenDeleteSubTaskById() throws IOException {
        System.out.println("\n\n***** updateStatusEpicWhenDeleteSubTaskById *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", IN_PROGRESS, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        SubTask subTask2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK2",
                "Test SUBTASK2 description", DONE, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);

        taskManager.deleteTaskById(2);

        Task savedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(savedEpic.getStatus(), DONE, "Статус задач не совпадает.");

        final List<Task> tasks = taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldPrintEmptyListOfSubTasksOfEpicWhenNumberOfSubTasksZero() throws IOException {
        System.out.println("\n\n***** shouldPrintEmptyListOfSubTasksOfEpicWhenNumberOfSubTasksZero *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);

        taskManager.getMapSubTasksOfEpic(1);

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldPrintListOfSubTasksOfEpic() throws IOException {
        System.out.println("\n\n***** shouldPrintListOfSubTasksOfEpic *****");

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", IN_PROGRESS, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        SubTask subTask2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK2",
                "Test SUBTASK2 description", DONE, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                1);

        taskManager.getMapSubTasksOfEpic(1);

        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldGenerateEndTimeAndPutTaskInTreeSet() throws IOException {
        System.out.println("\n\n***** shouldGenerateEndTimeAndPutTaskInTreeSet *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        Task savedTask = taskManager.getTaskById(task.getId());
        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(tasksSortedByTime, "Задачи на возвращаются.");
        assertEquals(1, tasksSortedByTime.size(), "Неверное количество задач.");
        assertEquals(savedTask.getStartTime(), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), "Задачи не совпадают.");
        assertEquals(savedTask.getEndTime(), LocalDateTime.of(2020, 1, 1, 0, 50, 0, 0), "Задачи не совпадают.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldGenerateEndTimeAndPutSubTaskInTreeSetAndUpdateEpicTime() throws IOException {
        System.out.println("\n\n***** shouldGenerateEndTimeAndPutSubTaskInTreeSetAndUpdateEpicTime *****");
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", IN_PROGRESS, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);


        Task savedEpic = taskManager.getTaskById(epic.getId());
        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(tasksSortedByTime, "Задачи на возвращаются.");
        assertEquals(1, tasksSortedByTime.size(), "Неверное количество задач.");
        assertEquals(savedEpic.getStartTime(), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), "Задачи не совпадают.");
        assertEquals(savedEpic.getEndTime(), LocalDateTime.of(2020, 1, 1, 0, 50, 0, 0), "Задачи не совпадают.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldNotCreateTaskWithSameTime() throws IOException {
        System.out.println("\n\n***** shouldNotCreateTaskWithSameTime *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Task task2 = taskManager.createOrUpdateTask(null, "Test TASK_2",
                "Test TASK_2 description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));

        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(tasksSortedByTime, "Задачи на возвращаются.");
        assertNull(taskManager.getTaskById(2));
        assertEquals(1, tasksSortedByTime.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldNotCreateTaskWithIntersectionOfTime() throws IOException {
        System.out.println("\n\n***** shouldNotCreateTaskWithIntersectionOfTime *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0));
        Task task2 = taskManager.createOrUpdateTask(null, "Test TASK_2",
                "Test TASK_2 description", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 49, 0, 0));
        Task task3 = taskManager.createOrUpdateTask(null, "Test TASK_3",
                "Test TASK_3 description", NEW, 50L,
                LocalDateTime.of(2019, 12, 31, 23, 11, 0, 0));

        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(tasksSortedByTime, "Задачи на возвращаются.");
        assertNull(taskManager.getTaskById(2));
        assertEquals(1, tasksSortedByTime.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldNotCreateSubTaskWithSameTime() throws IOException {
        System.out.println("\n\n***** shouldNotCreateSubTaskWithSameTime *****");
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", IN_PROGRESS, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        SubTask subTask_2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK_2",
                "Test SUBTASK description_2", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);

        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(tasksSortedByTime, "Задачи на возвращаются.");
        assertNull(taskManager.getTaskById(3));
        assertEquals(1, tasksSortedByTime.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldNotCreateSubTaskWithIntersectionOfTime() throws IOException {
        System.out.println("\n\n***** shouldNotCreateSubTaskWithIntersectionOfTime *****");
        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", IN_PROGRESS, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0),
                1);
        SubTask subTask_2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK_2",
                "Test SUBTASK description_2", NEW, 50L,
                LocalDateTime.of(2020, 1, 1, 0, 49, 0, 0),
                1);
        SubTask subTask_3 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK_3",
                "Test SUBTASK description_3", NEW, 50L,
                LocalDateTime.of(2019, 12, 31, 23, 11, 0, 0),
                1);

        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(tasksSortedByTime, "Задачи на возвращаются.");
        assertNull(taskManager.getTaskById(3));
        assertNull(taskManager.getTaskById(4));
        assertEquals(1, tasksSortedByTime.size(), "Неверное количество задач.");
        System.out.println("-------------------------------------------------\n\n");
    }

    @Test
    void shouldPrintSortedByStartTimeTreeSet() throws IOException {
        System.out.println("\n\n***** shouldPrintSortedByStartTimeTreeSet *****");
        Task task = taskManager.createOrUpdateTask(null, "Test TASK",
                "Test TASK description", NEW, 50L,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));

        Epic epic = taskManager.createOrUpdateEpic(null, "Test EPIC",
                "Test EPIC description", NEW);
        SubTask subTask = taskManager.createOrUpdateSubTask(null, "Test SUBTASK",
                "Test SUBTASK description", IN_PROGRESS, 50L,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0),
                2);
        SubTask subTask2 = taskManager.createOrUpdateSubTask(null, "Test SUBTASK2",
                "Test SUBTASK2 description", DONE, 50L,
                LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0),
                2);

        Set<Task> tasksSortedByTime = taskManager.getAllTasksAndSubTasksSortedByStartTime();

        assertNotNull(tasksSortedByTime, "Задачи на возвращаются.");
        assertEquals(3, tasksSortedByTime.size(), "Неверное количество задач.");
        taskManager.printAllTasksAndSubTasksSortedByStartTime();
        System.out.println("-------------------------------------------------\n\n");
    }


}
