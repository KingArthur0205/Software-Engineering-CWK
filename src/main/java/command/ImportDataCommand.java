package command;

import controller.Context;
import model.Staff;
import model.User;
import view.IView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class ImportDataCommand implements ICommand<Boolean> {
    private Boolean importResult = false;

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that the currently logged-in user is a Staff member
     * @verifies.that For event tags, clashing tags (with the same name but different
     * values) cause the whole operation to be cancelled. This is because
     * replacing or ignoring tags could mess up Events or Consumers using
     * either old or new tags.
     * @verifies.that For users, any user email clashes for users that are not the same
     * result in the operation being cancelled. This is because overwriting or
     * omitting users could break saved Bookings.
     * @verifies.that For events, clashing events (with the same title, startDateTime, and
     * endDateTime) that are not identical cause the operation to be
     * cancelled because otherwise this could result in either duplicate
     * events or issues with connected bookings.
     * @verifies.that For bookings, clashing bookings (by the same Consumer for the same
     * Events with the same bookingDateTime) cause the operation to be
     * cancelled because otherwise this could result in duplicate bookings.
     */

    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "ImportDataCommand",
                    ImportDataCommand.LogStatus.Import_Data_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            importResult = false;
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream("context.ser");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
             context.setContext((Context) objectInputStream.readObject());

             System.out.println("Context object has been deserialized from context.ser");
        } catch (IOException | ClassNotFoundException e) {
             System.err.println("Error deserializing context object: " + e.getMessage());
             e.printStackTrace();
             return;
        }
    }

    @Override
    public Boolean getResult() {
        return importResult;
    }

    private enum LogStatus {
        Import_Data_USER_NOT_STAFF,
    }
}
