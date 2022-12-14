package service;

public class Managers {
    public static TaskManager getDefault() {
        TaskManager taskManager = new InMemoryTaskManager();
        return taskManager;
    }

    public static TaskManager getFileBackedTaskManager() {
        TaskManager fileBackTaskManager = new FileBackedTaskManager("resources/ListAndHistoryOfTasks.txt");
        return fileBackTaskManager;
    }


    public static HistoryManager getDefaultHistory(){
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}