package com.cj.simplecontacts.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;
import com.cj.simplecontacts.adapter.ContactAdapter;
import com.cj.simplecontacts.enity.Contact;
import com.cj.simplecontacts.tool.CharacterParser;
import com.cj.simplecontacts.tool.Constant;
import com.cj.simplecontacts.view.IndexSiderBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenjun on 2017/6/11.
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

    private Handler handler = new Handler();

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
        resolver = context.getContentResolver();

        MyContentObserver contentObserver  = new MyContentObserver(handler);
        resolver.registerContentObserver(ContactsContract.RawContacts.CONTENT_URI,true,contentObserver);
        createTwoObject();
        addData();

        setListener();
        checkPermissionAndQuery();

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

        indexSiderBar.setTextDialog(tv_dialog);

    }

    private void showContactDialog(Contact contact){
        Dialog dialog = new Dialog(context,R.style.DialogTheme);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_dialog, null);
        ImageButton dialogShare = (ImageButton) view.findViewById(R.id.dialog_share);
        ImageButton dialogSeeMore = (ImageButton) view.findViewById(R.id.dialog_see_more);
        FrameLayout dialogSendMsg = (FrameLayout) view.findViewById(R.id.dialog_send_msg);
        FrameLayout dialogCall = (FrameLayout) view.findViewById(R.id.dialog_call);

        TextView dialogName = (TextView) view.findViewById(R.id.dialog_name);
        TextView dialogNum = (TextView) view.findViewById(R.id.dialog_num);
        TextView dialogRegion = (TextView) view.findViewById(R.id.dialog_region);

        dialogName.setText(contact.getName());
        ArrayList<String> numbers = contact.getNumbers();
        if(numbers != null && numbers.size()>0){
            dialogNum.setText(numbers.get(0));
        }
        String attr = contact.getAttr();
        if(TextUtils.isEmpty(attr)){
            dialogRegion.setText("正在查询...");
        }else {
            dialogRegion.setText(attr);
        }

        dialogShare.setOnClickListener(this);
        dialogSeeMore.setOnClickListener(this);
        dialogSendMsg.setOnClickListener(this);
        dialogCall.setOnClickListener(this);

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

    private void setUpRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ContactAdapter(datas, context);

        adapter.setReclerViewItemListener(new ContactAdapter.ReclerViewItemListener() {
            @Override
            public void onItemClick(int position) {
                HideSoftInput();
                Contact contact = datas.get(position);

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
            public void onItemChecked(int position,View v) {
                int checkedCount = adapter.getCheckedCount();
                if(checkedCount == (adapter.isHaveNotContact()?adapter.getItemCount()-2:adapter.getItemCount())){
                    isAllSelected = true;
                }else{
                    isAllSelected = false;
                }
                indexActivity.notifyCheckedItem(checkedCount,isAllSelected);
                HideSoftInput();
                notifyPop(checkedCount);
            }

            @Override
            public void onLongClick(int position,View v) {
                HideSoftInput();
                Contact contact = datas.get(position);
                if(!contact.isContact()){
                    //Toast.makeText(context,"不是联系人条目 ",Toast.LENGTH_SHORT).show();
                    return;
                }
                indexActivity.showToolbar();
                hideSearchBarElement();
                indexActivity.showActionMode();
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
                Log.d(TAG,"RecyclerView  onScrolled");
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


    private void checkPermissionAndQuery() {
            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_CONTACTS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    new AlertDialog.Builder(context)
                            .setMessage("您拒绝过授予读取通讯录的权限,但是只有申请该权限,才能查询通讯录,你确定要重新申请获取权限吗？")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //again request permission
                                    ContactsFragment.this.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                            Constant.MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                                }
                            })
                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }else{
                    // No explanation needed, we can request the permission.
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            Constant.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {

                queryContactsFromDB();
            }

    }


    private void queryContactsFromDB(){

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                Log.d(TAG,"subscribe  thread:"+Thread.currentThread().getName());
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
                      //  Log.d(TAG,"onNext:value="+value.intValue());
                      //  Log.d(TAG,"onNext  thread:"+Thread.currentThread().getName());
                        num_contacts_tv.setVisibility(View.VISIBLE);
                        num_contacts_tv.setText("共有" + value.intValue() + "个联系人");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete");
                       // Log.d(TAG,"onComplete thread:"+Thread.currentThread().getName());
                        updateData();
                      queryNumberAttribution();
                    }
                });
    }

    /**
     *
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
     *
     */

    private void queryDetailInformation(){
        Log.d(TAG,"queryDetailInformation()");
        String st = "";
        String sortString = ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY+" asc";//sort by  [A-Z]
        String selection = ContactsContract.RawContacts.ACCOUNT_TYPE+" = ?";
        String[] selectionArgs = {"com.android.localphone"};

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                "phonebook_label",
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.DISPLAY_NAME};

        Cursor phone = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs, sortString);
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
            contact.getNumbers().add(number);
            contact.setContact(true);



            if(datas.contains(contact)){
               // Log.d(TAG,"query  number="+number+"  name="+contact.getName());
                Contact c = datas.get(datas.indexOf(contact));
                c.getNumbers().add(number);
               // Log.d(TAG,"query  numbers="+c.getNumbers().size());
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
        Observable.fromIterable(datas)

                .filter(new Predicate<Contact>() {
                    @Override
                    public boolean test(Contact contact) throws Exception {
                       // Log.d(TAG," test:"+Thread.currentThread().getName());
                        ArrayList<String> numbers = contact.getNumbers();
                        if(numbers != null && numbers.size()>0){
                            SharedPreferences sharedPreferences = context.getSharedPreferences("simplecontacts", context.MODE_PRIVATE);
                            String att = sharedPreferences.getString(numbers.get(0), "");
                            if(TextUtils.isEmpty(att)){
                                att = getAttInfo(numbers.get(0));
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(numbers.get(0),att);
                                editor.commit();
                            }
                            contact.setAttr(att);
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Contact>() {
                    @Override
                    public void accept(Contact contact) throws Exception {
                       // Log.d(TAG," accept:"+Thread.currentThread().getName());
                        //Log.d(TAG,"accept  contact att="+contact.getAttr());
                    }
                });
    }

    /**
     *
     * @param num
     * @return
     */
    private String getAttInfo(String num){
        String attribution = "未知归属地";

        Log.d(TAG,"readAssetsFile  num="+num);
        InputStream is = null;
        try {
            is = context.getAssets().open("Mobile.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is,"utf8"));
            String line;
            while ((line=br.readLine()) != null){
                String[] attInfo = line.split(",");

                String seven = attInfo[1];
                seven = seven.substring(1,seven.length()-1).trim();//去掉""手机号前7位

                String province = attInfo[2];
                province = province.substring(1,province.length()-1).trim();//省份

                String city = attInfo[3];
                city = city.substring(1,city.length()-1).trim();//城市

                String operator = attInfo[4];
                operator = operator.substring(1,operator.length()-1).trim().replace("中国","");//运营商


                String areaCode = attInfo[5];
                areaCode = areaCode.substring(1,areaCode.length()-1).trim();//去掉""区号



               if(num.startsWith("1")){

                   if(num.trim().indexOf(seven, 0) == 0){
                       if(province.equals(city)){
                           attribution = city+" "+operator;
                       }else{
                           attribution = province+"-"+city+" "+operator;
                       }
                       break;
                   }else {
                       continue;
                   }

               }else{

                   if(num.indexOf(areaCode, 0) == 0){
                       if(province.equals(city)){
                           attribution = city;
                       }else{
                           attribution = province+"-"+city;
                       }
                       break;
                   }else {
                       continue;
                   }

               }
            }

        } catch (IOException e) {

        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return attribution;
    }
    private void updateData() {
        for (int i = 0; i < datas.size(); i++) {
            Contact c = datas.get(i);
            if (c != null) {
                String pinyin = characterParser.getSelling(c.getName());
                c.setPinying(pinyin);

            }
        }
        adapter.notifyDataSetChanged();

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
            case R.id.dialog_share:
                Toast.makeText(context,"分享",Toast.LENGTH_SHORT).show();
                break;
            case R.id.dialog_see_more:
                Toast.makeText(context,"查看更多",Toast.LENGTH_SHORT).show();
                break;
            case R.id.dialog_send_msg:
                Toast.makeText(context,"发短信",Toast.LENGTH_SHORT).show();
                break;
            case R.id.dialog_call:
                Toast.makeText(context,"打电话",Toast.LENGTH_SHORT).show();
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
           // Log.d(TAG,"onChange  selfChange="+selfChange);
            datas.clear();
            addData();
            queryContactsFromDB();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
           // Log.d(TAG,"onChange  uri="+uri.toString());
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
        scrollToFirstPosition();
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
                addData();
                num_contacts_tv.setVisibility(View.VISIBLE);
                cancel_search_tv.setVisibility(View.GONE);
                search_iv.setVisibility(View.GONE);
                search_et.setText("");
                search_clean_iv.setVisibility(View.GONE);
                search_et.setCursorVisible(false);
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
                ArrayList<String> numbers = contacts.getNumbers();
                if (numbers.size() > 0) {
                    if (numbers.get(0).contains(key)) {

                        temp.add(contacts);
                    } else {
                        ArrayList<Integer> wordIndex = contacts.getKeyIndexList(key);
                        if (wordIndex.size() > 0) {
                            temp.add(contacts);
                        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("permission", "onRequestPermissionsResult  requestCode" + requestCode);
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    queryContactsFromDB();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();


                } else {
                    // Permission Denied
                    // Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
