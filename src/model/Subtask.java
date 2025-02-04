package model;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, int id, Integer epicId) {
        super(name, description, id);
        this.epicId = epicId;
        taskStatus = Status.NEW;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return ("id=" + getId() + " name=" + getName() + " description=" + getDescription() + " taskStatus=" + taskStatus + " epicId=" + getEpicId());
    }
}
