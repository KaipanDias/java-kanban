package managers;

import model.*;
import exceptions.*;
import model.Task;

import java.io.*;
import java.nio.*;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(HEADER);
            for (Task task : getTasks()) {
                bw.write(toStringCSV(task));
                bw.newLine();
            }
            for (Epic epic : getEpics()) {
                bw.write(toStringCSV(epic));
                bw.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                bw.write(toStringCSV(subtask));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    public FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager tm = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            int maxTasksId = 0;

            while ((line = br.readLine()) != null) {
                Task task = fromString(line);
                if (task.getId() > maxTasksId) {
                    maxTasksId = task.getId();
                }
                if (task instanceof Epic) {
                    tm.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    Subtask tmpSubtask = (Subtask) task;
                    tm.subtasks.put(task.getId(), tmpSubtask);

                    ArrayList<Subtask> epicSubtasks = tm.epics.get(tmpSubtask.getEpicId()).getSubtasks();
                    epicSubtasks.add(tmpSubtask);
                } else {
                    tm.tasks.put(task.getId(), task);
                }
            }

            tm.id = maxTasksId;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтений файла: " + e.getMessage());
        }

        return tm;
    }

    private String toStringCSV(Task task) {
        return task.getId() + "," +
                TaskType.TASK + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ",";
    }

    private String toStringCSV(Subtask subtask) {
        return subtask.getId() + "," +
                TaskType.SUBTASK + "," +
                subtask.getName() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getEpicId() + ",";
    }

    private String toStringCSV(Epic epic) {
        return epic.getId() + "," +
                TaskType.EPIC + "," +
                epic.getName() + "," +
                epic.getStatus() + "," +
                epic.getDescription() + ",";
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        String taskId = values[0];
        String taskType = values[1];
        String taskName = values[2];
        Status taskStatus = Status.valueOf(values[3]);
        String taskDescription = values[4];

        switch (TaskType.valueOf(taskType)) {
            case TASK -> {
                return new Task(Integer.parseInt(taskId), taskName, taskDescription, taskStatus);
            }
            case SUBTASK -> {
                return new Subtask(Integer.parseInt(taskId), taskName, taskDescription, taskStatus, Integer.parseInt(values[5]));
            }
            case EPIC -> {
                return new Epic(Integer.parseInt(taskId), taskName, taskDescription, taskStatus);
            }
            default -> throw new ManagerSaveException("Такого типа задач нет" + taskType);
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public int addNewTask(Task task) {
        int sup = super.addNewTask(task);
        save();
        return sup;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int sup = super.addNewSubtask(subtask);
        save();
        return sup;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int sup = super.addNewEpic(epic);
        save();
        return sup;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
}
