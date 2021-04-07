package com.dsdairysystem.deliveryapp.order_placing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.DashBoard;
import com.dsdairysystem.deliveryapp.R;
import com.dsdairysystem.deliveryapp.query.EditOrder;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.dsdairysystem.deliveryapp.HomeFragment.customGauge;
import static com.dsdairysystem.deliveryapp.HomeFragment.milkTypeLeftAdapter;
import static com.dsdairysystem.deliveryapp.HomeFragment.milk_type_left_list;
import static com.dsdairysystem.deliveryapp.HomeFragment.sharedPref;
import static com.dsdairysystem.deliveryapp.HomeFragment.tvMilkLeft;
import static com.dsdairysystem.deliveryapp.order_placing.ClientAdapter.extTypeViewHolder.amount;

public class ClientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int EXT_TYPE=0,INT_TYPE=1;
    private ArrayList<ClientNumber> list;
    private ArrayList<String> client_list,quality_list = new ArrayList<String>();
    private ArrayList<Long> quantity_list = new ArrayList<Long>();
    private ArrayList<Integer> amount_list = new ArrayList<Integer>();
    private String name,address,city,quality;
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private long quantity;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private static final String TAG = "ClientAdapter";
    private int index;
    private ProgressDialog progressDialog;
    private Context context;
    private  OrderAdapter myAdapter;
    private long sum_amount;
    private long order_amount=0l;
    private String device_token;
    private APIService apiService;

    private String[] month_name = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

    public ClientAdapter(ArrayList<ClientNumber> list, ArrayList<String>client_list, Context context) {
        this.list = list;
        this.client_list= client_list;
        this.context=context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == EXT_TYPE){view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_extended_layout, parent, false);
            return new ClientAdapter.extTypeViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_layout, parent, false);
            return new ClientAdapter.TypeViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {


        index = position;

        final String client_no = list.get(position).getClient_no();
        Index.setTotal_amount(0l);


        Log.d(TAG,"Position="+position+",Client no is "+client_no);

        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Client").document(client_no).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()){

                        name = document.getString("name");
                        address = document.getString("address");

                        Log.d(TAG,name+" "+address);

                        if (position == 0){


                            ((ClientAdapter.extTypeViewHolder) holder).name.setText(name);
                            ((ClientAdapter.extTypeViewHolder) holder).address.setText(address);
                            ((ClientAdapter.extTypeViewHolder) holder).contact_info.setText(client_no);


                            FirebaseFirestore.getInstance().collection("Client").document(client_no).collection("DeliveryPerson").document(user_id)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    long amt = 0L;
                                        Map<String, Long> milkMap = (Map<String, Long>) documentSnapshot.get("Usual Order");
                                        quality_list.clear();
                                        quantity_list.clear();
                                        amount_list.clear();
                                        Log.d("Err",milkMap.toString());
                                        if (milkMap!=null) {
                                            for (String c : milkMap.keySet()) {
                                                Log.d("Err",milkMap.get(c).toString());
                                                amt += ((milkMap.get(c)) * (Index.getProduct_detail_map().get(c)));
                                            }
                                            quality_list.addAll(milkMap.keySet());
                                            quantity_list.addAll(milkMap.values());
                                        }
                                    ((extTypeViewHolder) holder).amount.setText(Long.toString(amt));
                                    Index.setTotal_amount(amt);



                                    if ((quantity_list != null && quality_list != null) || (!quality_list.isEmpty() && !quality_list.isEmpty())) {

                                        Long milkAvailable;
                                        int size = quantity_list.size();
                                        String milkType;

                                        for (int j=0;j<size;j++){
                                            if (quantity_list.size()<size){
                                                milkType = quality_list.get(size-j-1);
                                                milkAvailable = Index.getCurrentMilkTypeAvailability().get(milkType);
                                                if (milkAvailable==null) {
                                                    if (Index.getTotal_amount() != null) {
                                                        Index.setTotal_amount(Index.getTotal_amount() - ((Index.getProduct_detail_map().get(milkType)) * quantity_list.get(size-j-1)));
                                                        amount.findViewById(R.id.tvAmount);
                                                        amount.setText(Index.getTotal_amount().toString());
                                                    }
                                                    quantity_list.remove(size-j-1);
                                                    quality_list.remove(size-j-1);
                                                }
                                                else if ((quantity_list.get(position) > milkAvailable)) {
                                                    if (Index.getTotal_amount() != null) {
                                                        Index.setTotal_amount(Index.getTotal_amount() - ((Index.getProduct_detail_map().get(milkType)) * quantity_list.get(size-j-1)));
                                                        amount.findViewById(R.id.tvAmount);
                                                        amount.setText(Index.getTotal_amount().toString());
                                                    }
                                                    quantity_list.remove(size-j-1);
                                                    quality_list.remove(size-j-1);
                                                }

                                            } else {
                                                milkType = quality_list.get(j);
                                                milkAvailable = (Long) Index.getCurrentMilkTypeAvailability().get(milkType);
                                                if (milkAvailable==null) {
                                                    if (Index.getTotal_amount() != null) {
                                                        Index.setTotal_amount(Index.getTotal_amount() - ((Index.getProduct_detail_map().get(milkType)) * quantity_list.get(j)));
                                                        amount.findViewById(R.id.tvAmount);
                                                        amount.setText(Index.getTotal_amount().toString());
                                                    }
                                                    quantity_list.remove(j);
                                                    quality_list.remove(j);
                                                   }
                                                else if ((quantity_list.get(position) > milkAvailable)) {
                                                    if (Index.getTotal_amount() != null) {
                                                        Index.setTotal_amount(Index.getTotal_amount() - ((Index.getProduct_detail_map().get(milkType)) * quantity_list.get(j)));
                                                        amount.findViewById(R.id.tvAmount);
                                                        amount.setText(Index.getTotal_amount().toString());
                                                    }
                                                    quantity_list.remove(j);
                                                    quality_list.remove(j);
                                                }
                                            }
                                            milkAvailable = 0l;
                                        }

                                        myAdapter = new OrderAdapter(quantity_list, quality_list,context);
                                        myAdapter.notifyDataSetChanged();
                                        ((ClientAdapter.extTypeViewHolder) holder).order_list.setAdapter(myAdapter);
                                    }else {
                                        Toast.makeText(context,context.getResources().getString(R.string.no_orders_found),Toast.LENGTH_SHORT).show();
                                    }


                                    ((ClientAdapter.extTypeViewHolder) holder).btAdd.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {

                                            quality_list.add("Add Product");
                                            quantity_list.add(0l);
                                            myAdapter.notifyItemInserted(quality_list.size()-1);

                                        }
                                    });

                                    ((ClientAdapter.extTypeViewHolder) holder).btCancelOrder.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            //Yes button clicked

                                                            client_list.remove(list.get(0).getClient_no());
                                                            list.remove(0);

                                                            Index.setArrayList(client_list);

                                                            progressDialog = new ProgressDialog(v.getContext());
                                                            progressDialog.setTitle(v.getContext().getResources().getString(R.string.order_canceled));
                                                            progressDialog.setMessage(v.getContext().getResources().getString(R.string.updating_please_wait));
                                                            progressDialog.setCanceledOnTouchOutside(false);
                                                            progressDialog.show();

                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                public void run() {
                                                                    notifyDataSetChanged();
                                                                    quality_list.clear();
                                                                    quantity_list.clear();
                                                                    Index.setTotal_amount(0l);
                                                                    amount.setText(Index.getTotal_amount().toString());

                                                                    if (getItemCount() == 0) {
                                                                        index = Index.getINDEX();

                                                                        Date c = Calendar.getInstance().getTime();
                                                                        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                                                        String formattedDate = df.format(c);

                                                                        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref, MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                        editor.putInt("check", 0);
                                                                        editor.putInt("key", index + 1);
                                                                        editor.putString("Date",formattedDate);
                                                                        editor.commit();

                                                                        Intent intent = new Intent(v.getContext(), DashBoard.class);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        intent.putExtra("index", index + 1);
                                                                        Index.setINDEX(index + 1);
                                                                        v.getContext().startActivity(intent);
                                                                        progressDialog.dismiss();

                                                                    }else {
                                                                        progressDialog.dismiss();
                                                                    }

                                                                }
                                                            }, 2000);


                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            //No button clicked
                                                            break;
                                                    }
                                                }
                                            };
                                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                                            builder.setMessage(v.getContext().getResources().getString(R.string.are_you_sure_to_cancel_order)).setPositiveButton(v.getContext().getResources().getString(R.string.yes), dialogClickListener)
                                                    .setNegativeButton(v.getContext().getResources().getString(R.string.no), dialogClickListener).show();

                                        }
                                    });

                                    ((ClientAdapter.extTypeViewHolder) holder).btNotify.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {

                                            if ( quality_list.size()==0 || quality_list.isEmpty() ||quantity_list.contains(0l) || Index.getTotal_amount()==0l ) {
                                                Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.please_complete_the_order_to_proceed) , Toast.LENGTH_SHORT).show();
                                            } else {

                                                client_list.remove(list.get(0).getClient_no());
                                                list.remove(0);

                                                Index.setArrayList(client_list);

                                                quantity_list = myAdapter.getModifyQuantityList();
                                                quality_list = myAdapter.getModifyQualityList();

                                                progressDialog = new ProgressDialog(v.getContext());
                                                progressDialog.setTitle(v.getContext().getResources().getString(R.string.order_delivered));
                                                progressDialog.setMessage(v.getContext().getResources().getString(R.string.updating_please_wait));
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                saveOrder(name,client_no,quality_list,quantity_list,v,client_no);


                                            }
                                            UpdateToken();
                                        }
                                    });
                                }
                            });



                        }else {
                            ((ClientAdapter.TypeViewHolder) holder).address.setText(address);
                            ((ClientAdapter.TypeViewHolder) holder).name.setText(name);
                        }


                    }
                }

            }
        });


    }

    private void saveOrder(String name, final String contact, ArrayList<String> quality, ArrayList<Long> quantity, final View view, final String clientNo) {

        Date c = Calendar.getInstance().getTime();
        final String monthName = month_name[c.getMonth()];
        final String yearName =  (String) DateFormat.format("yyyy", c);
        final String dayName = (String) DateFormat.format("dd",c);
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c);
        long totalMilkLeft = Index.getCurrentTotalMilkAvailable();
        final Map<String,Long> stringLongMap = Index.getCurrentMilkTypeAvailability();
        sum_amount = 0l;
        sum_amount += Index.getTotal_amount();

        final Map<String,Long> order = new HashMap<>();

        for (int i = 0;i<quality.size();i++){
            order.put(quality.get(i),quantity.get(i));
            totalMilkLeft = totalMilkLeft - quantity.get(i);
            if (stringLongMap.keySet().contains(quality.get(i))){
                long l = stringLongMap.get(quality.get(i));
                stringLongMap.remove(quality.get(i));
                stringLongMap.put(quality.get(i),(l-quantity.get(i)));
            }
        }

        Index.setCurrentTotalMilkAvailable(totalMilkLeft);
        Index.setCurrentMilkTypeAvailability(stringLongMap);

        final  Map<String,Object> milkAvailableMap = new HashMap<>();
        milkAvailableMap.put("Total Milk Available",totalMilkLeft);
        milkAvailableMap.put("Milk Type Available",stringLongMap);
        milkAvailableMap.put("Date",formattedDate);

        final Map<String, Object> docData = new HashMap<>();
        docData.put("name",name);
        docData.put("Amount",Index.getTotal_amount());
        docData.put("Milk",order);
        docData.put("date",formattedDate);
        docData.put("timestamp",c);
        final long finalTotalMilkLeft = totalMilkLeft;
        mFirestore.collection("Delivery").document(user_id).collection("Orders").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    mFirestore.collection("Client").document(clientNo).collection("DeliveryPerson").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document.exists() && document != null){
                                    if ((Long)document.get("Due Amount")!=null) {
                                        sum_amount += (Long) document.get("Due Amount");
                                    }
                                    mFirestore.collection("Client").document(clientNo).collection("DeliveryPerson").document(user_id).update("Due Amount",sum_amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                mFirestore.collection("Delivery").document(user_id).collection("Milk Available").document("Current Availability").set(milkAvailableMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                    mFirestore.collection("Client").document(clientNo).collection("DeliveryPerson").document(user_id).collection("Orders").add(docData)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    customGauge.findViewById(R.id.gauge);
                                                                                    tvMilkLeft.findViewById(R.id.tvMilkLeft);
                                                                                    customGauge.setValue(Integer.parseInt(Long.toString(finalTotalMilkLeft)));
                                                                                    tvMilkLeft.setText(Long.toString(Index.getCurrentTotalMilkAvailable()));
                                                                                    milk_type_left_list.findViewById(R.id.milk_type_left_list);
                                                                                    milk_type_left_list.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                                                                                    milkTypeLeftAdapter = new MilkTypeLeftAdapter(stringLongMap);
                                                                                    milkTypeLeftAdapter.notifyDataSetChanged();
                                                                                    milk_type_left_list.setAdapter(milkTypeLeftAdapter);
                                                                                    Log.d(TAG,"New Order Added");
                                                                                    Toast.makeText(view.getContext(),view.getContext().getResources().getString(R.string.order_has_been_placee),Toast.LENGTH_SHORT).show();

                                                                                    saveStats(view,dayName,monthName,yearName,order,clientNo);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                            } else {
                                mFirestore.collection("Client").document(clientNo).collection("DeliveryPerson").document(user_id).update("Due Amount",sum_amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            mFirestore.collection("Delivery").document(user_id).collection("Milk Available").document("Current Availability").set(milkAvailableMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                mFirestore.collection("Client").document(clientNo).collection("DeliveryPerson").document(user_id).collection("Orders").add(docData)
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                customGauge.findViewById(R.id.gauge);
                                                                                tvMilkLeft.findViewById(R.id.tvMilkLeft);
                                                                                customGauge.setValue(Integer.parseInt(Long.toString(finalTotalMilkLeft)));
                                                                                tvMilkLeft.setText(Long.toString(Index.getCurrentTotalMilkAvailable()));
                                                                                milk_type_left_list.findViewById(R.id.milk_type_left_list);
                                                                                milk_type_left_list.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                                                                                milkTypeLeftAdapter = new MilkTypeLeftAdapter(stringLongMap);
                                                                                milkTypeLeftAdapter.notifyDataSetChanged();
                                                                                milk_type_left_list.setAdapter(milkTypeLeftAdapter);
                                                                                Log.d(TAG,"New Order Added");
                                                                                Toast.makeText(view.getContext(),view.getContext().getResources().getString(R.string.order_has_been_placee),Toast.LENGTH_SHORT).show();

                                                                                saveStats(view,dayName,monthName,yearName,order,clientNo);

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }

                        }
                    });
                  }
            }
        });
    }

    private void saveStats(final View view, final String dayName, final String monthName, final String yearName, final Map<String, Long> order, final String clientNo) {
        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Stats").document(yearName).collection(monthName).document(dayName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        long stat_amt = Long.parseLong(doc.getString("Amount"));
                        stat_amt += Index.getTotal_amount();
                        ArrayList<Map> productArray = new ArrayList<>();
                        productArray = (ArrayList<Map>) doc.get("Products");
                        ArrayList<Map> duplicate = new ArrayList<>();
                        duplicate.addAll(productArray);

                        for (Map m : duplicate) {
                            String prName = (String) m.get("Quality");
                            long prVol = Long.parseLong((String) m.get("Volume"));
                            long prPrice = Long.parseLong((String) m.get("Price"));

                            if (order.containsKey(prName)) {
                                productArray.remove(m);
                                prVol += order.get(prName);
                                prPrice += order.get(prName)*(Index.getProduct_detail_map().get(prName));
                                Map<String,String> newMap = new HashMap<>();
                                newMap.put("Quality",prName);
                                newMap.put("Volume",Long.toString(prVol));
                                newMap.put("Price",Long.toString(prPrice));
                                productArray.add(newMap);
                            }

                        }

                        Map docData = new HashMap();
                        docData.put("Amount",Long.toString(stat_amt));
                        docData.put("Month",monthName);
                        docData.put("Products",productArray);

                        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Stats").document(yearName).collection(monthName).document(dayName)
                                .set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Index.setTotal_amount(0l);
                                    amount.setText(Index.getTotal_amount().toString());
                                    sendNotifications(clientNo,"Order Delivered","Your Order has been delivered",view);
                                }
                            }
                        });

                    } else {
                        ArrayList<Map> productArray = new ArrayList<>();
                        ArrayList<String> allProducts = new ArrayList<>();
                        allProducts.addAll(Index.getProduct_list());
                        for (String s : allProducts) {
                            Map<String,Object> m = new HashMap<>();
                            if (order.keySet().contains(s)) {
                                m.put("Quality",s);
                                m.put("Volume",order.get(s).toString());
                                m.put("Price",Long.toString(order.get(s)*Index.getProduct_detail_map().get(s)));
                                productArray.add(m);
                            } else {
                                m.put("Quality",s);
                                m.put("Volume",Long.toString(0l));
                                m.put("Price",Long.toString(0l));
                                productArray.add(m);
                            }
                        }

                        Map docData = new HashMap();
                        docData.put("Amount",Long.toString(Index.getTotal_amount()));
                        docData.put("Month",monthName);
                        docData.put("Products",productArray);

                        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).collection("Stats").document(yearName).collection(monthName).document(dayName)
                                .set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Index.setTotal_amount(0l);
                                    amount.setText(Index.getTotal_amount().toString());
                                    sendNotifications(clientNo,"Order Delivered","Your Order has been delivered",view);
                                }
                            }
                        });

                    }
                }
            }
        });
    }


    private void updateUI(View v) {
        quality_list.clear();
        quantity_list.clear();
        notifyDataSetChanged();

        Index.setTotal_amount(0l);
        amount.setText(Index.getTotal_amount().toString());

        if (getItemCount() == 0) {

            index = Index.getINDEX();
            Date c = Calendar.getInstance().getTime();
            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);

            SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("check", 0);
            editor.putInt("key", index + 1);
            editor.putString("Date",formattedDate);
            editor.commit();

            Intent intent = new Intent(v.getContext(), DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("index", index + 1);
            Index.setINDEX(index + 1);
            progressDialog.dismiss();
            v.getContext().startActivity(intent);

        } else {

            SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Date c = Calendar.getInstance().getTime();
            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);

            Set<String> set = new HashSet<>();
            set.addAll(Index.getArrayList());

            editor.putStringSet("ArrayList", set);
            editor.putInt("key", Index.getINDEX());
            editor.putInt("check", 1);
            editor.putString("Date",formattedDate);
            editor.commit();
            progressDialog.dismiss();

        }
    }

    public void sendNotifications(final String clientMobile, final String title, final String message, final View view) {
        FirebaseFirestore.getInstance().collection("Client").document(clientMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                device_token=documentSnapshot.getString("Token id");
                assert device_token != null;
                Token token= new Token(device_token);
//                Log.d("FCM",device_token);
                //device_token = "ep12cGqTRv-Pd59tOMOiqV:APA91bEI5KjlMAq_OSRRkpCOYFTecMZVPkNqVBuX7V9QsNBKXDgikmBKf6wR7oefCGzPWq7dIfzmS5nxZcOmdI9Xq1ui8g-Z0XGUvoJAg7raCkzGX8Y5DVgGDGyXub__JFaf61-19K2D";
                if (device_token != null) {

                    Log.d("FCM","Title: "+title.toString()+" Message: "+message.toString());
                    Data data = new Data(title, message);
                    apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                    NotificationSender sender = new NotificationSender(data,device_token);
                    apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            if (response.code() == 200) {
                                int ds = response.body().success;
                                Log.d(TAG,"-- "+ds);
                                if (response.body().success != 1) {
                                    Toast.makeText(context, "Failed ", Toast.LENGTH_LONG);
                                    updateUI(view);
                                }
                                updateUI(view);
                                Log.d("FCM","Successfully Send");
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            updateUI(view);
                        }
                    });
                } else updateUI(view);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Unable to fetch token: "+e, Toast.LENGTH_SHORT).show();
                        updateUI(view);
                    }
                });

    }

    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseFirestore.getInstance().collection("Delivery").document(user_id).update("Token id",refreshToken);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return EXT_TYPE;
        }else{
            return INT_TYPE;
        }

    }

    @Override
    public int getItemCount() {
        if (list.size()>3){
            return 3;
        }else {
            return list.size();
        }

    }



    public static class extTypeViewHolder extends RecyclerView.ViewHolder {

        TextView name,address,contact_info;
        public static TextView amount;
        Button btNotify,btCancelOrder;
        FloatingActionButton btAdd;
        RecyclerView order_list;



        public extTypeViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvExtName);
            address= itemView.findViewById(R.id.tvExtAddress);
            contact_info = itemView.findViewById(R.id.tvExtContact);
            btNotify = itemView.findViewById(R.id.btNotify);
            btCancelOrder = itemView.findViewById(R.id.btCancelOrder);
            amount= itemView.findViewById(R.id.tvAmount);
            btAdd=itemView.findViewById(R.id.btAdd);
            order_list=itemView.findViewById(R.id.order_list);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            order_list.setLayoutManager(linearLayoutManager);


        }
    }

    public static class TypeViewHolder extends RecyclerView.ViewHolder {

        TextView name,address;

        public TypeViewHolder(View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.tv_client_name);
            address=itemView.findViewById(R.id.tv_client_address);

        }
    }


}
