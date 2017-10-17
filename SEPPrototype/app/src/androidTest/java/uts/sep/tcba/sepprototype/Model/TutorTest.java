package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.LinkedList;

import uts.sep.tcba.sepprototype.Model.Availability;
import uts.sep.tcba.sepprototype.Model.Tutor;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nick Earley on 4/10/2017.
 */
public class TutorTest {


    // start of student construction

    int ID = 121314;

    private LinkedList<Availability> availabilities = new LinkedList<Availability>();


    private static Tutor testTutor = new Tutor();

    public TutorTest() throws InterruptedException {

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
        this.testTutor = thing;

    }

    // end of student construction
    // start testing



    @Test
    public void getAvailabilities() throws Exception {
        availabilities.clear();
        Availability newAvail = new Availability("20/10/17", 12.30, 15.30, "here", 2);
        availabilities.add(newAvail);
        assertEquals(availabilities.toString() , testTutor.getAvailabilities().toString());


    }

    @Test
    public void sortAvailabilities() throws Exception {
        availabilities.clear();

        Availability newAvail = new Availability("20/9/17", 12.30, 15.30, "here", 2);
        Availability newAvail2 = new Availability("20/10/17", 12.30, 15.30, "here", 2);
        testTutor.getAvailabilities().add(newAvail);
        availabilities.add(newAvail);
        availabilities.add(newAvail2);
        testTutor.sortAvailabilities();
        assertEquals(availabilities.toString() , testTutor.getAvailabilities().toString());

    }

    @Test
    public void getAvailableDates() throws Exception {
        LinkedList<String> avail = new LinkedList<String>();
        avail.add("20/10/17");
        assertEquals(avail, testTutor.getAvailableDates());

    }

    @Test
    public void getAvailabilitiesForDate() throws Exception {
        Thread.sleep(5000);
        LinkedList<Availability> avail = testTutor.getAvailabilities();
        assertEquals(avail,testTutor.getAvailabilitiesForDate("20/10/17"));

    }

}