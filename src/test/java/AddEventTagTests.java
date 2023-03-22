import command.AddEventTagCommand;
import controller.Controller;
import model.EventTag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AddEventTagTests extends ConsoleTest{
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
}
