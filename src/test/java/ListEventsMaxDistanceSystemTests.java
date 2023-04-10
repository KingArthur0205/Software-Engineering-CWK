import command.CreateEventCommand;
import command.ListEventsMaxDistanceCommand;
import command.LogoutCommand;
import controller.Controller;
import model.EventTagCollection;
import model.EventType;
import model.TransportMode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
    }

    @Test
    void listWhenUserIsNotConsumer() {

    }

    @Test
    void listWhenConsumerAddressIsNull() {}

    @Test
    void listWhenConsumerAddressIsBlank() {}

    @Test
    void listWhenConsumerAddressFormatIsWrong() {}

    @Test
    void listEventsMaxDistanceSuccess() {
        Controller controller = setUp();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsMaxDistanceCommand listEventsMaxDistanceCommand =
                new ListEventsMaxDistanceCommand(false, true, null, TransportMode.car, 10000.0);
        startOutputCapture();
        controller.runCommand(listEventsMaxDistanceCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_MAX_DISTANCE_SUCCESS");
    }
}
