package command;

import controller.Context;
import view.IView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ImportDataCommand implements ICommand<Boolean> {
    @Override
    public void execute(Context context, IView view) {
        try (FileInputStream fileInputStream = new FileInputStream("context.ser");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
             context.setContext((Context) objectInputStream.readObject());
             System.out.println("Context object has been deserialized from context.ser");
             System.out.println(context.getUserState().getAllUsers());
        } catch (IOException | ClassNotFoundException e) {
             System.err.println("Error deserializing context object: " + e.getMessage());
             e.printStackTrace();
             return;
        }
    }

    @Override
    public Boolean getResult() {
        return null;
    }
}
