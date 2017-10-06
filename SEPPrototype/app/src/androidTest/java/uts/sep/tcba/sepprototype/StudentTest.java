package uts.sep.tcba.sepprototype;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Created by Nick Earley on 4/10/2017.
 */
public class StudentTest {

    int ID = 456;
    public LinkedList<Integer> tutors = new LinkedList<Integer>();

    Student testStudent = new Student(ID);
    // Tutor testTutor = new Tutor(ID);

    public StudentTest() {
        testStudent.tutors.add(456);
        tutors.add(456);

    }



    @Test
    public void fetchTutorsForSubject() throws Exception {
        testStudent.getTutors();


    }


    @Test
    public void makeConsultation() throws Exception {

    }

    @Test
    public void viewTutAvailability() throws Exception {

    }

    @Test
    public void getTutors() throws Exception {

        assertEquals(testStudent.getTutors(),tutors);

    }

    @Test
    public void getTutorsForIndex() throws Exception {

    }

}