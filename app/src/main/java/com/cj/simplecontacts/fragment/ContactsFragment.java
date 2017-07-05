package com.cj.simplecontacts.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;
import com.cj.simplecontacts.adapter.ContactAdapter;
import com.cj.simplecontacts.enity.Contact;
import com.cj.simplecontacts.tool.AndroidTool;
import com.cj.simplecontacts.tool.CharacterParser;
import com.cj.simplecontacts.view.IndexSiderBar;

import java.util.ArrayList;

/**
 * Created by chenjun on 2017/6/11.
 */

public class ContactsFragment extends Fragment {
    private final static String TAG = "ContactsFragment";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

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
    private ContentResolver resolver;
    private ContactAdapter adapter;

    private ArrayList<Contact> datas = new ArrayList();


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    num_contacts_tv.setVisibility(View.VISIBLE);
                    num_contacts_tv.setText("共有" + msg.arg1 + "个联系人");
                    break;
                case 1:
                    Log.d(TAG, "handleMessage 1");
                    setUpDatas();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ContactsFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ContactsFragment onCreateView");
        View view = inflater.inflate(R.layout.contacts_fragment, null);
        search_et = (EditText) view.findViewById(R.id.search_et);
        num_contacts_tv = (TextView) view.findViewById(R.id.num_contacts_tv);
        cancel_search_tv = (TextView) view.findViewById(R.id.cancel_tv);
        search_clean_iv = (ImageView) view.findViewById(R.id.search_clean_iv);
        search_iv = (ImageView) view.findViewById(R.id.search_iv);
        indexSiderBar = (IndexSiderBar) view.findViewById(R.id.inddex_sider_bar);
        tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_contacts);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        characterParser = CharacterParser.getInstance();
        resolver = context.getContentResolver();

        MyContentObserver contentObserver  = new MyContentObserver(handler);
        resolver.registerContentObserver(ContactsContract.RawContacts.CONTENT_URI,true,contentObserver);

        indexSiderBar.setTextDialog(tv_dialog);
        queryContacts();
        setListener();
        return view;
    }

    private void queryContacts() {
        if (AndroidTool.isPreM()) {
            //before 6.0  do not need runtime permission
            new Thread(runnable).start();
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_CONTACTS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((IndexActivity) context,
                        Manifest.permission.READ_CONTACTS)) {
                    new AlertDialog.Builder(context)
                            .setMessage("您是否授予访问通讯录的权限？")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //again request permission
                                    ActivityCompat.requestPermissions((IndexActivity) context,
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

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
                    ActivityCompat.requestPermissions((IndexActivity) context,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                Log.d(TAG, "startrunnable");
                new Thread(runnable).start();
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            datas.clear();
            String st = "";
            //"content://com.android.contacts/contacts"  uri
            String sortString = ContactsContract.Contacts.SORT_KEY_PRIMARY + " asc";//sort by  [A-Z]
            Cursor contactsCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortString);
            int count = contactsCursor.getCount();//get row quantity,  is  contacts quantity

            Message message = handler.obtainMessage();
            message.what = 0;
            message.arg1 = count;
            handler.sendMessage(message);

            st += "共" + count + "个联系人  \n ";

            while (contactsCursor.moveToNext()) {
                st += "----------------------------------------------- \n";
                Contact contacts = new Contact();
                String contactID = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phonebook_label = contactsCursor.getString(contactsCursor.getColumnIndex("phonebook_label"));
                //hasPhoneNum is 1 indicate have phone num
                int hasPhoneNum = contactsCursor.getInt(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                contacts.setContactID(contactID);
                contacts.setName(name);
                contacts.setPhonebookLabel(phonebook_label);

                st += "contactID:" + contactID + "\n";
                st += "name:" + name + "\n";
                st += "phonebook_label:" + phonebook_label + "\n";
                st += "hasPhoneNum:" + hasPhoneNum + "\n";

                //on contact maybe belong to more than one account   eg:weixin  qq  there just mind the com.android.localphone account
                String selection = ContactsContract.RawContacts.CONTACT_ID + " = ? and " + ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?";
                String[] selectionArgs = {contactID, "com.android.localphone"};

                Cursor rawContactsCursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI, null, selection, selectionArgs, null);

                String rawContactsId = "";
                if (rawContactsCursor.moveToFirst()) {
                    rawContactsId = rawContactsCursor.getString(rawContactsCursor.getColumnIndex(ContactsContract.RawContacts._ID));
                }
                contacts.setContactAccountID(rawContactsId);
                rawContactsCursor.close();

                st += "---------- \n";
                st += "rawContactsId:" + rawContactsId + "\n";

                // get phone nums
                if (hasPhoneNum > 0) {
                    //"content://com.android.contacts/data/phones"
                    Cursor PhoneCur = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = ?",
                            new String[]{rawContactsId}, null);
                    while (PhoneCur.moveToNext()) {
                        String number = PhoneCur.getString(PhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        contacts.getNumbers().add(number.replace(" ", ""));
                        st += "~~~~ \n";
                        st += "number:" + number + "\n";
                    }
                    st += "~~~~ \n";
                    PhoneCur.close();
                }
                st += "---------- \n";
                datas.add(contacts);
            }
            st += "----------------------------------------------- \n";
            // Log.d(TAG,"query  st="+st);
            contactsCursor.close();
            handler.sendEmptyMessage(1);
        }

    };

    private void setUpDatas() {
        for (int i = 0; i < datas.size(); i++) {
            Contact c = datas.get(i);
            if (c != null) {
                String pinyin = characterParser.getSelling(c.getName());
                c.setPinying(pinyin);
            }
        }
        adapter = new ContactAdapter(datas, context);
        adapter.setReclerViewItemListener(new ContactAdapter.ReclerViewItemListener() {
            @Override
            public void onItemClik(int position) {

            }

            @Override
            public void onItemChecked(int position) {

                // tv1.setText("一共选择了"+adapter.getCheckedCount()+"个");

            }

            @Override
            public void onLongClik(int position) {
                // tv1.setVisibility(View.VISIBLE);
                //  checkBox.setVisibility(View.VISIBLE);
                //  supportActionBar.setDisplayShowTitleEnabled(false);
                // supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
        });
        adapter.setAllItemChecked(false);
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                Log.d(TAG,"RecyclerView  onChildViewAttachedToWindow");
//                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
//                if (layoutManager instanceof LinearLayoutManager) {
//                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;

                    int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                    //Log.d(TAG,"RecyclerView  onChildViewAttachedToWindow firstPosition="+firstPosition);
                    if(firstPosition >= 0){
                        int section = adapter.getSectionForPosition(firstPosition);
                        indexSiderBar.setChooseIndex((char)(section)+"");
                    }
               // }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });

    }

    private class MyContentObserver extends ContentObserver{

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG,"onChange  selfChange="+selfChange);
            new Thread(runnable).start();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG,"onChange  uri="+uri.toString());
        }
    }
    public void hideCanceTv(){
        if(num_contacts_tv == null|| cancel_search_tv == null || search_iv == null ){
            return;
        }
        num_contacts_tv.setVisibility(View.VISIBLE);
        cancel_search_tv.setVisibility(View.GONE);
        search_iv.setVisibility(View.GONE);
        search_et.setCursorVisible(false);
    }
    private void setListener() {
        search_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((IndexActivity) getActivity()).hideToolbar();
                    num_contacts_tv.setVisibility(View.GONE);
                    cancel_search_tv.setVisibility(View.VISIBLE);
                    search_iv.setVisibility(View.VISIBLE);
                } else {
                    ((IndexActivity) getActivity()).showToolbar();
                    num_contacts_tv.setVisibility(View.VISIBLE);
                    cancel_search_tv.setVisibility(View.GONE);
                    search_iv.setVisibility(View.GONE);
                }
            }
        });
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged");
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
                Log.d(TAG, "afterTextChanged  s=" + key);


                int num = 0;
                //if num  search by phone num
                try {
                    num = Integer.parseInt(key);
                    mLayoutManager.scrollToPositionWithOffset(0,0);
                    searchContactsByNum(num + "");
                } catch (NumberFormatException exception) {
                    //not num   by name
                    Log.d(TAG, "afterTextChanged  not num");
                    searchContactsByName(key);
                }
            }
        });
        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setCursorVisible(true);
                ((IndexActivity) getActivity()).hideToolbar();
                num_contacts_tv.setVisibility(View.GONE);
                cancel_search_tv.setVisibility(View.VISIBLE);
                search_iv.setVisibility(View.VISIBLE);
            }
        });
        cancel_search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IndexActivity) getActivity()).showToolbar();
                num_contacts_tv.setVisibility(View.VISIBLE);
                cancel_search_tv.setVisibility(View.GONE);
                search_iv.setVisibility(View.GONE);
                search_et.setText("");
                search_clean_iv.setVisibility(View.GONE);
                search_et.setCursorVisible(false);
                InputMethodManager imm = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
                    int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = mLayoutManager.findLastVisibleItemPosition();
                    if(position != -1){
//                        if(position < firstPosition ||position > lastPosition){
//                            mRecyclerView.smoothScrollToPosition(position);
//                        }else{
                            mLayoutManager.scrollToPositionWithOffset(position,0);
                       // }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("permission", "onRequestPermissionsResult  requestCode" + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Thread(runnable).start();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(context, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                    //   writeDatasToExternalStorage();

                } else {
                    // Permission Denied
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
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

    public boolean checkBoxIsShowing(){
        if(adapter != null){
            return adapter.isShowCheckBox();
        }
        return false;
    }

    public void hideCheckbox(){
        if(adapter != null){
            adapter.setShowCheckBox(false);
            adapter.setAllItemChecked(false);
        }
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
