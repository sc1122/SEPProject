package uts.sep.tcba.sepprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.LinkedList;

public class Controller_MakeBooking extends AppCompatActivity {

    private Spinner subject;
    private Spinner date;
    private Spinner time;
    private Spinner subject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makebooking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContent();
        Button b = (Button) findViewById(R.id.save);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                String bookingDetails = getDetails();
                intent.putExtra("result", bookingDetails);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /*
    Method which collates the booking details and saves in the database
    TODO: Create booking use cases use this method!
     */
    public String getDetails(){

        Spinner consDate = (Spinner)findViewById(R.id.date);
        String date = consDate.getSelectedItem().toString();

        Spinner consTime = (Spinner)findViewById(R.id.time);
        String time = consTime.getSelectedItem().toString();

        Spinner subjectNo = (Spinner)findViewById(R.id.subject);
        String subject = subjectNo.getSelectedItem().toString();

        TextView tutorName = (TextView) findViewById(R.id.tutor);
        String tutor = tutorName.getSelectedItem().toString();

        TextView location = (TextView) findViewById(R.id.location);
        String consLocation = location.toString();

        //Bookings.addBooking(date, time, tutor, subject, consLocation);




        String booking = date + " " + time + " - " + tutor + " (" + subject + ")" + consLocation;
        return booking;
    }

    /*
    following 4 methods generates the content of each of the items in some way
    TODO: make this more dynamic, changing the time/date based off the subject
    TODO: set up listeners to hide date->time until the preceding list has a selection
     */
    private void setContent(){
        User tutorUser = new User();
        Tutor tutor = new Tutor(tutorUser);
        User stuUser = new User();
        Student student = new Student(stuUser);
        Availabilities content = new Availabilities();
        //Above code to be pulled from database on future iterations
        setSubjectList(student);
        setDateList(tutor);
        setTimeList(content);
        TextView tutorName = (TextView) findViewById(R.id.tutor);
        tutorName.setText(tutor.getFirstName() + " " + tutor.getLastName());
        TextView location = (TextView) findViewById(R.id.location);
        location.setText(content.getLocation());
    }

    private void setSubjectList(Student student){
        Spinner subjectNo = (Spinner)findViewById(R.id.subject);
        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, student.getSubjects());
        subjectNo.setAdapter(subjectsAdapter);
    }

    private void setDateList(Tutor tutor){
        Spinner consDate = (Spinner)findViewById(R.id.date);
        ArrayAdapter<String> datesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tutor.getAvailableDates());
        consDate.setAdapter(datesAdapter);
    }

    private void setTimeList(Availabilities availability){
        Spinner consTime = (Spinner)findViewById(R.id.time);
        ArrayAdapter<String> timesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, availability.getTimeslots());
        consTime.setAdapter(timesAdapter);
    }

}
