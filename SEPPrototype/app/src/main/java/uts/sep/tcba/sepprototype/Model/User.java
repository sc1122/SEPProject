package uts.sep.tcba.sepprototype.Model;

import android.util.Log;
import com.google.firebase.database.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

public class User implements Serializable {
    public int ID; // stores ID of user
    public String firstName; // stores first name of user
    public String lastName; // stores last name of user
    public String email; // stores email of user
    public LinkedList<Booking> bookings = new LinkedList<Booking>(); // stores user's bookings
    public LinkedList<String> subjects = new LinkedList<String>(); // stores subjects the user teaches/is enrolled in
    public LinkedList<Notification> notifications = new LinkedList<Notification>(); // stores user's notifications
    public String type; // stores type of user

    public User() { }

    public User(int ID) {  // Constructor for fetching the User from Firebase
        this.ID = ID;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + String.valueOf(ID));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("FirstName").getValue(String.class);
                lastName = dataSnapshot.child("LastName").getValue(String.class);
                email = dataSnapshot.child("Email").getValue(String.class);
                type = dataSnapshot.child("Type").getValue(String.class);
                fetchSubjects(dataSnapshot.child("Subjects"));
                fetchAndClearNotifications(dataSnapshot.child("Notifications"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseFailure", databaseError.toString());
            }
        });
    }

    public User (DataSnapshot data) { // Constructor for creating a new User to be used locally
        ID = Integer.parseInt(data.getKey());
        firstName = data.child("FirstName").getValue(String.class);
        lastName = data.child("LastName").getValue(String.class);
        email = data.child("Email").getValue(String.class);
        type = data.child("Type").getValue(String.class);
        fetchSubjects(data.child("Subjects"));
        fetchAndClearNotifications(data.child("Notifications"));
    }

    public int getID() { return this.ID; }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() { return this.email; }

    public String getType() { return this.type; }

    public String getFullName() {
        return this.firstName + " " + this.getLastName();
    }

    public LinkedList<Booking> getBookings() {
        return this.bookings;
    }

    public void sortBookings() { // chronologically sorts bookings based on start time
        Collections.sort(bookings, new Comparator<Booking>() {
            public int compare(Booking b1, Booking b2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                try {
                    Date date1 = formatter.parse(b1.getDate() + " " + b1.getStartTime()); // convert date-time string of first booking into date object for comparison
                    Date date2 = formatter.parse(b2.getDate() + " " + b2.getStartTime()); // convert date-time string of second booking into date object for comparison
                    return date1.compareTo(date2); // compare the two date objects to which precedes the other and sort chronologically
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public LinkedList<String> getSubjects() {
        return this.subjects;
    }

    public void fetchSubjects(DataSnapshot data) {
        subjects.clear();
        for (DataSnapshot d : data.getChildren()) {
            String subject = d.child("Name").getValue(String.class) + " (" + d.getKey() + ")";
            subjects.add(subject);
        }
    }

    public String getSubjectFromSubjects(int subjectID) {
        for (String subject : subjects) {
            if (subject.contains(subjectID+"")) {
                return subject;
            }
        }
        return null;
    }

    public LinkedList<Notification> getNotifications() {
        return notifications;
    }

    public void fetchAndClearNotifications(DataSnapshot data) {
        notifications.clear();
        for (DataSnapshot d : data.getChildren()) {
            notifications.add(new Notification(d));
        }
        FirebaseDatabase.getInstance().getReference("Users/" + this.getID()).child("Notifications").setValue(null); // Clear notifications from Firebase
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName + " (" + this.ID + ")";
    }
}
