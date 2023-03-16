package command;

import controller.Context;
import model.Staff;
import model.Event;
import model.EventType;
import model.User;
import view.IView;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * {@link CreateEventCommand} allows {@link Staff}s to create new {@link Event}s.
 * The command applies for the currently logged-in user.
 */
public class CreateEventCommand implements ICommand<Event> {
    private final String title;
    private final EventType type;
    private final int numTickets;
    private final int ticketPriceInPence;
    private final String venueAddress;
    private final String description;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final boolean hasSocialDistancing;
    private final boolean hasAirFiltration;
    private final boolean isOutdoors;
    private Event eventResult;

    /**
     * @param title               title of the event
     * @param type                type of the event
     * @param numTickets          number of initially available tickets for the event. This can be 0 if the event does
     *                            not need booking.
     * @param ticketPriceInPence  price in GBP pence per event ticket. This can be 0 if the event is free.
     * @param venueAddress        indicates where this performance will take place, would be displayed to users in app
     * @param description         additional details about the event
     * @param startDateTime       indicates the date and time when this performance is due to start
     * @param endDateTime         indicates the date and time when this performance is due to end
     * @param hasSocialDistancing indicates whether social distancing will be enforced at this performance.
     *                            Users can filter events based on this field if they have Covid-19 safety preferences
     * @param hasAirFiltration    indicates whether air filtration will be in place at this performance.
     *                            Users can filter events based on this field if they have Covid-19 safety preferences
     * @param isOutdoors          indicates whether this performance will take place outdoors. Normally would imply
     *                            {@link #hasAirFiltration}, but kept as a separate field for simplicity.
     *                            Users can filter events based on this field if they have Covid-19 safety preferences
     */
    public CreateEventCommand(String title,
                              EventType type,
                              int numTickets,
                              int ticketPriceInPence,
                              String venueAddress,
                              String description,
                              LocalDateTime startDateTime,
                              LocalDateTime endDateTime,
                              boolean hasSocialDistancing,
                              boolean hasAirFiltration,
                              boolean isOutdoors) {
        this.title = title;
        this.type = type;
        this.numTickets = numTickets;
        this.ticketPriceInPence = ticketPriceInPence;
        this.venueAddress = venueAddress;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.hasSocialDistancing = hasSocialDistancing;
        this.hasAirFiltration = hasAirFiltration;
        this.isOutdoors = isOutdoors;
    }

    /**
     * @return event number corresponding to the created event if successful and null otherwise
     */
    @Override
    public Event getResult() {
        return eventResult;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Staff member
     * @verifies.that event startDateTime is not after endDateTime
     * @verifies.that event startDateTime is in the future
     * @verifies.that no other event with the same title has the same startDateTime and endDateTime
     * @verifies.that the event ticket price is non-negative
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "CreateEventCommand",
                    CreateEventCommand.LogStatus.CREATE_EVENT_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            eventResult = null;
            return;
        }

        if (startDateTime.isAfter(endDateTime)) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_START_AFTER_END,
                    Map.of("startDateTime", startDateTime,
                            "endDateTime", endDateTime)
            );
            eventResult = null;
            return;
        }

        if (startDateTime.isBefore(LocalDateTime.now())) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_IN_THE_PAST,
                    Map.of("startDateTime", startDateTime)
            );
            eventResult = null;
            return;
        }

        // Use case 3.6:
        // If all the fields were correctly provided and an event with the
        // same name did not already exist for some or all of the same dates and times
        boolean isEventTitleAndTimeClash = context.getEventState().getAllEvents().stream()
                .anyMatch(otherEvent -> otherEvent.getTitle().equals(title)
                        && otherEvent.getStartDateTime().equals(startDateTime)
                        && otherEvent.getEndDateTime().equals(endDateTime)
                );
        if (isEventTitleAndTimeClash) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_TITLE_AND_TIME_CLASH,
                    Map.of("title", title,
                            "startDateTime", startDateTime,
                            "endDateTime", endDateTime)
            );
            eventResult = null;
            return;
        }

        if (ticketPriceInPence < 0) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_NEGATIVE_TICKET_PRICE,
                    Map.of("ticketPriceInPence", ticketPriceInPence)
            );
            eventResult = null;
            return;
        }

        Event event = context.getEventState().createEvent(title, type, numTickets,
                ticketPriceInPence, venueAddress, description,
                startDateTime, endDateTime, hasSocialDistancing, hasAirFiltration, isOutdoors);
        view.displaySuccess(
                "CreateEventCommand",
                LogStatus.CREATE_EVENT_SUCCESS,
                Map.of("eventNumber", event.getEventNumber(),
                        "organiser", currentUser,
                        "title", title)
        );
        eventResult = event;
    }

    private enum LogStatus {
        CREATE_EVENT_USER_NOT_STAFF,
        CREATE_EVENT_START_AFTER_END,
        CREATE_EVENT_IN_THE_PAST,
        CREATE_EVENT_TITLE_AND_TIME_CLASH,
        CREATE_EVENT_NEGATIVE_TICKET_PRICE,
        CREATE_EVENT_SUCCESS,
    }
}
