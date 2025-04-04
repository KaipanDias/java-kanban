package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class SubtaskHttpHandler  extends BaseHttpHandler implements HttpHandler {
    public SubtaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
