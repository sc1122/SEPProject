package uts.sep.tcba.sepprototype.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;

import uts.sep.tcba.sepprototype.Model.Availability;
import uts.sep.tcba.sepprototype.Model.Tutor;

public class Controller_ViewAvailability extends AppCompatActivity {
    private Button deleteButton, saveButton;
    private TextView date, time, capacity;
    private EditText location;
    public Availability currentAvailability;
    public Tutor currentTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_availability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get data from menu
        Bundle bundle = this.getIntent().getExtras();
        currentAvailability = (Availability) bundle.getSerializable("availability");
        currentTutor = (Tutor) bundle.getSerializable("user");

        // Load UI with availability
        setContent(currentAvailability);

        // Initialise the Save button action on tool bar
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Controller_ViewAvailability.this).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure you want to save your change");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!location.getText().toString().isEmpty()) { // if there is text in the location field
                                    currentAvailability.setLocation(location.toString()); // set the location of the current availability to the inputted location
                                    editAvailability(currentAvailability, currentTutor.getID(), location.getText().toString());  // set the location of the current availability and all subsequent bookings to the inputted location
                                }
                                finish(); // return to menu
                            }
                        });
                alertDialog.show();
            }
        });

        // Initialise the Delete button action
        deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Controller_ViewAvailability.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to remove this availability?");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAvailability(currentAvailability, currentTutor.getID());
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    // Populate the UI elements with the details of the availability
    private void setContent(Availability currentAvailability) {
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.location = (EditText) findViewById(R.id.location);
        this.capacity = (TextView) findViewById(R.id.capacity);

        date.setText(currentAvailability.getDate());
        time.setText(currentAvailability.getStartTime() + " - " + currentAvailability.getEndTime());
        location.setText(currentAvailability.getLocation());
        capacity.setText(currentAvailability.getCapacity() + "");
    }

    // removes the availability
    private void deleteAvailability(Availability currentAvailability, int userID) {
        currentAvailability.remove(currentAvailability, String.valueOf(userID));
    }

    // edits the location of the availability
    private void editAvailability(Availability currentAvailability, int userID, String location) {
        currentAvailability.edit(currentAvailability, String.valueOf(userID), location);
    }
}