package com.dsdairysystem.deliveryapp.route_tab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.BuildConfig;
import com.dsdairysystem.deliveryapp.DashBoard;
import com.dsdairysystem.deliveryapp.DeliveryProfileActivity;
import com.dsdairysystem.deliveryapp.Login_Screen;
import com.dsdairysystem.deliveryapp.MonthYearPickerDialog;
import com.dsdairysystem.deliveryapp.R;
import com.dsdairysystem.deliveryapp.order_placing.Index;
import com.dsdairysystem.deliveryapp.order_placing.MilkTypeLeftAdapter;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Call;
import retrofit2.Callback;

import static com.dsdairysystem.deliveryapp.HomeFragment.customGauge;
import static com.dsdairysystem.deliveryapp.HomeFragment.milkTypeLeftAdapter;
import static com.dsdairysystem.deliveryapp.HomeFragment.milk_type_left_list;
import static com.dsdairysystem.deliveryapp.HomeFragment.tvMilkLeft;
import static com.dsdairysystem.deliveryapp.order_placing.ClientAdapter.extTypeViewHolder.amount;

public class CustomerDetail extends AppCompatActivity {
    private String contactNumber, groupName;
    private FirebaseFirestore db;
    private TextView name,email,phone,address,due,noOrder,noAddedProduct;
    private RecyclerView recyclerView, product_list;
    private List<OrderModel> list;
    private String deliveryMancontact;
    private Spinner spinner;
    private ImageView btBack;
    private Button alertSave,alertCancel, btAddOrder, btPdf;
    private ImageButton  btSharePdf;
    private FloatingActionButton btAddMoreProduct;
    public static TextView amount;
    private ArrayList<String> quality_list = new ArrayList<>();
    private ArrayList<Long> quantity_list = new ArrayList<>();
    private AddOrderProductAdapter myAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private long sum_amount;
    private static final String TAG = "ClientDetails";

    private MaterialButton removeClient,saveButton;
    private String[] month_name = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

    private EditText groupname_,priority_;
    private Dialog dialog;

    private List<String> group;
    private ActionBar toolbar;

    private String custName,custMobile;

    private DatePicker datePicker;

    private ArrayAdapter<String> adapter;
    private int POSITION;

    private String selectedDate, device_token;
    private APIService apiService;
    private Date date;
    private  OrderListAdapter orderListAdapter;
    private ArrayList<Map> mapArrayList = new ArrayList<>();
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    private int sumAmt = 0;
    private Activity activity;
    Dialog customDialog;
    private Button btExPdf, btExcel;
    private ImageView btCancel;

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        Log.i("Number", getIntent().getStringExtra("number"));
//        getActionBar().hide();
        dialog = new Dialog(this);

        populateId();

        activity = this;

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list = new ArrayList<>();
        contactNumber = getIntent().getStringExtra("number");
        db = FirebaseFirestore.getInstance();
        //deliveryMancontact = "+919988776655"; // you need to access this from sharedprefs or a localdatabase
        deliveryMancontact= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        groupName = getIntent().getStringExtra("groupname");

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        //adapter.add(getIntent().getStringExtra("groupname"));
        adapter.add("Create a new group");

        db.collection("Client").document(contactNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Toast.makeText(CustomerDetail.this, documentSnapshot.get("Email").toString(), Toast.LENGTH_SHORT).show();


                custName = documentSnapshot.get("Name").toString();
                custMobile = documentSnapshot.get("Mobile").toString();

                name.setText(documentSnapshot.get("Name").toString());
                if (documentSnapshot.get("Email") != null){
                    email.setText(documentSnapshot.get("Email").toString());
                } else email.setText("No email registered");
                phone.setText(documentSnapshot.get("Mobile").toString());
                try {
                    address.setText(documentSnapshot.get("adl1").toString() + ", " +
                            documentSnapshot.get("adl2").toString() + ",\n" +
                            documentSnapshot.get("adl3").toString() + ",\n" +
                            documentSnapshot.get("City").toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        db.collection("Client")
                .document(contactNumber)
                .collection("DeliveryPerson")
                .document(deliveryMancontact).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        try {
                            due.setText(documentSnapshot.get("Due Amount").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

        btPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapper(false);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        btSharePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapper(true);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        db.collection("Client")
                .document(contactNumber)
                .collection("DeliveryPerson")
                .document(deliveryMancontact).collection("Orders").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int i = 0;
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                            noOrder.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                list.add(i,documentSnapshot.toObject(OrderModel.class));
                                i++;
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            orderListAdapter=new OrderListAdapter(list);
                            recyclerView.setAdapter(orderListAdapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
                        } else {
                            noOrder.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }
                });


        db.collection("Delivery").document(deliveryMancontact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    group = (ArrayList) documentSnapshot.get("Group");
                    for (String groupname : group) {
                        adapter.add(groupname);

                    }
                    adapter.remove(getIntent().getStringExtra("groupname"));
                    adapter.notifyDataSetChanged();
                    adapter.insert(getIntent().getStringExtra("groupname"), 0);
                    spinner.setAdapter(adapter);


                }

            }
        });

