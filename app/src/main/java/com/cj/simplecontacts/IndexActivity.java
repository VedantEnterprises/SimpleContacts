package com.cj.simplecontacts;


import android.Manifest;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends AppCompatActivity {
    private final static String TAG = "IndexActivity";


    private BottomNavigationBar bottomNavigationBar;
    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private ActionMode contactActionMode;
    private ActionMode smsActionMode;
    private ActionMode dialActionMode;
    private DialFragment dialFragment;
    private ContactsFragment contactsFragment;
    private MessageFragment messageFragment;
    private LifeAssistantFragment lifeAssistantFragment;
    private boolean isExceptionExit = false;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IndexActivity onCreate");
        setContentView(R.layout.activity_index);
        exitByBack = false;
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
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.overflow_btn_bg));
    }

    private void setUpFragment() {
        fragmentManager = getSupportFragmentManager();
        dialFragment = new DialFragment();
        contactsFragment = new ContactsFragment();
        messageFragment = new MessageFragment();
        lifeAssistantFragment = new LifeAssistantFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fg_container,dialFragment,Constant.DIAL_FRAGMENT);
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

                        Fragment cf00 = fragmentManager.findFragmentByTag(Constant.CONTACTS_FRAGMENT);
                        if(cf00 != null){
                            transaction0.hide(contactsFragment);
                            contactsFragment.onHide();
                        }

                        Fragment mf00 = fragmentManager.findFragmentByTag(Constant.MESSAGE_FRAGMENT);
                        if(mf00 != null){
                            transaction0.hide(messageFragment);
                            messageFragment.onHide();
                        }

                        Fragment df00 = fragmentManager.findFragmentByTag(Constant.DIAL_FRAGMENT);
                        if(df00 != null){
                            transaction0.show(dialFragment);
                        }else{

                            transaction0.add(R.id.fg_container,dialFragment,Constant.DIAL_FRAGMENT);
                        }


                        transaction0.remove(lifeAssistantFragment);
                        transaction0.commit();
                        break;
                    case 1:
                        FragmentTransaction transaction1 = fragmentManager.beginTransaction();

                        Fragment df01 = fragmentManager.findFragmentByTag(Constant.DIAL_FRAGMENT);
                        if(df01 != null){
                            transaction1.hide(dialFragment);
                        }
                        Fragment mf01 = fragmentManager.findFragmentByTag(Constant.MESSAGE_FRAGMENT);
                        if(mf01 != null){
                            transaction1.hide(messageFragment);
                            messageFragment.onHide();
                        }

                        Fragment cf01 = fragmentManager.findFragmentByTag(Constant.CONTACTS_FRAGMENT);
                        if(cf01 != null){
                            transaction1.show(contactsFragment);
                        }else {
                            transaction1.add(R.id.fg_container,contactsFragment,Constant.CONTACTS_FRAGMENT);
                        }


                        transaction1.remove(lifeAssistantFragment);
                        transaction1.commit();
                        break;
                    case 2:
                        FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                        Fragment cf02 = fragmentManager.findFragmentByTag(Constant.CONTACTS_FRAGMENT);
                        if(cf02 != null){
                            transaction2.hide(contactsFragment);
                            contactsFragment.onHide();
                        }

                        Fragment df02 = fragmentManager.findFragmentByTag(Constant.DIAL_FRAGMENT);
                        if(df02 != null){
                            transaction2.hide(dialFragment);
                        }

                        Fragment mf02 = fragmentManager.findFragmentByTag(Constant.MESSAGE_FRAGMENT);
                        if(mf02 != null){
                            transaction2.show(messageFragment);
                        }else {
                            transaction2.add(R.id.fg_container,messageFragment,Constant.MESSAGE_FRAGMENT);
                        }

                        transaction2.remove(lifeAssistantFragment);
                        transaction2.commit();
                        break;
                    case 3:
                        FragmentTransaction transaction3 = fragmentManager.beginTransaction();
                        Fragment cf03 = fragmentManager.findFragmentByTag(Constant.CONTACTS_FRAGMENT);
                        if(cf03 != null){
                            transaction3.hide(contactsFragment);
                            contactsFragment.onHide();
                        }

                        Fragment df03 = fragmentManager.findFragmentByTag(Constant.DIAL_FRAGMENT);
                        if(df03 != null){
                            transaction3.hide(dialFragment);
                        }

                        Fragment mf03 = fragmentManager.findFragmentByTag(Constant.MESSAGE_FRAGMENT);
                        if(mf03 != null){
                            transaction3.hide(messageFragment);
                            messageFragment.onHide();
                        }

                        transaction3.add(R.id.fg_container,lifeAssistantFragment,Constant.LIFE_ASSISTANT_FRAGMENT);
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
        List<String> permissionsList = new ArrayList();
        for (String permission : Constant.PERMISSIONS_ARRAY) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if(permissionsList.size() == 0){
            return;
        }
        ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), Constant.MY_PERMISSIONS_REQUEST_CODE);

    }

    /**
     * 隐藏toolbar
     */
    public void hideToolbar(){
        supportActionBar.hide();
    }

    /**
     * 显示toolbar
     */
    public void showToolbar(){
        supportActionBar.show();
    }



    /**
     * 长按联系人列表 item  进入ActionMode
     */
    public void showActionMode(String fragment){

        if(Constant.CONTACTS_FRAGMENT.equals(fragment)){
            if (contactActionMode != null) {
                return ;
            }
            contactActionMode = startSupportActionMode(new ContactCallback());
            contactActionMode.setTitle("已选(0)个");
        }else if(Constant.DIAL_FRAGMENT.equals(fragment)){
            if (dialActionMode != null) {
                return ;
            }
            dialActionMode = startSupportActionMode(new DialCallback());
            dialActionMode.setTitle("已选(0)个");
        }else if(Constant.MESSAGE_FRAGMENT.equals(fragment)){
            if (smsActionMode != null) {
                return ;
            }
            smsActionMode = startSupportActionMode(new SmsCallback());
            smsActionMode.setTitle("已选(0)个");
        }
    }

    @Override
    public void finish() {
        if(isExceptionExit){
            super.finish();

        }else{
            moveTaskToBack(true);
        }

    }
    private boolean exitByBack = false;
    @Override
    public void onBackPressed() {
        if(!supportActionBar.isShowing()){
            Log.d(TAG, "onBackPressed");
            supportActionBar.show();
            if(contactsFragment != null){
                contactsFragment.hideSearchBarElement();
                contactsFragment.scrollToFirstPosition();
            }
            if(messageFragment != null){
                messageFragment.hideSearchBarElement();
                messageFragment.scrollToFirstPosition();
            }
        }else{
            super.onBackPressed();
            exitByBack = true;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("permission", "onRequestPermissionsResult  requestCode" + requestCode);
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_CODE: {
                for (int i=0; i<permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //Toast.makeText(this, permissions[i]+"Permission Granted！", Toast.LENGTH_SHORT).show();
                        if(dialFragment != null){
                            dialFragment.queryCallRecordFromDB();
                        }
                    } else {
                        //Toast.makeText(this, "权限被拒绝： "+permissions[i], Toast.LENGTH_SHORT).show();
                        isExceptionExit = true;

                        new AlertDialog.Builder(this)
                                .setTitle("提醒")
                        .setMessage("因为你未授权 "+permissions[i]+" 权限,所以该应用不能正常使用")

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    IndexActivity.this.finish();
                                }
                            })
                        .create()
                        .show();
                    }
                }
                return;
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    private class ContactCallback implements ActionMode.Callback {

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
          //  Log.d(TAG,"onCheckedChanged  item = "+item.getItemId()+" "+item.getTitle());
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

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG,"onDestroyActionMode = ");
            if(contactsFragment != null){
                contactsFragment.hideCheckBox();
                contactsFragment.hideSearchBarElement();
                contactsFragment.showAssistantAndGroup();
                contactsFragment.scrollToFirstPosition();
            }
            contactActionMode = null;
        }
    }

    private class DialCallback implements ActionMode.Callback {

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
            //  Log.d(TAG,"onCheckedChanged  item = "+item.getItemId()+" "+item.getTitle());
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_multi_none_select) {
                // Toast.makeText(IndexActivity.this,"单选复选",Toast.LENGTH_SHORT).show();
                if(dialFragment != null){
                    if(dialFragment.isAllSelected()){
                        dialFragment.setAllSelected(false);
                        item.setIcon(R.drawable.iab_multi_select);
                    }else {
                        dialFragment.setAllSelected(true);
                        item.setIcon(R.drawable.iab_multi_none_select);
                    }
                }
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG,"onDestroyActionMode = ");
            if(dialFragment != null) {
                dialFragment.hideCheckBox();
            }
            dialActionMode = null;

        }
    }

    public void finishDialActionMode(){
        dialActionMode.finish();
    }


    private class SmsCallback implements ActionMode.Callback {

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
            //  Log.d(TAG,"onCheckedChanged  item = "+item.getItemId()+" "+item.getTitle());
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_multi_none_select) {
                // Toast.makeText(IndexActivity.this,"单选复选",Toast.LENGTH_SHORT).show();
                if(messageFragment != null){
                    if(messageFragment.isAllSelected()){
                        messageFragment.setAllSelected(false);
                        item.setIcon(R.drawable.iab_multi_select);
                    }else {
                        messageFragment.setAllSelected(true);
                        item.setIcon(R.drawable.iab_multi_none_select);
                    }
                }
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG,"onDestroyActionMode = ");
            if(messageFragment != null) {
                messageFragment.hideCheckBox();
            }
            smsActionMode = null;
        }
    }

    public void finishSmsActionMode(){
        smsActionMode.finish();
    }

    /**
     * 长按联系人列表 item  选择某个item时 通知选中的个数
     * @param count
     */
    public void notifyCheckedItem(int count,boolean isAllSelected,String fragment){
        if(Constant.CONTACTS_FRAGMENT.equals(fragment)){
            if(contactActionMode != null){
                contactActionMode.setTitle("已选("+count+")个");
                MenuItem item = contactActionMode.getMenu().findItem(R.id.action_multi_none_select);
                if(!isAllSelected ){
                    item.setIcon(R.drawable.iab_multi_select);
                }else{
                    item.setIcon(R.drawable.iab_multi_none_select);
                }
            }
        }else if(Constant.DIAL_FRAGMENT.equals(fragment)){
            if(dialActionMode != null){
                dialActionMode.setTitle("已选("+count+")个");
                MenuItem item = dialActionMode.getMenu().findItem(R.id.action_multi_none_select);
                if(!isAllSelected ){
                    item.setIcon(R.drawable.iab_multi_select);
                }else{
                    item.setIcon(R.drawable.iab_multi_none_select);
                }
            }
        }else if(Constant.MESSAGE_FRAGMENT.equals(fragment)){
            if(smsActionMode != null){
                smsActionMode.setTitle("已选("+count+")个");
                MenuItem item = smsActionMode.getMenu().findItem(R.id.action_multi_none_select);
                if(!isAllSelected ){
                    item.setIcon(R.drawable.iab_multi_select);
                }else{
                    item.setIcon(R.drawable.iab_multi_none_select);
                }
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
        if(exitByBack){
            if(contactsFragment != null){
                contactsFragment.scrollToFirstPosition();
            }
            exitByBack = false;
        }
        if(messageFragment != null){
            messageFragment.notifyDataChange();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "IndexActivity onPause");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "IndexActivity onStop");


        if(!supportActionBar.isShowing()) {
            supportActionBar.show();
            if(contactsFragment != null){
                contactsFragment.hideSearchBarElement();
                contactsFragment.showAssistantAndGroup();
                contactsFragment.scrollToFirstPosition();
            }
            if(messageFragment != null){
                messageFragment.hideSearchBarElement();
                messageFragment.scrollToFirstPosition();
            }
        }
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "IndexActivity onDestroy");
    }


}
