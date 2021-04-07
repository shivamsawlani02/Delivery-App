package com.dsdairysystem.deliveryapp.add_client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.dsdairysystem.deliveryapp.DashBoard;
import com.dsdairysystem.deliveryapp.Login_Screen;
import com.dsdairysystem.deliveryapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import pub.devrel.easypermissions.EasyPermissions;

public class AddClient extends AppCompatActivity {
    public static EditText phone;
    ImageView proceed,contact;
    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int CONTACTS_PERMISSION_REQUEST_CODE = 2;
    FirebaseFirestore db;
    String p;
    String milkmanMobile;
    int REQUEST_CODE_CONTACTS = 3;
    ProgressBar pgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        attachID();
        mCodeScanner = new CodeScanner(this, scannerView);
        if (!(EasyPermissions.hasPermissions(AddClient.this,
                Manifest.permission.CAMERA))) {
            EasyPermissions.requestPermissions(AddClient.this,
                    "ALLOW CAMERA ACCESS",
                    CAMERA_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CAMERA);
        }
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                AddClient.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        phone.setText(result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(EasyPermissions.hasPermissions(AddClient.this,
                        Manifest.permission.READ_CONTACTS))) {
                    EasyPermissions.requestPermissions(AddClient.this,
                            "ALLOW CONTACTS ACCESS",
                            CONTACTS_PERMISSION_REQUEST_CODE,
                            Manifest.permission.READ_CONTACTS);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent,REQUEST_CODE_CONTACTS);
                }
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p = phone.getText().toString();
                if (!(p.startsWith("+91"))) {
                    p = "+91" + p;
                }
                Log.i("MOBILE NUMBER", p);
                // int f = checkClientAlreadyAdded(p);
                if (p.length() < 13) {
                    Toast.makeText(AddClient.this, getResources().getString(R.string.invaild_mobile_no), Toast.LENGTH_SHORT).show();
                } else {
                    pgBar.setVisibility(View.VISIBLE);
                    proceed.setVisibility(View.INVISIBLE);
                    checkClientExists(p);
                }
                phone.setText("");
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void attachID() {
        phone = findViewById(R.id.mobile);
        proceed = findViewById(R.id.proceed);
        scannerView = findViewById(R.id.scanner_view);
        db = FirebaseFirestore.getInstance();
        //milkmanMobile = "+919988776655";
        milkmanMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        contact=findViewById(R.id.contact);
        pgBar=findViewById(R.id.progress_bar);
    }

    private void checkClientAlreadyAdded(final String m) {

        db.collection("Delivery").document(milkmanMobile).collection("Client").document(m).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            Toast.makeText(AddClient.this, getResources().getString(R.string.client_already_connected), Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Intent intent = new Intent(AddClient.this, ClientDetails.class);
                            intent.putExtra("phone",m);
                            Log.i("MOBILE INTENT",m);
                            startActivity(intent);
                            finish();
                        }
                        pgBar.setVisibility(View.INVISIBLE);
                        proceed.setVisibility(View.VISIBLE);
                    }
                });


    }
    private void checkClientExists(final String m)
    {
        db.collection("Client").document(m).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists())
                        {
                            checkClientAlreadyAdded(m);
                        }
                        else
                        {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            pgBar.setVisibility(View.INVISIBLE);
                                            proceed.setVisibility(View.VISIBLE);
                                            Intent intent = new Intent(AddClient.this,AddNewClient.class);
                                            intent.putExtra("phone",m);
                                            startActivity(intent);


                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }


                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddClient.this);
                            builder.setMessage("No client with this mobile number found!, Do you want to add this client?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();

                            }

                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTACTS) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String num = "";
                    if (Integer.valueOf(hasNumber) == 1) {
                        Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (numbers.moveToNext()) {
                            num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone.setText(num);
                            //Toast.makeText(MainActivity.this, "Number="+num, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }
}
