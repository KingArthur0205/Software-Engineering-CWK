import command.ListEventReviewsCommand;
import command.ReviewEventCommand;
import controller.Context;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ListEventReviewsSystemTest extends ConsoleTest{
    private Controller createAndReviewEvent() {
        // Create an event in the past
        Controller controller = createController();
        Context context = controller.getContext();
        Event testEvent = context.getEventState().createEvent("TestEvent", EventType.Music, 10,
                100, "55.94368888764689 -3.1888246174917114", "This is the Test Event",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), new EventTagCollection()
        );
        assertNotNull(testEvent);

        // Create a consumer, log in, and add a booking in the past
        createConsumer(controller);
        Consumer curConsumer = (Consumer)context.getUserState().getCurrentUser();
        controller.getContext().getBookingState().addBooking(new Booking(1, curConsumer, testEvent, 1, LocalDateTime.now()));
        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "This is a good event");
        controller.runCommand(reviewCmd);
        return controller;
    }

    private void addMoreReview(Controller controller, int eventNumber, String content) {
        controller.getContext().getBookingState().addBooking(new Booking(eventNumber, (Consumer)controller.getContext().getUserState().getCurrentUser(), controller.getContext().getEventState().findEventByNumber(eventNumber), 1, LocalDateTime.now()));
        ReviewEventCommand reviewCmd = new ReviewEventCommand(eventNumber, content);
        controller.runCommand(reviewCmd);
    }

    private void createMoreEvent(Controller controller) {
        Context context = controller.getContext();
        context.getEventState().createEvent("TestEvent", EventType.Dance, 100,
                100, "55.94368888764689 -3.1888246174917114", "This is the Test Event2",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), new EventTagCollection()
        );

        context.getEventState().createEvent("TestEvent", EventType.Movie, 50,
                100, "55.94368888764689 -3.1888246174917114", "This is the Test Event3",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), new EventTagCollection()
        );
    }

    @Test
    void listWhenNoEventInSystem() {
        Controller controller = createController();
        createConsumer(controller);
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");

        assertNotNull(testCmd.getResult());
        assertTrue(testCmd.getResult().isEmpty());
    }

    @Test
    void listWithNonexistentEventTitle() {
        Controller controller = createAndReviewEvent();
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent2");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");

        assertNotNull(testCmd.getResult());
        assertTrue(testCmd.getResult().isEmpty());
    }

    @Test
    void listWhenOneEventHasGivenTitle() {
        Controller controller = createAndReviewEvent();
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");

        assertNotNull(testCmd.getResult());
        assertTrue(testCmd.getResult().size() == 1);
    }

    @Test
    void listWhenEventHasMultipleReviews() {
        Controller controller = createAndReviewEvent();
        addMoreReview(controller, 1,"I don't like this event");
        addMoreReview(controller, 1,"This isn't what I expected.");
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");

        assertNotNull(testCmd.getResult());
        assertEquals(3, testCmd.getResult().size());
    }

    @Test
    void listWhenMultipleEventsHaveGivenTitle() {
        Controller controller = createAndReviewEvent();
        createMoreEvent(controller);
        addMoreReview(controller, 2, "This isn't a music event");
        addMoreReview(controller, 3,"This is n't a dance event");
        addMoreReview(controller, 1,"This isn't what I expected.");
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");

        assertNotNull(testCmd.getResult());
        assertEquals(4, testCmd.getResult().size());
    }
}
