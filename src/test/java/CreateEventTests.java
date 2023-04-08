import command.CreateEventCommand;
import command.LogoutCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;

public class CreateEventTests extends ConsoleTest {
    private static void registerPawsForAwwws(Controller controller) {
        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
    }

    private static Event createEvent(Controller controller,
                                     LocalDateTime startDateTime,
                                     LocalDateTime endDateTime) {
        String address = "55.944377051350656 -3.18913215894117";
        System.out.println(address);
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                500,
                100,
                address,
                "Come and enjoy some pets for pets",
                startDateTime,
                endDateTime,
                new EventTagCollection()
        );
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }

    @Test
    void createNonTicketedEvent() {
        startOutputCapture();
        createStaffAndEvent(0, 1);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );
    }

    @Test
    void createTicketedEvent() {
        startOutputCapture();
        Controller controller = createStaffAndEvent(1, 1);
        controller.runCommand(new LogoutCommand());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS"
        );
    }

    @Test
    void createEventInThePast() {
        Controller controller = createController();
        startOutputCapture();
        registerPawsForAwwws(controller);
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).plusHours(2)
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_IN_THE_PAST"
        );
    }

    @Test
    void createEventWithEndBeforeStart() {
        Controller controller = createController();
        startOutputCapture();
        registerPawsForAwwws(controller);
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).minusHours(2)
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_START_AFTER_END"
        );
    }

    @Test
    void createEventNotWithinBoundary() {}

    @Test
    void createEventWithInvalidAddress1() {
        String hi = "55.944377051350656,-3.18913215894117";
        String[] arr = hi.split(",");
        System.out.println(arr.length);
    }

    @Test
    void createEventWithInvalidAddress2() {}
}
