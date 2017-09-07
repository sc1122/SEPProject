package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

public class User {
    public int ID;
    public String firstName;
    public String lastName;
    public LinkedList<Integer> subjects;
    public boolean Student;

    public User() {

    }

    public User(int ID) {
        //database to populate/initialise
    }

    public int getID() {
        return ID;
    }

    public boolean getStudent() {
        return Student;
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
