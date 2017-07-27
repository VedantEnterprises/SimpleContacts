package com.cj.simplecontacts.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cj.simplecontacts.R;
import com.cj.simplecontacts.adapter.CallRecordAdapter;
import com.cj.simplecontacts.enity.CallRecord;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenjun on 2017/6/a11.
 */

public class DialFragment extends Fragment {
    private final static String TAG = "DialFragment";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private Context context;
    private ContentResolver resolver;
    private ArrayList<CallRecord> datas = new ArrayList();
    private CallRecordAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "DialFragment onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "DialFragment onCreateView");
        View view = inflater.inflate(R.layout.dial_fragment, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_call_record);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new CallRecordAdapter(datas,context);
        mRecyclerView.setAdapter(adapter);
        return view;
    }


    private void queryCallRecordFromDB() {
        Log.d(TAG, "queryCallRecordFromDB");

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                queryDetailInformation();
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        updateData();
                    }
                });
    }


    private void queryDetailInformation() {
        Log.d(TAG, "queryDetailInformation()");
        String st = "";
        String sortString = CallLog.Calls.DEFAULT_SORT_ORDER;
        // CallLog.Calls.PHONE_ACCOUNT_ID  subscription_id  3=卡2  2 =卡1
        // CallLog.Calls.CACHED_NAME,  //姓名
        //CallLog.Calls.NUMBER,    //号码
        // CallLog.Calls.TYPE,  //3呼入/呼出(2)/未接   1呼入接听  2呼出接听了
        // CallLog.Calls.DURATION  响铃时间或接听时间
        // CallLog.Calls.DATE  拨打的时间
        // CallLog.Calls._ID
        String[] projection = {
                CallLog.Calls._ID,
                CallLog.Calls.PHONE_ACCOUNT_ID,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION,
                CallLog.Calls.DATE};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor =
                resolver.query(CallLog.Calls.CONTENT_URI, //系统方式获取通讯录存储地址
                        projection, null, null, sortString);
        st += "----------------------------------------------- \n";
        while(cursor.moveToNext()){
            CallRecord callRecord = new CallRecord();
            String id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
            String accountId = cursor.getString(cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

            // 对手机号码进行预处理（去掉号码前的+86、首尾空格、“-”号等）
            number = number.replaceAll("^(\\+86)", "");
            number = number.replaceAll("^(86)", "");
            number = number.replaceAll("-", "");
            number = number.replaceAll(" ", "");
            number = number.trim();

            st += "id:" + id + "\n";
            st += "accountId:" + accountId + "\n";
            st += "name:" + name + "\n";
            st += "number:" + number + "\n";
            st += "type:" + type + "\n";
            st += "duration:" + duration + "\n";
            st += "date:" + date + "\n";

            callRecord.setType(type);
            callRecord.setName(name);
            callRecord.setNumber(number);
            callRecord.setAccountId(accountId);
            callRecord.setDate(date);
            callRecord.setDuration(duration);
            callRecord.setId(id);
            datas.add(callRecord);
        }
        st += "----------------------------------------------- \n";
        //  Log.d(TAG,"query  st="+st);
        cursor.close();
    }

    private void updateData() {
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "DialFragment onAttach  activity");
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "DialFragment onAttach context");
        super.onAttach(context);
        this.context = context;
        resolver = context.getContentResolver();
        queryCallRecordFromDB();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "DialFragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_dail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG,"onOptionsItemSelected");

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dial_clear) {
            Toast.makeText(getActivity(),"清空通话记录",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_dial_interception) {
            Toast.makeText(getActivity(),"拦截通话",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_dial_missed) {
            Toast.makeText(getActivity(),"未接来电",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_dial_setting) {
            Toast.makeText(getActivity(),"通话设置",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onStart() {
        Log.d(TAG, "DialFragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "DialFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "DialFragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "DialFragment onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DialFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "DialFragment onDetach");
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        Log.d(TAG, "DialFragment onDestroyView");
        super.onDestroyView();
    }


}
