import command.*;
import controller.Controller;
import model.EventTagCollection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UpdateConsumerProfileSystemTests extends ConsoleTest {
    @Test
    void updateConsumerNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "USER_UPDATE_PROFILE_NOT_LOGGED_IN"
        );
    }

    @Test
    void updateConsumerAsProvider() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                STAFF_PASSWORD,
                "Alice",
                STAFF_EMAIL,
                "000",
                "",
                STAFF_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_NOT_CONSUMER"
        );
    }

    @Test
    void updateProviderNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                STAFF_PASSWORD,
                STAFF_EMAIL,
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "USER_UPDATE_PROFILE_NOT_LOGGED_IN"
        );
    }

    @Test
    void updateProviderAsConsumer() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                CONSUMER_PASSWORD,
                CONSUMER_EMAIL,
                CONSUMER_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_NOT_STAFF"
        );
    }

    @Test
    void updateConsumerWrongPassword() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD + "test",
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_WRONG_PASSWORD"
        );
    }

    @Test
    void updateConsumerName() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS"
        );
    }

    @Test
    void updateConsumerNullName() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                null,
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    @Test
    void updateConsumerTakenEmail() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Domain Parker",
                "peter@parker.com",
                "01324456897",
                "55.94872684464941 -3.199892044473183", // Edinburgh Castle
                "park before it's popular!"
        ));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE"
        );
    }

    @Test
    void updateConsumerNewEmailAndRelogin() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());

        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                "peter@parker.com",
                CONSUMER_PASSWORD
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );
    }

    @Test
    void updateConsumerEmailInvalidatesOldEmail() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());

        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                CONSUMER_EMAIL,
                CONSUMER_PASSWORD
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_EMAIL_NOT_REGISTERED"
        );
    }

    @Test
    void updateConsumerProfileWhenAddressIsNull() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                null,
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS"
        );
    }

    @Test
    void updateConsumerProfileWhenAddressIsBlank() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS"
        );
    }

    @Test
    void updateConsumerProfileWhenAddressFormatIsWrong() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "Wrong Format",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_ADDRESS_INVALID"
        );
    }

    @Test
    void updateConsumerProfileWhenAddressIsOutOfBoundary() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "56.0221304308943 10.75585809433506", // Denmark
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_ADDRESS_NOT_WITHIN_BOUNDARY"
        );
    }

    @Test
    void updateConsumerProfileWhenTagNameIsInvalid() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "55.970287266858655 -3.2397796972823607",
                CONSUMER_PASSWORD,
                new EventTagCollection("NonexistTag=true")
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_TAG_DO_NOT_EXIST"
        );
    }

    @Test
    void updateConsumerProfileWhenOneTagValueIsInvalid() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "55.970287266858655 -3.2397796972823607",
                CONSUMER_PASSWORD,
                new EventTagCollection("venuecapacity=<20,hasAirFiltration=nonexist")
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_TAG_VALUE_DO_NOT_EXIST"
        );
    }

    @Test
    void updateConsumerProfileSuccess() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "55.970287266858655 -3.2397796972823607",
                CONSUMER_PASSWORD,
                new EventTagCollection("venueCapacity=<20,hasAirFiltration=false")
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS"
        );
    }
}
