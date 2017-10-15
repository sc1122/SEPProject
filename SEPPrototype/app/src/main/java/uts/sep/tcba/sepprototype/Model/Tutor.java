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
    }

    public LinkedList<Availability> getAvailabilities(){
        return availabilities;
    }

    public void setAvailabilities(LinkedList<Availability> availabilities) {
        this.availabilities = availabilities;
    }

    public void sortAvailabilities() {
        Collections.sort(availabilities, new Comparator<Availability>() {
            public int compare(Availability a1, Availability a2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
                try {
                    Date date1 = formatter.parse(a1.getDate());
                    Log.d("DATE1", date1.toString());
                    Date date2 = formatter.parse(a2.getDate());
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
