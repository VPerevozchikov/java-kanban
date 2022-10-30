package model;

public class SubTask extends Task {
    Integer idOfEpic;

    public SubTask(Integer id, String name, String description, Status status, Integer idOfEpic) {
        super(id, name, description, status);
        this.idOfEpic = idOfEpic;
    }

    @Override
    public String toString() {
        String result = "SubTask{" +
                "id ='" + id + '\'' +
                ", name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", status ='" + status + '\'' +
                ", idOfEpic ='" + idOfEpic + '\'' + "}";
        return result;
    }
}
