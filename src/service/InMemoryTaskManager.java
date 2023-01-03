package service;

import model.*;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();
    Integer id = 0;
    HashMap<Integer, Task> dataTask = new HashMap<>();
    HashMap<Integer, Epic> dataEpic = new HashMap<>();
    HashMap<Integer, SubTask> dataSubTask = new HashMap<>();

    Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    };

    Set<Task> allTasksAndSubTasksSortedByStartTime = new TreeSet<>(comparator);

    @Override
    public Task createOrUpdateTask(Integer idEnter, String name, String description, Status status, Long duration, LocalDateTime startTime) {

        if (duration < 0L || startTime == null) {
            System.out.println("Для задачи " + name + " введены некорректные время / дата " + duration
                    + " / " + startTime + ". Задача не создана.");
            return null;
        }

        if (idEnter != null) {
            if (!dataTask.containsKey(idEnter)) {
                System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                        + " не существует.");
                return null;
            } else {
                Task currentTask = dataTask.get(idEnter);
                Task updateTask = new Task(idEnter, name, description, status, duration, startTime);
                updateTask.setEndTime(updateTask.getEndTime(duration, startTime));
                if (currentTask.getStartTime().isEqual(updateTask.getStartTime()) && currentTask.getEndTime().isEqual(updateTask.getEndTime())) {
                    dataTask.put(idEnter, updateTask);
                    allTasksAndSubTasksSortedByStartTime.remove(currentTask);
                    allTasksAndSubTasksSortedByStartTime.add(updateTask);
                } else if (checkTime((TreeSet<Task>) allTasksAndSubTasksSortedByStartTime, updateTask.getStartTime(), updateTask.getEndTime())) {
                    dataTask.put(idEnter, updateTask);
                    allTasksAndSubTasksSortedByStartTime.remove(currentTask);
                    allTasksAndSubTasksSortedByStartTime.add(updateTask);
                } else {
                    System.out.println("Новая задача " + name + " пересекается с существующими по времени. Задача не создана.");
                    return null;
                }
                return updateTask;
            }
        } else {
            idEnter = ++id;
        }

        Task newTask = new Task(idEnter, name, description, status, duration, startTime);
        newTask.setEndTime(newTask.getEndTime(duration, startTime));

        if (checkTime((TreeSet<Task>) allTasksAndSubTasksSortedByStartTime, newTask.getStartTime(), newTask.getEndTime())) {
            dataTask.put(idEnter, newTask);
            allTasksAndSubTasksSortedByStartTime.add(newTask);
            return newTask;
        } else {
            System.out.println("Новая задача " + name + " пересекается с существующими по времени. Задача не создана.");
            --id;
            return null;
        }
    }

    @Override
    public Epic createOrUpdateEpic(Integer idEnter, String name, String description, Status status) {

        if (idEnter != null) {
            if (!dataEpic.containsKey(idEnter)) {
                System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                        + " не существует.");
                return null;
            } else {
                Epic currentEpic = dataEpic.get(idEnter);
                if (!currentEpic.getStatus().equals(status)) {
                    System.out.println("Обновление эпика зависит от статуса Подзадач. Обновление вручную невозможно.");
                    return null;
                } else {
                    HashMap<Integer, SubTask> mapFromCurrentEpic = currentEpic.getMapOfSubTasks();
                    Epic updateEpic = new Epic(idEnter, name, description, status);
                    updateEpic.setMapOfSubTasks(mapFromCurrentEpic);

                    updateDataTimeEpic(updateEpic);
                    dataEpic.put(idEnter, updateEpic);

                    return updateEpic;
                }
            }
        } else {
            if (status != Status.NEW) {
                System.out.println("Для нового эпика введите статус NEW");
                return null;
            }
            idEnter = ++id;
        }

        Epic newEpic = new Epic (idEnter, name, description, status);
        dataEpic.put(idEnter, newEpic);
        return newEpic;
    }

    @Override
    public SubTask createOrUpdateSubTask(Integer idEnter, String name, String description, Status status
            , Long duration, LocalDateTime startTime, Integer idOfEpic) {

        if (!dataEpic.containsKey(idOfEpic)) {
            System.out.println("Задача " + name + " не создана и не обновлена. Введенный idOfEpic " + idOfEpic
                    + " не принадлежит типу Эпик.");
            return null;
        }

        if (duration < 0L || startTime == null) {
            System.out.println("Для задачи " + name + " введены некорректные время / дата " + duration
                    + " / " + startTime + ". Задача не создана.");
            return null;
        }

        if (idEnter != null) {
            if (!dataSubTask.containsKey(idEnter)) {
                System.out.println("Задача " + name + " не создана и не обновлена. Введенный id " + idEnter
                        + " не существует.");
                return null;
            } else {
                SubTask currentSubTask = dataSubTask.get(idEnter);
                SubTask updateSubTask = new SubTask(idEnter, name, description, status,  duration, startTime, idOfEpic);
                updateSubTask.setEndTime(updateSubTask.getEndTime(duration, startTime));

                if (currentSubTask.getStartTime().isEqual(updateSubTask.getStartTime()) && currentSubTask.getEndTime().isEqual(updateSubTask.getEndTime())) {
                    dataSubTask.put(idEnter, updateSubTask);
                    allTasksAndSubTasksSortedByStartTime.remove(currentSubTask);
                    fillDataOfSubTask(idEnter, updateSubTask, idOfEpic);

                } else if (checkTime((TreeSet<Task>) allTasksAndSubTasksSortedByStartTime, updateSubTask.getStartTime(), updateSubTask.getEndTime())) {
                    allTasksAndSubTasksSortedByStartTime.remove(currentSubTask);
                    fillDataOfSubTask(idEnter, updateSubTask, idOfEpic);
                } else {
                    System.out.println("Новая задача " + name + " пересекается с существующими по времени. Задача не создана.");
                    return null;
                }
                return updateSubTask;
            }
        } else {
            idEnter = ++id;
        }
        SubTask newSubTask = new SubTask(idEnter, name, description, status,  duration, startTime, idOfEpic);
        newSubTask.setEndTime(newSubTask.getEndTime(duration, startTime));

        if (checkTime((TreeSet<Task>) allTasksAndSubTasksSortedByStartTime, newSubTask.getStartTime(), newSubTask.getEndTime())) {
            fillDataOfSubTask(idEnter, newSubTask, idOfEpic);
        } else {
            --id;
            System.out.println("Новая задача " + name + " пересекается с существующими по времени. Задача не создана.");
            return null;
        }
        return newSubTask;
    }

    public boolean checkTime(TreeSet<Task> mapWithTime, LocalDateTime startTimeForCheck, LocalDateTime endTimeForCheck) {

        if (mapWithTime.isEmpty()) {
            return true;
        } else {
            Task taskHead = mapWithTime.first();
            Task taskTail = mapWithTime.last();

            if (startTimeForCheck.isAfter(taskTail.getEndTime())) {
                return true;
            } else if (endTimeForCheck.isBefore(taskHead.getStartTime())) {
                return true;
            }

            if (mapWithTime.size() == 1) {
                if (startTimeForCheck.isAfter(taskHead.getStartTime()) && startTimeForCheck.isBefore(taskHead.getEndTime())) {
                    return false;
                } else if (endTimeForCheck.isAfter(taskHead.getStartTime()) && endTimeForCheck.isBefore(taskHead.getEndTime())) {
                    return false;
                } else if (startTimeForCheck.isEqual(taskHead.getStartTime()) && endTimeForCheck.isEqual(taskHead.getEndTime())) {
                    return false;
                } else {
                    return true;
                }
            } else {
                for (Task task : mapWithTime) {
                    return startTimeForCheck.isAfter(task.getEndTime()) && endTimeForCheck.isBefore(mapWithTime.higher(task).getStartTime());
                }
            }
        }
        return true;
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

        int countOfNewSubTasks = 0;
        for (SubTask subTask : dubMapOfSubTasks.values()) {
            Status currentStatus = subTask.getStatus();
            if (currentStatus.equals(Status.NEW)) {
                countOfNewSubTasks++;
            }
        }

        if (countOfDoneSubTasks == 0 && countOfInProgressSubTasks == 0) {
            epicForCheck.setStatus(Status.NEW);
        } else if (countOfInProgressSubTasks != 0) {
            epicForCheck.setStatus(Status.IN_PROGRESS);
        } else if (countOfDoneSubTasks == dubMapOfSubTasks.size()){
            epicForCheck.setStatus(Status.DONE);
        } else if (countOfNewSubTasks != 0) {
            epicForCheck.setStatus(Status.NEW);
        }
    }

    public void updateDataTimeEpic (Epic epicForUpdate) {
        LocalDateTime startTime = epicForUpdate.calcStartTime(epicForUpdate.getMapOfSubTasks());
        LocalDateTime endTime = epicForUpdate.calcEndTime(epicForUpdate.getMapOfSubTasks());

        epicForUpdate.setStartTime(startTime);
        epicForUpdate.setEndTime(endTime);
        epicForUpdate.calcDuration(startTime, endTime);
    }

    public void fillDataOfSubTask (int id, SubTask subTask, int idOfEpic) {
        dataSubTask.put(id, subTask);
        Epic dubEpic = dataEpic.get(idOfEpic);
        HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();

        dubMapOfSubTasks.put(id, subTask);
        checkStatusEpic(dubEpic);

        allTasksAndSubTasksSortedByStartTime.add(subTask);

        updateDataTimeEpic (dubEpic);
    }

    @Override
    public void deleteTaskById(Integer idEnter) {

        if (dataTask.containsKey(idEnter)) {
            Task dubTask = dataTask.get(idEnter);
            dataTask.remove(idEnter);
            historyManager.remove(idEnter);
            allTasksAndSubTasksSortedByStartTime.remove(dubTask);
        } else if (dataEpic.containsKey(idEnter)) {
            Epic dubEpic = dataEpic.get(idEnter);
            HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();
            for (SubTask subTask : dubMapOfSubTasks.values()) {
                int idOfSubTaskForRemove = subTask.getId();
                dataSubTask.remove(idOfSubTaskForRemove);
                historyManager.remove(idOfSubTaskForRemove);
                allTasksAndSubTasksSortedByStartTime.remove(subTask);
            }
            dubMapOfSubTasks.clear();
            dataEpic.remove(idEnter);
            historyManager.remove(idEnter);

        } else if (dataSubTask.containsKey(idEnter)) {
            SubTask dubSubTask = dataSubTask.get(idEnter);
            int idOfEpic = dubSubTask.getIdOfEpic();
            Epic dubEpic = dataEpic.get(idOfEpic);
            HashMap<Integer, SubTask> dubMapOfSubTasks = dubEpic.getMapOfSubTasks();
            dubMapOfSubTasks.remove(idEnter);

            dataSubTask.remove(idEnter);
            historyManager.remove(idEnter);
            allTasksAndSubTasksSortedByStartTime.remove(dubSubTask);

            checkStatusEpic(dubEpic);

            updateDataTimeEpic(dubEpic);
        } else {
            System.out.println("Такого ID нет");
        }

    }

    @Override
    public void deleteAllTasks() {
        for (int idNumber : dataTask.keySet()) {
            historyManager.remove(idNumber);
        }

        for (int idNumber : dataSubTask.keySet()) {
            historyManager.remove(idNumber);
        }

        for (int idNumber : dataEpic.keySet()) {
            historyManager.remove(idNumber);
        }

        dataTask.clear();
        dataSubTask.clear();
        dataEpic.clear();
        allTasksAndSubTasksSortedByStartTime.clear();

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
    public ArrayList<Task> getListAllTasks() {
        ArrayList<Task> dataAllTasks = new ArrayList<>();
        System.out.println("Список отдельных задач:");
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
        return dataAllTasks;
    }

    @Override
    public Set<Task> getAllTasksAndSubTasksSortedByStartTime() {
        return allTasksAndSubTasksSortedByStartTime;
    }

    @Override
    public void printAllTasksAndSubTasksSortedByStartTime() {
        Set<Task> test = getAllTasksAndSubTasksSortedByStartTime();
        System.out.println("\n ******** ПЕЧАТЬ ЗАДАЧ ПО ВРЕМЕНИ СТАРТА ******** \n");
        for (Task task : test) {
            System.out.println(task.getStartTime() + "   " + task);
        }
        System.out.println("\n");
        for (Epic epic : dataEpic.values()) {
            System.out.println(epic.getStartTime() + "   " + epic);
        }
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

    @Override
    public void getHistoryWithPrint() {
        historyManager.getHistoryWithPrint();
    }

}

