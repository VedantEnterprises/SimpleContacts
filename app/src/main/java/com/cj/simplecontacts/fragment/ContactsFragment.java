package com.cj.simplecontacts.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.BaseApplication;
import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;
import com.cj.simplecontacts.adapter.ContactAdapter;
import com.cj.simplecontacts.adapter.DialogNumAttAdapter;
import com.cj.simplecontacts.enity.Contact;
import com.cj.simplecontacts.enity.NumAttribution;
import com.cj.simplecontacts.enity.NumAttributionDao;
import com.cj.simplecontacts.enity.Number;
import com.cj.simplecontacts.tool.CharacterParser;
import com.cj.simplecontacts.tool.Constant;
import com.cj.simplecontacts.tool.NumberUtil;
import com.cj.simplecontacts.view.IndexSiderBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class ContactsFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "ContactsFragment";

    private EditText search_et;
    private TextView num_contacts_tv;
    private TextView tv_dialog;
    private TextView cancel_search_tv;
    private ImageView search_clean_iv;
    private ImageView search_iv;
    private IndexSiderBar indexSiderBar;
    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private CharacterParser characterParser;

    private Context context;
    private IndexActivity indexActivity;
    private ContentResolver resolver;
    private ContactAdapter adapter;

    private ArrayList<Contact> datas = new ArrayList();
    private boolean isAllSelected = false;
    private int sum = 0;//总的联系人
    private Handler handler = new Handler();
    private ArrayList<Number> nums = new ArrayList<>();//数据库中没有归属地的号码

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ContactsFragment onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ContactsFragment onCreateView");
        View view = inflater.inflate(R.layout.contacts_fragment, null);
        initViews(view);

        setUpRecyclerView();

        characterParser = CharacterParser.getInstance();

        MyContentObserver contentObserver  = new MyContentObserver(handler);
        resolver.registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,true,contentObserver);
        createTwoObject();
        addData();

        setListener();


        return view;
    }

    private Contact assistant;
    private Contact group;

    private void createTwoObject(){
        assistant = new Contact();
        assistant.setName(Constant.CONTACT_ASSISTANT);
        assistant.setContactID(Constant.CONTACT_ASSISTANT);
        assistant.setContact(false);

        group = new Contact();
        group.setName(Constant.CONTACT_GROUP);
        group.setContactID(Constant.CONTACT_GROUP);
        group.setContact(false);
    }

    private void addData(){
        if(assistant != null){
            if(!datas.contains(assistant)){
                datas.add(0,assistant);
            }
        }
        if(group != null){
            if(!datas.contains(group)){
                datas.add(1,group);
            }
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void deleteData(){
        if(assistant != null){
            if(datas.contains(assistant)){
                datas.remove(assistant);
            }
        }
        if(group != null){
            if(datas.contains(group)){
                datas.remove(group);
            }
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void initViews(View view) {
        search_et = (EditText) view.findViewById(R.id.search_et);
        num_contacts_tv = (TextView) view.findViewById(R.id.num_contacts_tv);
        cancel_search_tv = (TextView) view.findViewById(R.id.cancel_tv);
        search_clean_iv = (ImageView) view.findViewById(R.id.search_clean_iv);
        search_iv = (ImageView) view.findViewById(R.id.search_iv);
        indexSiderBar = (IndexSiderBar) view.findViewById(R.id.inddex_sider_bar);
        tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_contacts);

        num_contacts_tv.setText("共有" + sum + "个联系人");
        indexSiderBar.setTextDialog(tv_dialog);

    }

    /**
     *
     * @param contact
     */
    private void showContactDialog(final Contact contact){
        final Dialog dialog = new Dialog(context,R.style.DialogTheme);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_dialog, null);
        ImageButton dialogShare = (ImageButton) view.findViewById(R.id.dialog_share);
        ImageButton dialogSeeMore = (ImageButton) view.findViewById(R.id.dialog_see_more);
        TextView dialogName = (TextView) view.findViewById(R.id.dialog_name);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.dialog_rv);
        LinearLayout lSeeMore = (LinearLayout) view.findViewById(R.id.ll_see_more);
        TextView tvSeeMore = (TextView) view.findViewById(R.id.tv_see_more);

        final ArrayList<Number> numbers = contact.getNumbers();
       // Log.d(TAG, "showContactDialog numbers = "+numbers.size());
        if(numbers != null && numbers.size() >3){
            lSeeMore.setVisibility(View.VISIBLE);
            tvSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"查看"+contact.getName()+"更多号码",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            lSeeMore.setVisibility(View.GONE);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);
        DialogNumAttAdapter adapter = new DialogNumAttAdapter(numbers,context);
        rv.setAdapter(adapter);
        adapter.setReclerViewItemListener(new DialogNumAttAdapter.ReclerViewItemListener() {
            @Override
            public void onCallClick(int position) {
               // Toast.makeText(context,"拨打"+contact.getName()+"的号码:"+numbers.get(position).getNum(),Toast.LENGTH_SHORT).show();
                boolean multiSim = NumberUtil.isMultiSim(context);
               // Toast.makeText(context,"拨打"+contact.getName()+"的号码:"+numbers.get(position).getNum()+"  "+multiSim,Toast.LENGTH_SHORT).show();
                String num = numbers.get(position).getNum();
                if(multiSim){
                    dialog.dismiss();
                    showSelectSIMDialog(num,dialog);
                }else{
                    NumberUtil.call(context,0,num);
                }
            }

            @Override
            public void onSendMsgClick(int position) {
                //Toast.makeText(context,"给"+contact.getName()+"的号码:"+numbers.get(position).getNum()+" 发信息",Toast.LENGTH_SHORT).show();
                String num = numbers.get(position).getNum();
                Uri uri2 = Uri.parse("smsto:"+num);
                Intent intentMessage = new Intent(Intent.ACTION_VIEW,uri2);
                startActivity(intentMessage);
            }
        });

        dialogName.setText(contact.getName());

        dialogShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"分享"+contact.getName()+"的名片",Toast.LENGTH_SHORT).show();
            }
        });
        dialogSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"查看"+contact.getName()+"更多信息",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setContentView(view);

        WindowManager m = indexActivity.getWindowManager();
        Display d = m.getDefaultDisplay();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width =  (int) (d.getWidth() * 0.75);//宽度高可设置具体大小
        lp.height =  WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.show();
    }

    /**
     * 如果是双卡用户 弹出对话框 选择用哪张卡拨打
     * @param num
     * @param dialog
     */
    private void showSelectSIMDialog(final String num, final Dialog dialog){
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
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialog.show();
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




    private void setUpRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ContactAdapter(datas, context);

        adapter.setReclerViewItemListener(new ContactAdapter.ReclerViewItemListener() {
            @Override
            public void onItemClick(Contact contact) {
                HideSoftInput();
                //Contact contact = datas.get(position);

                if(contact.isContact()){
                    showContactDialog(contact);
                }else{
                    if(contact.getName().equals(Constant.CONTACT_ASSISTANT)){
                        Toast.makeText(context,"去联系人助手界面 ",Toast.LENGTH_SHORT).show();
                    }else if(contact.getName().equals(Constant.CONTACT_GROUP)){
                        Toast.makeText(context,"去我的分组界面 ",Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onItemChecked(Contact contact,View v) {
                int checkedCount = adapter.getCheckedCount();
                if(checkedCount == (adapter.isHaveNotContact()?adapter.getItemCount()-2:adapter.getItemCount())){
                    isAllSelected = true;
                }else{
                    isAllSelected = false;
                }
                indexActivity.notifyCheckedItem(checkedCount,isAllSelected,Constant.CONTACTS_FRAGMENT);
                HideSoftInput();
                notifyPop(checkedCount);
            }

            @Override
            public void onLongClick(Contact contact,View v) {
                HideSoftInput();
               // Contact contact = datas.get(position);
                if(!contact.isContact()){
                    //Toast.makeText(context,"不是联系人条目 ",Toast.LENGTH_SHORT).show();
                    return;
                }
                indexActivity.showToolbar();
                hideSearchBarElement();
                indexActivity.showActionMode(Constant.CONTACTS_FRAGMENT);
                isAllSelected = false;
                showPop(v);
            }
        });

        adapter.setAllItemChecked(false);

        mRecyclerView.setAdapter(adapter);

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
                int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                if(firstPosition >-1){
                    int section = adapter.getSectionForPosition(firstPosition);
                    if(section == 0 && (firstPosition <= 1)){
                        section = adapter.getSectionForPosition(2);
                    }
                    indexSiderBar.setChooseIndex((char)(section)+"");
                }
            }
        });

    }



    /**
     *打电话 增加联系人 删除联系人 都收到通知
     */
    private void notifyDataChange(){
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                       // Log.d(TAG,"notifyDataChange Observable create  thread:"+Thread.currentThread().getName());
                        int sum = querySum();
                        observableEmitter.onNext(new Integer(sum));
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
                            addData();
                            queryContactsFromDB();
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

    /**
     *先查询总人数,然后再详细信息
     */

    private void queryContactsFromDB(){
        Log.d(TAG,"queryContactsFromDB");
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        //Log.d(TAG,"queryContactsFromDB Observable create  thread:"+Thread.currentThread().getName());
                        int sum = querySum();
                        observableEmitter.onNext(new Integer(sum));
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
                            if(num_contacts_tv != null){

                                sum = value.intValue();
                                num_contacts_tv.setText("共有" + sum + "个联系人");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            //Log.d(TAG,"queryContactsFromDB  onComplete");
                           // Log.d(TAG,"queryContactsFromDB onComplete thread:"+Thread.currentThread().getName());
                            updateData();
                            queryNumberAttribution();
                        }
                    });
    }

    /**
     *总的联系人数
     * @return
     */

    private int  querySum(){
        Log.d(TAG,"querySum()");
        int count = 0;
        Cursor contactsCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(contactsCursor != null){
            count = contactsCursor.getCount();//get row quantity,  is  contacts quantity
            contactsCursor.close();
        }
        return count;
    }

    /**
     *查询详细信息
     */

    private void queryDetailInformation(){
       // Log.d(TAG,"queryDetailInformation()");
        String st = "";
        String sortString = ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY+" asc";//sort by  [A-Z]

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                ContactsContract.RawContacts.ACCOUNT_TYPE,
                "phonebook_label",
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        Cursor phone = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null, sortString);
        st += "----------------------------------------------- \n";

        while (phone.moveToNext()) {

            Contact contact = new Contact();

            String contactID = phone
                    .getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

            String phonebook_label = phone
                    .getString(phone
                            .getColumnIndex("phonebook_label"));
            String name = phone
                    .getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            name = name.trim();

            String rawContactsId = phone
                    .getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));

            String number = phone
                    .getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

          //  Log.d(TAG,"query  number1="+number1);
            // 对手机号码进行预处理（去掉号码前的+86、首尾空格、“-”号等）
            number = number.replaceAll("^(\\+86)", "");
            number = number.replaceAll("^(86)", "");
            number = number.replaceAll("-", "");
            number = number.replaceAll(" ", "");
            number = number.trim();

            st += "contactID:" + contactID + "\n";
            st += "name:" + name + "\n";
            st += "phonebook_label:" + phonebook_label + "\n";
            st += "rawContactsId:" + rawContactsId + "\n";
            st += "number:" + number + "\n";

            contact.setContactID(contactID);
            contact.setPhonebookLabel(phonebook_label);
            contact.setName(name);
            contact.setContactAccountID(rawContactsId);
            Number num = new Number();
            num.setNum(number);

            NumberUtil.Numbers numbers = NumberUtil.checkNumber(number);
            NumberUtil.PhoneType type = numbers.getType();
            String code = numbers.getCode();
            if(NumberUtil.PhoneType.INVALIDPHONE == type){//无效的
                num.setNumAttribution("未知归属地");

            }else {
                String attribution = NumberUtil.getAttInfo(type,code);
                if(TextUtils.isEmpty(attribution)){//数据库没有存储
                    nums.add(num);//稍后去查询
                }
                num.setNumAttribution(attribution);
            }

            contact.getNumbers().add(num);
            contact.setContact(true);



            if(datas.contains(contact)){
                Contact c = datas.get(datas.indexOf(contact));
                c.getNumbers().add(num);
            }else{
                datas.add(contact);
            }
        }
        st += "----------------------------------------------- \n";
       //  Log.d(TAG,"query  st="+st);
        phone.close();
    }

    /**
     * 归属地查询
     */
    private void queryNumberAttribution(){

        Observable.fromIterable(nums)
                  .observeOn(Schedulers.io())
                  .doOnNext(new Consumer<Number>() {
                        @Override
                        public void accept(Number phoneNumber) throws Exception {
                           // Log.d(TAG,"queryNumberAttribution doOnNext  accept:"+Thread.currentThread().getName());
                            NumberUtil.Numbers numbers = NumberUtil.checkNumber(phoneNumber.getNum());
                            NumberUtil.PhoneType type = numbers.getType();
                            String code = numbers.getCode();

                            if(NumberUtil.PhoneType.INVALIDPHONE == type){//无效的
                                phoneNumber.setNumAttribution("未知归属地");
                                return;
                            }
                            //Log.d(TAG,"accept  code:"+code+" type:"+type);
                            String attribution = NumberUtil.getAttInfo(type,code);
                            if(TextUtils.isEmpty(attribution)){//数据库没有存储
                                attribution = NumberUtil.getAttInfo(context,type,code);
                            }

                            NumAttribution numAttribution = new NumAttribution();
                            numAttribution.setCode(code);
                            numAttribution.setAttribution(attribution);
                            numAttribution.setType(type.name());
                            phoneNumber.setNumAttribution(attribution);

                            String s = NumberUtil.getAttInfo(type,code);
                            if(TextUtils.isEmpty(s)){//数据库没有存储
                                BaseApplication.getDaoInstant().getNumAttributionDao().insert(numAttribution);
                            }

                         }
                   })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Consumer<Number>() {
                        @Override
                        public void accept(Number number) throws Exception {
                           // Log.d(TAG,"accept accept:"+Thread.currentThread().getName());
                           // Log.d(TAG,"accept accept  number  num="+number.getNum()+"  attr= "+number.getNumAttribution());
                        }
                    });

    }



    /**
     *查询完成之后更新列表
     */
    private void updateData() {
        for (int i = 0; i < datas.size(); i++) {
            Contact c = datas.get(i);
            if (c != null) {
                String pinyin = characterParser.getSelling(c.getName());
                c.setPinying(pinyin);

            }
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }

    }


    private PopupWindow popupWindow;
    private View popupView;
    private FrameLayout fl1;
    private FrameLayout fl2;
    private FrameLayout fl3;
    private FrameLayout fl4;
    private TextView add_blacklist;
    private TextView send_msg;
    private TextView share;
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

            Drawable msgDrawable = getResources().getDrawable(R.drawable.mca_msg_icon);
            msgDrawable.setBounds(0, 0, msgDrawable.getMinimumWidth(), msgDrawable.getMinimumHeight());
            send_msg.setCompoundDrawables(null,msgDrawable,null,null);
            send_msg.setTextColor(getResources().getColor(R.color.pop_text_color_enable));

            Drawable shareDrawable = getResources().getDrawable(R.drawable.mca_share_icon);
            shareDrawable.setBounds(0, 0, shareDrawable.getMinimumWidth(), shareDrawable.getMinimumHeight());
            share.setCompoundDrawables(null,shareDrawable,null,null);
            share.setTextColor(getResources().getColor(R.color.pop_text_color_enable));

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

            Drawable msgDrawable = getResources().getDrawable(R.drawable.mca_msg_icon_disabled);
            msgDrawable.setBounds(0, 0, msgDrawable.getMinimumWidth(), msgDrawable.getMinimumHeight());
            send_msg.setCompoundDrawables(null,msgDrawable,null,null);
            send_msg.setTextColor(getResources().getColor(R.color.pop_text_color_disable));

            Drawable shareDrawable = getResources().getDrawable(R.drawable.mca_share_icon_disabled);
            shareDrawable.setBounds(0, 0, shareDrawable.getMinimumWidth(), shareDrawable.getMinimumHeight());
            share.setCompoundDrawables(null,shareDrawable,null,null);
            share.setTextColor(getResources().getColor(R.color.pop_text_color_disable));

            Drawable delDrawable = getResources().getDrawable(R.drawable.mca_bottom_item_del_disabled);
            delDrawable.setBounds(0, 0, delDrawable.getMinimumWidth(), delDrawable.getMinimumHeight());
            delete.setCompoundDrawables(null,delDrawable,null,null);
            delete.setTextColor(getResources().getColor(R.color.pop_text_color_disable));
        }
        popupWindow.update();
    }

    private void showPop(View view){
        //show popwindow
        popupView = indexActivity.getLayoutInflater().inflate(R.layout.popwindow, null);
        fl1 = (FrameLayout) popupView.findViewById(R.id.fl1);
        fl2 = (FrameLayout) popupView.findViewById(R.id.fl2);
        fl3 = (FrameLayout) popupView.findViewById(R.id.fl3);
        fl4 = (FrameLayout) popupView.findViewById(R.id.fl4);



        add_blacklist = (TextView) popupView.findViewById(R.id.add_blacklist);
        send_msg = (TextView) popupView.findViewById(R.id.send_msg);
        share = (TextView) popupView.findViewById(R.id.share);
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
        popupWindow.showAtLocation(view,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);


    }

    public void setAllSelected(boolean isAllSelected){
        this.isAllSelected = isAllSelected;//isSelectNone  fasle  当前已经全部选中
        adapter.setAllItemChecked(isAllSelected);
    }

    public boolean isAllSelected(){
        return this.isAllSelected;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl1:
                Toast.makeText(context,"加入黑名单",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fl2:
                Toast.makeText(context,"发信息",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fl3:
                Toast.makeText(context,"分享",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fl4:
                Toast.makeText(context,"删除",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private class MyContentObserver extends ContentObserver{

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG,"onChange  selfChange="+selfChange);

        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG,"onChange  uri="+uri.toString()+"  selfChange"+selfChange);
            //先查数据库总联系人人数,如果没有改变没必要去重新查询所有联系人  因为打电话 这个通知也会来
            notifyDataChange();
        }
    }

    /**
     * 按back key 如果此时toolbar is hide   then show toolbar meanwhile hide some view
     */
    public void hideSearchBarElement(){
        if(num_contacts_tv == null|| cancel_search_tv == null || search_iv == null || search_clean_iv == null|| search_et == null){
            return;
        }

        num_contacts_tv.setVisibility(View.VISIBLE);
        cancel_search_tv.setVisibility(View.GONE);
        search_iv.setVisibility(View.GONE);
        search_clean_iv.setVisibility(View.GONE);
        search_et.setText("");
        search_et.setCursorVisible(false);
        addData();
        scrollToFirstPosition();

    }

    public void showAssistantAndGroup(){
        addData();
    }

    public void scrollToFirstPosition(){
        if(mLayoutManager != null){
            mLayoutManager.scrollToPositionWithOffset(0,0);
        }
    }


    /**
     *
     */
    public void hideCheckBox(){
        if(adapter != null){
            adapter.setShowCheckBox(false);
            adapter.setAllItemChecked(false);
        }
        if(popupWindow != null){
            popupWindow.dismiss();
        }
    }


    private void setListener() {
        search_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "onFocusChange  hasFocus == true");
                    if(adapter != null && !adapter.isShowCheckBox()){
                        indexActivity.hideToolbar();
                        deleteData();
                    }
                    num_contacts_tv.setVisibility(View.GONE);
                    cancel_search_tv.setVisibility(View.VISIBLE);
                    search_iv.setVisibility(View.VISIBLE);
                } else {
                    indexActivity.showToolbar();
                    num_contacts_tv.setVisibility(View.VISIBLE);
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
                    indexSiderBar.setVisibility(View.VISIBLE);
                } else {
                    indexSiderBar.setVisibility(View.INVISIBLE);
                    search_iv.setVisibility(View.VISIBLE);
                    search_clean_iv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString().trim();
                //Log.d(TAG, "afterTextChanged  s=" + key);
                int num = 0;
                //if num  search by phone num
                try {
                    num = Integer.parseInt(key);
                    mLayoutManager.scrollToPositionWithOffset(0,0);
                    searchContactsByNum(num + "");
                } catch (NumberFormatException exception) {
                    //not num   by name
                   // Log.d(TAG, "afterTextChanged  not num");
                    searchContactsByName(key);
                }
            }
        });
        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick  search_et");
                search_et.setCursorVisible(true);
                if(adapter != null && !adapter.isShowCheckBox()){
                    indexActivity.hideToolbar();
                    deleteData();
                }
                num_contacts_tv.setVisibility(View.GONE);
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

        indexSiderBar.setOnTouchingLetterChangedListener(new IndexSiderBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if(!TextUtils.isEmpty(s)){
                    int section = s.charAt(0);
                    int position = adapter.getPositionForSection(section);
                    if(position != -1){
                            mLayoutManager.scrollToPositionWithOffset(position,0);

                    }
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_contact, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG,"onOptionsItemSelected");

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_contact_arrange) {
            Toast.makeText(getActivity(),"联系人整理",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_contact_backup) {
            Toast.makeText(getActivity(),"联系人备份",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_contact_recycler) {
            Toast.makeText(getActivity(),"联系人回收站",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_contact_setting) {
            Toast.makeText(getActivity(),"联系人设置",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_contact_add) {
            Toast.makeText(getActivity(),"添加联系人",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 通过电话号码查询
     * @param key
     */
    private void searchContactsByNum(final String key) {
        Log.d(TAG, "searchContactsByNum  key=" + key);
        ArrayList<Contact> temp = new ArrayList();
        for (int i = 0; i < datas.size(); i++) {
            Contact contacts = datas.get(i);
            if (contacts != null) {
                boolean flag = false;
                ArrayList<Number> numbers = contacts.getNumbers();
                for(int j=0;j<numbers.size();j++){
                    Number number = numbers.get(j);
                    if(number.getNum().contains(key)){
                        flag = true;
                        numbers.remove(number);
                        numbers.add(0,number);
                        temp.add(contacts);
                        break;
                    }
                }
                if(!flag){
                    ArrayList<Integer> wordIndex = contacts.getKeyIndexList(key);
                    if (wordIndex.size() > 0) {
                        temp.add(contacts);
                    }
                }

            }

        }
        if (adapter != null) {
            String str = temp.size() > 0 ? key : "";
            adapter.setKey(str);
            adapter.setList(temp);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 通过姓名查询
     * @param key
     */
    private void searchContactsByName(final String key) {
        if (!TextUtils.isEmpty(key)) {
            mLayoutManager.scrollToPositionWithOffset(0,0);
            ArrayList<Contact> temp = new ArrayList();
            for (int i = 0; i < datas.size(); i++) {
                Contact contacts = datas.get(i);
                if (contacts != null) {
                    ArrayList<Integer> set = contacts.getKeyIndexList(key);
                    if (set.size() > 0) {
                        temp.add(contacts);
                    }
                }
            }
            if (adapter != null) {
                String str = temp.size() > 0 ? key : "";
                adapter.setKey(str);
                adapter.setList(temp);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (adapter != null) {
                adapter.setKey("");
                adapter.setList(datas);
                adapter.notifyDataSetChanged();
            }
        }
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


    public  void onHide(){
        hideSearchBarElement();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "ContactsFragment onAttach  activity");
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "ContactsFragment onAttach context");
        super.onAttach(context);
        this.context = context;
        this.indexActivity = (IndexActivity) context;
        resolver = context.getContentResolver();
        queryContactsFromDB();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ContactsFragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        Log.d(TAG, "ContactsFragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "ContactsFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "ContactsFragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "ContactsFragment onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "ContactsFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "ContactsFragment onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "ContactsFragment onDestroyView");
        super.onDestroyView();
    }


}
