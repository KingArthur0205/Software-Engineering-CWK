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
                100, "Old College", "This is the Test Event",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), new EventTagCollection()
        );

        // Create a consumer, log in, and add a booking in the past
        createConsumer(controller);
        Consumer curConsumer = (Consumer)context.getUserState().getCurrentUser();
        curConsumer.addBooking(new Booking(100, curConsumer, testEvent,
                1, LocalDateTime.now().minusHours(20)));
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
    }

    @Test
    void reviewWhenUserIsStaff() {

    }

    @Test
    void reviewWhenConsumerHasNoBooking() {}

    @Test
    void reviewRunningEvent() {

    }

    @Test
    void reviewFutureEvent() {}
}
