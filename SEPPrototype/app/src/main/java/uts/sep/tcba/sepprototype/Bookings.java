package uts.sep.tcba.sepprototype;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
/**
 * Created by nick on 19/9/17.
 */

public class Bookings {

    private ObservableList<Booking> bookings = FXCollections.observableArrayList();

    public Bookings(String date, String time, String tutor, String subject, String location, String desc) {
        this.date = date;
        this.time = time;
        this.tutor = tutor;
        this.subject = subject;
        this.location = location;
        this.desc = desc;
    }

    public Bookings addBooking(String date, String time, String tutor, String subject, String location) {
        Bookings booking = new Bookings(date, time, tutor, subject, location);
        bookings.add(booking);
        return booking;
    }
}
