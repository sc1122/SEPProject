package uts.sep.tcba.sepprototype;

public class Student extends User {


    private User student;


    public Student(User user) {
        student = Student.this;
    }

    public void selectTimeslot() {

    }

    public void makeConsultation() {

    }

    public void viewTutAvailability() {
        student.getSubjects();

    }

}