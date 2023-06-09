import command.BookEventCommand;
import command.LogoutCommand;
import command.ReviewEventCommand;
import controller.Context;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;
import state.IEventState;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewEventSystemTests extends ConsoleTest{
    private Controller createAndBookEvent() {
        // Create an event in the past
        Controller controller = createController();
        Context context = controller.getContext();
        Event testEvent = context.getEventState().createEvent("TestEvent", EventType.Music, 10,
                100, "55.944377051350656 -3.18913215894117", "This is the Test Event",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), new EventTagCollection()
        );

        // Create a consumer, log in, and add a booking in the past
        createConsumer(controller);
        Consumer curConsumer = (Consumer)context.getUserState().getCurrentUser();
        Booking booking = context.getBookingState().createBooking(curConsumer, testEvent, 1);
        curConsumer.addBooking(booking);
        return controller;
    }

    @Test
    void reviewWithNonexistentEvent() {
        Controller controller = createAndBookEvent();

        ReviewEventCommand reviewCmd = new ReviewEventCommand(2, "Good Event");
        startOutputCapture();
        controller.runCommand(reviewCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_EVENT_NUMBER_DOES_NOT_EXIST");

        assertNull(reviewCmd.getResult());
    }

    @Test
    void reviewExistingEvent() {
        Controller controller = createAndBookEvent();

        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "Good Event");
        startOutputCapture();
        controller.runCommand(reviewCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_SUCCESS");

        IEventState eventState = controller.getContext().getEventState();
        Review resultReview = reviewCmd.getResult();
        Review addedReview = eventState.findEventByNumber(1).getReviews().get(0);

        assertNotNull(resultReview);
        assertEquals(resultReview, addedReview);
        assertEquals("Good Event", addedReview.getContent());
    }

    @Test
    void reviewWhenNotLoggedIn() {
        Controller controller = createAndBookEvent();
        // Log out the current user
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "Good Event");
        startOutputCapture();
        controller.runCommand(reviewCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_USER_NOT_CONSUMER");

        assertNull(reviewCmd.getResult());
    }

    @Test
    void reviewWhenUserIsStaff() {
        Controller controller = createAndBookEvent();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        createStaff(controller);
        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "Good Event");
        startOutputCapture();
        controller.runCommand(reviewCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_USER_NOT_CONSUMER");

        assertNull(reviewCmd.getResult());
    }

    @Test
    void reviewWhenConsumerHasNoBooking() {
        Controller controller = createController();
        createConsumer(controller);
        Context context = controller.getContext();
        Event testEvent = context.getEventState().createEvent("TestEvent", EventType.Music, 10,
                100, "Old College", "This is the Test Event",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), new EventTagCollection()
        );

        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "Good Event");
        startOutputCapture();
        controller.runCommand(reviewCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_USER_HAVE_NO_BOOKING");

        assertNull(reviewCmd.getResult());
    }

    @Test
    void reviewRunningEvent() {
        Controller controller = createController();
        createConsumer(controller);
        Context context = controller.getContext();
        Event testEvent = context.getEventState().createEvent("TestEvent", EventType.Music, 10,
                100, "Old College", "This is the Test Event",
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2), new EventTagCollection()
        );
        BookEventCommand bookEventCommand = new BookEventCommand(1, 1);
        controller.runCommand(bookEventCommand);

        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "Good Event");
        startOutputCapture();
        controller.runCommand(reviewCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_EVENT_NOT_OVER");

        assertNull(reviewCmd.getResult());
    }

    @Test
    void reviewFutureEvent() {
        Controller controller = createController();
        createConsumer(controller);
        Context context = controller.getContext();
        Event testEvent = context.getEventState().createEvent("TestEvent", EventType.Music, 10,
                100, "Old College", "This is the Test Event",
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), new EventTagCollection()
        );
        BookEventCommand bookEventCommand = new BookEventCommand(1, 1);
        controller.runCommand(bookEventCommand);

        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "Good Event");
        startOutputCapture();
        controller.runCommand(reviewCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_EVENT_NOT_OVER");

        assertNull(reviewCmd.getResult());
    }
}
