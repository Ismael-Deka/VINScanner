package com.example.vinscanner;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ismael on 5/11/2017.
 */

public class CarRecallAdapter extends RecyclerView.Adapter<CarRecallAdapter.ViewHolder> {

    private ArrayList<RecallAttribute> mRecallInfo;
    private HashMap<String,Boolean> isViewExpaned = new HashMap<>();
    private Context mContext;

    public CarRecallAdapter(ArrayList<RecallAttribute> newRecallInfo, Context newContext){
        mRecallInfo = newRecallInfo;
        mContext = newContext;
        for(int i = 0; i < mRecallInfo.size();i++){
            isViewExpaned.put(mRecallInfo.get(i).getCampaignNumber(),false);
        }
    }

    public Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView componentTextView;
        public TextView campaignTextView;
        public TextView summaryTextView;
        public TextView consequenceTextView;
        public TextView remedyTextView;
        public TextView dateTextView;
        public ImageView showMoreImage;
        public CardView recallCardView;



        public ViewHolder(View itemView) {
            super(itemView);
            componentTextView = (TextView) itemView.findViewById(R.id.component);
            campaignTextView = (TextView) itemView.findViewById(R.id.campaign);
            dateTextView = (TextView) itemView.findViewById(R.id.date);
            summaryTextView = (TextView)itemView.findViewById(R.id.summary);
            consequenceTextView = (TextView)itemView.findViewById(R.id.consequence);
            remedyTextView = (TextView)itemView.findViewById(R.id.remedy);
            showMoreImage = (ImageView)itemView.findViewById(R.id.show_more);
            recallCardView = (CardView) itemView.findViewById(R.id.recall_card);

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

        final RecallAttribute info = mRecallInfo.get(position);


        TextView component = holder.componentTextView;
        TextView campaign = holder.campaignTextView;
        TextView date = holder.dateTextView;
        final TextView summary = holder.summaryTextView;
        final TextView consequence = holder.consequenceTextView;
        final TextView remedy = holder.remedyTextView;
        CardView cardView = holder.recallCardView;
        final ImageView showMore = holder.showMoreImage;




        String infoComponent = info.getComponent();
        infoComponent= infoComponent.substring(0,1)+infoComponent.substring(1,infoComponent.length()).toLowerCase();

        component.setText(infoComponent);
        campaign.setText("Campaign Number: "+info.getCampaignNumber());
        date.setText(info.getDate());
        summary.setText(info.getSummary());
        consequence.setText(info.getConsequence());
        remedy.setText(info.getRemedy());

        if(isViewExpaned.get(info.getCampaignNumber())) {
            summary.setVisibility(View.VISIBLE);
            consequence.setVisibility(View.VISIBLE);
            remedy.setVisibility(View.VISIBLE);
            showMore.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }else {
            summary.setVisibility(View.GONE);
            consequence.setVisibility(View.GONE);
            remedy.setVisibility(View.GONE);
            showMore.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isViewExpaned.get(info.getCampaignNumber())){
                    summary.setVisibility(View.VISIBLE);
                    consequence.setVisibility(View.VISIBLE);
                    remedy.setVisibility(View.VISIBLE);
                    showMore.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    setViewOpen(true,info.getCampaignNumber());

                }else{
                    summary.setVisibility(View.GONE);
                    consequence.setVisibility(View.GONE);
                    remedy.setVisibility(View.GONE);
                    showMore.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    setViewOpen(false,info.getCampaignNumber());

                }
            }
        });




    }

    public void setViewOpen(boolean isOpen,String c){
        isViewExpaned.remove(c);
        isViewExpaned.put(c,isOpen);
    }

    @Override
    public int getItemCount() {
        return mRecallInfo.size();
    }
}
