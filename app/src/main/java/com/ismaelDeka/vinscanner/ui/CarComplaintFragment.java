package com.ismaelDeka.vinscanner.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.adapter.CarComplaintAdapter;
import com.ismaelDeka.vinscanner.car.Car;

/**
 * Created by Ismael on 11/11/2017.
 */

public class CarComplaintFragment extends Fragment {
    private Car mCar;
    RecyclerView mComplaintList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_complaint, container, false);
        mComplaintList = (RecyclerView) rootView.findViewById(R.id.complaint_list);

        mComplaintList.setAdapter(new CarComplaintAdapter(mCar.getComplaints(),getContext()));
        mComplaintList.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    private void setCar(Car newCar) {
        mCar = newCar;
    }

    public static CarComplaintFragment createFragment(Car newCar){
        CarComplaintFragment carFragment = new CarComplaintFragment();
        carFragment.setCar(newCar);
        return carFragment;
    }
}
