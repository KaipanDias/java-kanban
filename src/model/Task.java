package model;

import java.util.Objects;
import java.time.*;

public class Task {
    protected int id;

    public TaskType getTaskType() {
        return taskType;
    }

    protected TaskType taskType;
    protected String name;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status) {
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(0);
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(0);
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(0);
    }

    public Task(String name, String description) {
        this.name = name;
        this.taskType = TaskType.TASK;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(0);
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.taskType = TaskType.TASK;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}