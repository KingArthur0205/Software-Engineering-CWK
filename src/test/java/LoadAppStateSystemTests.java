
import command.*;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadAppStateSystemTests extends ConsoleTest{

    private LocalDateTime time = LocalDateTime.now();
    private static void createStaffAndFile(Controller controller){
        createStaff(controller);
        controller.runCommand(new SaveAppStateCommand("test.ser"));
    }

    protected static Booking BookFirstEvent(Controller controller, int numTicketsRequested, User consumer) {
        ListEventsCommand eventsCmd = new ListEventsCommand(false, false, null);
        controller.runCommand(eventsCmd);
        List<Event> events = eventsCmd.getResult();

        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookCmd = new BookEventCommand(
                firstEventNumber, numTicketsRequested
        );
        controller.runCommand(bookCmd);
        return bookCmd.getResult();
    }

    protected static Event createEvent(Controller controller, int numTickets, int eventDelayHours , LocalDateTime time) {
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                numTickets,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                time.plusHours(eventDelayHours),
                time.plusHours(eventDelayHours + 1),
                new EventTagCollection());
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }
    private static EventTag createEventTag(Controller controller, String tagName, Set<String> possibleValues,
                                           String defaultValue) {
        AddEventTagCommand eventCmd = new AddEventTagCommand(tagName, possibleValues, defaultValue);
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }

    @Test
    void loadDataNotLoggedIn() {
        Controller controller = createController();
        createStaffAndFile(controller);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test");
        controller.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_USER_NOT_STAFF"
        );
        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataLoggedInConsumer() {
        Controller controller = createController();
        createConsumer(controller);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test");
        controller.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_USER_NOT_STAFF"
        );

        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataFileNotFound() {
        Controller controller = createController();
        createStaffAndFile(controller);
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test1");
        controller.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_FILE_NOT_FOUND"
        );
        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataFileClashingUsers() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStaffAndFile(controller);
        createStaff(controller1);
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test.ser");
        controller1.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_USERS"
        );
        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataFileClashingEvents() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStaff(controller);
        createEvent(controller, 5,5,time);
        controller.runCommand(new SaveAppStateCommand("test.ser"));
        controller1.runCommand(new RegisterStaffCommand(
                "sell-the-pups@pawsforawwws.org",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
        createEvent(controller1, 5,5,time);
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test.ser");
        controller1.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_EVENTS"
        );

        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataFileEventTagClashingDefaultValue() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStaff(controller);
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        controller.runCommand(new SaveAppStateCommand("test.ser"));
        controller1.runCommand(new RegisterStaffCommand(
                "sell-the-pups@pawsforawwws.org",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
        EventTag tag1 = createEventTag(controller1, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value2");
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test.ser");
        controller1.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_EVENT_TAGS"
        );

        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataFileEventTagClashingPossibleValues() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStaff(controller);
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        controller.runCommand(new SaveAppStateCommand("test.ser"));
        controller1.runCommand(new RegisterStaffCommand(
                "sell-the-pups@pawsforawwws.org",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
        EventTag tag1 = createEventTag(controller1, "tag1",
                new HashSet<>(Arrays.asList("value1", "value3")), "value1");
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test.ser");
        controller1.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_EVENT_TAGS"
        );
        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataFileUserClash() {
        Controller controller = createController();
        createStaff(controller);

        controller.runCommand(new SaveAppStateCommand("test.ser"));

        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test.ser");
        controller.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_SUCCESSFUL"
        );

        assertTrue(loadAppStateCommand.getResult());
    }

    // The reason why this test passes is because our command passes to add this tag into the system
    @Test
    void loadDataFileEventTagClashingNameButSameValues() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStaff(controller);
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        controller.runCommand(new SaveAppStateCommand("test.ser"));

        // We run this command to avoid registering clashing users
        controller1.runCommand(new RegisterStaffCommand(
                "sell-the-pups@pawsforawwws.org",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
        EventTag tag1 = createEventTag(controller1, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test.ser");
        controller1.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_SUCCESSFUL"
        );

        assertTrue(loadAppStateCommand.getResult());
    }

    @Test
    void loadDataFileClashingBookings() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStaff(controller);
        createEvent(controller, 5,5,time);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                "bring-in-the-cash@pawsforawwws.org",
                "very insecure password 123"));
        controller.runCommand(new SaveAppStateCommand("save1.ser"));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand("i-would-never-steal-a@dog.xd","123456"));

        controller.runCommand(new BookEventCommand(1,1));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                "bring-in-the-cash@pawsforawwws.org",
                "very insecure password 123"));
        controller.runCommand(new SaveAppStateCommand("save2.ser"));
        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("save2.ser");
        controller.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_BOOKINGS"
        );

        assertFalse(loadAppStateCommand.getResult());
    }

    @Test
    void loadData() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStaff(controller);
        createEvent(controller, 5,5,time);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookFirstEvent(controller,1);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand("bring-in-the-cash@pawsforawwws.org",
                "very insecure password 123"));

        controller.runCommand(new SaveAppStateCommand("test.ser"));

        controller1.runCommand(new RegisterStaffCommand(
                "sell-the-pups@pawsforawwws.org",
                "very insecure password 123",
                "Nec temere nec timide"
        ));

        startOutputCapture();
        LoadAppStateCommand loadAppStateCommand = new LoadAppStateCommand("test.ser");
        controller1.runCommand(loadAppStateCommand);
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_SUCCESSFUL"
        );

        assertTrue(loadAppStateCommand.getResult());
    }
}

