package com.dsdairysystem.deliveryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.add_client.AddClient;
import com.dsdairysystem.deliveryapp.order_placing.AddMilkTypeQuantityAdapter;
import com.dsdairysystem.deliveryapp.order_placing.ClientNumber;
import com.dsdairysystem.deliveryapp.order_placing.ClientAdapter;
import com.dsdairysystem.deliveryapp.order_placing.Index;
import com.dsdairysystem.deliveryapp.order_placing.MilkTypeLeftAdapter;
import com.dsdairysystem.deliveryapp.query.QueryActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import pl.pawelkleczkowski.customgauge.CustomGauge;

import static android.content.Context.MODE_PRIVATE;
import static com.dsdairysystem.deliveryapp.DashBoard.drawer;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView, milk_type_list;
    public static RecyclerView milk_type_left_list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;
    private LinearLayoutManager linearLayoutManager;
    private String saved_date;
    //private String user_id="+919988776655";
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ProgressDialog progressDialog;
    private int EMPTY_LiST=0;
    private static final String TAG = "MainActivity";
    private Boolean intro;
    private ArrayList<Map> client_array = new ArrayList<Map>();
    private ArrayList<String> product_list = new ArrayList<String>();
    private Collection<String> clients;
    private ArrayList<ClientNumber> clientArrayList = new ArrayList<ClientNumber>();
    private ArrayList<String> stringArrayList = new ArrayList<String>();
    private ArrayList<String> allClientsArrayList = new ArrayList<String>();
    private Map<String,Long>  product_detail = new HashMap<>();
    private   RecyclerView.Adapter myAdapter;
    public int index,check,INDEX;
    private TextView tvGroup,tvDone,tvTodayReq;
    public static TextView tvMilkLeft;
    private ImageView ivDone,ivStart;
    private Button btStart, btStartAgain;
    private SharedPreferences sharedPreferences, introPref;
    public static final String sharedPref = "sharedPref";
    public static final String KEY = "key";
    private Set<String> saved_list;
    public static CustomGauge customGauge;
    public ArrayList<Long> totalRequirementArraylist = new ArrayList<Long>();
    private long total_requirement = 0l,total_milk_left=0l;
    private AddMilkTypeQuantityAdapter addMilkTypeQuantityAdapter;
    public static MilkTypeLeftAdapter milkTypeLeftAdapter;
    private LinearLayout mLinearLayout, clientListLayout;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    String currentLanguage = "en", currentLang;
    private FloatingActionButton btAddClient;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mFirestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences =  getActivity().getSharedPreferences(sharedPref,MODE_PRIVATE);
        Date date = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String Date = df.format(date);

        final int i = sharedPreferences.getInt(KEY,-1);
        saved_list= sharedPreferences.getStringSet("ArrayList",null);
        check = sharedPreferences.getInt("check",0);
        saved_date = sharedPreferences.getString("Date",null);

        introPref = getActivity().getSharedPreferences("MAIN",MODE_PRIVATE);
        intro = introPref.getBoolean("home",true);

        index = getActivity().getIntent().getIntExtra("index",0);

        if (i!=-1&&index==0){
            index=i;
            Index.setINDEX(index);
        }

        if (saved_date != null && !saved_date.equals(Date)){
            check = 0;
            saved_list = null;
            index = 0;
        }

        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView = view.findViewById(R.id.rv_client_list);
        recyclerView.setLayoutManager(linearLayoutManager);

        tvGroup=view.findViewById(R.id.tvGroup);
        tvDone=view.findViewById(R.id.tvDone);
        ivDone=view.findViewById(R.id.ivDone);

        btStart=view.findViewById(R.id.btStart);
        btStartAgain=view.findViewById(R.id.btStartAgain);
        btAddClient = view.findViewById(R.id.btAddClient);
        ImageButton btQuery = view.findViewById(R.id.btHomeNotify);
        ivStart= view.findViewById(R.id.ivStart);

        customGauge = view.findViewById(R.id.gauge);
        mLinearLayout = view.findViewById(R.id.main_linear_layout);
        clientListLayout = view.findViewById(R.id.client_list_layout);
        tvMilkLeft = view.findViewById(R.id.tvMilkLeft);
        milk_type_left_list = view.findViewById(R.id.milk_type_left_list);
        milk_type_left_list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        tvDone.setVisibility(View.GONE);
        ivDone.setVisibility(View.GONE);
        btStartAgain.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);

        btAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasCamPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                if (hasCamPermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                            showMessageOKCancel("You need to allow access to Camera",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                        REQUEST_CODE_ASK_PERMISSIONS);
                                            }
                                        }
                                    });
                            return;
                        }
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                REQUEST_CODE_ASK_PERMISSIONS);
                    }
                    return;
                } else {
                    if (Index.getProduct_detail_map() != null && !Index.getProduct_detail_map().isEmpty()){
                        Intent intent = new Intent(getActivity(), AddClient.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(),"Add products first, then add clients",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),AddProductActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QueryActivity.class);
                startActivity(intent);
            }
        });

        final CustomDrawerButton customDrawerButton = view.findViewById(R.id.custom_drawer);
        customDrawerButton.setDrawerLayout( drawer );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });

        final TapTargetSequence sequence =  new TapTargetSequence(getActivity())
                .targets(
//                        TapTarget.forToolbarMenuItem(toolbar,R.id.menuSearch,"Search Bar","Here you can search different things"),

                       // TapTarget.forToolbarOverflow(toolbar,"Menu Option","more options available here") ,
                        TapTarget.forView(view.findViewById(R.id.btAddClient), getActivity().getResources().getString(R.string.add_client), getActivity().getResources().getString(R.string.add_client_desc))
                                .outerCircleColor(R.color.violet)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(40)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true) ,

                        TapTarget.forView(view.findViewById(R.id.btHomeNotify), getActivity().getResources().getString(R.string.notification), getActivity().getResources().getString(R.string.notification_desc))
                                .outerCircleColor(R.color.orange)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(40)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true) ,

                        TapTarget.forView(view.findViewById(R.id.custom_drawer), getActivity().getResources().getString(R.string.menu), getActivity().getResources().getString(R.string.menu_desc))
                                .outerCircleColor(R.color.red)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(40)
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
            getActivity().getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("home",false).apply();
        }

        return view;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

        rootRef.collection("Delivery").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ArrayList<Map> mapArrayList = (ArrayList<Map>) document.get("Products");
                    if (mapArrayList != null) {
                        for (Map<String, Object> map : mapArrayList) {
                            product_detail.put((String) map.get("Quality"), (Long) map.get("Price"));
                            product_list.add((String) map.get("Quality"));
                        }
                        Index.setProduct_detail_map(product_detail);
                        Index.setProduct_list(product_list);
                    }
                }
            }
        });

        rootRef.collection("Delivery").document(user_id).collection("Milk Available").document("Today Total Milk").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()){
                                Index.setTotalMilkTypeAvailability((Map<String, Long>) doc.get("Milk Type Available"));
                                Index.setTodayTotalMilkAvailable(doc.getLong("Total Milk Available"));
                                customGauge.setEndValue(Integer.parseInt(Long.toString(doc.getLong("Total Milk Available"))));
                            }
                        }
                    }
                });

        rootRef.collection("Delivery").document(user_id).collection("Group")
                .orderBy("priority")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        if ((index > snapshotList.size()-1)){
                            tvDone.setVisibility(View.VISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvGroup.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.INVISIBLE);
                            mLinearLayout.setVisibility(View.VISIBLE);
                            int k =0;
                            if (snapshotList != null && !snapshotList.isEmpty()){
                                for (DocumentSnapshot documentSnapshot : snapshotList){
                                    if (documentSnapshot.get("Client") != null){
                                        k++;
                                    }
                                }
                                if (k>0){
                                    setMilkAvailabilityPart(snapshotList,index,"No");
                                    btStartAgain.setVisibility(View.VISIBLE);
                                    btStartAgain.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Date c = Calendar.getInstance().getTime();
                                            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                            String formattedDate = df.format(c);

                                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(sharedPref, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt("check", 0);
                                            editor.putInt("key", 0);
                                            editor.putString("Date",formattedDate);
                                            editor.commit();

                                            Intent intent = new Intent(v.getContext(), DashBoard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("index", 0);
                                            Index.setINDEX(0);
                                            v.getContext().startActivity(intent);

                                        }
                                    });
                                } else {
                                    if (Index.getProduct_detail_map() != null && !Index.getProduct_detail_map().isEmpty()){
                                        Toast.makeText(getActivity(),"Add all clients before start today's delivery",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(getActivity(),DeliveryProfileActivity.class);
                                        startActivity(intent);
                                        //Toast.makeText(getActivity(),"Add products in Profile Tab first to start delivery",Toast.LENGTH_SHORT).show();

                                    }}
                            } else {
                                if (Index.getProduct_detail_map() != null && !Index.getProduct_detail_map().isEmpty()){
                                    Toast.makeText(getActivity(),"Add all clients before start today's delivery",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(),"Add products in Profile Tab first to start delivery",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }else {
                            mLinearLayout.setVisibility(View.VISIBLE);
                            DocumentSnapshot documentSnapshot = snapshotList.get(index);
                            tvGroup.setText(documentSnapshot.getId());

                            if (saved_list != null && check == 1){
                                clientArrayList.clear();
                                tvGroup.setVisibility(View.VISIBLE);
                                setMilkAvailabilityPart(snapshotList,index,"Yes");

                                for (String s:saved_list){
                                    clientArrayList.add(new ClientNumber(s));
                                    stringArrayList.add(s);
                                }
                                myAdapter = new ClientAdapter(clientArrayList,stringArrayList,getActivity());
                                recyclerView.getRecycledViewPool().clear();
                                myAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(myAdapter);

                            }
                            else if (index==0){
                                for (DocumentSnapshot document : snapshotList){
                                    if (document.get("Client") != null){
                                        for (Map<String,String> map: (ArrayList<Map>)document.get("Client")){
                                            allClientsArrayList.addAll(map.values());
                                        }
                                    }
                                }
                                //getRequirement(allClientsArrayList);

                                tvDone.setVisibility(View.GONE);
                                ivDone.setVisibility(View.GONE);
                                tvGroup.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.INVISIBLE);
                                btStart.setVisibility(View.VISIBLE);
                                ivStart.setVisibility(View.VISIBLE);
                                //mLinearLayout.setVisibility(View.GONE);
                                clientListLayout.setVisibility(View.GONE);

                                btStart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {

                                        btStart.setVisibility(View.GONE);
                                        ivStart.setVisibility(View.GONE);
                                        tvGroup.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        mLinearLayout.setVisibility(View.VISIBLE);
                                        clientListLayout.setVisibility(View.VISIBLE);
                                        setMilkAvailabilityPart(snapshotList, index,"Yes");


                                    }
                                });

                            }
                            else {
                                tvGroup.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                setMilkAvailabilityPart(snapshotList, index,"Yes");
                                Log.d("INDEX", "INDEX" + index);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    private void setMilkAvailabilityPart(final List<DocumentSnapshot> snapshotList, final int index, final String ok) {

        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Milk Available").document("Current Availability").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            Date c = Calendar.getInstance().getTime();
                            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c);

                            if (doc.exists() && doc.getString("Date").equals(formattedDate)){
                                Index.setCurrentMilkTypeAvailability((Map<String, Long>) doc.get("Milk Type Available"));
                                milkTypeLeftAdapter = new MilkTypeLeftAdapter((Map<String, Long>) doc.get("Milk Type Available"));
                                milk_type_left_list.setAdapter(milkTypeLeftAdapter);
                                customGauge.setValue(Integer.parseInt(Long.toString(doc.getLong("Total Milk Available"))));
                                Index.setCurrentTotalMilkAvailable(doc.getLong("Total Milk Available"));
                                customGauge.setEndValue(Integer.parseInt(Long.toString(Index.getTodayTotalMilkAvailable())));
                                tvMilkLeft.setText(Long.toString(doc.getLong("Total Milk Available")));
                                if ( (index <= snapshotList.size()-1) && (saved_list==null || check==0) && ok.equals("Yes")) { getPriorityClients(snapshotList,index);}
                            }else{
                                if (Index.getProduct_detail_map() != null && !Index.getProduct_detail_map().isEmpty()){
                                    openDialog(snapshotList,index);
                                } else {
                                    Toast.makeText(getActivity(),"Add products in Profile Tab first to start delivery",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                });

    }

    private void getPriorityClients(final List<DocumentSnapshot> snapshotList, final int index) {

        DocumentSnapshot documentSnapshot = snapshotList.get(index);
        tvGroup.setText(documentSnapshot.getId());

        if (Long.toString(Index.getCurrentTotalMilkAvailable()) != null) {
            tvMilkLeft.setText(Long.toString(Index.getCurrentTotalMilkAvailable()));
        }

        client_array= (ArrayList<Map>) documentSnapshot.get("Client");

        if (client_array !=null && client_array.size()>0){
            for (int i=0;i<client_array.size();i++){
                Map<String,String> map = (Map<String,String>) client_array.get(i);
                clients =  map.values();
                for(String s:clients){
                    clientArrayList.add(new ClientNumber(s));
                    stringArrayList.add(s);
                }
            }

            Index.setArrayList(stringArrayList);

            Log.d(TAG,"Size "+clientArrayList.size());

            myAdapter = new ClientAdapter(clientArrayList,stringArrayList,getActivity());
            recyclerView.getRecycledViewPool().clear();
            myAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(myAdapter);


        }else{
            INDEX = Index.getINDEX();
            Date c = Calendar.getInstance().getTime();
            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);

            Toast.makeText(getActivity(),documentSnapshot.getId()+" has no added Clients",Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(sharedPref, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("check", 0);
            editor.putInt("key", index + 1);
            editor.putString("Date",formattedDate);
            editor.commit();

            Intent intent = new Intent(getActivity(), DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("index", INDEX + 1);
            Index.setINDEX(INDEX + 1);
            startActivity(intent);
        }


    }

    private void openDialog(final List<DocumentSnapshot> snapshotList, final int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view =  LayoutInflater.from(getActivity()).inflate(R.layout.add_milk_quantity_dialog, null);
        builder.setView(view)
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent refresh = new Intent(getActivity(), DashBoard.class);
                        refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(refresh);
                    }
                })
                .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ArrayList<Long> enteredQuantities = addMilkTypeQuantityAdapter.getEnteredQuantities();
                        ArrayList<String> milk_types = new ArrayList<>(Index.getProduct_detail_map().keySet());
                        if (enteredQuantities==null){
                            Toast.makeText(getActivity(),getResources().getString(R.string.enter_all_quantities_to_proceed),Toast.LENGTH_SHORT).show();
                        }
                        else {
                            for (long l : enteredQuantities){
                                total_milk_left += l;
                            }

                            final Map<String,Long> milkMap = new HashMap<>();

                            for (int j=0;j<milk_types.size();j++){
                                if (enteredQuantities.get(j) != 0l){
                                    milkMap.put(milk_types.get(j),enteredQuantities.get(j));
                                }
                            }

                            Date c = Calendar.getInstance().getTime();
                            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c);

                            final Map<String,Object> docData = new HashMap<>();
                            docData.put("Total Milk Available",total_milk_left);
                            docData.put("Milk Type Available",milkMap);
                            docData.put("Date",formattedDate);

                            FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Milk Available").document("Today Total Milk").set(docData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Milk Available").document("Current Availability").set(docData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){
                                                                    Index.setTodayTotalMilkAvailable(total_milk_left);
                                                                    Index.setCurrentTotalMilkAvailable(total_milk_left);
                                                                    Index.setTotalMilkTypeAvailability(milkMap);
                                                                    Index.setCurrentMilkTypeAvailability(milkMap);
                                                                    customGauge.setEndValue(Integer.parseInt(Long.toString(total_milk_left)));
                                                                    customGauge.setValue(Integer.parseInt(Long.toString(total_milk_left)));
                                                                    milkTypeLeftAdapter = new MilkTypeLeftAdapter(milkMap);
                                                                    milk_type_left_list.setAdapter(milkTypeLeftAdapter);
                                                                    tvMilkLeft.setText(Long.toString(total_milk_left));
                                                                    getPriorityClients(snapshotList,index);
                                                                }

                                                            }
                                                        });

                                            }

                                        }
                                    });

                        }
                    }
                });
        milk_type_list = view.findViewById(R.id.milk_type_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        milk_type_list.setLayoutManager(mLinearLayoutManager);
        ArrayList<String> stringArrayList1;
        if (Index.getProduct_detail_map() != null){
             stringArrayList1 = new ArrayList<>(Index.getProduct_detail_map().keySet());
        }else stringArrayList1 = new ArrayList<>();
        addMilkTypeQuantityAdapter = new AddMilkTypeQuantityAdapter(stringArrayList1);
        milk_type_list.setAdapter(addMilkTypeQuantityAdapter);
        tvTodayReq = view.findViewById(R.id.tvTodayRequirement);
        if (Index.getTotalQuantityRequire() == null){
            //getRequirement(allClientsArrayList);
        }
        tvTodayReq.setText(Long.toString(total_requirement));
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


}
