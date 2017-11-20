package com.transport.organelles.transport_.classforms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.transport.organelles.transport_.R;

import java.util.ArrayList;

/**
 * Created by Organelles on 9/20/2017.
 */

public class ExpenseAdapter extends ArrayAdapter<Costtype> {

    private Context context;
    ArrayList<Costtype> costtype;
    LayoutInflater inflater;
    //ArrayList<HashMap<String,String>> type;

    public ExpenseAdapter(Context context, int resource, ArrayList<Costtype> costtype) {
        super(context, resource);

        this.context = context;
        this.costtype = costtype;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        TextView costtypeid;
        TextView costtypename;
        EditText amount;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.expense_details, null);
            holder = new ViewHolder();
            holder.costtypename = (TextView) convertView.findViewById(R.id.costtypeName);
            holder.amount = (EditText)convertView.findViewById(R.id.costtype_amount);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        Costtype type = costtype.get(position);
        holder.costtypename.setText(type.getCosttype());
        //holder.amount.setText(type.getAmount());
        holder.amount.setId(position);
        holder.costtypename.setId(position);


        holder.amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    final int position = v.getId();
                    final EditText inputAmount = (EditText) v;
                    costtype.get(position).setAmount(inputAmount.getText().toString());

                }
            }
        });

        setupItem(holder);
        return convertView;
    }
    private void setupItem(ViewHolder holder) {
        holder.costtypename.setText(holder.costtypename.getText().toString());
        holder.amount.setText(String.valueOf(holder.amount.getText().toString()));
    }
    @Override
    public int getCount() {
        return costtype.size();
    }
}


    /*private class ViewHolder {
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
    public Costtype getItem(int position) {
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
*/