package com.lets.lettalknew;

public class ModalChat {
    String message, receiver, sender,image,time,audio;
    String isSeen,deletedBySender,deletedByReceiver;

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

    public String getDeletedBySender() {
        return deletedBySender;
    }

    public void setDeletedBySender(String deletedBySender) {
        this.deletedBySender = deletedBySender;
    }

    public String getDeletedByReceiver() {
        return deletedByReceiver;
    }

    public void setDeletedByReceiver(String deletedByReceiver) {
        this.deletedByReceiver = deletedByReceiver;
    }

    public ModalChat(String message, String receiver, String sender, String image, String time, String isSeen, String audio
    , String deletedBySender, String deletedByReceiver) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.image = image;
        this.time = time;
        this.isSeen = isSeen;
        this.audio=  audio;
        this.deletedBySender = deletedBySender;
        this.deletedByReceiver = deletedByReceiver;
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
