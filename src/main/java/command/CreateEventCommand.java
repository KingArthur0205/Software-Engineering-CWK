package command;

import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import external.MapSystem;
import model.*;
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
    private Event eventResult;
    private EventTagCollection tags;

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
     */
    public CreateEventCommand(String title,
                              EventType type,
                              int numTickets,
                              int ticketPriceInPence,
                              String venueAddress,
                              String description,
                              LocalDateTime startDateTime,
                              LocalDateTime endDateTime,
                              EventTagCollection tags) {
        this.title = title;
        this.type = type;
        this.numTickets = numTickets;
        this.ticketPriceInPence = ticketPriceInPence;
        this.venueAddress = venueAddress;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.tags = tags;
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

        MapSystem map = context.getMapSystem();
        // Verify if the venue address is provided, then:
        // 1. It is a valid long-lat form
        // 2. It falls within map system boundary
        if (venueAddress != null || !venueAddress.isBlank()) {
            String[] venueCoordinates = venueAddress.split(" ");
            // If the form of the address is not given in long-lat, createEvent fails
            if (venueCoordinates.length != 2) {
                view.displayFailure("CreateEventCommand", LogStatus.CREATE_EVENT_VENUE_ADDRESS_INCORRECT_FORMAT,
                        Map.of("venueAddress", venueAddress));
                eventResult = null;
                return;
            }
            // Verify if the venue address can be converted into two double
            try {
                Double.parseDouble(venueCoordinates[0]);
                Double.parseDouble(venueCoordinates[1]);
            }catch(NumberFormatException e){
                view.displayFailure("CreateEventCommand", LogStatus.CREATE_EVENT_VENUE_ADDRESS_INCORRECT_FORMAT,
                        Map.of("venueAddress", venueAddress));
            }

            String modifiedVenueAddress = venueCoordinates[0] + "," + venueCoordinates[1];
            // Verify if the address is within the boundary
            GHPoint addressPoint = map.convertToCoordinates(modifiedVenueAddress);
            if (!map.isPointWithinMapBounds(addressPoint)) {
                view.displayFailure("CreateEventCommand",
                        LogStatus.CREATE_EVENT_VENUE_ADDRESS_NOT_WITHIN_BOUNDARY,
                        Map.of("venueAddress", venueAddress));
                eventResult = null;
                return;
            }
        }

        if (tags != null) {
            Map<String, EventTag> possibleTags = context.getEventState().getPossibleTags();
            Map<String, String> eventTags = tags.getTags();
            for (String tagName: eventTags.keySet()) {
                if (!possibleTags.containsKey(tagName)) {
                    view.displayFailure("CreateEventCommand",
                            LogStatus.CREATE_EVENT_TAG_DO_NOT_EXIST,
                            Map.of("tags", tags));
                    eventResult = null;
                    return;
                }

                String tagValue = eventTags.get(tagName);
                EventTag tag = possibleTags.get(tagName);
                if (!tag.getValues().contains(tagValue)) {
                    view.displayFailure("CreateEventCommand", LogStatus.CREATE_EVENT_TAG_VALUE_DO_NOT_MATCH,
                            Map.of("tags", tags));
                    eventResult = null;
                    return;
                }
            }
        }

        Event event = context.getEventState().createEvent(title, type, numTickets,
                ticketPriceInPence, venueAddress, description,
                startDateTime, endDateTime, tags);
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
        CREATE_EVENT_VENUE_ADDRESS_INCORRECT_FORMAT,
        CREATE_EVENT_VENUE_ADDRESS_NOT_WITHIN_BOUNDARY,
        CREATE_EVENT_TAG_DO_NOT_EXIST,
        CREATE_EVENT_TAG_VALUE_DO_NOT_MATCH,
        CREATE_EVENT_SUCCESS,
    }
}
