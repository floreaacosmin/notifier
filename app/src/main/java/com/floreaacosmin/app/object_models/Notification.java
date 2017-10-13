package com.floreaacosmin.app.object_models;

public class Notification {

    private final Long id;
    private final String name;
    private final String content;
    private final String date;
    private final String author;
    private final String imageUrl;
    private final Integer isRead;


    public Notification(Long id, String name, String content,
                        String date, String author, String imageUrl, Integer isRead) {

        this.id = id;
        this.name = name;
        this.content = content;
        this.date = date;
        this.author = author;
        this.imageUrl = imageUrl;
        this.isRead = isRead;

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getNotificationDate() {
        return date;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
