import command.AddEventTagCommand;
import controller.Controller;
import model.Event;
import model.EventTag;
import org.junit.jupiter.api.Test;
import state.IEventState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AddEventTagSystemTests extends ConsoleTest{
    private static EventTag createEventTag(Controller controller, String tagName, Set<String> possibleValues,
                                           String defaultValue) {
        AddEventTagCommand eventCmd = new AddEventTagCommand(tagName, possibleValues, defaultValue);
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }

    @Test
    void addEventTagNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_USER_NOT_STAFF");

        // Check tag is null
        assertNull(tag);
        // Check that EventState doesn't have the tag as one element of the possibleTags
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addNewEventTag() {
        Controller controller = setUp();
        startOutputCapture();
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_SUCCESS");

        assertNotNull(tag);
        assertNotNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithNoPossibleValues() {}

    @Test
    void addTagWithOnePossibleValue() {}

    @Test
    void addTagWithClashedName() {
        Controller controller = setUp();
        startOutputCapture();

        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        EventTag tag2 = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value3", "value4")), "value3");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_SUCCESS");

        assertNull(tag2);
        assertEquals(tag, obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithInvalidDefaultValue() {}

    // Create a controller and setup the current user as a Staff.
    private Controller setUp() {
        Controller controller = createController();
        createStaff(controller);
        return controller;
    }

    // Obtain the actual Tag saved if successful, null otherwise
    private EventTag obtainEventTagFromState(Controller controller, String tagName) {
        IEventState eventState = controller.getContext().getEventState();
        return eventState.getPossibleTags().get(tagName);
    }
}
