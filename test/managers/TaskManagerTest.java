package managers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
abstract class TaskManagerTest <T extends TaskManager> {
  protected T taskManager;

  public abstract T getTaskManager();


  int currentIdOfTask;

  @BeforeEach
  public void beforeEach() {
    taskManager = getTaskManager();
    currentIdOfTask = 0;
  }

  public Task createTask() {
    currentIdOfTask++;
    Task task = new Task(currentIdOfTask, "Task " + currentIdOfTask, "Task description " + currentIdOfTask);
    taskManager.addNewTask(task);
    return task;
  }

  public Epic createEpic() {
    currentIdOfTask++;
    Epic epic = new Epic(currentIdOfTask, "Epic " + currentIdOfTask, "Epic description " + currentIdOfTask);
    taskManager.addNewEpic(epic);
    return epic;
  }

  public Subtask createSubTask(Epic epic) {
    currentIdOfTask++;
    Subtask subTask = new Subtask(currentIdOfTask, "SubTask " + currentIdOfTask, "SubTask description " + currentIdOfTask, epic.getStatus(), epic.getId());
    taskManager.addNewSubtask(subTask);

    return subTask;
  }

  @Test
  public void createAndReturnTaskTest() {
    Task task = createTask();

    Task createdTask = taskManager.getTaskById(currentIdOfTask);
    assertEquals(task, createdTask, "Задача не создалась");
  }

  @Test
  public void createAndReturnEpicTest() {
    Epic epic = createEpic();

    Epic createdEpic = taskManager.getEpicById(currentIdOfTask);
    assertEquals(epic, createdEpic, "Эпик не создался");
  }

  @Test
  public void createAndReturnSubtaskTest() {
    Epic epic = createEpic();
    Subtask subTask = createSubTask(epic);

    Subtask createdSubTask = taskManager.getSubtaskById(currentIdOfTask);
    assertEquals(subTask, createdSubTask, "Задача не создалась");
  }

  @Test
  public void getTasksTest() {
    Task task1 = createTask();

    Task task2 = createTask();

    Task task3 = createTask();

    List<Task> createdTasks = new ArrayList<>(3);
    createdTasks.add(task1);
    createdTasks.add(task2);
    createdTasks.add(task3);

    assertArrayEquals(createdTasks.toArray(), taskManager.getTasks().toArray());
  }

  @Test
  public void getEpicsTest() {
    Epic epic1 = createEpic();

    Epic epic2 = createEpic();

    Epic epic3 = createEpic();

    List<Epic> createdEpics = new ArrayList<>(3);
    createdEpics.add(epic1);
    createdEpics.add(epic2);
    createdEpics.add(epic3);

    assertArrayEquals(createdEpics.toArray(), taskManager.getEpics().toArray());
  }

  @Test
  public void getSubtasksTest() {
    Epic mainEpic = createEpic();

    Subtask subTask1 = createSubTask(mainEpic);

    Subtask subTask2 = createSubTask(mainEpic);

    Subtask subTask3 = createSubTask(mainEpic);

    List<Subtask> createdSubTasks = new ArrayList<>(3);
    createdSubTasks.add(subTask1);
    createdSubTasks.add(subTask2);
    createdSubTasks.add(subTask3);

    assertArrayEquals(createdSubTasks.toArray(), taskManager.getSubtasks().toArray());
  }

  @Test
  public void updateTaskTest() {
    Task task = createTask();

    task.setName("Updated Task 1 Name");
    task.setDescription("Updated Task 1 description");
    task.setStatus(Status.DONE);

    taskManager.updateTask(task);

    assertEquals(task, taskManager.getTaskById(task.getId()));
  }

  @Test
  public void updateEpicTest() {
    Epic epic = createEpic();

    Subtask subTask = createSubTask(epic);
    ArrayList<Integer> subTasks = new ArrayList<>(1);
    subTasks.add(subTask.getId());

    epic.setName("Updated Task 1 Name");
    epic.setDescription("Updated Task 1 description");

    taskManager.updateEpic(epic);

    assertEquals(epic, taskManager.getEpicById(epic.getId()));
  }

