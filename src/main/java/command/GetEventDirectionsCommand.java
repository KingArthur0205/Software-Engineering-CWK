package command;

import controller.Context;
import model.TransportMode;
import view.IView;

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

    }

    @Override
    public String[] getResult() {
        return new String[0];
    }
}
