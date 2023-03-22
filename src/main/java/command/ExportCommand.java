package command;

import controller.Context;
import view.IView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ExportCommand implements ICommand<Context> {


    @Override
    public void execute(Context context, IView view){
//        FileOutputStream fileOutputStream
//                = new FileOutputStream("yourfile.txt");
//        ObjectOutputStream objectOutputStream
//                = new ObjectOutputStream(fileOutputStream);
//        objectOutputStream.writeObject(context);
//        objectOutputStream.flush();
//        objectOutputStream.close();
    }

    @Override
    public Context getResult() {
        return null;
    }
}

