package com.transport.organelles.transport_.classforms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.transport.organelles.transport_.R;
import com.transport.organelles.transport_.model.linesegment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Organelles on 1/18/2018.
 */

public class linesegmentAdapter extends BaseAdapter {

    private Context context;
    List<linesegment> linesegments;
    LayoutInflater inflater;

    public linesegmentAdapter(Context context,int resource, List<linesegment> linesegments ) {
        this.context = context;
        this.linesegments = linesegments;
    }

    private static class ViewHolder {
        TextView linesegment_name;


    }

    @Override
    public int getCount() {
        return linesegments.size();
    }

    @Override
    public Object getItem(int position) {
        return linesegments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.linesegment_details, null);

            holder = new ViewHolder();
            holder.linesegment_name = (TextView)convertView.findViewById(R.id.linesegment_name);
            convertView.setTag(holder);
            }else{
            holder = (ViewHolder)convertView.getTag();
        }

        linesegment ls = linesegments.get(position);
        holder.linesegment_name.setText(ls.getLinesegment_name());
        setUpItem(holder);
        return convertView;
    }

    private void setUpItem( ViewHolder holder){
        holder.linesegment_name.setText(holder.linesegment_name.getText().toString());
    }

}
