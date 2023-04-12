import command.LogoutCommand;
import command.RegisterConsumerCommand;
import controller.Controller;
import model.Consumer;
import model.Staff;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterConsumerSystemTests extends ConsoleTest{
    @Test
    void registerWhenUserIsLoggedIn() {
        Controller controller = createController();
        createStaff(controller);
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "55.9487326960703 -3.1998482001001163",
                "elon");
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("USER_REGISTER_LOGGED_IN");

        assertNull(cmd.getResult());
    }

    @Test
    void registerWhenNameIsNull() {
        Controller controller = createController();
        RegisterConsumerCommand cmd = new RegisterConsumerCommand(null,
                "elon@gmail.com","00000000", "55.9487326960703 -3.1998482001001163",
                "elon");
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");

        assertNull(cmd.getResult());
    }

    @Test
    void registerWhenEmailAlreadyRegistered() {
        Controller controller = createController();
        createStaff(controller);
        LogoutCommand logoutCommand = new LogoutCommand();
        controller.runCommand(logoutCommand);
        // The email is used in createStaff command to register a staff
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Elon Musk",
                "bring-in-the-cash@pawsforawwws.org","00000000", "55.9487326960703 -3.1998482001001163",
                "elon");
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("USER_REGISTER_EMAIL_ALREADY_REGISTERED");

        assertNull(cmd.getResult());
    }

    @Test
    void registerWhenAddressFormatIsWrong() {
        Controller controller = createController();
        // The address is the address of a park in Germany
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "Wrong Format",
                "elon");
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("USER_REGISTER_INVALID_ADDRESS_FORMAT");

        assertNull(cmd.getResult());
    }

    @Test
    void registerWhenAddressIsOutOfBoundary() {
        Controller controller = createController();
        // The address is the address of a park in Germany
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "51.081912841069375 10.437909401077647",
                "elon");
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("USER_REGISTER_ADDRESS_OUT_OF_BOUNDS");

        assertNull(cmd.getResult());
    }

    @Test
    void registerSuccess() {
        Controller controller = createController();
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Elon Musk",
                "elon@gmail.com","00000000", "55.9487326960703 -3.1998482001001163",
                "elon");
        startOutputCapture();
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS", "USER_LOGIN_SUCCESS");

        assertNotNull(cmd.getResult());
        assertTrue(controller.getContext().getUserState().getAllUsers().containsKey(cmd.getResult().getEmail()));
        assertTrue(controller.getContext().getUserState().getAllUsers().containsValue(cmd.getResult()));
    }
}
