package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

public class Student extends User {

    private User student;


    public Student(User user) {
        student = Student.this;
        /*
        Initialises with two subjects to test concept
        TODO: pull list of subjects from database
        */
        subjects.add("41950 - Security Fundamentals");
        subjects.add("31672 - Information Systems");
    }

    public void selectTimeslot() {

    }

    public void makeConsultation() {

    }

    public void viewTutAvailability() {
        
    }

}
