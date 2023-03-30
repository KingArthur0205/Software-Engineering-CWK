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
