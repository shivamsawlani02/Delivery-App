package com.dsdairysystem.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.query.QueryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.dsdairysystem.deliveryapp.HomeFragment.sharedPref;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBar toolbar;
    public static NavigationView navigationView;
    public static DrawerLayout drawer;
    private Spinner language_spinner;
    private String[] languages = new String[]{"Select language","English","हिन्दी"};
    private ArrayAdapter productAdapter;
    String currentLanguage = "en", currentLang;
    Locale myLocale;
    private ProgressDialog progressDialog;
    public static final String sharedPref = "languagePref";
    public static final String KEY = "key";
    public static Activity activity;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_route:
                    fragment = new RouteFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_sales:
                    fragment = new SalesFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        loadFragment(new HomeFragment());
        activity = this;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigationView = findViewById(R.id.navigation_view);
        drawer = findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.bringToFront();

    }

    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            activity.finishAffinity();
            /*Intent intent = new Intent(DashBoard.this,MainActivity.class);
            intent.putExtra("exit",1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home :
                onBackPressed();
                break;

            case R.id.query :
                Intent intent = new Intent(DashBoard.this, QueryActivity.class);
                startActivity(intent);
                break;

            case R.id.details :
                Intent details = new Intent(DashBoard.this,DeliveryProfileActivity.class);
                startActivity(details);
                break;

            case R.id.product_add :
                Intent addProduct = new Intent(DashBoard.this,AddProductActivity.class);
                startActivity(addProduct);
                break;

            case R.id.logout :
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                FirebaseAuth.getInstance().signOut();
                                Intent intent=new Intent(DashBoard.this,Login_Screen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear().commit();
                                startActivity(intent);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }


                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
                builder.setMessage(getResources().getString(R.string.are_you_sure_to_logout)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
                break;

            case R.id.language :
                AlertDialog.Builder language_builder = new AlertDialog.Builder(this);
                final View view =  LayoutInflater.from(this).inflate(R.layout.language_dialog, null);
                language_builder.setView(view)
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (language_spinner.getSelectedItem().toString().equals("Select language")){
                                    Toast.makeText(DashBoard.this,"Select any Language",Toast.LENGTH_SHORT).show();
                                }else {
                                    progressDialog = new ProgressDialog(DashBoard.this);
                                    progressDialog.setTitle(getResources().getString(R.string.order_canceled));
                                    progressDialog.setMessage(getResources().getString(R.string.updating_please_wait));
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();
                                    switch (language_spinner.getSelectedItem().toString()){
                                        case "English" :
                                            setLocale("en");
                                            break;
                                        case "हिन्दी" :
                                            setLocale("hi");
                                            break;
                                    }
                                }
                            }
                        });
                language_spinner = view.findViewById(R.id.language_spinner);
                productAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item,languages);
                productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                language_spinner.setAdapter(productAdapter);
                language_builder.create().show();
                break;
        }
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setLocale(String localeName) {
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);

        SharedPreferences sharedPreferences = getSharedPreferences(sharedPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language",localeName);
        editor.commit();

        Intent refresh = new Intent(this, DashBoard.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        progressDialog.dismiss();
        startActivity(refresh);
    }

}
