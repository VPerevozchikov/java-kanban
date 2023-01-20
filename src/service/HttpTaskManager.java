package service;

import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import model.Task;
import server.KVTaskClient;
import java.io.IOException;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {
    KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();
    String empty = "empty";

    public HttpTaskManager(String url) throws IOException {
        super(url);
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {

        //сохранение Task

        if (!dataTask.isEmpty()) {
            StringBuilder builderStringOfTasks = new StringBuilder();

            for (Task task : dataTask.values()) {
                String jsonString = gson.toJson(task);
                builderStringOfTasks.append(";");
                builderStringOfTasks.append(jsonString);
            }
            builderStringOfTasks.deleteCharAt(0);

            String stringWithAllTasks = builderStringOfTasks.toString();
            kvTaskClient.put("task", stringWithAllTasks);
        } else {
            kvTaskClient.put("task", empty);
        }

        //сохранение Epic

        if (!dataEpic.isEmpty()) {
            StringBuilder builderStringOfEpics = new StringBuilder();

            for (Epic epic : dataEpic.values()) {
                String jsonString = gson.toJson(epic);
                builderStringOfEpics.append(";");
                builderStringOfEpics.append(jsonString);
            }
            builderStringOfEpics.deleteCharAt(0);

            String stringWithAllEpics = builderStringOfEpics.toString();
            kvTaskClient.put("epic", stringWithAllEpics);
        } else {
            kvTaskClient.put("epic", empty);
        }

        //сохранение SubTask

        if (!dataSubTask.isEmpty()) {
            StringBuilder builderStringOfSubTasks = new StringBuilder();

            for (SubTask subTask : dataSubTask.values()) {
                String jsonString = gson.toJson(subTask);
                builderStringOfSubTasks.append(";");
                builderStringOfSubTasks.append(jsonString);
            }
            builderStringOfSubTasks.deleteCharAt(0);

            String stringWithAllSubTasks = builderStringOfSubTasks.toString();
            kvTaskClient.put("subTask", stringWithAllSubTasks);
        } else {
            kvTaskClient.put("subTask", empty);
        }

        //сохранение history

        if (!super.getHistory().isEmpty()) {
            List<Task> history = super.getHistory();
            StringBuilder builderStringOfHistory = new StringBuilder();
            for (Task task : history) {
                String jsonString = gson.toJson(task.getId());
                builderStringOfHistory.append(";");
                builderStringOfHistory.append(jsonString);
            }
            builderStringOfHistory.deleteCharAt(0);

            String stringWithHistory = builderStringOfHistory.toString();
            kvTaskClient.put("history", stringWithHistory);
        } else {
            kvTaskClient.put("history", empty);
        }
    }

    @Override
    public void loadAllTasksFromServer() {

        //восстановление Task

        String recoveryTasks = kvTaskClient.load("task");
        if (recoveryTasks != null && !recoveryTasks.equals(empty)) {
            if (recoveryTasks.contains(";")) {
                String[] arrayOfTasks = recoveryTasks.split(";");
                for (int i = 0; i < arrayOfTasks.length; i++) {
                    Task task = gson.fromJson(arrayOfTasks[i], Task.class);
                    dataTask.put(task.getId(), task);
                    allTasksAndSubTasksSortedByStartTime.add(task);
                }
                System.out.println("Задачи типа Task восстановлены");
            } else {
                Task task = gson.fromJson(recoveryTasks, Task.class);
                dataTask.put(task.getId(), task);
                allTasksAndSubTasksSortedByStartTime.add(task);
                System.out.println("Задачи типа Task восстановлены");
            }
        } else {
            System.out.println("Список задач типа Task пуст");
        }

        //восстановление Epic

        String recoveryEpics = kvTaskClient.load("epic");
        if (recoveryEpics != null && !recoveryEpics.equals(empty)) {
            if (recoveryEpics.contains(";")) {
                String[] arrayOfEpics = recoveryEpics.split(";");
                for (int i = 0; i < arrayOfEpics.length; i++) {
                    Epic epic = gson.fromJson(arrayOfEpics[i], Epic.class);
                    dataEpic.put(epic.getId(), epic);
                }
                System.out.println("Задачи типа Epic восстановлены");
            } else {
                Epic epic = gson.fromJson(recoveryEpics, Epic.class);
                dataEpic.put(epic.getId(), epic);
                System.out.println("Задачи типа Epic восстановлены");
            }
        } else {
            System.out.println("Список задач типа Epic пуст");
        }

        //восстановление SubTask

        String recoverySubTasks = kvTaskClient.load("subTask");
        if (recoverySubTasks != null && !recoverySubTasks.equals(empty)) {
            if (recoverySubTasks.contains(";")) {
                String[] arrayOfSubTasks = recoverySubTasks.split(";");
                for (int i = 0; i < arrayOfSubTasks.length; i++) {
                    SubTask subTask = gson.fromJson(arrayOfSubTasks[i], SubTask.class);
                    dataSubTask.put(subTask.getId(), subTask);
                    allTasksAndSubTasksSortedByStartTime.add(subTask);
                }
                System.out.println("Задачи типа SubTask восстановлены");
            } else {
                SubTask subTask = gson.fromJson(recoverySubTasks, SubTask.class);
                dataSubTask.put(subTask.getId(), subTask);
                allTasksAndSubTasksSortedByStartTime.add(subTask);
                System.out.println("Задачи типа SubTask восстановлены");
            }
        } else {
            System.out.println("Список задач типа SubTask пуст");
        }

        //восстановление id

        List<Task> listOfAllTasks = getListAllTasks();
        int recoveryId = 0;

        for (Task task : listOfAllTasks) {
            if (task.getId() > recoveryId) {
                recoveryId = task.getId();
            }
        }
        id = recoveryId;

        //восстановление history

        String recoveryHistory = kvTaskClient.load("history");
        if (recoveryHistory != null && !recoveryHistory.equals(empty)) {
            if (recoveryHistory.contains(";")) {
                String[] arrayOfHistory = recoveryHistory.split(";");
                for (int i = 0; i < arrayOfHistory.length; i++) {
                    getTaskById(Integer.parseInt(arrayOfHistory[i]));
                }
                System.out.println("История задач восстановлена");
            } else {
                getTaskById(Integer.parseInt(recoveryHistory));
                System.out.println("История задач восстановлена");
            }
        } else {
            System.out.println("Список с историей задач пуст");
        }
    }
}