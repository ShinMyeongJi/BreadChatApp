package com.techtown.breadchatapp.notification;

public class Sender {
    private Notification notification;
    private String to;

    public Sender() {
    }

    public Sender(Notification notification, String to) {
        this.notification = notification;
        this.to = to;
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
}
