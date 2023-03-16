package command;

import controller.Context;
import model.Staff;
import model.User;
import view.IView;

import java.util.Map;

/**
 * {@link UpdateStaffProfileCommand} allows {@link Staff} members to update their account
 * details in the system.
 */
public class UpdateStaffProfileCommand extends UpdateProfileCommand {
    private final String oldPassword;
    private final String newEmail;
    private final String newPassword;

    /**
     * @param oldPassword   account password before the change, required for extra security verification. Must not be null
     * @param newEmail      new email address for this staff account.  Must not be null
     * @param newPassword   new password for this account. Must not be null
     */
    public UpdateStaffProfileCommand(String oldPassword,
                                     String newEmail,
                                     String newPassword) {
        this.oldPassword = oldPassword;
        this.newEmail = newEmail;
        this.newPassword = newPassword;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that oldPassword, newEmail, and newPassword are all not null
     * @verifies.that current user is logged in
     * @verifies.that oldPassword matches the current user's password
     * @verifies.that there is no other user already registered with the same email address as newEmail
     * @verifies.that currently logged-in user is a Staff member
     */
    @Override
    public void execute(Context context, IView view) {
        if (oldPassword == null || newEmail == null || newPassword == null) {
            view.displayFailure(
                    "UpdateStaffProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL,
                    Map.of("oldPassword", "***",
                            "newOrgEmail", String.valueOf(newEmail),
                            "newPassword", "***"
                    ));
            successResult = null;
            return;
        }

        if (isProfileUpdateInvalid(context, view, oldPassword, newEmail)) {
            successResult = false;
            return;
        }

        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "UpdateStaffProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_NOT_STAFF);
            successResult = false;
            return;
        }

        changeUserEmail(context, newEmail);
        currentUser.updatePassword(newPassword);

        view.displaySuccess(
                "UpdateStaffProfileCommand",
                LogStatus.USER_UPDATE_PROFILE_SUCCESS,
                Map.of("newEmail", newEmail,
                        "newPassword", "***")
        );
        successResult = true;
    }

    private enum LogStatus {
        USER_UPDATE_PROFILE_SUCCESS,
        USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL,
        USER_UPDATE_PROFILE_NOT_STAFF
    }
}
