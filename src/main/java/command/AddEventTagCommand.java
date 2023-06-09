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
 * {@link AddEventTagCommand} allows {@link model.Staff} to create a new {@link EventTag}
 * The command applies for currently logged-in staff.
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

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that the current user is a Staff member
     * @verifies.that the new tag name doesn't clash with any existing tags
     * @verifies.that there are at least 2 tag values
     * @verifies.that the default tag value is in the list of possible tag value
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        // Verify if current user is a Staff
        if (!(currentUser instanceof Staff)) {
            view.displayFailure("AddEventTagCommand",
                    LogStatus.ADD_EVENT_TAG_USER_NOT_STAFF,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            eventTagResult = null;
            return;
        }

        // Verify that the tagName doesn't clash with any existing tag names
        IEventState eventState = context.getEventState();
        if (eventState.getPossibleTags().containsKey(tagName)) {
            view.displayFailure("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_NAME_CLASH,
                    Map.of("tagName", tagName));
            eventTagResult = null;
            return;
        }

        assert(tagValues != null);
        // Verify that at least two tag values are provided
        if (tagValues == null || tagValues.size() < 2) {
            view.displayFailure("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_TOO_FEW_POSSIBLE_VALUES,
                    Map.of("tagValues", tagValues));
            eventTagResult = null;
            return;
        }

        assert(defaultValue != null);
        // Verify that the default value is in the list of possible values
        if (!(tagValues.contains(defaultValue))) {
            view.displayFailure("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_DEFAULT_VALUE_NOT_POSSIBLE,
                    Map.of("tagValues", tagValues, "defaultValues", defaultValue));
            eventTagResult = null;
            return;
        }

        // Add tag to the system
        EventTag tag = eventState.createEventTag(tagName, tagValues, defaultValue);
        view.displaySuccess("AddEventTagCommand", LogStatus.ADD_EVENT_TAG_SUCCESS,
                Map.of("tagName", tagName, "tagValues", tagValues, "defaultValue", defaultValue));
        eventTagResult = tag;
    }

    /**
     * @return tag corresponding to the newly created {@link EventTag} if successful and null otherwise
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
