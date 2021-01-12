package io.socket.parser;

public class Packet<T> {
    public int attachments;
    public T data;
    public int id = -1;
    public String nsp;
    public int type = -1;

    public Packet() {
    }

    public Packet(int i) {
        this.type = i;
    }

    public Packet(int i, T t) {
        this.type = i;
        this.data = t;
    }
}
