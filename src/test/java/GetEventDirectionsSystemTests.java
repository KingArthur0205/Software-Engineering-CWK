import command.GetEventDirectionsCommand;
import command.LogoutCommand;
import controller.Controller;
import model.EventTagCollection;
import model.EventType;
import model.TransportMode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class GetEventDirectionsSystemTests extends ConsoleTest{
    private Controller setUp() {
        Controller controller = createStaffAndEvent(3, 10);
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        return controller;
    }
    @Test
    void getDirectionWhenEventNumberNotExist() {
        startOutputCapture();
        Controller controller = setUp();
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(2, TransportMode.car);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS","USER_LOGOUT_SUCCESS","REGISTER_CONSUMER_SUCCESS", "USER_LOGIN_SUCCESS",
                "GET_EVENT_DIRECTIONS_NO_SUCH_EVENT");
    }

    @Test
    void getDirectionWhenEventAddressIsNull() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                null,
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS", "USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS");
    }

    @Test
    void getDirectionWhenEventAdrressIsBlank() {
        Controller controller = createController();

        startOutputCapture();
        createConsumer(controller);
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS", "USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS");
    }

    @Test
    void getDirectionWhenNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.944377051350656 -3.18913215894117",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER");
    }

    @Test
    void getDirectionWhenUserIsStaff() {}

    @Test
    void getDirectionWhenConsumerAddressIsNull() {}

    @Test
    void getDirectionWhenConsumerAddressIsBlank() {}

    @Test
    void getDirectionWhenConsumerAddressFormatIsIncorrect() {}

    @Test
    void getDirectionWhenVenueAddressFormatIsIncorrect() {}

    @Test
    void getDirectionSuccess() {}
}
