import command.CreateEventCommand;
import command.LogoutCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class CreateEventTests extends ConsoleTest {

    private static Event createEvent(Controller controller,
                                     LocalDateTime startDateTime,
                                     LocalDateTime endDateTime, String address,
                                     EventTagCollection eventTagCollection) {
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                500,
                100,
                address,
                "Come and enjoy some pets for pets",
                startDateTime,
                endDateTime,
                eventTagCollection
        );
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }

    // Check if the event is created into the system
    private static boolean isContainedInSystem(Controller controller, Event cmdResult) {
        return controller.getContext().getEventState().getAllEvents().contains(cmdResult);
    }


    @Test
    void createEventWhenNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).minusHours(2),
                "55.944377051350656 -3.18913215894117", // George Square,
                new EventTagCollection()
        );
        stopOutputCaptureAndCompare("CREATE_EVENT_USER_NOT_STAFF");

        assertNull(event);
        assertFalse(isContainedInSystem(controller, event));
    }

    @Test
    void createEventWhenUserIsConsumer() {
        Controller controller = createController();
        createConsumer(controller);
        startOutputCapture();
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).minusHours(2),
                "55.944377051350656 -3.18913215894117", // George Square,
                new EventTagCollection()
        );
        stopOutputCaptureAndCompare("CREATE_EVENT_USER_NOT_STAFF");

        assertNull(event);
        assertFalse(isContainedInSystem(controller, event));
    }

    @Test
    void createEventWithNegativePrice() {
        Controller controller = createController();
        createStaff(controller);
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                500,
                -100,
                "55.944377051350656 -3.18913215894117",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                new EventTagCollection()
        );
        startOutputCapture();
        controller.runCommand(eventCmd);
        stopOutputCaptureAndCompare("CREATE_EVENT_NEGATIVE_TICKET_PRICE");

        assertNull(eventCmd.getResult());
        assertFalse(isContainedInSystem(controller, eventCmd.getResult()));
    }

    @Test
    void createEventWithSameTitleAndTime() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        LocalDateTime time = LocalDateTime.now();
        Event event1 = createEvent(controller,time.plusHours(2),
                time.plusHours(5),
                "55.944377051350656 -3.18913215894117", // George Square,
                new EventTagCollection());

        Event event2 = createEvent(controller,time.plusHours(2),
                time.plusHours(5),
                "55.944377051350656 -3.18913215894117", // George Square,
                new EventTagCollection());
        stopOutputCaptureAndCompare("CREATE_EVENT_SUCCESS", "CREATE_EVENT_TITLE_AND_TIME_CLASH");

        assertNotNull(event1);
        assertNull(event2);
        assertTrue(isContainedInSystem(controller, event1));
        assertFalse(isContainedInSystem(controller, event2));
    }

    @Test
    void createNonTicketedEvent() {
        startOutputCapture();
        Controller controller = createStaffAndEvent(0, 1);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );

        assertFalse(controller.getContext().getEventState().getAllEvents().isEmpty());
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
        assertFalse(controller.getContext().getEventState().getAllEvents().isEmpty());
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
                "55.944377051350656 -3.18913215894117",
                new EventTagCollection()
        );
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_IN_THE_PAST"
        );

        assertNull(event);
        assertFalse(isContainedInSystem(controller, event));
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
                "55.944377051350656 -3.18913215894117", // George Square
                new EventTagCollection()
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_START_AFTER_END"
        );

        assertNull(event);
        assertFalse(isContainedInSystem(controller, event));
    }

    @Test
    void createEventNotWithinBoundary() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        Event event = createEvent(controller,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                "51.75731046567365 -0.0806357748349268", new EventTagCollection()); // London
        stopOutputCaptureAndCompare("CREATE_EVENT_VENUE_ADDRESS_NOT_WITHIN_BOUNDARY");

        assertNull(event);
        assertFalse(isContainedInSystem(controller, event));

    }

    @Test
    void createEventWithInvalidAddress1() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        Event event = createEvent(controller,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                "Invalid Format", new EventTagCollection());
        stopOutputCaptureAndCompare("CREATE_EVENT_VENUE_ADDRESS_INCORRECT_FORMAT");

        assertNull(event);
        assertFalse(isContainedInSystem(controller, event));
    }

    @Test
    void createEventWithNonexistentTagName() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        Event event = createEvent(controller,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                "55.944377051350656 -3.18913215894117",
                new EventTagCollection("nonexistent=true")); // London
        stopOutputCaptureAndCompare("CREATE_EVENT_TAG_DO_NOT_EXIST");

        assertNull(event);
        assertFalse(isContainedInSystem(controller, event));
    }

    @Test
    void createEventWhenSelectedTagValueNotMatchPossibleValues() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        Event event = createEvent(controller,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                "55.944377051350656 -3.18913215894117",
                new EventTagCollection("hasAirFiltration=invalid"));
        stopOutputCaptureAndCompare("CREATE_EVENT_TAG_VALUE_DO_NOT_MATCH");

        assertNull(event);
        assertFalse(isContainedInSystem(controller,event));
    }

    @Test
    void createEventWhenTagFormatIsWrong() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        Event event = createEvent(controller,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                "55.944377051350656 -3.18913215894117",
                new EventTagCollection("Wrong, Format"));
        stopOutputCaptureAndCompare("CREATE_EVENT_SUCCESS");

        assertNotNull(event);
        assertTrue(isContainedInSystem(controller, event));
    }

    @Test
    void createEventSuccess() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        Event event = createEvent(controller,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                "55.944377051350656 -3.18913215894117",
                new EventTagCollection("hasAirFiltration=false,hasSocialDistancing=true"));
        stopOutputCaptureAndCompare("CREATE_EVENT_SUCCESS");

        assertNotNull(event);
        assertTrue(isContainedInSystem(controller, event));
    }
}
