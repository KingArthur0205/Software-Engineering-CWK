package model;

import java.time.LocalDateTime;

public class Review {
    private final Consumer author;
    private final Event event;
    private final LocalDateTime creationDateTime;
    private final String content;

    public Review(Consumer author, Event event, LocalDateTime creationDateTime, String content) {
        this.author = author;
        this.event = event;
        this.creationDateTime = creationDateTime;
        this.content = content;
    }

    public String getContent() {return content;}

    public Consumer getAuthor() {
        return author;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }
}
