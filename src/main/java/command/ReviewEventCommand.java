package command;

import controller.Context;
import model.Review;
import view.IView;

public class ReviewEventCommandClass implements ICommand<Review> {
    private final Review reviewResult;
    private final long eventNumber;
    

    @Override
    public void execute(Context context, IView view) {

    }

    @Override
    public Review getResult() {
        return null;
    }
}
