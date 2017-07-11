package com.example.vinscanner;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vinscanner.db.CarContract;

import java.util.ArrayList;

/**
 * Created by Ismael on 3/21/2017.
 */

public class CarCursorAdapter extends CursorAdapter {
    private ArrayList<String> mCarNames;


    public CarCursorAdapter(Context context, Cursor c) {
        super(context, c,0);

        mCarNames = new ArrayList<>();


    }

    public CarCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.car_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {



        TextView nameTextView = (TextView) view.findViewById(R.id.car_name);
        TextView vinTextView= (TextView) view.findViewById(R.id.car_vin);

        int yearIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_YEAR);
        int makeIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MAKE);
        int modelIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MODEL);
        int vinIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_VIN);

        String year = cursor.getString(yearIndex);
        String make = cursor.getString(makeIndex);
        String model = cursor.getString(modelIndex);
        String vin = cursor.getString(vinIndex);

        String newMake = make.substring(0,1)+make.substring(1).toLowerCase();
        String carName = year+" "+newMake+" "+model;
        if(!mCarNames.contains(carName)){
            mCarNames.add(carName);
        }
        nameTextView.setText(carName);
        vinTextView.setText(vin);
    }


    public ArrayList<String> getCarNames() {
        return mCarNames;
    }
}
