package com.example.letsmeal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.letsmeal.dummy.CalendarPair;
import com.example.letsmeal.dummy.TimeRecommendation;
import com.example.letsmeal.dummy.User;
import com.example.letsmeal.dummy.Schedule;
import com.example.letsmeal.support.PermissionRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;

public class CreateScheduleActivity extends AppCompatActivity {

    private final String TAG = "CreateScheduleActivity";

    Schedule schedule;
    Calendar dateCalendar, timeCalendar;

    EditText titleText;
    EditText dateText;
    EditText timeText;
    EditText placeText;
    EditText participantsText;
    EditText descriptionText;
    TextView personNotFoundLabel;
    Button findUidBtn;
    Button submitBtn;
    Button mapActivityBtn;
    Button timeRecommendBtn;

    Toolbar createScheduleToolBar;

    ArrayList<User> participants;

    FirebaseFirestore db;
    CollectionReference scheduleCollection;
    CollectionReference userCollection;

    Handler usersHandler;
    LinearLayout participantsLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        /* Get my User instance from MainActivity */
        final User organizer = (User)getIntent().getSerializableExtra("organizer");

        /* Initialize fields */

        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        placeText = findViewById(R.id.placeText);
        participantsText = findViewById(R.id.participantsText);
        descriptionText = findViewById(R.id.descriptionText);
        personNotFoundLabel = findViewById(R.id.personNotFoundLabel);
        findUidBtn = findViewById(R.id.findUidBtn);
        submitBtn = findViewById(R.id.submitBtn);
        mapActivityBtn = findViewById(R.id.mapActivityBtn);
        timeRecommendBtn = findViewById(R.id.timeRecommendBtn);

        createScheduleToolBar = findViewById(R.id.createScheduleToolBar);
        participantsLinearLayout = findViewById(R.id.participantsLinearLayout);


        this.schedule = new Schedule(organizer);
        this.dateCalendar = Calendar.getInstance();
        this.timeCalendar = Calendar.getInstance();

        this.usersHandler = new Handler();

        this.db = FirebaseFirestore.getInstance();
        this.scheduleCollection = db.collection(getString(R.string.firestore_schedule_collection));
        this.userCollection = db.collection(getString(R.string.firestore_user_collection));

        /* Initialize Toolbar */

        setSupportActionBar(this.createScheduleToolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Initialize participant ArrayList and nametags */

        this.participants = new ArrayList<>();
        participants.add(organizer);
        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.nametag, null);
        tv.setText(organizer.getName());
        participantsLinearLayout.addView(tv);


        /* set listeners */

