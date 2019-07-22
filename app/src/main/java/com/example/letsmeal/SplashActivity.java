package com.example.letsmeal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.letsmeal.dummy.Schedule;
import com.example.letsmeal.dummy.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends Activity {

    private final String TAG = "SplashActivity";
    private final int RC_SIGN_IN = 100;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_splash);
        this.pref = getSharedPreferences("pref", MODE_PRIVATE);
        this.editor = pref.edit();


        boolean signInRequired = pref.getBoolean("signInRequired", true);
        if (signInRequired) {
            Log.d(TAG, "First access, launch signin activity");
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            Log.d(TAG, "Not first access, launch MainActivity");
            final int SPLASH_TIME_OUT = 2000;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String uid = pref.getString("uid", null);
                    if (uid == null) {
                        Log.d(TAG, "uid stored in SharedPreference is null");
                    }
                    String name = pref.getString("name", null);
                    if (name == null) {
                        Log.d(TAG, "name stored in SharedPreference is null");
                    }
                    launchMainActivity(new User(uid, name));
                }
            }, SPLASH_TIME_OUT);

        }
    }

    private void launchMainActivity(User me) {
        Intent MainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
        MainActivityIntent.putExtra("me", me);
        startActivity(new Intent(MainActivityIntent));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d(TAG, "Log in Success");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                editor.putBoolean("signInRequired", false);
                final User me = new User(user.getUid(), user.getDisplayName());

                editor.putString("uid", me.getUid());
                editor.putString("name", me.getName());
                editor.commit();

                FirebaseFirestore db =
                        FirebaseFirestore.getInstance();
                final DocumentReference docRef =
                        db.collection(getString(R.string.firestore_user_collection)).document(me.getUid());
                docRef.get().
                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "I already exist in User DB");
                                        launchMainActivity(me);
                                    } else {
                                        docRef.set(me)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "User DB insertion success");
                                                        launchMainActivity(me);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "User DB insertion failed with: ", e);
                                                    }
                                                });
                                    }

                                } else {
                                    Log.d(TAG, "User DB txn failed with" + task.getException());
                                }
                            }
                        });

            } else {
                // User canceled the sign-in flow using the back button.
                if (response == null) {
                    Log.d(TAG, "User canceled sign in flow with cancel button");
                    finish();
                } else {
                    int errorCode = response.getError().getErrorCode();
                    String errorMessage = response.getError().getMessage();
                    Log.d(TAG, "sign-in flow aborted with error code" + errorCode);
                    Log.d(TAG, errorMessage);
                }
            }
        }

    }

    private List<Schedule> getInitialScheduleList() {
        List<Schedule> schedules = new ArrayList<>();
        return schedules;
    }

}
