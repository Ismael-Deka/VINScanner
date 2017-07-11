package com.example.vinscanner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ismael on 5/11/2017.
 */

public class CarRecallAdapter extends RecyclerView.Adapter<CarRecallAdapter.ViewHolder> {

    private ArrayList<RecallAttribute> mRecallInfo;
    private Context mContext;

    public CarRecallAdapter(ArrayList<RecallAttribute> newRecallInfo, Context newContext){
        mRecallInfo = newRecallInfo;
        mContext = newContext;

    }

    public Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView componentTextView;
        public TextView summaryTextView;
        public TextView consequenceTextView;
        private TextView remedyTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            componentTextView = (TextView) itemView.findViewById(R.id.component);
            summaryTextView = (TextView) itemView.findViewById(R.id.summary);
            consequenceTextView = (TextView) itemView.findViewById(R.id.consequence);
            remedyTextView = (TextView) itemView.findViewById(R.id.remedy);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.recall_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CarRecallAdapter.ViewHolder holder, int position) {

        RecallAttribute info = mRecallInfo.get(position);

        TextView component = holder.componentTextView;
        TextView summary = holder.summaryTextView;
        TextView consequence = holder.consequenceTextView;
        TextView remedy = holder.remedyTextView;

        component.setText(info.getComponent());
        summary.setText(info.getSummary());
        consequence.setText(info.getConsequence());
        remedy.setText(info.getRemedy());


    }

    @Override
    public int getItemCount() {
        return mRecallInfo.size();
    }
}
