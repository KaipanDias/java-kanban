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

    public void clearEpicSubtasks(){
        ArrayList<Subtask> epicSubtasks;
        for(Subtask subtask : subtasks.values()){
            epicSubtasks = epics.get(subtask.getEpicId()).getSubtasks();
            epicSubtasks.remove(subtask);
            updateEpic(epics.get(subtask.getEpicId()));
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
        updateEpic(epic);
    }

    public void deleteEpicById(int id) {
        deleteSubtasksByEpicId(id);
        epics.remove(id);
    }

    private void deleteSubtasksByEpicId(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks();

        for (Subtask epicSubtask : oldSubtasks) {
            subtasks.remove(epicSubtask.getId());
        }
    }

    //ADD
    public int addNewTask(Task task) {
        id++;
        task.setId(id);
        tasks.put(id, task);

        return id;
    }

    public Integer addNewSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())){
            return null;
        }
        id++;
        subtask.setId(id);
        subtasks.put(id, subtask);
        addSubtaskToEpic(subtask);
        return id;
    }

    public void addSubtaskToEpic(Subtask subtask){
        ArrayList<Subtask> epicSubtasks = epics.get(subtask.getEpicId()).getSubtasks();
        epicSubtasks.add(subtask);
        updateEpic(epics.get(subtask.getEpicId()));
    }

    public int addNewEpic(Epic epic) {
        id++;
        epic.setId(id);
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
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId()); // получение эпика сабтаска
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks(); // получение всех сабтасков эпика
        Subtask epicSubtaskOld = new Subtask(subtask.getEpicId());

        for (Subtask epicSubtask : oldSubtasks) {
            if (epicSubtask.equals(subtask)) {
                epicSubtaskOld = epicSubtask;
            }
        }

        if (epicSubtaskOld != null) {
            oldSubtasks.remove(epicSubtaskOld);
        }
        oldSubtasks.add(subtask);
        updateEpic(epic);
    }

    public void updateEpic(Epic epic) {
        if(!epics.containsKey(epic.getId())){
            return;
        }
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());

        updateEpicStatus(epic);
    }

    private void updateEpicStatus(Epic epic){
        if(!epics.containsKey(epic.getId())){
            return;
        }

        int doneSubtasksCnt = 0; // кол-во сабтасков со стасусом дан

        ArrayList<Subtask> oldEpicSubtasks = epics.get(epic.getId()).getSubtasks();

        for (Subtask subtask : oldEpicSubtasks) {
            if (subtask.getStatus() == Status.DONE) {
                doneSubtasksCnt++;
            }
        }

        if (oldEpicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        } else if (doneSubtasksCnt == oldEpicSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }
}