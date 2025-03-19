import managers.*;
import model.*;

import java.io.*;

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


        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(new File("tasks.csv"));
        backedTaskManager = backedTaskManager.loadFromFile(new File("tasks.csv"));
        System.out.println(backedTaskManager.getTasks());
        System.out.println(backedTaskManager.getEpics());
        System.out.println(backedTaskManager.getSubtasks());


//        taskManager.getTaskById(task1.getId());
//        taskManager.getTaskById(task3.getId());
//        taskManager.getEpicById(epic2.getId());
//        taskManager.getTaskById(task1.getId());
//        taskManager.getEpicById(epic1.getId());
//
//        System.out.println(taskManager.getHistory());
//
//        taskManager.deleteTaskById(task3.getId());
//
//        System.out.println(taskManager.getHistory());
//
//        taskManager.deleteEpicById(epic1.getId());
//
//        System.out.println(taskManager.getHistory());
    }
}
