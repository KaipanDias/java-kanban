package managers;

import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    private static class Node<E> {
        public E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(historyMap.size());
        Node<Task> currentNode = head;

        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            node.data = null;

            if (head == node && tail == null) {
                head = null;
            } else if (head == node) {
                head = next;
                head.prev = null;
            } else if (tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    private void linkLast(Task task) {
        final Node<Task> newNode = new Node<>(tail, task, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }

        tail = newNode;
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.remove(id));
    }
}