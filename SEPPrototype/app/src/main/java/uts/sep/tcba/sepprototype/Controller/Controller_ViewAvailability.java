package uts.sep.tcba.sepprototype.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;

import uts.sep.tcba.sepprototype.Model.Availability;

public class Controller_ViewAvailability extends AppCompatActivity {
    private Button deleteButton, saveButton;
    private String userID;
    private TextView date, time, locationText, capacity;
    private EditText location;
    public Availability currentAvailability;

    public boolean hasChanged = false;

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

        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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
            public void onClick(View v) {
                if (hasChanged) {
                    currentAvailability.setLocation(location.toString());
                }
                editAvailability(currentAvailability, userID, location.getText().toString());
                finish();
            }
        });
    }

    private void setContent(Availability currentAvailability) {
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.locationText = (TextView) findViewById(R.id.locationText);
        this.location = (EditText) findViewById(R.id.location);
        this.capacity = (TextView) findViewById(R.id.capacity);

        date.setText(currentAvailability.getDate());
        time.setText(currentAvailability.getStartTime() + " - " + currentAvailability.getEndTime());
        location.setText(currentAvailability.getLocation());
        capacity.setText(currentAvailability.getCapacity() + "");
    }

    private void deleteAvailability(Availability currentAvailability, String userID) {
        currentAvailability.remove(currentAvailability, userID);
    }

    private void editAvailability(Availability currentAvailability, String userID, String location) {
        currentAvailability.edit(currentAvailability, userID, location);
    }

    private void editAvailability(Availability currentAvailability) {

    }

    private void changeLocation() {
        currentAvailability.setLocation(location.toString());
        Log.d("HOPE THIS WORKS!?!?", "YESSSS");
    }

}