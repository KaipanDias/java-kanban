package managers;

import model.*;
import exceptions.*;
import model.Task;

import java.io.*;
import java.nio.*;

public class FileBackedTaskManager extends InMemoryTaskManager{

    private final File file;

    public FileBackedTaskManager(File file){
        this.file = file;
    }
    private void save(){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write("id,type,name,status,description,epic");
            for(Task task : getTasks()){
                bw.write(toStringCSV(task));
                bw.newLine();
            }
            for(Epic epic : getEpics()){
                bw.write(toStringCSV(epic));
                bw.newLine();
            }
            for(Subtask subtask : getSubtasks()){
                bw.write(toStringCSV(subtask));
                bw.newLine();
            }
        }catch (IOException e){
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file){
        FileBackedTaskManager tm = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while (br.readLine() != null){
                Task task = fromString(br.readLine());
                if (task instanceof Epic){
                    tm.addNewEpic((Epic) task);
                }else if(task instanceof Subtask){
                    tm.addNewSubtask((Subtask) task);
                }else{
                    tm.addNewTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтений файла: " + e.getMessage());
        }

        return tm;
    }

    public static String toStringCSV(Task task){
        return task.getId() + "," +
                TaskType.TASK + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ",";
    }

    public static String toStringCSV(Subtask subtask){
        return subtask.getId() + "," +
                TaskType.SUBTASK + "," +
                subtask.getName() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getEpicId() + ",";
    }

    public static String toStringCSV(Epic epic){
        return epic.getId() + "," +
                TaskType.EPIC + "," +
                epic.getName() + "," +
                epic.getStatus() + "," +
                epic.getDescription() + ",";
    }

    public static Task fromString(String value){
        String[] values = value.split(",");
        String taskId = values[0];
        String taskType = values[1];
        String taskName = values[2];
        String taskStatus = values[3];
        String taskDescription = values[4];
        int subtaskEpicId = Integer.parseInt(values[5]);

        switch (TaskType.valueOf(taskType)){
            case TASK -> {
                return new Task(taskName, taskDescription);
            }
            case SUBTASK -> {
                return new Subtask(taskName, taskDescription, subtaskEpicId);
            }
            case EPIC -> {
                return new Epic(taskName,taskDescription);
            }default -> throw new ManagerSaveException("Такого типа задач нет" + taskType);
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
