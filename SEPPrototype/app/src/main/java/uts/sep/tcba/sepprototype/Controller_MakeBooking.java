package uts.sep.tcba.sepprototype;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.*;

import java.util.LinkedList;

public class Controller_MakeBooking extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private LinkedList<Availability> availabilities;
    private Availability selectedAvailability;
    private Student currentUser;
    private Tutor tutor;
    private String date;
    private double startTime;
    private double endTime;
    private int subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makebooking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = this.getIntent().getExtras();
        currentUser = (Student) bundle.getSerializable("user");
        setContent();

        setSupportActionBar(toolbar);
        Button b = (Button) findViewById(R.id.save);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                Bundle b = new Bundle();
                Booking book = getDetails();
                // iterate through all bookings for that tutor on that date at that time for that subject

                // if one matches that
                    b.putString("id",currentUser.getID()+"");
                // else
                    // new booking
                    b.putSerializable("booking", book);
                // end if

                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /*
    Method which collates the booking details and saves in the database
    Returns it as a new Booking Object
    Test here for no. of students exceeding max for booking
    TODO: Create booking use cases use this method!
     */
    public Booking getDetails(){
        Booking booking = new Booking(startTime, endTime, subject, tutor, selectedAvailability, currentUser, selectedAvailability.getID());
        return booking;
    }

    /*
    Following method generates the contents of each spinner by pulling the different availability
    Objects from firebase
    TODO: set up listeners to hide date->time until the preceding list has a selection, QOL change not needed for project
     */
    private void setContent(){
        setSubjectList();
    }

    private void setSubjectList(){
        Spinner subjectNo = (Spinner)findViewById(R.id.subject);
        subjectNo.setPrompt("Select Subject");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, currentUser.getSubjects());
        subjectNo.setAdapter(adapter);
        subjectNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("SELECTION", "MADE");
                subject = Integer.parseInt(adapterView.getAdapter().getItem(i).toString().substring(0,5));
                final int tutorID = currentUser.getTutorsForIndex(i);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("Users/" + tutorID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Clear fields if no availability for selected subject
                        Spinner consTime = (Spinner)findViewById(R.id.time);
                        consTime.setAdapter(null);
                        TextView loc = (TextView) findViewById(R.id.location);
                        loc.setText("");
                        TextView studentAttending = (TextView) findViewById(R.id.studentBooked);
                        studentAttending.setText("");
                        //-----------------------------------------------------

                        tutor = new Tutor(dataSnapshot);
                        setDateList();
                        TextView tutorName = (TextView) findViewById(R.id.tutor);
                        tutorName.setText(tutor.getFirstName() + " " + tutor.getLastName());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("RLdatabase", "Failed");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setDateList(){
        Spinner consDate = (Spinner)findViewById(R.id.date);
        consDate.setPrompt("Select Date");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tutor.getAvailableDates());
        consDate.setAdapter(adapter);
        consDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                date = adapterView.getAdapter().getItem(i).toString();
                availabilities = tutor.getAvailabilitiesForDate(date);
                setTimeList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setTimeList(){
        Spinner consTime = (Spinner)findViewById(R.id.time);
        consTime.setPrompt("Select Time");
        LinkedList<String> timeslots = new LinkedList<String>();
        for (Availability a : availabilities) {
            timeslots.addAll(a.generateTimeslots());
        }

        // Iterate through list of timeslots, remove any where there is a booking that meets the following criteria:
            // 1. Booking is for a different subject than the selected subject
            // 2. Booking is full

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, timeslots);
        consTime.setAdapter(adapter);
        consTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //CODE TO DETERMINE THE AVAILABILITY SELECTED
                String selectedtime = adapterView.getAdapter().getItem(i).toString();
                for (Availability a : availabilities) {
                    for (String s : a.generateTimeslots()) {
                        if (s.equals(selectedtime)) {
                            selectedAvailability = a;
                        }
                    }
                }
                String[] times = adapterView.getAdapter().getItem(i).toString().split(" - ");
                startTime = Double.parseDouble(times[0].replace(':','.'));
                endTime = Double.parseDouble(times[1].replace(':','.'));

                //TODO: read the number of student from the selected availability (have to rethink how you we're doing this
                TextView studentAttending = (TextView) findViewById(R.id.studentBooked);
                //LinkedList<String> numOfStudent = getDetails().getStudents();
                //studentAttending.setText("No.Students Attending/Allowed: " + numOfStudent.size() + "/" + selectedAvailability.getCapacity());

                TextView loc = (TextView) findViewById(R.id.location);
                loc.setText(selectedAvailability.getLocation());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

}
