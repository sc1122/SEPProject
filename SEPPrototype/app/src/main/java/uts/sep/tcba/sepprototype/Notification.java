package uts.sep.tcba.sepprototype;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;

public class Notification {
    private String tutor;
    private String date;
    private String times;

    public Notification (DataSnapshot data) {
        tutor = data.child("tutor").getValue().toString();
        date = data.child("date").getValue().toString();
        times = data.child("times").getValue().toString();
    }

    public Notification (Booking booking) {
        tutor = booking.getTutorName() + " (" + booking.getTutor() + ")";
        date = booking.getDate();
        times = booking.getStartTime() + " and " + booking.getEndTime();
    }

    public String getTutor() {
        return tutor;
    }

    public String getDate() {
        return date;
    }

    public String getTimes() {
        return times;
    }

    @Override
    public String toString() {
        return "Your booking with " + getTutor() + " on " + getDate() + " between " + getTimes() + " has been cancelled.";
    }
}
