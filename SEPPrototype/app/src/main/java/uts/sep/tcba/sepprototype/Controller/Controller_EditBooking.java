package uts.sep.tcba.sepprototype.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.*;

import uts.sep.tcba.sepprototype.Model.Booking;

public class Controller_EditBooking extends AppCompatActivity {

    public Booking currentBooking;


    private TextView subject , time, date, tutor;
    private EditText location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setContent(currentBooking);
    }

    private void setContent(Booking currentBooking) {
        this.subject = (TextView) findViewById(R.id.subject);
        this.tutor = (TextView) findViewById(R.id.tutor);
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.location = (EditText) findViewById(R.id.location);

        subject.setText(String.valueOf(currentBooking.getSubject()) );
        tutor.setText(currentBooking.getTutor() + currentBooking.getTutorName());
        date.setText(currentBooking.getDate());
        time.setText(currentBooking.getStartTime() + " - " + currentBooking.getEndTime());
        location.setText(currentBooking.getLocation());


    }
}
