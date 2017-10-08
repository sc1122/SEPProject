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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;


public class Controller_TutorMenu extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView listView;
    private FloatingActionButton newAvailabilityButton;
    private ArrayList<Booking> bookings = new ArrayList<Booking>();
    private ArrayList<Availability> availabilities = new ArrayList<Availability>();
    private ArrayList<String> pageList = new ArrayList<String>();
    private ArrayAdapter adapter;
    private FirebaseAuth mAuth;
    private Tutor currentTutor;
    private boolean bookingTab = true;
    private boolean availTab = false;
    private boolean existingAvailability = true;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    switch(item.getItemId()){
                        case R.id.navigation_home:
                            mTextMessage.setText(R.string.bookings_placeholder);
                            bookingTab = true;
                            availTab = false;
                            newAvailabilityButton.setVisibility(View.GONE);
                            refreshListView();
                            return true;
                        case R.id.navigation_dashboard:
                            mTextMessage.setText(R.string.availabilites_placeholder);
                            bookingTab = false;
                            availTab = true;
                            newAvailabilityButton.setVisibility(View.VISIBLE);
                            refreshListView();
                            return true;
                        case R.id.navigation_notifications:
                            mTextMessage.setText(R.string.notifications_placeholder);
                            bookingTab = false;
                            availTab = false;
                            newAvailabilityButton.setVisibility(View.GONE);
                            refreshListView();
                            return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Activity Initialisation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutormenu);
        mTextMessage = (TextView) findViewById(R.id.message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_tutor);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Fetch user from previous activity
        Intent intent = getIntent();
        int loggedInUserID = Integer.parseInt(intent.getStringExtra("user"));
        currentTutor = new Tutor(loggedInUserID);

        // Configure Firebase listeners
        initFirebase();

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
                if (bookingTab) {
                    Intent intent = new Intent(Controller_TutorMenu.this, Controller_ViewBooking.class);
                    Booking selectedItem = bookings.get(position);
                    Log.d("BOOKING", selectedItem.toString());
                    intent.putExtra("booking", selectedItem);
                    intent.putExtra("subject", currentTutor.getSubjects());
                    intent.putExtra("userType", currentTutor.getType());
                    intent.putExtra("subject", currentTutor.getSubjectFromSubjects(selectedItem.getSubject()));
                    startActivityForResult(intent, 2);
                }
            }
        });

        // Button press now creates a new availability
        newAvailabilityButton = (FloatingActionButton) findViewById(R.id.newAvailability);
        newAvailabilityButton.setVisibility(View.GONE);
        newAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Controller_TutorMenu.this, Controller_MakeAvailability.class);
                Bundle b = new Bundle();
                b.putSerializable("user", currentTutor);
                intent.putExtras(b);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void initFirebase() {
        // Set a listener on Bookings reference in Firebase so that when a booking is created/updated/removed, the changes will be pulled to device
        DatabaseReference refBooking = FirebaseDatabase.getInstance().getReference("Bookings");
        refBooking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getBookings(dataSnapshot); // Populate user bookings
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseFailure", databaseError.toString());
            }
        });

        // Set a listener on the user's ID reference in Firebase so that when any data is created/updated/removed (namely Notifications & Availabilities), the changes will be pulled to device
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users/" + currentTutor.getID());
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!getTitle().toString().contains(currentTutor.getFullName())) {
                    setTitle(getTitle() + " - " + currentTutor.getFullName() + " (T)");
                }
                getAvailabilities(dataSnapshot); // Populate user availabilities
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseFailure", databaseError.toString());
            }
        });
    }

    public void getBookings(DataSnapshot dataSnapshot) {
        bookings.clear(); // clears currently stored bookings
        for (DataSnapshot booking : dataSnapshot.child("Bookings").getChildren()) { // for each booking
            //Log.d("GetBooking", booking.toString());
            if (booking.child("tutor").getValue().toString().equals(currentTutor.getID())) { // if the the logged in tutor is the tutor hosting the booking
                bookings.add(new Booking(booking, currentTutor.getFullName())); // create a new booking object to be stored locally
            }
        }
        sortBookings(); // sorts bookings chronologically
        refreshListView(); // refresh list UI
    }

    public void sortBookings() {
        Collections.sort(bookings, new Comparator<Booking>() {
            public int compare(Booking b1, Booking b2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                try {
                    Date date1 = formatter.parse(b1.getDate() + " " + b1.getStartTime()); // convert date-time string of first booking into date object for comparison
                    Date date2 = formatter.parse(b2.getDate() + " " + b2.getStartTime()); // convert date-time string of second booking into date object for comparison
                    return date1.compareTo(date2); // compare the two date objects to which precedes the other and sort chronologically
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void getAvailabilities(DataSnapshot dataSnapshot) {
        availabilities.clear();
        for (DataSnapshot data : dataSnapshot.child("Availabilities").getChildren()) {
            availabilities.add(new Availability(data)); // create a new availability object to be stored locally
        }
        sortAvailabilities(); // sorts bookings chronologically
        refreshListView(); // refresh list UI
    }

    public void sortAvailabilities() {
        Collections.sort(availabilities, new Comparator<Availability>() {
            @Override
            public int compare(Availability a1, Availability a2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                try {
                    Date date1 = formatter.parse(a1.getDate() + " " + a1.getStartTime()); // convert date-time string of first availability into date object for comparison
                    Date date2 = formatter.parse(a2.getDate() + " " + a2.getStartTime()); // convert date-time string of second availabilty into date object for comparison
                    return date1.compareTo(date2); // compare the two date objects to which precedes the other and sort chronologically
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void refreshListView() {
        pageList.clear(); // clear the list
        if (bookingTab) { // if the booking tab is the active tab
            for (Booking b : bookings) {
                pageList.add(b.toString()); // load bookings into the list
            }
        } else if (availTab) { // if the availabilities tab is the active tab
            for (Availability a : availabilities) {
                pageList.add(a.toString()); // load availabilities into the list
            }
        }
        if (pageList.size() > 0) { // if the list is not empty
            mTextMessage.setVisibility(View.GONE); // hide list empty message
        } else {
            mTextMessage.setVisibility(View.VISIBLE); // display list empty message
        }
        adapter.notifyDataSetChanged(); // notify the list that the data has changed
    }

    // Return method from make availability and from view booking
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Availability availability = (Availability) bundle.getSerializable("availability");
                //Check if selected Time period makes sense to none-time traveller;
                if(selectedTimeIsCorrect(availability)) {
                    availabilityDoesNotExist(availability);
                }else{
                    showErrorDialog("Improper Time Selected", "Please select proper time slot");
                }
                //TODO: Add availability to availability list
            }
            BottomNavigationView view = (BottomNavigationView) findViewById(R.id.navigation_tutor);
            view.setSelectedItemId(R.id.navigation_dashboard);
        }
    }

    private boolean selectedTimeIsCorrect(Availability availability){
        double startTime = Double.parseDouble(availability.getStartTime().replace(':','.'));
        double endTime = Double.parseDouble(availability.getEndTime().replace(':','.'));
        if(startTime >= endTime)
            return false;
        return true;
    }

    private void availabilityDoesNotExist(final Availability availability) {
        String tutorId = String.valueOf(currentTutor.getID());
        final String date = availability.getDate();
        final double startTime = Double.parseDouble(availability.getStartTime().replace(':','.'));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(tutorId).child("Availabilities");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainloop:
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    String ssStartTime = snapshot.child("startTime").getValue().toString().replace(':','.');
                    String ssEndTime = snapshot.child("endTime").getValue().toString().replace(':','.');
                    //If availability with the same date is found
                    if(snapshot.child("date").getValue().equals(date)) {
                        //if the start time is within the range of existing availability with the same date
                        if((startTime >= Double.parseDouble(ssStartTime) && startTime < Double.parseDouble(ssEndTime))){
                            //show error if it is
                            showErrorDialog("Availability Error", "Input availability already existed");
                            existingAvailability = true;
                            //Break out of the main loop to prevent checking for another round
                            break mainloop;
                        }else {
                            //Check again if there is existing availability because there might be multiple availability period on one day
                                existingAvailability = false;
                            }
                        } else{
                        existingAvailability = false;
                    }
                }

                if(existingAvailability == false){
                    addAvailabilityToFirebase(availability);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addAvailabilityToFirebase(Availability availability) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + currentTutor.getID() + "/Availabilities");
        DatabaseReference bookingStatus = ref.push();
        bookingStatus.setValue(availability);
    }


    private void showErrorDialog(String title, String errorMessage){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder(Controller_TutorMenu.this).create();
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
}
