package uts.sep.tcba.sepprototype.Controller;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import uts.sep.tcba.sepprototype.Model.Availability;
import uts.sep.tcba.sepprototype.Model.Booking;
import uts.sep.tcba.sepprototype.Model.Notification;
import uts.sep.tcba.sepprototype.Model.Tutor;

public class Controller_TutorMenu extends AppCompatActivity {

    private TextView mTextMessage; // TextView UI component to indicated an empty list
    private ListView listView; // ListView UI component to represent data
    private FloatingActionButton newAvailabilityButton; // Button UI component to handle creation of availabilities
    private LinkedList<Booking> bookings = new LinkedList<Booking>(); // stores user's bookings
    private LinkedList<Availability> availabilities = new LinkedList<Availability>(); // stores user's subjects
    private LinkedList<Notification> notifications = new LinkedList<Notification>(); // stores user's notifications
    private LinkedList<String> pageList = new LinkedList<String>(); // list used to display data from above 3 LinkedLists in UI
    private ArrayAdapter adapter; // defines adapter which binds the pageList data to the listView UI
    private FirebaseAuth mAuth; // stores auth state of Firebase Authentication database
    private Tutor currentTutor; // stores the details of the tutor currently logged in
    private boolean bookingTabSelected = true; // boolean flag for which tab is active
    private boolean availTabSelected = false; // boolean flag for which tab is active
    private boolean notifTabSelected = false; // boolean flag for which tab is active

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    // Reset tab selected boolean flags
                    bookingTabSelected = false;
                    availTabSelected = false;
                    notifTabSelected = false;
                    // Depending on the tab selected
                    switch(item.getItemId()){
                        case R.id.navigation_home: // if the booking tab is selected
                            bookingTabSelected = true; // set booking tab flag
                            mTextMessage.setText(R.string.bookings_placeholder); // set placeholder text if no bookings to show
                            newAvailabilityButton.setVisibility(View.GONE); // disable new availability
                            refreshListView(); // refresh list UI
                            return true;
                        case R.id.navigation_dashboard: // if the subject tab is selected
                            availTabSelected = true; // set availabilities tab flag
                            mTextMessage.setText(R.string.availabilites_placeholder); // set placeholder text if no availabilities to show
                            newAvailabilityButton.setVisibility(View.VISIBLE); // enable new availability
                            refreshListView(); // refresh list UI
                            return true;
                        case R.id.navigation_notifications:
                            notifTabSelected = true; // set notification tab flag
                            mTextMessage.setText(R.string.notifications_placeholder); // set placeholder text if notifications to show
                            newAvailabilityButton.setVisibility(View.GONE); // disable new availability
                            refreshListView(); // refresh list UI
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

        // Initialise the new availability button in the bottom right
        newAvailabilityButton = (FloatingActionButton) findViewById(R.id.newAvailability);
        newAvailabilityButton.setVisibility(View.GONE);
        newAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Controller_TutorMenu.this, Controller_MakeAvailability.class); // set destination to MakeAvailability activity
                Bundle b = new Bundle();
                b.putSerializable("user", currentTutor); // Pass current user
                intent.putExtras(b); // add data to intent
                startActivityForResult(intent, 1); // transition to MakeAvailability activity with the intent
            }
        });

        // Data ListView initialisation
        listView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pageList) { // Define a new Adapter to bind data to UI list
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
        listView.setAdapter(adapter); // Bind the adapted to the UI list
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Set a listener for clicking item in the ListView to trigger the ViewBooking or ViewAvailability activity on the pressed list item
                if (bookingTabSelected) { // if booking selected
                    Booking selectedItem = bookings.get(position); // get booking selected
                    // Pass booking data to ViewBooking activity
                    Intent intent = new Intent(Controller_TutorMenu.this, Controller_ViewBooking.class); // set destination to ViewBooking activity
                    Bundle b = new Bundle();
                    b.putSerializable("user", currentTutor); // Pass current user
                    b.putSerializable("booking", selectedItem); // Pass selected booking
                    intent.putExtras(b); // add data to intent
                    startActivityForResult(intent, 2); // transition to ViewBooking activity with the intent
                }else if(availTabSelected) { // if availability selected
                    Intent intent = new Intent(Controller_TutorMenu.this, Controller_ViewAvailability.class); // set destination to ViewAvailability activity
                    Availability selectedAvailability = availabilities.get(position); // get availability selected
                    Bundle b = new Bundle();
                    b.putSerializable("user", currentTutor); // Pass current user
                    b.putSerializable("availability", selectedAvailability); // Pass selected availability
                    intent.putExtras(b); // add data to intent
                    startActivityForResult(intent, 3); // transition to MakeBooking activity with the intent
                }
            }
        });

        // User initialisation
        int loggedInUserID = Integer.parseInt(getIntent().getStringExtra("user")); // Fetch user from previous activity
        currentTutor = new Tutor(loggedInUserID); // create tutor object from user ID (constructor pulls data directly from Firebase)

        // Firebase data change listeners initialised and data fetched
        initFirebase();

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
                Log.e("FirebaseFailure", databaseError.toString());
            }
        });

        // Set a listener on the user's ID reference in Firebase so that when any data is created/updated/removed (namely Availabilities), the changes will be pulled to device
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users/" + currentTutor.getID());
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAvailabilities(dataSnapshot); // Populate user availabilities
                currentTutor.setAvailabilities(availabilities);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseFailure", databaseError.toString());
            }
        });

        // Set a listener for when the tutor logs in to tutor's notifications since last log in
        DatabaseReference refNot = FirebaseDatabase.getInstance().getReference("Users/" + currentTutor.getID());
        refNot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setTitle(getTitle() + " - " + currentTutor.getFullName() + " (T)"); // set title of menu to include user's name
                for (DataSnapshot d : dataSnapshot.child("Notifications").getChildren()) {
                    notifications.add(new Notification(d)); //  get all notifications for user
                }
                FirebaseDatabase.getInstance().getReference("Users/" + currentTutor.getID()).child("Notifications").setValue(null); // Clear notifications from Firebase
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseFailure", databaseError.toString());
            }
        });
    }

    public void getBookings(DataSnapshot dataSnapshot) {
        bookings.clear(); // clears currently stored bookings
        for (DataSnapshot booking : dataSnapshot.getChildren()) { // for each booking
            if (booking.child("tutor").getValue(Integer.class) == (currentTutor.getID())) { // if the the logged in tutor is the tutor hosting the booking
                Log.i("Booking", booking.toString()); // informational debug statement written to log indicating a booking fetched
                bookings.add(new Booking(booking)); // create a new booking object to be stored locally
            }
        }
        sortBookings(); // sorts bookings chronologically
        refreshListView(); // refresh list UI
    }

    public void sortBookings() { // chronologically sorts bookings based on start time
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
        availabilities.clear(); // clears currently stored availabilities
        for (DataSnapshot availabiliy : dataSnapshot.child("Availabilities").getChildren()) { // for each availability
            Log.i("Availability", availabiliy.toString()); // informational debug statement written to log indicating an availability has been fetched
            availabilities.add(new Availability(availabiliy)); // create a new availability object to be stored locally
        }
        sortAvailabilities(); // sorts availabilities chronologically
        refreshListView(); // refresh list UI
    }

    public void sortAvailabilities() { // chronologically sorts availabilities based on start time
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
        if (bookingTabSelected) { // if the booking tab is the active tab
            for (Booking b : bookings) {
                pageList.add(b.toString()); // load bookings into the list
            }
        } else if (availTabSelected) { // if the availabilities tab is the active tab
            for (Availability a : availabilities) {
                pageList.add(a.toString()); // load availabilities into the list
            }
        } else if (notifTabSelected) { // if the notifications tab is the active tab
            for (Notification not : notifications) {
                pageList.add(not.createdMessageToTutor());  // load notifications into the list
            }
        }
        if (pageList.size() > 0) { // if the list is not empty
            mTextMessage.setVisibility(View.GONE); // hide list empty message
        } else {
            mTextMessage.setVisibility(View.VISIBLE); // display list empty message
        }
        adapter.notifyDataSetChanged(); // notify the list that the data has changed
    }

    public void addAvailabilityToFirebase(Availability availability) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + currentTutor.getID() + "/Availabilities"); // gets the tutor's availabilities reference in Firebase
        ref.push().setValue(availability); // pushes the availability object to a new reference child to the tutor's Availabilities reference in Firebase
    }

    @Override
    public void onBackPressed(){ // handles logging out, creates log out dialog and logs user out if they affirm
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
                        mAuth.signOut(); // sign user out of application
                        finish(); // return to login activity
                    }
                });
        alertDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // Return method from MakeAvailability and from ViewBooking
        if (requestCode == 1 && data != null) {  // if the activity returning to the TutorMenu is MakeAvailability
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras(); // get data parsed in Intent
                Availability availability = (Availability) bundle.getSerializable("availability"); // extract Availability object from MakeAvailability controller
                addAvailabilityToFirebase(availability); // save the availability to Firebase
            }
            BottomNavigationView view = (BottomNavigationView) findViewById(R.id.navigation_tutor);
            view.setSelectedItemId(R.id.navigation_dashboard); // display availabilities tab
        }
    }
}
