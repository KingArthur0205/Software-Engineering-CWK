import command.CreateEventCommand;
import command.ListEventsCommand;
import command.LogoutCommand;
import controller.Controller;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ListEventsSystemTests extends ConsoleTest{
    private Controller setup() {
        Controller controller = createController();
        createStaff(controller);
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Event1",
                EventType.Theatre,
                30,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=false")
        );
        CreateEventCommand eventCmd2 = new CreateEventCommand(
                "Event2",
                EventType.Theatre,
                30,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=true")
        );
        CreateEventCommand eventCmd3 = new CreateEventCommand(
                "Event3",
                EventType.Theatre,
                30,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=true,hasAirFiltration=true")
        );
        controller.runCommand(eventCmd);
        controller.runCommand(eventCmd2);
        controller.runCommand(eventCmd3);

        return controller;
    }
    @Test
    void listNotOnlyUserEvents() {
        Controller controller = setup();
        ListEventsCommand cmd = new ListEventsCommand(false, false, null);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");
    }

    @Test
    void listEventsUserNotLoggedIn() {
        Controller controller = setup();
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        ListEventsCommand listEventsCommand = new ListEventsCommand(true, false, null);
        startOutputCapture();
        controller.runCommand(listEventsCommand);
        stopOutputCaptureAndCompare("LIST_EVENTS_NOT_LOGGED_IN");
    }

    @Test
    void listEventsUserIsStaff() {
        Controller controller = setup();
        ListEventsCommand cmd = new ListEventsCommand(true, false, null);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");
    }

    @Test
    void listEventsUserIsConsumer() {
        Controller controller = setup();
        // Logout staff and log-in as a consumer
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        createConsumer(controller);
        ListEventsCommand cmd = new ListEventsCommand(false, false, null);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");
    }

    @Test
    void listActiveEventsOnly() {

    }

    @Test
    void listEventsFittingConsumerPreference() {

    }
}