  @Test
  public void updateSubtaskAndEpicTest() {
    Epic mainEpic = createEpic();

    Subtask subtask1 = createSubTask(mainEpic);
    Subtask subtask2 = createSubTask(mainEpic);
    Subtask subtask3 = createSubTask(mainEpic);

    subtask1.setStatus(Status.NEW);
    subtask2.setStatus(Status.NEW);
    subtask3.setStatus(Status.NEW);

    taskManager.updateSubtask(subtask1);
    taskManager.updateSubtask(subtask2);
    taskManager.updateSubtask(subtask3);
    assertEquals(mainEpic.getStatus(), Status.NEW); //  a. Все подзадачи со статусом NEW.

    subtask1.setStatus(Status.IN_PROGRESS);

    taskManager.updateSubtask(subtask1);
    assertEquals(mainEpic.getStatus(), Status.IN_PROGRESS); //  d. Подзадачи со статусом IN_PROGRESS.

    subtask1.setStatus(Status.DONE);

    taskManager.updateSubtask(subtask1);
    assertEquals(mainEpic.getStatus(), Status.IN_PROGRESS); //  c. Подзадачи со статусами NEW и DONE.

    subtask1.setStatus(Status.DONE);
    subtask2.setStatus(Status.DONE);
    subtask3.setStatus(Status.DONE);

    taskManager.updateSubtask(subtask1);
    taskManager.updateSubtask(subtask2);
    taskManager.updateSubtask(subtask3);
    assertEquals(mainEpic.getStatus(), Status.DONE);
  }

  @Test
  public void deleteTaskByIdTest() {
    Task task = createTask();

    taskManager.deleteTaskById(task.getId());

    assertNull(taskManager.getTaskById(task.getId()));
  }

  @Test
  public void deleteEpicByIdTest() {
    Epic epic = createEpic();

    taskManager.deleteEpicById(epic.getId());

    assertNull(taskManager.getEpicById(epic.getId()));
  }

  @Test
  public void deleteSubtaskByIsTest() {
    Epic epic = createEpic();
    Subtask subTask = createSubTask(epic);

    taskManager.deleteSubtaskById(subTask.getId());

    assertNull(taskManager.getSubtaskById(subTask.getId()));
  }


  @Test
  public void deleteAllTasksTest() {
    for (int i = 0; i < 10; i++) {
      Task task = createTask();
    }
    taskManager.deleteAllTasks();

    assertEquals(0, taskManager.getTasks().toArray().length);
  }

  @Test
  public void deleteAllEpicsTest() {
    for (int i = 0; i < 10; i++) {
      Epic epic = createEpic();
    }
    taskManager.deleteAllEpics();

    assertEquals(0, taskManager.getTasks().toArray().length);
  }

  @Test
  public void deleteAllSubtasksTest() {
    Epic epic = createEpic();
    for (int i = 0; i < 10; i++) {
      Subtask subTask = createSubTask(epic);
    }
    taskManager.deleteAllSubtasks();

    assertEquals(0, taskManager.getTasks().toArray().length);

  }

  @Test
  public void subtasksShouldNotContainDeletedSubtaskIsTest() {//Удаляемые подзадачи не должны хранить внутри себя старые id.
    Epic epic = createEpic();
    Subtask subtask1 = createSubTask(epic);
    Subtask subtask2 = createSubTask(epic);

    int subtaskIdToDelete = subtask1.getId();

    taskManager.deleteSubtaskById(subtaskIdToDelete);

    assertNull(taskManager.getSubtaskById(subtaskIdToDelete));
  }

  @Test
  public void epicShouldNotContainDeletedSubtaskIdTest() {//Внутри эпиков не должно оставаться неактуальных id подзадач.
    Epic epic = createEpic();
    Subtask subtask1 = createSubTask(epic);
    Subtask subtask2 = createSubTask(epic);

    int subtaskIdToDelete = subtask1.getId();

    taskManager.deleteSubtaskById(subtaskIdToDelete);

    assertFalse(taskManager.getEpicById(epic.getId()).getSubtasks().contains(subtask1));
  }

  @Test
  public void isTaskCrossTest() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    Task task1 = createTask();
    task1.setStartTime(LocalDateTime.parse("28.03.2025 00:10", formatter));
    task1.setDuration(Duration.ofMinutes(5));
    taskManager.updateTask(task1);

    Task task2 = createTask();
    task2.setStartTime(LocalDateTime.parse("28.03.2025 00:13", formatter));
    task2.setDuration(Duration.ofMinutes(5));
    taskManager.updateTask(task2);

    Task task3 = createTask();
    task3.setStartTime(LocalDateTime.parse("28.03.2025 00:30", formatter));
    task3.setDuration(Duration.ofMinutes(5));
    taskManager.updateTask(task3);

    assertEquals(3, taskManager.getTasks().size());
    assertEquals(2, taskManager.getPrioritizedTasks().size());

    Epic epic = createEpic();

    Subtask subtask1 = createSubTask(epic);
    subtask1.setStartTime(LocalDateTime.parse("28.03.2025 01:00", formatter));
    subtask1.setDuration(Duration.ofMinutes(40));
    taskManager.updateSubtask(subtask1);

    Subtask subtask2 = createSubTask(epic);
    subtask2.setStartTime(LocalDateTime.parse("27.03.2025 00:31", formatter));
    subtask2.setDuration(Duration.ofMinutes(10));
    taskManager.updateSubtask(subtask2);

    assertEquals(50, epic.getDuration().toMinutes());

  }
}