package command;

import controller.Context;
import view.IView;
import model.*;

import java.time.LocalDate;
import java.util.Map;

public class ListEventsMaxDistanceCommand extends ListEventsCommand{
    private final TransportMode transportMode;
    private final double maxDistance;
    /**
     * @param userEventsOnly   if true, the returned events will be filtered depending on the logged-in user:
     *                         for {@link Staff}s only the {@link Event}s they have created,
     *                         and for {@link Consumer}s only the {@link Event}s that match their {@link EventTagCollection}
     * @param activeEventsOnly if true, returned {@link Event}s will be filtered to contain only {@link Event}s with
     *                         {@link EventStatus#ACTIVE}
     * @param searchDate       chosen date to look for events. Can be null. If not null, only {@link Event}s that are
     *                         happening on {@link #searchDate} (i.e., starting, ending, or in between) will be included
     */
    public ListEventsMaxDistanceCommand(boolean userEventsOnly, boolean activeEventsOnly, LocalDate searchDate,
                                        TransportMode transportMode, double maxDistance) {
        super(userEventsOnly, activeEventsOnly, searchDate);
        this.transportMode = transportMode;
        this.maxDistance = maxDistance;
    }

    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        if (currentUser == null) {
            view.displayFailure(
                    "ListEventsMaxDistanceCommand",
                    LogStatus.LIST_EVENTS_MAX_DISTANCE_NOT_LOGGED_IN,
                    Map.of("activeEventsOnly", activeEventsOnly,
                            "userEventsOnly", true)
            );
            eventListResult = null;
            return;
        }

        if (!(currentUser instanceof Consumer)) {
            view.displayFailure("ListEventsMaxDistanceCommand",
                    LogStatus.LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER,
                    Map.of("currentUser", currentUser != null ? currentUser : "none"));
            eventListResult = null;
            return;
        }

        Consumer consumer = (Consumer)currentUser;
        String consumerAddress = consumer.getAddress();
        if (consumerAddress == null || consumerAddress.isBlank()) {
            view.displayFailure("ListEventsMaxDistanceCommand",
                    LogStatus.LIST_EVENTS_CONSUMER_ADDRESS_INVALID,
                    Map.of("consumerAddress", consumerAddress != null ? consumerAddress : "none"));
            eventListResult = null;
            return;
        }
    }

    private enum LogStatus {
        LIST_EVENTS_MAX_DISTANCE_NOT_LOGGED_IN,
        LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER,
        LIST_EVENTS_CONSUMER_ADDRESS_INVALID,
        LIST_EVENTS_MAX_DISTANCE_SUCCESS
    }
}
