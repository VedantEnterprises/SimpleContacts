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

public class LifeAssistantFragment extends Fragment {
    private final static String TAG = "LifeAssistantFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LifeAssistantFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LifeAssistantFragment onCreateView");
        View view = inflater.inflate(R.layout.life_assistant_fragment, null);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "LifeAssistantFragment onAttach  activity");
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "LifeAssistantFragment onAttach context");
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LifeAssistantFragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "LifeAssistantFragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "LifeAssistantFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "LifeAssistantFragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "LifeAssistantFragment onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LifeAssistantFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "LifeAssistantFragment onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "LifeAssistantFragment onDestroyView");
        super.onDestroyView();
    }


}
