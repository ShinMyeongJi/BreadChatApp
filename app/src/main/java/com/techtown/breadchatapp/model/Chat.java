package com.techtown.breadchatapp.model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean seen;
    private boolean send;


    public Chat() {
    }

    public Chat(String sender, String receiver, String message, boolean seen, boolean send) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.seen = seen;
        this.send = send;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isSend() {
        return send;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
