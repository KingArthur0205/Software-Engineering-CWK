
import command.*;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoadAppStateCommandSystemTests extends ConsoleTest{

    private LocalDateTime time = LocalDateTime.now();
    private static void createStandardFile(Controller controller){
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
        createStandardFile(controller);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("test"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_USER_NOT_STAFF"
        );
    }

    @Test
    void loadDataLoggedInConsumer() {
        Controller controller = createController();
        createConsumer(controller);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("test"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_USER_NOT_STAFF"
        );
    }

    @Test
    void loadDataFileNotFound() {
        Controller controller = createController();
        createStandardFile(controller);
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("test1"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_FILE_NOT_FOUND"
        );
    }

    @Test
    void loadDataFileClashingUsers() {
        Controller controller = createController();
        Controller controller1 = createController();
        createStandardFile(controller);
        createStaff(controller1);
        startOutputCapture();
        controller1.runCommand(new LoadAppStateCommand("test.ser"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_USERS"
        );
    }

    /*
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
        controller1.runCommand(new LoadAppStateCommand("test.ser"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_EVENTS"
        );
    }*/

    @Test
    void loadDataFileClashingEventTags() {
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
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        startOutputCapture();
        controller1.runCommand(new LoadAppStateCommand("test.ser"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_EVENTTAGS"
        );
    }

    /*
    @Test
    void loadDataFileClashingBookings() {
        Controller controller = createController();
        Controller controller1 = createController();
//
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
        controller.runCommand(new LoadAppStateCommand("save2.ser"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_CLASHING_BOOKINGS"
        );

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
        controller1.runCommand(new LoadAppStateCommand("test.ser"));
        stopOutputCaptureAndCompare(
                "LOAD_APP_STATE_SUCCESSFUL"
        );
    }
     */
}

