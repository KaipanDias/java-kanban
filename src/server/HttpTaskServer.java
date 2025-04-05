package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    public void start() {
        try {
            Gson gson = getGson();
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHttpHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtaskHttpHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicHttpHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHttpHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager, gson));
            httpServer.start();
            System.out.println("Сервер запущен на http://localhost:8080/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
