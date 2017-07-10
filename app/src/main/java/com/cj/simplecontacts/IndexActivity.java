package com.cj.simplecontacts;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cj.simplecontacts.fragment.ContactsFragment;
import com.cj.simplecontacts.fragment.DialFragment;
import com.cj.simplecontacts.fragment.LifeAssistantFragment;
import com.cj.simplecontacts.fragment.MessageFragment;
import com.cj.simplecontacts.tool.Constant;

public class IndexActivity extends AppCompatActivity {
    private final static String TAG = "IndexActivity";


    private BottomNavigationBar bottomNavigationBar;
    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private ActionMode actionMode;
    private DialFragment dialFragment;
    private ContactsFragment contactsFragment;
    private MessageFragment messageFragment;
    private LifeAssistantFragment lifeAssistantFragment;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IndexActivity onCreate");
        setContentView(R.layout.activity_index);

        initViews();
        setUpFragment();
        setUpBottomNavigationBar();
        applyPermission();
    }


    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("轻版通讯录");
        toolbar.setNavigationIcon(R.drawable.sliding_menu_btn_bg);
    }

    private void setUpFragment() {
        fragmentManager = getSupportFragmentManager();
        dialFragment = new DialFragment();
        contactsFragment = new ContactsFragment();
        messageFragment = new MessageFragment();
        lifeAssistantFragment = new LifeAssistantFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fg_container,dialFragment);
        transaction.commit();
    }

    private void setUpBottomNavigationBar() {

        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.tab_widget_green_dial_down,"拨号")
                                    .setInactiveIconResource(R.drawable.tab_widget_green_dial))

                .addItem(new BottomNavigationItem(R.drawable.tab_widget_green_contacts_pressed,"联系人")
                            .setInactiveIconResource(R.drawable.tab_widget_green_contacts))

                .addItem(new BottomNavigationItem(R.drawable.tab_widget_green_message_pressed,"信息")
                        .setInactiveIconResource(R.drawable.tab_widget_green_message))

                .addItem(new BottomNavigationItem(R.drawable.tab_widget_green_cloud_pressed,"生活助手")
                        .setInactiveIconResource(R.drawable.tab_widget_green_cloud))

                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                Log.d(TAG, "onTabSelected position="+position);
                switch (position){
                    case 0:
                        FragmentTransaction transaction0 = fragmentManager.beginTransaction();
                        Fragment fragment0 = fragmentManager.findFragmentByTag("contactsFragment");
                        if(fragment0 != null){
                            transaction0.hide(contactsFragment);
                        }
                        transaction0.remove(messageFragment);
                        transaction0.remove(lifeAssistantFragment);
                        transaction0.add(R.id.fg_container,dialFragment,"dialFragment");
                        transaction0.commit();
                        break;
                    case 1:
                        FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                        Fragment fragment1 = fragmentManager.findFragmentByTag("contactsFragment");
                        if(fragment1 != null){
                            transaction1.show(contactsFragment);

                        }else {
                            transaction1.replace(R.id.fg_container, IndexActivity.this.contactsFragment,"contactsFragment");
                        }
                        transaction1.remove(dialFragment);
                        transaction1.remove(messageFragment);
                        transaction1.remove(lifeAssistantFragment);

                        transaction1.commit();
                        break;
                    case 2:
                        FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                        Fragment fragment2 = fragmentManager.findFragmentByTag("contactsFragment");
                        if(fragment2 != null){
                            transaction2.hide(contactsFragment);
                        }
                        transaction2.remove(dialFragment);
                        transaction2.remove(lifeAssistantFragment);
                        transaction2.add(R.id.fg_container,messageFragment,"messageFragment");
                        transaction2.commit();
                        break;
                    case 3:
                        FragmentTransaction transaction3 = fragmentManager.beginTransaction();
                        Fragment fragment3 = fragmentManager.findFragmentByTag("contactsFragment");
                        if(fragment3 != null){
                            transaction3.hide(contactsFragment);
                        }
                        transaction3.remove(dialFragment);
                        transaction3.remove(messageFragment);
                        transaction3.add(R.id.fg_container,lifeAssistantFragment,"messageFragment");
                        transaction3.commit();
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onTabUnselected(int position) {
                Log.d(TAG, "onTabUnselected position="+position);
            }

            @Override
            public void onTabReselected(int position) {
                Log.d(TAG, "onTabReselected position="+position);
            }
        });
    }

    /**
     * 6.0 运行时权限申请
     */
    private void applyPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {
                new AlertDialog.Builder(this)
                        .setMessage("您拒绝过授予读取通讯录的权限,但是只有申请该权限,才能查询通讯录,你确定要重新申请获取权限吗？")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //again request permission
                                ActivityCompat.requestPermissions(IndexActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        Constant.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    /**
     * 隐藏toolbar
     */
    public void hideToolbar(){
        getSupportActionBar().hide();
    }

    /**
     * 显示toolbar
     */
    public void showToolbar(){
        getSupportActionBar().show();
    }

    /**
     * 长按联系人列表 item  进入ActionMode
     */
    public void showActionMode(){
        if (actionMode != null) {
            return ;
        }
        actionMode = startSupportActionMode(new MyCallback());
        actionMode.setTitle("已选(0)个");
    }

    @Override
    public void finish() {
        //  super.finish();
        moveTaskToBack(true);
    }

    @Override
    public void onBackPressed() {
        if(!supportActionBar.isShowing()){
            Log.d(TAG, "onBackPressed");
            supportActionBar.show();
            if(contactsFragment != null){
                contactsFragment.hideCancelTv();
            }
        }else{
            super.onBackPressed();
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
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    //   writeDatasToExternalStorage();

                } else {
                    // Permission Denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
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


    private class MyCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            Log.d(TAG,"onCheckedChanged  item = "+item.getItemId()+" "+item.getTitle());
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_multi_none_select) {
               // Toast.makeText(IndexActivity.this,"单选复选",Toast.LENGTH_SHORT).show();
                if(contactsFragment != null){
                    if(contactsFragment.isAllSelected()){
                        contactsFragment.setAllSelected(false);
                        item.setIcon(R.drawable.iab_multi_select);
                    }else {
                        contactsFragment.setAllSelected(true);
                        item.setIcon(R.drawable.iab_multi_none_select);
                    }
                }
                return true;
            }
            // mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG,"onDestroyActionMode = ");
            if(contactsFragment != null){
                contactsFragment.hideCheckBox();
            }
            actionMode = null;
        }
    }

    /**
     * 长按联系人列表 item  选择某个item时 通知选中的个数
     * @param count
     */
    public void notifyCheckedItem(int count,boolean isAllSelected){
        if(actionMode != null){
            actionMode.setTitle("已选("+count+")个");
            MenuItem item = actionMode.getMenu().findItem(R.id.action_multi_none_select);
            if(!isAllSelected ){
                item.setIcon(R.drawable.iab_multi_select);
            }else{
                item.setIcon(R.drawable.iab_multi_none_select);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "IndexActivity onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "IndexActivity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "IndexActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "IndexActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "IndexActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "IndexActivity onDestroy");
    }


}
