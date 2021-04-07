package com.dsdairysystem.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class DeliveryDetailedOrder extends AppCompatActivity {
    TextInputEditText date,time,amount,quantity,clientName,clientMobile;
    String ID;
    FirebaseFirestore db;
    //String milkmanMobile="+919988776655";
    String milkmanMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_detailed_order);
        attachID();
        toolbar = getSupportActionBar();
        toolbar.setTitle(getResources().getString(R.string.order_details));
        toolbar.setDisplayHomeAsUpEnabled(true);
        db.collection("Delivery").document(milkmanMobile).collection("Orders").document(ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> map;
                        clientName.setText(documentSnapshot.getString("name"));
                        String Date= DateFormat.getDateInstance().format(documentSnapshot.getTimestamp("timestamp").toDate());
                        date.setText(Date);
                        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
                        String Time =localDateFormat.format(documentSnapshot.getTimestamp("timestamp").toDate());
                        time.setText(getResources().getString(R.string.time)+": "+Time);
                        amount.setText(getResources().getString(R.string.amount)+": "+documentSnapshot.getDouble("Amount")+" Rupees");
                        map= (Map<String, Object>) documentSnapshot.get("Milk");
                        quantity.setText("");
                        for(Map.Entry<String,Object> entry : map.entrySet())
                        {
                            quantity.append(entry.getKey()+" : "+entry.getValue());
                            quantity.append("\n");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DeliveryDetailedOrder.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private  void attachID()
    {
        date=findViewById(R.id.tv_date);
        time=findViewById(R.id.tv_time);
        amount=findViewById(R.id.tv_amount);
        quantity=findViewById(R.id.tv_quantity);
        clientName=findViewById(R.id.tv_client_name);
        clientMobile=findViewById(R.id.tv_client_mobile);
        ID=getIntent().getStringExtra("ID");
        db= FirebaseFirestore.getInstance();
    }
}