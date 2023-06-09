import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestBooking extends ConsoleTest{
    Consumer consumer;
    Event event;
    Booking booking;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime bookingTime;
    @BeforeEach
    void createBooking() {
        bookingTime = LocalDateTime.now();
        startTime = bookingTime.plusHours(1);
        endTime = bookingTime.plusHours(2);
        consumer = new Consumer("Elon Musk", "elon@gmail.com","1234",null,"123");
        event = new Event(1,"TestEvent", EventType.Music, 10,
                100, "55.94368888764689 -3.1888246174917114", "This is the Test Event",
                startTime, endTime, new EventTagCollection());
        booking = new Booking(10, this.consumer, this.event, 1, bookingTime);
    }
    @Test
    void testConstructor() {
        assertEquals(10, booking.getBookingNumber());
        assertEquals(this.consumer, booking.getBooker());
        assertEquals(this.event, booking.getEvent());
        assertEquals(1, booking.getNumTickets());
        assertEquals(BookingStatus.Active, booking.getStatus());

    }

    @Test
    void testCancelByConsumer() {
        booking.cancelByConsumer();
        assertEquals(BookingStatus.CancelledByConsumer, booking.getStatus());
    }

    @Test
    void testCancelByProvider() {
        booking.cancelByProvider();
        assertEquals(BookingStatus.CancelledByProvider, booking.getStatus());
    }

    @Test
    void testToString() {
        String bookingToString = booking.toString();
        String testString = "Booking{status=Active, bookingNumber=10, booker=Elon Musk, event=Event{eventNumber=1, " +
                "title='TestEvent', type=Music, numTicketsCap=10, ticketPriceInPence=100, " +
                "venueAddress='55.94368888764689 -3.1888246174917114', description='This is the Test Event', startDateTime=" +
                startTime.toString() + ", endDateTime=" + endTime.toString() + ", " +
                "status=ACTIVE, numTicketsLeft=10, tags=EventTagCollection{tags={}}}, numTickets=1, bookingDateTime=" +
                bookingTime.toString() + "}";
        assertEquals(bookingToString, testString);
    }

    @Test
    void testEqualsIdentical() {
        Booking booking2 = new Booking(10, this.consumer, this.event, 1, bookingTime);

        assertTrue(booking.equals(booking2));
    }

    @Test
    void testEqualsNull() {
        assertFalse(booking.equals(null));
    }

    @Test
    void testEqualsNotIdentical() {
        Booking booking2 = new Booking(5, this.consumer, this.event, 1, bookingTime);
        assertFalse(booking.equals(booking2));
    }

    @Test
    void testEqualsToItSelf() {
        assertTrue(booking.equals(booking));
    }

    @Test
    void testHashCodeToIdentical() {
        Booking booking2 = new Booking(10, this.consumer, this.event, 1, bookingTime);

        assertTrue(booking.equals(booking2));
        assertTrue(booking.hashCode() == booking2.hashCode());
    }

    @Test
    void testHashCodeToItself() {
        assertTrue(booking.hashCode() == booking.hashCode());
    }

    @Test
    void testHashCodeToNotIdentical() {
        Booking booking2 = new Booking(3, this.consumer, this.event, 1, bookingTime);

        assertFalse(booking.equals(booking2));
        assertFalse(booking.hashCode() == booking2.hashCode());
    }

}
