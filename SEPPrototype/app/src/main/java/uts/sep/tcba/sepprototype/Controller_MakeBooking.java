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

import javax.security.auth.Subject;

public class Controller_MakeBooking extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

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
        String booking = "PLACEHOLER";
                //date.getText().toString() + " " + time.getText().toString() + " - " + tutor.getText().toString() + " (" + subject.getText().toString() + ")\n" + location.getText().toString();
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
        subjectNo.setPrompt("Select Subject");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, student.getSubjects());
        subjectNo.setAdapter(adapter);
    }

    private void setDateList(Tutor tutor){
        Spinner consDate = (Spinner)findViewById(R.id.date);
        consDate.setPrompt("Select Date");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tutor.getAvailableDates());
        consDate.setAdapter(adapter);
    }

    private void setTimeList(Availabilities availability){
        Spinner consTime = (Spinner)findViewById(R.id.time);
        consTime.setPrompt("Select Time");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, availability.getTimeslots());
        consTime.setAdapter(adapter);
    }

    /*
    Skeleton code of final method when records are pulled from database
    Will result in removal of three respective methods when completed
    TODO: In future iterations
     */
    private void setList(Student student){
        LinkedList<String> subjects = student.getSubjects();
        Spinner subjectNo = (Spinner)findViewById(R.id.subject);
        //Insert code to set field based on subject choice and selected items
    }
}
