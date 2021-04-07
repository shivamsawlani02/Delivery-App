package com.dsdairysystem.deliveryapp.add_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewClient extends AppCompatActivity {

    private EditText etName, etEmail, etAddress1, etCity, etAddress2, etAddress3;
    private String name, email, phone, address1, city, address2, address3;
    private Button btSave;
    private ProgressDialog progressDialog;
    private ImageView btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_client);

        etName = findViewById(R.id.editTextName);
        etAddress1 = findViewById(R.id.editTextAddress);
        etAddress2 = findViewById(R.id.editTextAddress2);
        etAddress3 = findViewById(R.id.editTextAddress3);
        etCity = findViewById(R.id.editTextCity);
        etEmail = findViewById(R.id.editTextEmail);
        btSave = findViewById(R.id.btSaveClient);
        btBack = findViewById(R.id.btBack3);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        phone = getIntent().getStringExtra("phone");

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                address1 = etAddress1.getText().toString();
                address2 = etAddress2.getText().toString();
                address3 = etAddress3.getText().toString();
                city = etCity.getText().toString();

                if(name.isEmpty() || address1.isEmpty() || city.isEmpty()){
                    Toast.makeText(AddNewClient.this,getResources().getString(R.string.please_fill_all_the_details),Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(AddNewClient.this);
                    progressDialog.setTitle(getResources().getString(R.string.adding_new_clients));
                    progressDialog.setMessage(getResources().getString(R.string.updating_please_wait));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    if (address2.isEmpty()) address2="";
                    if (address3.isEmpty()) address3="";

                    Map<String,Object> docData = new HashMap<>();
                    docData.put("Name",name);
                    docData.put("Mobile",phone);
                    docData.put("adl1",address1);
                    docData.put("adl2",address2);
                    docData.put("adl3",address3);
                    docData.put("City",city);
                    if (!email.isEmpty()) docData.put("Email",email);

                    FirebaseFirestore.getInstance().collection("Client").document(phone).set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(AddNewClient.this,ClientDetails.class);
                                intent.putExtra("phone",phone);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                progressDialog.dismiss();
                                startActivity(intent);
                            } else progressDialog.dismiss();
                        }
                    });


                }

            }
        });

    }
}