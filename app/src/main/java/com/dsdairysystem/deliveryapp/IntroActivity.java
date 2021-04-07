package com.dsdairysystem.deliveryapp;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;


public class IntroActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageButton mNextBtn;
    Button mSkipBtn;
    Button mFinishBtn;
    ImageView indi1;
    ImageView indi2;
    ImageView indi3;
    ImageView indi4;
    ImageView indi5;
    ImageView[] indicators;
    Activity activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        activity = this;

        indi1 = findViewById(R.id.intro_indicator_1);
        indi2 = findViewById(R.id.intro_indicator_2);
        indi3 = findViewById(R.id.intro_indicator_3);
        indi4 = findViewById(R.id.intro_indicator_4);
        indi5 = findViewById(R.id.intro_indicator_5);

        mNextBtn = findViewById(R.id.intro_btn_next);
        viewPager = findViewById(R.id.view_pager);
        mFinishBtn = findViewById(R.id.intro_btn_finish);
        mSkipBtn = findViewById(R.id.intro_btn_skip);

        //View view=getWindow().getDecorView();
        indicators=new ImageView[]{indi1,indi2,indi3,indi4,indi5};
        final int color1 = ContextCompat.getColor(this, R.color.orange);
        final int color2 = ContextCompat.getColor(this, R.color.azure);
        final int color3 = ContextCompat.getColor(this, R.color.spring_green);
        final ArgbEvaluator evaluator=new ArgbEvaluator();
        final int[] colorList = new int[]{color1, color2,color3,color1,color2};
        viewPager.setAdapter(new IntroAdapter());
        updateIndicators(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == colorList.length-1 ? position : position + 1]);
                viewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);

                switch (position) {
                    case 0:
                        viewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        viewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        viewPager.setBackgroundColor(color3);
                        break;
                    case 3:
                        viewPager.setBackgroundColor(color1);
                        break;
                    case 4:
                        viewPager.setBackgroundColor(color2);
                        break;
                }
                mNextBtn.setVisibility(position == colorList.length-1 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == colorList.length-1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        });
        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("intro",false).apply();
                startActivity(new Intent(IntroActivity.this,Login_Screen.class));
                finish();
            }
        });
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("intro",false).apply();
                startActivity(new Intent(IntroActivity.this,Login_Screen.class));
                finish();
            }
        });
    }
    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    @Override
    public void onBackPressed() {
        activity.finishAffinity();
    }
}
