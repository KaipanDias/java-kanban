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

class HttpTaskManagerEpicTest {

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
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic Name 1", "Epic description 1");
        Epic epic2 = new Epic("Epic Name 1", "Epic description 2");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Task responseEpic = gson.fromJson(jsonArray.get(0), Epic.class);


        assertEquals(epic1.getId(), responseEpic.getId());
        assertEquals(epic1.getName(), responseEpic.getName());
        assertEquals(epic1.getDescription(), responseEpic.getDescription());
        assertEquals(epic1.getStatus(), responseEpic.getStatus());
        assertEquals(epic1.getDuration(), responseEpic.getDuration());

    }

    @Test
    public void testGetEpicByID() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic Name 1", "Epic description 1");
        Epic epic2 = new Epic("Epic Name 1", "Epic description 2");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        Task responseEpic = gson.fromJson(jsonElement, Epic.class);


        assertEquals(epic1.getId(), responseEpic.getId());
        assertEquals(epic1.getName(), responseEpic.getName());
        assertEquals(epic1.getDescription(), responseEpic.getDescription());
        assertEquals(epic1.getStatus(), responseEpic.getStatus());
        assertEquals(epic1.getDuration(), responseEpic.getDuration());

    }

    @Test
    public void testGetSubtaskByEicID() throws IOException, InterruptedException {
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
        URI url = URI.create(BASE_URL + "/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        Subtask responseSubtask = gson.fromJson(jsonArray.get(0), Subtask.class);

        assertEquals(subtask1.getId(), responseSubtask.getId());
        assertEquals(subtask1.getName(), responseSubtask.getName());
        assertEquals(subtask1.getDescription(), responseSubtask.getDescription());
        assertEquals(subtask1.getStatus(), responseSubtask.getStatus());
        assertEquals(subtask1.getEpicId(), responseSubtask.getEpicId());

    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {

        Epic epic1 = new Epic(1, "Epic Name", "Epic Description");
        String epicJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic Name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic Name 1", "Epic description 1");
        Epic epic2 = new Epic("Epic Name 1", "Epic description 2");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void testDeleteEpicByID() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic Name 1", "Epic description 1");
        Epic epic2 = new Epic("Epic Name 1", "Epic description 2");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> taskManager.getEpicById(1));
    }
}