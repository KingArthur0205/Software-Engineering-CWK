package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * {@link Event} represents an event that can be booked by {@link Consumer}s. Tickets can be free, but they are
 * required to attend, and there is a maximum cap on the number of tickets that can be booked.
 */
public class Event implements Serializable {
    private long eventNumber;
    private final String title;
    private final EventType type;
    private final int numTicketsCap;
    private final int ticketPriceInPence;
    private final String venueAddress;
    private final String description;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private EventTagCollection tags;
    private List<Review> reviews;
    private long serialVersionUID;
    private EventStatus status;
    private int numTicketsLeft;

    /**
     * Create a new Event with status = {@link EventStatus#ACTIVE}
     *
     * @param eventNumber         unique event identifier
     * @param title               name of the event
     * @param type                type of the event
     * @param numTicketsCap       maximum number of tickets, initially all available for booking
     * @param ticketPriceInPence  price of each ticket in GBP pence
     * @param venueAddress        address where the performance will be taking place
     * @param description         additional details about the event, e.g., who the performers in a concert will be
     *                            or if payment is required on entry in addition to ticket booking
     * @param startDateTime       date and time when the performance will begin
     * @param endDateTime         date and time when the performance will end
     * @param tags                names and selected values associated with the event
     */
    public Event(long eventNumber,
                 String title,
                 EventType type,
                 int numTicketsCap,
                 int ticketPriceInPence,
                 String venueAddress,
                 String description,
                 LocalDateTime startDateTime,
                 LocalDateTime endDateTime,
                 EventTagCollection tags) {
        this.eventNumber = eventNumber;
        this.title = title;
        this.type = type;
        this.numTicketsCap = numTicketsCap;
        this.ticketPriceInPence = ticketPriceInPence;
        this.venueAddress = venueAddress;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        // A new event has no reviews
        this.reviews = new ArrayList<>();
        this.tags = tags;

        this.status = EventStatus.ACTIVE;
        this.numTicketsLeft = numTicketsCap;
        this.serialVersionUID = UUID.randomUUID().getLeastSignificantBits();

    }

    /**
     * @return Number of the maximum cap of tickets which were initially available
     */
    public int getNumTicketsCap() {
        return numTicketsCap;
    }

    /**
     * @return Number of the tickets left that can still be purchased.
     */
    public int getNumTicketsLeft() {
        return numTicketsLeft;
    }

    public void setNumTicketsLeft(int numTicketsLeft) {
        this.numTicketsLeft = numTicketsLeft;
    }

    public int getTicketPriceInPence() {
        return ticketPriceInPence;
    }

    public long getEventNumber() {
        return eventNumber;
    }

    public String getTitle() {
        return title;
    }

    public EventType getType() {
        return type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public EventTagCollection getTags() {
        return tags;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setEventNumber(long number){
        this.eventNumber = number;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Set {@link #status} to {@link EventStatus#CANCELLED}
     */
    public void cancel() {
        status = EventStatus.CANCELLED;
    }

    /**
     * @param review a new {@link Review} of the event
     */
    public void addReview(Review review) {
        reviews.add(review);
    }

    public List<Review> getReviews() {return reviews;}


    @Override
    public String toString() {
        return "Event{" +
                "eventNumber=" + eventNumber +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", numTicketsCap=" + numTicketsCap +
                ", ticketPriceInPence=" + ticketPriceInPence +
                ", venueAddress='" + venueAddress + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status=" + status +
                ", numTicketsLeft=" + numTicketsLeft +
                ", tags=" + tags +
                '}';
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public String getDescription() {
        return description;
    }
    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Check if o is an instance of Event or not
        if (!(o instanceof Event)) {
            return false;
        }

        // typecast o to Event so that we can compare data members
        Event c = (Event) o;

        boolean venueEquals = Objects.equals(venueAddress, c.venueAddress);
        boolean descriptionEquals = Objects.equals(description, c.description);

        // Compare the data members and return accordingly
        return venueEquals && eventNumber == c.eventNumber && title.equals(c.title) && type.equals(c.type)
                && numTicketsCap == c.numTicketsCap && ticketPriceInPence == c.ticketPriceInPence && descriptionEquals
                && startDateTime.equals(c.startDateTime) && endDateTime.equals(c.endDateTime) && tags.equals(c.tags)
                && reviews.equals(c.reviews) && status.equals(c.status) && numTicketsLeft == c.numTicketsLeft;
    }

    @Override
    public int hashCode() {
        int hash = 2;

        hash = 2 * hash + (venueAddress == null ? 0 : venueAddress.hashCode());
        hash = 2 * hash + (title == null ? 0 : title.hashCode());
        hash = 2 * hash + type.hashCode();
        hash = 2 * hash + (description == null ? 0 : description.hashCode());
        hash = 2 * hash + (startDateTime == null ? 0 : startDateTime.hashCode());
        hash = 2 * hash + (endDateTime == null ? 0 : endDateTime.hashCode());
        hash = 2 * hash + tags.hashCode();
        hash = 2 * hash + reviews.hashCode();
        hash = 2 * hash + status.hashCode();

        hash += (int)eventNumber;
        hash += numTicketsLeft;
        hash += ticketPriceInPence;
        hash += numTicketsCap;
        return hash;
    }
}
