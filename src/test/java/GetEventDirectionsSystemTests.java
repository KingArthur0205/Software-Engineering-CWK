import command.GetEventDirectionsCommand;
import command.LogoutCommand;
import command.RegisterConsumerCommand;
import controller.Controller;
import model.Consumer;
import model.EventTagCollection;
import model.EventType;
import model.TransportMode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertNull(getEventDirectionsCommand.getResult());
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
        assertNull(getEventDirectionsCommand.getResult());
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
        assertNull(getEventDirectionsCommand.getResult());
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
        assertNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionWhenUserIsStaff() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
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
        stopOutputCaptureAndCompare("REGISTER_STAFF_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER");
        assertNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionWhenConsumerAddressIsNull() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", null,
                "elon");
        controller.runCommand(registerConsumerCommand);
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
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS");
        assertNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionWhenConsumerAddressIsBlank() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "",
                "elon");
        controller.runCommand(registerConsumerCommand);
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
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS");
        assertNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionWhenConsumerAddressFormatIsIncorrect() {
        Controller controller = createController();
        startOutputCapture();
        controller.getContext().getUserState().setCurrentUser(new Consumer("Elon Musk",
                "elon@gmail.com","00000000", "Wrong Format",
                "elon"));
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
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_CONSUMER_ADDRESS_INVALID");
        assertNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionWhenVenueAddressFormatIsIncorrect() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "55.944377051350656 -3.18913215894117",
                "elon");
        controller.runCommand(registerConsumerCommand);
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "Wrong Format",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_VENUE_ADDRESS_INVALID");
        assertNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionSuccessWithCar() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "55.944377051350656 -3.18913215894117", //Edinburgh
                "elon");
        controller.runCommand(registerConsumerCommand);
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94340074067482 -4.265672460954532", // Glasgow
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_SUCCESS");
        assertNotNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionSuccessWithFoot() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "55.944377051350656 -3.18913215894117", //Edinburgh
                "elon");
        controller.runCommand(registerConsumerCommand);
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94340074067482 -4.265672460954532", // Glasgow
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.foot);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_SUCCESS");
        assertNotNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionSuccessWithBike() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "55.944377051350656 -3.18913215894117", //Edinburgh
                "elon");
        controller.runCommand(registerConsumerCommand);
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94340074067482 -4.265672460954532", // Glasgow
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.bike);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_SUCCESS");
        assertNotNull(getEventDirectionsCommand.getResult());
    }

    @Test
    void getDirectionSuccessWithWheelchair() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "55.944377051350656 -3.18913215894117", //Edinburgh
                "elon");
        controller.runCommand(registerConsumerCommand);
        controller.getContext().getEventState().createEvent("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94340074067482 -4.265672460954532", // Glasgow
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8),
                new EventTagCollection());
        GetEventDirectionsCommand getEventDirectionsCommand = new GetEventDirectionsCommand(1, TransportMode.wheelchair);
        controller.runCommand(getEventDirectionsCommand);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","GET_EVENT_DIRECTIONS_SUCCESS");
        assertNotNull(getEventDirectionsCommand.getResult());
    }
}
