package com.cj.simplecontacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cj.simplecontacts.adapter.SmsDetailAdapter;
import com.cj.simplecontacts.enity.Message;
import com.cj.simplecontacts.tool.ContactTool;
import com.cj.simplecontacts.tool.NumberUtil;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SmsActivity extends AppCompatActivity {
    private final static String TAG = "SmsActivity";
    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Message> datas = new ArrayList<>();
    private SmsDetailAdapter adapter;
    private int threadID = -1;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Intent intent = getIntent();
        if(intent != null){
            message = intent.getParcelableExtra("sms");
        }
        initView();
        setUpSupportActionBar();
        setUpRecyclerView();

        if(message != null){
            threadID = message.getThreadID();
            querySmsFromDB();
            Log.d(TAG,"onCreate  threadID="+threadID);

        }else{
            Log.d(TAG,"message is null ");
        }

    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_sms);

    }

    private void setUpSupportActionBar(){
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        supportActionBar.setIcon(R.drawable.default_contact_head_icon);
        if(message != null){
            String person = message.getPerson();
            if(TextUtils.isEmpty(person)){
                supportActionBar.setTitle("陌生人");
            }else {
                supportActionBar.setTitle(person);
            }
            supportActionBar.setSubtitle(message.getAddress());
        }

        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.overflow_btn_bg));
        toolbar.setNavigationIcon(R.drawable.sms_back_btn_bg);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsActivity.this.onBackPressed();
            }
        });

    }

    private void setUpRecyclerView(){

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    private void querySmsFromDB(){
        Log.d(TAG,"querySmsFromDB");
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        //Log.d(TAG,"queryContactsFromDB Observable create  thread:"+Thread.currentThread().getName());
                        queryDetailInformation();
                        observableEmitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        // Log.d(TAG,"queryContactsFromDB onNext:value="+value.intValue());
                        //  Log.d(TAG,"queryContactsFromDB onNext  thread:"+Thread.currentThread().getName());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        //Log.d(TAG,"queryContactsFromDB  onComplete");
                        // Log.d(TAG,"queryContactsFromDB onComplete thread:"+Thread.currentThread().getName());
                        updateData();

                    }
                });
    }

    private void queryDetailInformation() {
        Log.d(TAG, "queryDetailInformation()");
        String st = "";
        Cursor cursor = ContactTool.getSmsCursorByThreadID(getContentResolver(),threadID);
        if (cursor == null) {
            return;
        }
        st += "----------------------------------------------- \n";

        while(cursor.moveToNext()){
            Message message = new Message();

            int id = cursor.getInt(cursor.getColumnIndex(Telephony.Sms._ID));
            int threadID = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.THREAD_ID));
            String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            String person = cursor.getString(cursor.getColumnIndex(Telephony.Sms.PERSON));
            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
            int subID = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID));
            int readStatus= cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ));
            int status = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.STATUS));
            int type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE));
            String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));

            st += "id:" + id + "\n";
            st += "threadID:" + threadID + "\n";
            st += "address:" + address + "\n";
            st += "person:" + person + "\n";
            st += "type:" + type + "\n";
            st += "readStatus:" + readStatus + "\n";
            st += "subID:" + subID + "\n";
            st += "date:" + date + "\n";
            st += "status:" + status + "\n";
            st += "body:" + body + "\n";


            message.set_id(id);
            message.setThreadID(threadID);
            message.setAddress(address);
            message.setPerson(person);
            message.setDate(date);
            message.setSubID(subID);
            message.setReadStatus(readStatus);
            message.setStatus(status);
            message.setType(type);
            message.setBody(body);

            datas.add(message);

        }
        st += "----------------------------------------------- \n";
        //  Log.d(TAG,"queryDetailInformation  st="+st);
        cursor.close();
    }

    private void updateData(){
        Log.d(TAG,"updateData  size="+datas.size());
        adapter = new SmsDetailAdapter(datas,this);
        mRecyclerView.setAdapter(adapter);
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
