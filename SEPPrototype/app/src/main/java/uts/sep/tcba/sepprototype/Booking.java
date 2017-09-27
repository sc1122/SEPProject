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
    private String startTime;
    private String endTime;
    private String location;
    private int tutor;
    private String tutorName;
    private int subject;
    private LinkedList<String> students = new LinkedList<String>();

    public Booking() {}

    public Booking(DataSnapshot booking, String tutorName) {
        this.date = booking.child("date").getValue().toString();
        this.startTime = booking.child("startTime").getValue().toString();
        this.endTime = booking.child("endTime").getValue().toString();
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

    public Booking(String sTime, String eTime, int sub, Tutor t, Availability a, Student s) {
        this.date = a.getAvailDate();
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = a.getLocation();
        this.tutor = t.getID();
        this.tutorName = t.getFirstName() + " " + t.getLastName();
        this.subject = sub;
        this.capacity = a.getStudentLimit();
        this.students.add(String.valueOf(s.getID()));
    }

    public int getCapacity() {
        return this.capacity;
    }

    public String getDate() {
        return this.date;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime() {
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
        return this.date + " " + this.startTime + " - " + tutorName + " (" + this.subject + ")\n" + this.location;
    }

}
