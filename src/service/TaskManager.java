package service;


import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> getListAllTasks();

    void deleteAllTasks();

    Task getTaskById(Integer idEnter) throws IOException;

    void deleteTaskById(Integer idEnter);

    Task createOrUpdateTask(Integer idEnter, String name, String description, Status status, Long duration, LocalDateTime startTime) throws IOException;

    Epic createOrUpdateEpic(Integer idEnter, String name, String description, Status status) throws IOException;

    SubTask createOrUpdateSubTask(Integer idEnter, String name, String description, Status status, Long duration, LocalDateTime startTime, Integer idOfEpic) throws IOException;

    List<Task> getMapSubTasksOfEpic(Integer idOfEpic);

    List<Task> getHistory();

    List<Task> getListOfTasks();

    List<Epic> getListOfEpics();

    List<SubTask> getListOfSubTask();

    void getHistoryWithPrint();

    Set<Task> getAllTasksAndSubTasksSortedByStartTime();

    void printAllTasksAndSubTasksSortedByStartTime();

    void loadAllTasksFromServer();
}




















