package com.dsdairysystem.deliveryapp.query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.R;
import com.dsdairysystem.deliveryapp.model.RequestNotificaton;
import com.dsdairysystem.deliveryapp.model.SendNotificationModel;
import com.dsdairysystem.deliveryapp.send_notification.APIService;
import com.dsdairysystem.deliveryapp.send_notification.Client;
import com.dsdairysystem.deliveryapp.send_notification.Data;
import com.dsdairysystem.deliveryapp.send_notification.MyResponse;
import com.dsdairysystem.deliveryapp.send_notification.NotificationSender;
import com.dsdairysystem.deliveryapp.send_notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class EditOrder extends AppCompatActivity {
    static TextInputEditText amount;
    TextInputEditText date,time;
    String orderID,clientMobile,documentID;
    FirebaseFirestore db;
    //String milkmanMobile="+919988776655";
    String milkmanMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    ArrayList<QueryProduct> arrayList;
    QueryProductAdapter adapter;
    RecyclerView recyclerView;
    Button update;
    ImageView btBack;
    ImageButton addProduct;
    Dialog productDialog;
    EditText quantity;
    Spinner qualitySpinner;
    Button add,cancel;
    String qual,quan;
    Long amt;
    int position;
    private APIService apiService;

    ArrayAdapter<String> qualityAdapter;
    ArrayAdapter<Long> amountAdapter;
    String device_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        attachId();

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditOrder.this, QueryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        db.collection("Client").document(clientMobile).collection("DeliveryPerson").document(milkmanMobile).collection("Orders")
                .document(orderID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Date timestamp=documentSnapshot.getTimestamp("timestamp").toDate();
                date.setText(getResources().getString(R.string.date)+": "+DateFormat.getDateInstance().format(timestamp));
                time.setText(getResources().getString(R.string.time)+": "+DateFormat.getTimeInstance().format(timestamp));
                amount.setText(documentSnapshot.getLong("Amount")+"");
                amt=documentSnapshot.getLong("Amount");
                Map<String,Object> map= (Map<String, Object>) documentSnapshot.get("Milk");
                for (Map.Entry<String,Object> entry : map.entrySet())
                {
                    QueryProduct product=new QueryProduct(entry.getKey(),entry.getValue().toString()+"");
                    arrayList.add(product);

                }
                adapter=new QueryProductAdapter(arrayList,getApplicationContext());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                addProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        productDialog.setContentView(R.layout.dailog_add_product);
                        productDialog.show();
                        quantity=productDialog.findViewById(R.id.dialog_quantity);
                        qualitySpinner=productDialog.findViewById(R.id.spinner);
                        add=productDialog.findViewById(R.id.add);
                        cancel=productDialog.findViewById(R.id.cancel);
                        db.collection("Delivery").document(milkmanMobile).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        ArrayList<Map<String,Object>> productList= (ArrayList<Map<String, Object>>) documentSnapshot.get("Products");
                                        qualityAdapter.clear();
                                        for(int i=0;i<productList.size();i++)
                                        {
                                            qualityAdapter.add((String) productList.get(i).get("Quality"));
                                            amountAdapter.add((Long) productList.get(i).get("Price"));
                                        }
                                        qualitySpinner.setAdapter(qualityAdapter);
                                        qualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                qual=adapterView.getSelectedItem()+"";
                                                position=adapterView.getSelectedItemPosition();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                        add.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                quan=quantity.getText().toString();
                                                if(quan.isEmpty())
                                                {
                                                    Toast.makeText(EditOrder.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    int flag=-1;
                                                    for(int i=0;i<arrayList.size();i++)
                                                    {
                                                        if(qual.equalsIgnoreCase(arrayList.get(i).getQuality()))
                                                        {
                                                            flag=i;
                                                            break;
                                                        }
                                                    }
                                                    if(flag!=-1)
                                                    {
                                                        QueryProduct product=new QueryProduct(arrayList.get(flag).getQuality(),
                                                                ""+(Long.parseLong(arrayList.get(flag).getQuantity())+Long.parseLong(quan)));
                                                        arrayList.set(flag,product);
                                                    }
                                                    else
                                                    {
                                                        QueryProduct product = new QueryProduct(qual, quan);
                                                        arrayList.add(product);
                                                    }
                                                    amt=amt+amountAdapter.getItem(position)*Long.parseLong(quan);
                                                    amount.setText(amt+"");
                                                    adapter.notifyDataSetChanged();
                                                    productDialog.dismiss();

                                                }
                                            }
                                        });
                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                productDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                    }
                });

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        updateOrder(arrayList,Long.parseLong(amount.getText().toString()));
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditOrder.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    void attachId()
    {
        date=findViewById(R.id.tv_date);
        time=findViewById(R.id.tv_time);
        amount=findViewById(R.id.tv_amount);
        update=findViewById(R.id.update);
        btBack=findViewById(R.id.btBack);
        addProduct=findViewById(R.id.add_product);
        clientMobile=getIntent().getStringExtra("clientMobile");
        orderID=getIntent().getStringExtra("orderID");
        documentID = getIntent().getStringExtra("documentID");
        db=FirebaseFirestore.getInstance();
        arrayList=new ArrayList<>();
        recyclerView=findViewById(R.id.rv_product);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productDialog=new Dialog(this);
        qualityAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item);
        amountAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item);
    }
    private void updateOrder(ArrayList<QueryProduct> arrayList, final Long amount)
    {
        final Map<String,Object> milk = new HashMap<>();
        for (int i=0;i<arrayList.size();i++)
        {
            milk.put(arrayList.get(i).getQuality(),Long.parseLong(arrayList.get(i).getQuantity()));
        }
        final DocumentReference order=db.collection("Client").document(clientMobile).collection("DeliveryPerson").document(milkmanMobile).collection("Orders")
                .document(orderID);
        order.update("Milk",milk).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    order.update("Amount",amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                db.collection("Delivery").document(milkmanMobile).collection("Notification").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditOrder.this, "Order has been updated", Toast.LENGTH_SHORT).show();
                                        sendNotifications(clientMobile,"Query Resolved","Your query has been resolved");
                                        Intent intent=new Intent(EditOrder.this, QueryActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });;
    }


    public void sendNotifications(final String clientMobile, final String title, final String message) {

        FirebaseFirestore.getInstance().collection("Client").document(clientMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                device_token=documentSnapshot.getString("Token id");
                Token token= new Token(device_token);
                if (device_token != null) {

                    Data data = new Data(title, message);
                    apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                    NotificationSender sender = new NotificationSender(data,device_token);
                    apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            if (response.code() == 200) {
                                int ds = response.body().success;
                                //Log.d(TAG,"-- "+ds);
                                if (response.body().success != 1) {
                                    Toast.makeText(EditOrder.this, "Failed ", Toast.LENGTH_LONG);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                        }
                    });
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditOrder.this, "Unable to fetch token: "+e, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
