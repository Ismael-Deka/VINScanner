package com.example.vinscanner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinscanner.R;
import com.example.vinscanner.car.CarAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Ismael on 10/5/2017.
 */

public class CarInfoAdapter extends RecyclerView.Adapter<CarInfoAdapter.ViewHolder> {
    private ArrayList<CarAttribute> mCarInfo;
    private HashMap<String,Boolean> isViewExpaned = new HashMap<>();
    private ArrayList<String> mCategories;
    private String mVin;
    private String mMsrp;
    private Context mContext;

    public CarInfoAdapter(String vin,String msrp,ArrayList<CarAttribute> newCarInfo, Context newContext){
        mCarInfo = newCarInfo;
        mContext = newContext;
        mVin = vin;
        mMsrp =msrp;
        mCategories = getCurrentCategories(newCarInfo);
        for(int i = 0; i < mCategories.size(); i++){
            isViewExpaned.put(mCategories.get(i),true);
        }
    }

    private ArrayList<String> getCurrentCategories(ArrayList<CarAttribute> info){
        ArrayList<String> categories = new ArrayList<>();

        for(int i =2; i<info.size(); i++){
            if(!categories.contains(info.get(i).getCategory())){
                categories.add(info.get(i).getCategory());
                Log.e("adapter",info.get(i).getCategory());
            }
        }
        Collections.sort(categories);
        int i = categories.indexOf("General");
        Collections.swap(categories,0,i);
        return categories;

    }

    public Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView categoriesTextView;
        public TextView attributesTextView;
        public ImageView showMoreImage;
        public CardView infoCardView;


        public ViewHolder(View itemView) {
            super(itemView);
            categoriesTextView = (TextView)itemView.findViewById(R.id.category);
            attributesTextView = (TextView)itemView.findViewById(R.id.attributes);
            showMoreImage = (ImageView)itemView.findViewById(R.id.show_more_info);
            infoCardView = (CardView) itemView.findViewById(R.id.info_card);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.car_info_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    private ArrayList<CarAttribute> getAttributesByCategory(String category){
        ArrayList<CarAttribute> attributes = new ArrayList<>();

        for(int i = 2; i < mCarInfo.size();i++){
            if(mCarInfo.get(i).getCategory().equals(category)){
                attributes.add(mCarInfo.get(i));
            }
        }
        return attributes;
    }

    @Override
    public void onBindViewHolder(CarInfoAdapter.ViewHolder holder, int position) {

        final ArrayList<CarAttribute> info = getAttributesByCategory(mCategories.get(position));

        final int p = position;
        final TextView category = holder.categoriesTextView;
        final TextView attributes = holder.attributesTextView;
        CardView cardView = holder.infoCardView;
        final ImageView showMore = holder.showMoreImage;

        //Set Category color to light blue
        category.setText(mCategories.get(position));
        category.setTextColor(Color.parseColor("#42A5F5"));
        attributes.setText("");
        String key ="";
        String value="";
        if(mCategories.get(position).equals("General")){
            attributes.append(Html.fromHtml("<b>Vin</b>: " + mVin));
            attributes.append("\n" + "\n");
            attributes.append(Html.fromHtml("<b>List price</b>: " + mMsrp));
            attributes.append("\n" + "\n");
        }

        for(int i = 0; i < info.size(); i++){
            key = info.get(i).getKey();
            value=info.get(i).getValue();
            if(!value.equals("") && !attributes.getText().toString().contains(key)) {
                attributes.append(Html.fromHtml("<b>" + key + "</b>" + ": " + value));
                attributes.append("\n" + "\n");
            }
        }




        if(isViewExpaned.get(mCategories.get(position))) {
            attributes.setVisibility(View.VISIBLE);
            showMore.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }else {
            attributes.setVisibility(View.GONE);
            showMore.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isViewExpaned.get(mCategories.get(p))){
                    attributes.setVisibility(View.VISIBLE);
                    showMore.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    setViewOpen(true,mCategories.get(p));

                }else{
                    attributes.setVisibility(View.GONE);
                    showMore.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    setViewOpen(false,mCategories.get(p));

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
        return mCategories.size();
    }
}
