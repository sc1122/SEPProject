package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

public class Tutor extends User {

    private LinkedList<Availabilities> availability;

    public Tutor(User user){
        //temporary implementation until availabilities and tutor details are pulled from database
        availability = new LinkedList<Availabilities>();
        availability.add(new Availabilities());
        firstName = "Joe";
        lastName = "Hunter";
    }

    public void addAvailability() {

    }

    public void editAvailability() {

    }

    public void setAvailability() {
    }

    public LinkedList<Availabilities> getAvailability(){return availability;}

    /*
    Returns list of strings of all available dates
     */
    public LinkedList<String> getAvailableDates(){
        LinkedList<String> availDates = new LinkedList<String>();
        for(Availabilities available : availability)
            availDates.add(available.getAvailDate());
        return availDates;
    }

}
