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

import javax.security.auth.Subject;

public class Controller_MakeBooking extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private Availability content = new Availability("date", 12.00, 14.00, 4, "location");
    public Student currentUser;
    public Tutor tutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makebooking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = this.getIntent().getExtras();
        currentUser = new Student((User) bundle.getSerializable("user"));
        setContent();

        setSupportActionBar(toolbar);
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
    Returns it as a new Booking Object
    Test here for no. of students exceeding max for booking
    TODO: Create booking use cases use this method!
     */
    public String getDetails(){
        String booking = "PLACEHOLER";
        return booking;
    }

    /*
    following 4 methods generates the content of each of the items in some way
    TODO: make this more dynamic, changing the time/date based off the subject
    TODO: set up listeners to hide date->time until the preceding list has a selection
     */
    private void setContent(){
        setSubjectList(currentUser);
        setDateList(tutor);
        setTimeList(content);
        TextView tutorName = (TextView) findViewById(R.id.tutor);
        tutorName.setText(tutor.getFirstName() + " " + tutor.getLastName());
        TextView location = (TextView) findViewById(R.id.location);
        location.setText(content.getLocation());
    }

    private void setSubjectList(final Student student){
        Spinner subjectNo = (Spinner)findViewById(R.id.subject);
        subjectNo.setPrompt("Select Subject");
        subjectNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String subjectSelection = adapterView.getAdapter().getItem(i).toString().substring(0,5);
                tutor = new Tutor(student.getTutorsForIndex(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, student.getSubjects());
        subjectNo.setAdapter(adapter);
    }

    private void setDateList(Tutor tutor){
        Spinner consDate = (Spinner)findViewById(R.id.date);
        consDate.setPrompt("Select Date");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tutor.getAvailableDates());
        consDate.setAdapter(adapter);
    }

    private void setTimeList(Availability availability){
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

    private String getSpinnerContent(Spinner spinner){
        return spinner.getSelectedItem().toString();
    }

    /* Eventually, obtain max booked from booking object
    private boolean isNotFull(){
        if (content.getStudentBooked() >= content.getStudentLimit()) {

            Log.e("Test", "this was run");
            AlertDialog fullAlert = new AlertDialog.Builder(Controller_MakeBooking.this).create();
            fullAlert.setTitle("Alert");
            fullAlert.setMessage("This session has reached max number of student!");

            fullAlert.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Back",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            return false;
    }
    */
}
