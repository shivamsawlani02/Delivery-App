package com.dsdairysystem.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    ImageView logo;
    TextView text1;
    EditText name;
    EditText email;
    Button btn_nxt;
    String newToken;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
       // getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_register);

        //   logo = findViewById(R.id.logo);
        text1 = findViewById(R.id.register_text);
        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        btn_nxt = findViewById(R.id.nxt_btn);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Register.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ph = getIntent();
                String usermane = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String phnumber = ph.getStringExtra("phonenumber");


                final String token = newToken;

                if (usermane.isEmpty()) {
                    name.setError("name cannot be empty");
                }  else {

                    if (Email.isEmpty()) {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = db.collection("Delivery").document(phnumber);


                        Map<String, String> profileDoc = new HashMap<>();
                        profileDoc.put("Name",usermane);
                        profileDoc.put("Mobile Number",phnumber);
                        profileDoc.put("Token id",token);

                        ProfileDetails profile_deatils = new ProfileDetails(usermane, Email, phnumber, token);
                        documentReference.set(profileDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(Register.this, DashBoard.class);

                                Pair[] pairs = new Pair[2];
                                pairs[0] = new Pair<View, String>(text1, "trans2");
                                pairs[1] = new Pair<View, String>(btn_nxt, "trans4");

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register.this, pairs);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent, options.toBundle());


                                } else {
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                    } else {


                        // LINKING EMAIL WITH PHONE

                        firebaseAuth.getCurrentUser().updateEmail(Email);


                        FirebaseFirestore db = FirebaseFirestore.getInstance();


                        DocumentReference documentReference = db.collection("Delivery").document(phnumber);
                        Map<String, String> profileDoc = new HashMap<>();
                        profileDoc.put("Name",usermane);
                        profileDoc.put("Mobile Number",phnumber);
                        profileDoc.put("Email", Email);
                        profileDoc.put("Token id",token);

                        ProfileDetails profile_deatils = new ProfileDetails(usermane, Email, phnumber, token);
                        documentReference.set(profileDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(Register.this, DashBoard.class);

                                Pair[] pairs = new Pair[2];
                                pairs[0] = new Pair<View, String>(text1, "trans2");
                                pairs[1] = new Pair<View, String>(btn_nxt, "trans4");


                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register.this, pairs);
                                    startActivity(intent, options.toBundle());


                                } else {
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        });

                    }


                }
            }

        });

    }
}
