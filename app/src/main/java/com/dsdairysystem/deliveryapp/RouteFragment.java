package com.dsdairysystem.deliveryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.query.QueryActivity;
import com.dsdairysystem.deliveryapp.route_tab.CustomerSocietyAdapter;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.dsdairysystem.deliveryapp.DashBoard.drawer;


/**
 * A simple {@link Fragment} subclass.
 */
public class RouteFragment extends Fragment {

    List<String> list;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> colonyList;
    private String user_id;
    private TextView noGroup, firstGrp;
    private CustomerSocietyAdapter customerSocietyAdapter;
    private ArrayList<String> allGroups= new ArrayList<>();
    private ArrayList<String> groups= new ArrayList<>();
    private int i=0;
    private Boolean intro;
    private SharedPreferences introPref;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(colonyList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

//            db.collection("Delivery").document(user_id).update("Group",colonyList).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.i("MOVE","Updated to the firestore");
//                }
//            });
//
//            for(int i = 0 ; i<colonyList.size();i++){
//                db.collection("Delivery").document(user_id).collection("Group").document(colonyList.get(i)).update("priority",i);
//            }
//
//            Log.i("MOVE", colonyList.toString());

            return false;

        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            db.collection("Delivery").document(user_id).update("Group", colonyList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("MOVE", "Updated to the firestore");
                }
            });

            for (int i = 0; i < colonyList.size(); i++) {
                db.collection("Delivery").document(user_id).collection("Group").document(colonyList.get(i)).update("priority", i);
            }

            Log.i("MOVE", colonyList.toString());

        }
    };

    public RouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        introPref = getActivity().getSharedPreferences("MAIN",MODE_PRIVATE);
        intro = introPref.getBoolean("route",true);

        list = new ArrayList<>();
        colonyList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.customer_society_rv);
        noGroup = view.findViewById(R.id.tv_no_added_group);
        firstGrp = view.findViewById(R.id.tv1);
        ImageButton btQuery = view.findViewById(R.id.btRouteNotify);
        btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QueryActivity.class);
                startActivity(intent);
            }
        });
        final CustomDrawerButton customDrawerButton = view.findViewById(R.id.custom_drawer2);
        customDrawerButton.setDrawerLayout( drawer );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });

        colonyList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customerSocietyAdapter = new CustomerSocietyAdapter(groups,getActivity());
        recyclerView.setAdapter(customerSocietyAdapter);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //user_id = "+919988776655";
        user_id = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        colonyList.clear();
        i=0;
        groups.clear();
        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                colonyList = (ArrayList) documentSnapshot.get("Group");
                i=0;
                if (colonyList == null || colonyList.isEmpty()){
                    noGroup.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    firstGrp.setText(colonyList.get(0));
                    groups.addAll(colonyList);
                    customerSocietyAdapter.notifyDataSetChanged();

                    allGroups = new ArrayList<>(colonyList);
                    noGroup.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    //Collections.reverse(colonyList);
                    for (int j=0;j<colonyList.size();j++) {
                        final int finalJ = j;
                        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Group").document(colonyList.get(j)).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()){
                                                ArrayList<Map> maps = (ArrayList<Map>) documentSnapshot.get("Client");
                                                if (maps == null ) {
                                                    deleteGroup(colonyList.get(finalJ));
                                                    groups.remove(colonyList.get(finalJ));
                                                    customerSocietyAdapter.notifyDataSetChanged();
                                                } else {
                                                    if (maps.size()==0) {
                                                        deleteGroup(colonyList.get(finalJ));
                                                        groups.remove(colonyList.get(finalJ));
                                                        customerSocietyAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });

                    }

                }
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }
        });

        final TapTargetSequence sequence =  new TapTargetSequence(getActivity())
                .targets(
//                        TapTarget.forToolbarMenuItem(toolbar,R.id.menuSearch,"Search Bar","Here you can search different things"),

                        TapTarget.forView(view.findViewById(R.id.list_item), getActivity().getResources().getString(R.string.group_intro), getActivity().getResources().getString(R.string.group_intro_desc))
                                .outerCircleColor(R.color.yellow)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.black)
                                .titleTextSize(30)
                                .titleTextColor(R.color.black)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.black)
                                .textColor(R.color.black)
                                .targetRadius(45)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true)

                )


                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                });

       if (intro) {
           sequence.start();
           getActivity().getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("route",false).apply();
       }



    }

    private void deleteGroup(final String g) {
        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Group").document(g).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Empty group "+g+ " deleted", Toast.LENGTH_SHORT).show();
                            allGroups.remove(g);
                            FirebaseFirestore.getInstance().collection("Delivery").document(user_id).update("Group",allGroups)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //  customerSocietyAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
