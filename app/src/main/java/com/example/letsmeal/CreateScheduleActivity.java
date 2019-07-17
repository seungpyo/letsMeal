package com.example.letsmeal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

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
    Button submitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        this.schedule = (Schedule) getIntent().getSerializableExtra("schedule");
        this.dateCalendar = Calendar.getInstance();
        this.timeCalendar = Calendar.getInstance();

        this.titleText = findViewById(R.id.titleText);
        this.dateText = findViewById(R.id.dateText);
        this.timeText = findViewById(R.id.timeText);
        this.placeText = findViewById(R.id.placeText);
        this.participantsText = findViewById(R.id.participantsText);
        this.descriptionText = findViewById(R.id.descriptionText);
        this.personNotFoundLabel = findViewById(R.id.personNotFoundLabel);
        this.submitBtn = findViewById(R.id.submitBtn);

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

        participantsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                return;
            }
        });


        submitBtn.setOnClickListener(submitBtnOnclickListener);

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

    /**
     * Listens to onKey event of participantsText.
     * Detects space bar / enter input, and tokenize the latest name input.
     */

    /**
     * This function listens to space or enter key event.
     * If those keys are pressed, it gets the latest name.
     * The name is given as a query to Person DB, thus validating the given username.
     */
    public boolean getLastInputPersonName(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER)) {
            String [] names = participantsText.getText().toString().split("\\s+");
            String newName = names[names.length - 1];
            Person foundPerson = findPersonByName(newName);
            // No such person found
            if (foundPerson == null) {
                personNotFoundLabel.setText(newName + getString(R.string.no_such_person_name));
                personNotFoundLabel.setVisibility(View.VISIBLE);
                Log.d(TAG, "NO Such person");
            }
            Log.d(TAG, "onKey detected name - " + newName);
            // Does this undermine performance?
            return true;
        } else {
            Log.d(TAG, "Trivial Key Input Detected!");
            personNotFoundLabel.setVisibility(View.INVISIBLE);
            return false;
        }
    }



    /**
     * Finds a Person from database by name
     * Currently, this function just returns a trivial Person object.
     * TODO: Send a query to FireBase CloudStore and retrieve a Person object.
     * @param name The name of the person to find
     * @return A Person instance whose name matches the name. Returns null if no such Person exists.
     */
    private Person findPersonByName(String name) {
        if (name.equals("ㅗ")) {
            return null;
        }
        return new Person(name);
    }



    /**
     * Listens to onClick event of submitBtn.
     * Wraps up all the data into a Schedule instance,
     * and returns it to MainActivity.
     */
    private Button.OnClickListener submitBtnOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            schedule.setCalendar(
                    dateCalendar.get(dateCalendar.YEAR),
                    dateCalendar.get(dateCalendar.MONTH),
                    dateCalendar.get(dateCalendar.DAY_OF_MONTH),
                    timeCalendar.get(timeCalendar.HOUR_OF_DAY),
                    timeCalendar.get(timeCalendar.MINUTE));
            schedule.setDescription(descriptionText.getText().toString());

            schedule.setTitle(titleText.getText().toString());
            Log.d(TAG, Schedule.getDateString(dateCalendar));
            Log.d(TAG, Schedule.getTimeString(timeCalendar));
            Log.d(TAG, schedule.getCalendar().toString());
            schedule.setLatitude(0.0);
            schedule.setLongitude(0.0);
            schedule.setPlace(placeText.getText().toString());
            schedule.setParticipantsAsString(participantsText.getText().toString());

            Intent resultSchedule = new Intent();
            resultSchedule.putExtra("schedule", schedule);
            setResult(RESULT_OK, resultSchedule);
            finish();
        }
    };

}
