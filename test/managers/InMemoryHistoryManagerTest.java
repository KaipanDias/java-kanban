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
        Task task = new Task(currentIdOfTask,"Task 1", "Task description 1");
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
        Subtask subTask = new Subtask(currentIdOfTask, "SubTask 1", "SubTask description 1", epic.getStatus(),epic.getId());
        inMemoryTaskManager.addNewSubtask(subTask);

        return subTask;
    }

    @Test
    public void should_add_task_to_history() {
        Task task = createTask();

        inMemoryTaskManager.getTaskById(task.getId());


        assertEquals(inMemoryHistoryManager.getHistory().getLast(), task, "Задача не попала в историю");
    }

    @Test
    public void should_add_epic_to_history() {
        Epic epic = createEpic();

        inMemoryTaskManager.getEpicById(epic.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), epic, "Задача не попала в историю");
    }

    @Test
    public void should_add_subtask_to_history() {
        Epic epic = createEpic();
        Subtask subTask = createSubTask(epic);

        inMemoryTaskManager.getSubtaskById(subTask.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), subTask, "Задача не попала в историю");
    }

    @Test
    public void should_add_task_to_last_and_delete_first() {
        Task task = createTask();
        Epic epic = createEpic();

        for (int i = 0; i < 5; i++) {
            inMemoryTaskManager.getTaskById(task.getId());
            inMemoryTaskManager.getEpicById(epic.getId());
        }
        inMemoryTaskManager.getEpicById(epic.getId());


        assertEquals(inMemoryHistoryManager.getHistory().getLast(), epic, "Задача не попала в историю");
        assertEquals(inMemoryHistoryManager.getHistory().getFirst(), epic, "Задача не попала в историю");
        assertEquals(10, inMemoryHistoryManager.getHistory().toArray().length);
    }


    @Test
    public void should_add_all_tasks_to_history() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subTask = createSubTask(epic);
        ArrayList<Task> watchedHistory = new ArrayList<>(10);

        for (int i = 0; i < 3; i++) {
            inMemoryTaskManager.getTaskById(task.getId());
            inMemoryTaskManager.getEpicById(epic.getId());
            inMemoryTaskManager.getSubtaskById(subTask.getId());
            watchedHistory.add(task);
            watchedHistory.add(epic);
            watchedHistory.add(subTask);
        }

        inMemoryTaskManager.getTaskById(task.getId());
        watchedHistory.add(task);

        assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
    }

}