package uts.sep.tcba.sepprototype.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.*;

import java.util.LinkedList;

import uts.sep.tcba.sepprototype.Model.Availability;
import uts.sep.tcba.sepprototype.Model.Booking;
import uts.sep.tcba.sepprototype.Model.Notification;
import uts.sep.tcba.sepprototype.Model.Student;
import uts.sep.tcba.sepprototype.Model.Tutor;

public class Controller_MakeBooking extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> dateAdapter;
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
    private String desc;
    private EditText description;

    private Spinner consDate;
    private Spinner consTime;
    private Bundle bundleSend;
    private Intent intentSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makebooking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        consDate = (Spinner)findViewById(R.id.date);
        consTime = (Spinner)findViewById(R.id.time);

        // Get data from menu
        Bundle bundle = this.getIntent().getExtras();
        currentUser = (Student) bundle.getSerializable("user");
        setContent();

        // Initialise the Save button action on tool bar
        Button b = (Button) findViewById(R.id.save);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intentSend = getIntent();
                bundleSend = new Bundle();
                final Booking book = getDetails(); // create a local booking object for comparison with the booking in Firebase
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings"); // set a reference for all bookings
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) { // iterate through all bookings
                            // If there is a booking at the same time as selected that is for the same subject and is a child of the same tutor's availability
                            if (book.getStartTime().equals(data.child("startTime").getValue(String.class)) && book.getEndTime().equals(data.child("endTime").getValue(String.class)) &&
                                    book.getSubject() == data.child("subject").getValue(Integer.class) && book.getAvailabilityID().equals(data.child("availabilityID").getValue(String.class))) {
                                Log.i("MATCH FOUND", data.getKey()); // informational debug statement written to log to indicate a match has been found
                                existingBookingID = data.getKey(); // store the bookingID for that match
                            }
                        }
                        bundleSend.putString("existingBookingID", existingBookingID); // Pass existingBookingID
                        bundleSend.putSerializable("booking", book); // Pass booking
                        intentSend.putExtras(bundleSend); // add data to intent
                        setResult(RESULT_OK, intentSend); // add intent to finish
                        finish(); // transition back back to StudentMenu
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { Log.e("FirebaseFailure", databaseError.toString()); }
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
        Booking booking = new Booking(startTime, endTime, subject, tutor, selectedAvailability, currentUser, selectedAvailability.getID(), desc); // create a new booking object locally to be pushed to Firebase
        booking.sendCreateNotification(new Notification(booking)); // send notification to tutor notifying them of the student booking them
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
                        Log.e("FirebaseFailure", databaseError.toString());
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setDateList(){
        consDate.setPrompt("Select Date");
        dateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tutor.getAvailableDates());
        consDate.setAdapter(dateAdapter);
        consDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                date = adapterView.getAdapter().getItem(i).toString();
                availabilities = tutor.getAvailabilitiesForDate(date);
                setTimeList();
                setDesc();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setTimeList(){
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

        if (timeslots.size() == 0) {
            dateAdapter.remove(consDate.getSelectedItem().toString());
        }
    }

    public void removeClashingTimeslots(final LinkedList<String> startTimes, final LinkedList<String> endTimes){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings"); // set a reference for all bookings
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) { // iterate through all bookings
                    for (int i = 0; i < startTimes.size(); i = i + 1) { // for each timeslot in the availability
                        if (startTimes.get(i).equals(data.child("startTime").getValue(String.class).replace(':', '.')) &&
                                endTimes.get(i).equals(data.child("endTime").getValue(String.class).replace(':', '.'))) { // If there is a booking at the same time as selected
                            if (data.child("students").child(String.valueOf(currentUser.getID())).exists()) { // If the student is already booked
                                Log.i("Student already booked", "Removing timeslot"); // informational debug statement written to log to indicate student is booked for this time
                                timeslots.remove(startTimes.get(i) + " - " + endTimes.get(i)); // remove that timeslot
                            } else if (subject == data.child("subject").getValue(Integer.class)
                                    && data.child("students").getChildrenCount() < data.child("capacity").getValue(Long.class)) { // If there is a booking for the same subject as selected and not booking not full
                                Log.i("Timeslot already booked", "Keeping timeslot, same subject and not full"); // informational debug statement written to log to indicate this is a bookable timeslot
                            } else { // Otherwise, timeslot is booked for different subject or other, remove timeslot
                                Log.d("Timeslot already booked", "Removing timeslot"); // informational debug statement written to log to indicate this is not a bookable timeslot
                                timeslots.remove(startTimes.get(i) + " - " + endTimes.get(i)); // remove that timeslot
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
                studentAttending.setText("Maximum No. of Students: " + selectedAvailability.getCapacity());

                TextView loc = (TextView) findViewById(R.id.location);
                loc.setText(selectedAvailability.getLocation());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setDesc(){
        this.description = (EditText) findViewById(R.id.description);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                desc = description.getText().toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
