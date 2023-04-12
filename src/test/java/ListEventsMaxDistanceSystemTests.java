import command.*;
import controller.Controller;
import model.Consumer;
import model.EventTagCollection;
import model.EventType;
import model.TransportMode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ListEventsMaxDistanceSystemTests extends ConsoleTest{
    private Controller setUp() {
        Controller controller = createController();
        createStaff(controller);
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Event1",
                EventType.Theatre,
                500,
                100,
                "55.944377051350656 -3.18913215894117", //George Square, Edinburgh
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection()
        );
        CreateEventCommand eventCmd2 = new CreateEventCommand(
                "Event2",
                EventType.Theatre,
                500,
                100,
                "55.923588787270965 -3.174528332470629", //George Square, Edinburgh
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection()
        );

        CreateEventCommand eventCmd3 = new CreateEventCommand(
                "Event3",
                EventType.Theatre,
                500,
                100,
                "55.97582748797642 -3.166806878460426", //George Square, Edinburgh
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection()
        );
        controller.runCommand(eventCmd);
        controller.runCommand(eventCmd2);
        controller.runCommand(eventCmd3);

        return controller;
    }
    @Test
    void listEventsWhenNotLoggedIn() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_NOT_LOGGED_IN");

        assertNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listWhenUserIsNotConsumer() {
        Controller controller = setUp();

        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER");

        assertNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listWhenConsumerAddressIsNull() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        createConsumer(controller);
        UpdateConsumerProfileCommand updateConsumerProfileCommand  =
                new UpdateConsumerProfileCommand("123456","cici","gongzi@163.com","12398",null,"gong",new EventTagCollection());
        controller.runCommand(updateConsumerProfileCommand);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_CONSUMER_ADDRESS_INVALID");

        assertNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listWhenConsumerAddressIsBlank() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        createConsumer(controller);
        UpdateConsumerProfileCommand updateConsumerProfileCommand  =
                new UpdateConsumerProfileCommand("123456","cici","gongzi@163.com","12398","","gong",new EventTagCollection());
        controller.runCommand(updateConsumerProfileCommand);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_CONSUMER_ADDRESS_INVALID");

        assertNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listWhenConsumerAddressFormatIsWrong() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);

        controller.getContext().getUserState().setCurrentUser(new Consumer("Elon","elon@gmail.com","123","blabal","123"));
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_CONSUMER_ADDRESS_INVALID");

        assertNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listEventsMaxDistanceVenueAddressInValid() {
        Controller controller = setUp();
        controller.getContext().getEventState().createEvent("Event4",
                EventType.Theatre,
                500,
                100,
                "blbal", //Invalid
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection());
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");

        assertNotNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listEventsMaxDistanceVenueAddressNULL() {
        Controller controller = setUp();
        controller.getContext().getEventState().createEvent("Event4",
                EventType.Theatre,
                500,
                100,
                null, //Invalid
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection());
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");

        assertNotNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listEventsMaxDistanceVenueAddressIsBlank() {
        Controller controller = setUp();
        controller.getContext().getEventState().createEvent("Event4",
                EventType.Theatre,
                500,
                100,
                "", //Invalid
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection());
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");

        assertNotNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listEventsMaxDistanceSuccessWithBike() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.bike, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");

        assertNotNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listEventsMaxDistanceSuccessWithFoot() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.foot, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");

        assertNotNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listEventsMaxDistanceSuccessWithWheelchair() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.wheelchair, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");

        assertNotNull(listEventsMaxDistanceCommand.getResult());
    }

    @Test
    void listEventsMaxDistanceSuccessWithCar() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");

        assertNotNull(listEventsMaxDistanceCommand.getResult());
    }
}
