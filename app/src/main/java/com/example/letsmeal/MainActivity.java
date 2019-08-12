package com.example.letsmeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.letsmeal.dummy.Schedule;
import com.example.letsmeal.dummy.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {


    private final String TAG = "MainActivity";
    private final int CREATE_SCHEDULE_REQ = 100;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    TextView noScheduleLabel;

    FirebaseFirestore db;
    CollectionReference scheduleCollection;
    CollectionReference userCollection;

    Handler initialScheduleHandler;
    ArrayList<Schedule> initialScheudles;

    /**
     * A FireBase UID to identify user.
     */
    private User me;

    // TODO: print user info (e.g. name, thumbnail, etc.) on DrawerLayout.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = findViewById(R.id.mainToolBar);
        setSupportActionBar(mainToolbar);
        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mainToolbar,
                R.string.open,
                R.string.close
        ));
        mNavView = findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);


        /**
         * Initialize RecyclerView
         *
         *
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.recyclerview);
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

        noScheduleLabel = findViewById(R.id.noScheduleLabel);

        /**
         * Get the user's UID from SplashActivity
         */
        me = (User)getIntent().getSerializableExtra("me");
        Log.d(TAG, "Received uid " + me.getUid() + " from Splash");
        /**
         * Initialize FireStore instance and CollectionReferences.
         */
        db = FirebaseFirestore.getInstance();
        scheduleCollection = db.collection(getString(R.string.firestore_schedule_collection));
        userCollection = db.collection(getString(R.string.firestore_user_collection));


        this.initialScheduleHandler = new Handler();
        Thread getInitialSchedules = new Thread(new GetInitialSchedules());
        getInitialSchedules.start();

        final FloatingActionButton addItemFab = findViewById(R.id.addItemFab);
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createScheduleIntent = new Intent(MainActivity.this, CreateScheduleActivity.class);
                Log.d(TAG, "organizer uid is " + MainActivity.this.getUid() + " just before creating schedule");
                User organizer = new User(MainActivity.this.getUid(), MainActivity.this.getUserName());
                createScheduleIntent.putExtra("organizer", organizer);
                startActivityForResult(createScheduleIntent, CREATE_SCHEDULE_REQ);
            }
        });


        Handler organizerNameHandler = new Handler();
        Thread fetchOrganizerName = new Thread(new Runnable() {
            @Override
            public void run() {
                DocumentReference docRef = userCollection.document(me.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                me.setName(document.get("name").toString());
                                Log.d(TAG, "Got organizer name: " + me.getName());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
        fetchOrganizerName.start();


    }

    private void logout() {
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
        editor.putString("uid", null);
        editor.commit();
        startActivity(new Intent(MainActivity.this, SplashActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_SCHEDULE_REQ && resultCode == RESULT_OK) {
            Schedule schedule = (Schedule)data.getExtras().getSerializable("schedule");
            final ItemCard newCard = new ItemCard(schedule);

            // Kind of WAL; DB transaction works like a logging of addItemCard().
            scheduleCollection.add(schedule).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Successfully wrote a schedule object at FireStore");
                        noScheduleLabel.setVisibility(View.INVISIBLE);
                        recyclerViewAdapter.addItemCard(newCard);
                        recyclerViewAdapter.setContext(getApplicationContext());
                        recyclerView.setAdapter(recyclerViewAdapter);

                    } else if (task.isCanceled()) {
                        Log.d(TAG, "Txn canceled while writing a schedule object at FireStore");
                    } else {
                        Log.d(TAG, "Error writing a schedule object at FireStore");
                    }
                }
            });


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id) {
            case R.id.action_logout:
                logout();
                return true;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * An internal class fetching initial schedules from FireStore.
     * TODO: Replacing this with AsyncTask may enable reporting using onProgressUpdate().
     *
     */
    private class GetInitialSchedules implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "Query: paticipantUids contains " + MainActivity.this.me.getUid());
            Query query = scheduleCollection.whereArrayContains("participantUids", MainActivity.this.me.getUid());

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "Got " + task.getResult().size() + " initial schedules");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final ItemCard newCard = new ItemCard((Schedule)document.toObject(Schedule.class));
                            initialScheduleHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    noScheduleLabel.setVisibility(View.INVISIBLE);
                                    recyclerViewAdapter.addItemCard(newCard);
                                    recyclerViewAdapter.setContext(getApplicationContext());
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Failed to get initial schedules: " + task.getException());
                    }
                }
            });
        }
    }


    /**
     * @return A String UID which can be used to identify a user in FireBase services
     */
    public String getUid() {
        return this.me.getUid();
    }

    public String getUserName() {
        return this.me.getName();
    }
}


