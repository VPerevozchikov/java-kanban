package service;


import model.Status;
import model.Task;
import java.util.List;

public interface TaskManager {
    List<Task> getListAllTasks();
    void deleteAllTasks();
    Task getTaskById(Integer idEnter);
    void deleteTaskById(Integer idEnter);
    void createOrUpdateTask(Integer idEnter, String name, String description, Status status);
    void createOrUpdateEpic(Integer idEnter, String name, String description, Status status);
    void createOrUpdateSubTask(Integer idEnter, String name, String description, Status status, Integer idOfEpic);
    void printMapSubTasksOfEpic (Integer idOfEpic);
    List<Task> getHistory();
}




















