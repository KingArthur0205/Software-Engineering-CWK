package state;

import model.Event;
import model.EventTag;
import model.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * {@link EventState} is a concrete implementation of {@link IEventState}.
 */
public class EventState implements IEventState, Serializable {
    private final List<Event> events;
    private long nextEventNumber;
    private final Map<String, EventTag> possibleTags;

    /**
     * Create a new EventState with an empty list of events, which keeps track of the next event and performance numbers
     * it will generate, starting from 1 and incrementing by 1 each time when requested.
     */
    public EventState() {
        events = new LinkedList<>();
        nextEventNumber = 1;
        possibleTags = new HashMap<>();
        Set<String> valueSet = new HashSet<>(Arrays.asList("true", "false"));
        Set<String> capacitySet = new HashSet<>(Arrays.asList("<20", "20-100", "100-200", "200"));
        createEventTag("hasSocialDistancing", valueSet, "false");
        createEventTag("hasAirFiltration", valueSet, "false");
        createEventTag("venueCapacity", capacitySet, "<20");
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
        this.possibleTags = otherImpl.possibleTags;
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

    @Override
    public void addEvent(Event event) {
        events.add(event);
    }

    @Override
    public Map<String, EventTag> getPossibleTags() {
        return possibleTags;
    }

    @Override
    public EventTag createEventTag(String tagName, Set<String> possibleValues, String defaultValue) {
        EventTag tag = new EventTag(possibleValues, defaultValue);
        possibleTags.put(tagName, tag);
        return tag;
    }
}
