package com.cj.simplecontacts;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class IndexActivity extends AppCompatActivity {
    private final static String TAG = "IndexActivity";
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"IndexActivity onCreate");
        setContentView(R.layout.activity_index);
        iv = (ImageView) findViewById(R.id.iv);
        iv.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv.setVisibility(View.GONE);

            }
        },1000);
    }

    @Override
    public void finish() {
      //  super.finish();
        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"IndexActivity onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"IndexActivity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"IndexActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"IndexActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"IndexActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"IndexActivity onDestroy");
    }
}
