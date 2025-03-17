package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

class FileBackedTaskManagerTest {
    @Test
    public void saveAndLoadEmptyFile() throws IOException {
        File testFile = File.createTempFile("testFile", ".csv");
        testFile.deleteOnExit();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void testSaveAndGetTasks() throws IOException {
        File testFile = File.createTempFile("testFile", ".csv");
        testFile.deleteOnExit();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        Task task1 = new Task("Task name", "Task description");
        Task task2 = new Task("Task name", "Task description");

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic = new Epic("Epic name", "Epic description");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic.getStatus(), epic.getId());
        taskManager.addNewSubtask(subtask);

        assertEquals(2, taskManager.getTasks().size());
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());

        assertEquals(task1, taskManager.getTasks().getFirst());
        assertEquals(subtask, taskManager.getSubtasks().getFirst());
        assertEquals(epic, taskManager.getEpics().getFirst());

    }
}