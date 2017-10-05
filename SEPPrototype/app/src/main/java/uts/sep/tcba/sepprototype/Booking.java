package uts.sep.tcba.sepprototype;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.*;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by CalebAdra on 20/9/17.
 */

public class Booking implements Serializable {

    private String bookingID;
    private int capacity;
    private String date;
    private double startTime;
    private double endTime;
    private String location;
    private int tutor;
    private String tutorName;
    private int subject;
    private LinkedList<String> students = new LinkedList<String>();
    private String availabilityID;

    public Booking(DataSnapshot booking, String tutorName) {
        this.bookingID = booking.getKey().toString();
        Log.d("KEYS", bookingID);
        this.date = booking.child("date").getValue().toString();
        this.startTime = Double.valueOf(booking.child("startTime").getValue().toString().replace(':','.'));
        this.endTime = Double.valueOf(booking.child("endTime").getValue().toString().replace(':','.'));
        this.location = booking.child("location").getValue().toString();
        this.tutor = booking.child("tutor").getValue(Integer.class);
        this.tutorName = tutorName;
        this.subject = booking.child("subject").getValue(Integer.class);
        this.capacity = booking.child("capacity").getValue(Integer.class);
        for (DataSnapshot d: booking.child("students").getChildren()){
            if (d.child("BookingStatus").getValue().toString().equals("Attending")) {
                this.students.add(d.getKey());
            }
        }
        this.availabilityID = booking.child("availabilityID").getValue().toString();
    }

    public Booking(Double sTime, Double eTime, int sub, Tutor t, Availability a, Student s, String availID) {
        this.date = a.getDate();
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = a.getLocation();
        this.tutor = t.getID();
        this.tutorName = t.getFirstName() + " " + t.getLastName();
        this.subject = sub;
        this.capacity = a.getCapacity();
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

    public String getStartTime() {
        return String.format("%.2f", this.startTime).replace('.',':');
    }

    public void setStartTime(double time) {this.startTime = time;}

    public void setEndTime(double time) {this.endTime = time;}

    @Exclude
    public Double getDoubleStartTime() {
        return this.startTime;
    }

    public String getEndTime() {
        return String.format("%.2f", this.endTime).replace('.',':');
    }

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
    public LinkedList<String> getStudents() {
        return this.students;
    }

    @Override
    public String toString(){
        return this.date + " " + getStartTime() + " - " + tutorName + " (" + this.subject + ")\n" + this.location;
    }

    /*
    Returns true if the booking is full, returns false otherwise
     */
    @Exclude
    public boolean isFull(){
        return (students.size() == capacity);
    }

}
