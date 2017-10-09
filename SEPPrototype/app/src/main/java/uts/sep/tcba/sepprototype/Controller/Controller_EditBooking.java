package uts.sep.tcba.sepprototype.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.*;

import uts.sep.tcba.sepprototype.Model.Booking;
import uts.sep.tcba.sepprototype.R;

public class Controller_EditBooking extends AppCompatActivity {

    public Booking currentBooking;

    private TextView subject , tutor;
    private Spinner date, time;

    //private double time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setContent(Booking currentBooking) {
        this.subject = (TextView) findViewById(R.id.subject);
        this.tutor = (TextView) findViewById(R.id.tutor);
        this.date = (Spinner) findViewById(R.id.dateSpinner);
        this.time = (Spinner) findViewById(R.id.timeSpinner);

        subject.setText(String.valueOf(currentBooking.getSubject()) );


        tutor.setText(currentBooking.getTutor() + currentBooking.getTutorName());
        //date.setSpinner(currentBooking.getDate());
        time.setPrompt(currentBooking.getStartTime() + " - " + currentBooking.getEndTime());
    }


    //method for when time is changed
    public void changeTime() {
        handleSTime();
        handleETime();
    }



    public void handleSTime() {
        String sTime = time.getSelectedItem().toString();
        String[] splited_sTime = sTime.split(" - ");
        sTime = splited_sTime[0];
        currentBooking.setStartTime(Double.parseDouble(sTime));
    }

    public void handleETime() {
        String eTime = time.getSelectedItem().toString();
        String[] splited_eTime = eTime.split(" - ");
        eTime = splited_eTime[1];
        currentBooking.setEndTime(Double.parseDouble(eTime));
    }

}
