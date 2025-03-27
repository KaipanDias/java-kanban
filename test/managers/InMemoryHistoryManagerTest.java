package managers;


import managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager inMemoryTaskManager;
    HistoryManager inMemoryHistoryManager;
    int currentIdOfTask;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        inMemoryTaskManager = Managers.getDefault();
        currentIdOfTask = 0;
    }

    public Task createTask() {
        currentIdOfTask++;
        Task task = new Task(currentIdOfTask, "Task 1", "Task description 1");
        inMemoryTaskManager.addNewTask(task);
        return task;
    }

    public Epic createEpic() {
        currentIdOfTask++;
        Epic epic = new Epic(currentIdOfTask, "Epic 1", "Epic description 1");
        inMemoryTaskManager.addNewEpic(epic);
        return epic;
    }

    public Subtask createSubTask(Epic epic) {
        currentIdOfTask++;
        Subtask subTask = new Subtask(currentIdOfTask, "SubTask 1", "SubTask description 1", epic.getStatus(), epic.getId());
        inMemoryTaskManager.addNewSubtask(subTask);

        return subTask;
    }

    @Test
    public void addTaskToHistoryTest() {
        Task task = createTask();
        Task task2 = createTask();


        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task2.getId());

        assertEquals(inMemoryTaskManager.getHistory().getLast(), task2, "Задача не попала в историю");
        assertNotEquals(inMemoryTaskManager.getHistory().getFirst(), task2, "Неправильный порядок просмотра истории");
    }


    @Test
    public void addEpicToHistoryTest() {
        Epic epic = createEpic();

        inMemoryTaskManager.getEpicById(epic.getId());

        inMemoryTaskManager.getHistory();
        assertEquals(inMemoryTaskManager.getHistory().getLast(), epic, "Задача не попала в историю");
    }

    @Test
    public void addSubTaskToHistoryTest() {
        Epic epic = createEpic();
        Subtask subTask = createSubTask(epic);

        inMemoryTaskManager.getSubtaskById(subTask.getId());

        assertEquals(inMemoryTaskManager.getHistory().getLast(), subTask, "Задача не попала в историю");
    }

    @Test
    public void addTaskToLastAndDeleteFirstTest() {
        Task task = createTask();
        Epic epic = createEpic();
        ArrayList<Task> watchedHistory = new ArrayList<>();


        watchedHistory.add(task);
        watchedHistory.add(epic);

        for (int i = 0; i < 5; i++) {
            inMemoryTaskManager.getTaskById(task.getId());
            inMemoryTaskManager.getEpicById(epic.getId());
        }
        inMemoryTaskManager.getEpicById(epic.getId());


        assertEquals(inMemoryTaskManager.getHistory().getLast(), epic, "Задача не попала в историю");
        assertEquals(inMemoryTaskManager.getHistory().getFirst(), task, "Задача не попала в историю");
        assertEquals(watchedHistory.size(), inMemoryTaskManager.getHistory().toArray().length);
    }


    @Test
    public void addAllTaskToHistoryTest() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subTask = createSubTask(epic);
        ArrayList<Task> watchedHistory = new ArrayList<>();


        for (int i = 0; i < 10; i++) {
            inMemoryTaskManager.getTaskById(task.getId());
            inMemoryTaskManager.getEpicById(epic.getId());
            inMemoryTaskManager.getSubtaskById(subTask.getId());
        }
        watchedHistory.add(task);
        watchedHistory.add(epic);
        watchedHistory.add(subTask);

        assertArrayEquals(watchedHistory.toArray(), inMemoryTaskManager.getHistory().toArray());
    }

    @Test
    public void removeFromHistoryTest() {
        Task task1 = createTask();
        Task task2 = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubTask(epic);

        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subtask);

        inMemoryHistoryManager.remove(task1.getId());
        inMemoryHistoryManager.remove(subtask.getId());
        inMemoryHistoryManager.remove(epic.getId());

        assertFalse(inMemoryTaskManager.getHistory().contains(task1));
        assertFalse(inMemoryTaskManager.getHistory().contains(subtask));
        assertFalse(inMemoryTaskManager.getHistory().contains(epic));
    }

    @Test
    public void historyDoesNotContainsDuplicatesTest() {
        Task task = createTask();
        Task task1 = createTask();
        Task task2 = createTask();

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task);


        assertEquals(3, inMemoryHistoryManager.getHistory().size());
    }
}