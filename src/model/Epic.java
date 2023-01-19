package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {

    HashMap<Integer, SubTask> mapOfSubTasks = new HashMap<>();

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void setMapOfSubTasks(HashMap<Integer, SubTask> mapOfSubTasks) {
        this.mapOfSubTasks = mapOfSubTasks;
    }

    public HashMap<Integer, SubTask> getMapOfSubTasks() {
        return mapOfSubTasks;
    }

    public Long calcDuration(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime != null && endTime != null) {
            Duration durationEpic = Duration.between(startTime, endTime);
            duration = durationEpic.toMinutes();
            return duration;
        } else {
            duration = null;
            return duration;
        }
    }

    public LocalDateTime calcStartTime(HashMap<Integer, SubTask> mapOfSubTasksDub) {
        LocalDateTime resultOfSearchStartTime = LocalDateTime.of(2999, 12, 31, 23, 59, 59, 59);
        if (!mapOfSubTasksDub.isEmpty()) {
            for (SubTask subTask : mapOfSubTasksDub.values()) {
                LocalDateTime timeForCompare = subTask.getStartTime();
                if (timeForCompare.isBefore(resultOfSearchStartTime)) {
                    resultOfSearchStartTime = timeForCompare;
                }
            }
        } else {
            return null;
        }
        return resultOfSearchStartTime;
    }

    public LocalDateTime calcEndTime(HashMap<Integer, SubTask> mapOfSubTasksDub) {
        LocalDateTime resultOfSearchEndTime = LocalDateTime.of(999, 12, 31, 23, 59, 59, 59);
        if (!mapOfSubTasksDub.isEmpty()) {
            for (SubTask subTask : mapOfSubTasksDub.values()) {
                LocalDateTime timeForCompare = subTask.getEndTime(subTask.getDuration(), subTask.getStartTime());
                if (timeForCompare.isAfter(resultOfSearchEndTime)) {
                    resultOfSearchEndTime = timeForCompare;
                }
            }
        } else {
            return null;
        }
        return resultOfSearchEndTime;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id ='" + id + '\'' +
                ", name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", status ='" + status + '\'' +
                ", duration ='" + duration + '\'' +
                ", startTime ='" + startTime + '\'' +
                ", endTime ='" + endTime + '\'' + "}";
        return result;
    }

    public String epicToString(Epic epic) {

        String result = epic.id + ","
                + TaskType.EPIC + ","
                + epic.name + ","
                + epic.status + ","
                + epic.description + ","
                + epic.duration + ","
                + epic.startTime + ","
                + epic.endTime + "\n";

        return result;
    }
}
