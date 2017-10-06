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
    private LinkedList<String> timeslots = new LinkedList<String>();
    private String existingBookingID = "";

    private Spinner consTime;
    private Bundle bundleSend;
    private Intent intentSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makebooking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final Bundle bundle = this.getIntent().getExtras();
        currentUser = (Student) bundle.getSerializable("user");
        setContent();

        intentSend = getIntent();
        setSupportActionBar(toolbar);
        Button b = (Button) findViewById(R.id.save);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bundleSend = new Bundle();
                final Booking book = getDetails();
                // iterate through all bookings for that tutor on that date at that time for that subject
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            // If there is a booking at the same time as selected
                            if (book.getStartTime().equals(data.child("startTime").getValue().toString()) &&
                                    book.getEndTime().equals(data.child("endTime").getValue().toString()) &&
                                    book.getSubject() == data.child("subject").getValue(Integer.class) &&
                                    book.getTutor() == data.child("tutor").getValue(Integer.class)) {
                                Log.d("MATCH", "FOUND");
                                existingBookingID = data.getKey();
                            }
                        }
                        Log.d("MATCH", existingBookingID);
                        bundleSend.putString("existingBookingID", existingBookingID);
                        bundleSend.putSerializable("booking", book);
                        intentSend.putExtras(bundleSend);
                        setResult(RESULT_OK, intentSend);
                        finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
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
                String subjectString = adapterView.getAdapter().getItem(i).toString();
                subject = Integer.parseInt(subjectString.substring(subjectString.length()-6,subjectString.length()-1));
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
        consTime = (Spinner)findViewById(R.id.time);
        consTime.setPrompt("Select Time");
        timeslots.clear();
        for (Availability a : availabilities) {
            timeslots.addAll(a.generateTimeslots());
        }

        LinkedList<String> startTimes = new LinkedList<String>();
        LinkedList<String> endTimes = new LinkedList<String>();

        for (String timeslot : timeslots) {
            String[] time = timeslot.split("\\s-\\s");
            startTimes.add(time[0]);
            endTimes.add(time[1]);
        }

        removeClashingTimeslots(startTimes, endTimes);
    }

    public void removeClashingTimeslots(final LinkedList<String> startTimes, final LinkedList<String> endTimes){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (int i = 0; i < startTimes.size(); i = i + 1) {
                        // If there is a booking at the same time as selected
                        if (startTimes.get(i).equals(data.child("startTime").getValue().toString().replace(':', '.')) &&
                                endTimes.get(i).equals(data.child("endTime").getValue().toString().replace(':', '.'))) {
                            if (data.child("students").child(String.valueOf(currentUser.getID())).exists()) {
                                Log.d("Student booked", "WOO");
                                timeslots.remove(startTimes.get(i) + " - " + endTimes.get(i));
                            } else if (subject == data.child("subject").getValue(Integer.class) // If there is a booking for the same subject as selected and not full
                                    && data.child("students").getChildrenCount() < data.child("capacity").getValue(Long.class)) {
                                Log.d("Booked but open", "WOO");
                            } else {
                                // Otherwise, remove timeslot
                                Log.d("Not free", "WOO");
                                timeslots.remove(startTimes.get(i) + " - " + endTimes.get(i));
                            }
                        }
                    }
                }
                bindToMenu();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void bindToMenu() {
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
