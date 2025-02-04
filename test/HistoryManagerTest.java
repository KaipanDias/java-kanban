import managers.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Subtask;
import model.Task;
import managers.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {
    static TaskManager inMemoryTaskManager;
    static HistoryManager inMemoryHistoryManager;
    static int currentIdOfTask;

    @BeforeEach
    public void beforeEach() {

        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault(inMemoryHistoryManager);
        currentIdOfTask = 0;
    }

    public Task createTask() {
        currentIdOfTask++;
        Task task = new Task("Task 1", "Task description 1", currentIdOfTask);
        inMemoryTaskManager.createTask(task);
        return task;
    }

    public Epic createEpic() {
        currentIdOfTask++;
        Epic epic = new Epic("Epic 1", "Epic description 1", currentIdOfTask, new ArrayList<>(0));
        inMemoryTaskManager.createEpic(epic);
        return epic;
    }

    public Subtask createSubTask(Epic epic) {
        currentIdOfTask++;
        Subtask subTask = new Subtask("SubTask 1", "SubTask description 1", currentIdOfTask, epic.getId());
        inMemoryTaskManager.createSubTask(subTask);

        return subTask;
    }

    @Test
    public void should_add_task_to_history() {
        Task task = createTask();

        inMemoryTaskManager.getTask(task.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), task, "Задача не попала в историю");
    }

    @Test
    public void should_add_epic_to_history() {
        Epic epic = createEpic();

        inMemoryTaskManager.getEpic(epic.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), epic, "Задача не попала в историю");
    }

    @Test
    public void should_add_subtask_to_history() {
        Epic epic = createEpic();
        Subtask subTask = createSubTask(epic);

        inMemoryTaskManager.getSubTask(subTask.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), subTask, "Задача не попала в историю");
    }

    @Test
    public void should_add_task_to_last_and_delete_first() {
        Task task = createTask();
        Epic epic = createEpic();

        for (int i = 0; i < 5; i++) {
            inMemoryTaskManager.getTask(task.getId());
            inMemoryTaskManager.getEpic(epic.getId());
        }
        inMemoryTaskManager.getEpic(epic.getId());


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
            inMemoryTaskManager.getTask(task.getId());
            inMemoryTaskManager.getEpic(epic.getId());
            inMemoryTaskManager.getSubTask(subTask.getId());
            watchedHistory.add(task);
            watchedHistory.add(epic);
            watchedHistory.add(subTask);
        }

        inMemoryTaskManager.getTask(task.getId());
        watchedHistory.add(task);

        assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
    }


}
