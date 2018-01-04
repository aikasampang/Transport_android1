package com.transport.organelles.transport_.forms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.transport.organelles.transport_.R;

/**
 * Created by Organelles on 6/13/2017.
 */

public class mainGrid extends BaseAdapter {



    private Context mContext;
    private final String[] web;
    private final int[] Imageid;

    public mainGrid(Context c,String[] web,int[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    @Override
    public int getCount() {
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)  {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.main_menu_grid, null);
            TextView textView = (TextView) grid.findViewById(R.id.myImageViewText);
            ImageView imageView = (ImageView)grid.findViewById(R.id.myImageView);
            textView.setText(web[position]);
            imageView.setImageResource(Imageid[position]);
        } else {
            grid = (View) convertView;
        }

        return grid;

    }
}
