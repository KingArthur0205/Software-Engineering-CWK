package command;

import controller.Context;
import model.EventTag;
import view.IView;

import java.util.Set;


/**
 * {@link AddEventTagCommand} allows {@link model.Staff} to create new {@link EventTag}s
 * The command applies for currently logged-in user.
 */
public class AddEventTagCommand implements ICommand{
    private final String tagName;
    private final Set<String> tagValues;
    private final String defaultValue;
    private EventTag eventTagResult;

    /**
     * @param tagName      name of the Tag
     * @param tagValues    possible values of the Tag
     * @param defaultValue default value of the Tag
     */
    public AddEventTagCommand(String tagName, Set<String> tagValues, String defaultValue) {
        this.tagName = tagName;
        this.tagValues = tagValues;
        this.defaultValue = defaultValue;
    }

    @Override
    public void execute(Context context, IView view) {

    }

    /**
     * @return tag corresponding to the newly created Tag if successful and null otherwise
     */
    @Override
    public EventTag getResult() {
        return eventTagResult;
    }

    private enum LogStatus {
        ADD_EVENT_TAG_USER_NOT_STAFF,
        ADD_EVENT_TAG_SUCCESS
    }
}