        FirebaseFirestore.getInstance().collection("Delivery").document(deliveryMancontact).collection("Milk Available").document("Current Availability").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            Date c = Calendar.getInstance().getTime();
                            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c);

                            if(doc.exists() && doc.getString("Date").equals(formattedDate)) {
                                Index.setCurrentMilkTypeAvailability((Map<String, Long>) doc.get("Milk Type Available"));
                                Index.setCurrentTotalMilkAvailable(doc.getLong("Total Milk Available"));
                            } else {
                                Map<String,Long> map = new HashMap<>();
                                Index.setCurrentMilkTypeAvailability((Map<String, Long>) map);
                            }
                        }
                    }
                });


        btAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Index.setTotal_amount_new_order(0l);
                quality_list.clear();
                quantity_list.clear();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
                View view =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.add_new_order_dialog, null);
                builder.setView(view)
                        .setNegativeButton(v.getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton(v.getContext().getResources().getString(R.string.add_order), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (Long.parseLong(amount.getText().toString()) != 0l){
                                    progressDialog = new ProgressDialog(v.getContext());
                                    progressDialog.setTitle(v.getContext().getResources().getString(R.string.order_delivered));
                                    progressDialog.setMessage(v.getContext().getResources().getString(R.string.updating_please_wait));
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            saveOrder(custName,custMobile,myAdapter.getModifyQualityList(),myAdapter.getModifyQuantityList(),v);

                                        }
                                    }, 2000);


                                } else Toast.makeText(CustomerDetail.this,getResources().getString(R.string.complete_order_details),Toast.LENGTH_SHORT);

                            }
                        });
                btAddMoreProduct = view.findViewById(R.id.btAddMoreProduct);
                product_list = view.findViewById(R.id.add_products_list);
                product_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                myAdapter = new AddOrderProductAdapter(quantity_list,quality_list,getApplicationContext());
                product_list.setAdapter(myAdapter);
                amount = view.findViewById(R.id.tvTotalAddAmount);
                noAddedProduct= view.findViewById(R.id.tvNoProductAdded);

                btAddMoreProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noAddedProduct.setVisibility(View.GONE);
                        product_list.setVisibility(View.VISIBLE);
                        quality_list.add("Add Product");
                        quantity_list.add(0l);
                        myAdapter.notifyItemInserted(quality_list.size()-1);
                    }
                });
                if (Index.getCurrentMilkTypeAvailability() != null && !Index.getCurrentMilkTypeAvailability().isEmpty()) {
                    builder.create().show();
                } else Toast.makeText(CustomerDetail.this,getResources().getString(R.string.first_add_today_req),Toast.LENGTH_SHORT).show();
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(CustomerDetail.this, adapter.getItem(position), Toast.LENGTH_SHORT).show();
                if (position != 0 && position != 1) {
                    saveButton.setAlpha(1);
                    saveButton.setEnabled(true);
                    POSITION = position;

                } else if (position == 0) {
                    saveButton.setAlpha(0.5f);
                    saveButton.setEnabled(false);
                } else if (position == 1) {
                    //do something to create a new group
                    dialog.setContentView(R.layout.dialog_layout);
                    dialog.show();
                    alertSave = dialog.findViewById(R.id.save_btnl);
                    alertCancel = dialog.findViewById(R.id.btnl_cancel);
                    groupname_ = dialog.findViewById(R.id.ed_new_group);
                    priority_ = dialog.findViewById(R.id.ed_priority);
                    Toast.makeText(CustomerDetail.this, getResources().getString(R.string.make_a_group), Toast.LENGTH_SHORT).show();
                    dialogFunction();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroup(POSITION);
            }
        });


        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                list.clear();
                orderListAdapter.notifyDataSetChanged();
                selectedDate = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
                Log.i("selectedDate", selectedDate);
                final SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yy");
                try {
                    date = formatter.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


//                Toast.makeText(CustomerDetail.this, formatter.format(date), Toast.LENGTH_SHORT).show();

                db.collection("Client")
                        .document(contactNumber)
                        .collection("DeliveryPerson")
                        .document(deliveryMancontact).collection("Orders").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                Log.i("Success", "True");
                                int i = 0;

                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

//                                    Log.i("Field",documentSnapshot.getData().get("Date")+"  ////  "+(formatter.format(date).trim())+"");

                                    if (String.valueOf(documentSnapshot.get("Date")).equals(formatter.format(date).trim())) {
                                        list.add(i, documentSnapshot.toObject(OrderModel.class));
                                    }
                                    i++;
                                }
                                Log.i("VAL OF I", i + "");
                                orderListAdapter.notifyDataSetChanged();


                            }
                        });

            }
        });

        removeClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Disconnecting", "true");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                progressDialog = new ProgressDialog(CustomerDetail.this);
                                progressDialog.setTitle(getResources().getString(R.string.disconnecting));
                                progressDialog.setMessage(getResources().getString(R.string.updating_please_wait));
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();

                                db.collection("Delivery").document(deliveryMancontact).collection("Client").document(custMobile).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        db.collection("Client").document(custMobile).collection("DeliveryPersson").document(deliveryMancontact).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                db.collection("Delivery").document(deliveryMancontact).collection("Group").document(groupName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        DocumentSnapshot doc = task.getResult();
                                                        if (task.isSuccessful() && doc.exists()){
                                                            mapArrayList = (ArrayList<Map>) doc.get("Client");
                                                            Map<String,String> clientMap = new HashMap<>();
                                                            clientMap.put(custName,custMobile);
                                                            mapArrayList.remove(clientMap);

                                                            if (mapArrayList.size() ==0 || mapArrayList.isEmpty()){
                                                                db.collection("Delivery").document(deliveryMancontact).collection("Group").document(groupName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        db.collection("Delivery").document(deliveryMancontact).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                DocumentSnapshot userDoc = task.getResult();
                                                                                if (task.isSuccessful()){
                                                                                    ArrayList<String> groups = (ArrayList<String>) userDoc.get("Group");
                                                                                    groups.remove(groupName);
                                                                                    db.collection("Delivery").document(deliveryMancontact).update("Group",groups).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Toast.makeText(CustomerDetail.this, getResources().getString(R.string.client_deleted_successfully), Toast.LENGTH_SHORT).show();
                                                                                            Intent intent = new Intent(CustomerDetail.this, DashBoard.class);
                                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                            startActivity(intent);
                                                                                            progressDialog.dismiss();
                                                                                        }
                                                                                    });
                                                                                } else progressDialog.dismiss();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }

                                                            else {
                                                                db.collection("Delivery").document(deliveryMancontact).collection("Group").document(groupName)
                                                                        .update("Client",mapArrayList)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                Toast.makeText(CustomerDetail.this, getResources().getString(R.string.client_deleted_successfully), Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(CustomerDetail.this, DashBoard.class);
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                startActivity(intent);
                                                                                progressDialog.dismiss();
                                                                            }
                                                                        });
                                                            }


                                                        } else progressDialog.dismiss();
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }


                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDetail.this);
                builder.setMessage(getResources().getString(R.string.are_you_sure_to_disconnect)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
            }
        });

        }
    private void createPdfWrapper(Boolean share) throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            if (share) {
                showExportDialog(true);
            } else {
            MonthYearPickerDialog pd = new MonthYearPickerDialog();
            pd.setList(list,getApplicationContext(), activity, custName, custMobile, share,"pdf");
            pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");}

        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showExportDialog(final Boolean share) {
        customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.export_dialog);
        btExcel = customDialog.findViewById(R.id.btExportExcel);
        btExPdf = customDialog.findViewById(R.id.btExportPdf);
        btCancel = customDialog.findViewById(R.id.cancel);

        btExPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(list,getApplicationContext(), activity, custName, custMobile, share,"pdf");
                pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        btExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(list,getApplicationContext(), activity, custName, custMobile, share,"excel");
                pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    private void dialogFunction() {

        alertSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> newgroup = group;
                if (groupname_.getText().toString().isEmpty()) {
                    Toast.makeText(CustomerDetail.this, getResources().getString(R.string.enter_group_name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (priority_.getText().toString().isEmpty()) {
                    Toast.makeText(CustomerDetail.this, getResources().getString(R.string.enter_group_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(priority_.getText().toString()) > newgroup.size()) {
                    Toast.makeText(CustomerDetail.this, "Enter a number between 0 and " + newgroup.size(), Toast.LENGTH_SHORT).show();
                    return;
                }


                newgroup.add(Integer.parseInt(priority_.getText().toString().trim()), groupname_.getText().toString().trim());
//                Log.i("LIST",newgroup.toString());


                db.collection("Delivery").document(deliveryMancontact).update("Group", newgroup).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        createGroupDoc(newgroup, Integer.parseInt(priority_.getText().toString()));
                    }
                });


            }
        });

        alertCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                spinner.setSelection(0);
            }
        });
    }

    private void createGroupDoc(final List<String> newgroup, final int pos) {
        final Map<String, Object> maps = new HashMap<>();
        maps.put(custName, custMobile);


        final List<Map<String, Object>> list = new ArrayList<>();
        list.add(maps);

        NewGroupModel newGroupModel = new NewGroupModel(pos, list);
        db.collection("Delivery").document(deliveryMancontact).collection("Group")
                .document(newgroup.get(pos)).set(newGroupModel, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        for (int i = 0; i < newgroup.size(); i++) {
                            db.collection("Delivery")
                                    .document(deliveryMancontact)
                                    .collection("Group")
                                    .document(newgroup.get(i))
                                    .update("priority", i).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    db.collection("Delivery")
                                            .document(deliveryMancontact)
                                            .collection("Group")
                                            .document(getIntent().getStringExtra("groupname")).update("Client", FieldValue.arrayRemove(maps)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();
                                            //intent to some other activity
                                            spinner.setSelection(pos);
                                            Intent intent = new Intent(CustomerDetail.this, DashBoard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        }


                    }
                });


    }

    private void updateGroup(final int pos) {
//        Toast.makeText(this, adapter.getItem(POSITION), Toast.LENGTH_SHORT).show();
        final Map<String, Object> maps = new HashMap<>();
        maps.put(custName, custMobile);

        db.collection("Delivery")
                .document(deliveryMancontact)
                .collection("Group")
                .document(adapter.getItem(pos)).update("Client", FieldValue.arrayUnion(maps)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                db.collection("Delivery")
                        .document(deliveryMancontact)
                        .collection("Group")
                        .document(getIntent().getStringExtra("groupname")).update("Client", FieldValue.arrayRemove(maps)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CustomerDetail.this, getResources().getString(R.string.group_changed_to) + adapter.getItem(pos), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CustomerDetail.this, DashBoard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

            }
        });

    }


    private void saveOrder(String name, final String contact, ArrayList<String> quality, ArrayList<Long> quantity, final View view) {

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
        sum_amount += Index.getTotal_amount_new_order();

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
        docData.put("Amount",Index.getTotal_amount_new_order());
        docData.put("Milk",order);
        docData.put("date",formattedDate);
        docData.put("timestamp",c);
        final long finalTotalMilkLeft = totalMilkLeft;
        mFirestore.collection("Delivery").document(deliveryMancontact).collection("Orders").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    mFirestore.collection("Client").document(contact).collection("DeliveryPerson").document(deliveryMancontact).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document.exists() && document != null){
                                    if ((Long)document.get("Due Amount")!=null) {
                                        sum_amount += (Long) document.get("Due Amount");
                                    }
                                    mFirestore.collection("Client").document(contact).collection("DeliveryPerson").document(deliveryMancontact).update("Due Amount",sum_amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                mFirestore.collection("Delivery").document(deliveryMancontact).collection("Milk Available").document("Current Availability").set(milkAvailableMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                    mFirestore.collection("Client").document(contact).collection("DeliveryPerson").document(deliveryMancontact).collection("Orders").add(docData)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    Log.d(TAG,"New Order Added");

                                                                                    saveStats(view,dayName,monthName,yearName,order);

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
                                mFirestore.collection("Client").document(contact).collection("DeliveryPerson").document(deliveryMancontact).update("Due Amount",sum_amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            mFirestore.collection("Delivery").document(deliveryMancontact).collection("Milk Available").document("Current Availability").set(milkAvailableMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                mFirestore.collection("Client").document(contact).collection("DeliveryPerson").document(deliveryMancontact).collection("Orders").add(docData)
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                Log.d(TAG,"New Order Added");

                                                                                saveStats(view,dayName,monthName,yearName,order);
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

    private void saveStats(final View view, final String dayName, final String monthName, final String yearName, final Map<String, Long> order) {
        FirebaseFirestore.getInstance().collection("Delivery").document(deliveryMancontact).collection("Stats").document(yearName).collection(monthName).document(dayName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        long stat_amt = Long.parseLong(doc.getString("Amount"));
                        stat_amt += Index.getTotal_amount_new_order();
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
                                prPrice += prVol*(Index.getProduct_detail_map().get(prName));
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

                        FirebaseFirestore.getInstance().collection("Delivery").document(deliveryMancontact).collection("Stats").document(yearName).collection(monthName).document(dayName)
                                .set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(view.getContext(),view.getContext().getResources().getString(R.string.order_has_been_placee),Toast.LENGTH_SHORT).show();
                                    Index.setTotal_amount(0l);
                                    amount.setText(Index.getTotal_amount().toString());
                                    Index.setTotal_amount_new_order(0l);
                                    sendNotifications(custMobile,"Order Delivered","Your Order has been delivered");
                                    Intent intent = new Intent(CustomerDetail.this,DashBoard.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    //intent.putExtra("number",custMobile);
                                    progressDialog.dismiss();
                                    startActivity(intent);
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

                        FirebaseFirestore.getInstance().collection("Delivery").document(deliveryMancontact).collection("Stats").document(yearName).collection(monthName).document(dayName)
                                .set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(view.getContext(),view.getContext().getResources().getString(R.string.order_has_been_placee),Toast.LENGTH_SHORT).show();
                                    Index.setTotal_amount(0l);
                                    Index.setTotal_amount_new_order(0l);
                                    amount.setText(Index.getTotal_amount().toString());
                                    sendNotifications(custMobile,"Order Delivered","Your Order has been delivered");
                                    Intent intent = new Intent(CustomerDetail.this,DashBoard.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    //intent.putExtra("number",custMobile);
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                }
                            }
                        });

                    }
                }
            }
        });
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
                                    Toast.makeText(CustomerDetail.this, "Failed ", Toast.LENGTH_LONG);
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
                        Toast.makeText(CustomerDetail.this, "Unable to fetch token: "+e, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void populateId() {
        name = findViewById(R.id.textView);
        email = findViewById(R.id.textView2);
        phone = findViewById(R.id.textView3);
        address = findViewById(R.id.textView4);
        recyclerView = findViewById(R.id.rv_customerdetail);
        due = findViewById(R.id.textView6);
        spinner = findViewById(R.id.spinner);
        saveButton = findViewById(R.id.button);
        noOrder = findViewById(R.id.tv_no_client_order);
        btBack = findViewById(R.id.btBack2);
        datePicker = findViewById(R.id.datepicker_customer_detail);
        removeClient = findViewById(R.id.remove_client);
        btAddOrder = findViewById(R.id.btAddOrder);
        btPdf = findViewById(R.id.btOpenPdf2);
        btSharePdf = findViewById(R.id.btSharePdf);
    }
}