        dateText.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDatePickerDialog().show();
            }
        });

        timeText.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTimePickerDialog().show();
            }
        });

        findUidBtn.setOnClickListener(findUidBtnOnClickListener);

        submitBtn.setOnClickListener(submitBtnOnclickListener);

        timeRecommendBtn.setOnClickListener(timeRecommendBtnOnClickListener);


        mapActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MapActivity.class));
            }
        });

    }



    /**
     * A helper function to initialize a DatePickerDialog using current date.
     * @return A DatePickerDialog initialized with current date
     */
    private DatePickerDialog currentDatePickerDialog() {
        Calendar cal = Calendar.getInstance();

        return new DatePickerDialog(
                this, onDateSetListener,
                cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DAY_OF_MONTH));
    }

    /**
     * A helper function to initialize a TimePickerDialog using current time.
     * TimePicker does NOT show 24-hour view.
     * @return A TimePickerDialog initialized with current time
     */
    private TimePickerDialog currentTimePickerDialog() {
        Calendar cal = Calendar.getInstance();
        return new TimePickerDialog(
                this, onTimeSetListener,
                cal.get(cal.HOUR_OF_DAY), cal.get(cal.MINUTE), false);
    }

    /**
     * This function modifies this.schedule's YEAR, MONTH, DAY_OF_MONTH fields.
     * Beware of month; it starts from 0, not 1.
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            dateCalendar.set(year, month, dayOfMonth);
            dateText.setText(Schedule.getDateString(dateCalendar));
        }
    };



    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            timeCalendar.set(0, 0, 0, hourOfDay, minute);
            timeText.setText(Schedule.getTimeString(timeCalendar));
        }
    };


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    private Button.OnClickListener findUidBtnOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            CreateScheduleActivity.this.personNotFoundLabel.setVisibility(View.INVISIBLE);
            final String name = participantsText.getText().toString();

            participantsText.setText("");

            //TODO: Check if the requested name's UID is already in local device due to a previous query.

            class FetchUidFromFirestore implements Runnable {
                public void run() {
                    final Query query = userCollection.whereEqualTo("name", name);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Got " + task.getResult().size() + " matching users");
                                // For now, we don't care about duplicate names.
                                if (task.getResult().size() == 0) {
                                    CreateScheduleActivity.this.personNotFoundLabel.setText(
                                            "사용자 " + name + "을 찾을 수 없습니다.");
                                    CreateScheduleActivity.this.personNotFoundLabel.setVisibility(View.VISIBLE);
                                }
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    // User document has UID as its document ID.
                                    final String uid = document.getId();
                                    final User user = new User(uid, name);
                                    if (!participants.contains(user)) {
                                        usersHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                participants.add(user);
                                                // TODO: Update UI using user name tags.
                                                TextView nametag =
                                                        (TextView) getLayoutInflater().inflate(R.layout.nametag, null);
                                                nametag.setText(name);
                                                participantsLinearLayout.addView(nametag);
                                                Log.d(TAG, "Added " + document.get("name") + ", uid = " + document.get("uid"));
                                            }
                                        });
                                    } else {
                                        CreateScheduleActivity.this.personNotFoundLabel.setText(
                                                "사용자 " + name + "는 이미 초대 되었습니다.");
                                        CreateScheduleActivity.this.personNotFoundLabel.setVisibility(View.VISIBLE);
                                        Log.d(TAG, "Duplicate uid for name " + document.get("name"));
                                    }
                                }
                            } else {
                                Log.d(TAG, "Failed to get initial schedules: " + task.getException());
                            }
                        }
                    });
                }
            };

            Thread fetchUidFromFirestore = new Thread(new FetchUidFromFirestore());
            fetchUidFromFirestore.start();

        }
    };

    /**
     * Listens to onClick event of submitBtn.
     * Wraps up all the data into a Schedule instance,
     * and returns it to MainActivity.
     */
    private Button.OnClickListener submitBtnOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            schedule.setTimestamp(
                    dateCalendar.get(dateCalendar.YEAR),
                    dateCalendar.get(dateCalendar.MONTH),
                    dateCalendar.get(dateCalendar.DAY_OF_MONTH),
                    timeCalendar.get(timeCalendar.HOUR_OF_DAY),
                    timeCalendar.get(timeCalendar.MINUTE));
            schedule.setDescription(descriptionText.getText().toString());

            schedule.setTitle(titleText.getText().toString());
            Log.d(TAG, Schedule.getDateString(dateCalendar));
            Log.d(TAG, Schedule.getTimeString(timeCalendar));
            Log.d(TAG, schedule.timestampToCalendar().toString());
            schedule.setPlace(placeText.getText().toString());

            schedule.setParticipants(participants);

            Intent resultSchedule = new Intent();
            resultSchedule.putExtra("schedule", schedule);
            setResult(RESULT_OK, resultSchedule);
            finish();
        }
    };

    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.TITLE, // required for debugging.
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
    };

    private static final int PROJECTION_TITLE_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_END_INDEX = 2;

    private Button.OnClickListener timeRecommendBtnOnClickListener = new Button.OnClickListener() {
       @Override
       public void onClick(View v) {

           Log.d(TAG, "requesting calendar perms...");
           PermissionRequest.with(CreateScheduleActivity.this).needCalendar().request();

       }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult");
        Cursor cur = null;
        ContentResolver cr = CreateScheduleActivity.this.getApplicationContext().getContentResolver();
        ContentValues cv = new ContentValues();

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.set(2019, 05, 21);
        end.set(2020, 01, 01);

        CalendarPair cp = new CalendarPair(start, end);


        String selction = "((dtstart >= " + cp.start.getTimeInMillis() + ") AND " +
                "(dtend <= " + cp.end.getTimeInMillis() + "))";


        ArrayList<CalendarPair> calendarPairs = new ArrayList<>();
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, cp.start.getTimeInMillis());
        ContentUris.appendId(builder, cp.end.getTimeInMillis());

        switch (requestCode) {
            case PermissionRequest.REQUEST_CALENDAR_PERM_CODE:
                try {
                    cur = cr.query(builder.build(),
                            INSTANCE_PROJECTION,
                            selction, null, null);

                    while (cur.moveToNext()) {
                        String title = cur.getString(PROJECTION_TITLE_INDEX);
                        long beginMillis = cur.getLong(PROJECTION_BEGIN_INDEX);
                        long endMillis = cur.getLong(PROJECTION_END_INDEX);

                        // TODO: datetime comparison code should be put here.

                        Calendar beginCal = Calendar.getInstance();
                        beginCal.setTimeInMillis(beginMillis);
                        Calendar endCal = Calendar.getInstance();
                        endCal.setTimeInMillis(endMillis);
                        calendarPairs.add(new CalendarPair(beginCal, endCal));

                        Log.d(TAG, "Schedule title: " + title);

                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                TimeRecommendation tr = new TimeRecommendation(calendarPairs);
                ArrayList<CalendarPair> recommendations = tr.recommendTime();

                break;
            default:
                Log.d(TAG, "Wrong request code for calendar permission!");

        }

    };




}
