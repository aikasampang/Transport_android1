package com.transport.organelles.transport_.Class;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.transport.organelles.transport_.R;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by Organelles on 9/22/2017.
 */

public class WithholdingAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Withholding> withholdings;
    public WithholdingAdapter(Context context, int textResourceId, ArrayList objects) {
        super(context, textResourceId, objects);
        this.context = context;
        withholdings = objects;
    }
    private class ViewHolder{
        TextView withholding_typename;
        TextView withholding_amount;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null ){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.withholding_details, null);
            viewHolder =  new ViewHolder();
            viewHolder.withholding_typename = (TextView)convertView.findViewById(R.id.withholding_name);
            viewHolder.withholding_amount = (TextView)convertView.findViewById(R.id.withholding_amount);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Withholding withholding = withholdings.get(position);
        viewHolder.withholding_typename.setText(withholding.withholding_typename);
        viewHolder.withholding_amount.setText(withholding.withholding_amount);
        return convertView;
    }
}
