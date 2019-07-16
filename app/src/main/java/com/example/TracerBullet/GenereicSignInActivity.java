package com.example.TracerBullet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.KakaoSDK;

public class GenereicSignInActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSignInActivity";


    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private static int KAKAO_SIGN_IN = 200;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d (TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // .requestIdToken(getString(R.string.default_web_client_id))
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        findViewById(R.id.com_kakao_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoSignIn();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void kakaoSignIn() {
        Log.d(TAG, "Kakao signin triggered!!!!");
        Intent kakaoSignInIntent = new Intent(this, KakaoLoginActivity.class);
        startActivityForResult(kakaoSignInIntent, KAKAO_SIGN_IN);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        finish();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            if (account == null) {
                Log.d(TAG, "No such user");
            } else {
                Log.d(TAG, "Logged in");
            }

        } catch (ApiException e) {
            Log.w(TAG, "signInResult: failed code = " + e.getStatusCode());

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        Log.d(TAG, "IdToken: " + account.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        Log.d(TAG, "credential: " + credential);
        Log.d(TAG, "mAuth: " + mAuth);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential: success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            Log.w(TAG, "signInWithCredential: failure", task.getException());

                        }
                    }
                });
    }



}
