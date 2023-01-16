package model;

import user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    String name;
    String description;
    Status status;
    Integer id;
    Long duration;
    LocalDateTime startTime;
    LocalDateTime endTime;

    protected User user;

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String name, String description, Status status, Long duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Integer id, String name, String description, Status status, Long duration, LocalDateTime startTime, User user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.user = user;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public String getDescription() {
        return description;
    }

    public LocalDateTime getEndTime(Long duration, LocalDateTime startTime) {
        endTime = startTime.plusMinutes(duration);
        return endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, name, description, status);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "id ='" + id + '\'' +
                ", name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", status ='" + status + '\'' +
                ", duration ='" + duration + '\'' +
                ", startTime ='" + startTime + '\'' +
                ", endTime ='" + endTime + '\'' + "}";

        return result;
    }

    public String taskToString(Task task) {

        String result = task.id + ","
                + TaskType.TASK + ","
                + task.name + ","
                + task.status + ","
                + task.description + ","
                + task.duration + ","
                + task.startTime + ","
                + task.endTime + "\n";

        return result;
    }



}




