package com.ismaelDeka.vinscanner.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.car.CarComplaintAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.ismaelDeka.vinscanner.R.id.date;


/**
 * Created by Ismael on 11/11/2017.
 */

public class CarComplaintAdapter extends RecyclerView.Adapter<CarComplaintAdapter.ViewHolder> {

    private final ArrayList<CarComplaintAttribute> mComplaints;

    private final HashMap<String,Boolean> isViewExpanded = new HashMap<>();
    private final Context mContext;

    public CarComplaintAdapter(ArrayList<CarComplaintAttribute> newRecallInfo, Context newContext){
        mComplaints = newRecallInfo;
        Collections.reverse(mComplaints);
        mContext = newContext;
        for(int i = 0; i < mComplaints.size();i++){

            isViewExpanded.put(mComplaints.get(i).getODINumber(),false);
        }
    }

    public Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView componentTextView;
        public TextView summaryTextView;
        public TextView numInjuredTextView;
        public TextView numDeadTextView;
        public TextView datafileTextView;
        public TextView dateIncidentTextView;
        public TextView fireTextView;
        public TextView crashTextView;
        public ImageView showMoreImage;
        public CardView complaintCardView;



        public ViewHolder(View itemView) {
            super(itemView);
            componentTextView = itemView.findViewById(R.id.component);
            datafileTextView = itemView.findViewById(date);
            summaryTextView = itemView.findViewById(R.id.summary);
            numInjuredTextView = itemView.findViewById(R.id.num_injured);
            numDeadTextView = itemView.findViewById(R.id.num_death);
            dateIncidentTextView = itemView.findViewById(R.id.date_incident);
            fireTextView = itemView.findViewById(R.id.fire);
            crashTextView = itemView.findViewById(R.id.crash);
            showMoreImage = itemView.findViewById(R.id.show_more);
            complaintCardView = itemView.findViewById(R.id.complaint_card);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.car_complaint_list_item, parent, false);

        return new ViewHolder(contactView);
    }


    @Override
    public int getItemCount() {
        return mComplaints.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final CarComplaintAttribute info = mComplaints.get(position);


        TextView component = holder.componentTextView;

        TextView date = holder.datafileTextView;
        final TextView dateIncident = holder.dateIncidentTextView;
        final TextView crash = holder.crashTextView;
        final TextView fire = holder.fireTextView;
        final TextView summary = holder.summaryTextView;
        final TextView numInjured = holder.numInjuredTextView;
        final TextView numDead = holder.numDeadTextView;
        CardView cardView = holder.complaintCardView;
        final ImageView showMore = holder.showMoreImage;


        String infoComponent = info.getComponent();
        infoComponent = infoComponent.charAt(0) + infoComponent.substring(1).toLowerCase();

        component.setText(Html.fromHtml("<b>Complaint Subject:</b>" + "\n" + infoComponent));
        date.setText(info.getDateFiled());
        summary.setText(Html.fromHtml("<b>Summary:</b>" + "\n" + info.getSummary()));
        numInjured.setText(Html.fromHtml("<b>Number of Injuries:</b>" + "\n" + info.getNumberInjured()));
        numDead.setText(Html.fromHtml("<b>Number of Deaths:</b>" + "\n" + info.getNumberDeaths()));
        dateIncident.setText(Html.fromHtml("<b>Date of Incident:</b>" + "\n" + info.getDateIncident()));
        crash.setText(Html.fromHtml("<b>Crash?:</b>" + "\n" + info.getCrash()));
        fire.setText(Html.fromHtml("<b>Fire?:</b>" + "\n" + info.getFire()));

        if (Boolean.TRUE.equals(isViewExpanded.get(info.getODINumber()))) {
            summary.setVisibility(View.VISIBLE);
            numInjured.setVisibility(View.VISIBLE);
            numDead.setVisibility(View.VISIBLE);
            crash.setVisibility(View.VISIBLE);
            fire.setVisibility(View.VISIBLE);
            showMore.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        } else {
            summary.setVisibility(View.GONE);
            numInjured.setVisibility(View.GONE);
            numDead.setVisibility(View.GONE);
            crash.setVisibility(View.GONE);
            fire.setVisibility(View.GONE);
            showMore.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Boolean.FALSE.equals(isViewExpanded.get(info.getODINumber()))) {
                    summary.setVisibility(View.VISIBLE);
                    numInjured.setVisibility(View.VISIBLE);
                    numDead.setVisibility(View.VISIBLE);
                    crash.setVisibility(View.VISIBLE);
                    fire.setVisibility(View.VISIBLE);
                    showMore.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    setViewOpen(true, info.getODINumber());

                } else {
                    summary.setVisibility(View.GONE);
                    numInjured.setVisibility(View.GONE);
                    numDead.setVisibility(View.GONE);
                    crash.setVisibility(View.GONE);
                    fire.setVisibility(View.GONE);
                    showMore.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    setViewOpen(false, info.getODINumber());

                }
            }
        });

    }

    public void setViewOpen(boolean isOpen,String c){
        isViewExpanded.remove(c);
        isViewExpanded.put(c,isOpen);
    }




}
