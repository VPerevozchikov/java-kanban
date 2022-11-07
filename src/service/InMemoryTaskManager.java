package service;

import model.Task;
import model.Epic;
import model.SubTask;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();
    Integer id = 0;
    HashMap<Integer, Task> dataTask = new HashMap<>();
    HashMap<Integer, Epic> dataEpic = new HashMap<>();
    HashMap<Integer, SubTask> dataSubTask = new HashMap<>();


    @Override
    public ArrayList<Task> getListAllTasks() {
        ArrayList<Task> dataAllTasks = new ArrayList<>();
        System.out.println("Список одтельных задач:");
        for (Integer id : dataTask.keySet()) {
            System.out.println("ID " + id + " " + dataTask.get(id));
            dataAllTasks.add(dataTask.get(id));
        }
        System.out.println(".................");
        System.out.println("Список эпиков:");
        for (Integer id : dataEpic.keySet()) {
            System.out.println("ID " + id + " " + dataEpic.get(id));
            dataAllTasks.add(dataEpic.get(id));
        }
        System.out.println(".................");
        System.out.println("Список подзадач:");
        for (Integer id : dataSubTask.keySet()) {
            System.out.println("ID " + id + " " + dataSubTask.get(id));
            dataAllTasks.add(dataSubTask.get(id));
        }
        System.out.println(".................");
        return dataAllTasks;
    }

    @Override
    public void deleteAllTasks() {
        dataTask.clear();
        dataSubTask.clear();
        dataEpic.clear();
    }

    @Override
    public Task getTaskById(Integer idEnter) {
        Task task;
        if (dataTask.containsKey(idEnter)) {
            task = dataTask.get(idEnter);
        } else if (dataEpic.containsKey(idEnter)) {
            task = dataEpic.get(idEnter);
        } else if (dataSubTask.containsKey(idEnter)) {
            task = dataSubTask.get(idEnter);
        } else {
            System.out.println("Запрашиваемый ID " + idEnter + " не найден.");
            task = null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTaskById(Integer idEnter) {
        if (dataTask.containsKey(idEnter)) {
            dataTask.remove(idEnter);
        } else if (dataEpic.containsKey(idEnter)) {
            Epic dubEpic = dataEpic.get(idEnter);
            HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();
            for (SubTask subTask : dubMapOfSubTasks.values()) {
                int idOfSubTaskForRemove = subTask.getId();
                dataSubTask.remove(idOfSubTaskForRemove);
            }
            dataEpic.remove(idEnter);
        } else if (dataSubTask.containsKey(idEnter)) {
            SubTask dubSubTask = dataSubTask.get(idEnter);
            int idOfEpic = dubSubTask.getIdOfEpic();
            Epic dubEpic = dataEpic.get(idOfEpic);
            HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();
            dubMapOfSubTasks.remove(idEnter);

            dataSubTask.remove(idEnter);

            checkStatusEpic(dubEpic);
        } else {
            System.out.println("Такого ID нет");
        }
    }

    @Override
    public void createOrUpdateTask(Integer idEnter, String name, String description, Status status) {

            if (idEnter != null) {
                if (!dataTask.containsKey(idEnter)) {
                    System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                            + " не существует.");
                    return;
                }
            } else {
                idEnter = ++id;
            }
            dataTask.put(idEnter, new Task(idEnter, name, description, status));
    }

    @Override
    public void createOrUpdateEpic(Integer idEnter, String name, String description, Status status) {

        if (idEnter != null) {
            if (!dataEpic.containsKey(idEnter)) {
                System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                        + " не существует.");
                return;
            } else {
                Epic currentEpic = dataEpic.get(idEnter);
                if (!currentEpic.getStatus().equals(status)) {
                    System.out.println("Обновление эпика зависит от статуса Подзадач. Обновление вручную невозможно.");
                    return;
                }
            }
        } else {
            if (status != Status.NEW) {
                System.out.println("Для нового эпика введите статус NEW");
                return;
            }
            idEnter = ++id;
        }
        dataEpic.put(idEnter, new Epic(idEnter, name, description, status));
    }

    public void checkStatusEpic (Epic epicForCheck) {
        HashMap<Integer, SubTask> dubMapOfSubTasks = epicForCheck.getMapOfSubTasks();

        int countOfDoneSubTasks = 0;
        for (SubTask subTask : dubMapOfSubTasks.values()) {
            Status currentStatus = subTask.getStatus();
            if (currentStatus.equals(Status.DONE)) {
                countOfDoneSubTasks++;
            }
        }

        int countOfInProgressSubTasks = 0;
        for (SubTask subTask : dubMapOfSubTasks.values()) {
            Status currentStatus = subTask.getStatus();
            if (currentStatus.equals(Status.IN_PROGRESS)) {
                countOfInProgressSubTasks++;
            }
        }

        if (countOfDoneSubTasks == 0 && countOfInProgressSubTasks == 0) {
            epicForCheck.setStatus(Status.NEW);
        } else if (countOfDoneSubTasks == 0 && countOfInProgressSubTasks != 0) {
            epicForCheck.setStatus(Status.IN_PROGRESS);
        } else if (countOfDoneSubTasks == dubMapOfSubTasks.size()){
            epicForCheck.setStatus(Status.DONE);
        }
    }

    @Override
    public void createOrUpdateSubTask(Integer idEnter, String name, String description, Status status
            , Integer idOfEpic) {

        if (!dataEpic.containsKey(idOfEpic)) {
            System.out.println("Задача " + name + " не создана и не обновлена. Введенный idOfEpic " + idOfEpic
                    + " не принадлежит типу Эпик.");
            return;
        }

        if (idEnter != null) {
            if (!dataSubTask.containsKey(idEnter)) {
                System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                        + " не существует.");
                return;
            }
        } else {
            idEnter = ++id;
        }
        dataSubTask.put(idEnter, new SubTask(idEnter, name, description, status, idOfEpic));
        Epic dubEpic = dataEpic.get(idOfEpic);
        HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();
        dubMapOfSubTasks.put(idEnter, new SubTask(idEnter, name, description, status, idOfEpic));

        checkStatusEpic(dubEpic);
    }

    @Override
    public void printMapSubTasksOfEpic (Integer idOfEpic) {
        Epic newEpic = dataEpic.get(idOfEpic);
        HashMap<Integer, SubTask> newMapOfSubTasks = newEpic.getMapOfSubTasks();
        for (Integer id : newMapOfSubTasks.keySet()) {
            System.out.println("ID " + id + " " + newMapOfSubTasks.get(id));
        }
    }
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


}

