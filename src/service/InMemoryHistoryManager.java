package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static final int REQUEST_LENGTH = 10;
    List<Task> historyOfView = new ArrayList<>();

    @Override
    public void add (Task task) {
        if (historyOfView.size() < REQUEST_LENGTH) {
            historyOfView.add(task);
        } else {
            historyOfView.remove(0);
            historyOfView.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("История просмотров:");
        for (int i = 0; i < historyOfView.size(); i++) {
            System.out.println((i + 1) + ". " + historyOfView.get(i));
        }
        return historyOfView;
    }
}
