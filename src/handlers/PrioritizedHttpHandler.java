package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedHttpHandler  extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
