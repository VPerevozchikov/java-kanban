package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

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

    public static UserManager getUserDefault() {
        return new InMemoryUserManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter());
        return gsonBuilder.create();
    }
}