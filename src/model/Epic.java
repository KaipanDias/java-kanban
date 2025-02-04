package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subTasksIds;

    public Epic(String name, String description, int id, List<Integer> subTasksIds) {
        super(name, description, id);
        this.subTasksIds = subTasksIds;
        this.taskStatus = getTaskStatus();
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(List<Integer> subTasks) {
        this.subTasksIds = subTasks;
    }

    public void addSubTaskId(Integer subTaskId){
        this.subTasksIds.add(subTaskId);
    }

    public void removeSubTaskId(Integer subTaskId){
        this.subTasksIds.remove(subTaskId);
    }

    public void setTaskStatus(List<Subtask> subTasks) {
        ArrayList<Status> subTaskStatuses = new ArrayList<>();
        for (Subtask subTask : subTasks) {
            subTaskStatuses.add(subTask.taskStatus);
        }
        if (subTaskStatuses.stream().allMatch(element -> element.equals(Status.NEW))) {
            System.out.println("Все подзадачи равны NEW");
            this.taskStatus = Status.NEW;
        } else if ((subTaskStatuses.stream().allMatch(element -> element.equals(Status.DONE)))) {
            System.out.println("Все подзадачи равны DONE");
            this.taskStatus = Status.DONE;
        } else {
            this.taskStatus = Status.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return ("id=" + getId() + " name=" + getName() + " description=" + getDescription() + " taskStatus=" + taskStatus + " subTasksIds=" + getSubTasksIds());
    }
}
