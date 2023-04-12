package command;

import controller.Context;
import model.*;
import view.IView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.util.Set;

/**
 * {@link LoadAppStateCommand} allows {@link Staff} members to save the state of system.
 */
public class LoadAppStateCommand implements ICommand<Boolean> {
    private Boolean importResult;
    private String filename;
    Context contextNew = null;

    /**
     * @param filename           the location of file that going to load from
     */
    public LoadAppStateCommand(String filename) {
        this.filename = filename;
        importResult = true;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Consumer
     * @verifies.that imported users are not clashing
     * @verifies.that imported events are not clashing
     * @verifies.that imported tags with same name dont have different values
     * @verifies.that imported bookings are not clashing
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        // Verify that current user is a staff
        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            importResult = false;
            return;
        }

        // Verify that the file exists and load the file into the input stream
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
             contextNew = (Context) objectInputStream.readObject();
        }  catch (FileNotFoundException e) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_FILE_NOT_FOUND,
                    Map.of("file: ", filename)
            );
            importResult = false;
            return;
        } catch (IOException | ClassNotFoundException e) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_UNKNOWN_FAIL,
                    Map.of("file: ", filename));
            importResult = false;
            return;
        }

        ArrayList<String> userSkip = new ArrayList<>();  // list to keep track of user positions to skip
        ArrayList<Integer> eventSkip = new ArrayList<>(); // list to keep track of event positions to skip
        ArrayList<String> tagSkip = new ArrayList<>(); // list to keep track of event tag positions to skip

        // Get emails and users of the current and imported context
        Map<String, User> currentContextUsers = context.getUserState().getAllUsers();
        Map<String, User> importedContextUsers = contextNew.getUserState().getAllUsers();

        for (String importedUserEmail : importedContextUsers.keySet()) {
            for (String currentUserEmail : currentContextUsers.keySet()) {
                // If there is an email clash, verify if the two users are the same
                if (importedUserEmail.equals(currentUserEmail)) {
                    // Get the corresponding users
                    User currentContextUser = currentContextUsers.get(currentUserEmail);
                    User importedContextUser = importedContextUsers.get(importedUserEmail);

                    // Verify if the users are the same by comparing their serial version UIDs.
                    boolean ifUsersAreSame = importedContextUser.getSerialVersionUID() == currentContextUser.getSerialVersionUID();
                    if (ifUsersAreSame) {
                        userSkip.add(importedUserEmail);
                    } else {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_USERS,
                                Map.of("user", importedContextUsers.get(importedUserEmail) ,
                                        " - ", currentContextUsers.get(currentUserEmail) )
                        );
                        importResult = false;
                        return;
                    }
                }
            }
        }

        // Get all events from the current and imported context
        List<Event> currentContextEvents = context.getEventState().getAllEvents();
        List<Event> importedContextEvents = contextNew.getEventState().getAllEvents();


        for (int i = 0; i < importedContextEvents.size(); i++) {
            for (int j = 0; j < currentContextEvents.size(); j++) {
                // Get the corresponding event
                Event importedContextEvent = importedContextEvents.get(i);
                Event currentContextEvent = currentContextEvents.get(j);
                // Check if the events' titles, startDateTime, endDateTime are clashed
                Boolean isTitleIdentical = importedContextEvent.getTitle().equals(currentContextEvent.getTitle());
                Boolean isStartDateIdentical = importedContextEvent.getStartDateTime().equals(currentContextEvent.getStartDateTime());
                Boolean isEndDateIdentical = importedContextEvent.getEndDateTime().equals(currentContextEvent.getEndDateTime());

                // Verify that events with the same titles, startDateTime, and endDateTime do not have the same values
                if(isTitleIdentical && isStartDateIdentical && isEndDateIdentical) {
                    boolean ifEventAreSame = importedContextEvent.getSerialVersionUID() == currentContextEvent.getSerialVersionUID();

                    if (ifEventAreSame) {
                        eventSkip.add(i);
                    } else {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_EVENTS,
                                Map.of("events", importedContextEvent," - ", currentContextEvent)
                        );
                        importResult = false;
                        return;
                    }
                }
            }
        }

        // Get all EventTags from current and imported context
        Map<String, EventTag> currentContextEventTags = context.getEventState().getPossibleTags();
        Map<String, EventTag> importedContextEventTags = contextNew.getEventState().getPossibleTags();
        for (String importedEventTagName : importedContextEventTags.keySet()) {
            // Ignore the default tags
            if(importedEventTagName.equals("hasSocialDistancing") || importedEventTagName.equals("hasAirFiltration") || importedEventTagName.equals("isOutdoors") || importedEventTagName.equals("venueCapacity")){
                continue;
            }

            for (String currentContextEventTagName : currentContextEventTags.keySet()) {
                if (importedEventTagName.equals(currentContextEventTagName)) {
                    // Get the possible values and the default values of corresponding EventTag from current and imported context
                    Set<String> importedContextEventTagValues = importedContextEventTags.get(importedEventTagName).getValues();
                    Set<String> currentContextEventTagValues = currentContextEventTags.get(currentContextEventTagName).getValues();
                    String importedContextEventTagDefaultValue = importedContextEventTags.get(importedEventTagName).getDefaultValue();
                    String currentContextEventTagDefaultValue = currentContextEventTags.get(currentContextEventTagName).getDefaultValue();

                    // If the possible values or default values are not the same, abort the command
                    if (!importedContextEventTagValues.equals(currentContextEventTagValues)
                                    || !importedContextEventTagDefaultValue.equals(currentContextEventTagDefaultValue)) {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_EVENT_TAGS,
                                Map.of("tags", importedContextEventTags.get(importedEventTagName) ," - ", currentContextEventTags.get(currentContextEventTagName) )
                        );
                        importResult = false;
                        return;
                    } else {
                        tagSkip.add(importedEventTagName);
                    }
                }
            }
        }

        List<Booking> currentContextBookings = context.getBookingState().getAllBookings();
        List<Booking> importedContextBookings = contextNew.getBookingState().getAllBookings();

        for (int i = 0; i < importedContextBookings.size(); i++) {
            for (int j = 0; j < currentContextBookings.size(); j++) {
                // Get the corresponding booking
                Booking importedContextBooking = importedContextBookings.get(i);
                Booking currentContextBooking = currentContextBookings.get(j);
                Boolean isBookerNameIdentical = importedContextBooking.getBooker().getName().equals(currentContextBooking.getBooker().getName());
                Boolean isBookingEventNumberIdentical = importedContextBooking.getEvent().getEventNumber() == (currentContextBooking.getEvent().getEventNumber());
                Boolean isBookingDateTimeIdentical = importedContextBooking.getBookingDateTime().equals(currentContextBooking.getBookingDateTime());

                // If the bookings are clashed(same booker name, event number, and booking date time), abort the command
                if(isBookerNameIdentical && isBookingEventNumberIdentical && isBookingDateTimeIdentical) {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_BOOKINGS,
                                Map.of("bookings", importedContextBooking," - ", currentContextBooking)
                        );
                        importResult = false;
                        return;
                }
            }
        }

        // Add the data into the current system context
        for (String key : importedContextUsers.keySet()) {
            if(userSkip.contains(key)){
                continue;
            }
            context.getUserState().addUser(importedContextUsers.get(key));

        }

        for (int i = 0; i < importedContextEvents.size(); i++) {
            if(eventSkip.contains(i)){
                continue;
            }
            context.getEventState().addEvent(importedContextEvents.get(i));
        }

        for (String importedEventTagName : importedContextEventTags.keySet()) {
            if (tagSkip.contains(importedEventTagName)) {
                continue;
            }
            EventTag importedEventTag = importedContextEventTags.get(importedEventTagName);
            context.getEventState().createEventTag(importedEventTagName, importedEventTag.getValues() , importedEventTag.getDefaultValue());
        }

        for (int i = 0; i < importedContextBookings.size(); i++) {
            context.getBookingState().addBooking(importedContextBookings.get(i));
        }

        view.displaySuccess(
                "LoadAppStateCommand",
                LogStatus.LOAD_APP_STATE_SUCCESSFUL);

    }

    @Override
    public Boolean getResult() {
        return importResult;
    }

    private enum LogStatus {
        LOAD_APP_STATE_USER_NOT_STAFF,
        LOAD_APP_STATE_CLASHING_USERS,
        LOAD_APP_STATE_CLASHING_EVENTS,
        LOAD_APP_STATE_CLASHING_EVENT_TAGS,
        LOAD_APP_STATE_CLASHING_BOOKINGS,
        LOAD_APP_STATE_SUCCESSFUL,
        LOAD_APP_STATE_FILE_NOT_FOUND,
        LOAD_APP_STATE_UNKNOWN_FAIL,
    }
}

