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
        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            importResult = false;
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(filename);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
             contextNew = (Context) objectInputStream.readObject();
        }  catch (FileNotFoundException e) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_FILE_NOT_FOUND,
                    Map.of("file: ", filename)
            );
            return;
        } catch (IOException | ClassNotFoundException e) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_UNKNOWN_FAIL,
                    Map.of("file: ", filename));
            return;
        }

        ArrayList<String> userSkip = new ArrayList<>();  //list to keep track of user positions to skip
        ArrayList<Integer> eventSkip = new ArrayList<>(); //list to keep track of event positions to skip


        Map<String, User> contextAllUsers = context.getUserState().getAllUsers();
        Map<String, User> contextAllUsersNew = contextNew.getUserState().getAllUsers();

        for (String key : contextAllUsersNew.keySet()) {
            for (String key1 : contextAllUsers.keySet()) {
                if (contextAllUsersNew.get(key).getEmail().equals( (contextAllUsers.get(key1).getEmail()))) {
                    boolean comparison = contextAllUsersNew.get(key).getSerialVersionUID() == contextAllUsers.get(key1).getSerialVersionUID();
                    if(comparison) {
                        userSkip.add(key);
                    } else {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_USERS,
                                Map.of("user", contextAllUsersNew.get(key) ," - ", contextAllUsers.get(key1) )
                        );
                        importResult = false;
                        return;
                    }
                }
            }
        }

        List<Event> contextAllEvents = context.getEventState().getAllEvents();
        List<Event> contextAllEventsNew = contextNew.getEventState().getAllEvents();


        for (int i = 0; i < contextAllEventsNew.size(); i++) {
            for (int j = 0; j < contextAllEvents.size(); j++) {
                Boolean a = contextAllEventsNew.get(i).getTitle().equals(contextAllEvents.get(j).getTitle());
                Boolean b = contextAllEventsNew.get(i).getStartDateTime().equals(contextAllEvents.get(j).getStartDateTime());
                Boolean c = contextAllEventsNew.get(i).getEndDateTime().equals(contextAllEvents.get(j).getEndDateTime());


                if(a && b && c) {
                    boolean comparison = contextAllEventsNew.get(i).getSerialVersionUID() == contextAllEvents.get(j).getSerialVersionUID();
                    System.out.print(comparison);

                    if (comparison) {
                        eventSkip.add(i);
                    } else {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_EVENTS,
                                Map.of("events", contextAllEventsNew.get(i) ," - ", contextAllEvents.get(j) )
                        );
                        importResult = false;
                        return;
                    }

                }
            }

        }

        Map<String, EventTag> contextAllEventTag = context.getEventState().getPossibleTags();
        Map<String, EventTag> contextAllEventTagNew = contextNew.getEventState().getPossibleTags();
        for (String key : contextAllEventTagNew.keySet()) {
            if(key.equals("hasSocialDistancing") || key.equals("hasAirFiltration") || key.equals("isOutdoors") || key.equals("venueCapacity")){
                continue;
            }
            for (String key1 : contextAllEventTag.keySet()) {

                if( key.equals(key1)) {

                    if (
                            contextAllEventTagNew.get(key).getValues().equals(contextAllEventTag.get(key1).getValues())
                                    && contextAllEventTagNew.get(key).getDefaultValue().equals(contextAllEventTag.get(key1).getDefaultValue())
                    ){
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_EVENTTAGS,
                                Map.of("tags", contextAllEventTagNew.get(key) ," - ", contextAllEventTag.get(key1) )
                        );
                        importResult = false;
                        return;
                    }
                }
            }
        }

        List<Booking> contextAllBookings = context.getBookingState().getAllBookings();
        List<Booking> contextAllBookingsNew = contextNew.getBookingState().getAllBookings();



        for (int i = 0; i < contextAllBookingsNew.size(); i++) {
            for (int j = 0; j < contextAllBookings.size(); j++) {
                Boolean a = contextAllBookingsNew.get(i).getBooker().getName().equals(contextAllBookings.get(j).getBooker().getName());
                Boolean b = contextAllBookingsNew.get(i).getEvent().getEventNumber() == (contextAllBookings.get(j).getEvent().getEventNumber());
                Boolean c = contextAllBookingsNew.get(i).getBookingDateTime().equals(contextAllBookings.get(j).getBookingDateTime());
//
                if(a && b && c) {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_BOOKINGS,
                                Map.of("bookings", contextAllBookingsNew.get(i) ," - ", contextAllBookings.get(j))
                        );
                        importResult = false;
                        return;
                }

            }
        }






        if (importResult) {
            for (String key : contextAllUsersNew.keySet()) {
                if(userSkip.contains(key)){
                    continue;
                }
                context.getUserState().addUser(contextAllUsersNew.get(key));

            }


            for (int i = 0; i < contextAllEventsNew.size(); i++) {
                if(eventSkip.contains(i)){
                    continue;
                }

                context.getEventState().addEvent(contextAllEventsNew.get(i));
            }

            for (String key : contextAllEventTagNew.keySet()) {
                context.getEventState().createEventTag(key, contextAllEventTagNew.get(key).getValues() , contextAllEventTagNew.get(key).getDefaultValue() );
            }

            for (int i = 0; i < contextAllBookingsNew.size(); i++) {

                context.getBookingState().addBooking(contextAllBookingsNew.get(i));
            }

            view.displaySuccess(
                    "LoadAppStateCommand",
                    LogStatus.LOAD_APP_STATE_SUCCESSFUL);
            importResult = true;
            return;

            //reorder events and booking numbers

        }

    }

    @Override
    public Boolean getResult() {
        return importResult;
    }

    private enum LogStatus {
        LOAD_APP_STATE_USER_NOT_STAFF,
        LOAD_APP_STATE_CLASHING_USERS,
        LOAD_APP_STATE_CLASHING_EVENTS,
        LOAD_APP_STATE_CLASHING_EVENTTAGS,
        LOAD_APP_STATE_CLASHING_BOOKINGS,
        LOAD_APP_STATE_SUCCESSFUL,
        LOAD_APP_STATE_FILE_NOT_FOUND,
        LOAD_APP_STATE_UNKNOWN_FAIL,
    }
}

