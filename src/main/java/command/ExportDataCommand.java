package command;

import controller.Context;
import view.IView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ExportDataCommand implements ICommand<Boolean> {


    @Override
    public void execute(Context context, IView view) {
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
}

