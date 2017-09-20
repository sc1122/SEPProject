package uts.sep.tcba.sepprototype;

import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * Created by seant on 15/09/2017.
 * Class which handles the availabilities of the tutor
 * Done in a linked list of all availabilities
 */

public class Availabilities {

    private String availDate;
    private double startTime;
    private double endTime;
    private int studentLimit;
    private String location;

    public Availabilities(){
        /*
        in full implementation, pull records entered into database to here
        Currently hard coded
        */
        availDate = "08/10/17";
        startTime = 10.00;
        endTime = 16.00;
        studentLimit = 3;
        location = "CB11.04.401";
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
        if (String.valueOf(time).endsWith(".30"))
            return (String.format("%.2f", time) + " - " + String.format("%.2f", (time+0.7)));
        else
            return (String.format("%.2f", time) + " - " + String.format("%.2f",(time+0.3)));

    }

    public String getAvailDate(){return availDate;}

    public int getStudentLimit(){return studentLimit;}

    public String getLocation(){return location;}



}
