package uts.sep.tcba.sepprototype;

import android.util.Log;

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
    private int studentLimit;
    private String location;

    public Availability(String date, double sTime, double eTime, int limit, String loc) {
        /*
        in full implementation, pull records entered into database to here
        Currently hard coded
        */
        date = "08/10/17";
        sTime = 10.00;
        eTime = 16.00;
        limit = 3;
        location = "CB11.04.401";
        /* Oi, can you not just use placeholders and actually do some work :)
           Remove the above code when values are being passed */
        availDate = date;
        startTime = sTime;
        endTime = eTime;
        studentLimit = limit;
        location = loc;
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

    public int getStudentLimit() {
        return studentLimit;
    }
    
    public String getLocation() {
        return location;
    }

}
