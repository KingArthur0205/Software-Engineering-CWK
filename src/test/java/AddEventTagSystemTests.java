import command.AddEventTagCommand;
import controller.Controller;
import model.EventTag;
import org.junit.jupiter.api.Test;

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
        createEventTag(controller, "tag1", new HashSet<>(Arrays.asList("value1")), "defValue");
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_USER_NOT_STAFF");
    }

    @Test
    void addNewEventTag() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        EventTag tag = createEventTag(controller, "tag1",
                new HashSet<>(Arrays.asList("value1", "value2")), "defValue");
        assertNotNull(tag);
        stopOutputCaptureAndCompare("ADD_EVENT_TAG_SUCCESS");
    }

    @Test
    void addAlreadyExistedTag() {}
}
