package command;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import com.sun.source.tree.Tree;
import controller.Context;
import external.MapSystem;
import view.IView;
import model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link ListEventsMaxDistanceCommand} allows {@link model.Consumer} to get
 * a list of {@link Event}s based on address set in their profile{@link model.Consumer}.
 * The command applies for currently logged-in user.
 */
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
     * @param transportMode    the chosen transport mode for calculating distance, {@link TransportMode}s
     * @param maxDistance      the maximum range used to filter the events that not with in this range
     */
    public ListEventsMaxDistanceCommand(boolean userEventsOnly, boolean activeEventsOnly, LocalDate searchDate,
                                        TransportMode transportMode, double maxDistance) {
        super(userEventsOnly, activeEventsOnly, searchDate);
        this.transportMode = transportMode;
        this.maxDistance = maxDistance;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Consumer
     * @verifies.that current user has an address set up in their profile
     */
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

        // Check if current user is a consumer
        if (!(currentUser instanceof Consumer)) {
            view.displayFailure("ListEventsMaxDistanceCommand",
                    LogStatus.LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER,
                    Map.of("currentUser", currentUser != null ? currentUser : "none"));
            eventListResult = null;
            return;
        }

        Consumer consumer = (Consumer)currentUser;
        String consumerAddress = consumer.getAddress();
        // Check if consumer address is not null
        if (consumerAddress == null || consumerAddress.isBlank()) {
            view.displayFailure("ListEventsMaxDistanceCommand",
                    LogStatus.LIST_EVENTS_MAX_DISTANCE_CONSUMER_ADDRESS_INVALID,
                    Map.of("consumerAddress", consumerAddress != null ? consumerAddress : "none"));
            eventListResult = null;
            return;
        }

        MapSystem mapSystem = context.getMapSystem();
        GHPoint consumerPoint = null;
        try {
            String[] consumerAddressCoordinates = consumerAddress.split(" ");
            String modifiedConsumerAddress = consumerAddressCoordinates[0] + "," + consumerAddressCoordinates[1];
            consumerPoint = mapSystem.convertToCoordinates(modifiedConsumerAddress);
        } catch(Exception e) {
            view.displayFailure("ListEventsMaxDistanceCommand",
                    LogStatus.LIST_EVENTS_MAX_DISTANCE_CONSUMER_ADDRESS_INVALID,
                    Map.of("consumerAddress", consumerAddress));
            eventListResult = null;
            return;
        }

        // Filter based on consumer preference
        EventTagCollection preferences = consumer.getPreferences();
        List<Event> eventsFittingPreferences = context.getEventState().getAllEvents().stream()
                .filter(event -> super.eventSatisfiesPreferences(preferences, event))
                .collect(Collectors.toList());

        Map<Double, Event> result = new TreeMap<>();
        // Filter based on event distance
        for (int i = 0; i < eventsFittingPreferences.size(); ++i) {
            Event event = eventsFittingPreferences.get(i);
            String eventAddress = event.getVenueAddress();
            if (eventAddress == null || eventAddress.isBlank()) {
                continue;
            }

            GHPoint eventPoint = null;
            try {
                String[] eventAddressCoordinates = eventAddress.split(" ");
                String modifiedEventAddress = eventAddressCoordinates[0] + "," + eventAddressCoordinates[1];
                eventPoint = mapSystem.convertToCoordinates(modifiedEventAddress);
            } catch(Exception e) {
                continue;
            }

            ResponsePath path = mapSystem.routeBetweenPoints(transportMode,consumerPoint, eventPoint);
            InstructionList il = path.getInstructions();
            Double distance = 0.0;
            for (Instruction in : il) {
                distance += in.getDistance();
            }

            if (distance <= maxDistance) {
                result.put(distance, event);
            }
        }

        List<Event> events = new ArrayList<>();
        for (Map.Entry<Double, Event> entry : result.entrySet()) {
            events.add(entry.getValue());
        }
        eventListResult = events;
        view.displaySuccess(
                "ListEventsMaxDistanceCommand",
                LogStatus.LIST_EVENTS_MAX_DISTANCE_SUCCESS,
                Map.of("activeEventsOnly", activeEventsOnly,
                        "userEventsOnly", true,
                        "searchDate", String.valueOf(searchDate),
                        "maxDistance", maxDistance,
                        "transportMode", transportMode,
                        "eventList", eventListResult)
        );
    }

    private enum LogStatus {
        LIST_EVENTS_MAX_DISTANCE_NOT_LOGGED_IN,
        LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER,
        LIST_EVENTS_MAX_DISTANCE_CONSUMER_ADDRESS_INVALID,
        LIST_EVENTS_MAX_DISTANCE_SUCCESS
    }
}
