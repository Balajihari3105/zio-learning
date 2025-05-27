package cron;

class Node {
    int head;
    Node next = null;
    Node(int num) {
        head = num;
    }
}

public class LinkedList {

    Node head;
    int lenght = 0;

    public LinkedList(int num) {
        this.head = new Node(num);
        lenght++;
    }

    public void add(int num) {
        if (lenght == 0) {
            new LinkedList(num);
            lenght++;
        } else {
            Node t = head;
            while (t.next != null) {
                t = t.next;
            }
            t.next = new Node(num);
            lenght++;
        }
    }

    public void get() {
        Node track = head;
        while (track != null) {
            System.out.println(track.head);
            track = track.next;
        }
    }
}

