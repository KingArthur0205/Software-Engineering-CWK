package command;

import controller.Context;
import model.Event;
import model.Review;
import state.IEventState;
import view.IView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListEventReviewsCommand implements ICommand<List<Review>>{
    private final String eventTitle;
    private List<Review> reviewsResult;

    public ListEventReviewsCommand(String eventTitle) {
        this.eventTitle = eventTitle;
        this.reviewsResult = new ArrayList<>();
    }
    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     */
    @Override
    public void execute(Context context, IView view) {
        IEventState eventState = context.getEventState();
        List<Event> allEvents = eventState.getAllEvents();
        for(Event event : allEvents) {
            if (event.getTitle().equals(eventTitle)) {
                List<Review> reviewsOfEvent = event.getReviews();
                reviewsResult.addAll(reviewsOfEvent);
            }
        }

        view.displaySuccess(
                "ListEventReviewsCommand",
                LogStatus.LIST_EVENT_REVIEWS_SUCCESS,
                Map.of("eventTitle", eventTitle, "reviewsResult", reviewsResult.toString()));
    }

    @Override
    public List<Review> getResult() {
        return reviewsResult;
    }

    private enum LogStatus {
        LIST_EVENT_REVIEWS_SUCCESS
    }
}

