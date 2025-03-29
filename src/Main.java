import managers.*;
import model.*;

import java.io.*;

import java.time.*;
import java.time.format.*;

public class Main {

    public static void main(String[] args) {

        FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("tasks.csv"));

        Task task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Task 2", "Task 2 description", Status.NEW);
        taskManager.addNewTask(task2);
        Task task3 = new Task("Task 3", "Task 3 description", Status.NEW);
        taskManager.addNewTask(task3);

        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 description", epic1.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 description", epic1.getId());
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask 3", "Subtask 3 description", epic2.getId());
        taskManager.addNewSubtask(subtask3);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
        LocalDateTime firstTime = LocalDateTime.parse("07:14 23.02.25", formatter);
        LocalDateTime secondTime = LocalDateTime.parse("11:00 27.02.25", formatter);
        LocalDateTime thirdTime = LocalDateTime.parse("08:19 23.02.25", formatter);

        task1.setStartTime(firstTime);
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.updateTask(task1);

        task2.setStartTime(secondTime);
        task2.setDuration(Duration.ofMinutes(360));
        taskManager.updateTask(task2);

        task3.setStartTime(thirdTime);
        task3.setDuration(Duration.ofMinutes(10));
        taskManager.updateTask(task3);

        System.out.println(taskManager.getPrioritizedTasks());
    }
}