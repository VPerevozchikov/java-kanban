package model;

import java.time.LocalDateTime;

public class SubTask extends Task {

    Integer idOfEpic;

    public SubTask(Integer id, String name, String description, Status status, Long duration, LocalDateTime startTime, Integer idOfEpic) {
        super(id, name, description, status, duration, startTime);
        this.idOfEpic = idOfEpic;
    }

    public Integer getIdOfEpic() {
        return idOfEpic;
    }

    @Override
    public String toString() {
        String result = "SubTask{" +
                "id ='" + id + '\'' +
                ", name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", status ='" + status + '\'' +
                ", duration ='" + duration + '\'' +
                ", startTime ='" + startTime + '\'' +
                ", endTime ='" + endTime + '\'' +
                ", idOfEpic ='" + idOfEpic + '\'' + "}";
        return result;
    }
    public String subTaskToString(SubTask subTask) {

        String result = subTask.id + ","
                + TaskType.SUBTASK + ","
                + subTask.name + ","
                + subTask.status + ","
                + subTask.description + ","
                + subTask.duration + ","
                + subTask.startTime + ","
                + subTask.endTime + ","
                + subTask.idOfEpic + "\n";

        return result;
    }
}
