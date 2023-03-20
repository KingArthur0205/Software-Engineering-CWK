package command;

import controller.Context;
import model.User;
import view.IView;

import java.util.Map;

/**
 * {@link UpdateProfileCommand} contains common behaviour shared between profile update commands
 */
public abstract class UpdateProfileCommand implements ICommand<Boolean> {
    protected Boolean successResult;

    /**
     * Common error checking method for all profile updates.
     *
     * @param context     object that provides access to global application state
     * @param view        allows passing information to the user interface
     * @param oldPassword password before the change, which must match the account's password
     * @param newEmail    specified email address to use for the account after the change
     * @return True/false based on whether the profile update is valid
     */
    protected boolean isProfileUpdateInvalid(Context context, IView view, String oldPassword, String newEmail) {
        User currentUser = context.getUserState().getCurrentUser();

        if (currentUser == null) {
            view.displayFailure(
                    "UpdateProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_NOT_LOGGED_IN);
            return true;
        }

        if (!currentUser.checkPasswordMatch(oldPassword)) {
            view.displayFailure(
                    "UpdateProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_WRONG_PASSWORD);
            return true;
        }

        if (context.getUserState().getAllUsers().containsKey(newEmail) &&
                context.getUserState().getAllUsers().get(newEmail) != currentUser) {
            view.displayFailure(
                    "UpdateProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE,
                    Map.of("old email", currentUser.getEmail(),
                            "new email", newEmail)
            );
            return true;
        }

        return false;
    }

    /**
     * Common update method for profile changes that involve a change of email (used as unique user identifier).
     * The method assumes error checking has already been performed before invoking this.
     * @param context  object that provides access to global application state
     * @param newEmail new email address for the current user
     */
    protected void changeUserEmail(Context context, String newEmail) {
        User currentUser = context.getUserState().getCurrentUser();
        String oldEmail = currentUser.getEmail();
        if (oldEmail.equals(newEmail)) {
            // Email hasn't changed, no need to do anything
            return;
        }

        currentUser.setEmail(newEmail);
        context.getUserState().getAllUsers().remove(oldEmail);
        context.getUserState().addUser(currentUser);
    }

    /**
     * @return True if successful, false if not, and null if the command has not been executed yet
     */
    @Override
    public Boolean getResult() {
        return successResult;
    }

    private enum LogStatus {
        USER_UPDATE_PROFILE_NOT_LOGGED_IN,
        USER_UPDATE_PROFILE_WRONG_PASSWORD,
        USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE
    }
}
