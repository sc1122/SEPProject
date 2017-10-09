package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.LinkedList;

public class Availability implements Serializable {

    private String ID; // Stores availability ID, only used when fetching data from Firebase
    private String date; // Stores the date of the availability
    private double startTime; // Stores the start time of the availability
    private double endTime; // Stores the end time of the availability
    private int capacity; // Stores the student limit of the availability
    private String location; // Stores the location of the availability

    public Availability(DataSnapshot ds) { // Constructor for fetching the availability from Firebase
        ID = ds.getKey();
        date = ds.child("date").getValue(String.class);
        startTime = Double.parseDouble(ds.child("startTime").getValue(String.class).replace(':','.'));
        endTime = Double.parseDouble(ds.child("endTime").getValue(String.class).replace(':','.'));
        capacity = ds.child("capacity").getValue(Integer.class);
        location = ds.child("location").getValue(String.class);
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

    public void setLocation(String Location) {this.location = Location;}

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

    @Override
    public String toString(){
        return this.date + " " + getStartTime() + " - " + getEndTime() + "\n" + this.location;
    }

    public void remove(Availability currentAvailability,String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users").child(userID).child("Availabilities/" + currentAvailability.getID());
        Log.d("REF",ref.toString());
        ref.setValue(null);
    }
}
