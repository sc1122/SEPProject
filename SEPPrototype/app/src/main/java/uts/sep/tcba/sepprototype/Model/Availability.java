package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void remove(final Availability currentAvailability, String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users").child(userID).child("Availabilities/" + currentAvailability.getID());
        //Remove the availability
        ref.setValue(null);

        //Remove associated booking(s)
        final DatabaseReference bookingRef = database.getReference().child("Bookings");
        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Find bookings matching availability by comparing ID
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if(data.child("availabilityID").getValue().equals(currentAvailability.getID())) {
                        //get affected booking
                        Booking bookingAffected = new Booking(data);

                        for (String student :bookingAffected.getStudents()) {
                            bookingAffected.remove("tutor", bookingAffected, student);
                            //Send notification to each student - Alina: will be handled in remove method
                            //sendNotification(new Notification(bookingAffected), student);
                        }

                        //Remove booking - Alina: will be handled in remvoe method
                       // bookingRef.child(data.getKey()).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void edit(final Availability currentAvailability, String userID, final String location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users").child(userID).child("Availabilities/" + currentAvailability.getID()).child("location");
        //Change the location
        ref.setValue(location);

        //Also change location for associated booking(s)
        final DatabaseReference bookingRef = database.getReference().child("Bookings");
        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Find booking matching availability by comparing ID
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if(data.child("availabilityID").getValue().equals(currentAvailability.getID())){
                        //Edit all locations for the bookings
                        bookingRef.child(data.getKey()).child("location").setValue(location);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendNotification(Notification notification, String student) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users");
        ref.child(student).child("Notifications").push().setValue(notification);
    }
}
