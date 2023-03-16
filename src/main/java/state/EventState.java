package state;

import model.Event;
import model.EventType;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link EventState} is a concrete implementation of {@link IEventState}.
 */
public class EventState implements IEventState {
    private final List<Event> events;
    private long nextEventNumber;

    /**
     * Create a new EventState with an empty list of events, which keeps track of the next event and performance numbers
     * it will generate, starting from 1 and incrementing by 1 each time when requested
     */
    public EventState() {
        events = new LinkedList<>();
        nextEventNumber = 1;
    }

    /**
     * Copy constructor to make a deep copy of another EventState instance
     *
     * @param other instance to copy
     */
    public EventState(IEventState other) {
        EventState otherImpl = (EventState) other;
        events = new LinkedList<>(otherImpl.events);
        nextEventNumber = otherImpl.nextEventNumber;
    }

    @Override
    public List<Event> getAllEvents() {
        return events;
    }

    @Override
    public Event findEventByNumber(long eventNumber) {
        return events.stream()
                .filter(event -> event.getEventNumber() == eventNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Event createEvent(String title,
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
        long eventNumber = nextEventNumber;
        nextEventNumber++;

        Event event = new Event(eventNumber, title, type, numTickets,
                ticketPriceInPence, venueAddress, description, startDateTime,
                endDateTime, hasSocialDistancing, hasAirFiltration, isOutdoors);
        events.add(event);
        return event;
    }
}
