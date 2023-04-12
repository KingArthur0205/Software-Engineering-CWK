import command.BookEventCommand;
import command.CreateEventCommand;
import command.LogoutCommand;
import controller.Context;
import controller.Controller;
import model.Booking;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookEventSystemTests extends ConsoleTest {
    private static boolean isContainedInSystem(Controller controller, Booking bookEventCommand) {
        return controller.getContext().getBookingState().getAllBookings().contains(bookEventCommand);
    }
    
    @Test
    void bookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );

        assertNotNull(booking);
        assertTrue(isContainedInSystem(controller, booking));
    }

    @Test
    void bookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(5, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookEventCommand1 = new BookEventCommand(firstEventNumber, 3);
        BookEventCommand bookEventCommand2 = new BookEventCommand(firstEventNumber, 2);
        controller.runCommand(bookEventCommand1);
        controller.runCommand(bookEventCommand2);
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );

        Booking bookingResult1 = bookEventCommand1.getResult();
        Booking bookingResult2 = bookEventCommand2.getResult();
        assertNotNull(bookingResult1);
        assertNotNull(bookingResult2);
        assertTrue(isContainedInSystem(controller, bookingResult1));
        assertTrue(isContainedInSystem(controller, bookingResult2));
    }

    @Test
    void bookEventWithNegativeNumberTicket() {
        Controller controller = createStaffAndEvent(1, 1);
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        startOutputCapture();
        Booking bookingResult = createConsumerAndBookFirstEvent(controller, -2);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_INVALID_NUM_TICKETS"
        );

        assertFalse(isContainedInSystem(controller, bookingResult));
    }

    @Test
    void bookCancelledEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.getContext().getEventState().getAllEvents().get(0).cancel();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        startOutputCapture();
        Booking bookingResult = createConsumerAndBookFirstEvent(controller, 1);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_EVENT_NOT_ACTIVE"
        );

        assertFalse(isContainedInSystem(controller, bookingResult));
    }

    @Test
    void bookEventThatIsAlreadyOver() {
        Controller controller = createController();
        Context context = controller.getContext();
        Event testEvent = context.getEventState().createEvent("TestEvent", EventType.Music, 10,
                100, "55.944377051350656 -3.18913215894117", "This is the Test Event",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), new EventTagCollection()
        );

        createConsumer(controller);
        BookEventCommand bookEventCommand = new BookEventCommand(1, 10);
        startOutputCapture();
        controller.runCommand(bookEventCommand);
        stopOutputCaptureAndCompare("BOOK_EVENT_ALREADY_OVER");

        assertNull(bookEventCommand.getResult());
        assertFalse(isContainedInSystem(controller, bookEventCommand.getResult()));
    }

    @Test
    void bookEventThatRequiresPayment() {
        Controller controller = createController();
        createStaff(controller);
        CreateEventCommand createEventCommand = new CreateEventCommand("TestEvent", EventType.Music, 10,
                100, "55.944377051350656 -3.18913215894117", "This is the Test Event",
                LocalDateTime.now().plusHours(11), LocalDateTime.now().plusHours(18), new EventTagCollection());
        controller.runCommand(createEventCommand);
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);

        BookEventCommand bookEventCommand = new BookEventCommand(1, 10);
        startOutputCapture();
        controller.runCommand(bookEventCommand);
        stopOutputCaptureAndCompare("BOOK_EVENT_SUCCESS");

        assertNotNull(bookEventCommand.getResult());
        assertTrue(isContainedInSystem(controller, bookEventCommand.getResult()));
    }

    @Test
    void overbookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        Booking bookingResult = createConsumerAndBookFirstEvent(controller, 2);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );

        assertFalse(isContainedInSystem(controller, bookingResult));
    }

    @Test
    void overbookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(2, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookEventCommand1 = new BookEventCommand(firstEventNumber, 1);
        BookEventCommand bookEventCommand2 = new BookEventCommand(firstEventNumber, 2);
        controller.runCommand(bookEventCommand1);
        controller.runCommand(bookEventCommand2);
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );

        Booking bookingResult1 = bookEventCommand1.getResult();
        Booking bookingResult2 = bookEventCommand2.getResult();
        assertNotNull(bookingResult1);
        assertNull(bookingResult2);
        assertTrue(isContainedInSystem(controller, bookingResult1));
        assertFalse(isContainedInSystem(controller, bookingResult2));
    }


    @Test
    void bookEventNotLoggedIn() {
        Controller controller = createStaffAndEvent(5, 1);
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();

        startOutputCapture();
        BookEventCommand bookEventCommand = new BookEventCommand(firstEventNumber, 5);
        controller.runCommand(bookEventCommand);
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_USER_NOT_CONSUMER"
        );

        assertNull(bookEventCommand.getResult());
        assertFalse(isContainedInSystem(controller, bookEventCommand.getResult()));
    }

    @Test
    void bookNonExistingEvent() {
        Controller controller = createStaffAndEvent(5, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        BookEventCommand bookEventCommand = new BookEventCommand(1324, 1);
        controller.runCommand(bookEventCommand);
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_EVENT_NOT_FOUND"
        );

        assertNull(bookEventCommand.getResult());
        assertFalse(isContainedInSystem(controller, bookEventCommand.getResult()));
    }
}
