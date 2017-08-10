package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cj.simplecontacts.R;
import com.cj.simplecontacts.enity.Message;
import com.cj.simplecontacts.tool.TimeUtil;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-8-9.
 */

public class SmsDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Message> list;
    private Context context;
    public SmsDetailAdapter(ArrayList<Message> list, Context context){
        this.list = list;
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null ;
        Log.d("test","onCreateViewHolder  viewType:"+viewType);
        if(viewType == 1){//receive
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.sms_item_recive, parent, false);
            holder = new ReceiveViewHolder(v);
        }else if(viewType == 2){//send
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.sms_item_send, parent, false);
            holder = new SendViewHolder(v);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = list.get(position);
        long date = message.getDate();
        String body = message.getBody();
        int subID = message.getSubID();
//        Log.d("test","getDate  date:"+date);
//        Log.d("test","getBody  body:"+body);
//        Log.d("test","getSubID  subID:"+subID);
        String sim = "";
        if(subID == 2){
            sim = "卡1";
        }else if(subID == 3){
            sim = "卡2";
        }else{
            sim = "";
        }
        if(holder instanceof ReceiveViewHolder){
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder)holder;
            receiveViewHolder.date.setText(TimeUtil.timeCompare(date));
            if(TextUtils.isEmpty(sim)){
                receiveViewHolder.sim.setVisibility(View.INVISIBLE);
            }else {
                receiveViewHolder.sim.setVisibility(View.VISIBLE);
                receiveViewHolder.sim.setText(sim);
            }
            receiveViewHolder.body.setText(body);

        }else if(holder instanceof SendViewHolder){
            SendViewHolder sendViewHolder = (SendViewHolder)holder;
            sendViewHolder.date.setText(TimeUtil.timeCompare(date));
            if(TextUtils.isEmpty(sim)){
                sendViewHolder.sim.setVisibility(View.INVISIBLE);
            }else {
                sendViewHolder.sim.setVisibility(View.VISIBLE);
                sendViewHolder.sim.setText(sim);
            }
            sendViewHolder.body.setText(body);
        }

    }

    @Override
    public int getItemViewType(int position) {

        return list.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ReceiveViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView sim;
        public TextView body;
        public android.view.View view;
        public ReceiveViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            date = (TextView) itemView.findViewById(R.id.sms_receive_date);
            sim = (TextView) itemView.findViewById(R.id.sms_receive_sim);
            body = (TextView) itemView.findViewById(R.id.sms_receive_body);
        }
    }
    public  class SendViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView sim;
        public TextView body;
        public android.view.View view;
        public SendViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            date = (TextView) itemView.findViewById(R.id.sms_send_date);
            sim = (TextView) itemView.findViewById(R.id.sms_send_sim);
            body = (TextView) itemView.findViewById(R.id.sms_send_body);
        }
    }
}
