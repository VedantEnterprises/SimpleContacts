package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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

    public SmsAdapter(ArrayList<Message> list,Context context){
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message m = list.get(position);
        String address = m.getAddress();
        String person = m.getPerson();
        String body = m.getBody();
        long date = m.getDate();
        if(TextUtils.isEmpty(person)){
            holder.name.setText(address);
        }else{
            holder.name.setText(person);
        }
        holder.body.setText(body);
        holder.date.setText(TimeUtil.timeCompare(date));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView name;
        public TextView body;
        public ImageView photo;
        public android.view.View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            date = (TextView) itemView.findViewById(R.id.sms_date);
            name = (TextView) itemView.findViewById(R.id.sms_address);
            body = (TextView) itemView.findViewById(R.id.sms_body);
            photo = (ImageView) itemView.findViewById(R.id.sms_iv);
        }
    }

}
