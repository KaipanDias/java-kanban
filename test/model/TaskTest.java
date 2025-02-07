package model;

import managers.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class TaskTest {
    TaskManager inMemoryTaskManager;
    HistoryManager inMemoryHistoryManager;
    int currentIdOfTask;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault();
        currentIdOfTask = 0;
    }

    public Task createTask() {
        currentIdOfTask++;
        Task task = new Task(currentIdOfTask,"Task 1", "Task description 1");
        inMemoryTaskManager.addNewTask(task);
        return task;
    }

    @Test
    public void task_equals_if_same_id(){
        Task task1 = createTask();
        Task task2 = createTask();

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }
}