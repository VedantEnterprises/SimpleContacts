package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Contact c = list.get(position);
        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowCheckBox){
                    holder.cb.setChecked(!holder.cb.isChecked());
                    return;
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
                if(isShowCheckBox){
                    holder.cb.setChecked(!holder.cb.isChecked());
                    return true;
                }
                if(listener != null){
                    listener.onLongClick(position);
                }
                //Toast.makeText(context,"长按",Toast.LENGTH_SHORT).show();
                isShowCheckBox = isShowCheckBox?isShowCheckBox:true;
                ContactAdapter.this.notifyDataSetChanged();
                return true;
            }
        });
        if(c != null){
            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d("test","onCheckedChanged pos="+position+"  isChecked="+isChecked);
                    c.setChecked(isChecked);
                    if(listener != null){
                        listener.onItemChecked(position);
                    }
                }
            });

            holder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Toast.makeText(context,"clic cb position="+position,Toast.LENGTH_SHORT).show();
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
            ArrayList<String> numbers = c.getNumbers();
            if(numbers != null && numbers.size() > 0){
                String s = numbers.get(0);
                SpannableStringBuilder builder1 = new SpannableStringBuilder(
                        s);
                if(TextUtils.isEmpty(key)) {
                    holder.number.setText(s);
                }else{
                    int index = s.indexOf(key);
                    if(index < 0){
                        holder.number.setText(s);
                    }else{
                        for(int i = 0;i<key.length();i++){
                            ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimaryDark));
                            builder1.setSpan(redSpan, i, i+1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        holder.number.setText(builder1);
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAllItemChecked(boolean checked){

        for(int i=0;i<list.size();i++){
            list.get(i).setChecked(checked);
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
            if(list.get(i).isChecked()){
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
        void onLongClick(int position);
        void onItemChecked(int position);
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView section;
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
            name = (TextView) v.findViewById(R.id.name);
            number = (TextView) v.findViewById(R.id.number);
            photo = (ImageView) v.findViewById(R.id.photo);
            cb = (CheckBox) v.findViewById(R.id.cb);
            ll = (LinearLayout) v.findViewById(R.id.ll);
        }
    }
}
