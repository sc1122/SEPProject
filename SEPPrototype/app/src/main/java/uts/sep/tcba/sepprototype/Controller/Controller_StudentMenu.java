package uts.sep.tcba.sepprototype.Controller;

import android.app.AlertDialog;
import android.content.Context;
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

import uts.sep.tcba.sepprototype.Model.Booking;
import uts.sep.tcba.sepprototype.Model.Notification;
import uts.sep.tcba.sepprototype.Model.Student;

public class Controller_StudentMenu extends AppCompatActivity{

    private TextView mTextMessage;
    private ListView listView;
    private FloatingActionButton newBookingButton;
    private LinkedList<Booking> bookings = new LinkedList<Booking>();
    private LinkedList<String> subjects = new LinkedList<String>();
    private LinkedList<Notification> notifications = new LinkedList<Notification>();
    private LinkedList<String> pageList = new LinkedList<String>();
    private ArrayAdapter adapter;
    private FirebaseAuth mAuth;
    private Student currentStudent;
    private boolean bookingTabSelected = true;
    private boolean subjectTabSelected = false;
    private boolean notifTabSelected = false;
    final Context context = this; //TODO: not sure what this is doing
    private boolean newBooking = true;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    // Reset tab selected boolean flags
                    bookingTabSelected = false;
                    subjectTabSelected = false;
                    notifTabSelected = false;
                    // Depending on the tab selected
                    switch(item.getItemId()){
                        case R.id.navigation_home: // if the booking tab is selected
                            bookingTabSelected = true; // set booking tab flag
                            mTextMessage.setText(R.string.bookings_placeholder); // set placeholder text if no bookings to show
                            newBookingButton.setVisibility(View.VISIBLE); // enable new booking
                            refreshListView(); // refresh list UI
                            return true;
                        case R.id.navigation_dashboard: // if the subject tab is selected
                            subjectTabSelected = true; // set subject tab flag
                            mTextMessage.setText(R.string.subjects_placeholder); // set placeholder text if no subjects to show
                            newBookingButton.setVisibility(View.GONE); // disable new booking
                            refreshListView(); // refresh list UI
                            return true;
                        case R.id.navigation_notifications: // if the notification tab is selected
                            notifTabSelected = true; // set notification tab flag
                            mTextMessage.setText(R.string.notifications_placeholder); // set placeholder text if notifications to show
                            newBookingButton.setVisibility(View.GONE); // disable new booking
                            refreshListView(); // refresh list UI
                            return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Activity initialisation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentmenu);
        mTextMessage = (TextView) findViewById(R.id.message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_student);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Data ListView initialisation
        listView = (ListView) findViewById(R.id.list); // Set ListView UI element to display different lists based on tab selected
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Set a listener for clicking item in the ListView to trigger the view booking screen on the pressed list item
                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_student);
                if (bookingTabSelected) {
                    Intent intent = new Intent(Controller_StudentMenu.this, Controller_ViewBooking.class);
                    Booking selectedItem = bookings.get(position);
                    intent.putExtra("booking", selectedItem);
                    intent.putExtra("id",currentStudent.getID()+"");
                    intent.putExtra("userType" , currentStudent.getType());
                    intent.putExtra("subject", currentStudent.getSubjectFromSubjects(selectedItem.getSubject()));
                    startActivityForResult(intent, 1);
                }
            }
        });

        // Initialise the new booking button in the bottom right
        newBookingButton = (FloatingActionButton) findViewById(R.id.newBooking);
        newBookingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Controller_StudentMenu.this, Controller_MakeBooking.class);
                Bundle b = new Bundle();
                b.putSerializable("user", currentStudent);
                intent.putExtras(b);
                startActivityForResult(intent, 1);
            }
        });

        // User initialisation
        int loggedInUserID = Integer.parseInt(getIntent().getStringExtra("user")); // Fetch user from previous activity
        currentStudent = new Student(loggedInUserID); // create student object from user ID (constructor pulls data directly from Firebase)

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
                Log.d("FirebaseFailure", databaseError.toString());
            }
        });

        // Set a listener for when the student logs in to fetch the student's subjects and their notifications since last log in
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users/" + currentStudent.getID());
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setTitle(getTitle() + " - " + currentStudent.getFullName() + " (S)"); // set title of menu to include user's name
                subjects.addAll(currentStudent.getSubjects()); // Populate user subjects
                for (DataSnapshot d : dataSnapshot.child("Notifications").getChildren()) {
                    notifications.add(new Notification(d)); //  get all notifications for user
                }
                FirebaseDatabase.getInstance().getReference("Users/" + currentStudent.getID()).child("Notifications").setValue(null); // Clear notifications from Firebase
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public void getBookings(DataSnapshot dataSnapshot) {
        bookings.clear(); // clears currently stored bookings
        for (DataSnapshot booking : dataSnapshot.getChildren()) { // for each booking
            Log.i("Booking", booking.toString()); // informational debug statement written to log indicating a booking fetched
            if (booking.child("students").child(String.valueOf(currentStudent.getID())).exists()) { // if the the logged in tutor is the tutor hosting the booking
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
                    Date date1 = formatter.parse(b1.getDate() + " " + b1.getStartTime());
                    Date date2 = formatter.parse(b2.getDate() + " " + b2.getStartTime());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void refreshListView() { // reload the listview data on tab change
        pageList.clear(); // clear the list
        if (bookingTabSelected) { // if the bookings tab is the active tab
            for (Booking b : bookings) {
                pageList.add(b.toString()); // load bookings into the list
            }
        } else if (subjectTabSelected) { // if the subjects tab is the active tab
            pageList.addAll(subjects); // load subjects into the list
        } else if (notifTabSelected) { // if the notifications tab is the active tab
            for (Notification not : notifications) {
                pageList.add(not.cancelledMessageToStudent());  // load notifications into the list
            }
        }
        if (pageList.size() > 0) { // if the list is not empty
            mTextMessage.setVisibility(View.GONE); // hide list empty message
        } else {
            mTextMessage.setVisibility(View.VISIBLE); // display list empty message
        }
        adapter.notifyDataSetChanged(); // notify the list that the data has changed
    }

    public void addBookingToFirebase(final Booking booking, String existingBookingID) {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings"); // get reference of bookings in Firebase
        DatabaseReference newBookingRef = bookingsRef.push(); // get the database reference for new booking
        DatabaseReference targetRef;
        if (newBooking) { // if its a new booking (user not being added to an existing booking object in Firebase)
            newBookingRef.setValue(booking); // push the booking object to Firebase
            targetRef = newBookingRef; // set the target ref to the location of the new booking object
        } else { // otherwise being added to an existing booking object in Firebase
            targetRef = bookingsRef.child(existingBookingID); // set the target ref to the location of the existing booking
        }
        setCurrentStudentInformation(targetRef, booking.getCurrentStudentNotes()); // adds current student and their notes to the targeted booking
    }

    public void setCurrentStudentInformation(DatabaseReference bookingStudentStatus, String studentNotes) {
        bookingStudentStatus.child("students").child(String.valueOf(currentStudent.getID())).child("Name").setValue(currentStudent.getFullName()); // adds student to booking
        bookingStudentStatus.child("students").child(String.valueOf(currentStudent.getID())).child("Notes").setValue(studentNotes); // adds student's notes to booking
    }

    // Handle log out
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
                        mAuth.signOut(); // sign user out of application
                        finish(); // return to login activity
                    }
                });
        alertDialog.show();
    }

    // Return method from MakeBooking controller
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) { // if the activity returning to the StudentMenu is MakeBooking
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras(); // extract data passed back
                String existingBookingID = bundle.getString("existingBookingID"); // get the existingBookingID if there is one
                if (existingBookingID.equals("")) { // if there isn't an existing booking
                    newBooking = true;
                } else { // otherwise there is an existing booking for the student to be added to
                    newBooking = false;
                }
                Booking booking = (Booking) bundle.getSerializable("booking"); // extract booking object from makeBooking controller
                addBookingToFirebase(booking, existingBookingID); // save the relevant information to Firebase
                sortBookings(); // sort the bookings chronologically
                adapter.notifyDataSetChanged(); // notify the listview to refresh the UI
            }
        }
    }
}
