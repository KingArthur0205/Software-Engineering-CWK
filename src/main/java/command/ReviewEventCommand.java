package command;

import controller.Context;
import model.*;
import state.BookingState;
import state.IEventState;
import state.IUserState;
import view.IView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReviewEventCommand implements ICommand<Review> {
    private Review reviewResult;
    private final long eventNumber;
    private final String content;

    public ReviewEventCommand(long eventNumber, String content) {
        this.eventNumber = eventNumber;
        this.content = content;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that an event exists with the corresponding eventNumber
     * @verifies.that the event is already over
     * @verifies.that the current user is a logged-in Consumer
     * @verifies.that the consumer had at least 1 valid booking (not cancelled by the consumer) at the event
     */

    @Override
    public void execute(Context context, IView view) {
        // Check an event with the provided event number exists
        IEventState eventState = context.getEventState();
        Event eventToBeReviewed = eventState.findEventByNumber(eventNumber);
        if (eventToBeReviewed == null) {
            view.displayFailure("ReviewEventCommand", LogStatus.REVIEW_EVENT_EVENT_NUMBER_DOES_NOT_EXIST,
                    Map.of("eventNumber", eventNumber));
            reviewResult = null;
            return;
        }

        // Check the current user is a logged-in Consumer
        User currentUser = context.getUserState().getCurrentUser();
        if (!(currentUser instanceof Consumer)) {
            view.displayFailure("BookEventCommand",
                    LogStatus.REVIEW_EVENT_USER_NOT_CONSUMER,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            reviewResult = null;
            return;
        }

        List<Booking> bookings = context.getBookingState().findBookingsByEventNumber(eventNumber);
        Boolean ifConsumerHasBooking = false;
        for (Booking booking : bookings) {
            if (booking.getBooker() == currentUser)
                ifConsumerHasBooking = true;
        }

        if (!ifConsumerHasBooking) {
            view.displayFailure("BookEventCommand",
                    LogStatus.REVIEW_EVENT_USER_HAVE_NO_BOOKING,
                    Map.of("eventToBeReviewed", eventToBeReviewed)
            );
            reviewResult = null;
            return;
        }

        // Create and add the Review
        IUserState userState = context.getUserState();
        Consumer author = (Consumer)userState.getCurrentUser();

        LocalDateTime creationTime = LocalDateTime.now();
        reviewResult = new Review(author, eventToBeReviewed, creationTime, content);
        eventToBeReviewed.addReview(reviewResult);
        view.displaySuccess("ReviewEventCommand", LogStatus.REVIEW_EVENT_SUCCESS,
                Map.of("author", author, "event", eventToBeReviewed,
                        "creationTime", creationTime, "content", content));
    }

    @Override
    public Review getResult() {
        return reviewResult;
    }

    private enum LogStatus {
        REVIEW_EVENT_EVENT_NUMBER_DOES_NOT_EXIST,
        REVIEW_EVENT_EVENT_NOT_OVER,
        REVIEW_EVENT_USER_NOT_CONSUMER,
        REVIEW_EVENT_USER_HAVE_NO_BOOKING,
        REVIEW_EVENT_SUCCESS
    }
}
