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

    private int capacity;
    private String date;
    private double startTime;
    private double endTime;
    private String location;
    private int tutor;
    private String tutorName;
    private int subject;
    private LinkedList<String> students = new LinkedList<String>();

    public Booking(DataSnapshot booking, String tutorName) {
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
    }

    public Booking(Double sTime, Double eTime, int sub, Tutor t, Availability a, Student s) {
        this.date = a.getDate();
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = a.getLocation();
        this.tutor = t.getID();
        this.tutorName = t.getFirstName() + " " + t.getLastName();
        this.subject = sub;
        this.capacity = a.getCapacity();
        this.students.add(String.valueOf(s.getID()));
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

    public String setStartTime(double time) {this.startTime = time;}

    public Double DoubleStartTime() {
        return this.startTime;

    }

    public String getEndTime() {
        return String.format("%.2f", this.endTime).replace('.',':');
    }

    public Double DoubleEndTime() {
        return this.endTime;
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
    public boolean isFull(){
        return (students.size() == capacity);
    }

}
