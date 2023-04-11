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
    // Create an AddEventTagCommand with specified tagName, possibleValues, and defaultValue and run it.
    private static EventTag createEventTag(Controller controller, String tagName, Set<String> possibleValues,
                                           String defaultValue) {
        AddEventTagCommand eventCmd = new AddEventTagCommand(tagName, possibleValues, defaultValue);
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }

    // Create a controller and set the current user as a Staff.
    private Controller setUp() {
        Controller controller = createController();
        createStaff(controller);
        return controller;
    }

    // Obtain the actual Tag saved in EventState if successful, null otherwise
    private EventTag obtainEventTagFromState(Controller controller, String tagName) {
        IEventState eventState = controller.getContext().getEventState();
        return eventState.getPossibleTags().get(tagName);
    }

    @Test
    void addEventTagNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        // tag is the result of the AddEventTagCommand class
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_USER_NOT_STAFF");

        // Check if tag is not created(null)
        assertNull(tag);
        // Check that EventState doesn't have the tag with the name "tag1" stored
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addEventTagAsConsumer() {
        Controller controller = createController();
        createConsumer(controller);

        startOutputCapture();
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_USER_NOT_STAFF");

        // Check if tag is not created(null)
        assertNull(tag);
        // Check that EventState doesn't have the tag with the name "tag1" stored
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addNewEventTagSuccess() {
        // Create a controller and log in as a Staff
        Controller controller = setUp();
        startOutputCapture();
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_SUCCESS");

        // Check that "tag1" is added into EventState(not null)
        assertNotNull(tag);
        // Check that EventState has the tag with the name "tag1" stored
        assertNotNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithNoPossibleValues() {
        // Create a controller and log in as a Staff
        Controller controller = setUp();
        startOutputCapture();
        // Note: possibleValue is empty
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_TOO_FEW_POSSIBLE_VALUES");

        // Check if tag is not created(null)
        assertNull(tag);
        // Check that EventState doesn't have the tag with the name "tag1" stored
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithOnePossibleValue() {
        Controller controller = setUp();
        startOutputCapture();
        // Note: possibleValues only has one value
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1")), "value1");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_TOO_FEW_POSSIBLE_VALUES");

        // Check if tag is not created(null)
        assertNull(tag);
        // Check that EventState doesn't have the tag with the name "tag1" stored
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithClashedName() {
        Controller controller = setUp();
        startOutputCapture();
        // Create two tags with the same names but different possible values and default value.
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value1");
        EventTag tag2 = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value3", "value4")), "value3");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_SUCCESS", "ADD_EVENT_TAG_NAME_CLASH");

        // Check if tag1 is added successfully(not null)
        assertNotNull(tag);
        // Check that tag1 is actually stored into EventState
        assertEquals(tag, obtainEventTagFromState(controller, "tag1"));
        // Check that tag2 is not added successfully(null)
        assertNull(tag2);
        // Check the tag stored is not the second one created
        assertNotEquals(tag2, obtainEventTagFromState(controller, "tag1"));
    }

    @Test
    void addTagWithInvalidDefaultValue() {
        Controller controller = setUp();
        startOutputCapture();
        // Note: the default value is not one of the possible values
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "value3");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_DEFAULT_VALUE_NOT_POSSIBLE");

        assertNull(tag);
        assertNull(obtainEventTagFromState(controller, "tag1"));
    }
}
