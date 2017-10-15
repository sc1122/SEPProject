package uts.sep.tcba.sepprototype.Model;

import com.google.firebase.database.DataSnapshot;

public class Notification {
    private String tutor;
    private String student;
    private String date;
    private String times;

    public Notification (DataSnapshot data) {
        tutor = data.child("tutor").getValue(String.class);
        student = data.child("student").getValue(String.class);
        date = data.child("date").getValue(String.class);
        times = data.child("times").getValue(String.class);
    }

    public Notification (Booking booking) {
        tutor = booking.getTutorName() + " (" + booking.getTutor() + ")";
        student = booking.getCurrentStudentName() + " (" + booking.getStudents().getFirst() + ")";
        date = booking.getDate();
        times = booking.getStartTime() + " and " + booking.getEndTime();
    }

    public String getTutor() {
        return tutor;
    }

    public String getStudent() {
        return student;
    }

    public String getDate() {
        return date;
    }

    public String getTimes() {
        return times;
    }

    public String cancelledMessageToStudent() {
        return "Your booking with " + getTutor() + " on " + getDate() + " between " + getTimes() + " has been cancelled.";
    }

    public String createdMessageToTutor() {
        return "You have a booking with " + student + " on " + getDate() + " between " + getTimes() + ".";
    }
}
