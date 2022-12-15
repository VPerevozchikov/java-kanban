package service;

import model.Task;
import java.util.NoSuchElementException;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> listHistory = getTasks();
/*        System.out.println("История просмотров: ");
        for (int i = 0; i < listHistory.size(); i++){
            System.out.println((i + 1) + ". " + listHistory.get(i));
        }*/
        return listHistory;
    }

    @Override
    public void getHistoryWithPrint() {
        ArrayList<Task> listHistory = getTasks();
        System.out.println("История просмотров: ");
        for (int i = 0; i < listHistory.size(); i++){
            System.out.println((i + 1) + ". " + listHistory.get(i));
        }
    }


    @Override
    public void remove(int id) {
        removeNode(nodeMap.get(id));
    }


    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        nodeMap.put(task.getId(), newNode);

    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node node = head; node != null; node = node.next) {
            tasks.add(node.task);
        }
        return tasks;
    }

    private void removeNode(Node node) {
        nodeMap.remove(node.task.getId());
        Node next = node.next;
        Node prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.task = null;
    }

    class Node {
        public Task task;
        public Node next;
        public Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}
