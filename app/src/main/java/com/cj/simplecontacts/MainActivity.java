package com.cj.simplecontacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ViewPager vp;
    private LinearLayout ll_pager_indicator;

    private LayoutInflater layoutInflater;

    private ArrayList<View> vpList = new ArrayList<View>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"MainActivity onCreate");
        setContentView(R.layout.activity_main);

        if(!isFirstUse()){
            startActivity(new Intent(MainActivity.this,IndexActivity.class));
            finish();
            return;
        }
        initCommonObject();
        initViews();
        setUpViewPager();
        setListener();
    }

    private boolean isFirstUse(){
        SharedPreferences sharedPreferences = getSharedPreferences("simplecontacts", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isFirstUse", true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"MainActivity onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"MainActivity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"MainActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"MainActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"MainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"MainActivity onDestroy");
    }

    private void initCommonObject() {
        layoutInflater = getLayoutInflater();
    }

    private void initViews() {
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        vp = (ViewPager) findViewById(R.id.vp);
        ll_pager_indicator = (LinearLayout) findViewById(R.id.ll_pager_indicator);
    }

    private void setUpViewPager() {
        View view1 = layoutInflater.inflate(R.layout.activity_main_page1, null);
        View view2 = layoutInflater.inflate(R.layout.activity_main_page2, null);
        View view3 = layoutInflater.inflate(R.layout.activity_main_page3, null);
        Button button = (Button) view3.findViewById(R.id.enter_index_activity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,IndexActivity.class));
                setFlagNotFirstUse();
                finish();
            }
        });
        if(vpList == null){
            vpList = new ArrayList<View>();
        }
        vpList.clear();
        vpList.add(view1);
        vpList.add(view2);
        vpList.add(view3);

        vp.setAdapter(new MyAdpater());
        vp.setCurrentItem(0);
        ll_pager_indicator.setVisibility(View.VISIBLE);
        iv1.setSelected(true);
        iv2.setSelected(false);
        iv3.setSelected(false);
    }

    private void setFlagNotFirstUse(){
        SharedPreferences sharedPreferences = getSharedPreferences("simplecontacts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstUse",false);
        editor.commit();
    }

    private void setListener() {
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ll_pager_indicator.setVisibility(View.VISIBLE);
                    iv1.setSelected(true);
                    iv2.setSelected(false);
                    iv3.setSelected(false);
                }else if(position == 1){
                    ll_pager_indicator.setVisibility(View.VISIBLE);
                    iv1.setSelected(false);
                    iv2.setSelected(true);
                    iv3.setSelected(false);
                }else if(position == 2){
                    ll_pager_indicator.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MyAdpater extends PagerAdapter{

        @Override
        public int getCount() {
            Log.d(TAG,"size = "+vpList.size());
            return vpList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = vpList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
          container.removeView(vpList.get(position));
        }
    }

}
