package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

public class Student extends User {

    private User student;
    private LinkedList<Integer> studentSubjects;


    public Student(User user) {
        student = Student.this;
        studentSubjects = Student.this.getSubjects();
    }

    public void selectTimeslot() {

    }

    public void makeConsultation() {

    }

    public void viewTutAvailability() {
        
    }

}
