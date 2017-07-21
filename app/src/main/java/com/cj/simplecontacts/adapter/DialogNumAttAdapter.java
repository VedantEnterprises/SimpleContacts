package com.cj.simplecontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cj.simplecontacts.R;
import com.cj.simplecontacts.enity.Number;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-7-18.
 */

public class DialogNumAttAdapter extends  RecyclerView.Adapter<DialogNumAttAdapter.ViewHolder>{
    private ArrayList<Number> list;
    private Context context;

    public DialogNumAttAdapter(ArrayList<Number> list,Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.dialog_rv_item, parent, false);
        DialogNumAttAdapter.ViewHolder vh = new DialogNumAttAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Number number = list.get(position);
        String numAttribution = number.getNumAttribution();

        holder.dialogNum.setText(number.getNum());
        if(TextUtils.isEmpty(numAttribution)){
            holder.dialogRegion.setText("正在查询...");
        }else{
            holder.dialogRegion.setText(number.getNumAttribution());
        }

        holder.dialogSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onSendMsgClick(position);
                }
            }
        });

        holder.dialogCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onCallClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size()>3 ? 3:list.size();
    }

    private DialogNumAttAdapter.ReclerViewItemListener listener;
    public  void setReclerViewItemListener(DialogNumAttAdapter.ReclerViewItemListener listener){
        this.listener = listener;
    }
    public interface ReclerViewItemListener{
        void onCallClick(int position);
        void onSendMsgClick(int position);
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout dialogSendMsg;
        public FrameLayout dialogCall;
        public TextView dialogNum;
        public TextView dialogRegion;

        public ViewHolder(View itemView) {
            super(itemView);

            dialogSendMsg = (FrameLayout) itemView.findViewById(R.id.dialog_send_msg);
            dialogCall = (FrameLayout) itemView.findViewById(R.id.dialog_call);

            dialogNum = (TextView) itemView.findViewById(R.id.dialog_num);
            dialogRegion = (TextView) itemView.findViewById(R.id.dialog_region);
        }
    }


}
