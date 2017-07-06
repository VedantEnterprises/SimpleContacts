package com.cj.simplecontacts.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.R;

/**
 * Created by chenjun on 2017/6/11.
 */

public class DialFragment extends Fragment {
    private final static String TAG = "DialFragment";
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
        return view;
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
