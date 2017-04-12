package com.android.movieapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

 public class Custom_Adapter extends BaseAdapter{


    private static Inflater inflate;  //to get xml files


     String[] postersURLs = {};


     private Context context;

     public Custom_Adapter(Context c , String[] s )
     {
         context = c;
         if(s != null)
        postersURLs = s ;

     }



    @Override
    public int getCount() {
        return postersURLs.length;

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
     public View getView(int position, View convertView, ViewGroup parent)
     {
         ImageView imageView;
         if (convertView == null) {
             imageView = new ImageView(context);
      //     imageView.setLayoutParams(new GridView.LayoutParams(360, 420));

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
             imageView.setPadding(0, 0, 0, 0);
         } else {
             imageView = (ImageView) convertView;
         }
      // imageView.setImageResource(imageIDs[position]);

       Picasso.with(context).load(postersURLs[position]).into(imageView);

         return imageView;

     }

}
