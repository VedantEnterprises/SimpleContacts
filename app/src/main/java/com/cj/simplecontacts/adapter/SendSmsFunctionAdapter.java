package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cj.simplecontacts.R;
import com.cj.simplecontacts.enity.SmsFunction;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-8-11.
 */

public class SendSmsFunctionAdapter extends RecyclerView.Adapter<SendSmsFunctionAdapter.ViewHolder>{
    private ArrayList<SmsFunction> list;
    private Context context;
    public SendSmsFunctionAdapter(ArrayList<SmsFunction> list,Context context){
        this.list = list;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.send_function_item, parent, false);
        SendSmsFunctionAdapter.ViewHolder vh = new SendSmsFunctionAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SmsFunction smsFunction = list.get(position);
        holder.name.setText(smsFunction.getName());
        holder.btn.setImageDrawable(smsFunction.getDrawable());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton btn;
        public TextView name;
        public android.view.View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            btn = (ImageButton) itemView.findViewById(R.id.btn);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
