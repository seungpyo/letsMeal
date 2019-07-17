package com.example.letsmeal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.letsmeal.dummy.Schedule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true); // 뭔 소리지?
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.activity_main);

        final FloatingActionButton addItemFab = findViewById(R.id.addItemFab);
        addItemFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent createScheduleIntent = new Intent(MainActivity.this, CreateScheduleActivity.class);
                Schedule schedule = new Schedule(getMyId());
                createScheduleIntent.putExtra("schedule", schedule);
                startActivityForResult(createScheduleIntent, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // CreateScheduleActivity
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Schedule schedule = (Schedule)data.getExtras().getSerializable("schedule");
            ItemCard newCard = new ItemCard(schedule);
            Log.d(TAG, schedule.toString());

            recyclerViewAdapter.addItemCard(newCard);
            recyclerViewAdapter.setContext(getApplicationContext());
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    /**
     * Currently, we just use a timestamp as a temporary solution.
     * This can cause problem if multiple users call this function on the same time,
     * getting duplicate IDs.
     * @return user's Person.ID
     */
    private long getMyId() {
        return Calendar.getInstance().getTimeInMillis();
    }

}
