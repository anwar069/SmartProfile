package com.anwar069.smartprofile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anwar069.smartprofile.R;

/**
 * Created by Ahmed on 24/10/2015.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    String[] spinnerValues;
    String[] spinnerSubs = { "Normal (both Sound and Vibration)", "Vibrate notification only", "No notification" };
    int total_images[] = { R.mipmap.ic_general, R.mipmap.ic_vibrate, R.mipmap.ic_silent};
    Context context;
    public SpinnerAdapter(Context ctx, String[] objects) {
        super(ctx, R.layout.spinner_layout, objects);
        context=ctx;
        spinnerValues=objects;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mySpinner = inflater.inflate(R.layout.spinner_layout, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.text_main_seen);
        main_text.setText(spinnerValues[position]);
        TextView subSpinner = (TextView) mySpinner.findViewById(R.id.sub_text_seen);
        subSpinner.setText(spinnerSubs[position]);
        ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.left_pic);
        left_icon.setImageResource(total_images[position]);
        return mySpinner;
    }


}
