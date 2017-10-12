package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Created by Nick Earley on 9/10/2017.
 */
public class NotificationTest {

    private static double startTime = 8.30; // Stores the start time of the booking
    private static double endTime = 9.0; // Stores the end time of the booking
    private static int subject = 31272; // Stores the subject ID of the booking
    private static String description = "I want to break free from your love you're so self satisfied i don't need you";
    private static String availabilityID = "-KvlUIh7Gn8fyX00Q2X3"; // Stores the ID of the availability in which the booking resides
    static Student s = new Student();
    static Availability a = new Availability("31/10/17", 8.30, 9.0, "Death Star", 2);
    static Tutor t = new Tutor();

    private int ID = 456;

    private static Booking testBook = new Booking(startTime, endTime, subject, t, a , s, availabilityID, description);
    private static Notification testNotification = new Notification(testBook);

    public NotificationTest() throws InterruptedException {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + ID);
        Thread.sleep(5000);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tutor testUser = new Tutor(dataSnapshot);
                set(testUser);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public void set(Tutor thing) {
        this.t = thing;


    }



    @Test
    public void getTutor() throws Exception {

        Booking testBook = new Booking(startTime, endTime, subject, t, a , s, availabilityID, description);
        Notification testNotification = new Notification(testBook);

        assertEquals("Seven Nine (456)",testNotification.getTutor());

    }

    @Test
    public void getDate() throws Exception {
        Thread.sleep(100);

        assertEquals("31/10/17",testNotification.getDate());

    }

    @Test
    public void getTimes() throws Exception {


        assertEquals("8:30 and 9:00",testNotification.getTimes());

    }

}