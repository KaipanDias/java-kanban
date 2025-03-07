package managers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //GET
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasksById(int id){
        if(!epics.containsKey(id)){
            return null;
        }
        Epic epicSubtasks = epics.get(id);
        return  new ArrayList<>(epicSubtasks.getSubtasks());
    }

    //DELETE
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()){
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }


    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks();

        for (Subtask epicSubtask : oldSubtasks) {
            subtasks.remove(epicSubtask.getId());
        }
        epics.remove(id);
    }

    //ADD
    @Override
    public int addNewTask(Task task) {
        task.setId(++id);
        tasks.put(id, task);

        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())){
            return null;
        }
        subtask.setId(++id);
        subtasks.put(id, subtask);
        ArrayList<Subtask> epicSubtasks = epics.get(subtask.getEpicId()).getSubtasks();
        epicSubtasks.add(subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(++id);
        epics.put(id, epic);
        return id;
    }

    //UPDATE
    @Override
    public void updateTask(Task task) {
        if(tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        }
    }

    @Override
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


    @Override
    public void updateEpic(Epic epic) {
        if(!epics.containsKey(epic.getId())){
            return;
        }
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    private void updateEpicStatus(Epic epic){
        if(!epics.containsKey(epic.getId())){
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

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
}