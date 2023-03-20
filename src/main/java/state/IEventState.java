package state;

import model.Staff;
import model.Event;
import model.EventType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link IEventState} is an interface representing the portion of application state that contains all the {@link Event}
 * information.
 */
public interface IEventState {
    /**
     * @return List of all registered {@link Event}s in the application
     */
    List<Event> getAllEvents();

    /**
     * @param eventNumber unique event identifier to look up in the event state
     * @return {@link Event} corresponding to the specified event number if there is one, and null otherwise
     */
    Event findEventByNumber(long eventNumber);

    /**
     * Create a new {@link Event} (includes generating a new unique event number) and add it to the event state
     *
     * @param title               name of the new event
     * @param type                type of the new event
     * @param numTickets          maximum number of tickets for the new event, which are all initially available
     * @param ticketPriceInPence  price per ticket in GBP pence
     * @param venueAddress        address where the new performance will take place
     * @param description         additional details about the event
     * @param startDateTime       date and time when the new performance will begin
     * @param endDateTime         date and time when the new performance will end
     * @param hasSocialDistancing whether the new performance will have social distancing in place
     * @param hasAirFiltration    whether the new performance will have air filtration in place
     * @param isOutdoors          whether the new performance will be outdoors
     * @return The newly created {@link Event}
     */
    Event createEvent(String title,
                      EventType type,
                      int numTickets,
                      int ticketPriceInPence,
                      String venueAddress,
                      String description,
                      LocalDateTime startDateTime,
                      LocalDateTime endDateTime,
                      boolean hasSocialDistancing,
                      boolean hasAirFiltration,
                      boolean isOutdoors);
}
