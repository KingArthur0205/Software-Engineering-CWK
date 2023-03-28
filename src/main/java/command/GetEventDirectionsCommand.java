package command;

import controller.Context;
import model.Event;
import model.TransportMode;
import state.IEventState;
import view.IView;

import java.util.Map;

public class GetEventDirectionsCommand implements ICommand<String[]>{
    private String[] directionsResult;
    private final long eventNumber;
    private TransportMode transportMode;

    public GetEventDirectionsCommand(long eventNumber, TransportMode transportMode) {
        this.eventNumber = eventNumber;
        this.transportMode = transportMode;
    }

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
    }

    @Override
    public String[] getResult() {
        return directionsResult;
    }

    private enum LogStatus {
        GET_EVENT_DIRECTIONS_NO_SUCH_EVENT,
        GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS
    }
}
