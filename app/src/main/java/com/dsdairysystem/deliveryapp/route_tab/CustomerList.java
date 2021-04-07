package com.dsdairysystem.deliveryapp.route_tab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dsdairysystem.deliveryapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomerList extends AppCompatActivity {
    List<Map<String, String>> clientList;
    String user_id, groupname;
    private FirebaseFirestore db;
    private TextView noClient;
    private ActionBar toolbar;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(clientList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);


            Log.i("MOVE", clientList.toString());


            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            db.collection("Delivery").document(user_id)
                    .collection("Group")
                    .document(getIntent().getStringExtra("groupname"))
                    .update("Client", clientList)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("Client change on db", "Successfull");
                        }
                    });

        }
    };
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        db = FirebaseFirestore.getInstance();
        //user_id = "+919988776655";
        user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        clientList=new ArrayList<>();
        noClient=findViewById(R.id.tv_no_client);
        groupname = getIntent().getStringExtra("groupname");

        toolbar = getSupportActionBar();
        toolbar.setTitle(getResources().getString(R.string.customer_list));

//        Log.i("SS", getIntent().getStringExtra("groupname"));


        db.collection("Delivery")
                .document(user_id)
                .collection("Group")
                .document(groupname)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        clientList = (List<Map<String, String>>) documentSnapshot.get("Client");
                        recyclerView = findViewById(R.id.rv_customerlist);
                        if(clientList==null){
                            clientList=new ArrayList<>();
                            recyclerView.setVisibility(View.GONE);
                            noClient.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noClient.setVisibility(View.GONE);
                        }

//                        Log.i("CLIENTLIST", clientList.toString());
                        List<String> clientnames = new ArrayList<>();
                        List<String> clientnumbers = new ArrayList<>();
                        for (int i = 0; i < clientList.size(); i++) {
                            clientnames.add(i, clientList.get(i).keySet().toArray()[0].toString());
                            clientnumbers.add(i,clientList.get(i).get(clientnames.get(i)));
                        }

                        Log.i("NAME", clientnames.toString());
                        Log.i("NUMBER", clientnumbers.toString());

                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(new CustomerListAdapter(clientnames,clientnumbers,CustomerList.this,getIntent().getStringExtra("groupname")));
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);

                    }
                });

    }
}
