package command;

import controller.Context;
import view.IView;

public class ImportCommand implements ICommand<Context> {
    @Override
    public void execute(Context context, IView view) {

    }

    @Override
    public Context getResult() {
        return null;
    }
}
