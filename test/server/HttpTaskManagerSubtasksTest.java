package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import exceptions.NotFoundException;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerSubtasksTest {

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
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Name", "Epic description");

        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        Subtask subtask2 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Task responseSubtask = gson.fromJson(jsonArray.get(0), Task.class);


        assertEquals(subtask1.getId(), responseSubtask.getId());
        assertEquals(subtask1.getName(), responseSubtask.getName());
        assertEquals(subtask1.getDescription(), responseSubtask.getDescription());
        assertEquals(subtask1.getStatus(), responseSubtask.getStatus());

    }

    @Test
    public void testGetSubtaskByID() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Name", "Epic description");

        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        Subtask subtask2 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        ;
        Task responseSubtask = gson.fromJson(jsonElement, Task.class);


        assertEquals(subtask1.getId(), responseSubtask.getId());
        assertEquals(subtask1.getName(), responseSubtask.getName());
        assertEquals(subtask1.getDescription(), responseSubtask.getDescription());
        assertEquals(subtask1.getStatus(), responseSubtask.getStatus());

    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic Name", "Epic description");

        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        Subtask subtaskFormManager = subtasksFromManager.getFirst();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");


        assertEquals(subtask.getName(), subtaskFormManager.getName());
        assertEquals(subtask.getDescription(), subtaskFormManager.getDescription());
        assertEquals(subtask.getStatus(), subtaskFormManager.getStatus());
        assertEquals(subtask.getEpicId(), subtaskFormManager.getEpicId());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Name", "Epic description");

        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask(
                subtask1.getId(),
                "Subtask name",
                "Subtask new description",
                epic.getStatus(),
                epic.getId());

        String subtaskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task subtaskFromManager = taskManager.getSubtaskById(subtask1.getId());

        assertEquals(subtaskFromManager.getId(), subtask2.getId());
        assertEquals(subtaskFromManager.getName(), subtask2.getName());
        assertEquals(subtaskFromManager.getDescription(), subtask2.getDescription());
        assertEquals(subtaskFromManager.getStatus(), subtask2.getStatus());

    }

    @Test
    public void testDeleteSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Name", "Epic description");

        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        Subtask subtask2 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(taskManager.getTasks().isEmpty());

    }

    @Test
    public void testDeleteTaskByID() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Name", "Epic description");

        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        Subtask subtask2 = new Subtask(
                "Subtask name",
                "Subtask description",
                epic.getStatus(),
                epic.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(2));
    }

}