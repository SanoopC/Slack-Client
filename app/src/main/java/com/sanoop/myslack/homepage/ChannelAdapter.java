package com.sanoop.myslack.homepage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanoop.myslack.R;

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.MyViewHolder> {

    private Context mContext;
    private List<ChannelModel> channelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView channelTitleTextView, sederNameTextView, messageTextView;

        public MyViewHolder(View view) {
            super(view);
            channelTitleTextView = (TextView) view.findViewById(R.id.channel_title_text_view);
            sederNameTextView = (TextView) view.findViewById(R.id.sender_name_text_view);
            messageTextView = (TextView) view.findViewById(R.id.message_text_view);
        }
    }


    public ChannelAdapter(Context mContext, List<ChannelModel> channelList) {
        this.mContext = mContext;
        this.channelList = channelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ChannelModel channelModel = channelList.get(position);
        holder.channelTitleTextView.setText(channelModel.getChannelName());
        holder.sederNameTextView.setText(channelModel.getSenderName());
        holder.messageTextView.setText(channelModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }
}
