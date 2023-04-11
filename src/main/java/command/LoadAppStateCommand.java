package command;

import controller.Context;
import model.*;
import view.IView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.ArrayList;
import java.io.FileNotFoundException;

/**
 * {@link LoadAppStateCommand} allows {@link Staff} members to save the state of system.
 */
public class LoadAppStateCommand implements ICommand<Boolean> {
    private Boolean importResult;
    private String filename;
    Context context1 = null;

    /**
     * @param filename           the location of file that going to load from
     */
    public LoadAppStateCommand(String filename) {
        this.filename = filename;
        importResult = true;
    }

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
             context1 = (Context) objectInputStream.readObject();
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

        ArrayList<String> userSkip = new ArrayList<>();
        ArrayList<Integer> eventSkip = new ArrayList<>();
        ArrayList<Integer> bookingSkip = new ArrayList<>();


        for (String key : context1.getUserState().getAllUsers().keySet()) {
            for (String key1 : context.getUserState().getAllUsers().keySet()) {
                if (context1.getUserState().getAllUsers().get(key).getEmail().equals( (context.getUserState().getAllUsers().get(key1).getEmail()))) {
                    boolean comparison = context1.getUserState().getAllUsers().get(key).getSerialVersionUID() == context.getUserState().getAllUsers().get(key1).getSerialVersionUID();
                    if(comparison) {
                        userSkip.add(key);
                    } else {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_USERS,
                                Map.of("user", context1.getUserState().getAllUsers().get(key) ," - ", context.getUserState().getAllUsers().get(key1) )
                        );
                        importResult = false;
                        return;
                    }
                }
            }
        }


        for (int i = 0; i < context1.getEventState().getAllEvents().size(); i++) {
            for (int j = 0; j < context.getEventState().getAllEvents().size(); j++) {
                Boolean a = context1.getEventState().getAllEvents().get(i).getTitle().equals(context.getEventState().getAllEvents().get(j).getTitle());
                Boolean b = context1.getEventState().getAllEvents().get(i).getStartDateTime().equals(context.getEventState().getAllEvents().get(j).getStartDateTime());
                Boolean c = context1.getEventState().getAllEvents().get(i).getEndDateTime().equals(context.getEventState().getAllEvents().get(j).getEndDateTime());


                if(a && b && c) {
                    boolean comparison = context1.getEventState().getAllEvents().get(i).getSerialVersionUID() == context.getEventState().getAllEvents().get(j).getSerialVersionUID();
                    System.out.print(comparison);

                    if (comparison) {
                        eventSkip.add(i);
                    } else {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_EVENTS,
                                Map.of("events", context1.getEventState().getAllEvents().get(i) ," - ", context.getEventState().getAllEvents().get(j) )
                        );
                        importResult = false;
                        return;
                    }

                }
            }

        }

        for (String key : context1.getEventState().getPossibleTags().keySet()) {
            if(key.equals("hasSocialDistancing") || key.equals("hasAirFiltration") || key.equals("isOutdoors") || key.equals("venueCapacity")){
                continue;
            }
            for (String key1 : context.getEventState().getPossibleTags().keySet()) {

                if( key.equals(key1)) {

                    if (
                            context1.getEventState().getPossibleTags().get(key).getValues().equals(context.getEventState().getPossibleTags().get(key1).getValues())
                                    && context1.getEventState().getPossibleTags().get(key).getDefaultValue().equals(context.getEventState().getPossibleTags().get(key1).getDefaultValue())
                    ){
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_EVENTTAGS,
                                Map.of("tags", context1.getEventState().getPossibleTags().get(key) ," - ", context.getEventState().getPossibleTags().get(key1) )
                        );
                        importResult = false;
                        return;
                    }
                }
            }
        }

        for (int i = 0; i < context1.getBookingState().getAllBookings().size(); i++) {
            for (int j = 0; j < context.getBookingState().getAllBookings().size(); j++) {
                Boolean a = context1.getBookingState().getAllBookings().get(i).getBooker().getName().equals(context.getBookingState().getAllBookings().get(j).getBooker().getName());
                Boolean b =context1.getBookingState().getAllBookings().get(i).getEvent().getEventNumber() == (context.getBookingState().getAllBookings().get(j).getEvent().getEventNumber());
                Boolean c = context1.getBookingState().getAllBookings().get(i).getBookingDateTime().equals(context.getBookingState().getAllBookings().get(j).getBookingDateTime());
//
                if(a && b && c) {
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_BOOKINGS,
                                Map.of("bookings", context1.getBookingState().getAllBookings().get(i) ," - ", context.getBookingState().getAllBookings().get(j))
                        );
                        importResult = false;
                        return;
                }

            }
        }






        if (importResult) {
            for (String key : context1.getUserState().getAllUsers().keySet()) {
                if(userSkip.contains(key)){
                    continue;
                }
                context.getUserState().addUser(context1.getUserState().getAllUsers().get(key));

            }


            for (int i = 0; i < context1.getEventState().getAllEvents().size(); i++) {
                if(eventSkip.contains(i)){
                    continue;
                }

                context.getEventState().addEvent(context1.getEventState().getAllEvents().get(i));
            }

            for (String key : context1.getEventState().getPossibleTags().keySet()) {
                context.getEventState().createEventTag(key, context1.getEventState().getPossibleTags().get(key).getValues() , context1.getEventState().getPossibleTags().get(key).getDefaultValue() );
            }

            for (int i = 0; i < context1.getBookingState().getAllBookings().size(); i++) {
                if(bookingSkip.contains(i)){
                    continue;
                }
                context.getBookingState().addBooking(context1.getBookingState().getAllBookings().get(i)); // incorrect wrong time
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

