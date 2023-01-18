package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault() throws IOException {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static TaskManager getFileBackedTaskManager() {
        TaskManager fileBackTaskManager = new FileBackedTaskManager("resources/ListAndHistoryOfTasks.txt");
        return fileBackTaskManager;
    }


    public static HistoryManager getDefaultHistory(){
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }



    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        return gsonBuilder.create();
    }
}