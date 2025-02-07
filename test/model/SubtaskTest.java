package model;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager inMemoryTaskManager;
    HistoryManager inMemoryHistoryManager;
    int currentIdOfTask;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault();
        currentIdOfTask = 0;
    }

    public Subtask createSubTask() {
        currentIdOfTask++;
        Subtask subTask = new Subtask(currentIdOfTask, "SubTask 1", "SubTask description 1", Status.NEW,1);
        inMemoryTaskManager.addNewSubtask(subTask);

        return subTask;
    }

    @Test
    public void subtask_equals_if_same_id(){


        Task subTask1 = createSubTask();
        Task subTask2 = createSubTask();

        subTask1.setId(1);
        subTask2.setId(1);

        assertEquals(subTask1, subTask2);
    }
}