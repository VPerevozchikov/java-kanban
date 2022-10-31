package service;

import model.Task;
import model.Epic;
import model.SubTask;
import model.Status;

import java.util.HashMap;

public class Manager {
    Integer id = 0;
    HashMap<Integer, Task> dataTask = new HashMap<>();
    HashMap<Integer, Epic> dataEpic = new HashMap<>();
    HashMap<Integer, SubTask> dataSubTask = new HashMap<>();

    public HashMap<Integer, Task> getListAllTasks() {
        HashMap<Integer, Task> dataAllTasks = new HashMap<>();
        System.out.println("Список одтельных задач:");
        for (Integer id : dataTask.keySet()) {
            System.out.println("ID " + id + " " + dataTask.get(id));
            dataAllTasks.put(id, dataTask.get(id));
        }
        System.out.println(".................");
        System.out.println("Список эпиков:");
        for (Integer id : dataEpic.keySet()) {
            System.out.println("ID " + id + " " + dataEpic.get(id));
            dataAllTasks.put(id, dataEpic.get(id));
        }
        System.out.println(".................");
        System.out.println("Список подзадач:");
        for (Integer id : dataSubTask.keySet()) {
            System.out.println("ID " + id + " " + dataSubTask.get(id));
            dataAllTasks.put(id, dataSubTask.get(id));
        }
        System.out.println(".................");
        return dataAllTasks;
    }

    public void deleteAllTasks() {
        dataTask.clear();
        dataSubTask.clear();
        dataEpic.clear();
    }

    public Task getTaskById(Integer idEnter) {
        Task task;
        if (dataTask.containsKey(idEnter)) {
            task = dataTask.get(idEnter);
        } else if (dataEpic.containsKey(idEnter)) {
            task = dataEpic.get(idEnter);
        } else if (dataSubTask.containsKey(idEnter)) {
            task = dataSubTask.get(idEnter);
        } else {
            System.out.println("Такого ID нет");
            task = null;
        }
        return task;
    }
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
            dataSubTask.remove(idEnter);
        } else {
            System.out.println("Такого ID нет");
        }
    }

    public void createOrUpdateTask(Integer idEnter, String name, String description, Status status) {
        if (dataTask.containsKey(idEnter)) {
            dataTask.remove(idEnter);
            Task newTask = new Task(idEnter, name, description, status);
            dataTask.put(idEnter, newTask);
        } else if (idEnter == null){
            id++;
            Task newTask = new Task(id, name, description, status);
            newTask.setId(id);
            dataTask.put(id, newTask);
        } else {
            System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                    + " не существует.");
        }
    }

    public void createOrUpdateEpic(Integer idEnter, String name, String description, Status status) {
        if (dataEpic.containsKey(idEnter)) {
            Epic currentEpic = dataEpic.get(idEnter);
            if (currentEpic.getStatus().equals(status)) {
                dataEpic.remove(idEnter);
                Epic newEpic = new Epic(idEnter, name, description, status);
                dataEpic.put(idEnter, newEpic);
            } else {
                System.out.println("Обновление эпика зависит от статуса Подзадач. Обновление вручную невозможно.");
            }
        } else if (idEnter == null){
            if (status == Status.NEW) {
                id++;
                Epic newEpic = new Epic(id, name, description, status);
                dataEpic.put(id, newEpic);
            } else {
                System.out.println("Для нового эпика введите статус NEW");
            }
        } else {
            System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                    + " не существует.");
        }
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
    public void createOrUpdateSubTask(Integer idEnter, String name, String description, Status status
            , Integer idOfEpic) {
        if (dataEpic.containsKey(idOfEpic)) {
            if (dataSubTask.containsKey(idEnter)) {
                Epic dubEpic = dataEpic.get(idOfEpic);
                HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();
                dubMapOfSubTasks.remove(idEnter);
                dataSubTask.remove(idEnter);
                SubTask updateSubTask = new SubTask(idEnter, name, description, status, idOfEpic);
                dataSubTask.put(idEnter, updateSubTask);
                dubMapOfSubTasks.put(idEnter, updateSubTask);
                dubEpic.setMapOfSubTasks(dubMapOfSubTasks);

                checkStatusEpic(dubEpic);

            } else if (idEnter == null) {
                id++;
                SubTask newSubTask = new SubTask(id, name, description, status, idOfEpic);
                newSubTask.setId(id);
                dataSubTask.put(id, newSubTask);
                Epic dubEpic = dataEpic.get(idOfEpic);
                HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();
                dubMapOfSubTasks.put(id, newSubTask);
                dubEpic.setMapOfSubTasks(dubMapOfSubTasks);

                checkStatusEpic(dubEpic);

            } else {
                System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                        + " не существует.");
            }
        } else {
            System.out.println("Задача " + name + " не создана и не обновлена. Введенный idOfEpic " + idOfEpic
                    + " не принадлежит типу Эпик.");
        }
    }

    public void printMapSubTasksOfEpic (Integer idOfEpic) {
        Epic newEpic = dataEpic.get(idOfEpic);
        HashMap<Integer, SubTask> newMapOfSubTasks = newEpic.getMapOfSubTasks();
        for (Integer id : newMapOfSubTasks.keySet()) {
            System.out.println("ID " + id + " " + newMapOfSubTasks.get(id));
        }

    }


}

