package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerHistoryTest {

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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task1 1", Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Testing task2 1", Duration.ofMinutes(5), LocalDateTime.now()
                .plusHours(1));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

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


        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        Task taskFormHistory = gson.fromJson(jsonArray.get(0), Task.class);

        assertEquals(3, jsonArray.size(), "Не все здачи попали в историю");

        assertEquals(task1.getId(), taskFormHistory.getId());
        assertEquals(task1.getName(), taskFormHistory.getName());
        assertEquals(task1.getDescription(), taskFormHistory.getDescription());
        assertEquals(task1.getStatus(), taskFormHistory.getStatus());

    }
}