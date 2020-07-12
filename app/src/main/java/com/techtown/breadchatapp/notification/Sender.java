package com.techtown.breadchatapp.notification;

public class Sender {
    private Notification notification;
    private String to;
    private Data data;

    public Sender() {
    }

    public Sender(Notification notification, String to, Data data) { //
        this.notification = notification;
        this.to = to;
        this.data = data;
    }

    public Notification getNotification() {
        return notification;
    }

    public String getTo() {
        return to;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
