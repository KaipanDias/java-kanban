package model;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        setStatus(Status.NEW);
    }

    public Subtask(int epicId) {
        this.epicId = epicId;
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
                ", status=" + status +
                '}';
    }
}