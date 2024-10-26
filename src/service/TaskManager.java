package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    //GET
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public ArrayList<Subtask> getAllEpicSubtasksById(int id){
        if(!epics.containsKey(id)){
            return new ArrayList<>();
        }
        Epic epicSubtasks = epics.get(id);
        return  new ArrayList<>(epicSubtasks.getSubtasks());
    }

    //DELETE
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        clearEpicSubtasks();
        subtasks.clear();
    }

    private void clearEpicSubtasks(){
        for (Epic epic : epics.values()){
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)){
            return;
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        Subtask subtask = subtasks.get(id);
        epic.getSubtasks().remove(subtask);
        updateEpicStatus(epic);

        subtasks.remove(id);
    }

    public void deleteEpicById(int id) {
        deleteSubtasksByEpicId(id);
        epics.remove(id);
    }

    private void deleteSubtasksByEpicId(int id) {
        if (!epics.containsKey(id)){
            return;
        }
        Epic epic = epics.get(id);
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks();

        for (Subtask epicSubtask : oldSubtasks) {
            subtasks.remove(epicSubtask.getId());
        }
    }

    //ADD
    public int addNewTask(Task task) {
        task.setId(++id);
        tasks.put(id, task);

        return id;
    }

    public Integer addNewSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())){
            return null;
        }
        subtask.setId(++id);
        subtasks.put(id, subtask);
        addSubtaskToEpic(subtask);
        return id;
    }

    private void addSubtaskToEpic(Subtask subtask){
        ArrayList<Subtask> epicSubtasks = epics.get(subtask.getEpicId()).getSubtasks();
        epicSubtasks.add(subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    public int addNewEpic(Epic epic) {
        epic.setId(++id);
        epics.put(id, epic);
        return id;
    }

    //UPDATE
    public void updateTask(Task task) {
        if(!tasks.containsKey(task.getId())){
            return;
        }else{
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if(!subtasks.containsKey(subtask.getId())){
            return;
        }

        Epic epic = epics.get(subtask.getEpicId()); // получение эпика сабтаска
        epic.getSubtasks().remove(subtask);
        epic.getSubtasks().add(subtask);
        updateEpicStatus(epic);

        subtasks.put(subtask.getId(), subtask);
    }


    public void updateEpic(Epic epic) {
        if(!epics.containsKey(epic.getId())){
            return;
        }
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    private void updateEpicStatus(Epic epic){
        if(!epics.containsKey(epic.getId())){
            epic.setStatus(Status.NEW);
            return;
        }

        ArrayList<Subtask> oldEpicSubtasks = epics.get(epic.getId()).getSubtasks();
        if (oldEpicSubtasks.isEmpty()){
            epic.setStatus(Status.NEW);
            return;
        }

        int doneSubtasksCnt = 0; // кол-во сабтасков со стасусом дан
        int newSubtasksCnt = 0;

        for (Subtask subtask : oldEpicSubtasks) {
            if (subtask.getStatus() == Status.NEW) {
                newSubtasksCnt++;
            } else if (subtask.getStatus() == Status.DONE) {
                doneSubtasksCnt++;
            }
        }

        if (newSubtasksCnt == oldEpicSubtasks.size()){
            epic.setStatus(Status.NEW);
        } else if (doneSubtasksCnt == oldEpicSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }
}