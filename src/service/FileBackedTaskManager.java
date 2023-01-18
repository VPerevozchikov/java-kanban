package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

/*    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getFileBackedTaskManager();

        taskManager.createOrUpdateTask(null, "Сходить в магазин",
                "Молоко 2л",  Status.NEW, 60L,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0).plusMinutes(10));
        taskManager.createOrUpdateEpic(null, "Построить дом",
                "Двухэтажный деревянный", Status.NEW);
        taskManager.createOrUpdateSubTask(null, "Вырыть фундамент",
                "глубина 1.8 м", Status.IN_PROGRESS, 6000L,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0).plusMinutes(100), 2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getListAllTasks();
        taskManager.getHistoryWithPrint();

        System.out.println("\n***** ПРОВЕРКА ФАКТА ВОССТАНОВЛЕНИЯ МЕНЕДЖЕРА ИЗ ФАЙЛА *****\n");

        FileBackedTaskManager doubleFBTM = loadFromFile(new File("resources/ListAndHistoryOfTasks.txt"));
        doubleFBTM.load("resources/ListAndHistoryOfTasks.txt");
        doubleFBTM.getListAllTasks();
        doubleFBTM.getHistoryWithPrint();
    }*/

    @Override
    public Task createOrUpdateTask(Integer idEnter, String name, String description, Status status, Long duration, LocalDateTime startTime) {
        Task task = super.createOrUpdateTask(idEnter, name, description, status, duration, startTime);
        save();
        return task;
    }

    @Override
    public Epic createOrUpdateEpic(Integer idEnter, String name, String description, Status status) {
        Epic epic = super.createOrUpdateEpic(idEnter, name, description, status);
        save();
        return epic;
    }

    @Override
    public SubTask createOrUpdateSubTask(Integer idEnter, String name, String description, Status status
            , Long duration, LocalDateTime startTime, Integer idOfEpic) {
        SubTask subTask = super.createOrUpdateSubTask(idEnter, name, description, status, duration, startTime, idOfEpic);
        save();
        return subTask;
    }

    @Override
    public void deleteTaskById(Integer idEnter) {
        super.deleteTaskById(idEnter);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskById(Integer idEnter) {
        Task task = super.getTaskById(idEnter);
        save();
        return task;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(filename)) {
            if (Files.exists(Paths.get(filename))) {

                fileWriter.write("id,type,name,status,description,duration,startTime,endTime,epic\n");
                for (Task task : dataTask.values()) {
                    fileWriter.write(task.taskToString(task));
                }
                for (Epic epic : dataEpic.values()) {
                    fileWriter.write(epic.epicToString(epic));
                }
                for (SubTask subTask : dataSubTask.values()) {
                    fileWriter.write(subTask.subTaskToString(subTask));
                }

                if (!super.getHistory().isEmpty()) {
                    List<Task> history = super.getHistory();
                    StringBuilder historyView = new StringBuilder();

                    int count = 0;
                    for (Task task : history) {
                        if (count != (history.size() - 1)) {
                            historyView.append(task.getId() + ",");
                            count++;
                        } else {
                            historyView.append(task.getId());
                        }
                    }
                    fileWriter.write("\n" + historyView);
                }

            } else {
                throw new ManagerSaveException("Файл отсутствует");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при попытки сохранить данные в файл");
        }
    }

    public void load(String filename) {

//восстановление задач
        List<String[]> tasksFromTxt = readFromFile(filename);

        tasksFromTxt.remove(0);
        tasksFromTxt.remove(tasksFromTxt.size() - 1);
        tasksFromTxt.remove(tasksFromTxt.size() - 1);

        for (String[] taskTxt : tasksFromTxt) {
            if (taskTxt[1].equals("TASK")) {
                Task recoveryTask = new Task(Integer.parseInt(taskTxt[0]),
                        taskTxt[2], taskTxt[4], checkStatus(taskTxt[3]),
                        Long.parseLong(taskTxt[5]),LocalDateTime.parse(taskTxt[6]));
                recoveryTask.setEndTime(LocalDateTime.parse(taskTxt[7]));

                dataTask.put(Integer.parseInt(taskTxt[0]), recoveryTask);
                allTasksAndSubTasksSortedByStartTime.add(recoveryTask);

            } else if (taskTxt[1].equals("EPIC")) {
                Epic recoveryEpic = new Epic(Integer.parseInt(taskTxt[0]),
                        taskTxt[2], taskTxt[4], checkStatus(taskTxt[3]));
                recoveryEpic.setStartTime(LocalDateTime.parse(taskTxt[6]));
                recoveryEpic.setEndTime(LocalDateTime.parse(taskTxt[7]));
                recoveryEpic.setDuration(Long.parseLong(taskTxt[5]));

                dataEpic.put(Integer.parseInt(taskTxt[0]), recoveryEpic);
                allTasksAndSubTasksSortedByStartTime.add(recoveryEpic);

            } else if (taskTxt[1].equals("SUBTASK")) {
                SubTask recoverySubTask = new SubTask(Integer.parseInt(taskTxt[0]),
                        taskTxt[2], taskTxt[4], checkStatus(taskTxt[3]),
                        Long.parseLong(taskTxt[5]),
                        LocalDateTime.parse(taskTxt[6]),
                        Integer.parseInt(taskTxt[8]));

                recoverySubTask.setEndTime(LocalDateTime.parse(taskTxt[7]));

                dataSubTask.put(Integer.parseInt(taskTxt[0]), recoverySubTask);
                allTasksAndSubTasksSortedByStartTime.add(recoverySubTask);
            }
        }

//восстановление истории просмотров
        List<String[]> historyFromTxt = readFromFile(filename);

        String[] history = historyFromTxt.get(historyFromTxt.size() - 1);
        for (String number : history) {
            getTaskById(Integer.parseInt(number));
        }
    }

    public List<String[]> readFromFile (String filename) {
        List<String[]> dataFromTxt = new ArrayList<>();
        try (FileReader reader = new FileReader(filename, StandardCharsets.UTF_8)) {
            BufferedReader br = new BufferedReader(reader);
            while (br.ready()) {
                String line = br.readLine();
                String[] dataOfTask = line.split(",");
                dataFromTxt.add(dataOfTask);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при попытке считать данные из файла");
        }
        return dataFromTxt;
    }



    public Status checkStatus(String statusFromTxt) {
        Status status = null;
        switch (statusFromTxt) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                status = Status.IN_PROGRESS;
                break;
            case "DONE":
                status = Status.DONE;
                break;
        }
        return status;
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManagerLoadFromFile = new FileBackedTaskManager(file.toString());
        return fileBackedTaskManagerLoadFromFile;
    }



}


