package model;

import managers.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager inMemoryTaskManager;
    HistoryManager inMemoryHistoryManager;
    int currentIdOfTask = 0;

    public Task createTask() {
        currentIdOfTask++;
        return new Task(currentIdOfTask,"Task 1", "Task description 1");
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