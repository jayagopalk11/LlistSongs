package com.tester.llistsongs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static android.R.attr.data;

/**
 * Created by Jai on 2/11/2017.
 */

public class customAdapter extends ArrayAdapter<ListItems> {

    public customAdapter(Context context, ListItems[] item) {
        super(context, R.layout.custom_layout, item);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflator = LayoutInflater.from(getContext());
        View customView = myInflator.inflate(R.layout.custom_layout,parent,false);


        ListItems value = getItem(position);

        //TextView myText1 = (TextView) customView.findViewById(R.id.myView1);
        TextView title = (TextView) customView.findViewById(R.id.titleName);
        TextView artist = (TextView) customView.findViewById(R.id.artist);
        ImageView myImage = (ImageView) customView.findViewById(R.id.imageView);
        TextView duration = (TextView) customView.findViewById(R.id.duration);

        title.setText(value.items);
        title.setSelected(true);
        artist.setText(value.desc);
        duration.setText(value.duration);

        //myText1.setMovementMethod(new ScrollingMovementMethod());

        if(value.arr != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            //Bitmap bitmap = BitmapFactory.decodeByteArray(value.arr, 0, value.arr.length, options);
            Bitmap bitmap = BitmapFactory.decodeFile(value.arr, options);

            myImage.setImageBitmap(bitmap);
        }
        else{
            myImage.setImageResource(R.drawable.andy);
        }



        return customView;
    }
}
