package com.transport.organelles.transport_.Forms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.transport.organelles.transport_.Class.Costtype;
import com.transport.organelles.transport_.Class.DBQuery;
import com.transport.organelles.transport_.Class.ExpenseAdapter;
import com.transport.organelles.transport_.Class.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.util.ArrayList;

/**
 * Created by Organelles on 10/2/2017.
 */

public class frmExpense extends AppCompatActivity {


    ListView listView;
    Button button_expense;
    ArrayList<Costtype> costtypes = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmexpense);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setObjects();
        objectListeners();
    }

    private void setObjects(){

        listView = (ListView)findViewById(R.id.list_expense);
        button_expense = (Button)findViewById(R.id.bExpense);

    }

    private void objectListeners(){
        DBQuery dbQuery =  new DBQuery(frmExpense.this);

        String trip = GlobalVariable.getLasttrip();
        costtypes = dbQuery.expenseList(trip);
        final ExpenseAdapter adapter = new ExpenseAdapter(frmExpense.this, R.layout.expense_details,costtypes);
        ListView lv= (ListView)findViewById(R.id.list_expense);
        lv.setAdapter(adapter);




        button_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int items = adapter.getCount();


                Log.wtf("count", items +"");
                final ArrayList<String> amount = new ArrayList<>();
                for(int i=0; i<items;i++){

                }





//                Intent intent = new Intent (frmExpense.this, frmIngress.class);
//                startActivity(intent);
//                finish();

            }
        });



    }






}
