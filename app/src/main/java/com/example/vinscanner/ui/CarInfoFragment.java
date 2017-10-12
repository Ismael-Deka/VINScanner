package com.example.vinscanner.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vinscanner.R;
import com.example.vinscanner.adapter.CarInfoAdapter;
import com.example.vinscanner.car.Car;


public class CarInfoFragment extends Fragment {
    private Car mCar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_car_info, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.info_list);
        CarInfoAdapter infoAdapter = new CarInfoAdapter(mCar.getVin(),mCar.getMarketPrice(),mCar.getAttributes(),getContext());

        recyclerView.setAdapter(infoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void setCar(Car newCar) {
        mCar = newCar;
    }

    public static CarInfoFragment createFragment(Car newCar){
        CarInfoFragment carInfoFragment = new CarInfoFragment();
        carInfoFragment.setCar(newCar);
        return carInfoFragment;
    }
}
