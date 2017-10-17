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
    private String currentStudentName; // Temporarily stores the student creating/adding themselves to a booking
    private LinkedList<String> students = new LinkedList<String>(); // Stores the student IDs of the students attending the booking
    private LinkedList<String> studentNames = new LinkedList<String>(); // Stores the names of the students attending the booking
    private String currentStudentNotes; // Temporarily stores the notes of the student creating/adding themselves to a booking
    private LinkedList<String> studentNotes = new LinkedList<String>(); // Stores the notes of the students attending the booking
    private String availabilityID; // Stores the ID of the availability in which the booking resides

    public Booking(DataSnapshot booking) { // Constructor for fetching the Booking from Firebase
        this.bookingID = booking.getKey();
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
            this.studentNames.add(d.child("Name").getValue(String.class));
            if (d.child("Notes").exists()) {
                this.studentNotes.add(d.child("Notes").getValue(String.class));
            } else {
                this.studentNotes.add("No note to display");
            }
        }
        this.availabilityID = booking.child("availabilityID").getValue(String.class);
    }

    public Booking(Double sTime, Double eTime, int sub, Tutor t, Availability a, Student s, String availID, String notes) { // Constructor for creating a new booking to be used locally and stored to Firebase
        this.date = a.getDate();
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = a.getLocation();
        this.tutor = t.getID();
        this.tutorName = t.getFullName();
        this.subject = sub;
        this.capacity = a.getCapacity();
        this.currentStudentName = s.getFullName();
        this.currentStudentNotes = notes;
        this.students.add(String.valueOf(s.getID()));
        this.availabilityID = availID;
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

    @Exclude
    public String getCurrentStudentName() {
        return currentStudentName;
    }

    @Exclude
    public LinkedList<String> getStudents() {
        return this.students;
    }

    @Exclude
    public LinkedList<String> getStudentNames() {
        return studentNames;
    }

    @Exclude
    public String getCurrentStudentNotes() {
        return currentStudentNotes;
    }

    public LinkedList<String> getStudentNotes() {
        return this.studentNotes;
    }

    @Exclude
    public String getAllNotes() { // gets the notes for all students to be loaded into the Notes field in ViewBooking
        String allNotes = "";
        for (int i = 0; i < getStudents().size(); i++) {
            allNotes = allNotes + getStudentNames().get(i) + " (" + getStudents().get(i) + ")" + " - " + getStudentNotes().get(i); // concat students name, id and notes
            if (i < getStudents().size() - 1) { // if there are more students
                allNotes = allNotes + "\n\n"; // add a line break
            }
        }
        return allNotes;
    }

    public void add(User user, String existingBookingID, boolean newBooking) {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings"); // get reference of bookings in Firebase
        DatabaseReference newBookingRef = bookingsRef.push(); // get the database reference for new booking
        DatabaseReference targetRef;
        if (newBooking) { // if its a new booking (user not being added to an existing booking object in Firebase)
            newBookingRef.setValue(this); // push the booking object to Firebase
            targetRef = newBookingRef; // set the target ref to the location of the new booking object
        } else { // otherwise being added to an existing booking object in Firebase
            targetRef = bookingsRef.child(existingBookingID); // set the target ref to the location of the existing booking
        }
        targetRef.child("students").child(String.valueOf(user.getID())).child("Name").setValue(user.getFullName()); // adds student to booking
        targetRef.child("students").child(String.valueOf(user.getID())).child("Notes").setValue(currentStudentNotes); // adds student's notes to booking
    }

    public void remove(String userType, Booking currentBooking, String userID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Bookings/" + currentBooking.getBookingID());
        if (userType.equals("Student")) { // if its a student
            if (currentBooking.getStudents().size() > 1) { // if not the only student attending
                ref.child("students").child(userID).setValue(null); // remove that student from the booking
            } else {
                ref.setValue(null); // remove the booking
            }
        } else if (userType.equals("Tutor")) { // if its a tutor
            sendCancellationNotifications(new Notification(currentBooking)); // send cancellation notification
            ref.setValue(null); // // remove the booking
        }
    }

    public void sendCreateNotification(Notification notification) { // notify tutor on student booking a timeslot of their availability
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(String.valueOf(tutor)).child("Notifications"); // set reference to the Notifications of the tutor of the booking
        ref.push().setValue(notification); // save the notification to Firebase
    }

    public void sendCancellationNotifications(Notification notification) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users"); // set reference to the Users reference
        for (String s : getStudents()) { // for each student in the booking
            ref.child(s).child("Notifications").push().setValue(notification); // set the reference to their Notifications and save the notification to Firebase
        }
    }

    public void edit(final Booking booking, String location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Bookings").child(booking.getBookingID()).child("location");
        ref.setValue(location); // change the location
    }

    @Override
    public String toString(){
        return this.date + " " + getStartTime() + " - " + tutorName + " (" + this.subject + ")\n" + this.location;
    }


}
