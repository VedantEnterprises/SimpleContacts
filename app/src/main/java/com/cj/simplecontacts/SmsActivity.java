package com.cj.simplecontacts;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cj.simplecontacts.adapter.SendSmsFunctionAdapter;
import com.cj.simplecontacts.adapter.SmsDetailAdapter;
import com.cj.simplecontacts.enity.Message;
import com.cj.simplecontacts.enity.SmsFunction;
import com.cj.simplecontacts.tool.CommonTool;
import com.cj.simplecontacts.tool.ContactTool;
import com.cj.simplecontacts.tool.NumberUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SmsActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "SmsActivity";
    /* 自定义ACTION常数，作为广播的Intent Filter识别常数 */
    private static String SMS_SEND_ACTION = "SMS_SEND_ACTION";

    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private RecyclerView mRecyclerView;
    private RecyclerView sendFuctionRv;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Message> datas = new ArrayList<>();
    private ArrayList<SmsFunction> list = new ArrayList<>();
    private SmsDetailAdapter adapter;
    private int threadID = -1;
    private Message message;
    private ImageButton collapseSmsFunction;
    private CheckBox selectSim;
    private ImageView sendSms;
    private LinearLayout ll;
    private EditText smsEdit;
    private int heightPixels;
    private SMSSendResultReceiver mReceiver;
    private String phoneNum;
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private boolean mHiddenActionIsOver = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Intent intent = getIntent();
        if (intent != null) {
            message = intent.getParcelableExtra("sms");
        }
        initView();
        setUpSupportActionBar();
        setUpRecyclerView();

        if (message != null) {
            threadID = message.getThreadID();
            querySmsFromDB();
            Log.d(TAG, "onCreate  threadID=" + threadID);

        } else {
            Log.d(TAG, "message is null ");
        }
        addListener();
        createAnimation();



    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_sms);
        sendFuctionRv = (RecyclerView) findViewById(R.id.sms_send_function);
        collapseSmsFunction = (ImageButton) findViewById(R.id.collapse_sms_function);
        selectSim = (CheckBox) findViewById(R.id.select_sim);
        sendSms = (ImageView) findViewById(R.id.send_sms);
        ll = (LinearLayout) findViewById(R.id.ll);
        smsEdit = (EditText) findViewById(R.id.sms_edit);

        sendSms.setEnabled(false);


        heightPixels = CommonTool.getCommonTool(this).getHeightPixels();
    }

    private void addListener() {
        collapseSmsFunction.setOnClickListener(this);
        selectSim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int y = (int) ll.getY();
                Log.d(TAG, "onCheckedChanged   y="+y);
                if (isChecked) {
                    Toast t2 = Toast.makeText(SmsActivity.this, "卡2", Toast.LENGTH_SHORT);
                    t2.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 60, heightPixels-y+10);
                    t2.show();
                } else {
                    Toast t1 = Toast.makeText(SmsActivity.this, "卡1", Toast.LENGTH_SHORT);
                    t1.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 60, heightPixels-y+10);
                    t1.show();
                }
            }
        });
        sendSms.setOnClickListener(this);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideSoftInput();
                if(isSendFunctionRvVisible()){
                    hideSmsFunction();
                }
                return false;
            }
        });
        smsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick  search_et");
                smsEdit.setCursorVisible(true);
                if(isSendFunctionRvVisible()){
                   // hideSmsFunction();
                    sendFuctionRv.setVisibility(View.GONE);
                }
            }
        });
        smsEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "onFocusChange  hasFocus == true");
                    if(isSendFunctionRvVisible()){
                        //hideSmsFunction();
                        sendFuctionRv.setVisibility(View.GONE);
                    }
                } else {

                }
            }
        });

        smsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().trim();
                if(TextUtils.isEmpty(trim)){
                    sendSms.setEnabled(false);
                    sendSms.setImageDrawable(SmsActivity.this.getResources().getDrawable(R.drawable.send_message_gray));
                }else{
                    sendSms.setImageDrawable(SmsActivity.this.getResources().getDrawable(R.drawable.send_message_normal_green));
                    sendSms.setEnabled(true);
                }
            }
        });
    }

    private void setUpSupportActionBar() {
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        supportActionBar.setIcon(R.drawable.default_contact_head_icon);
        if (message != null) {
            String person = message.getPerson();
            if (TextUtils.isEmpty(person)) {
                supportActionBar.setTitle("陌生人");
            } else {
                supportActionBar.setTitle(person);
            }
            phoneNum = message.getAddress();
            supportActionBar.setSubtitle(phoneNum);
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



    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(SMS_SEND_ACTION);
        mReceiver = new SMSSendResultReceiver();
        registerReceiver(mReceiver, mFilter);
    }

    private void setUpRecyclerView() {

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);

        sendFuctionRv.setLayoutManager(gridLayoutManager);
        updateList();
        SendSmsFunctionAdapter smsFunctionAdapter = new SendSmsFunctionAdapter(list, this);
        sendFuctionRv.setAdapter(smsFunctionAdapter);

    }

    private void updateList() {
        list.clear();
        SmsFunction timing = new SmsFunction();
        timing.setName("定时");
        timing.setDrawable(getResources().getDrawable(R.drawable.timing_clock_bg));

        SmsFunction emotion = new SmsFunction();
        emotion.setName("表情");
        emotion.setDrawable(getResources().getDrawable(R.drawable.emoticon_enter_bg));

        SmsFunction feature = new SmsFunction();
        feature.setName("精选");
        feature.setDrawable(getResources().getDrawable(R.drawable.featured_message_bg));

        SmsFunction picture = new SmsFunction();
        picture.setName("图片");
        picture.setDrawable(getResources().getDrawable(R.drawable.picture_bg));

        SmsFunction card = new SmsFunction();
        card.setName("名片");
        card.setDrawable(getResources().getDrawable(R.drawable.card_bg));

        SmsFunction location = new SmsFunction();
        location.setName("位置");
        location.setDrawable(getResources().getDrawable(R.drawable.location_bg));

        SmsFunction useful = new SmsFunction();
        useful.setName("常用");
        useful.setDrawable(getResources().getDrawable(R.drawable.useful_bg));

        list.add(timing);
        list.add(emotion);
        list.add(feature);
        list.add(picture);
        list.add(card);
        list.add(location);
        list.add(useful);
    }

    private boolean isSendFunctionRvVisible() {
        return sendFuctionRv.getVisibility() == View.VISIBLE;
    }



    private void createAnimation() {
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(400);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        mHiddenAction.setDuration(400);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sendFuctionRv.setVisibility(View.GONE);
                mHiddenActionIsOver = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



    private void showSmsFunction() {
        if (sendFuctionRv.getVisibility() == View.GONE) {
            sendFuctionRv.startAnimation(mShowAction);
            sendFuctionRv.setVisibility(View.VISIBLE);
        }
    }

    private void hideSmsFunction() {
        if (!mHiddenActionIsOver) {
            return;
        }
        if (sendFuctionRv.getVisibility() == View.VISIBLE) {
            sendFuctionRv.startAnimation(mHiddenAction);
            mHiddenActionIsOver = false;
        }
    }

    private void querySmsFromDB() {
        Log.d(TAG, "querySmsFromDB");
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
        datas.clear();
        String st = "";
        Cursor cursor = ContactTool.getSmsCursorByThreadID(getContentResolver(), threadID);
        if (cursor == null) {
            return;
        }
        st += "----------------------------------------------- \n";

        while (cursor.moveToNext()) {
            Message message = new Message();

            int id = cursor.getInt(cursor.getColumnIndex(Telephony.Sms._ID));
            int threadID = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.THREAD_ID));
            String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            String person = cursor.getString(cursor.getColumnIndex(Telephony.Sms.PERSON));
            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
            int subID = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID));
            int readStatus = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ));
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

    private void updateData() {
        Log.d(TAG, "updateData  size=" + datas.size());
        adapter = new SmsDetailAdapter(datas, this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_sms_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        int id = item.getItemId();

        if (id == R.id.clear_sms) {
            Toast.makeText(this, "清空信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.contact_detail) {
            Toast.makeText(this, "联系人详情", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.add_block_list) {
            Toast.makeText(this, "加入黑名单", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.more) {
            Toast.makeText(this, "更多", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.call) {
            Toast.makeText(this, "拨打电话", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isSendFunctionRvVisible()){
            hideSmsFunction();
            return;
        }

        super.onBackPressed();
    }

    /**
     * 隐藏软键盘
     */
    public void HideSoftInput(){
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collapse_sms_function:
                if(isSendFunctionRvVisible()){
                    hideSmsFunction();
                }else{
                    HideSoftInput();
                    showSmsFunction();
                }

                break;
            case R.id.send_sms:
                String content = smsEdit.getText().toString().trim();
                sendSMS(content);
                break;
            default:
                break;
        }
    }


    /**
     * 直接调用短信接口发短信    如果群发可以循环调用
     * @param
     * @param message
     */
    public void sendSMS(String message){
        //获取短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
         /* 建立自定义Action常数的Intent(给PendingIntent参数之用) */
        Class SMClass=SmsManager.class; //通过反射查到了SmsManager有个叫做mSubId的属性
        Intent itSend = new Intent(SMS_SEND_ACTION);
        Field field= null;
        try {
            field = SMClass.getDeclaredField("mSubId");
            field.setAccessible(true);
            field.set(smsManager,selectSim.isChecked()?3:2);//2是卡1  3卡2
        } catch (Exception e) {
            e.printStackTrace();
        }
          //我的手机上设定1就是卡1电信卡，设定0就是卡2联通卡，也没试过别的卡

         /* sentIntent参数为传送后接受的广播信息PendingIntent */
        PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNum, null, text, mSendPI, null);
        }
        smsEdit.setText("");
    }


    public class SMSSendResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SMS_SEND_ACTION))
            {
                try
                {
                    switch(getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(SmsActivity.this, "发送信息成功", Toast.LENGTH_SHORT).show();
                            querySmsFromDB();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(SmsActivity.this, "发送短信失败", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
                catch(Exception e)
                {

                    e.getStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
