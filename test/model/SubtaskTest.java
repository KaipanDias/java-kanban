package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    int currentIdOfTask = 0;

    public Subtask createSubTask() {
        currentIdOfTask++;
        return new Subtask(currentIdOfTask, "SubTask 1", "SubTask description 1", Status.NEW,1);
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