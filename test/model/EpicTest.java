package model;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager inMemoryTaskManager;
    HistoryManager inMemoryHistoryManager;
    int currentIdOfTask;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault();
        currentIdOfTask = 0;
    }

    public Epic createEpic() {
        currentIdOfTask++;
        Epic epic = new Epic(currentIdOfTask, "Epic 1", "Epic description 1");
        inMemoryTaskManager.addNewEpic(epic);
        return epic;
    }

    @Test
    public void epic_equals_if_same_id(){
        Task epic1 = createEpic();
        Task epic2 = createEpic();

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }
}