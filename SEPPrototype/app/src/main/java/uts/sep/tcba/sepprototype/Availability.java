package uts.sep.tcba.sepprototype;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * Created by Sean Crimmins on 15/09/2017.
 * Class which handles the availabilities of the seperate tutors
 *
 */

public class Availability implements Serializable {

    private String ID;
    private String date;
    private double startTime;
    private double endTime;
    private int capacity;
    private String location;

    public Availability() { }

    public Availability(DataSnapshot ds) {
        ID = ds.getKey().toString();
        date = ds.child("date").getValue().toString();
        startTime = Double.parseDouble(ds.child("startTime").getValue().toString().replace(':','.'));
        endTime = Double.parseDouble(ds.child("endTime").getValue().toString().replace(':','.'));
        capacity = ds.child("capacity").getValue(Integer.class);
        location = ds.child("location").getValue().toString();
    }

    public Availability(String date, Double sTime, Double eTime, String loc, int cap) {
        this.date = date;
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = loc;
        this.capacity = cap;
    }

    /*
    divides the total available time, up into the 30min timeslots for consulatation
     */
    public LinkedList<String> generateTimeslots(){
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

    private String timeDivide(double time) {
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

    public String getStartTime() {
        return String.format("%.2f", startTime).replace('.',':');
    }

    public String getEndTime() {
        return String.format("%.2f", endTime).replace('.',':');
    }
}
