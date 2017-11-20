package com.transport.organelles.transport_.forms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.transport.organelles.transport_.classforms.Costtype;
import com.transport.organelles.transport_.classforms.DBAccess;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.ExpenseAdapter;
import com.transport.organelles.transport_.classforms.GlobalVariable;
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

    String ed_fuelin, ed_fuelout, ed_toll, ed_washing, ed_oil, ed_police, ed_tire, ed_parking, ed_wreck, ed_spare, ed_materials, ed_injury, ed_other, ed_dricom, ed_condcom,
            ed_organelles_, ed_dribonusI, ed_condbonusI, ed_dribonusII, ed_condbonusII, ed_liters, ed_pier, ed_diesel, ed_refund, ed_pparking, ed_special;
    private DBAccess dba;
    private LinearLayout lrefund, lspecialtrip;
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

        //listView = (ListView)findViewById(R.id.list_expense);
        lrefund = (LinearLayout)findViewById(R.id.lRefund);
         lspecialtrip = (LinearLayout)findViewById(R.id.lspecialtrip);
        button_expense = (Button) findViewById(R.id.bExpense);

        fuelIn = (EditText)findViewById(R.id.expense_fuelin);
        fuelOut = (EditText)findViewById(R.id.expense_fuelout);
        washing = (EditText)findViewById(R.id.expense_washing);
        toll = (EditText)findViewById(R.id.expense_toll);
        oil = (EditText)findViewById(R.id.expense_oil);
        police = (EditText)findViewById(R.id.expense_police);
        tire = (EditText)findViewById(R.id.expense_tire);
        parking = (EditText)findViewById(R.id.expense_parking);
        wreck = (EditText)findViewById(R.id.expense_wreck);
        spare = (EditText)findViewById(R.id.expense_spare);
        materials = (EditText)findViewById(R.id.expense_materials);
        injury = (EditText)findViewById(R.id.expense_injury);
        other = (EditText)findViewById(R.id.expense_other);
        dricom = (EditText)findViewById(R.id.expense_dricom);
        condcom = (EditText)findViewById(R.id.expense_condcom);
        organelles_ = (EditText)findViewById(R.id.expense_organelles);
        dribonusI = (EditText)findViewById(R.id.expense_dribonusI);
        dribonusII = (EditText)findViewById(R.id.expense_dribonusII);
        condbonusII = (EditText)findViewById(R.id.expense_condbonusII);
        liters = (EditText)findViewById(R.id.expense_liters);
        pier = (EditText)findViewById(R.id.expense_pier);
        diesel = (EditText)findViewById(R.id.expense_diesel);
        refund = (EditText)findViewById(R.id.expense_refund);
        pparking = (EditText)findViewById(R.id.expense_pparking);
        special = (EditText)findViewById(R.id.expense_special);


    }

    private void objectListeners() {
        DBQuery dbQuery = new DBQuery(frmExpense.this);
        String trip = GlobalVariable.getLasttrip();

        Double d = Double.parseDouble(dbQuery.dricomm(trip)) ;
        Double c = Double.parseDouble(dbQuery.condcomm(trip));
        String dri = String.format("%.2f", d);
        String cond = String.format("%.2f", c);
        dricom.setText(dri);
        condcom.setText(cond);



        costtypes = dbQuery.expenseList(trip);
        final ExpenseAdapter adapter = new ExpenseAdapter(frmExpense.this, R.layout.expense_details, costtypes);
        //ListView lv= (ListView)findViewById(R.id.list_expense);
        //lv.setAdapter(adapter);


        button_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String ed_fuelin, ed_fuelout, ed_toll, ed_oil, ed_police, ed_tire, ed_parking, ed_wreck, ed_materials, ed_injury, ed_dricom, ed_condcom,
//                        ed_organelles_, ed_dribonusI, ed_condbonusI, ed_dribonusII, ed_condbonusII, ed_liters, ed_pier, ed_diesel, ed_refund, ed_pparking, ed_special;
                if(fuelIn.length() == 0 && fuelOut.length() == 0 ){
                    Log.wtf("empty", "in and out");
                }




                AlertDialog.Builder builder = new AlertDialog.Builder(frmExpense.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want submit this expense?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertMethod();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


            builder.show();
            }
        });

        lspecialtrip.setVisibility(View.GONE);
        lrefund.setVisibility(View.GONE);
    }

    public void insertMethod(){
        DBQuery dbQuery = new DBQuery(frmExpense.this);
        String trip = GlobalVariable.getLasttrip();
        String devicename = GlobalVariable.getPhoneName();
        dba = DBAccess.getInstance(frmExpense.this);

        ed_fuelin = fuelIn.getText().toString();
        ed_fuelout = fuelOut.getText().toString();
//        ed_toll = Double.parseDouble(toll.getText().toString());
//        ed_washing = Double.parseDouble(washing.getText().toString()) ;
//        ed_oil = Double.parseDouble(oil.getText().toString());
//        ed_police = Double.parseDouble(police.getText().toString());
//        ed_tire = Double.parseDouble(tire.getText().toString());
//        ed_parking = Double.parseDouble(parking.getText().toString());
//        ed_wreck = Double.parseDouble(wreck.getText().toString());
//        ed_spare = Double.parseDouble(spare.getText().toString());
//        ed_materials = Double.parseDouble(materials.getText().toString());
//        ed_injury = Double.parseDouble(injury.getText().toString());
//        ed_other = Double.parseDouble(other.getText().toString());
//        ed_dricom = Double.parseDouble(dricom.getText().toString());
//        ed_condcom = Double.parseDouble(condcom.getText().toString());
//        ed_organelles_ = Double.parseDouble(organelles_.getText().toString());
//        ed_dribonusI = Double.parseDouble(dribonusI.getText().toString());
//        ed_condbonusI = Double.parseDouble(condbonusI.getText().toString());
//        ed_dribonusII = Double.parseDouble(dribonusII.getText().toString());
//        ed_condbonusII= Double.parseDouble(condbonusII.getText().toString());
//        ed_liters = Double.parseDouble(liters.getText().toString());
//        ed_pier = Double.parseDouble(pier.getText().toString());
//        ed_diesel = Double.parseDouble(diesel.getText().toString());
//        ed_refund = Double.parseDouble(refund.getText().toString());
//        ed_pparking = Double.parseDouble(pparking.getText().toString());
//        ed_special = Double.parseDouble(special.getText().toString());


        String sql = "INSERT INTO TRIPCOST (TRIPID, DEVICENAME, COSTTYPEID, AMOUNT, TAG, REMARKS)" +
                "VALUES " +
                "('"+trip+"', '"+devicename+"', '1', '"+ ed_fuelin+"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '2', '"+ ed_fuelout +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '3', '"+ ed_washing +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '4', '"+ ed_toll +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '5', '"+ ed_spare +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '6', '"+ ed_police +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '7', '"+ ed_tire +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '8', '"+ ed_parking +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '9', '"+ ed_wreck +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '10', '"+ ed_spare +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '11', '"+ ed_materials +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '12', '"+ ed_injury+"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '13', '"+ ed_other +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '14', '"+ ed_dricom +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '15', '"+ ed_condcom +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '16', '"+ ed_organelles_ +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '17', '"+ ed_dribonusI +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '18', '"+ ed_condbonusI +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '19', '"+ ed_dribonusII +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '20', '"+ ed_condbonusII +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '21', '"+ ed_liters +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '22', '"+ ed_pier +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '23', '"+ ed_diesel +"', 'NULL', '')," +
                "('"+trip+"', '"+devicename+"', '25', '"+ ed_pparking +"', 'NULL', '');";
        Log.wtf("logged sql", sql);
        if (!dba.executeQuery(sql)) {
            Toast.makeText(frmExpense.this, "Can't insert data to database!. TRIPCOST", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("Insert", "insert into tripcost");

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}