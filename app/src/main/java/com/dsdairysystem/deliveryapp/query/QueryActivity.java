package com.dsdairysystem.deliveryapp.query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.MainActivity;
import com.dsdairysystem.deliveryapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QueryActivity extends AppCompatActivity {

    RecyclerView queries;
    QueryAdapter adapter;
    ArrayList<Query> arrayList;
   // String milkmanMobile = "+919988776655";
    String milkmanMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
TextView noNtification;
    FirebaseFirestore db;
    ProgressBar pgBar;
    private DocumentSnapshot lastVisible;
    ImageView btBack;
    int c = 0;
    private boolean isLastItemReached = false;
    boolean isScrolling = false;
    int currentItems, totalItems, scrolledOutItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        queries=findViewById(R.id.queries);
        pgBar = findViewById(R.id.progress);
        btBack = findViewById(R.id.btBack);
        noNtification = findViewById(R.id.tv_no_notification);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        arrayList=new ArrayList<>();
        queries.setLayoutManager(new LinearLayoutManager(this));
        queries.setAdapter(adapter);
        db=FirebaseFirestore.getInstance();

        pagination();
    }

    void pagination() {
        c = 0;
        arrayList.clear();
        isLastItemReached = false;
        isScrolling=false;
        db.collection("Delivery").document(milkmanMobile).collection("Notification").limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() == null || task.getResult().isEmpty()){
                                queries.setVisibility(View.GONE);
                                noNtification.setVisibility(View.VISIBLE);
                            }else {
                                queries.setVisibility(View.VISIBLE);
                                noNtification.setVisibility(View.GONE);
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Query query=new Query(document.getString("date"),document.getString("clientName"),document.getString("description"),
                                        document.getString("clientMobile"),document.getString("orderID"),document.getId());
                                arrayList.add(c,query);
                                ++c;
                            }
                            adapter = new QueryAdapter(arrayList, getApplicationContext());
                            if (arrayList.size() > 0)
                            {
                                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                queries.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                            isScrolling = true;
                                        }
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                        currentItems = linearLayoutManager.getChildCount();
                                        totalItems = linearLayoutManager.getItemCount();
                                        scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                                        if (isScrolling && (scrolledOutItems + currentItems == totalItems) && (!isLastItemReached)) {
                                            Log.i("ISSCROLLED", "TRUE");
                                            pgBar.setVisibility(View.VISIBLE);
                                            isScrolling = false;
                                            db.collection("Delivery").document(milkmanMobile).collection("Notification").startAfter(lastVisible).limit(5).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    Query query=new Query(document.getString("date"),document.getString("clientName"),
                                                                            document.getString("description"),document.getString("clientMobile"),document.getString("orderID"),document.getId());
                                                                    arrayList.add(c,query);
                                                                    ++c;
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                                if(task.getResult().size()>0)
                                                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                pgBar.setVisibility(View.INVISIBLE);
                                                                if (task.getResult().size() < 5) {
                                                                    isLastItemReached = true;
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                            queries.setAdapter(adapter);
                        } else {
                            Toast.makeText(QueryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
