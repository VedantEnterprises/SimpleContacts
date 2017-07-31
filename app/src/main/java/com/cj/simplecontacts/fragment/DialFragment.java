package com.cj.simplecontacts.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.BaseApplication;
import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;
import com.cj.simplecontacts.adapter.CallRecordAdapter;
import com.cj.simplecontacts.enity.CallRecord;
import com.cj.simplecontacts.enity.Contact;
import com.cj.simplecontacts.enity.NumAttribution;
import com.cj.simplecontacts.enity.NumAttributionDao;
import com.cj.simplecontacts.enity.Number;
import com.cj.simplecontacts.tool.NumberUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenjun on 2017/6/a11.
 */

public class DialFragment extends Fragment {
    private final static String TAG = "DialFragment";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IndexActivity indexActivity;
    private Context context;
    private ContentResolver resolver;
    private ArrayList<CallRecord> datas = new ArrayList();
    private CallRecordAdapter adapter;
    private ArrayList<String> nums = new ArrayList<>();//数据库中没有归属地的号码
    private Handler handler = new Handler();
    private int sum;//通话记录总条数

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
        setUpRecyclerView();
        DialFragment.MyContentObserver contentObserver = new DialFragment.MyContentObserver(handler);
        resolver.registerContentObserver(CallLog.Calls.CONTENT_URI, true, contentObserver);
        return view;
    }


    private void setUpRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new CallRecordAdapter(datas, context);
        mRecyclerView.setAdapter(adapter);
        adapter.setReclerViewItemListener(new CallRecordAdapter.ReclerViewItemListener() {
            @Override
            public void onItemClick(int position) {
                CallRecord callRecord = datas.get(position);
                boolean multiSim = NumberUtil.isMultiSim(context);
                if(multiSim){
                    showSelectSIMDialog(callRecord.getNumber());
                }else{
                    NumberUtil.call(context,0,callRecord.getNumber());
                }
            }

            @Override
            public void onLongClick(int position, View v) {

            }

            @Override
            public void onItemChecked(int position, View v) {

            }
        });
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
                        queryNumberAttribution();
                    }
                });
    }

    /**
     * 如果是双卡用户 弹出对话框 选择用哪张卡拨打
     * @param num
     *
     */
    private void showSelectSIMDialog(final String num){
        final Dialog mDialog = new Dialog(context,R.style.DialogTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_select_sim, null);
        TextView cancelCall = (TextView) view.findViewById(R.id.cancel_call);
        LinearLayout callBySim1 = (LinearLayout) view.findViewById(R.id.call_by_sim1);
        LinearLayout callBySim2 = (LinearLayout) view.findViewById(R.id.call_by_sim2);
        callBySim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,"用卡1拨打",Toast.LENGTH_SHORT).show();
                NumberUtil.call(context,0,num);
                mDialog.dismiss();
            }
        });
        callBySim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(context,"用卡2拨打",Toast.LENGTH_SHORT).show();
                NumberUtil.call(context,1,num);
                mDialog.dismiss();
            }
        });

        cancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();

            }
        });

        WindowManager m = indexActivity.getWindowManager();
        Display d = m.getDefaultDisplay();
        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width =  (int) (d.getWidth() * 0.75);//宽度高可设置具体大小
        lp.height =  WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        mDialog.show();
    }


    private void queryDetailInformation() {
        Log.d(TAG, "queryDetailInformation()");
        String st = "";
        Cursor cursor = getCallLogCursor();
        if(cursor == null){
            return;
        }
        st += "----------------------------------------------- \n";
        sum = cursor.getCount();
        while (cursor.moveToNext()) {
            CallRecord callRecord = new CallRecord();
            String name = "";
            String id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
            String accountId = cursor.getString(cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            String location = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));

            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

            if(TextUtils.isEmpty(name)){
                //拨打电话  马上挂掉   不会有name
                Cursor phone = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER+" = ?",
                        new String[]{"+86"+number}, null);
                if(phone.moveToFirst()){
                    name= phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                   // Log.d(TAG, "queryDetailInformation() 数据库有 name = "+name);
                }else{
                   // Log.d(TAG, "queryDetailInformation() 数据库没有该电话话号码 ");
                }
                phone.close();
            }else{
               // Log.d(TAG, "queryDetailInformation() name is not null  "+name);
            }
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
            NumberUtil.Numbers numbers = NumberUtil.checkNumber(number);
            NumberUtil.PhoneType phoneType = numbers.getType();
            String code = numbers.getCode();
            if(NumberUtil.PhoneType.INVALIDPHONE == phoneType){//无效的
                callRecord.setNumAttr("未知归属地");

            }else {
                String attribution = "";
                List<NumAttribution> list = BaseApplication
                        .getDaoInstant()
                        .getNumAttributionDao()
                        .queryBuilder()
                        .where(NumAttributionDao.Properties.Code.eq(code),NumAttributionDao.Properties.Type.eq(phoneType.name()))
                        .list();
                if(list != null && list.size()>0){
                    NumAttribution numAttribution = list.get(0);
                    attribution = numAttribution.getAttribution();
                    //Long id = numAttribution.getId();
                    // Log.d(TAG,"accept accept  attribution:"+attribution+"  id ="+id);
                }else{
                    // Log.d(TAG,"accept db no data");
                    //attribution = getAttInfo(type,code);
                    nums.add(number);
                }
                callRecord.setNumAttr(attribution);
            }
            callRecord.setType(type);
            callRecord.setLocation(location);
            callRecord.setName(name);
            callRecord.setNumber(number);
            callRecord.setAccountId(accountId);
            callRecord.setDate(date);
            callRecord.setDuration(duration);
            callRecord.setId(id);
            if(!datas.contains(callRecord)){
                datas.add(callRecord);
            }
        }
        st += "----------------------------------------------- \n";
        //  Log.d(TAG,"query  st="+st);
        cursor.close();
    }

    private void updateData() {
        boolean multiSim = NumberUtil.isMultiSim(context);

        if (adapter != null) {
            adapter.setMultiSim(multiSim);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 归属地查询
     */
    private void queryNumberAttribution(){
        Log.d(TAG,"queryNumberAttribution");
        Observable.fromIterable(nums)
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String phoneNumber) throws Exception {
                        Log.d(TAG,"queryNumberAttribution doOnNext  accept:"+Thread.currentThread().getName());
                        NumberUtil.Numbers numbers = NumberUtil.checkNumber(phoneNumber);
                        NumberUtil.PhoneType type = numbers.getType();
                        String code = numbers.getCode();
                        if(NumberUtil.PhoneType.INVALIDPHONE == type){//无效的
                            //phoneNumber.setNumAttribution("未知归属地");
                            return;
                        }
                        Log.d(TAG,"accept  code:"+code+" type:"+type);
                        String attribution = "";
                        attribution = NumberUtil.getAttInfo(context,type,code);
                        NumAttribution numAttribution = new NumAttribution();
                        numAttribution.setCode(code);
                        numAttribution.setAttribution(attribution);
                        numAttribution.setType(type.name());
                        BaseApplication.getDaoInstant().getNumAttributionDao().insert(numAttribution);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
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
        this.indexActivity = (IndexActivity) context;
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
        Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_dail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected");

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dial_clear) {
            Toast.makeText(getActivity(), "清空通话记录", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_dial_interception) {
            Toast.makeText(getActivity(), "拦截通话", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_dial_missed) {
            Toast.makeText(getActivity(), "未接来电", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_dial_setting) {
            Toast.makeText(getActivity(), "通话设置", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private Cursor getCallLogCursor() {
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
                CallLog.Calls.GEOCODED_LOCATION,
                CallLog.Calls.DURATION,
                CallLog.Calls.DATE};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        Cursor cursor =
                resolver.query(CallLog.Calls.CONTENT_URI, //系统方式获取通讯录存储地址
                        projection, null, null, sortString);
        return cursor;
    }

    private void notifyDataChange(){
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        // Log.d(TAG,"notifyDataChange Observable create  thread:"+Thread.currentThread().getName());
                        Cursor cursor = getCallLogCursor();
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
                            nums.clear();
                            queryCallRecordFromDB();
                        }else{
                            Log.d(TAG,"notifyDataChange 人数没有变化");
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
