package com.lets.lettalknew;

public class ModalChat {
    String message, receiver, sender,image,time;
    boolean isSeen;

    public ModalChat() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public ModalChat(String message, String receiver, String sender, String image, String time, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.image = image;
        this.time = time;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
