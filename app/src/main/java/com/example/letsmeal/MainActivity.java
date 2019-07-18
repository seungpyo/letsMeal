package com.example.letsmeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.letsmeal.dummy.Schedule;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    private final String TAG = "MainActivity";

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    TextView noScheduleLabel;

    /**
     * A FireBase UID to identify user.
     */
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true); // 뭔 소리지?
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.activity_main);

        noScheduleLabel = findViewById(R.id.noScheduleLabel);

        uid = getIntent().getStringExtra("uid");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.firestore_root_collection_name));


        final FloatingActionButton addItemFab = findViewById(R.id.addItemFab);
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createScheduleIntent = new Intent(MainActivity.this, CreateScheduleActivity.class);
                // Schedule schedule = new Schedule(MainActivity.this.getUid());
                // createScheduleIntent.putExtra("schedule", schedule);
                createScheduleIntent.putExtra("organizerUid", MainActivity.this.getUid());
                startActivityForResult(createScheduleIntent, 100);
            }
        });


        final FloatingActionButton logOutFab = findViewById(R.id.logOutFab);
        logOutFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });

                SharedPreferences.Editor editor =
                        getSharedPreferences("pref", MODE_PRIVATE).edit();
                editor.putBoolean("signInRequired", true);
                editor.commit();
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                finish();
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

            noScheduleLabel.setVisibility(View.INVISIBLE);

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
     * @deprecated getMyId() was a temporary feature. Please use getUid instead.
     */
    private long getMyId() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * @return A String UID which can be used to identify a user in FireBase services
     */
    public String getUid() {
        return this.uid;
    }
}
