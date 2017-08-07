package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.R;
import com.cj.simplecontacts.enity.CallRecord;
import com.cj.simplecontacts.enity.Message;
import com.cj.simplecontacts.tool.TimeUtil;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-8-2.
 */

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.ViewHolder>{
    private ArrayList<Message> list;
    private Context context;
    private boolean isShowCheckBox = false;
    private String key;

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    public SmsAdapter(ArrayList<Message> list, Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.sms_item, parent, false);
        SmsAdapter.ViewHolder vh = new SmsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Message m = list.get(position);
        String address = m.getAddress();
        String person = m.getPerson();
        String body = m.getBody();
        long date = m.getDate();
        boolean checked = m.isChecked();

        String name = TextUtils.isEmpty(person)?address:person;

        if(TextUtils.isEmpty(key)) {
            holder.name.setText(name);
            holder.body.setText(body);
        }else{
            SpannableStringBuilder nameBuilder = new SpannableStringBuilder(
                    name);
            int nameIndex = name.indexOf(key);
            if(nameIndex < 0){
                holder.name.setText(name);
            }else{
                for(int i = nameIndex;i<key.length()+nameIndex;i++){
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent));
                    nameBuilder.setSpan(redSpan, i, i+1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.name.setText(nameBuilder);
            }


            int bodyIndex = body.indexOf(key);
            if(bodyIndex < 0){
                holder.body.setText(body);
            }else{
                int start = bodyIndex-6<0 ? 0:bodyIndex-6;
                body = body.substring(start);
                if(start!=0){
                    body = "..."+body;
                }
                int bodyFinalIndex = body.indexOf(key);
                SpannableStringBuilder bodyBuilder = new SpannableStringBuilder(
                        body);

                for(int i = bodyFinalIndex;i<key.length()+bodyFinalIndex;i++){
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent));
                    bodyBuilder.setSpan(redSpan, i, i+1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.body.setText(bodyBuilder);
            }

        }

        holder.date.setText(TimeUtil.timeCompare(date));
        if(isShowCheckBox){
            holder.cb.setVisibility(View.VISIBLE);
            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    m.setChecked(isChecked);
                    if(listener != null){
                        listener.onItemChecked(m,buttonView);
                    }
                }
            });
            holder.cb.setChecked(checked);
        }else {
            holder.cb.setVisibility(View.GONE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"点击打电话",Toast.LENGTH_SHORT).show();
                if(isShowCheckBox){
                    holder.cb.setChecked(!holder.cb.isChecked());
                    return;
                }
                if(listener != null){
                    listener.onItemClick(m);
                }
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener != null){
                    listener.onLongClick(m,v);
                }
                isShowCheckBox = isShowCheckBox?isShowCheckBox:true;
                SmsAdapter.this.notifyDataSetChanged();
                return true;
            }
        });
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setList(ArrayList<Message> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public int getCheckedCount(){
        int count = 0;
        for(int i=0;i<list.size();i++){
            Message message = list.get(i);
            if(message.isChecked()){
                count++;
            }
        }
        return count;
    }

    public void setAllItemChecked(boolean checked){

        for(int i=0;i<list.size();i++){
            list.get(i).setChecked(checked);
        }
        notifyDataSetChanged();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView name;
        public TextView body;
        public ImageView photo;
        public CheckBox cb;
        public android.view.View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            date = (TextView) itemView.findViewById(R.id.sms_date);
            name = (TextView) itemView.findViewById(R.id.sms_address);
            body = (TextView) itemView.findViewById(R.id.sms_body);
            photo = (ImageView) itemView.findViewById(R.id.sms_iv);
            cb = (CheckBox) itemView.findViewById(R.id.sms_cb);
        }
    }

    private SmsAdapter.ReclerViewItemListener listener;
    public  void setReclerViewItemListener(SmsAdapter.ReclerViewItemListener listener){
        this.listener = listener;
    }

    public interface ReclerViewItemListener{
        void onItemClick(Message m);
        void onLongClick(Message m,View v);
        void onItemChecked(Message m,View v);
    }

}
