package uts.sep.tcba.sepprototype.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;

public class Notification {
    private String tutor;
    private String date;
    private String times;
    private LinkedList<String> students = new LinkedList<String>();
    private String notType;

    public Notification (DataSnapshot data) {
        tutor = data.child("tutor").getValue(String.class);
        date = data.child("date").getValue(String.class);
        times = data.child("times").getValue(String.class);
    }

    public Notification (Booking booking, String notType) {
        tutor = booking.getTutorName() + " (" + booking.getTutor() + ")";
        date = booking.getDate();
        times = booking.getStartTime() + " and " + booking.getEndTime();
        students = booking.getStudents();
        this.notType = notType;
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

    public String getStudents() {
        String studentsString = "";
        boolean first = true;
        for (String s : students) {
            if (first) {
                studentsString = s;
                first = false;
            } else {
                studentsString = studentsString + ", " + s;
            }
        }
        return studentsString;
    }

    public void saveNotificationFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users");
        if (notType.equals("cancel")) {
            for (String s : students) {
                ref.child(s).child("Notifications").push().setValue(cancelledMessagetoStudent());
            }
        } else if (notType.equals("create")){
                ref.child(tutor).child("Notifications").push().setValue(CreatedMessagetoTutor());
        }
    }

    public String cancelledMessagetoStudent() {
        return "Your booking with " + getTutor() + " on " + getDate() + " between " + getTimes() + " has been cancelled.";
    }

    public String CreatedMessagetoTutor() {
        return "You have a new booking with " + getStudents() + " on " + getDate() + " between " + getTimes() + ".";
    }

}
