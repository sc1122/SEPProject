package uts.sep.tcba.sepprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class Controller_StudentMenu extends AppCompatActivity{

private TextView mTextMessage;
private ListView listView;
private FloatingActionButton newBookingButton;
private ArrayList<Booking> bookings = new ArrayList<Booking>();
private ArrayList<String> subjects = new ArrayList<String>();
private ArrayList<String> pageList = new ArrayList<String>();
private ArrayAdapter adapter;
private FirebaseAuth mAuth;
public User currentUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    pageList.clear();
                    mTextMessage.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    switch(item.getItemId()){
                        case R.id.navigation_home:
                            newBookingButton.setVisibility(View.VISIBLE);
                            for (Booking b: bookings) {
                                pageList.add(b.toString());
                            }
                            adapter.notifyDataSetChanged();
                            if (bookings.size() > 0) {
                                mTextMessage.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            return true;
                        case R.id.navigation_dashboard:
                            newBookingButton.setVisibility(View.GONE);
                            pageList.addAll(subjects);
                            adapter.notifyDataSetChanged();
                            if (subjects.size() > 0) {
                                mTextMessage.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            return true;
                        case R.id.navigation_notifications:
                            newBookingButton.setVisibility(View.GONE);
                            mTextMessage.setText(R.string.notifications_placeholder);
                            return true;
                    }
                    return false;
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Activity Initialisation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentmenu);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Fetch user from previous activity
        Intent intent = getIntent();
        String loggedInUserID = intent.getStringExtra("user");
        currentUser = new User(loggedInUserID);

        // Set a listener for when data is updated/loaded to refresh the view
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + loggedInUserID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refreshUserData();
                // Populate user bookings
                getBookings(String.valueOf(currentUser.getID()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });

        // Set ListView UI element to display different lists based on tab selected
        listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter to bind data to UI list
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pageList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                textView.setMinHeight(0); // Min Height
                textView.setMinimumHeight(0); // Min Height
                textView.setHeight(200); // Height
                textView.setTextSize(16);
                return view;
            }
        };

        // Bind the adapted to the UI list
        listView.setAdapter(adapter);

        // Set a listener for clicking item in the ListView to trigger the view booking screen on the pressed list item
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("HELLO", "WORLD");
                //Object o = listView.getItemAtPosition(position);  // Create view booking screen and load booking details
            }
        });

        // Define the new booking button in the bottom right
        newBookingButton = (FloatingActionButton) findViewById(R.id.newBooking);
        newBookingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Controller_StudentMenu.this, Controller_MakeBooking.class);
                intent.putExtra("caller", "Controller_StudentMenu");
                startActivityForResult(intent, 1);
            }
        });
    }

    public void refreshUserData() {
        setTitle(getTitle() + " - " + currentUser.getFirstName() + " " + currentUser.getLastName());
        subjects.addAll(currentUser.getSubjects());
    }

    public void getBookings(final String ID) {
        bookings.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Bookings");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot booking : dataSnapshot.getChildren()) {
                    Log.d("BOOKING", booking.toString());
                    if (booking.child("Students").child(ID).exists()) {
                        Log.d("STUDENT", booking.getValue().toString());
                        Booking b = new Booking(booking);
                        bookings.add(b);
                    } else if (booking.child("Tutor").getValue().toString().equals(ID)) {
                        Log.d("TUTOR", booking.getValue().toString());
                        Booking b = new Booking(booking);
                        bookings.add(b);
                    }
                }
                pageList.clear();
                for (Booking b: bookings) {
                    pageList.add(b.toString()); // Display bookings on initial load
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder(Controller_StudentMenu.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Would you like to log out?");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        finish();
                    }
                });
        alertDialog.show();
    }

    // Return method from make booking view
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                String booking = data.getStringExtra("result");
                //bookings.add(booking); // fix this shit
                pageList.add(booking);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
