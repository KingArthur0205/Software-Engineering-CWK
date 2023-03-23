package command;

import controller.Context;
import model.Consumer;
import model.Event;
import model.Review;
import model.User;
import state.IEventState;
import state.IUserState;
import view.IView;

import java.time.LocalDateTime;
import java.util.Map;

public class ReviewEventCommand implements ICommand<Review> {
    private Review reviewResult;
    private final long eventNumber;
    private final String content;

    public ReviewEventCommand(long eventNumber, String content) {
        this.eventNumber = eventNumber;
        this.content = content;
    }

    @Override
    public void execute(Context context, IView view) {
        IEventState eventState = context.getEventState();
        if (eventState.findEventByNumber(eventNumber) == null) {
            view.displayFailure("ReviewEventCommand", LogStatus.REVIEW_EVENT_EVENT_NUMBER_DOES_NOT_EXIST,
                    Map.of("eventNumber", eventNumber));
            reviewResult = null;
            return;
        }

        User currentUser = context.getUserState().getCurrentUser();
        if (!(currentUser instanceof Consumer)) {
            view.displayFailure("BookEventCommand",
                    LogStatus.REVIEW_EVENT_USER_NOT_CONSUMER,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            reviewResult = null;
            return;
        }

        IUserState userState = context.getUserState();
        Consumer author = (Consumer)userState.getCurrentUser();

        Event event = eventState.findEventByNumber(eventNumber);
        LocalDateTime creationTime = LocalDateTime.now();
        reviewResult = new Review(author, event, creationTime, content);
        event.addReview(reviewResult);
        view.displaySuccess("ReviewEventCommand", LogStatus.REVIEW_EVENT_SUCCESS,
                Map.of("author", author, "event", event,
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
