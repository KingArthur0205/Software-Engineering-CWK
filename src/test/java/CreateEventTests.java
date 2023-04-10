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

    private static Event createEvent(Controller controller,
                                     LocalDateTime startDateTime,
                                     LocalDateTime endDateTime, String address) {
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
        createStaff(controller);
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).plusHours(2),
                "55.944377051350656 -3.18913215894117"
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
        createStaff(controller);
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).minusHours(2),
                "55.944377051350656 -3.18913215894117"
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_START_AFTER_END"
        );

        assertNull(event);
    }

    @Test
    void createEventNotWithinBoundary() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        Event event = createEvent(controller,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                "51.75731046567365 -0.0806357748349268"); // London
        stopOutputCaptureAndCompare("CREATE_EVENT_VENUE_ADDRESS_NOT_WITHIN_BOUNDARY");

    }

    @Test
    void createEventWithInvalidAddress1() {
        String hi = "55.944377051350656,-3.18913215894117";
        String[] arr = hi.split(",");
        System.out.println(arr.length);
    }

    @Test
    void createEventWithInvalidAddress2() {}
}
