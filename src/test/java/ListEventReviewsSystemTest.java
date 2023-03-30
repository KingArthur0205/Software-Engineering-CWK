import command.ListEventReviewsCommand;
import command.ReviewEventCommand;
import controller.Context;
import controller.Controller;
import model.Booking;
import model.Consumer;
import model.Event;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ListEventReviewsSystemTest extends ConsoleTest{
    private Controller createAndReviewEvent() {
        // Create an event in the past
        Controller controller = createController();
        Context context = controller.getContext();
        Event testEvent = context.getEventState().createEvent("TestEvent", EventType.Music, 10,
                100, "Old College", "This is the Test Event",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), false,
                false, false);

        // Create a consumer, log in, and add a booking in the past
        createConsumer(controller);
        Consumer curConsumer = (Consumer)context.getUserState().getCurrentUser();
        ReviewEventCommand reviewCmd = new ReviewEventCommand(1, "This is a good event");
        controller.runCommand(reviewCmd);
        return controller;
    }

    private void addMoreReview(Controller controller, int eventNumber, String content) {
        ReviewEventCommand reviewCmd = new ReviewEventCommand(eventNumber, content);
        controller.runCommand(reviewCmd);
    }

    private void createMoreEvent(Controller controller) {
        Context context = controller.getContext();
        context.getEventState().createEvent("TestEvent", EventType.Dance, 100,
                100, "Old College", "This is the Test Event2",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), false,
                false, false);

        context.getEventState().createEvent("TestEvent", EventType.Movie, 50,
                100, "Old College", "This is the Test Event3",
                LocalDateTime.now().minusHours(11), LocalDateTime.now().minusHours(8), false,
                false, false);
    }

    @Test
    void listWhenNoEventInSystem() {
        Controller controller = createController();
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");
    }

    @Test
    void listWithNonexistentEventTitle() {
        Controller controller = createAndReviewEvent();
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent2");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");

    }

    @Test
    void listWhenOneEventHasGivenTitle() {
        Controller controller = createAndReviewEvent();
        ListEventReviewsCommand testCmd = new ListEventReviewsCommand("TestEvent");
        startOutputCapture();
        controller.runCommand(testCmd);
        stopOutputCaptureAndCompare("LIST_EVENT_REVIEWS_SUCCESS");
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
    }
}
