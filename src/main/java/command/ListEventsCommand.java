package command;

import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link ListEventsCommand} allows anyone to get a list of {@link Event}s available on the system.
 * Optionally, users can specify a particular {@link LocalDate} to look up events for.
 */
public class ListEventsCommand implements ICommand<List<Event>> {
    private final boolean userEventsOnly;
    private final boolean activeEventsOnly;
    private final LocalDate searchDate;
    private List<Event> eventListResult;

    /**
     * @param userEventsOnly   if true, the returned events will be filtered depending on the logged-in user:
     *                         for {@link Staff}s only the {@link Event}s they have created,
     *                         and for {@link Consumer}s only the {@link Event}s that match their {@link ConsumerPreferences}
     * @param activeEventsOnly if true, returned {@link Event}s will be filtered to contain only {@link Event}s with
     *                         {@link EventStatus#ACTIVE}
     * @param searchDate       chosen date to look for events. Can be null. If not null, only {@link Event}s that are
     *                         happening on {@link #searchDate} (i.e., starting, ending, or in between) will be included
     */
    public ListEventsCommand(boolean userEventsOnly, boolean activeEventsOnly, LocalDate searchDate) {
        this.userEventsOnly = userEventsOnly;
        this.activeEventsOnly = activeEventsOnly;
        this.searchDate = searchDate;
    }

    private static boolean eventSatisfiesPreferences(ConsumerPreferences preferences, Event event) {
        return (!preferences.preferOutdoorsOnly || event.isOutdoors())
                        && (!preferences.preferAirFiltration || event.hasAirFiltration())
                        && (!preferences.preferSocialDistancing || event.hasSocialDistancing())
                        && (preferences.preferredMaxCapacity >= event.getNumTicketsCap()
                );
    }

    private static List<Event> filterEvents(List<Event> events, boolean activeEventsOnly, LocalDate searchDate) {
        Stream<Event> filteredEvents = events.stream();
        if (activeEventsOnly) {
            filteredEvents = filteredEvents.filter(event -> event.getStatus() == EventStatus.ACTIVE);
        }
        if (searchDate != null) {
            filteredEvents = filteredEvents.filter(event ->
                    event.getStartDateTime().toLocalDate().equals(searchDate)
                            || event.getEndDateTime().toLocalDate().equals(searchDate)
                            || (searchDate.isAfter(event.getStartDateTime().toLocalDate())
                            && searchDate.isBefore(event.getEndDateTime().toLocalDate())));
        }
        return filteredEvents.collect(Collectors.toList());
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that if userEventsOnly is set, the current user must be logged in
     */
    @Override
    public void execute(Context context, IView view) {
        if (!userEventsOnly) {
            eventListResult = filterEvents(context.getEventState().getAllEvents(), activeEventsOnly, searchDate);
            view.displaySuccess(
                    "ListEventsCommand",
                    LogStatus.LIST_EVENTS_SUCCESS,
                    Map.of("activeEventsOnly", activeEventsOnly,
                            "userEventsOnly", false,
                            "searchDate", String.valueOf(searchDate),
                            "eventList", eventListResult)
            );
            return;
        }

        User currentUser = context.getUserState().getCurrentUser();

        if (currentUser == null) {
            view.displayFailure(
                    "ListEventsCommand",
                    LogStatus.LIST_EVENTS_NOT_LOGGED_IN,
                    Map.of("activeEventsOnly", activeEventsOnly,
                            "userEventsOnly", true)
            );
            eventListResult = null;
            return;
        }

        if (currentUser instanceof Staff) {
            eventListResult = filterEvents(context.getEventState().getAllEvents(), activeEventsOnly, searchDate);
            view.displaySuccess(
                    "ListEventsCommand",
                    LogStatus.LIST_EVENTS_SUCCESS,
                    Map.of("activeEventsOnly", activeEventsOnly,
                            "userEventsOnly", true,
                            "searchDate", String.valueOf(searchDate),
                            "eventList", eventListResult)
            );
            return;
        }

        if (currentUser instanceof Consumer) {
            Consumer consumer = (Consumer) currentUser;
            ConsumerPreferences preferences = consumer.getPreferences();
            List<Event> eventsFittingPreferences = context.getEventState().getAllEvents().stream()
                    .filter(event -> eventSatisfiesPreferences(preferences, event))
                    .collect(Collectors.toList());

            eventListResult = filterEvents(eventsFittingPreferences, activeEventsOnly, searchDate);
            view.displaySuccess(
                    "ListEventsCommand",
                    LogStatus.LIST_EVENTS_SUCCESS,
                    Map.of("activeEventsOnly", activeEventsOnly,
                            "userEventsOnly", true,
                            "searchDate", String.valueOf(searchDate),
                            "eventList", eventListResult)
            );
            return;
        }

        eventListResult = null;
    }

    /**
     * @return List of {@link Event}s if successful and null otherwise
     */
    @Override
    public List<Event> getResult() {
        return eventListResult;
    }

    private enum LogStatus {
        LIST_EVENTS_SUCCESS,
        LIST_EVENTS_NOT_LOGGED_IN,
    }
}
