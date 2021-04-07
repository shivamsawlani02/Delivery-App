package com.dsdairysystem.deliveryapp.add_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.DashBoard;
import com.dsdairysystem.deliveryapp.MainActivity;
import com.dsdairysystem.deliveryapp.R;
import com.dsdairysystem.deliveryapp.route_tab.CustomerDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    FirebaseFirestore db;
    //String milkManMobile="+919988776655";
    String milkManMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    RecyclerView recyclerView;
    ArrayList<ProductModel> arrayListProducts;
    AddClientProductAdapter adapter;
    ImageView back;
    Button save;
    String milkManName;
    String clientName,clientMobile,clientAddress;
    String groupname;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        attachID();


        db.collection("Delivery").document(milkManMobile).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<Map<String,Object>> arrayList= (ArrayList<Map<String, Object>>)documentSnapshot.get("Products");
                        if (arrayList != null && !arrayList.isEmpty()){
                            for (int i=0;i<arrayList.size();i++) {
                            ProductModel productModel=new ProductModel((String) arrayList.get(i).get("Quality"),"0");
                            arrayListProducts.add(productModel);
                        }
                            adapter=new AddClientProductAdapter(arrayListProducts,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                        else {
                            Toast.makeText(ProductActivity.this,"No Added Product found, first add Products in Profile then add clients",Toast.LENGTH_LONG ).show();
                            Intent intent = new Intent(ProductActivity.this,DashBoard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }


                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String,Object> productMap =new HashMap<>();
                                for (int i=0;i<arrayListProducts.size();i++)
                                {
                                    if(!arrayListProducts.get(i).getQuantity().equals("0"))
                                    {
                                        productMap.put(arrayListProducts.get(i).getProductName(),Long.parseLong(arrayListProducts.get(i).getQuantity()));
                                    }
                                }
                                if(productMap.isEmpty())
                                {
                                    Toast.makeText(ProductActivity.this, getResources().getString(R.string.select_alleast_one_product), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    saveClient(productMap);
                                }
                            }
                        });

                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ProductActivity.this,Groups.class);
                startActivity(intent);
                finish();
            }
        });

    }

    void saveClient(final Map<String,Object> productMap) {
        //saving milkman details under client
        progressDialog = new ProgressDialog(ProductActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.saving_client));
        progressDialog.setMessage(getResources().getString(R.string.updating_please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        db.collection("Delivery").document(milkManMobile).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        milkManName=documentSnapshot.getString("Name");

                        //Milkman milkman = new Milkman(milkManName, milkManMobile,"DeliveryPerson");

                        Map<String,Object> docdata = new HashMap<>();
                        docdata.put("name",milkManName);
                        docdata.put("mobile",milkManMobile);
                        docdata.put("type","DeliveryPerson");
                        docdata.put("Due Amount",0l);
                        docdata.put("Usual Order",productMap);

                        db.collection("Client").document(clientMobile).collection("DeliveryPerson").document(milkManMobile).set(docdata)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProductActivity.this, "Milkman data saved under client", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
        //saving client details under milkman
        final Client client = new Client(clientName,clientMobile,clientAddress,productMap);
        db.collection("Delivery").document(milkManMobile).collection("Client").document(clientMobile).set(client)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProductActivity.this, "Client data saved under milkman", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //saving client under the group selected by milkman

        final ArrayList<Map> user = new ArrayList<>();
        final Map<String,Object> new_user = new HashMap<>();
        new_user.put(clientName,clientMobile);


        db.collection("Delivery").document(milkManMobile).collection("Group").document(groupname).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if(document.contains("Client"))
                            {
                                user.addAll((ArrayList<Map>) document.get("Client"));
                                user.add(new_user);
                                db.collection("Delivery").document(milkManMobile).collection("Group").document(groupname).update("Client", user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProductActivity.this, "Client saved under Group document as an array of map", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else
                            {
                                ArrayList<Map<String,Object>> arrayList=new ArrayList<>();
                                arrayList.add(new_user);
                                db.collection("Delivery").document(milkManMobile).collection("Group").document(groupname).update("Client",arrayList);
                            }
                        }
                        Intent home = new Intent(ProductActivity.this, DashBoard.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(home);
                        progressDialog.dismiss();
                        finish();
                    }
                });
    }
    void attachID() {
        recyclerView=findViewById(R.id.products_rv);
        back=findViewById(R.id.back_to_groups);
        save=findViewById(R.id.save);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db= FirebaseFirestore.getInstance();
        arrayListProducts=new ArrayList<>();
        groupname=getIntent().getStringExtra("groupName");

        clientName = getIntent().getStringExtra("name");
        clientMobile = getIntent().getStringExtra("mobile");
        clientAddress = getIntent().getStringExtra("address");
    }

}
