package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        taskManager.addNewTask(task1);

        Epic epic = new Epic("Epic name", "Epic description");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic.getStatus(), epic.getId());
        taskManager.addNewSubtask(subtask);

        FileBackedTaskManager taskManager1 = new FileBackedTaskManager(testFile);

        taskManager1 = taskManager1.loadFromFile(testFile);

        Task loadedFromFileTask = taskManager1.getTasks().getFirst();
        Epic loadedFromFileEpic = taskManager1.getEpics().getFirst();
        Subtask loadedFromFileSubtask = taskManager1.getSubtasks().getFirst();

        //Проверка восстановление таска
        assertEquals(task1.getId(), loadedFromFileTask.getId());
        assertEquals(task1.getName(), loadedFromFileTask.getName());
        assertEquals(task1.getDescription(), loadedFromFileTask.getDescription());
        assertEquals(task1.getStatus(), loadedFromFileTask.getStatus());

        //Проверка восстановления эпика

        assertEquals(epic.getId(), loadedFromFileEpic.getId());
        assertEquals(epic.getName(), loadedFromFileEpic.getName());
        assertEquals(epic.getDescription(), loadedFromFileEpic.getDescription());
        assertEquals(epic.getStatus(), loadedFromFileEpic.getStatus());

        assertEquals(epic.getSubtasks().getFirst().getId(), loadedFromFileEpic.getSubtasks().getFirst().getId());
        assertEquals(epic.getSubtasks().getFirst().getName(), loadedFromFileEpic.getSubtasks().getFirst().getName());
        assertEquals(epic.getSubtasks().getFirst().getDescription(), loadedFromFileEpic.getSubtasks().getFirst().getDescription());
        assertEquals(epic.getSubtasks().getFirst().getStatus(), loadedFromFileEpic.getSubtasks().getFirst().getStatus());
        assertEquals(epic.getSubtasks().getFirst().getEpicId(), loadedFromFileEpic.getSubtasks().getFirst().getEpicId());

        //Проверка восстановление подзадачи
        assertEquals(subtask.getId(), loadedFromFileSubtask.getId());
        assertEquals(subtask.getName(), loadedFromFileSubtask.getName());
        assertEquals(subtask.getDescription(), loadedFromFileSubtask.getDescription());
        assertEquals(subtask.getStatus(), loadedFromFileSubtask.getStatus());
        assertEquals(subtask.getEpicId(), loadedFromFileSubtask.getEpicId());

    }

    @Test
    public void testSaveAndGetWithTime() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        File testFile = File.createTempFile("testFile", ".csv");
        testFile.deleteOnExit();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        Task task1 = new Task("Task name", "Task description");
        task1.setStartTime(LocalDateTime.parse("28.03.2025 00:01", formatter));
        task1.setDuration(Duration.ofMinutes(1));
        taskManager.addNewTask(task1);

        Epic epic = new Epic("Epic name", "Epic description");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic.getStatus(), epic.getId());
        subtask.setStartTime(LocalDateTime.parse("28.03.2025 00:20", formatter));
        subtask.setDuration(Duration.ofMinutes(10));
        taskManager.addNewSubtask(subtask);

        FileBackedTaskManager loadedTasks = new FileBackedTaskManager(testFile);

        loadedTasks = loadedTasks.loadFromFile(testFile);

        Task loadedFromFileTask = loadedTasks.getTasks().getFirst();
        Epic loadedFromFileEpic = loadedTasks.getEpics().getFirst();
        Subtask loadedFromFileSubtask = loadedTasks.getSubtasks().getFirst();

        //Проверка восстановление таска
        assertEquals(task1.getId(), loadedFromFileTask.getId());
        assertEquals(task1.getName(), loadedFromFileTask.getName());
        assertEquals(task1.getDescription(), loadedFromFileTask.getDescription());
        assertEquals(task1.getStatus(), loadedFromFileTask.getStatus());
        assertEquals(task1.getStartTime(), loadedFromFileTask.getStartTime());
        assertEquals(task1.getDuration(), loadedFromFileTask.getDuration());

        //Проверка восстановления эпика

        assertEquals(epic.getId(), loadedFromFileEpic.getId());
        assertEquals(epic.getName(), loadedFromFileEpic.getName());
        assertEquals(epic.getDescription(), loadedFromFileEpic.getDescription());
        assertEquals(epic.getStatus(), loadedFromFileEpic.getStatus());
        assertEquals(epic.getStartTime(), loadedFromFileEpic.getStartTime());
        assertEquals(epic.getDuration(), loadedFromFileEpic.getDuration());

        assertEquals(epic.getSubtasks().getFirst().getId(), loadedFromFileEpic.getSubtasks().getFirst().getId());
        assertEquals(epic.getSubtasks().getFirst().getName(), loadedFromFileEpic.getSubtasks().getFirst().getName());
        assertEquals(epic.getSubtasks().getFirst().getDescription(), loadedFromFileEpic.getSubtasks().getFirst().getDescription());
        assertEquals(epic.getSubtasks().getFirst().getStatus(), loadedFromFileEpic.getSubtasks().getFirst().getStatus());
        assertEquals(epic.getSubtasks().getFirst().getEpicId(), loadedFromFileEpic.getSubtasks().getFirst().getEpicId());
        assertEquals(epic.getSubtasks().getFirst().getStartTime(), loadedFromFileEpic.getSubtasks().getFirst().getStartTime());
        assertEquals(epic.getSubtasks().getFirst().getDuration(), loadedFromFileEpic.getSubtasks().getFirst().getDuration());

        //Проверка восстановление подзадачи
        assertEquals(subtask.getId(), loadedFromFileSubtask.getId());
        assertEquals(subtask.getName(), loadedFromFileSubtask.getName());
        assertEquals(subtask.getDescription(), loadedFromFileSubtask.getDescription());
        assertEquals(subtask.getStatus(), loadedFromFileSubtask.getStatus());
        assertEquals(subtask.getEpicId(), loadedFromFileSubtask.getEpicId());
        assertEquals(subtask.getStartTime(), loadedFromFileSubtask.getStartTime());
        assertEquals(subtask.getDuration(), loadedFromFileSubtask.getDuration());

    }
}