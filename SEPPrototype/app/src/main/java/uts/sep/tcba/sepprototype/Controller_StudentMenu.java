package uts.sep.tcba.sepprototype;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class Controller_StudentMenu extends AppCompatActivity{

private TextView mTextMessage;
private ListView listView;
private FloatingActionButton newBookingButton;
private ArrayList<Booking> bookings = new ArrayList<Booking>();
private ArrayList<String> subjects = new ArrayList<String>();
private ArrayList<Notification> notifications = new ArrayList<Notification>();
private ArrayList<String> pageList = new ArrayList<String>();
private ArrayAdapter adapter;
private FirebaseAuth mAuth;
private Student currentStudent;
private boolean bookingTab = true;
final Context context = this;
private boolean newBooking = true;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    pageList.clear();
                    mTextMessage.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    switch(item.getItemId()){
                        case R.id.navigation_home:
                            bookingTab = true;
                            newBookingButton.setVisibility(View.VISIBLE);
                            refreshBookings();
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.navigation_dashboard:
                            bookingTab = false;
                            newBookingButton.setVisibility(View.GONE);
                            pageList.addAll(subjects);
                            adapter.notifyDataSetChanged();
                            if (subjects.size() > 0) {
                                mTextMessage.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            return true;
                        case R.id.navigation_notifications:
                            bookingTab = false;
                            newBookingButton.setVisibility(View.GONE);
                            mTextMessage.setText(R.string.notifications_placeholder);
                            for (Notification not : notifications) {
                                pageList.add(not.toString());
                            }
                            if (notifications.size() > 0) {
                                mTextMessage.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
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

        // Set a listener for when data is updated/loaded to refresh the view
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + loggedInUserID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Populate user subjects
                subjects.addAll(currentStudent.getSubjects());
                //  get all notifications for user
                for (DataSnapshot d : dataSnapshot.child("Notifications").getChildren()) {
                    notifications.add(new Notification(d));
                }
                // Clears notifications from Firebase
                FirebaseDatabase.getInstance().getReference("Users/" + currentStudent.getID()).child("Notifications").setValue(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!getTitle().toString().contains(currentStudent.getFirstName())) {
                    setTitle(getTitle() + " - " + currentStudent.getFirstName() + " " + currentStudent.getLastName() + " (S)");
                }
                // Populate user bookings
                getBookings(String.valueOf(currentStudent.getID()));
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

                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_student);
                if (bookingTab) {
                    Intent intent = new Intent(Controller_StudentMenu.this, Controller_ViewBooking.class);
                    Booking selectedItem = bookings.get(position);
                    Log.d("BOOKING", selectedItem.toString());
                    Log.d("BOOKINGID", selectedItem.getAvailabilityID());
                    Log.d("USERID", currentStudent.getID()+"");
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

    public void getBookings(final String ID) {
        bookings.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookings.clear();
                Log.d("HelloMemes", dataSnapshot.toString());
                for (DataSnapshot booking : dataSnapshot.child("Bookings").getChildren()) {
                    String tutorID = booking.child("tutor").getValue().toString();
                    DataSnapshot tutorBooked = dataSnapshot.child("Users").child(tutorID);
                    String tutorName = tutorBooked.child("FirstName").getValue().toString() + " " + tutorBooked.child("LastName").getValue().toString();
                    if (booking.child("students").child(ID).exists()) {
                        Log.d("STUDENT", booking.getValue().toString());
                        Booking b = new Booking(booking, tutorName);
                        bookings.add(b);
                    }
                }
                sortBookings();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public void sortBookings() {
        Collections.sort(bookings, new Comparator<Booking>() {
            public int compare(Booking b1, Booking b2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                try {
                    Date date1 = formatter.parse(b1.getDate() + " " + b1.getStartTime());
                    Log.d("DATE1", date1.toString());
                    Date date2 = formatter.parse(b2.getDate() + " " + b2.getStartTime());
                    Log.d("DATE2", date2.toString());
                    Log.d("COMPARE", String.valueOf(date1.compareTo(date2)));
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        refreshBookings();
    }

    public void refreshBookings() {
        if (bookingTab) {
            pageList.clear();
            for (Booking b: bookings) {
                pageList.add(b.toString());
            }
            if (bookings.size() > 0) {
                mTextMessage.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void addBookingToFirebase(final Booking booking, String id) {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        DatabaseReference newBookingRef = bookingsRef.push();
        DatabaseReference targetRef;
        if (newBooking) {
            newBookingRef.setValue(booking);
            targetRef = newBookingRef;
        } else {
            targetRef = bookingsRef.child(id);
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
