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

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerPrioritizedTest {

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
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1");
        Task task1 = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now()
                .plusHours(1));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);

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


        subtask1.setDuration(Duration.ofMinutes(10));
        subtask1.setStartTime(LocalDateTime.now().plusMonths(18));

        subtask2.setDuration(Duration.ofMinutes(5));
        subtask2.setStartTime(LocalDateTime.now().plusDays(1));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(3, jsonArray.size(), "Не все задачи добавлены в список");

        Task fistInOrder = gson.fromJson(jsonArray.get(0), Task.class);

        Subtask lastInOrder = gson.fromJson(jsonArray.get(2), Subtask.class);

        assertEquals(task1.getId(), fistInOrder.getId());
        assertEquals(task1.getName(), fistInOrder.getName());
        assertEquals(task1.getDescription(), fistInOrder.getDescription());
        assertEquals(task1.getStatus(), fistInOrder.getStatus());

        assertEquals(subtask1.getId(), lastInOrder.getId());
        assertEquals(subtask1.getName(), lastInOrder.getName());
        assertEquals(subtask1.getDescription(), lastInOrder.getDescription());
        assertEquals(subtask1.getStatus(), lastInOrder.getStatus());
        assertEquals(subtask1.getEpicId(), lastInOrder.getEpicId());
    }
}