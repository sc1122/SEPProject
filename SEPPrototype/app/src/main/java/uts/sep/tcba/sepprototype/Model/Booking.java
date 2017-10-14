package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import com.google.firebase.database.*;

import java.io.Serializable;
import java.util.LinkedList;

public class Booking implements Serializable {

    private String bookingID; // Stores booking ID, only used when fetching data from Firebase
    private int capacity; // Stores the student limit of the booking
    private String date; // Stores the date of the booking
    private double startTime; // Stores the start time of the booking
    private double endTime; // Stores the end time of the booking
    private String location; // // Stores the location of the booking
    private int tutor; // Stores the ID of the tutor hosting the booking
    private String tutorName; // Stores the name of the tutor hosting the booking
    private int subject; // Stores the subject ID of the booking
    private LinkedList<String> students = new LinkedList<String>(); // Stores the students attending the booking
    private String availabilityID; // Stores the ID of the availability in which the booking resides
    private String description; //stores description made alongside booking

    public Booking(Double sTime, Double eTime, int sub, Tutor t, Availability a, Student s, String availID, String description) { // Constructor for creating a new booking to be used locally and stored to Firebase
        this.date = a.getDate();
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = a.getLocation();
        this.tutor = t.getID();
        this.tutorName = t.getFullName();
        this.subject = sub;
        this.capacity = a.getCapacity();
        this.students.add(String.valueOf(s.getID()));
        this.availabilityID = availID;
        this.description = description;

        //send notifications to studnet and tutor
        //new Notification(this, "student", "create").saveNotificationFirebase(); //but this isn't really necessary since student the one making booking
        new Notification(this, "create").saveNotificationFirebase();
    }

    public Booking(DataSnapshot booking) { // Constructor for fetching the Booking from Firebase
        this.bookingID = booking.getKey();
        Log.d("KEY IS", bookingID);
        this.date = booking.child("date").getValue(String.class);
        this.startTime = Double.valueOf(booking.child("startTime").getValue(String.class).replace(':','.'));
        this.endTime = Double.valueOf(booking.child("endTime").getValue(String.class).replace(':','.'));
        this.location = booking.child("location").getValue(String.class);
        this.tutor = booking.child("tutor").getValue(Integer.class);
        this.tutorName = booking.child("tutorName").getValue(String.class);
        this.subject = booking.child("subject").getValue(Integer.class);
        this.capacity = booking.child("capacity").getValue(Integer.class);
        for (DataSnapshot d: booking.child("students").getChildren()){
            this.students.add(d.getKey());
        }
        this.availabilityID = booking.child("availabilityID").getValue(String.class);
        this.description = booking.child("description").getValue(String.class);

    }



    @Exclude
    public String getBookingID() {
        return this.bookingID;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public String getDate() {
        return this.date;
    }

    public String getStartTime() { // returns a string of the start time to saved to Firebase
        return String.format("%.2f", this.startTime).replace('.',':');
    }

    public void setStartTime(double time) {this.startTime = time;}

    @Exclude
    public Double getDoubleStartTime() {
        return this.startTime;
    }

    public String getEndTime() { // returns a string of the end time to saved to Firebase
        return String.format("%.2f", this.endTime).replace('.',':');
    }

    public void setEndTime(double time) {this.endTime = time;}

    @Exclude
    public Double getDoubleEndTime() {
        return this.endTime;
    }

    public String getAvailabilityID() {
        return availabilityID;
    }

    public String getLocation() {
        return this.location;
    }

    public int getTutor() {
        return this.tutor;
    }

    public String getTutorName() {
        return tutorName;
    }

    public int getSubject() {
        return this.subject;
    }

    public String getDescription() {return this.description;}

    @Exclude
    public LinkedList<String> getStudents() {
        return this.students;
    }

    public void remove(String userType, Booking currentBooking, String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Bookings/" + currentBooking.getBookingID());
        if (userType.equals("Student")) {
            if (currentBooking.getStudents().size() > 1) {
                ref.child("students").child(userID).setValue(null);
            } else {
                ref.setValue(null);
            }
        } else if (userType.equals("Tutor")) {
            ref.setValue(null);
            //send cancellation notification to student
            Notification cancelNotification = new Notification(currentBooking, "cancel");
            cancelNotification.saveNotificationFirebase();
        }
    }




/*
    public void sendTutorNotification(Notification notification, String notificationType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users");
        for (String s : students) {
            if (notificationType == "cancel")
                ref.child(s).child("Notifications").push().setValue(notification.cancelledMessagetoStudent());
            else if (notificationType == "create")
                ref.child(s).child("Notifications").push().setValue(notification.CreatedMessagetoStudent());
        }
    }

    */


    @Override
    public String toString(){
        return this.date + " " + getStartTime() + " - " + tutorName + " (" + this.subject + ")\n" + this.location;
    }

    @Exclude
    public boolean isFull(){ //Returns true if the booking is full, returns false otherwise
        return (students.size() == capacity);
    }

}
