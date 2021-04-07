package com.dsdairysystem.deliveryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.query.QueryActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.dsdairysystem.deliveryapp.DashBoard.drawer;


/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment {

    int i=0;

    int m=0;

    BarChart barChart;
    LineChart lineChart;
    MaterialSpinner spinner;
    MaterialSpinner spinner2;
    String month="June";
    String month1="June";
    String phone;
    int l, val;
    String mt;

    Float finalam=0f;
    Float finalam1=0f;
    Float finalam2=0f;

    Float finalcr=0f;
    Float finalor=0f;
    Float finalst=0f;

    final String[] monthly = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    int[] rainbow;


    Map<String,Float> totalPriceProduct = new HashMap<>();

    ArrayList<ArrayList> entriesList = new ArrayList<>();
    Map<String,ArrayList<Entry>> entriesLineMap = new HashMap<>();
    Map<String,ArrayList<BarEntry>> entriesMap = new HashMap<>();
    ArrayList<String> priceProductNames = new ArrayList<>();

    ArrayList<Entry> entriesLine = new ArrayList<Entry>();
    ArrayList<Entry> entriesLine1 = new ArrayList<Entry>();
    ArrayList<Entry> entriesLine2 = new ArrayList<Entry>();

    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
    ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<String> numMap = new ArrayList<>();
    RecyclerView recyclerView;
    DeliveryOrderAdapter adapter;
    ArrayList<DeliveryOrder> arrayList;
    FirebaseFirestore db;
    int currentItems, totalItems, scrolledOutItems;
    private DocumentSnapshot lastVisible;
    //String milkmanMobile = "+919988776655";
    String milkmanMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private boolean isLastItemReached = false;
    boolean isScrolling = false;
    ProgressBar pgBar;
    TextView noOrder;
    int c = 0;
    private Boolean intro;
    private SharedPreferences introPref;

    List<Map<String, Object>> milktype;


    public SalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        lineChart=view.findViewById(R.id.linchart);
        spinner=view.findViewById(R.id.spinner);
        spinner2=view.findViewById(R.id.spinner2);
        noOrder = view.findViewById(R.id.tv_no_recent_order);
        phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        rainbow = getActivity().getResources().getIntArray(R.array.rainbow);
        //phone="+919988776655";

        barChart=view.findViewById(R.id.barchart);

        Calendar calendar=Calendar.getInstance();
        val=calendar.get(Calendar.MONTH);

        month=monthly[val];
        month1=monthly[val];

        ImageButton btQuery = view.findViewById(R.id.btSaleNotify);
        btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QueryActivity.class);
                startActivity(intent);
            }
        });

        spinner2.setItems("JANUARY","FEBRUARY","MARCH","April","MAY","June","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER","YEAR");

        spinner.setItems("JANUARY","FEBRUARY","MARCH","April","MAY","June","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER","YEAR");

        recyclerView = view.findViewById(R.id.recycler_view_orders);
        pgBar = view.findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        introPref = getActivity().getSharedPreferences("MAIN",MODE_PRIVATE);
        intro = introPref.getBoolean("sales",true);
        arrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchOrdersAndpagination();
