package model;

import java.time.*;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int epicId) {
        super(id, name, description);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
        setStatus(Status.NEW);
    }

    public Subtask(String name, String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }


    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", epicId=" + epicId +
                '}';
    }
}