package com.cj.simplecontacts.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;

/**
 * Created by chenjun on 2017/6/11.
 */

public class ContactsFragment extends Fragment {
    private final static String TAG = "ContactsFragment";

    private EditText search_et;
    private TextView num_contacts_tv;
    private TextView cancel_search_tv;
    private ImageView search_clean_iv;
    private ImageView search_iv;

    private Context context;

    private Drawable et_left_drawable;

    private Handler handler;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ContactsFragment onCreate");
        handler = new Handler();
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


        setListener();
        return view;
    }

    private void setListener() {
        search_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ((IndexActivity)getActivity()).hideToolbar();
                    num_contacts_tv.setVisibility(View.GONE);
                    cancel_search_tv.setVisibility(View.VISIBLE);
                    search_iv.setVisibility(View.VISIBLE);
                }else{
                    ((IndexActivity)getActivity()).showToolbar();
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
                if(TextUtils.isEmpty(string)){
                    search_iv.setVisibility(View.GONE);
                    search_clean_iv.setVisibility(View.GONE);
                }else {
                    search_iv.setVisibility(View.VISIBLE);
                    search_clean_iv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged");

            }
        });
        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setCursorVisible(true);
                ((IndexActivity)getActivity()).hideToolbar();
                num_contacts_tv.setVisibility(View.GONE);
                cancel_search_tv.setVisibility(View.VISIBLE);
                search_iv.setVisibility(View.VISIBLE);
            }
        });
        cancel_search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IndexActivity)getActivity()).showToolbar();
                num_contacts_tv.setVisibility(View.VISIBLE);
                cancel_search_tv.setVisibility(View.GONE);
                search_iv.setVisibility(View.GONE);
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
