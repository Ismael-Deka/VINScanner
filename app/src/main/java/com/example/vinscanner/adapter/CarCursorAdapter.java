package com.example.vinscanner.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.vinscanner.R;
import com.example.vinscanner.db.CarContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ismael on 3/21/2017.
 */

public class CarCursorAdapter extends CursorAdapter {

    private boolean mDeleteState = false;
    private ArrayList<ImageButton> mDeleteButtons;
    private Context mContext;



    public CarCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        mContext = context;
        mDeleteButtons = new ArrayList<>();


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
    public void bindView(View view, final Context context, Cursor cursor) {
        if(!cursor.isClosed()) {
            final View listItem = view;
            CircleImageView logoImageView = (CircleImageView) view.findViewById(R.id.list_logo);
            TextView nameTextView = (TextView) view.findViewById(R.id.car_name);
            TextView vinTextView = (TextView) view.findViewById(R.id.car_vin);
            final ImageButton deleteButton = (ImageButton) view.findViewById(R.id.car_list_delete_button);
            mDeleteButtons.add(deleteButton);
            if(mDeleteState){
                deleteButton.setVisibility(View.VISIBLE);
            }

            int yearIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_YEAR);
            int makeIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MAKE);
            int modelIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MODEL);
            int vinIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_VIN);
            final int idIndex = cursor.getColumnIndex(CarContract.CarEntry._ID);

            String year = cursor.getString(yearIndex);
            final String make = cursor.getString(makeIndex);
            String model = cursor.getString(modelIndex);
            final String vin = cursor.getString(vinIndex);
            final Long id = cursor.getLong(idIndex);

            Bitmap bmp = getCarLogo(make, context);
            if (bmp != null) {
                logoImageView.setImageBitmap(bmp);
            }


            String newMake = make.substring(0, 1) + make.substring(1).toLowerCase();
            final String carName = year + " " + newMake + " " + model;
            nameTextView.setText(carName);
            vinTextView.setText(vin);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDeleteState) {
                        deleteVehicle(id, carName,listItem);
                    }
                }
            });



        }

    }

   private void deleteVehicle(final long id, String vehicleName, final View listItem){
       DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               switch (which) {
                   case DialogInterface.BUTTON_POSITIVE:
                       final Uri uri = ContentUris.withAppendedId(CarContract.CarEntry.CONTENT_URI, id);
                       listItem.startAnimation(outToRightAnimation());
                       listItem.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                           @Override
                           public void onAnimationStart(Animation animation) {

                           }

                           @Override
                           public void onAnimationEnd(Animation animation) {
                               listItem.setVisibility(View.GONE);
                               mContext.getContentResolver().delete(uri, null, null);
                           }

                           @Override
                           public void onAnimationRepeat(Animation animation) {

                           }
                       });

                       break;

                   case DialogInterface.BUTTON_NEGATIVE:
                       //No button clicked
                       break;
               }
           }
       };
       AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
           builder.setMessage("Are you sure you want to delete "+vehicleName+"?" ).setPositiveButton("Yes", dialogClickListener)
                   .setNegativeButton("No", dialogClickListener).show();

   }

    public void setDeleteState(boolean mDeleteState) {
        this.mDeleteState = mDeleteState;
        if(mDeleteState){
            setAllButtonsVisible();
        }else{
            setAllButtonsGone();
        }

    }

    private void setAllButtonsVisible(){
        for (int i = 0; i < mDeleteButtons.size(); i++) {
            mDeleteButtons.get(i).setAnimation(inFromLeftAnimation());
            mDeleteButtons.get(i).getAnimation().start();
            mDeleteButtons.get(i).setVisibility(View.VISIBLE);

        }
    }
    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private void setAllButtonsGone(){
        for (int i = 0; i < mDeleteButtons.size(); i++) {

            mDeleteButtons.get(i).setAnimation(outToLeftAnimation());
            mDeleteButtons.get(i).getAnimation().start();
            mDeleteButtons.get(i).setVisibility(View.GONE);


        }

    }



    private Bitmap getCarLogo(String make, Context c){
        try {
            ContextWrapper cw = new ContextWrapper(c);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File(directory,make+".jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }


    }

}
