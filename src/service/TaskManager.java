package service;


import model.Status;
import model.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {
    List<Task> getListAllTasks();
    void deleteAllTasks();
    Task getTaskById(Integer idEnter) throws IOException;
    void deleteTaskById(Integer idEnter);
    void createOrUpdateTask(Integer idEnter, String name, String description, Status status) throws IOException;
    void createOrUpdateEpic(Integer idEnter, String name, String description, Status status) throws IOException;
    void createOrUpdateSubTask(Integer idEnter, String name, String description, Status status, Integer idOfEpic) throws IOException;
    void printMapSubTasksOfEpic (Integer idOfEpic);
    List<Task> getHistory();

    void getHistoryWithPrint();
}




















