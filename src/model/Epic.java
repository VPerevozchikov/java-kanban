package model;

import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, SubTask> mapOfSubTasks = new HashMap<>();


    public Epic (Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public HashMap<Integer, SubTask> getMapOfSubTasks() {
        return mapOfSubTasks;
    }

    public void setMapOfSubTasks(HashMap<Integer, SubTask> mapOfSubTasks) {
        this.mapOfSubTasks = mapOfSubTasks;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id ='" + id + '\'' +
                ", name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", status ='" + status + '\'' + "}";
        return result;
    }

}
