package managers;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static List<Task> history = new ArrayList<>(10);;

    @Override
    public void add(Task task) {
        if (task != null){
            history.add(task);
            if (history.size() > 10) {
                history.removeFirst();
            }
        }

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

}
