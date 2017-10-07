package uts.sep.tcba.sepprototype;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Availability implements Serializable {

    private String ID; // Stores availability ID, only used when fetching data from Firebase
    private String date; // Stores the date of the availability
    private double startTime; // Stores the start time of the availability
    private double endTime; // Stores the end time of the availability
    private int capacity; // Stores the student limit of the availability
    private String location; // Stores the location of the availability

    public Availability(DataSnapshot ds) { // Constructor for fetching the availability from Firebase
        ID = ds.getKey().toString();
        date = ds.child("date").getValue().toString();
        startTime = Double.parseDouble(ds.child("startTime").getValue().toString().replace(':','.'));
        endTime = Double.parseDouble(ds.child("endTime").getValue().toString().replace(':','.'));
        capacity = ds.child("capacity").getValue(Integer.class);
        location = ds.child("location").getValue().toString();
    }

    public Availability(String date, Double sTime, Double eTime, String loc, int cap) { // Constructor for creating a new availability to be used locally and stored to Firebase
        this.date = date;
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = loc;
        this.capacity = cap;
    }

    public LinkedList<String> generateTimeslots(){ // Divides the total availability time up into the 30 minute timeslots for bookings
        LinkedList<String> timeSlots = new LinkedList<String>();
        double t = startTime;
        while (t < endTime) {
            if(String.format("%.2f", t).endsWith(".30")) {
                timeSlots.add(timeDivide(t));
                t += 0.7;
            }else{
                timeSlots.add(timeDivide(t));
                t += 0.3;
            }
        }
        return timeSlots;
    }

    private String timeDivide(double time) { //TODO: Add comments to explain what this does
        if (String.format("%.2f", time).endsWith(".30"))
            return (String.format("%.2f", time) + " - " + String.format("%.2f", (time+0.7)));
        else
            return (String.format("%.2f", time) + " - " + String.format("%.2f",(time+0.3)));
    }

    @Exclude
    public String getID() {
        return ID;
    }

    public String getDate() {
        return date;
    }

    public int getCapacity() {
        return capacity;
    }
    
    public String getLocation() {
        return location;
    }

    @Exclude
    public double getStartTimeDouble() {
        return startTime;
    }

    @Exclude
    public double getEndTimeDouble() {
        return endTime;
    }

    public String getStartTime() { // returns a string of the start time to saved to Firebase
        return String.format("%.2f", startTime).replace('.',':');
    }

    public String getEndTime() { // returns a string of the end time to saved to Firebase
        return String.format("%.2f", endTime).replace('.',':');
    }
}
