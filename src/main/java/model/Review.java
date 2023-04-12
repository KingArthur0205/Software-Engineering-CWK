package model;

import java.time.LocalDateTime;

/**
 * {@link Review} represents a review that can be added by a {@link Consumer} to an {@link Event}.
 * The review contains the creation time and the contents written by the consumer.
 */
public class Review {
    private final Consumer author;
    private final Event event;
    private final LocalDateTime creationDateTime;
    private final String content;

    /**
     * @param author           the {@link Consumer} who wrote the review
     * @param event            the {@link Event} that is reviewed
     * @param creationDateTime the creation time of the review
     * @param content          the contents of the review
     */
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

    public String toString() {
        return "Review" +
                "{" +"author=" + author.getName() +
                ", creationDateTime=" + creationDateTime +
                ", content=" + content +
                "}";
    }
}
