package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import exceptions.NotFoundException;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now()
                .plusHours(1));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Task responseTask = gson.fromJson(jsonArray.get(0), Task.class);


        assertEquals(task.getId(), responseTask.getId());
        assertEquals(task.getName(), responseTask.getName());
        assertEquals(task.getDescription(), responseTask.getDescription());
        assertEquals(task.getStatus(), responseTask.getStatus());

    }

    @Test
    public void testGetTaskByID() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now()
                .plusHours(1));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        ;
        Task responseTask = gson.fromJson(jsonElement, Task.class);


        assertEquals(task1.getId(), responseTask.getId());
        assertEquals(task1.getName(), responseTask.getName());
        assertEquals(task1.getDescription(), responseTask.getDescription());
        assertEquals(task1.getStatus(), responseTask.getStatus());

    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        Task task = new Task(1, "Test 2", "Task description", Status.NEW);
        Task task2 = new Task(1, "Test 2", "Task new description", Status.IN_PROGRESS);

        LocalDateTime startTIme = LocalDateTime.now();

        task.setStartTime(startTIme);
        task2.setStartTime(startTIme);

        taskManager.addNewTask(task);

        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task taskFromManager = taskManager.getTaskById(task.getId());

        assertEquals(taskFromManager.getId(), task2.getId());
        assertEquals(taskFromManager.getName(), task2.getName());
        assertEquals(taskFromManager.getDescription(), task2.getDescription());
        assertEquals(taskFromManager.getStatus(), task2.getStatus());

    }

    @Test
    public void testDeleteTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now()
                .plusHours(1));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(taskManager.getTasks().isEmpty());

    }

    @Test
    public void testDeleteTaskByID() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now()
                .plusHours(1));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> taskManager.getTaskById(2));
    }

}