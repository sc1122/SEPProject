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
    private String userType;
    private String notificationType;

    public Notification (DataSnapshot data) {
        tutor = data.child("tutor").getValue(String.class);
        date = data.child("date").getValue(String.class);
        times = data.child("times").getValue(String.class);
    }

    //Alina: the intent is to have not only cancellation bookings but also creation bookings.
    //when new booking is created with a tutor, he gets a notification.
    //when an existing booking is cancelled by student, he gets a notification.
    // //(go to remove booking method and see if actually being removied or only one student leaves.
    //

    public Notification (Booking booking, String user, String notifType) {
        tutor = booking.getTutorName() + " (" + booking.getTutor() + ")";
        date = booking.getDate();
        times = booking.getStartTime() + " and " + booking.getEndTime();
        students = booking.getStudents();
        userType = user;
        notificationType = notifType;
        //saveNotificationFirebase();
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
        return students.toString();
    }



    public void saveNotificationFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users");
        if (userType.equals("student")) {
            for (String s : students) {
                if (notificationType == "cancel")
                    ref.child(s).child("Notifications").push().setValue(cancelledMessagetoStudent());
                else if (notificationType == "create")
                    ref.child(s).child("Notifications").push().setValue(CreatedMessagetoStudent());
            }
        } else if (userType.equals("tutor")){
            if (notificationType.equals("cancel"))
                ref.child(tutor).child("Notifications").push().setValue(cancelledMessagetoTutor());
            else if (notificationType.equals("create"))
                ref.child(tutor).child("Notifications").push().setValue(CreatedMessagetoTutor());
        }
    }




    //TODO: proper toString to the students linked list which has ", "s an "and" before the last name

    //TODO: pass in user type into the method and have it decide which message to return based on student/tutor

    public String cancelledMessagetoStudent() {
        return "Your booking with " + getTutor() + " on " + getDate() + " between " + getTimes() + " has been cancelled.";
    }

    public String cancelledMessagetoTutor () {
        return "Your booking with " + getStudents() + " on " + getDate() + " between " + getTimes() + " has been cancelled.";
    }

    public String CreatedMessagetoTutor() {
        return "You have a new booking with " + getStudents() + " on " + getDate() + " between " + getTimes() + ".";
    }

    public String CreatedMessagetoStudent() {
        return "You made a new booking with " + getTutor() + " on " + getDate() + " between " + getTimes() + ".";
    }
}
