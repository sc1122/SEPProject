package uts.sep.tcba.sepprototype.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import uts.sep.tcba.sepprototype.Model.Availability;
import uts.sep.tcba.sepprototype.R;

import com.google.firebase.database.FirebaseDatabase;


public class Controller_ViewAvailability extends AppCompatActivity {
    private Button deleteButton, saveButton;
    private TextView date, time, locationText, location, capacity;
    public Availability currentAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_availability);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = this.getIntent().getExtras();
        currentAvailability = (Availability) bundle.getSerializable("availability");
        setContent(currentAvailability);

        //Delete Button Code
        deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("REF", currentAvailability.toString());
                deleteAvailability(currentAvailability);
                finish();
            }
        });

        //Save Button on Tool bar
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                editAvailability(currentAvailability);
                finish();
            }
        });
    }



    private void setContent(Availability currentAvailability) {
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.locationText = (TextView) findViewById(R.id.locationText);
        this.location = (TextView) findViewById(R.id.location);
        this.capacity = (TextView) findViewById(R.id.capacity);

        date.setText(currentAvailability.getDate());
        time.setText(currentAvailability.getStartTime() + " - " + currentAvailability.getEndTime());
        location.setText(currentAvailability.getLocation());
        capacity.setText(currentAvailability.getCapacity() + "");
    }

    private void deleteAvailability(Availability currentAvailability) {
        Log.d("REF", "HI");
        currentAvailability.remove(currentAvailability);
    }

    private void editAvailability(Availability currentAvailability) {
    }
}
