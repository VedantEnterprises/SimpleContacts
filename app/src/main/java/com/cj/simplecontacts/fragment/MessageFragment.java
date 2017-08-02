package com.cj.simplecontacts.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;
import com.cj.simplecontacts.adapter.CallRecordAdapter;
import com.cj.simplecontacts.adapter.SmsAdapter;
import com.cj.simplecontacts.enity.Message;

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

public class MessageFragment extends Fragment {
    private final static String TAG = "MessageFragment";
    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private Context context;
    private IndexActivity indexActivity;
    private ContentResolver resolver;
    private ArrayList<Message> datas = new ArrayList<>();
    private int sum = 0;//总的联系人
    private SmsAdapter adapter;
    private Handler handler = new Handler();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "MessageFragment onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "MessageFragment onCreateView");
        View view = inflater.inflate(R.layout.message_fragment, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_sms);
        setUpRecyclerView();

        MessageFragment.MyContentObserver contentObserver = new MessageFragment.MyContentObserver(handler);
        resolver.registerContentObserver(Uri.parse("content://sms"), true, contentObserver);

        return view;
    }

    private void setUpRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new SmsAdapter(datas, context);
        mRecyclerView.setAdapter(adapter);

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


    private Cursor getSmsCursor(){
        // Log.d(TAG,"getSmsCursor()");
        String sortString = Telephony.Sms.DEFAULT_SORT_ORDER;//sort by  [A-Z]
        Uri uri = Uri.parse("content://sms");
        String[] projection = {
                Telephony.Sms._ID,
                Telephony.Sms.THREAD_ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.PERSON,
                Telephony.Sms.DATE,
                Telephony.Sms.READ,
                Telephony.Sms.SUBSCRIPTION_ID,
                Telephony.Sms.STATUS,
                Telephony.Sms.TYPE,
                Telephony.Sms.BODY};
        Cursor c = resolver.query(uri, projection, null, null, sortString);
        return c;
    }



    private void queryDetailInformation() {
        Log.d(TAG, "queryDetailInformation()");
        String st = "";
        Cursor cursor = getSmsCursor();
        if (cursor == null) {
            return;
        }
        st += "----------------------------------------------- \n";
        sum = cursor.getCount();
        while(cursor.moveToNext()){
            Message message = new Message();
            int id = cursor.getInt(cursor.getColumnIndex(Telephony.Sms._ID));
            int threadID = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.THREAD_ID));
            String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            String person = cursor.getString(cursor.getColumnIndex(Telephony.Sms.PERSON));
            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
            int readStatus = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID));
            int subID = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ));
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
            message.setReadStatus(readStatus);
            message.setStatus(status);
            message.setType(type);
            message.setBody(body);
            if(!datas.contains(message)){
                datas.add(message);
            }
        }
        st += "----------------------------------------------- \n";
        //  Log.d(TAG,"queryDetailInformation  st="+st);
        cursor.close();
    }

    private void updateData(){
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, "onChange  selfChange=" + selfChange);
            //先查数据库总联系人人数,如果没有改变没必要去重新查询所有联系人  因为打电话 这个通知也会来
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            notifyDataChange();
            Log.d(TAG, "onChange  uri=" + uri.toString() + "  selfChange" + selfChange);
        }
    }

    private void notifyDataChange(){
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        // Log.d(TAG,"notifyDataChange Observable create  thread:"+Thread.currentThread().getName());
                        Cursor cursor = getSmsCursor();
                        if(cursor != null){
                            observableEmitter.onNext(cursor.getCount());
                        }
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
                        //Log.d(TAG,"notifyDataChange onNext:value="+value.intValue());
                        // Log.d(TAG,"notifyDataChange onNext  thread:"+Thread.currentThread().getName());

                        if(value.intValue() != sum){//如果人数有变化
                            datas.clear();
                            querySmsFromDB();
                        }else{
                            Log.d(TAG,"notifyDataChange 信息没有变化");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        // Log.d(TAG,"notifyDataChange  onComplete");
                        // Log.d(TAG,"notifyDataChange onComplete thread:"+Thread.currentThread().getName());

                    }
                });
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "MessageFragment onAttach  activity");
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "MessageFragment onAttach context");
        super.onAttach(context);
        this.context = context;
        this.indexActivity = (IndexActivity) context;
        resolver = context.getContentResolver();
        querySmsFromDB();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "MessageFragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_message, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG,"onOptionsItemSelected");

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_msg_clean) {
            Toast.makeText(getActivity(),"短信清理",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_msg_recycler) {
            Toast.makeText(getActivity(),"信息回收站",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_msg_collect) {
            Toast.makeText(getActivity(),"信息收藏",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_msg_interception) {
            Toast.makeText(getActivity(),"拦截信息",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_msg_unread) {
            Toast.makeText(getActivity(),"未读信息",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_msg_settings) {
            Toast.makeText(getActivity(),"信息设置",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_msg_new) {
            Toast.makeText(getActivity(),"新建信息",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "MessageFragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "MessageFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "MessageFragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "MessageFragment onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MessageFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "MessageFragment onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "MessageFragment onDestroyView");
        super.onDestroyView();
    }


}
