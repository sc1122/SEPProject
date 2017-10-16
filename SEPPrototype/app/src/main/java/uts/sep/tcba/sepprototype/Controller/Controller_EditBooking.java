package uts.sep.tcba.sepprototype.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;

import uts.sep.tcba.sepprototype.Model.Booking;
import uts.sep.tcba.sepprototype.Model.User;

public class Controller_EditBooking extends AppCompatActivity {

    public Booking currentBooking;
    public User currentUser;
    private TextView subject , time, date, tutor, capacity;
    private EditText location;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get data from previous activity
        Bundle bundle = getIntent().getExtras();
        currentBooking = (Booking) bundle.getSerializable("booking");
        currentUser = (User) bundle.getSerializable("user");

        //Populate data
        setContent(currentBooking);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(Controller_EditBooking.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you make changes to the booking?"); // Issue a warning

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Abort cancellation
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String loc = location.getText().toString(); //Get location text field
                                editBookingLocation(currentBooking, loc); //Edit the location of the specific booking only, to offer more flexibility to tutor

                                //Finish this activity as well as viewBooking activity
                                Intent intent = new Intent(getApplicationContext(), Controller_TutorMenu.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        });
                alertDialog.show();
            }
        });
    }


    private void setContent(Booking currentBooking) {
        //Link view to fields
        this.subject = (TextView) findViewById(R.id.subject);
        this.tutor = (TextView) findViewById(R.id.tutor);
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.capacity = (TextView) findViewById(R.id.capacity);
        this.location = (EditText) findViewById(R.id.location);
        this.save = (Button) findViewById(R.id.save);

        //Populate each field with booking information
        subject.setText(String.valueOf(currentUser.getSubjectFromSubjects(currentBooking.getSubject())));
        tutor.setText(currentBooking.getTutorName() + " (" +currentBooking.getTutor() + ")");
        date.setText(currentBooking.getDate());
        time.setText(currentBooking.getStartTime() + " - " + currentBooking.getEndTime());
        location.setText(currentBooking.getLocation());
        capacity.setText(currentBooking.getStudents().size() + "/" + currentBooking.getCapacity());
    }

    //Edit this booking Location only to allow more flexibility to the tutor
    private void editBookingLocation(Booking currentBooking, String location) {
        currentBooking.edit(currentBooking, location);
    }

}
