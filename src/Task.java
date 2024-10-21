import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;


    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task() {
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Status getStatus() {
        return status;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) &&
                Objects.equals(description, otherTask.description);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (description != null) {
            hash = hash + description.hashCode();
        }

        if (status != null) {
            hash = hash + status.hashCode();
        }
        hash = hash * 31;

        if (id != 0) {
            hash = hash + id;
        }
        hash = hash * 31;

        return hash;
    }

    @Override
    public String toString() {

        String result =  "Task{" +
                "id='" + id + '\'' +
                "name='" + name + '\'';

        if (description != null) {
            result = result + ", description.length=" + description.length() + '\'' +
                    ", status=" + status +
                    '}';
        } else {
            result = result + ", description=null" + '\'' +
                    ", status=" + status +
                    '}';
        }

        return result;
    }
}