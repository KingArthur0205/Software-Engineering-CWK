import command.AddEventTagCommand;
import controller.Controller;
import model.EventTag;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class AddEventTagTests extends ConsoleTest{
    private static EventTag createEventTag(Controller controller, String tagName, Set<String> possibleValues,
                                           String defaultValue) {
        AddEventTagCommand eventCmd = new AddEventTagCommand(tagName, possibleValues, defaultValue);
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }
}
