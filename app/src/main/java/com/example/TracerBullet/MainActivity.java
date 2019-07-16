package com.example.TracerBullet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaSession2;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.*;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DocSnippets";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent signInActityIntent;
        signInActityIntent = new Intent(this, GenereicSignInActivity.class);
        startActivity(signInActityIntent);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference partyEvents = db.collection("partyEvents");



        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });


        Button submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "업로드 완료", Toast.LENGTH_SHORT).show();
                String title = ((EditText)findViewById(R.id.titleBox)).getText().toString();
                String date = ((EditText)findViewById(R.id.dateBox)).getText().toString();
                String time = ((EditText)findViewById(R.id.timeBox)).getText().toString();
                String phoneNum = ((EditText)findViewById(R.id.phoneNumBox)).getText().toString();
                String email = ((EditText)findViewById(R.id.emailBox)).getText().toString();
                Map<String, Object> peMap = createPartyEventMap(title, date, time,
                        phoneNum, email);


                partyEvents.document(title).set(peMap);
                Log.d(TAG, "Document added with title: " + title);


            }

        });

        Button searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String title = ((EditText)findViewById(R.id.titleBox)).getText().toString();
                DocumentReference peDocRef = db.collection("partyEvents").document(title);
                peDocRef.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot docSnap = task.getResult();
                                    if (docSnap.exists()) {
                                        // Fill in the textboxes.
                                        Map<String, Object> foundPartyEvent = docSnap.getData();

                                        EditText dateBox = (EditText)findViewById(R.id.dateBox);
                                        dateBox.setText((String)foundPartyEvent.get("date"));

                                        EditText timeBox = (EditText)findViewById(R.id.timeBox);
                                        timeBox.setText((String)foundPartyEvent.get("time"));

                                        EditText emailBox = (EditText)findViewById(R.id.emailBox);
                                        emailBox.setText((String)foundPartyEvent.get("email"));

                                        EditText phoneNumBox = (EditText)findViewById(R.id.phoneNumBox);
                                        phoneNumBox.setText((String)foundPartyEvent.get("phoneNum"));

                                    } else {
                                        Log.d(TAG, "document [" + title + "] doesn't exist.");
                                    }
                                } else {
                                    Log.d(TAG, "get() failed with", task.getException());
                                }
                            }
                        });
            }
        });
    }



    private Map<String, Object> createPartyEventMap(String title, String date, String time,
                                                    String phoneNum, String email) {
        Map<String, Object> peMap = new HashMap<>();
        peMap.put("title", title);
        peMap.put("date", date);
        peMap.put("time", time);
        peMap.put("phoneNum", phoneNum);
        peMap.put("email", email);

        return peMap;
    }





}
