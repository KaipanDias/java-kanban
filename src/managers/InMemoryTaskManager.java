package managers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

import java.time.*;

public class InMemoryTaskManager implements TaskManager {

    protected int id = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    //GET
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasksById(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        Epic epicSubtasks = epics.get(id);
        return new ArrayList<>(epicSubtasks.getSubtasks());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    //DELETE
    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        });
    }


    @Override
    public void deleteAllEpics() {
        epics.values().forEach(task -> historyManager.remove(task.getId()));

        epics.clear();

        deleteAllSubtasks();
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        Subtask subtask = subtasks.get(id);
        epic.getSubtasks().remove(subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtask);
        }
        updateEpicStatus(epic);

        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        Epic epic = epics.get(id);
        epic.getSubtasks().stream()
                .map(Subtask::getId)
                .forEach(subtaskId -> {
                    prioritizedTasks.remove(subtasks.get(subtaskId));
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                });
        historyManager.remove(id);
        epics.remove(id);
    }

    //ADD
    @Override
    public int addNewTask(Task task) {
        if (task.getStartTime() != null) {
            if (isTaskCrossAnyTask(task)) {
                task.setId(++id);
                tasks.put(id, task);
                prioritizedTasks.add(task);
                return id;
            } else {
                System.out.println("Задача " + task.getName() + " пересекаются с существущей");
                return 0;
            }
        }
        task.setId(++id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            if (subtask.getStartTime() != null) {
                if (isTaskCrossAnyTask(subtask)) {
                    subtask.setId(++id);
                    subtasks.put(id, subtask);

                    ArrayList<Subtask> epicSubtasks = epics.get(subtask.getEpicId()).getSubtasks();
                    epicSubtasks.add(subtask);
                    updateEpicStatus(epics.get(subtask.getEpicId()));
                    prioritizedTasks.add(subtask);
                    return id;
                } else {
                    System.out.println("Подзадача " + subtask.getName() + "  пересекается с существующей");
                    return null;
                }
            }
            subtask.setId(++id);
            subtasks.put(id, subtask);

            ArrayList<Subtask> epicSubtasks = epics.get(subtask.getEpicId()).getSubtasks();
            epicSubtasks.add(subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
            return id;
        }
        return null;
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(++id);
        epics.put(id, epic);
        return id;
    }

    //UPDATE
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if (task.getStartTime() != null) {
                if (isTaskCrossAnyTask(task)) {
                    prioritizedTasks.remove(task);
                    prioritizedTasks.add(task);
                    tasks.put(task.getId(), task);
                } else {
                    System.out.println("Задача " + task.getName() + "  пересекается с существующей");
                    return;
                }
                return;
            }

            prioritizedTasks.remove(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }

        if (subtask.getStartTime() != null) {
            if (isTaskCrossAnyTask(subtask)) {

                Epic epic = epics.get(subtask.getEpicId()); // получение эпика сабтаска
                epic.getSubtasks().remove(subtask);
                epic.getSubtasks().add(subtask);
                updateEpicStatus(epic);

                prioritizedTasks.remove(subtask);
                prioritizedTasks.add(subtask);
                subtasks.put(subtask.getId(), subtask);
            } else {
                System.out.println("Подзадача " + subtask.getName() + "  пересекается с существующей");
                return;
            }
            return;
        }


        Epic epic = epics.get(subtask.getEpicId()); // получение эпика сабтаска
        epic.getSubtasks().remove(subtask);
        epic.getSubtasks().add(subtask);
        updateEpicStatus(epic);

        prioritizedTasks.remove(subtask);
        subtasks.put(subtask.getId(), subtask);
    }


    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    protected void updateEpicStatus(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }

        ArrayList<Subtask> oldEpicSubtasks = epics.get(epic.getId()).getSubtasks();
        if (oldEpicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }

        long epicDuration = oldEpicSubtasks.stream()
                .map(Task::getDuration)
                .map(Duration::getSeconds).mapToLong(value -> value).sum();

        epic.setDuration(Duration.ofSeconds(epicDuration));

        if (oldEpicSubtasks.stream().allMatch(subtask -> subtask.getStartTime() == null)) {
            epic.setStartTime(null);
            epic.setEndTime(null);
        } else {
            oldEpicSubtasks.stream().filter(subtask -> subtask.getStartTime() != null)
                    .min(Comparator.comparing(Task::getStartTime))
                    .ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));

            oldEpicSubtasks.stream().filter(subtask -> subtask.getStartTime() != null)
                    .max(Comparator.comparing(Task::getStartTime))
                    .ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
        }

        long doneSubtasksCnt = oldEpicSubtasks.stream().filter(subtask -> subtask.getStatus() == Status.DONE).count(); // кол-во сабтасков со стасусом дан
        long newSubtasksCnt = oldEpicSubtasks.stream().filter(subtask -> subtask.getStatus() == Status.NEW).count();

        if (newSubtasksCnt == oldEpicSubtasks.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneSubtasksCnt == oldEpicSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private boolean isTaskCrossAnyTask(Task task) {
        if (task.getStartTime() == null) {
            return false;
        } else {
            return getPrioritizedTasks()
                    .stream()
                    .filter(prioritizedTasks -> !prioritizedTasks.equals(task))
                    .noneMatch(prioritizedTasks -> isTasksCross(prioritizedTasks, task));
        }
    }

    private boolean isTasksCross(Task task1, Task task2) {
        LocalDateTime task1StartTime = task1.getStartTime();
        LocalDateTime task2StartTime = task2.getStartTime();
        LocalDateTime task1FinishTime = task1.getEndTime();
        LocalDateTime task2FinishTime = task2.getEndTime();

        if (task1StartTime == null || task2StartTime == null) {
            return false;
        } else if (task1StartTime.isEqual(task2StartTime)) {
            return true;
        }

        return (task1StartTime.isBefore(task2FinishTime) && task1FinishTime.isAfter(task2StartTime))
                || (task2StartTime.isBefore(task1FinishTime) && task2FinishTime.isAfter(task1StartTime));
    }
}