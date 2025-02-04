package managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubTask(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(Subtask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(Subtask subTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

}