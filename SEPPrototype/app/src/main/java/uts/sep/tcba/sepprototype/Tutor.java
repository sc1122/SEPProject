package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

public class Tutor extends User {

    private LinkedList<Availability> availabilities = new LinkedList<Availability>();

    public Tutor(int ID){
        super(ID);
    }

    public Tutor(User user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.ID = user.getID();
        this.subjects = user.getSubjects();
        this.type = user.getType();
        this.email = user.getEmail();
    }

    public void addAvailability() {

    }

    public void editAvailability() {

    }

    public LinkedList<Availability> getAvailability(){return availabilities;}

    /*
    Returns list of strings of all available dates
     */
    public LinkedList<String> getAvailableDates(){
        LinkedList<String> availDates = new LinkedList<String>();
        for(Availability available : availabilities)
            availDates.add(available.getAvailDate());
        return availDates;
    }

}
