package uts.sep.tcba.sepprototype;

import android.util.Log;
import com.google.firebase.database.*;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    public void sortAvailabilities() {
        Collections.sort(availabilities, new Comparator<Availability>() {
            public int compare(Availability a1, Availability a2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
                try {
                    Date date1 = formatter.parse(a1.getAvailDate());
                    Log.d("DATE1", date1.toString());
                    Date date2 = formatter.parse(a2.getAvailDate());
                    Log.d("DATE2", date2.toString());
                    Log.d("COMPARE", String.valueOf(date1.compareTo(date2)));
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    /*
    Returns list of strings of all available dates
     */
    public LinkedList<String> getAvailableDates(){
        LinkedList<String> availDates = new LinkedList<String>();
        LinkedList<String> alreadyAdded = new LinkedList<String>();
        sortAvailabilities();
        for (Availability a : availabilities) {
            if (!availDates.contains(a.getAvailDate())) {
                availDates.add(a.getAvailDate());
            }
        }
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
