package command;

import controller.Context;
import model.EventTag;
import model.Staff;
import model.User;
import state.EventState;
import state.IEventState;
import view.IView;

import java.util.Map;
import java.util.Set;


/**
 * {@link AddEventTagCommand} allows {@link model.Staff} to create new {@link EventTag}s
 * The command applies for currently logged-in user.
 */
public class AddEventTagCommand implements ICommand<EventTag>{
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
        User currentUser = context.getUserState().getCurrentUser();
        // Check if user is Staff
        if (!(currentUser instanceof Staff)) {
            view.displayFailure("AddEventTagCommand",
                    LogStatus.ADD_EVENT_TAG_USER_NOT_STAFF,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            eventTagResult = null;
            return;
        }

        // Check the tagName doesn't clash with any existing ones
        IEventState eventState = context.getEventState();
        if (eventState.getPossibleTags().containsKey(tagName)) {
            view.displayFailure("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_NAME_CLASH,
                    Map.of("tagName", tagName));
            eventTagResult = null;
            return;
        }

        // Check if there are at least two tag values
        if (tagValues == null || tagValues.size() < 2) {
            view.displayFailure("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_TOO_FEW_POSSIBLE_VALUES,
                    Map.of("tagValues", tagValues));
            eventTagResult = null;
            return;
        }

        // Check if the default value is in the list of possible values
        if (!(tagValues.contains(defaultValue))) {
            view.displayFailure("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_DEFAULT_VALUE_NOT_POSSIBLE,
                    Map.of("tagValues", tagValues, "defaultValues", defaultValue));
            eventTagResult = null;
            return;
        }

        // Add tag
        EventTag tag = eventState.createEventTag(tagName, tagValues, defaultValue);
        view.displaySuccess("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_SUCCESS,
                Map.of("tagName", tagName, "tagValues", tagValues, "defaultValue", defaultValue));
        eventTagResult = tag;
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
        ADD_EVENT_TAG_NAME_CLASH,
        ADD_EVENT_TAG_DEFAULT_VALUE_NOT_POSSIBLE,
        ADD_EVENT_TAG_TOO_FEW_POSSIBLE_VALUES,
        ADD_EVENT_TAG_SUCCESS
    }
}
