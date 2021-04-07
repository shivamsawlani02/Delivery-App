package com.dsdairysystem.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Login_Screen extends AppCompatActivity {

    ImageView logo;
    TextView text1;
    TextView text2;
    Button btn_next;
    EditText phonenumber;
    private Spinner language_spinner;
    private String[] languages = new String[]{"Select language","English","हिन्दी"};
    private ArrayAdapter productAdapter;
    Locale myLocale;
    String currentLanguage = "en", currentLang;
    public static final String sharedPref = "languagePref";
    public static final String KEY = "key";
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen          super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        activity = this;

        currentLanguage = getIntent().getStringExtra(currentLang);
        //  logo = findViewById(R.id.logo);
        text1 = findViewById(R.id.welocme_text);
        //text2 = findViewById(R.id.mini_text);
        btn_next = findViewById(R.id.btn_continue);
        phonenumber = findViewById(R.id.phonenumber);

        language_spinner = findViewById(R.id.language_spinner);
        productAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item,languages);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_spinner.setAdapter(productAdapter);

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        setLocale("en");
                        break;
                    case 2:
                        setLocale("hi");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                String number = phonenumber.getText().toString();

                if (number.isEmpty()) {
                    phonenumber.setError("Field is empty");
                } else {
                    if (number.length()!=10){
                        phonenumber.setError("wrong format");
                    } else {
                        Intent intent = new Intent(Login_Screen.this, OTP_Verify.class);

                        Pair[] pairs = new Pair[3];
                        // pairs[0] = new Pair<View, String>(logo, "trans1");
                        pairs[0] = new Pair<View, String>(text1, "trans2");
                        pairs[1] = new Pair<View, String>(btn_next, "trans4");
                        pairs[2] = new Pair<View, String>(phonenumber, "trans5");

                        intent.putExtra("phonenumber", number);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login_Screen.this, pairs);
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        activity.finishAffinity();
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
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

            Intent refresh = new Intent(this, Login_Screen.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(Login_Screen.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }

}
