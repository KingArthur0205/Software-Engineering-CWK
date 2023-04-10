package command;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import external.MapSystem;
import model.Consumer;
import model.Event;
import model.TransportMode;
import model.User;
import state.IEventState;
import state.IUserState;
import view.IView;

import java.util.Arrays;
import java.util.Map;

public class GetEventDirectionsCommand implements ICommand<String[]>{
    private String[] directionsResult;
    private final long eventNumber;
    private TransportMode transportMode;

    public GetEventDirectionsCommand(long eventNumber, TransportMode transportMode) {
        this.eventNumber = eventNumber;
        this.transportMode = transportMode;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that there is an event corresponding to the provided eventNumber
     * @verifies.that the event includes a venueAddress
     * @verifies.that the current user is a Consumer
     * @verifies.that the consumer's profile includes an address
     */

    @Override
    public void execute(Context context, IView view) {
        IEventState eventState = context.getEventState();
        Event event = eventState.findEventByNumber(eventNumber);
        if (event == null) {
            view.displayFailure("GetEventDirectionsCommand", LogStatus.GET_EVENT_DIRECTIONS_NO_SUCH_EVENT,
                    Map.of("eventNumber", eventNumber));
            directionsResult = new String[0];
            return;
        }

        if (event.getVenueAddress() == null || event.getVenueAddress().isBlank()) {
            view.displayFailure("GetEventDirectionsCommand", LogStatus.GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS,
                    Map.of("event", event));
            directionsResult = new String[0];
            return;
        }

        IUserState userState = context.getUserState();
        User currentUser = userState.getCurrentUser();
        if (!(currentUser instanceof Consumer)) {
            view.displayFailure("GetEventDirectionsCommand", LogStatus.GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER,
                    Map.of("currentUser", currentUser));
            directionsResult = new String[0];
            return;
        }

        Consumer consumer = (Consumer)currentUser;
        if (consumer.getAddress() == null || consumer.getAddress().isBlank()) {
            view.displayFailure("GetEventDirectionsCommand", LogStatus.GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS,
                    Map.of("consumer", consumer));
            directionsResult = new String[0];
            return;
        }

        MapSystem mapSystem = context.getMapSystem();
        String consumerAddress = consumer.getAddress();
        String venueAddress = event.getVenueAddress();
        GHPoint consumerAddressPoint = null;
        GHPoint venueAddressPoint = null;
        try {
            String[] consumerAddressCoordinates = consumerAddress.split(" ");
            String modifiedConsumerAddress = consumerAddressCoordinates[0] + "," + consumerAddressCoordinates[1];
            consumerAddressPoint = mapSystem.convertToCoordinates(modifiedConsumerAddress);
        } catch (Exception e) {
            view.displayFailure("GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_CONSUMER_ADDRESS_INVALID,
                    Map.of("consumerAddress", consumerAddress));
        }
        try {
            String[] venueAddressCoordinates = venueAddress.split(" ");
            String modifiedVenueAddress = venueAddressCoordinates[0] + "," + venueAddressCoordinates[1];
            venueAddressPoint = mapSystem.convertToCoordinates(modifiedVenueAddress);
        } catch (Exception e) {
            view.displayFailure("GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_VENUE_ADDRESS_INVALID,
                    Map.of("venueAddress", venueAddress));
        }
        ResponsePath path = mapSystem.routeBetweenPoints(transportMode, consumerAddressPoint, venueAddressPoint);
        InstructionList instructions = path.getInstructions();
        Translation translation = mapSystem.getTranslation();
        directionsResult = new String[instructions.size() + 1];

        double totalDistance = 0.0;
        for (int i = 1; i < directionsResult.length; ++i) {
            Instruction instruction = instructions.get(i);
            totalDistance += instruction.getDistance();
            String direction = "distance " + instruction.getDistance() + " for instruction: "
                    + instruction.getTurnDescription(translation);
            directionsResult[i] = direction;
        }
        directionsResult[0] = new String("total distance: " + totalDistance);
        view.displaySuccess("GetEventDirectionsCommand", LogStatus.GET_EVENT_DIRECTIONS_SUCCESS,
                Map.of("event", event, "consumer", consumer,
                        "directions", Arrays.toString(directionsResult)));
    }

    @Override
    public String[] getResult() {
        return directionsResult;
    }

    private enum LogStatus {
        GET_EVENT_DIRECTIONS_NO_SUCH_EVENT,
        GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS,
        GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER,
        GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS,
        GET_EVENT_DIRECTIONS_CONSUMER_ADDRESS_INVALID,
        GET_EVENT_DIRECTIONS_VENUE_ADDRESS_INVALID,
        GET_EVENT_DIRECTIONS_SUCCESS
    }
}
