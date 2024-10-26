package model;

import java.util.ArrayList;

public class Epic extends Task{

    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{"  +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}