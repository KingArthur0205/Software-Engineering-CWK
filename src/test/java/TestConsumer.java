import model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestConsumer extends ConsoleTest{
    private Consumer createConsumer() {
        return new Consumer("Elon Musk", "elon@gmail.com","1234",null,"123");
    }
    @Test
    void testToString() {
        Consumer consumer = createConsumer();
        String consumerString = "Consumer{bookings=[], name='Elon Musk', phoneNumber='1234', address='null', preferences=EventTagCollection{tags={}}}";
        assertEquals(consumer.toString(), consumerString);
    }

    @Test
    void testConstructorWithNullAddress() {
        Consumer consumer = new Consumer("Elon Musk", "elon@gmail.com","1234","","123");
        // Verify that the Consumer instance is created
        assertNotNull(consumer);
        // Verify each field matches the input given
        assertEquals(consumer.getName(), "Elon Musk");
        assertEquals(consumer.getEmail(), "elon@gmail.com");
        assertEquals(consumer.getPhoneNumber(), "1234");
        assertEquals(consumer.getAddress(), "");
        assertEquals(consumer.getBookings(), new ArrayList<>());
        assertTrue(consumer.getPreferences().getTags().isEmpty());
    }

    @Test
    void testConstructorWithEmptyAddress() {
        Consumer consumer = new Consumer("Elon Musk", "elon@gmail.com","1234","55.944377051350656 -3.18913215894117","123");
        // Verify that the Consumer instance is created
        assertNotNull(consumer);
        // Verify each field matches the input given
        assertEquals(consumer.getName(), "Elon Musk");
        assertEquals(consumer.getEmail(), "elon@gmail.com");
        assertEquals(consumer.getPhoneNumber(), "1234");
        assertEquals(consumer.getAddress(), "55.944377051350656 -3.18913215894117");
        assertEquals(consumer.getBookings(), new ArrayList<>());
        assertTrue(consumer.getPreferences().getTags().isEmpty());
    }

    @Test
    void testConstructorWithValidAddress() {
        Consumer consumer = createConsumer();
        // Verify that the Consumer instance is created
        assertNotNull(consumer);
        // Verify each field matches the input given
        assertEquals(consumer.getName(), "Elon Musk");
        assertEquals(consumer.getEmail(), "elon@gmail.com");
        assertEquals(consumer.getPhoneNumber(), "1234");
        assertEquals(consumer.getAddress(), null);
        assertEquals(consumer.getBookings(), new ArrayList<>());
        assertTrue(consumer.getPreferences().getTags().isEmpty());
    }

    @Test
    void testAddBooking() {
        Consumer consumer = createConsumer();
        Event event = new Event(1, "event1", EventType.Music,20,10,
                "55.944377051350656 -3.18913215894117","Description",
                LocalDateTime.now().plusHours(8),LocalDateTime.now().plusHours(11),
                new EventTagCollection());
        Booking booking = new Booking(1, consumer, event, 1, LocalDateTime.now());
        consumer.addBooking(booking);

        assertTrue(consumer.getBookings().contains(booking));
    }

    @Test
    void testNotify() {
        String message = "The event is cancelled.";
        Consumer consumer = createConsumer();
        startOutputCapture();
        consumer.notify(message);
        stopOutputCaptureAndCompare("Message to elon@gmail.com and 1234: The event is cancelled.");
    }
}
