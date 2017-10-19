package com.transport.organelles.transport_.Forms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
    EditText fuelIn, fuelOut, washing, toll, oil, police, tire , parking, wreck, spare, materials, injury, other, dricom, condcom,
            organelles_, dribonusI, condbonusI, dribonusII, condbonusII, liters, pier, diesel, refund, pparking, special;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmexpense);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setObjects();
        objectListeners();
    }

    private void setObjects() {

        listView = (ListView)findViewById(R.id.list_expense);
        //button_expense = (Button) findViewById(R.id.bExpense);

//        fuelIn = (EditText)findViewById(R.id.expense_fuelin);
//        fuelOut = (EditText)findViewById(R.id.expense_fuelout);
//        washing = (EditText)findViewById(R.id.expense_washing);
//        toll = (EditText)findViewById(R.id.expense_toll);
//        oil = (EditText)findViewById(R.id.expense_oil);
//        police = (EditText)findViewById(R.id.expense_police);
//        tire = (EditText)findViewById(R.id.expense_tire);
//        parking = (EditText)findViewById(R.id.expense_parking);
//        wreck = (EditText)findViewById(R.id.expense_wreck);
//        spare = (EditText)findViewById(R.id.expense_spare);
//        materials = (EditText)findViewById(R.id.expense_materials);
//        injury = (EditText)findViewById(R.id.expense_injury);
//        other = (EditText)findViewById(R.id.expense_other);
//        dricom = (EditText)findViewById(R.id.expense_dricom);
//        condcom = (EditText)findViewById(R.id.expense_condcom);
//        organelles_ = (EditText)findViewById(R.id.expense_organelles);
//        dribonusI = (EditText)findViewById(R.id.expense_dribonusI);
//        dribonusII = (EditText)findViewById(R.id.expense_dribonusII);
//        condbonusII = (EditText)findViewById(R.id.expense_condbonusII);
//        liters = (EditText)findViewById(R.id.expense_liters);
//        pier = (EditText)findViewById(R.id.expense_pier);
//        diesel = (EditText)findViewById(R.id.expense_diesel);
//        refund = (EditText)findViewById(R.id.expense_refund);
//        pparking = (EditText)findViewById(R.id.expense_pparking);
//        special = (EditText)findViewById(R.id.expense_special);
//

    }

    private void objectListeners() {
        DBQuery dbQuery = new DBQuery(frmExpense.this);

        String trip = GlobalVariable.getLasttrip();
        costtypes = dbQuery.expenseList(trip);
        final ExpenseAdapter adapter = new ExpenseAdapter(frmExpense.this, R.layout.expense_details, costtypes);
        //ListView lv= (ListView)findViewById(R.id.list_expense);
        //lv.setAdapter(adapter);


        button_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }
}