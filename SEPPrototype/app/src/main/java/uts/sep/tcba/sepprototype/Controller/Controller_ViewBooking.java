package uts.sep.tcba.sepprototype.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uts.sep.tcba.sepprototype.Model.Booking;

public class Controller_ViewBooking extends AppCompatActivity {

    public Booking currentBooking;
    private String userID;
    private String userType;
    private Button cancelButton, editButton;
    private TextView subject , tutor, date, time, locationText, location, capacity, description, descriptionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);


        Bundle bundle = this.getIntent().getExtras();
        currentBooking = (Booking) bundle.getSerializable("booking");
        userID = bundle.getString("id");
        userType = bundle.getString("userType");

        //Toolbar setting, disable edit button for student
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(userType.equals("Student")) {
            editButton = (Button) findViewById(R.id.edit);
            editButton.setVisibility(View.GONE);
        }

        setContent(bundle.getString("subject"));

        //Cancel Button Code
        cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Controller_ViewBooking.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to remove this booking?");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                currentBooking.remove(userType, currentBooking, userID);
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });

        //Edit Button on Tool bar
        editButton = (Button) findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),Controller_EditBooking.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void setContent(String subjectString) {
        this.subject = (TextView) findViewById(R.id.subject);
        this.tutor = (TextView) findViewById(R.id.tutor);
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.locationText = (TextView) findViewById(R.id.locationText);
        this.location = (TextView) findViewById(R.id.location);
        this.capacity = (TextView) findViewById(R.id.capacity);
        this.description = (TextView) findViewById(R.id.description);
        this.descriptionText = (TextView) findViewById(R.id.descriptionText);

        subject.setText(subjectString);
        tutor.setText(currentBooking.getTutorName() + " (" + currentBooking.getTutor() + ")");
        date.setText(currentBooking.getDate());
        time.setText(currentBooking.getStartTime() + " - " + currentBooking.getEndTime());
        if(currentBooking.getDescription()== null){
            descriptionText.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }else{
            description.setText(currentBooking.getDescription());
        }
        description.setText(currentBooking.getDescription());
        if(currentBooking.getLocation() == "") {
            locationText.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
        }else{
            location.setText(currentBooking.getLocation());
        }
        capacity.setText(currentBooking.getStudents().size() + " / "+ currentBooking.getCapacity());
    }



}
