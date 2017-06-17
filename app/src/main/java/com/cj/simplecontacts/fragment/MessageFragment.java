package com.cj.simplecontacts.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.simplecontacts.R;

/**
 * Created by chenjun on 2017/6/11.
 */

public class MessageFragment extends Fragment {
    private final static String TAG = "MessageFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "MessageFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "MessageFragment onCreateView");
        View view = inflater.inflate(R.layout.message_fragment, null);
        return view;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "MessageFragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);
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
