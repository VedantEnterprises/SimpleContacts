package com.cj.simplecontacts;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cj.simplecontacts.fragment.ContactsFragment;
import com.cj.simplecontacts.fragment.DialFragment;
import com.cj.simplecontacts.fragment.LifeAssistantFragment;
import com.cj.simplecontacts.fragment.MessageFragment;

public class IndexActivity extends AppCompatActivity {
    private final static String TAG = "IndexActivity";


    private BottomNavigationBar bottomNavigationBar;
    private FrameLayout fragement_container;
    private Toolbar toolbar;

    private DialFragment dialFragment;
    private ContactsFragment contactsFragment;
    private MessageFragment messageFragment;
    private LifeAssistantFragment lifeAssistantFragment;

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IndexActivity onCreate");
        setContentView(R.layout.activity_index);

        initViews();
        setUpFragment();
        setUpBottomNavigationBar();
    }


    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        fragement_container = (FrameLayout) findViewById(R.id.fg_container);

        setSupportActionBar(toolbar);
    }

    private void setUpFragment() {
        fragmentManager = getSupportFragmentManager();
        dialFragment = new DialFragment();
        contactsFragment = new ContactsFragment();
        messageFragment = new MessageFragment();
        lifeAssistantFragment = new LifeAssistantFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fg_container,dialFragment);
        transaction.commit();
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
                switch (position){
                    case 0:
                        FragmentTransaction transaction0 = fragmentManager.beginTransaction();
                        transaction0.replace(R.id.fg_container,dialFragment);
                        transaction0.commit();
                        break;
                    case 1:
                        FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                        transaction1.replace(R.id.fg_container,contactsFragment);
                        transaction1.commit();
                        break;
                    case 2:
                        FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                        transaction2.replace(R.id.fg_container,messageFragment);
                        transaction2.commit();
                        break;
                    case 3:
                        FragmentTransaction transaction3 = fragmentManager.beginTransaction();
                        transaction3.replace(R.id.fg_container,lifeAssistantFragment);
                        transaction3.commit();
                        break;
                    default:
                        break;

                }
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


    public void hideToolbar(){
        getSupportActionBar().hide();
    }

    public void showToolbar(){
        getSupportActionBar().show();
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