//        db.collection("Delivery").document(milkmanMobile).collection("Orders").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful())
//                        {
//                            int c=0;
//                            for (QueryDocumentSnapshot document : task.getResult())
//                            {
//                                Log.i("DOCUMENT ID",document.getId());
//                                Map<String,Object> map;
//                                map= (Map<String, Object>) document.get("Milk");
//                                String date= DateFormat.getDateInstance().format(document.getTimestamp("timestamp").toDate());
//                                DeliveryOrder order = new DeliveryOrder(document.getId(),date,map);
//                                arrayList.add(c,order);
//                                c++;
//
//                                for(Map.Entry<String,Object> entry : map.entrySet())
//                                {
//                                    Log.i("MAP VALUE",entry.getKey()+":"+entry.getValue());
//                                }
//
//                            }
//                            Log.i("ARRAY SIZE",String.valueOf(arrayList.size()));
//                            adapter = new DeliveryOrderAdapter(arrayList,getApplicationContext());
//                            recyclerView.setAdapter(adapter);
//                        }
//                        else
//                        {
//                            Toast.makeText(DeliveryOrderList.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        final CustomDrawerButton customDrawerButton = view.findViewById(R.id.custom_drawer3);
        customDrawerButton.setDrawerLayout( drawer );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {


            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item=="YEAR"){
                    entries2.clear();
                    entries1.clear();
                    entries.clear();
                    entriesMap.clear();
                    labels.clear();
                    barChart.clear();

                    yeargraph();
                }else {
                    i = 0;
                    entries2.clear();
                    entries1.clear();
                    entries.clear();
                    labels.clear();
                    barChart.clear();
                    entriesMap.clear();
                    month = item;

                    Toast.makeText(getActivity(), "the item is" + month, Toast.LENGTH_LONG).show();
                    graph2();
                }
            }
        });


        spinner2.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(item=="YEAR") {

                    entriesLine.clear();
                    numMap.clear();
                    entriesLineMap.clear();

                    lineChart.clear();

                    yeargraph2();
                }else {
                    i = 0;
                    entriesLine.clear();
                    numMap.clear();
                    entriesLineMap.clear();
                    lineChart.clear();

                    month1 = item;

                    Toast.makeText(getActivity(), "the item is" + month1, Toast.LENGTH_LONG).show();
                    graph();
                }


            }
        });
        spinner.setSelectedIndex(val);
        spinner2.setSelectedIndex(val);

        graph();
        graph2();

        final TapTargetSequence sequence =  new TapTargetSequence(getActivity())
                .targets(
//                        TapTarget.forToolbarMenuItem(toolbar,R.id.menuSearch,"Search Bar","Here you can search different things"),

                        TapTarget.forView(view.findViewById(R.id.stats2), getActivity().getResources().getString(R.string.stats_intro), getActivity().getResources().getString(R.string.stats_intro_desc))
                                .outerCircleColor(R.color.spring_green)
                                .outerCircleAlpha(0.75f)
                                .targetCircleColor(R.color.black)
                                .titleTextSize(30)
                                .titleTextColor(R.color.black)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.black)
                                .textColor(R.color.black)
                                .targetRadius(60)
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
            getActivity().getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("sales",false).apply();
        }

    }

    protected  void graph(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Delivery").document(phone).collection("Stats").document("2020").collection(month1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        milktype = (List<Map<String, Object>>) documentSnapshot.get("Products");
                        Map<String,Float> pricePro = new HashMap<>();
                        Float b = 0f;
                        for (Map<String,Object> mapProduct : milktype) {
                            String price = mapProduct.get("Price").toString();
                            String proName = mapProduct.get("Quality").toString();
                            priceProductNames.add(proName);
                            Float f = Float.parseFloat(price);
                            b += f;
                            pricePro.put(mapProduct.get("Quality").toString(),f);

                            if (entriesLineMap.keySet().contains(proName)) {
                                ArrayList<Entry> storeEntriesLine = new ArrayList<>();
                                storeEntriesLine.addAll(entriesLineMap.get(proName));
                                storeEntriesLine.add(new Entry(i,f));
                                entriesLineMap.replace(proName,storeEntriesLine);
                            } else {
                                ArrayList<Entry> storeEntriesLine = new ArrayList<>();
                                storeEntriesLine.add(new Entry(i,f));
                                entriesLineMap.put(proName,storeEntriesLine);
                            }
                        }

                       /* String cream= milktype.get(0).get("Price").toString();
                        String organi= milktype.get(1).get("Price").toString();
                        String singletoned= milktype.get(2).get("Price").toString();


                        Float cr=Float.parseFloat(cream);
                        Float or=Float.parseFloat(organi);
                        Float st=Float.parseFloat(singletoned); */
                        String k=documentSnapshot.getId();
                        numMap.add(i,"DAY"+k);


                       /* entriesLine.add(new Entry(i,cr));
                        entriesLine1.add(new Entry(i,or));
                        entriesLine2.add(new Entry(i,st)); */


                        i++;
                        Log.d("tag", "getting" +  documentSnapshot.getData() + documentSnapshot.get("Amount")+documentSnapshot.getId());
                    }

                    setgraph2(entriesLineMap);


                }else{
                    Log.d("graph","no data ");
                }
            }

        });

    }

    public void graph2(){

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Delivery").document(phone).collection("Stats").document("2020").collection(month).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        milktype = (List<Map<String, Object>>) documentSnapshot.get("Products");
                        Map<String,Float> volPro = new HashMap<>();
                        Float b = 0f;
                        for (Map<String,Object> mapProduct : milktype) {
                            String price = mapProduct.get("Volume").toString();
                            String proName = mapProduct.get("Quality").toString();
                            priceProductNames.add(proName);
                            Float f = Float.parseFloat(price);
                            b += f;
                            volPro.put(mapProduct.get("Quality").toString(),f);

                            if (entriesMap.keySet().contains(proName)) {
                                ArrayList<BarEntry> storeEntriesLine = new ArrayList<>();
                                storeEntriesLine.addAll(entriesMap.get(proName));
                                storeEntriesLine.add(new BarEntry(i,f));
                                entriesMap.replace(proName,storeEntriesLine);
                            } else {
                                ArrayList<BarEntry> storeEntriesLine = new ArrayList<>();
                                storeEntriesLine.add(new BarEntry(i,f));
                                entriesMap.put(proName,storeEntriesLine);
                            }
                        }
                        String k=documentSnapshot.getId();
                        labels.add("DAY "+k);

                       /* //  String amount=milktype.get(0).get("Price").toString();
                        String cream= milktype.get(1).get("Volume").toString();
                        String organi= milktype.get(2).get("Volume").toString();
                        String singletoned= milktype.get(0).get("Volume").toString();
                        //   Float b=Float.parseFloat(amount);
                        Float cr=Float.parseFloat(cream);
                        Float or=Float.parseFloat(organi);
                        Float st=Float.parseFloat(singletoned);
                        entries.add(new BarEntry(i,cr));
                        entries1.add(new BarEntry(i,or));
                        entries2.add(new BarEntry(i,st)); */
                        i++;
                        Log.d("tag", "getting" +  documentSnapshot.getData() + documentSnapshot.get("Amount")+documentSnapshot.getId());
                    }

                    setgraph(entriesMap);


                }else{
                    Log.d("graph","no data ");
                }
            }

        });

    }

    protected void setgraph(Map<String,ArrayList<BarEntry>> entriesMap){

        int color = 0;

        BarData data1 = new BarData();
        data1.setBarWidth(0.15f);

        if(entriesMap != null && !entriesMap.isEmpty()) {

            for (String prNAME : entriesMap.keySet()) {
                BarDataSet bardataset = new BarDataSet(entriesMap.get(prNAME), prNAME);
                bardataset.setColor(rainbow[color]);
                data1.addDataSet(bardataset);
                color++;
            }
            XAxis barasix = barChart.getXAxis();


            barasix.setPosition(XAxis.XAxisPosition.BOTTOM);
            barasix.setGranularity(1f);
            barasix.setDrawLabels(true);
            barasix.setDrawGridLines(false);
            barasix.setDrawAxisLine(true);

            barChart.getAxisRight().setEnabled(false);

            barChart.getAxisLeft().setGranularity(1f);

            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.gradient);

            float barSpace = 0.02f;
            float groupSpace = 0.3f;

            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            //   barChart.getXAxis().setLabelCount(entries.size());

            barChart.getXAxis().setAxisMinimum(0);


            if (!entriesMap.isEmpty() && data1.getDataSets().size()>1) {
                barChart.setData(data1);
                barChart.invalidate();
                barChart.groupBars(0, groupSpace, barSpace);
                barChart.animateY(1000);
            } else if (!entriesMap.isEmpty() && data1.getDataSets().size()<=1) {
                BarDataSet bardataset = new BarDataSet(new ArrayList<BarEntry>(),"");
                bardataset.setColor(rainbow[color]);
                data1.addDataSet(bardataset);

                barChart.setData(data1);
                barChart.invalidate();
                barChart.groupBars(0, groupSpace, barSpace);
                barChart.animateY(1000);
       //         throw new RuntimeException("BarData needs to hold at least 2 BarDataSets to allow grouping.");
            }

        }

    }


    public void setgraph2(Map<String,ArrayList<Entry>> entriesLineMap){
        LineData data = new LineData();
        int color = 0;
        if(entriesLineMap != null && !entriesLineMap.isEmpty()) {

            for (String prNAME : entriesLineMap.keySet()) {
                LineDataSet lineDataSet = new LineDataSet(entriesLineMap.get(prNAME), prNAME);

                lineDataSet.setCircleRadius(5f);
                lineDataSet.setCircleHoleRadius(2.5f);
                lineDataSet.setCircleColor(Color.WHITE);




                XAxis xAxis = lineChart.getXAxis();


                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);



                IAxisValueFormatter formatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return numMap.get((int) value);
                    }

                    public int getDecimalDigits() {
                        return 0;
                    }

                };
                //xAxis.setValueFormatter(formatter);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(numMap));

                xAxis.setGranularity(1f);
                xAxis.setLabelCount(entriesLineMap.get(prNAME).size());

                YAxis yAxisRight = lineChart.getAxisRight();
                yAxisRight.setEnabled(false);

                YAxis yAxisLeft = lineChart.getAxisLeft();
                yAxisLeft.setGranularity(1f);

                lineDataSet.setColor(rainbow[color]);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.gradient);

                lineDataSet.setFillDrawable(drawable);
                lineDataSet.setDrawFilled(true);

                data.addDataSet(lineDataSet);
                color++;
            }

           /* LineDataSet lineDataSet = new LineDataSet(entriesLine, "Cream");
            LineDataSet lineDataSet1 = new LineDataSet(entriesLine1, "Organic");
            LineDataSet lineDataSet2 = new LineDataSet(entriesLine2, "Single Toned");


            lineDataSet.setCircleRadius(5f);
            lineDataSet.setCircleHoleRadius(2.5f);
            lineDataSet.setCircleColor(Color.WHITE);
            //  lineDataSet.setHighLightColor(Color.WHITE);


            lineDataSet1.setCircleRadius(5f);
            lineDataSet1.setCircleHoleRadius(2.5f);
            lineDataSet1.setCircleColor(Color.WHITE);
            //     lineDataSet1.setHighLightColor(Color.WHITE);


            lineDataSet2.setCircleRadius(5f);
            lineDataSet2.setCircleHoleRadius(2.5f);
            lineDataSet2.setCircleColor(Color.WHITE);
            //  lineDataSet2.setHighLightColor(Color.WHITE); */



            lineChart.animateX(1000);



            if (!entriesLineMap.isEmpty()) {
                lineChart.setData(data);
                lineChart.invalidate();
            }

        }



    }

    protected void yeargraph() {
        m=0;
        totalPriceProduct.clear();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (i = 0; i< monthly.length-1; i++) {
            db.collection("Delivery").document(phone).collection("Stats").document("2020").collection(monthly[i]).whereEqualTo("Month",monthly[i]).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        QuerySnapshot document = task.getResult();
                        if (!document.isEmpty()) {

                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                if (documentSnapshot.exists()) {

                                    milktype = (List<Map<String, Object>>) documentSnapshot.get("Products");

                                    if (milktype!= null && !milktype.isEmpty()) {

                                        for (Map<String,Object> mapProduct : milktype) {

                                            if (mapProduct.get("Quality") != null ) {
                                                String price = mapProduct.get("Volume").toString();
                                                String prname = mapProduct.get("Quality").toString();
                                                Float f = Float.parseFloat(price);

                                                if (totalPriceProduct.keySet().contains(prname)) {
                                                    Float l = totalPriceProduct.get(mapProduct.get("Quality"));
                                                    totalPriceProduct.remove(prname);
                                                    l += f;
                                                    totalPriceProduct.put(prname,l);
                                                } else {
                                                    totalPriceProduct.put(prname,f);
                                                }
                                            }

                                        }
                                        mt=documentSnapshot.get("Month").toString();
                                    }
                                }

                                //  String amount=milktype.get(0).get("Price").toString();
                                //   Float b=Float.parseFloat(amount);


                                /*String amount = documentSnapshot.get("Amount").toString();
                                String cream = milktype.get(1).get("Volume").toString();
                                String organi =  milktype.get(2).get("Volume").toString();
                                String singletoned = milktype.get(0).get("Volume").toString();
                                finalam += Float.parseFloat(amount);
                                finalcr += Float.parseFloat(cream);
                                finalor += Float.parseFloat(organi);
                                finalst = Float.parseFloat(singletoned);

                                Log.d("tag", "getting" + documentSnapshot.getData() + finalam + finalst + mt); */

                            }

                            for (String s : totalPriceProduct.keySet()) {
                                ArrayList<BarEntry> entriesLine = new ArrayList<BarEntry>();
                                entriesLine.add(new BarEntry(m,totalPriceProduct.get(s)));
                                entriesMap.put(s,entriesLine);
                            }


                            /*entriesLine.add(new Entry(m, finalam));
                            entries.add(new BarEntry(m, finalcr));
                            entries1.add(new BarEntry(m, finalor));
                            numMap.add(m,mt ); */
                            labels.add(mt);


                            m++;

                            /*finalam=0f;
                            finalcr=0f;
                            finalor=0f;
                            finalst=0f; */

                        }
                        setgraph(entriesMap);

                    }
                }

            });


        }



    }

    public void yeargraph2(){
        m=0;
        totalPriceProduct.clear();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (i = 0; i< monthly.length-1; i++) {
            db.collection("Delivery").document(phone).collection("Stats").document("2020").collection(monthly[i]).whereEqualTo("Month",monthly[i]).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        QuerySnapshot document = task.getResult();
                        if (!document.isEmpty()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                milktype = (List<Map<String, Object>>) documentSnapshot.get("Products");

                                for (Map<String,Object> mapProduct : milktype) {

                                    if (mapProduct.get("Quality") != null) {
                                        String price = mapProduct.get("Price").toString();
                                        String prname = mapProduct.get("Quality").toString();
                                        Float f = Float.parseFloat(price);

                                        if (totalPriceProduct.containsKey(prname)) {
                                            Float l = totalPriceProduct.get(prname);
                                            l += f;
                                            totalPriceProduct.replace(prname,l);
                                        } else {
                                            totalPriceProduct.put(prname,f);
                                        }
                                    }
                                }
                                mt=documentSnapshot.get("Month").toString();


                                //     String amount = documentSnapshot.get("Amount").toString();
                                /*String cream = milktype.get(1).get("Price").toString();
                                String organi =  milktype.get(2).get("Price").toString();
                                String singletoned = milktype.get(0).get("Price").toString();
                                finalam += Float.parseFloat(cream);
                                finalam1 += Float.parseFloat(organi);
                                finalam2 += Float.parseFloat(singletoned);

                                Log.d("tag", "getting" + documentSnapshot.getData() + finalam + finalst + mt); */

                            }

                            entriesList.clear();
                            for (String s : totalPriceProduct.keySet()) {
                                ArrayList<Entry> entriesLine = new ArrayList<Entry>();
                                entriesLine.add(new Entry(m,totalPriceProduct.get(s)));
                                entriesLineMap.put(s,entriesLine);
                            }

                           /* entriesLine.add(new Entry(m, finalam));
                            entriesLine1.add(new Entry(m, finalam1));
                            entriesLine2.add(new Entry(m, finalam2)); */

                            numMap.add(m,mt );
                            m++;

                            finalam=0f;

                        }
                        setgraph2(entriesLineMap);

                    }
                }

            });


        }


    }

    void fetchOrdersAndpagination() {
        c = 0;
//        arrayList.clear();
        isLastItemReached = false;
        isScrolling = false;
        db.collection("Delivery").document(milkmanMobile).collection("Orders")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            c = 0;
                            if (task.getResult() == null || task.getResult().isEmpty()){
                                recyclerView.setVisibility(View.GONE);
                                noOrder.setVisibility(View.VISIBLE);
                            }else {
                                recyclerView.setVisibility(View.VISIBLE);
                                noOrder.setVisibility(View.GONE);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("DOCUMENT ID", document.getId());
                                Map<String, Object> map;
                                map = (Map<String, Object>) document.get("Milk");
                                String date = DateFormat.getDateInstance().format(document.getTimestamp("timestamp").toDate());
                                Log.i("DATE OF TIMESTAMP", date);
                                DeliveryOrder order = new DeliveryOrder(document.getId(), date, map);
                                arrayList.add(c, order);
                                c++;
                            }

                            Log.i("ARRAY SIZE", String.valueOf(arrayList.size()));
                            adapter = new DeliveryOrderAdapter(arrayList, getActivity());
                            if (arrayList.size() > 0) {
                                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                            db.collection("Delivery").document(milkmanMobile).collection("Orders")
                                                    .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(5).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    Log.i("DOCUMENT ID", document.getId());
                                                                    Map<String, Object> map;
                                                                    map = (Map<String, Object>) document.get("Milk");
                                                                    String date = DateFormat.getDateInstance().format(document.getTimestamp("timestamp").toDate());
                                                                    DeliveryOrder order = new DeliveryOrder(document.getId(), date, map);
                                                                    arrayList.add(c, order);
                                                                    c++;
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                                if (task.getResult().size() > 0) {
                                                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                }
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
                            recyclerView.setAdapter(adapter);
                        } else {
                            noOrder.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
