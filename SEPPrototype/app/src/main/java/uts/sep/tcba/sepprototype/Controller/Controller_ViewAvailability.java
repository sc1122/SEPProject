package uts.sep.tcba.sepprototype.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uts.sep.tcba.sepprototype.Model.Availability;

public class Controller_ViewAvailability extends AppCompatActivity {
    private Button deleteButton, saveButton;
    private TextView date, time, locationText, location, capacity;
    private String userID;
    public Availability currentAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_availability);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = this.getIntent().getExtras();
        currentAvailability = (Availability) bundle.getSerializable("availability");
        userID = bundle.getString("userID");

        setContent(currentAvailability);

        //Delete Button Code
        deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteAvailability(currentAvailability, userID);
                finish();
            }
        });

        //Save Button on Tool bar
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
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

    private void deleteAvailability(Availability currentAvailability,String userID) {
        currentAvailability.remove(currentAvailability, userID);
    }

    private void editAvailability(Availability currentAvailability) {
    }
}
