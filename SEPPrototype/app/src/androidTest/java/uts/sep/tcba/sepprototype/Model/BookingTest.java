package uts.sep.tcba.sepprototype.Model;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Created by Nick Earley on 9/10/2017.
 */
public class BookingTest {

    private String bookingID = "-KvrAc8ClOEUU-E8DOS9"; // Stores booking ID, only used when fetching data from Firebase
    private int capacity = 2; // Stores the student limit of the booking
    private String date = "31/10/17"; // Stores the date of the booking
    private double startTime = 8.30; // Stores the start time of the booking
    private double endTime = 9.0; // Stores the end time of the booking
    private String location = "Death Star"; // // Stores the location of the booking
    private int tutor = 123; // Stores the ID of the tutor hosting the booking
    private String tutorName = "Test User"; // Stores the name of the tutor hosting the booking
    private int subject = 31272; // Stores the subject ID of the booking
    private LinkedList<String> students = new LinkedList<String>(); // Stores the students attending the booking
    private String availabilityID = "-KvlUIh7Gn8fyX00Q2X3"; // Stores the ID of the availability in which the booking resides
    private String description = "Booking description sample displayed here";

    Student s = new Student();
    Availability a = new Availability("31/10/17", 8.30, 9.0, "Death Star", 2);
    Tutor t = new Tutor();

    Booking testBook = new Booking(startTime, endTime, subject, t, a , s, availabilityID, description);


    // can use main to test with real students/tutors later

    public BookingTest() {
    }

    //getters , - getbookingid for now


//    @Test
//    public void getBookingID() throws Exception {
//        assertEquals(testBook.getBookingID(), bookingID);
//
//
//    }

    @Test
    public void getCapacity() throws Exception {
        assertEquals(testBook.getCapacity(), capacity);


    }

    @Test
    public void getDate() throws Exception {

        assertEquals(testBook.getDate(), date);


    }



    @Test
    public void getStartTime() throws Exception {

        String teststartime = "8:30";

        assertEquals(testBook.getStartTime(), teststartime);

    }



    @Test
    public void getEndTime() throws Exception {
        String testendtime = "9:00";
        assertEquals(testBook.getEndTime(), testendtime);

    }

    @Test
    public void getAvailabilityID() throws Exception {
        assertEquals(testBook.getAvailabilityID(), availabilityID);

    }

    @Test
    public void getLocation() throws Exception {
        assertEquals(testBook.getLocation(), location);

    }

    // Boolean

    @Test
    public void isFull() throws Exception {


    }

    // Actions


    @Test
    public void remove() throws Exception {

    }

    @Test
    public void sendNotifications() throws Exception {

    }


    // setters

    @Test
    public void setStartTime() throws Exception {
        testBook.setStartTime(3.0);
        assertEquals(testBook.getStartTime(), "3:00");



    }

    @Test
    public void setEndTime() throws Exception {
        testBook.setEndTime(3.0);
        assertEquals(testBook.getEndTime(), "3:00");

    }


}