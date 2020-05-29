package com.lets.lettalknew;

public class NotificationSender {

    private NotificationData data;
    private String to;

    public NotificationSender(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }
    public NotificationSender() {}

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
