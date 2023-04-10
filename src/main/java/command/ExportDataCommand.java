package command;

import controller.Context;
import model.Event;
import model.Staff;
import model.User;
import view.IView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class ExportDataCommand implements ICommand<Boolean> {
    private Boolean eventResult;

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
                    "ExportDataCommand",
                    ExportDataCommand.LogStatus.Export_Data_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            eventResult = false;
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream("context.ser");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
             objectOutputStream.writeObject(context);

             fileOutputStream.close();
             objectOutputStream.close();

             System.out.println("Context object has been serialized to context.ser");
        } catch (IOException e) {
            System.err.println("Error serializing context object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Boolean getResult() {
        return null;
    }
    private enum LogStatus {
        Export_Data_USER_NOT_STAFF,

    }
}


