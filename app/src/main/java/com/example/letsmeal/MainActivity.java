package com.example.letsmeal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
        /**
         * RecyclerView setHasFixedSize 함수에 대한 설명
         *  @see https://developer.android.com/reference/android/support/v7/widget/RecyclerView#sethasfixedsize
         *  요약: 리사이클러 뷰는 내용물에 상관없이 그 크기를 고정시킬 수 있는 경우 최적화를 수행할 수 있음.
         *  만약, 리사이클러 뷰를 담고 있는 부모 뷰의 크기가 달라지는 경우 이에 맞춰서 리사이클러 뷰의 크기
         *  또한 다시 계산해야하기 때문에 오버헤드가 있는데 이러한 오버헤드가 불필요한 경우 쓰면 되는듯함.
         */
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
