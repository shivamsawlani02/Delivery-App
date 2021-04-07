package com.dsdairysystem.deliveryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN=5000;

    Animation topanim, botanim;
    private SharedPreferences sharedPreferences, introPref;
    private Boolean intro = true;
    public static final String sharedPref = "languagePref";
    public static final String KEY = "key";
    String localeName;
    Locale myLocale;
    ImageView logo;                    // LOGO IMAGE
    TextView welcome_text;             // TEXT
    Map<String, Object> map;
    String phnumber;
    int check=0;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen          super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int exit = getIntent().getIntExtra("exit",0);

        if (exit ==1) {
            onBackPressed();
        } else {

            topanim = AnimationUtils.loadAnimation(this, R.anim.animation);
            botanim = AnimationUtils.loadAnimation(this, R.anim.anim_bottom);

            videoView = findViewById(R.id.video_view);
            videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.splash_screen_client);
            videoView.start();

          //  logo = findViewById(R.id.counrtydairy);
            //welcome_text = findViewById(R.id.welcome);
            //logo.setAnimation(topanim);
            //welcome_text.setAnimation(botanim);

            sharedPreferences =  getSharedPreferences(sharedPref,MODE_PRIVATE);
            localeName = sharedPreferences.getString("language","en");

            introPref = getSharedPreferences("MAIN",MODE_PRIVATE);
            intro = introPref.getBoolean("intro",true);

            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);




            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(isFinishing())
                        return;
                    Intent intent = new Intent(MainActivity.this, Login_Screen.class);

                    if (intro) {
                        Intent introIntent = new Intent(MainActivity.this,IntroActivity.class);
                        startActivity(introIntent);
                    } else {
                        if (check==0){
                            startActivity(intent);
                        }
                        if (check==1){

                            Intent work = new Intent(MainActivity.this, DashBoard.class);
                            work.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(work);

                        }
                        if (check==2){
                            Intent intent1 = new Intent(getApplicationContext(), Register.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            intent1.putExtra("phonenumber", phnumber);
                            startActivity(intent1);
                        }
                    }
                }
            });


           /* new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, Login_Screen.class);

                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(logo, "trans1");

                    pairs[1] = new Pair<View, String>(welcome_text, "trans2");

                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);



                    if (check==0){
                        startActivity(intent, activityOptions.toBundle());
                    }
                    if (check==1){

                        Intent work = new Intent(MainActivity.this, DashBoard.class);
                        work.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(work);

                    }
                    if (check==2){
                        Intent intent1 = new Intent(getApplicationContext(), Register.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent1.putExtra("phonenumber", phnumber);
                        startActivity(intent1);
                    }


//                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//
//
//                }else{
//
//                    startActivity(intent, activityOptions.toBundle());
//                }
//                finish();
//
                }
            }, SPLASH_SCREEN); */


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            phnumber= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference documentReference = db.collection("Delivery").document(phnumber);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();

                        if (snapshot != null && snapshot.exists()) {

                            check=1;
                            Log.d("ac", "exists");


                        } else {
                            check=2;
                        }
                    }

                }
            });
        }

    }
}
