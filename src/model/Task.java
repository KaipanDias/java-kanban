package model;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status taskStatus;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        taskStatus = Status.NEW;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return ("id=" + getId() + " name=" + getName() + " description=" + getDescription() + " taskStatus=" + taskStatus);
    }
}


