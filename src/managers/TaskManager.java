package managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //GET
    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    ArrayList<Subtask> getAllEpicSubtasksById(int id);

    //DELETE
    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    //ADD
    int addNewTask(Task task);

    Integer addNewSubtask(Subtask subtask);

    int addNewEpic(Epic epic);

    //UPDATE
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Task> getHistory();
}
