package com.dsdairysystem.deliveryapp.add_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClientDetails extends AppCompatActivity {
    TextView name, mobile, address;
    Button confirm;
    FirebaseFirestore db;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        attachID();
        final String p = getIntent().getStringExtra("phone");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClientDetails.this,AddClient.class));
                finish();
            }
        });

        //fetching  client data from fireStore
        db.collection("Client").document(p).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String ad=documentSnapshot.getString("adl1")+","+documentSnapshot.getString("adl2")+" "+documentSnapshot.getString("adl3")+" "+documentSnapshot.getString("City");
                            Log.i("ADDRESS",ad);
                            name.setText(documentSnapshot.getString("Name"));
                            address.setText(ad);
                            mobile.setText(p);

                        } else {
                            Toast.makeText(ClientDetails.this, "No such client found", Toast.LENGTH_SHORT).show();
                            name.setText("No such client found");
                            mobile.setVisibility(View.INVISIBLE);
                            address.setVisibility(View.INVISIBLE);
                            confirm.setVisibility(View.INVISIBLE);
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ClientDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent group = new Intent(ClientDetails.this, Groups.class);
                group.putExtra("name", name.getText().toString());
                group.putExtra("mobile", p);
                group.putExtra("address", address.getText().toString());
                startActivity(group);
                finish();
            }
        });
    }
    private void attachID() {
        db = FirebaseFirestore.getInstance();
        name = findViewById(R.id.tv_name);
        mobile = findViewById(R.id.tv_phone);
        address = findViewById(R.id.tv_address);
        confirm = findViewById(R.id.confirm);
        cancel=findViewById(R.id.cancel);
    }
}
