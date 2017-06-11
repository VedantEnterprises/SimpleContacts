package com.cj.simplecontacts;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class IndexActivity extends AppCompatActivity {
    private final static String TAG = "IndexActivity";
    private BottomNavigationBar bottomNavigationBar;
    private FrameLayout fragement_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IndexActivity onCreate");
        setContentView(R.layout.activity_index);
        initViews();

        setUpBottomNavigationBar();
    }



    private void initViews() {
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        fragement_container = (FrameLayout) findViewById(R.id.fg_container);
    }

    private void setUpBottomNavigationBar() {

        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.tab_widget_green_dial_down,"拨号")
                                    .setInactiveIconResource(R.drawable.tab_widget_green_dial))

                .addItem(new BottomNavigationItem(R.drawable.tab_widget_green_contacts_pressed,"联系人")
                            .setInactiveIconResource(R.drawable.tab_widget_green_contacts))

                .addItem(new BottomNavigationItem(R.drawable.tab_widget_green_message_pressed,"信息")
                        .setInactiveIconResource(R.drawable.tab_widget_green_message))

                .addItem(new BottomNavigationItem(R.drawable.tab_widget_green_cloud_pressed,"生活助手")
                        .setInactiveIconResource(R.drawable.tab_widget_green_cloud))

                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                Log.d(TAG, "onTabSelected position="+position);
            }

            @Override
            public void onTabUnselected(int position) {
                Log.d(TAG, "onTabUnselected position="+position);
            }

            @Override
            public void onTabReselected(int position) {
                Log.d(TAG, "onTabReselected position="+position);
            }
        });
    }

    @Override
    public void finish() {
        //  super.finish();
        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "IndexActivity onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "IndexActivity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "IndexActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "IndexActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "IndexActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "IndexActivity onDestroy");
    }
}
