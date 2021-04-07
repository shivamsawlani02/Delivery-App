package com.dsdairysystem.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsdairysystem.deliveryapp.order_placing.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeliveryProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileContact, tvProfileConnectedClient,tvNoProducts , tvProfileEditProduct;
    private RecyclerView recyclerView;
    //private String user_id="+919988776655";
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ArrayList<Map> product_array = new ArrayList<Map>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter myAdapter;
    private Button fabAddProduct;
    ImageView btBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_profile);

        tvProfileConnectedClient=findViewById(R.id.tvProfileConnectedClient);
        tvProfileContact=findViewById(R.id.tvProfileContact);
        tvProfileName=findViewById(R.id.tvProfileName);
        tvNoProducts=findViewById(R.id.tvNoProducts);
        fabAddProduct=findViewById(R.id.fabAddProduct);
        recyclerView=findViewById(R.id.product_list);
        tvProfileEditProduct = findViewById(R.id.tvProfileEditProduct);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        btBack = findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryProfileActivity.this,DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryProfileActivity.this,AddProductActivity.class);
                startActivity(intent);
            }
        });

        tvProfileEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvProfileEditProduct.getText().equals(getResources().getString(R.string.edit))){
                    Index.setProductEditable(true);
                    tvProfileEditProduct.setText(getResources().getString(R.string.cancel));
                    myAdapter = new ProductAdapter(Index.getAvailableProducts());
                    myAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(myAdapter);
                } else {
                    Index.setProductEditable(false);
                    tvProfileEditProduct.setText(getResources().getString(R.string.edit));
                    myAdapter = new ProductAdapter(Index.getAvailableProducts());
                    myAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(myAdapter);
                }
            }
        });

        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Client").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                tvProfileConnectedClient.setText(Integer.toString(snapshotList.size()));
            }
        });

        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        tvProfileName.setText(document.getString("Name"));
                        tvProfileContact.setText(document.getString("Mobile Number"));

                        if (document.get("Products") != null) {
                            product_array = (ArrayList<Map>) document.get("Products");
                            recyclerView.setVisibility(View.VISIBLE);
                            Collections.reverse(product_array);
                            Index.setAvailableProducts(product_array);
                            Index.setProductEditable(false);
                            myAdapter = new ProductAdapter(product_array);
                            myAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(myAdapter);
                        }else {
                            recyclerView.setVisibility(View.GONE);
                            tvNoProducts.setVisibility(View.VISIBLE);
                        }

                    }
                }

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeliveryProfileActivity.this,DashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}