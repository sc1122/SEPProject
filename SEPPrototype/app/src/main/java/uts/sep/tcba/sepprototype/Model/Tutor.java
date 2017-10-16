package uts.sep.tcba.sepprototype.Model;

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

    public LinkedList<Availability> availabilities = new LinkedList<Availability>();

    public Tutor() { };

    public Tutor(int ID){ // Constructor for fetching the Tutor from Firebase
        super(ID);
    }

    public Tutor(DataSnapshot data){ // Constructor for creating a new Tutor to be used locally
        super(data);
        fetchAvailabilites(data);
    }
    public void fetchAvailabilites(DataSnapshot data) { // statically fetches tutor's availabilities from Firebase for making a booking
        for (DataSnapshot ds : data.child("Availabilities").getChildren()) {
            availabilities.add(new Availability(ds));
        }
    }

    public LinkedList<Availability> getAvailabilities(){
        return availabilities;
    }

    public void sortAvailabilities() { // chronologically sorts availabilities based on start time
        Collections.sort(availabilities, new Comparator<Availability>() {
            @Override
            public int compare(Availability a1, Availability a2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                try {
                    Date date1 = formatter.parse(a1.getDate() + " " + a1.getStartTime()); // convert date-time string of first availability into date object for comparison
                    Date date2 = formatter.parse(a2.getDate() + " " + a2.getStartTime()); // convert date-time string of second availabilty into date object for comparison
                    return date1.compareTo(date2); // compare the two date objects to which precedes the other and sort chronologically
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
        sortAvailabilities();
        for (Availability a : availabilities) {
            if (!availDates.contains(a.getDate())) {
                availDates.add(a.getDate());
            }
        }
        return availDates;
    }

    public LinkedList<Availability> getAvailabilitiesForDate(String date){
        LinkedList<Availability> avail = new LinkedList<Availability>();
        for (Availability a : availabilities) {
            if (a.getDate().equals(date)) {
                avail.add(a);
            }
        }
        Collections.sort(avail, new Comparator<Availability>() {
            public int compare(Availability a1, Availability a2) {
                double t1 = a1.getStartTimeDouble();
                Log.d("TIME1", String.valueOf(t1));
                double t2 = a2.getStartTimeDouble();
                Log.d("TIME2", String.valueOf(t2));

                if (t1 < t2) {
                    Log.d("TIME1", "SMALLER");
                    return -1;
                } else if (t1 > t2) {
                    Log.d("TIME2", "SMALLER");
                    return 1;
                }
                return 0;
            }
        });
        return avail;
    }

}
