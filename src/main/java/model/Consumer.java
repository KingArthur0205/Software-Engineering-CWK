package model;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link Consumer} represents a user of the application, who can browse {@link Event}s and book {@link Event}s.
 * The address of the consumer must be within Scotland. Consumer's preferences of type {@link EventTagCollection} is
 * initialized to empty by default.
 */
public class Consumer extends User {
    private final List<Booking> bookings;
    private String name;
    private String phoneNumber;
    private String address;
    private EventTagCollection preferences;

    /**
     * Create a new Consumer with an empty list of bookings and default Covid-19 preferences
     *
     * @param name        full name of the Consumer
     * @param email       email address of the Consumer (used to log in to the application and for event cancellation
     *                    notifications)
     * @param phoneNumber phone number of the Consumer (used for event cancellation notifications)
     * @param address     address of the Consumer (optional)
     * @param password    password used to log in to the application
     */
    public Consumer(String name, String email, String phoneNumber, String address, String password) {
        super(email, password);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.preferences = new EventTagCollection();
        this.bookings = new LinkedList<>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public EventTagCollection getPreferences() {
        return preferences;
    }

    public void setPreferences(EventTagCollection preferences) {
        this.preferences = preferences;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    /**
     * Mock method: print out a message to STDOUT. A real implementation would send an email and/or text to the
     * {@link Consumer}'s {@link #phoneNumber}.
     *
     * @param message message from an {@link Staff} regarding an event cancellation
     */
    public void notify(String message) {
        System.out.println("Message to " + getEmail() + " and " + phoneNumber + ": " + message);
    }

    public void setPhoneNumber(String newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "bookings=" + bookings +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", preferences=" + preferences +
                '}';
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Check if o is an instance of Complex or not

        if (!(o instanceof Consumer)) {
            return false;
        }

        // typecast o to Consumer so that we can compare data members
        Consumer c = (Consumer) o;

        boolean addressEquals = address == null ? c.address == null : c.address.equals(address);

        // Compare the data members and return accordingly
        return name.equals(c.name) && phoneNumber.equals(c.phoneNumber) && addressEquals && bookings.equals(c.bookings) && preferences.equals(c.preferences);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 3 * hash + (name == null ? 0 : name.hashCode());
        hash = 3 * hash + (phoneNumber == null ? 0 : phoneNumber.hashCode());
        hash = 3 * hash + (address == null ? 0 : address.hashCode());
        hash = 3 * hash + bookings.hashCode();
        hash = 3 * hash + preferences.hashCode();
        return hash;
    }
}
