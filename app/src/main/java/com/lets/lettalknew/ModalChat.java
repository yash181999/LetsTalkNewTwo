package com.lets.lettalknew;

public class ModalChat {
    String message, receiver, sender,image,time,audio;
    String isSeen;

    public ModalChat() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String isSeen() {
        return isSeen;
    }

    public void setSeen(String seen) {
        isSeen = seen;
    }

    public ModalChat(String message, String receiver, String sender, String image, String time, String isSeen,String audio) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.image = image;
        this.time = time;
        this.isSeen = isSeen;
        this.audio=  audio;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
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
