package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.simplecontacts.R;
import com.cj.simplecontacts.enity.CallRecord;
import com.cj.simplecontacts.tool.TimeUtil;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-7-27.
 */

public class CallRecordAdapter extends RecyclerView.Adapter<CallRecordAdapter.ViewHolder>{
    private ArrayList<CallRecord> list;
    private Context context;

    public CallRecordAdapter(ArrayList<CallRecord> list,Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.call_record_item, parent, false);
        CallRecordAdapter.ViewHolder vh = new CallRecordAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallRecord callRecord = list.get(position);
        String accountId = callRecord.getAccountId();
        String number = callRecord.getNumber();
        long date = callRecord.getDate();
        int type = callRecord.getType();
        String name = callRecord.getName();
        Log.d("test","getType  type:"+type);
        Log.d("test","getAccountId  accountId:"+accountId);
        switch (type){
            case CallLog.Calls.INCOMING_TYPE:
                holder.photo.setImageResource(R.drawable.ic_call_log_list_outgoing_call);
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                holder.photo.setImageResource(R.drawable.ic_call_log_list_default_call);
                break;
            case CallLog.Calls.MISSED_TYPE:
                holder.photo.setImageResource(R.drawable.ic_call_log_list_missed_call);
                break;
            default:
                break;
        }

        holder.date.setText(TimeUtil.timeCompare(date));
        if(TextUtils.isEmpty(name)){
            holder.name.setText(number);
            holder.number.setText("上海");
        }else{
            holder.name.setText(name);
            holder.number.setText(number);
        }
        holder.sim.setText(accountId);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView sim;
        public TextView name;
        public TextView number;
        public ImageView photo;
        public android.view.View View;
        public ViewHolder(View itemView) {
            super(itemView);
            View = itemView;
            date = (TextView) itemView.findViewById(R.id.call_date);
            sim = (TextView) itemView.findViewById(R.id.call_sim);
            name = (TextView) itemView.findViewById(R.id.call_name);
            number = (TextView) itemView.findViewById(R.id.call_number);
            photo = (ImageView) itemView.findViewById(R.id.call_log_iv);
        }
    }
}
