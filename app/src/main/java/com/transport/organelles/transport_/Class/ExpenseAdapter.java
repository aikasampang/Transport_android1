package com.transport.organelles.transport_.Class;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.transport.organelles.transport_.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Organelles on 9/20/2017.
 */

public class ExpenseAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Costtype> costtype;
    ArrayList<HashMap<String,String>> type;

    public ExpenseAdapter(Context context,  int textViewResourceId,ArrayList objects) {
        super(context, textViewResourceId, objects);

        this.context = context;
        costtype = objects;
    }


    private class ViewHolder {
        TextView costtypeid;
        TextView costtypename;
        EditText amount;

    }


    @Override
    public int getCount() {
        return costtype.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return costtype.get(position);
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null ){

            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expense_details, null);
            viewHolder = new ViewHolder();
            viewHolder.costtypename = (TextView)convertView.findViewById(R.id.costtypeName);
            viewHolder.amount = (EditText) convertView.findViewById(R.id.costtype_amount);
            viewHolder.amount.setId(position);
            //viewHolder.amount.addTextChangedListener(new GenericTextWatcher(viewHolder.amount));
            convertView.setTag(viewHolder);



        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Costtype cost = costtype.get(position);
        viewHolder.costtypename.setText(cost.costtype);
        viewHolder.amount.setText(cost.amount);

//        viewHolder.costtype.setText(type.get(position).get("costtype"));
//        viewHolder.amount.setText(type.get(position).get("amount"));

        viewHolder.amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    final int pos = v.getId();
                    final EditText a = (EditText) v;
                    final TextView name= (TextView)v;
                    costtype.get(pos).amount = a.getText().toString();
                    costtype.get(pos).costtype = name.getText().toString();
                    Log.wtf("Adapter",a.getText().toString()+ " " + pos + " "+ name.getText().toString());
                }
            }
        });

        return convertView;
    }


//    private class GenericTextWatcher implements TextWatcher {
//
//        private View view;
//        private GenericTextWatcher(View view) {
//            this.view = view;
//        }
//
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//        public void afterTextChanged(Editable editable) {
//            final int position = view.getId();
//            final EditText editText = (EditText) view;
//            costtype.set(position, editText.getText().toString());)
//        }
//    }
}
