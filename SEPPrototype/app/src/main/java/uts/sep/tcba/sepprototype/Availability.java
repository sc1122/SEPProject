package uts.sep.tcba.sepprototype;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * Created by seant on 15/09/2017.
 * Class which handles the availabilities of the tutor
 * Done in a linked list of all availabilities
 * NOTE: THIS WILL NOT BE IMPLEMENTED LIKE THIS, NO NEED FOR LINKEDLIST
 *       THIS IS A OBJECT TO EMULATE AN AVAILABILITY INTERNALLY
 *       LINKEDLIST IN THIS CASE IS THE DATASNAPSHOT FROM FIREBASE, NOT LOCAL LINKEDLIST
 */

public class Availability {

    private String availDate;
    private double startTime;
    private double endTime;
    private int capacity;
    private String location;

    public Availability() { }

    public Availability(DataSnapshot ds) {
        availDate = ds.child("Date").getValue().toString();
        startTime = Double.parseDouble(ds.child("StartTime").getValue().toString().replace(':','.'));
        endTime = Double.parseDouble(ds.child("EndTime").getValue().toString().replace(':','.'));
        capacity = ds.child("Capacity").getValue(Integer.class);
        location = ds.child("Location").getValue().toString();
    }

    public Availability(String date, Double sTime, Double eTime, String loc, int cap) {
        this.availDate = date;
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = loc;
        this.capacity = cap;
    }

    /*
    divides the total available time, up into the 30min timeslots for consulatation
     */
    public LinkedList<String> getTimeslots(){
        LinkedList<String> timeSlots = new LinkedList<String>();
        double t = startTime;
        while (t <= endTime) {
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

    public String getAvailDate() {
        return availDate;
    }

    public int getCapacity() {
        return capacity;
    }
    
    public String getLocation() {
        return location;
    }

    public String getStartTime() {
        return String.format("%.2f", startTime).replace('.',':');
    }

    public String getEndTime() {
        return String.format("%.2f", endTime).replace('.',':');
    }
}
