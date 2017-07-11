package com.example.vinscanner.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vinscanner.CarRecallAdapter;
import com.example.vinscanner.R;
import com.example.vinscanner.car.Car;

/**
 * Created by Ismael on 4/20/2017.
 */

public class CarRecallFragment extends Fragment {
    private Car mCar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_car_recall, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recall_list);

        CarRecallAdapter recallAdapter = new CarRecallAdapter(mCar.getRecallInfo(),getContext());

        recyclerView.setAdapter(recallAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        /*TextView description = (TextView) rootView.findViewById(R.id.description);

        ArrayList<RecallAttribute> recallInfo = mCar.getRecallInfo();
        String key;
        String value;

        for(int i = 0; i < recallInfo.size(); i++){
            key = recallInfo.get(i).getComponent();
            value = recallInfo.get(i).getSummary();
            description.setText(description.getText()+key+": "+value+"\n"+"\n");
        }*/




        return rootView;
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
