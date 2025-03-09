package managers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.util.List;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
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

    public Epic createEpic() {
        currentIdOfTask++;
        Epic epic = new Epic(currentIdOfTask, "Epic 1", "Epic description 1");
        inMemoryTaskManager.addNewEpic(epic);
        return epic;
    }

    public Subtask createSubTask(Epic epic) {
        currentIdOfTask++;
        Subtask subTask = new Subtask(currentIdOfTask, "SubTask 1", "SubTask description 1", epic.getStatus(),epic.getId());
        inMemoryTaskManager.addNewSubtask(subTask);

        return subTask;
    }

    @Test
    public void should_create_and_return_task() {
        Task task = createTask();

        Task createdTask = inMemoryTaskManager.getTaskById(currentIdOfTask);
        assertEquals(task, createdTask, "Задача не создалась");
    }

    @Test
    public void should_create_and_return_epic() {
        Epic epic = createEpic();

        Epic createdEpic = inMemoryTaskManager.getEpicById(currentIdOfTask);
        assertEquals(epic, createdEpic, "Эпик не создался");
    }

    @Test
    public void should_create_and_return_subTask() {
        Epic epic = createEpic();
        Subtask subTask = createSubTask(epic);

        Subtask createdSubTask = inMemoryTaskManager.getSubtaskById(currentIdOfTask);
        assertEquals(subTask, createdSubTask, "Задача не создалась");
    }

    @Test
    public void should_return_list_of_tasks() {
        Task task1 = createTask();

        Task task2 = createTask();

        Task task3 = createTask();

        List<Task> createdTasks = new ArrayList<>(3);
        createdTasks.add(task1);
        createdTasks.add(task2);
        createdTasks.add(task3);

        assertArrayEquals(createdTasks.toArray(), inMemoryTaskManager.getTasks().toArray());
    }

    @Test
    public void should_return_list_of_epics() {
        Epic epic1 = createEpic();

        Epic epic2 = createEpic();

        Epic epic3 = createEpic();

        List<Epic> createdEpics = new ArrayList<>(3);
        createdEpics.add(epic1);
        createdEpics.add(epic2);
        createdEpics.add(epic3);

        assertArrayEquals(createdEpics.toArray(), inMemoryTaskManager.getEpics().toArray());
    }

    @Test
    public void should_return_list_of_subtasks() {
        Epic mainEpic = createEpic();

        Subtask subTask1 = createSubTask(mainEpic);

        Subtask subTask2 = createSubTask(mainEpic);

        Subtask subTask3 = createSubTask(mainEpic);

        List<Subtask> createdSubTasks = new ArrayList<>(3);
        createdSubTasks.add(subTask1);
        createdSubTasks.add(subTask2);
        createdSubTasks.add(subTask3);

        assertArrayEquals(createdSubTasks.toArray(), inMemoryTaskManager.getSubtasks().toArray());
    }

    @Test
    public void should_update_task() {
        Task task = createTask();

        task.setName("Updated Task 1 Name");
        task.setDescription("Updated Task 1 description");
        task.setStatus(Status.DONE);

        inMemoryTaskManager.updateTask(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(task.getId()));
    }

    @Test
    public void should_update_epic() {
        Epic epic = createEpic();

        Subtask subTask = createSubTask(epic);
        ArrayList<Integer> subTasks = new ArrayList<>(1);
        subTasks.add(subTask.getId());

        epic.setName("Updated Task 1 Name");
        epic.setDescription("Updated Task 1 description");

        inMemoryTaskManager.updateEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpicById(epic.getId()));
    }

    @Test
    public void should_update_subtask_and_update_epic_task_status() {
        Epic mainEpic = createEpic();

        Subtask subTask = createSubTask(mainEpic);

        subTask.setName("SubTask Updated Name 1");
        subTask.setDescription("SubTask Updated Description 1");
        subTask.setStatus(Status.DONE);

        inMemoryTaskManager.updateSubtask(subTask);

        assertEquals(subTask, inMemoryTaskManager.getSubtaskById(subTask.getId()));
        assertEquals(Status.DONE, mainEpic.getStatus());
    }

    @Test
    public void should_delete_task() {
        Task task = createTask();

        inMemoryTaskManager.deleteTaskById(task.getId());

        assertNull(inMemoryTaskManager.getTaskById(task.getId()));
    }

    @Test
    public void should_delete_epic() {
        Epic epic = createEpic();

        inMemoryTaskManager.deleteEpicById(epic.getId());

        assertNull(inMemoryTaskManager.getEpicById(epic.getId()));
    }

    @Test
    public void should_delete_subtask() {
        Epic epic = createEpic();
        Subtask subTask = createSubTask(epic);

        inMemoryTaskManager.deleteSubtaskById(subTask.getId());

        assertNull(inMemoryTaskManager.getSubtaskById(subTask.getId()));
    }


    @Test
    public void should_delete_all_tasks() {
        for (int i = 0; i < 10; i++) {
            Task task = createTask();
        }
        inMemoryTaskManager.deleteAllTasks();

        assertEquals(0, inMemoryTaskManager.getTasks().toArray().length);
    }

    @Test
    public void should_delete_all_epics() {
        for (int i = 0; i < 10; i++) {
            Epic epic = createEpic();
        }
        inMemoryTaskManager.deleteAllEpics();

        assertEquals(0, inMemoryTaskManager.getTasks().toArray().length);
    }

    @Test
    public void should_delete_all_subtasks() {
        Epic epic = createEpic();
        for (int i = 0; i < 10; i++) {
            Subtask subTask = createSubTask(epic);
        }
        inMemoryTaskManager.deleteAllSubtasks();

        assertEquals(0, inMemoryTaskManager.getTasks().toArray().length);

    }

    @Test
    public void subtasksShouldNotContainDeletedSubtaskIs(){//Удаляемые подзадачи не должны хранить внутри себя старые id.
        Epic epic = createEpic();
        Subtask subtask1 = createSubTask(epic);
        Subtask subtask2 = createSubTask(epic);

        int subtaskIdToDelete = subtask1.getId();

        inMemoryTaskManager.deleteSubtaskById(subtaskIdToDelete);

        assertNull(inMemoryTaskManager.getSubtaskById(subtaskIdToDelete));
    }

    @Test
    public void epicShouldNotContainDeletedSubtaskId(){//Внутри эпиков не должно оставаться неактуальных id подзадач.
        Epic epic = createEpic();
        Subtask subtask1 = createSubTask(epic);
        Subtask subtask2 = createSubTask(epic);

        int subtaskIdToDelete = subtask1.getId();

        inMemoryTaskManager.deleteSubtaskById(subtaskIdToDelete);

        assertFalse(inMemoryTaskManager.getEpicById(epic.getId()).getSubtasks().contains(subtask1));
    }
}