package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.R;
import com.cj.simplecontacts.enity.Contact;
import com.cj.simplecontacts.enity.Number;
import com.cj.simplecontacts.tool.Constant;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-7-5.
 */

public class ContactAdapter extends  RecyclerView.Adapter<ContactAdapter.ViewHolder> implements SectionIndexer {
    private ArrayList<Contact> list;
    private Context context;
    private boolean isShowCheckBox = false;
    private String key;

    public ContactAdapter(ArrayList<Contact> list,Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.contact_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    boolean isPressed = false;
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Contact c = list.get(position);
        holder.ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        if(isPressed){
                           // Toast.makeText(context,"长按松开:"+c.getName(),Toast.LENGTH_SHORT).show();
                            if(listener != null){
                                listener.onLongClick(position,view);
                            }
                            isPressed = false;
                        }
                        break;
                }
                return false;
            }
        });
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(c.isContact()){
                    if(isShowCheckBox){
                        holder.cb.setChecked(!holder.cb.isChecked());
                        return;
                    }
                }else {

                   // Toast.makeText(context,"点击"+c.getName(),Toast.LENGTH_SHORT).show();
                }
                if(listener != null){
                    listener.onItemClick(position);
                }
                // Toast.makeText(context,"点击 pos="+position,Toast.LENGTH_SHORT).show();

            }
        });
        holder.ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!c.isContact()){
                    Toast.makeText(context,"长按"+c.getName(),Toast.LENGTH_SHORT).show();
                    isPressed = true;
                    return true;
                }
                if(isShowCheckBox){
                    holder.cb.setChecked(!holder.cb.isChecked());
                    return true;
                }
                if(listener != null){
                    listener.onLongClick(position,v);
                }
                //Toast.makeText(context,"长按",Toast.LENGTH_SHORT).show();
                isShowCheckBox = isShowCheckBox?isShowCheckBox:true;
                ContactAdapter.this.notifyDataSetChanged();
                return true;
            }
        });

        if(!c.isContact()){//联系人助手  和 我的分组
            holder.number.setVisibility(View.GONE);
            holder.section.setVisibility(View.GONE);
            holder.cb.setVisibility(View.GONE);

            holder.name.setText(c.getName());
            if(Constant.CONTACT_ASSISTANT.equals(c.getName())){
                holder.helper.setVisibility(View.VISIBLE);
                holder.photo.setImageResource(R.drawable.ic_contact_sort);
            }else if(Constant.CONTACT_GROUP.equals(c.getName())){
                holder.photo.setImageResource(R.drawable.ic_contact_group);
                holder.helper.setVisibility(View.GONE);
            }
        }else{

            holder.number.setVisibility(View.VISIBLE);
            holder.section.setVisibility(View.VISIBLE);
            holder.cb.setVisibility(View.VISIBLE);
            holder.helper.setVisibility(View.GONE);
            holder.photo.setImageResource(R.drawable.default_contact_head_icon);

            //根据position获取分类的首字母的char ascii值
            int section = getSectionForPosition(position);

            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  //  Log.d("test","onCheckedChanged pos="+position+"  isChecked="+isChecked);
                    c.setChecked(isChecked);
                    if(listener != null){
                        listener.onItemChecked(position,buttonView);
                    }
                }
            });

            if(isShowCheckBox){
                holder.cb.setVisibility(View.VISIBLE);
                if(c.isChecked()){
                    holder.cb.setChecked(true);
                }else {
                    holder.cb.setChecked(false);
                }
            }else {
                holder.cb.setVisibility(View.GONE);
            }


            SpannableStringBuilder builder = new SpannableStringBuilder(
                    c.getName());


            if(TextUtils.isEmpty(key)){
                holder.name.setText(c.getName());
            }else {
                ArrayList<Integer> list = c.getKeyIndexList(key);
                //Log.d("test","name:"+c.getName());
                for(int i=0;i<list.size();i++){
                    Integer integer = list.get(i);
                    //   Log.d("test","integer"+integer.intValue());
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimaryDark));
                    builder.setSpan(redSpan, integer.intValue()-1, integer.intValue(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.name.setText(builder);
            }

            if(position == getPositionForSection(section)){
                holder.section.setVisibility(View.VISIBLE);
                holder.section.setText(c.getPhonebookLabel());
            }else{
                holder.section.setVisibility(View.GONE);
            }

            ArrayList<Number> numbers = c.getNumbers();
            int size = numbers.size();
           // Log.d("test","numbers  size:"+size+"  name="+c.getName());
            if(numbers != null && size > 0){
                Number num = numbers.get(0);
                String s = num.getNum();
                SpannableStringBuilder builder1 = new SpannableStringBuilder(
                        s);
                if(TextUtils.isEmpty(key)) {

                    holder.number.setText(size==1?s:s+" 多号码");
                }else{
                    int index = s.indexOf(key);
                    if(index < 0){
                        holder.number.setText(size==1?s:s+" 多号码");
                    }else{
                        for(int i = 0;i<key.length();i++){
                            ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimaryDark));
                            builder1.setSpan(redSpan, i, i+1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        holder.number.setText(size==1?builder1:builder1+" 多号码");
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public boolean isHaveNotContact(){
        for (int i=0;i<list.size();i++){
            if(!list.get(i).isContact()){
                return true;
            }
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAllItemChecked(boolean checked){

        for(int i=0;i<list.size();i++){
            Contact contact = list.get(i);
            if(contact.isContact()){
                contact.setChecked(checked);
            }
        }
        notifyDataSetChanged();
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public int getCheckedCount(){
        int count = 0;
        for(int i=0;i<list.size();i++){
            Contact contact = list.get(i);
            if(contact.isContact() && contact.isChecked()){
                count++;
            }
        }
        return count;
    }

    public void setList(ArrayList<Contact> list) {
        this.list = list;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getItemCount(); i++) {
            Contact c = list.get(i);
            String firstLetter = c.getPhonebookLabel();
            if(!TextUtils.isEmpty(firstLetter)){
                char firstChar = firstLetter.toUpperCase().charAt(0);
                if (firstChar == sectionIndex) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        if(position >= list.size() ||position < 0 ){
            return 0;
        }
        Contact c = list.get(position);
        if(c != null){
            String firstLetter = c.getPhonebookLabel();
            if(!TextUtils.isEmpty(firstLetter)){
                return firstLetter.charAt(0);
            }
        }
        return 0;
    }

    private ReclerViewItemListener listener;
    public  void setReclerViewItemListener(ReclerViewItemListener listener){
        this.listener = listener;
    }
    public interface ReclerViewItemListener{
        void onItemClick(int position);
        void onLongClick(int position,View v);
        void onItemChecked(int position,View v);
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView section;
        public TextView helper;
        public TextView name;
        public TextView number;
        public ImageView photo;
        public CheckBox cb;
        public LinearLayout ll;

        public android.view.View View;
        public ViewHolder(View v) {
            super(v);
            View = v;
            section = (TextView) v.findViewById(R.id.section);
            helper = (TextView) v.findViewById(R.id.contact_help);
            name = (TextView) v.findViewById(R.id.name);
            number = (TextView) v.findViewById(R.id.number);
            photo = (ImageView) v.findViewById(R.id.photo);
            cb = (CheckBox) v.findViewById(R.id.cb);
            ll = (LinearLayout) v.findViewById(R.id.ll);
        }
    }
}
