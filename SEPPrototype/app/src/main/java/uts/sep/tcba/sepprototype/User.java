package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

public class User {
    public int ID;
    public String firstName;
    public String lastName;
    public LinkedList<Integer> subjects;
    public boolean isStudent;

    public User() {

    }

    public User(int ID) {
        //database to populate/initialise
    }

    public int getID() {
        return ID;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LinkedList<Integer> getSubjects() {
        return subjects;
    }

    public void cancelConsultation() {

    }
}
