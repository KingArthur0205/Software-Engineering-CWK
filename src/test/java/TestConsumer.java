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

        // Verify that booking is actually added to the system
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

    @Test
    void testEquals() {
        Consumer consumer1 = createConsumer();
        Consumer consumer2 = createConsumer();

        assertNotNull(consumer1);
        assertNotNull(consumer2);
        assertTrue(consumer1.equals(consumer2));
    }

    @Test
    void testEqualsNull() {
        Consumer consumer1 = createConsumer();

        assertFalse(consumer1.equals(null));
    }

    @Test
    void testEqualsToItself() {
        Consumer consumer1 = createConsumer();

        assertTrue(consumer1.equals(consumer1));
    }

    @Test
    void testEqualsNotIdentical() {
        Consumer consumer2 = new Consumer("Different user", "elon@gmail.com","1234","","123");
        Consumer consumer1 = createConsumer();

        assertFalse(consumer1.equals(consumer2));
    }

    @Test
    void testHashCodeToIdentical() {
        Consumer consumer1 = createConsumer();
        Consumer consumer2 = createConsumer();

        assertNotNull(consumer1);
        assertNotNull(consumer2);
        assertTrue(consumer1.equals(consumer2));

        assertTrue(consumer1.hashCode() == consumer2.hashCode());
    }

    @Test
    void testHashCodeToItself() {
        Consumer consumer1 = createConsumer();
        assertTrue(consumer1.equals(consumer1));
        assertEquals(consumer1.hashCode(), consumer1.hashCode());
    }

    @Test
    void testHashCodeToNotIdentical() {
        Consumer consumer2 = new Consumer("Different user", "elon@gmail.com","1234","","123");
        Consumer consumer1 = createConsumer();

        assertFalse(consumer1.equals(consumer2));
        assertNotEquals(consumer1.hashCode(), consumer2.hashCode());
    }
}
