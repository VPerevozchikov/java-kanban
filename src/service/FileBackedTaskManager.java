package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getFileBackedTaskManager();

        taskManager.createOrUpdateTask(null, "Сходить в магазин",
                "Молоко 2л", Status.NEW);
        taskManager.createOrUpdateEpic(null, "Построить дом",
                "Двухэтажный деревянный", Status.NEW);
        taskManager.createOrUpdateSubTask(null, "Вырыть фунтамент",
                "глубина 1.8 м", Status.IN_PROGRESS, 2);
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
    }

    @Override
    public void createOrUpdateTask(Integer idEnter, String name, String description, Status status) {
        super.createOrUpdateTask(idEnter, name, description, status);
        save();
    }

    @Override
    public void createOrUpdateEpic(Integer idEnter, String name, String description, Status status) {
        super.createOrUpdateEpic(idEnter, name, description, status);
        save();
    }

    @Override
    public void createOrUpdateSubTask(Integer idEnter, String name, String description, Status status
            , Integer idOfEpic) {
        super.createOrUpdateSubTask(idEnter, name, description, status, idOfEpic);
        save();
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
        super.getTaskById(idEnter);
        save();
        return dataTask.get(idEnter);
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(filename)) {
            if (Files.exists(Paths.get(filename))) {

                fileWriter.write("id,type,name,status,description,epic\n");
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
                dataTask.put(Integer.parseInt(taskTxt[0]), new Task(Integer.parseInt(taskTxt[0]),
                        taskTxt[2], taskTxt[4], checkStatus(taskTxt[3])));
            } else if (taskTxt[1].equals("EPIC")) {
                dataEpic.put(Integer.parseInt(taskTxt[0]), new Epic(Integer.parseInt(taskTxt[0]),
                        taskTxt[2], taskTxt[4], checkStatus(taskTxt[3])));
            } else if (taskTxt[1].equals("SUBTASK")) {
                dataSubTask.put(Integer.parseInt(taskTxt[0]), new SubTask(Integer.parseInt(taskTxt[0]),
                        taskTxt[2], taskTxt[4], checkStatus(taskTxt[3]),
                        Integer.parseInt(taskTxt[5])));
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


