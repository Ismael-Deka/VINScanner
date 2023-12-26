package com.ismaelDeka.vinscanner.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.adapter.CarRecallAdapter;
import com.ismaelDeka.vinscanner.car.Car;
import com.ismaelDeka.vinscanner.car.RecallAttribute;

import java.util.ArrayList;

/**
 * Created by Ismael on 4/20/2017.
 */

public class CarRecallFragment extends Fragment {
    private Car mCar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_car_recall, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recall_list);
        TextView emptyState = rootView.findViewById(R.id.empty_state);


        if(savedInstanceState == null) {
            recyclerView.setAdapter(new CarRecallAdapter(mCar.getRecallInfo(), getContext()));
            if(mCar.getRecallInfo().size()>0){
                emptyState.setVisibility(View.GONE);
            }
        }else{
            recyclerView.setAdapter(readSavedInstance(savedInstanceState));
            if(savedInstanceState.getInt("attrSize")>0){
                emptyState.setVisibility(View.GONE);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        ArrayList<RecallAttribute> attributes = mCar.getRecallInfo();
        for(int i = 0; i < attributes.size(); i++){

            outState.putString("campaign"+i, attributes.get(i).getCampaignNumber());
            outState.putString("component"+i,attributes.get(i).getComponent());
            outState.putString("consequence"+i,attributes.get(i).getConsequence());
            outState.putString("date"+i,attributes.get(i).getDate());
            outState.putString("summary"+i,attributes.get(i).getSummary());
            outState.putString("remedy"+i,attributes.get(i).getRemedy());
        }
        outState.putInt("attrSize", attributes.size());
    }
    private CarRecallAdapter readSavedInstance(Bundle b){
        int size = b.getInt("attrSize");
        ArrayList<RecallAttribute> attributes = new ArrayList<>();
        for(int i = 0; i < size; i++){
            attributes.add(new RecallAttribute(b.getString("campaign"+i),b.getString("component"+i),b.getString("summary"+i),
                                            b.getString("consequence"+i),b.getString("remedy"+i),b.getString("date+i")));
        }

        return new CarRecallAdapter(attributes, getContext());

    }

    private void setCar(Car newCar) {
        mCar = newCar;
    }

    public static CarRecallFragment createFragment(Car newCar){
        CarRecallFragment carRecallFragment = new CarRecallFragment();
        carRecallFragment.setCar(newCar);
        return carRecallFragment;
    }
}
