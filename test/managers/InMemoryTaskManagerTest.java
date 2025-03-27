package managers;


import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public void getTaskManager() {
        new InMemoryTaskManager();
    }

    @BeforeEach
    void setUp() {
        getTaskManager();
    }
}