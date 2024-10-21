import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
public class TaskManager {
    private static int id = 1;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    //GET
    public ArrayList<Task> getTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        for (Task task : tasks.values()) {
            allTasks.add(task);
        }

        return allTasks;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(subtask);
        }

        return allSubtasks;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();

        for (Epic epic : epics.values()) {
            allEpics.add(epic);
        }

        return allEpics;
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



    //DELETE
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setSubtasks(new ArrayList<>());
            updateEpic(epic);
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

        Epic epic = epics.get(subtasks.get(id).getEpicId());

        ArrayList<Subtask> oldSubtasks = epic.getSubtasks();
        Subtask oldSubtask = subtasks.get(id);
        Subtask subtaskToBeDeleted = new Subtask();

        for (Subtask epicSubtask : oldSubtasks) {
            if (epicSubtask.equals(oldSubtask)) {
                subtaskToBeDeleted = epicSubtask;
            }
        }

        if (subtaskToBeDeleted != null) {
            oldSubtasks.remove(subtaskToBeDeleted);
        }

        subtasks.remove(id);
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
        oldSubtasks.clear();
    }

    //ADD
    public void addNewTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        id = id + 1;
    }

    public void addNewSubtask(Subtask subtask) {
        subtask.setId(id);
        subtasks.put(id, subtask);
        id = id + 1;
        ArrayList<Subtask> epicSubtasks = epics.get(subtask.getEpicId()).getSubtasks();
        epicSubtasks.add(subtask);
        updateEpic(epics.get(subtask.getEpicId()));
    }

    public void addNewEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        id = id + 1;
    }

    //UPDATE
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {

        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId()); // получение эпика сабтаска
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks(); // получение всех сабтасков эпика
        Subtask epicSubtaskOld = new Subtask();

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
    
    private void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        int newSubtasksCnt = 0; // кол-во сабтасков со стасусом нью

        int doneSubtasksCnt = 0; // кол-во сабтасков со стасусом дан

        ArrayList<Subtask> oldEpicSubtasks = epics.get(epic.getId()).getSubtasks();

        for (Subtask subtask : oldEpicSubtasks) {
            if (subtask.getStatus() == Status.NEW) {
                newSubtasksCnt++;
            } else if (subtask.getStatus() == Status.DONE) {
                doneSubtasksCnt++;
            }
        }

        if (newSubtasksCnt == oldEpicSubtasks.size()){
            epic.setStatus(Status.NEW);
        } else if (oldEpicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (doneSubtasksCnt == oldEpicSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}