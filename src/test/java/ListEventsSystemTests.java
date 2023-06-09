import command.*;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
    void listUserEventsOnly() {
        Controller controller = setup();
        ListEventsCommand cmd = new ListEventsCommand(false, true, null);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");

        assertNotNull(cmd.getResult());
        assertEquals(3, cmd.getResult().size());
    }

    @Test
    void listSearchDateIsNotNullWhenActiveEventsOnly() {
        Controller controller = setup();
        ListEventsCommand cmd = new ListEventsCommand(false, true, LocalDateTime.now().plusHours(3).toLocalDate());
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");

        assertNotNull(cmd.getResult());
        assertEquals(3, cmd.getResult().size());
    }

    @Test
    void listSearchDateIsNotNull() {
        Controller controller = setup();
        controller.getContext().getEventState().createEvent("Event1",
                EventType.Theatre,
                30,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().minusHours(24),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=false"));
        ListEventsCommand cmd = new ListEventsCommand(false, false, LocalDateTime.now().plusHours(3).toLocalDate());
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");

        assertNotNull(cmd.getResult());
        assertEquals(4, cmd.getResult().size());
    }

    @Test
    void listNotOnlyUserEvents() {
        Controller controller = setup();
        ListEventsCommand cmd = new ListEventsCommand(false, false, null);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");

        assertNotNull(cmd.getResult());
        assertEquals(3, cmd.getResult().size());
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

        assertNull(listEventsCommand.getResult());
    }


    @Test
    void listEventsUserIsStaff() {
        Controller controller = setup();
        ListEventsCommand cmd = new ListEventsCommand(true, false, null);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");

        assertNotNull(cmd.getResult());
        assertEquals(3, cmd.getResult().size());
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

        assertNotNull(cmd.getResult());
        assertEquals(3, cmd.getResult().size());
    }

    @Test
    void listActiveEventsOnly() {
        Controller controller = setup();
        createEvent(controller, 500, 2);
        ListEventsCommand cmd = new ListEventsCommand(true, false, null);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");

        assertNotNull(cmd.getResult());
        assertEquals(4, cmd.getResult().size());
    }

    @Test
    void listEventsFittingConsumerPreference() {
        Controller controller = setup();

        CreateEventCommand eventCmd = new CreateEventCommand(
                "Event4",
                EventType.Theatre,
                10,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(12),
                new EventTagCollection("hasAirFiltration=true")
        );
        CreateEventCommand eventCmd2 = new CreateEventCommand(
                "Event5",
                EventType.Theatre,
                10,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(12),
                new EventTagCollection("hasAirFiltration=false")
        );
        controller.runCommand(eventCmd);
        controller.runCommand(eventCmd2);

        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        ListEventsCommand cmd = new ListEventsCommand(true, false, null);
        createConsumer(controller);
        UpdateConsumerProfileCommand updateConsumerProfileCommand =
                new UpdateConsumerProfileCommand("123456", "hi","gongzi@163.com","781230", "55.94368888764689 -3.1888246174917114", "password",
                        new EventTagCollection("hasAirFiltration=false"));
        controller.runCommand(updateConsumerProfileCommand);
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("LIST_EVENTS_SUCCESS");

        assertNotNull(cmd.getResult());
        assertEquals(1, cmd.getResult().size());
    }
}
