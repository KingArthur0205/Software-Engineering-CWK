package command;

import controller.Context;
import model.Event;
import model.Staff;
import model.User;
import view.IView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * {@link SaveAppStateCommand} allows {@link Staff} members to save the state of system.
 */
public class SaveAppStateCommand implements ICommand<Boolean> {
    private String filename;
    private Boolean exportResult;

    /**
     * @param filename           the location of file that going to save
     */
    public SaveAppStateCommand(String filename){
        this.exportResult = true;
        this.filename = filename;
    }


    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that the currently logged-in user is a Staff member
     */
    @Override
    public void execute(Context context, IView view) {

        User currentUser = context.getUserState().getCurrentUser();
        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "SaveAppStateCommand",
                    LogStatus.SAVE_APP_STATE_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            exportResult = false;
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
             objectOutputStream.writeObject(context);

             fileOutputStream.close();
             objectOutputStream.close();

            view.displaySuccess(
                    "SaveAppStateCommand",
                    LogStatus.SAVE_APP_STATE_SUCCESSFUL
                    );
            exportResult = true;
        } catch (IOException e) {
            view.displayFailure(
                    "SaveAppStateCommand",
                    LogStatus.SAVE_APP_STATE_UNKNOWN_FAIL,
                    Map.of("file: ", filename));
        }
    }

    @Override
    public Boolean getResult() {
        return exportResult;
    }
    private enum LogStatus {
        SAVE_APP_STATE_USER_NOT_STAFF,
        SAVE_APP_STATE_SUCCESSFUL,
        SAVE_APP_STATE_UNKNOWN_FAIL
    }
}


