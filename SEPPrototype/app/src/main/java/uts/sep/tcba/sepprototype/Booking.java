package uts.sep.tcba.sepprototype;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.*;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by CalebAdra on 20/9/17.
 */

public class Booking {

    public int capacity;
    public String date;
    public String startTime;
    public String endTime;
    public String location;
    public int tutorID;
    public String tutorName;
    public int subject;
    public LinkedList<String> students = new LinkedList<String>();

    public Booking(DataSnapshot booking, String tutorName) {
        capacity = booking.child("Capacity").getValue(Integer.class);
        date = booking.child("Date").getValue().toString();
        startTime = booking.child("StartTime").getValue().toString();
        endTime = booking.child("EndTime").getValue().toString();
        location = booking.child("Location").getValue().toString();
        tutorID = booking.child("Tutor").getValue(Integer.class);
        this.tutorName = tutorName;
        subject = booking.child("Subject").getValue(Integer.class);
        for (DataSnapshot d: booking.child("Students").getChildren()){
            if (d.child("BookingStatus").getValue().toString().equals("Attending")) {
                students.add(d.getKey());
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public int getTutorID() {
        return tutorID;
    }

    public String getTutorName() {
        return tutorName;
    }

    public int getSubject() {
        return subject;
    }

    public LinkedList<String> getStudents() {
        return students;
    }

    @Override
    public String toString(){
        return date + " " + startTime + " - " + tutorName + " (" + subject + ")\n" + location;
    }

}
