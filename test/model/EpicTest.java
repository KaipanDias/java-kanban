package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    int currentIdOfTask = 0;

    public Epic createEpic() {
        currentIdOfTask++;
        return new Epic(currentIdOfTask, "Epic 1", "Epic description 1");
    }

    @Test
    public void epic_equals_if_same_id() {
        Task epic1 = createEpic();
        Task epic2 = createEpic();

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }
}