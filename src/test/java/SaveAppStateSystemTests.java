import command.SaveAppStateCommand;
import controller.Controller;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveAppStateSystemTests extends ConsoleTest{
    @Test
    void exportDataNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        SaveAppStateCommand saveAppStateCommand = new SaveAppStateCommand("test");
        controller.runCommand(saveAppStateCommand);
        stopOutputCaptureAndCompare(
                "SAVE_APP_STATE_USER_NOT_STAFF"
        );

        assertFalse(saveAppStateCommand.getResult());
    }

    @Test
    void exportDataLoggedInConsumer() {
        Controller controller = createController();
        createConsumer(controller);
        startOutputCapture();
        SaveAppStateCommand saveAppStateCommand = new SaveAppStateCommand("test");
        controller.runCommand(saveAppStateCommand);
        stopOutputCaptureAndCompare(
                "SAVE_APP_STATE_USER_NOT_STAFF"
        );

        assertFalse(saveAppStateCommand.getResult());
    }

    @Test
    void exportDataPathNotExist() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        SaveAppStateCommand saveAppStateCommand = new SaveAppStateCommand("/Wrong path");
        controller.runCommand(saveAppStateCommand);
        stopOutputCaptureAndCompare(
                "SAVE_APP_STATE_UNKNOWN_FAIL"
        );

        assertFalse(saveAppStateCommand.getResult());
    }

    @Test
    void exportData() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        SaveAppStateCommand saveAppStateCommand = new SaveAppStateCommand("test");
        controller.runCommand(saveAppStateCommand);
        stopOutputCaptureAndCompare(
                "SAVE_APP_STATE_SUCCESSFUL"
        );

        assertTrue(saveAppStateCommand.getResult());
    }
}
