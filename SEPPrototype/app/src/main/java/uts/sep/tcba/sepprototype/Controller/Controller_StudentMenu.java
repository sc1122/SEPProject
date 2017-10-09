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
import uts.sep.tcba.sepprototype.R;

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
private boolean bookingTab = true;
private boolean subjectTab = false;
private boolean notifTab = false;
final Context context = this; //TODO: not sure what this is doing
private boolean newBooking = true;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    bookingTab = false;
                    subjectTab = false;
                    notifTab = false;
                    switch(item.getItemId()){
                        case uts.sep.tcba.sepprototype.R.id.navigation_home:
                            bookingTab = true;
                            mTextMessage.setText(R.string.bookings_placeholder);
                            newBookingButton.setVisibility(View.VISIBLE);
                            refreshListView();
                            return true;
                        case R.id.navigation_dashboard:
                            subjectTab = true;
                            mTextMessage.setText(R.string.subjects_placeholder);
                            newBookingButton.setVisibility(View.GONE);
                            refreshListView();
                            return true;
                        case R.id.navigation_notifications:
                            notifTab = true;
                            mTextMessage.setText(R.string.notifications_placeholder);
                            newBookingButton.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_studentmenu);
        mTextMessage = (TextView) findViewById(R.id.message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_student);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Fetch user from previous activity
        Intent intent = getIntent();
        int loggedInUserID = Integer.parseInt(intent.getStringExtra("user"));
        String loggedInUserType = intent.getStringExtra("type");
        currentStudent = new Student(loggedInUserID);

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

                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_student);
                if (bookingTab) {
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

        // Define the new booking button in the bottom right
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
                setTitle(getTitle() + " - " + currentStudent.getFullName() + " (S)");
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
            //Log.d("GetBooking", booking.toString());
            if (booking.child("students").child(String.valueOf(currentStudent.getID())).exists()) { // if the the logged in tutor is the tutor hosting the booking
                bookings.add(new Booking(booking)); // create a new booking object to be stored locally
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

    public void refreshListView() {
        pageList.clear(); // clear the list
        if (bookingTab) { // if the bookings tab is the active tab
            for (Booking b : bookings) {
                pageList.add(b.toString()); // load bookings into the list
            }
        } else if (subjectTab) { // if the subjects tab is the active tab
            pageList.addAll(subjects); // load subjects into the list
        } else if (notifTab) { // if the notifications tab is the active tab
            for (Notification not : notifications) {
                pageList.add(not.toString());  // load notifications into the list
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
        DatabaseReference newBookingRef = bookingsRef.push(); // get new reference
        DatabaseReference targetRef;
        if (newBooking) {
            newBookingRef.setValue(booking);
            targetRef = newBookingRef;
        } else {
            targetRef = bookingsRef.child(existingBookingID);
        }
        setAttendingBooking(targetRef);
    }

    public void setAttendingBooking(DatabaseReference bookingStudentStatus) {
        bookingStudentStatus.child("students").child(String.valueOf(currentStudent.getID())).setValue(currentStudent.getFirstName() + " " + currentStudent.getLastName());
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
                Bundle bundle = data.getExtras();
                String existingBookingID = bundle.getString("existingBookingID");
                if (existingBookingID.equals("")) {
                    Log.d("IT'S", "NULL");
                    newBooking = true;
                } else {
                    Log.d("IT'S", "NOT");
                    newBooking = false;
                }
                Booking booking = (Booking) bundle.getSerializable("booking");
                addBookingToFirebase(booking, existingBookingID);
                sortBookings();
                adapter.notifyDataSetChanged();
            }
        }
    }
}
