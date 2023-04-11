package command;

import controller.Context;
import model.Staff;
import model.User;
import view.IView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SaveAppStateCommand implements ICommand<Boolean> {
    private String filename;
    private Boolean exportResult;

    public SaveAppStateCommand(String filename){
        this.exportResult = true;
        this.filename = filename;
    }


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

