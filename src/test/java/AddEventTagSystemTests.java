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
    void addTagWithNoPossibleValues() {
        Controller controller = setUp();
        startOutputCapture();
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_TOO_FEW_POSSIBLE_VALUES");

        assertNull(tag);
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithOnePossibleValue() {
        Controller controller = setUp();
        startOutputCapture();
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1")), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_TOO_FEW_POSSIBLE_VALUES");

        assertNull(tag);
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithClashedName() {
        Controller controller = setUp();
        startOutputCapture();
        // Create two tags with the same names but different possible and default values.
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        EventTag tag2 = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value3", "value4")), "value3");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_SUCCESS", "ADD_EVENT_TAG_NAME_CLASH");

        assertNull(tag2);
        // Check the tag stored is the first one created
        assertEquals(tag, obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithInvalidDefaultValue() {
        Controller controller = setUp();
        startOutputCapture();
        // Create two tags with the same names but different possible and default values.
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value3");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_DEFAULT_VALUE_NOT_POSSIBLE");

        assertNull(tag);
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

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
