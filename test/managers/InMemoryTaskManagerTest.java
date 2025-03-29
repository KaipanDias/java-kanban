package managers;


import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    public void setUp() {
        taskManager = getTaskManager();
    }
}