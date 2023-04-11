import command.BookEventCommand;
import command.CreateEventCommand;
import command.LogoutCommand;
import controller.Context;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class BookEventSystemTests extends ConsoleTest {
    @Test
    void bookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumerAndBookFirstEvent(controller, 1);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );
    }

    @Test
    void bookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(5, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        controller.runCommand(new BookEventCommand(firstEventNumber, 3));
        controller.runCommand(new BookEventCommand(firstEventNumber, 2));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );
    }

    @Test
    void bookEventWithNegativeNumberTicket() {
        Controller controller = createStaffAndEvent(1, 1);
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        startOutputCapture();
        createConsumerAndBookFirstEvent(controller, -2);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_INVALID_NUM_TICKETS"
        );
    }

    @Test
    void bookCancelledEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.getContext().getEventState().getAllEvents().get(0).cancel();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        startOutputCapture();
        createConsumerAndBookFirstEvent(controller, 1);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_EVENT_NOT_ACTIVE"
        );
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
    }

    @Test
    void overbookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumerAndBookFirstEvent(controller, 2);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );
    }

    @Test
    void overbookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(2, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        controller.runCommand(new BookEventCommand(firstEventNumber, 1));
        controller.runCommand(new BookEventCommand(firstEventNumber, 2));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );
    }


    @Test
    void bookEventNotLoggedIn() {
        Controller controller = createStaffAndEvent(5, 1);
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();

        startOutputCapture();
        controller.runCommand(new BookEventCommand(firstEventNumber, 5));
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_USER_NOT_CONSUMER"
        );
    }

    @Test
    void bookNonExistingEvent() {
        Controller controller = createStaffAndEvent(5, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        controller.runCommand(new BookEventCommand(1324, 1));
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_EVENT_NOT_FOUND"
        );
    }
}
