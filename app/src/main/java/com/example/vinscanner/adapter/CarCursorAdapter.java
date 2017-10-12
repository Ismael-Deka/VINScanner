package com.example.vinscanner.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.vinscanner.R;
import com.example.vinscanner.db.CarContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ismael on 3/21/2017.
 */

public class CarCursorAdapter extends CursorAdapter {


    public CarCursorAdapter(Context context, Cursor c) {
        super(context, c,0);



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

        if(!cursor.isClosed()) {
            CircleImageView logoImageView = (CircleImageView) view.findViewById(R.id.list_logo);
            TextView nameTextView = (TextView) view.findViewById(R.id.car_name);
            TextView vinTextView = (TextView) view.findViewById(R.id.car_vin);
            CheckBox checkBoxView =(CheckBox) view.findViewById(R.id.car_list_checkbox);

            int yearIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_YEAR);
            int makeIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MAKE);
            int modelIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MODEL);
            int vinIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_VIN);


            String year = cursor.getString(yearIndex);
            final String make = cursor.getString(makeIndex);
            String model = cursor.getString(modelIndex);
            String vin = cursor.getString(vinIndex);

            Bitmap bmp = getCarlogo(make, context);
            if (bmp != null) {
                logoImageView.setImageBitmap(bmp);
            }


            String newMake = make.substring(0, 1) + make.substring(1).toLowerCase();
            String carName = year + " " + newMake + " " + model;
            nameTextView.setText(carName);
            vinTextView.setText(vin);

        }



    }

    private Bitmap getCarlogo(String make,Context c){
        try {
            ContextWrapper cw = new ContextWrapper(c);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File(directory,make+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }



    }


}
