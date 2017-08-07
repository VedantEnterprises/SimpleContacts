package com.cj.simplecontacts.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.BaseApplication;
import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;
import com.cj.simplecontacts.SmsActivity;
import com.cj.simplecontacts.adapter.CallRecordAdapter;
import com.cj.simplecontacts.adapter.SmsAdapter;
import com.cj.simplecontacts.enity.CallRecord;
import com.cj.simplecontacts.enity.Contact;
import com.cj.simplecontacts.enity.Message;
import com.cj.simplecontacts.enity.NumAttribution;
import com.cj.simplecontacts.tool.Constant;
import com.cj.simplecontacts.tool.NumberUtil;
import com.cj.simplecontacts.tool.SmsComparator;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenjun on 2017/6/a11.
 */

public class MessageFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "MessageFragment";
    private RecyclerView mRecyclerView;
    private EditText search_et;
    private TextView num_sms_tv;
    private TextView cancel_search_tv;
    private ImageView search_clean_iv;
    private ImageView search_iv;

    private LinearLayoutManager mLayoutManager;
    private Context context;
    private IndexActivity indexActivity;
    private ContentResolver resolver;
    private ArrayList<Message> datas = new ArrayList<>();
    private ArrayList<Message> messages = new ArrayList<>();
    private int sum = 0;//总的联系人
    private SmsAdapter adapter;
    private Handler handler = new Handler();
    private boolean isAllSelected = false;

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
        initViews(view);

        setUpRecyclerView();

        MessageFragment.MyContentObserver contentObserver = new MessageFragment.MyContentObserver(handler);
        resolver.registerContentObserver(Uri.parse("content://sms"), true, contentObserver);
        setListener();
        return view;
    }

    private void initViews(View view) {
        search_et = (EditText) view.findViewById(R.id.search_et);
        num_sms_tv = (TextView) view.findViewById(R.id.num_sms_tv);
        cancel_search_tv = (TextView) view.findViewById(R.id.cancel_tv);
        search_clean_iv = (ImageView) view.findViewById(R.id.search_clean_iv);
        search_iv = (ImageView) view.findViewById(R.id.search_iv);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_sms);
    }

    private void setUpRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new SmsAdapter(datas, context);
        mRecyclerView.setAdapter(adapter);

        adapter.setReclerViewItemListener(new SmsAdapter.ReclerViewItemListener() {
            @Override
            public void onItemClick(Message m) {
                HideSoftInput();
                Toast.makeText(context,"查看信息对话",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SmsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onLongClick(Message m, View v) {
                HideSoftInput();
                indexActivity.showToolbar();
                indexActivity.showActionMode(Constant.MESSAGE_FRAGMENT);
                hideSearchBarElement();
                isAllSelected = false;
                showPop(v);
            }

            @Override
            public void onItemChecked(Message m, View v) {
                int checkedCount = adapter.getCheckedCount();
                isAllSelected = checkedCount == adapter.getItemCount();
                indexActivity.notifyCheckedItem(checkedCount,isAllSelected,Constant.MESSAGE_FRAGMENT);
                HideSoftInput();
                notifyPop(checkedCount);
            }
        });

        adapter.setAllItemChecked(false);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                HideSoftInput();

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d(TAG,"RecyclerView  onScrolled");
            }
        });


    }
    private void setListener() {
        search_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "onFocusChange  hasFocus == true");
                    if(adapter != null && !adapter.isShowCheckBox()){
                        indexActivity.hideToolbar();
                    }
                    num_sms_tv.setVisibility(View.GONE);
                    cancel_search_tv.setVisibility(View.VISIBLE);
                    search_iv.setVisibility(View.VISIBLE);
                } else {
                    indexActivity.showToolbar();
                    num_sms_tv.setVisibility(View.VISIBLE);
                    cancel_search_tv.setVisibility(View.GONE);
                    search_iv.setVisibility(View.GONE);
                }
            }
        });
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(TAG, "onTextChanged");
                String string = search_et.getText().toString();
                if (TextUtils.isEmpty(string)) {
                    search_iv.setVisibility(View.GONE);
                    search_clean_iv.setVisibility(View.GONE);
                } else {
                    search_iv.setVisibility(View.VISIBLE);
                    search_clean_iv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString().trim();
                //Log.d(TAG, "afterTextChanged  s=" + key);
              
                searchSmsByKey(key);
            }
        });

        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick  search_et");
                search_et.setCursorVisible(true);
                if(adapter != null && !adapter.isShowCheckBox()){
                    indexActivity.hideToolbar();

                }
                num_sms_tv.setVisibility(View.GONE);
                cancel_search_tv.setVisibility(View.VISIBLE);
                search_iv.setVisibility(View.VISIBLE);
            }
        });

        cancel_search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexActivity.showToolbar();
                hideSearchBarElement();
                HideSoftInput();
            }
        });

        search_clean_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setText("");
            }
        });


    }
    
    private void searchSmsByKey(String key){
        ArrayList<Message> temp = new ArrayList();
        if(!TextUtils.isEmpty(key)){
            mLayoutManager.scrollToPositionWithOffset(0,0);

            for (int i = 0; i < datas.size(); i++) {
                Message message = datas.get(i);
                if (message != null) {
                    String person = message.getPerson();
                    String body = message.getBody();
                    String address = message.getAddress();
                    ArrayList<Message> list = message.getList();
                    if((person!= null && person.contains(key)) || body.contains(key) || address.contains(key)){
                        temp.add(message);
                    }else{
                        for(int j = 1; j < list.size(); j++){
                            Message m = list.get(j);
                            String p = m.getPerson();
                            String b = m.getBody();
                            String a = m.getAddress();
                            if((p!= null && p.contains(key)) || b.contains(key) || a.contains(key)){
                                m.setList(list);
                                temp.add(m);
                                break;
                            }
                        }
                    }
                }
            }
            if (adapter != null) {
                String str = temp.size() > 0 ? key : "";
                adapter.setKey(str);
                SmsComparator sc = new SmsComparator();
                Collections.sort(temp,sc);
                adapter.setList(temp);
                adapter.notifyDataSetChanged();
            }
        }else{
            if (adapter != null) {
                temp = null;
                adapter.setKey("");
                adapter.setList(datas);
                adapter.notifyDataSetChanged();
            }
        }
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
                        searchNameFromDB();
                    }
                });
    }

    private void searchNameFromDB(){
        Log.d(TAG,"queryNumberAttribution");
        Observable.fromIterable(messages)
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        // Log.d(TAG,"queryNumberAttribution doOnNext  accept:"+Thread.currentThread().getName());
                        String address = message.getAddress();
                        if(address.startsWith("10")){
                            return;
                        }
                        //去数据库里查询
                        String s = "";
                        if(!address.startsWith("+86")){
                            s ="+86"+address;
                        }else{
                            s = address;
                        }
                        Cursor phone = resolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER+" = ?",
                                new String[]{s}, null);
                        if(phone.moveToFirst()){
                            String person= phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            message.setPerson(person);
                        }
                        phone.close();

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Message>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Message message) {
                        if(message.getPerson()!= null && adapter != null){
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        // Log.d(TAG,"查询完成");

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

            if(address.startsWith("10")){
                String name = NumberUtil.getNameByNum(address);
                if(!TextUtils.isEmpty(name)){
                    person = name;
                }
            }
            message.set_id(id);
            message.setThreadID(threadID);
            message.setAddress(address);
            message.setPerson(person);
            message.setDate(date);
            message.setReadStatus(readStatus);
            message.setStatus(status);
            message.setType(type);
            message.setBody(body);

            messages.add(message);
            if(!datas.contains(message)){
                message.getList().add(message);
                datas.add(message);
            }else{
                int i = datas.indexOf(message);
                Message m = datas.get(i);
                ArrayList<Message> list = m.getList();
                list.add(message);
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

    public boolean isAllSelected(){
        return this.isAllSelected;
    }

    public void setAllSelected(boolean isAllSelected){
        this.isAllSelected = isAllSelected;//isSelectNone  fasle  当前已经全部选中
        adapter.setAllItemChecked(isAllSelected);
    }

    public void hideCheckBox(){
        if(adapter != null){
            adapter.setShowCheckBox(false);
            adapter.setAllItemChecked(false);
        }
        if(popupWindow != null){
            popupWindow.dismiss();
        }
    }


    private PopupWindow popupWindow;
    private View popupView;
    private FrameLayout fl1;
    private FrameLayout fl2;
    private FrameLayout fl3;
    private FrameLayout fl4;
    private TextView add_blacklist;
    private TextView much_reply;
    private TextView mark_read;
    private TextView delete;

    private void notifyPop(int checkedCount){
        if(checkedCount>0){
            fl1.setClickable(true);
            fl2.setClickable(true);
            fl3.setClickable(true);
            fl4.setClickable(true);

            Drawable removeDrawable = getResources().getDrawable(R.drawable.bottom_remove_icon);
            removeDrawable.setBounds(0, 0, removeDrawable.getMinimumWidth(), removeDrawable.getMinimumHeight());
            add_blacklist.setCompoundDrawables(null,removeDrawable,null,null);
            add_blacklist.setTextColor(getResources().getColor(R.color.pop_text_color_enable));

            Drawable msgDrawable = getResources().getDrawable(R.drawable.btn_muchreply);
            msgDrawable.setBounds(0, 0, msgDrawable.getMinimumWidth(), msgDrawable.getMinimumHeight());
            much_reply.setCompoundDrawables(null,msgDrawable,null,null);
            much_reply.setTextColor(getResources().getColor(R.color.pop_text_color_enable));

            Drawable shareDrawable = getResources().getDrawable(R.drawable.common_mark_common);
            shareDrawable.setBounds(0, 0, shareDrawable.getMinimumWidth(), shareDrawable.getMinimumHeight());
            mark_read.setCompoundDrawables(null,shareDrawable,null,null);
            mark_read.setTextColor(getResources().getColor(R.color.pop_text_color_enable));

            Drawable delDrawable = getResources().getDrawable(R.drawable.mca_bottom_item_del);
            delDrawable.setBounds(0, 0, delDrawable.getMinimumWidth(), delDrawable.getMinimumHeight());
            delete.setCompoundDrawables(null,delDrawable,null,null);
            delete.setTextColor(getResources().getColor(R.color.pop_text_color_enable));
        }else{
            fl1.setClickable(false);
            fl2.setClickable(false);
            fl3.setClickable(false);
            fl4.setClickable(false);

            Drawable removeDrawable = getResources().getDrawable(R.drawable.bottom_remove_icon_disabled);
            removeDrawable.setBounds(0, 0, removeDrawable.getMinimumWidth(), removeDrawable.getMinimumHeight());
            add_blacklist.setCompoundDrawables(null,removeDrawable,null,null);
            add_blacklist.setTextColor(getResources().getColor(R.color.pop_text_color_disable));

            Drawable msgDrawable = getResources().getDrawable(R.drawable.btn_muchreply_disabled);
            msgDrawable.setBounds(0, 0, msgDrawable.getMinimumWidth(), msgDrawable.getMinimumHeight());
            much_reply.setCompoundDrawables(null,msgDrawable,null,null);
            much_reply.setTextColor(getResources().getColor(R.color.pop_text_color_disable));

            Drawable shareDrawable = getResources().getDrawable(R.drawable.common_mark_common_disabled);
            shareDrawable.setBounds(0, 0, shareDrawable.getMinimumWidth(), shareDrawable.getMinimumHeight());
            mark_read.setCompoundDrawables(null,shareDrawable,null,null);
            mark_read.setTextColor(getResources().getColor(R.color.pop_text_color_disable));

            Drawable delDrawable = getResources().getDrawable(R.drawable.mca_bottom_item_del_disabled);
            delDrawable.setBounds(0, 0, delDrawable.getMinimumWidth(), delDrawable.getMinimumHeight());
            delete.setCompoundDrawables(null,delDrawable,null,null);
            delete.setTextColor(getResources().getColor(R.color.pop_text_color_disable));
        }
        popupWindow.update();
    }

    private void showPop(View view){
        //show popwindow
        popupView = indexActivity.getLayoutInflater().inflate(R.layout.sms_popwindow, null);
        fl1 = (FrameLayout) popupView.findViewById(R.id.fl1);
        fl2 = (FrameLayout) popupView.findViewById(R.id.fl2);
        fl3 = (FrameLayout) popupView.findViewById(R.id.fl3);
        fl4 = (FrameLayout) popupView.findViewById(R.id.fl4);



        add_blacklist = (TextView) popupView.findViewById(R.id.add_blacklist);
        much_reply = (TextView) popupView.findViewById(R.id.much_reply);
        mark_read = (TextView) popupView.findViewById(R.id.mark_read);
        delete = (TextView) popupView.findViewById(R.id.delete);

        fl1.setOnClickListener(this);
        fl2.setOnClickListener(this);
        fl3.setOnClickListener(this);
        fl4.setOnClickListener(this);

        fl1.setClickable(false);
        fl2.setClickable(false);
        fl3.setClickable(false);
        fl4.setClickable(false);

        popupWindow  = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl1:
                Toast.makeText(context,"加入黑名单",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fl2:
                Toast.makeText(context,"批量回复",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fl3:
                Toast.makeText(context,"标记为已读",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fl4:
                Toast.makeText(context,"删除",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
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

    public void notifyDataChange(){
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        // Log.d(TAG,"notifyDataChange Observable create  thread:"+Thread.currentThread().getName());
                        Cursor cursor = getSmsCursor();
                        if(cursor != null){
                            observableEmitter.onNext(cursor.getCount());
                        }
                        cursor.close();
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
                            messages.clear();
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

    public  void onHide(){
        hideSearchBarElement();
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

    public void scrollToFirstPosition(){
        if(mLayoutManager != null){
            mLayoutManager.scrollToPositionWithOffset(0,0);
        }
    }
    /**
     * 按back key 如果此时toolbar is hide   then show toolbar meanwhile hide some view
     */
    public void hideSearchBarElement(){
        if(num_sms_tv == null|| cancel_search_tv == null || search_iv == null || search_clean_iv == null|| search_et == null){
            return;
        }

        num_sms_tv.setVisibility(View.VISIBLE);
        cancel_search_tv.setVisibility(View.GONE);
        search_iv.setVisibility(View.GONE);
        search_clean_iv.setVisibility(View.GONE);
        search_et.setText("");
        search_et.setCursorVisible(false);
        scrollToFirstPosition();
    }



    /**
     * 隐藏软键盘
     */
    public void HideSoftInput(){
        InputMethodManager imm =  (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(indexActivity.getWindow().getDecorView().getWindowToken(),
                    0);
        }
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
