import command.SaveAppStateCommand;
import controller.Controller;
import org.junit.jupiter.api.Test;

public class SaveAppStateSystemTests extends ConsoleTest{
    @Test
    void exportDataNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new SaveAppStateCommand("test"));
        stopOutputCaptureAndCompare(
                "SAVE_APP_STATE_USER_NOT_STAFF"
        );
    }

    @Test
    void exportDataLoggedInConsumer() {
        Controller controller = createController();
        createConsumer(controller);
        startOutputCapture();
        controller.runCommand(new SaveAppStateCommand("test"));
        stopOutputCaptureAndCompare(
                "SAVE_APP_STATE_USER_NOT_STAFF"
        );
    }

    @Test
    void exportData() {
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        controller.runCommand(new SaveAppStateCommand("test"));
        stopOutputCaptureAndCompare(
                "SAVE_APP_STATE_SUCCESSFUL"
        );
    }
}
