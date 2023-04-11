import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEvent extends ConsoleTest{
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Event event;
    private EventTagCollection tags;
    @BeforeEach
    void setUp() {
        tags = new EventTagCollection("hasAirFiltration=false,hasSocialDistancing=true");
        startTime = LocalDateTime.now().plusHours(1);
        endTime = LocalDateTime.now().plusHours(2);
        event = new Event(1, "Event1", EventType.Music, 30, 100,
                "55.94368888764689 -3.1888246174917114", "This is the Test Event",
                startTime, endTime, tags);
    }

    @Test
    void testConstructor() {
        assertEquals(1, event.getEventNumber());
        assertEquals("Event1", event.getTitle());
        assertEquals(EventType.Music, event.getType());
        assertEquals(30, event.getNumTicketsCap());
        assertEquals(30, event.getNumTicketsLeft());
        assertEquals(100, event.getTicketPriceInPence());
        assertEquals("55.94368888764689 -3.1888246174917114", event.getVenueAddress());
        assertEquals("This is the Test Event", event.getDescription());
        assertEquals(startTime, event.getStartDateTime());
        assertEquals(endTime, event.getEndDateTime());
        assertEquals(tags, event.getTags());
        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertEquals(0, event.getReviews().size());
    }

    @Test
    void testCancel() {
        event.cancel();
        assertEquals(EventStatus.CANCELLED, event.getStatus());
    }

    @Test
    void testAddReview() {
        Consumer consumer = new Consumer("Elon Musk", "elon@gmail.com","1234",null,"123");
        Review review = new Review(consumer, event, LocalDateTime.now(),"This is a good event.");
        Review review2 = new Review(consumer, event, LocalDateTime.now(),"This is a bad event.");
        event.addReview(review);
        event.addReview(review2);
        List<Review> reviews = event.getReviews();

        assertEquals(2, reviews.size());
        assertEquals(review, reviews.get(0));
        assertEquals(review2, reviews.get(1));
    }

    @Test
    void testToString() {
        String eventToString = event.toString();
        String eventString = "Event{eventNumber=1, title='Event1', type=Music, numTicketsCap=30, ticketPriceInPence=100, " +
                "venueAddress='55.94368888764689 -3.1888246174917114', description='This is the Test Event', " +
                "startDateTime="+ startTime.toString()+ ", endDateTime=" + endTime.toString() +", status=ACTIVE, " +
                "numTicketsLeft=30, tags=EventTagCollection{tags={hasSocialDistancing=true, hasAirFiltration=false}}}";
        assertEquals(eventString, eventToString);
    }
}
