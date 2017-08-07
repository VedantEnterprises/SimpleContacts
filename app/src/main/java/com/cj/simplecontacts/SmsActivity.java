package com.cj.simplecontacts;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SmsActivity extends AppCompatActivity {
    private final static String TAG = "SmsActivity";
    private Toolbar toolbar;
    private ActionBar supportActionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("李洪伟");
        supportActionBar.setSubtitle("18521020687");
        toolbar.setNavigationIcon(R.drawable.sms_back_btn_bg);
        supportActionBar.setIcon(R.drawable.default_contact_head_icon);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.overflow_btn_bg));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG,"onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_sms_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"onOptionsItemSelected");
        int id = item.getItemId();

        if (id == R.id.clear_sms) {
            Toast.makeText(this,"清空信息",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.contact_detail) {
            Toast.makeText(this,"联系人详情",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.add_block_list) {
            Toast.makeText(this,"加入黑名单",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.more) {
            Toast.makeText(this,"更多",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.call) {
            Toast.makeText(this,"拨打电话",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
