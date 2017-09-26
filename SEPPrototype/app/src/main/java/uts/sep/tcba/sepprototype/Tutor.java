package uts.sep.tcba.sepprototype;

import android.util.Log;
import com.google.firebase.database.*;

import java.io.Serializable;
import java.util.LinkedList;

public class Tutor extends User implements Serializable {

    private LinkedList<Availability> availabilities = new LinkedList<Availability>();

    public Tutor(int ID){
        super(ID);
        //fetchAvailabilities();
    }

    public Tutor(DataSnapshot data){
        super(data);
        fetchAvailabilities(data);
    }

    public Tutor(User user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.ID = user.getID();
        this.subjects = user.getSubjects();
        this.type = user.getType();
        this.email = user.getEmail();
        //fetchAvailabilities();
    }

    public void fetchAvailabilities(DataSnapshot data) {
        for (DataSnapshot ds : data.child("Availabilities").getChildren()) {
            availabilities.add(new Availability(ds));
        }
        /* FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + this.getID() + "/Availabilities");
        Log.d("TUTOR YAY", ref.toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    availabilities.add(new Availability(ds));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });*/
    }

    public void addAvailability() {

    }

    public void editAvailability() {

    }

    public LinkedList<Availability> getAvailabilities(){
        return availabilities;
    }

    /*
    Returns list of strings of all available dates
     */
    public LinkedList<String> getAvailableDates(){
        LinkedList<String> availDates = new LinkedList<String>();
        for(Availability available : availabilities)
            availDates.add(available.getAvailDate());
        return availDates;
    }

    public LinkedList<Availability> getAvailabilitiesForDate(String date){
        LinkedList<Availability> avail = new LinkedList<Availability>();
        for (Availability a : availabilities) {
            if (a.getAvailDate().equals(date)) {
                avail.add(a);
            }
        }
        return avail;
    }

}
