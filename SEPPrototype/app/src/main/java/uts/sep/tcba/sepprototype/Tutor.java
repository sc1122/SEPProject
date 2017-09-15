package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

public class Tutor extends User {

    private LinkedList<Availabilities> availability;

    public Tutor(User user) {
        availability.add(new Availabilities(Tutor.this));
    }

    public void addAvailability() {

    }

    public void editAvailability() {

    }

    public void setStudentAvailability() {

    }

    public LinkedList<Availabilities> getAvailability(){return availability;}

    public LinkedList<String> getAvailableDates(){
        LinkedList<String> availDates = new LinkedList<String>();
        int i = 0;
        for(Availabilities available : availability){
            availDates.set(i, available.getAvailDate());
        }
        return availDates;
    }

}
