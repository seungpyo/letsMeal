package com.example.letsmeal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.letsmeal.dummy.Schedule;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                        Log.d(TAG, "uid stored in SharedPreference is ull");
                    }

                    Intent MainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                    MainActivityIntent.putExtra("uid", uid);
                    startActivity(new Intent(MainActivityIntent));
                    finish();
                }
            }, SPLASH_TIME_OUT);

        }
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
                editor.putString("uid", user.getUid());
                editor.commit();

                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                Log.d(TAG, "UID put as " + user.getUid());
                mainActivityIntent.putExtra("uid", user.getUid());
                startActivity(mainActivityIntent);
                finish();
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
