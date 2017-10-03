package com.transport.organelles.transport_.Forms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.transport.organelles.transport_.Class.Costtype;
import com.transport.organelles.transport_.Class.DBAccess;
import com.transport.organelles.transport_.Class.DBAssets;
import com.transport.organelles.transport_.Class.DBQuery;
import com.transport.organelles.transport_.Class.ExpenseAdapter;
import com.transport.organelles.transport_.Class.GlobalClass;
import com.transport.organelles.transport_.Class.GlobalVariable;
import com.transport.organelles.transport_.Class.Withholding;
import com.transport.organelles.transport_.Class.WithholdingAdapter;
import com.transport.organelles.transport_.R;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;

import static android.R.id.list;

/**
 * Created by Organelles on 6/14/2017.
 */

public class frmIngress extends AppCompatActivity {
    TextView txtDateTime, totalCollection_text, totalGross_text, expenses_text, withholding_text, totalCommission_text, netsales_text, partial_text, shortover_text;
    EditText specialTrip, cancelled, finalremit;
    Button b_finalremit, b_ingress, b_tripreport, b_expense, b_withholding;
    String  tripId, sqlQuery = "", dtstartTime, vehicle, busmode,linesegment = "" , checkpoint ="", refDate = "" ;
    int gCurrentKM= 0, mpadcount, penalty, cancel,  manual, partialremit, expense, withholding, lastremit,
            tripcount, gross_tr, curdir,tripcount_tr, remitfinal;
    Double driamt, condamt, condrate, drirate, gross, totalgross, shortamt, basisgross, dribonus, condbonus;
    DBAccess dba;
    Spinner terminal;

    String[] ptype, ptype2;
    ArrayList<Costtype> costtypes = null;
    ArrayList<Withholding> withholdings = null;
    ArrayList<List> uploadWith;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmingress);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitleBar();
        setObjects();
        objectListeners();
        formLoad();

    }
    private void setTitleBar() {
        //Display login user
        TextView txtUser = (TextView) findViewById(R.id.txtUser);
        final TextView txtBattery = (TextView) findViewById(R.id.txtBattery);
        txtDateTime = (TextView) findViewById(R.id.txtDateTime);

        final String percent = "%";
        final String batteryLevel = "";

        assert txtUser != null;
        txtUser.setText("Dela Rosa");


        //Thread use for time
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                assert txtDateTime != null;
                                txtDateTime.setText(currentDateTimeString);
                                assert txtBattery != null;
                                String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmIngress.this));
                                txtBattery.setText( batteryLevel + "%          ");
                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        t.start();

    }

    private void setObjects(){

        totalCollection_text = (TextView)findViewById(R.id.ing_collections);
        totalGross_text = (TextView)findViewById(R.id.ing_totalgross);
        expenses_text = (TextView)findViewById(R.id.ing_expenses);
        withholding_text = (TextView)findViewById(R.id.ing_withholding);
        totalCommission_text = (TextView)findViewById(R.id.ing_commisions);
        netsales_text = (TextView)findViewById(R.id.ing_netsales);
        partial_text = (TextView)findViewById(R.id.ing_partial);
        shortover_text = (TextView)findViewById(R.id.ing_short);

        specialTrip = (EditText)findViewById(R.id.ing_manual);
        cancelled = (EditText)findViewById(R.id.ing_refund);
        finalremit = (EditText)findViewById(R.id.ing_finalremit);

        b_finalremit = (Button)findViewById(R.id.ing_remit);
        b_ingress = (Button)findViewById(R.id.ing_ingress);
        b_tripreport = (Button)findViewById(R.id.ing_tripreport);
        b_expense = (Button)findViewById(R.id.ing_buttonexp);
        b_withholding = (Button)findViewById(R.id.ing_buttonwith);

        terminal = (Spinner)findViewById(R.id.ing_terminal);


    }
    private void objectListeners(){

        tripId = GlobalVariable.getLasttrip();
        final DBQuery dbQuery = new DBQuery(frmIngress.this);
        gross = Double.parseDouble(dbQuery.getGross(tripId));



        b_finalremit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!finalremit.getText().toString().isEmpty()){
                    finalremit();
                }else{
                    Toast.makeText(frmIngress.this, "Enter an amount in Final Remit", Toast.LENGTH_LONG).show();

                }
            }
        });

        b_ingress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tripid = GlobalVariable.getLasttrip();
                String devicename = GlobalVariable.getPhoneName();

                ingress(tripid, devicename);
            }
        });

        b_tripreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripreport(0);
            }
        });

        expenses_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(frmIngress.this);

            }
        });

        specialTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(frmIngress.this);
                builder.setTitle("Manual");
                builder.setMessage("Enter the amount");
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.modal_ingress, null);
                final EditText m_amount = (EditText) alertLayout.findViewById(R.id.ingress_edittext);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int m = Integer.parseInt(m_amount.getText().toString());
                        manual = m;
                        updateAmounts();
                        dialog.dismiss();
                    }
                });
                builder.setView(alertLayout);
                builder.show();
            }
        });
        cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(frmIngress.this);
                builder.setTitle("Cancelled/Refund");
                builder.setMessage("Enter the amount");
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.modal_ingress, null);

                final EditText c_cancel = (EditText) alertLayout.findViewById(R.id.ingress_edittext);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int c = Integer.parseInt(c_cancel.getText().toString());
                        cancel = c;
                        updateAmounts();
                        dialog.dismiss();
                    }
                });
                builder.setView(alertLayout);
                builder.show();
            }
        });

        b_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (frmIngress.this, frmExpense.class);
                startActivity(intent);
            }
        });




//        b_expense.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DBQuery dbQuery1 = new DBQuery(frmIngress.this);
//                String trip = GlobalVariable.getLasttrip();
//                LayoutInflater inflater = getLayoutInflater();
//                View alertLayout = inflater.inflate(R.layout.frmexpense, null);
//                costtypes = dbQuery1.expenseList(trip);
//                final ExpenseAdapter adapter = new ExpenseAdapter(frmIngress.this, R.layout.expense_details,costtypes);
//                ListView lv= (ListView) alertLayout.findViewById(R.id.list_expense);
//                lv.setAdapter(adapter);
//                AlertDialog.Builder alert = new AlertDialog.Builder(frmIngress.this);
//                alert.setTitle("Expenses");
//                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                alert.setView(alertLayout);
//                alert.create().show();
//
//                }
//            });

        b_withholding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withholdings = dbQuery.withholdingList();
                LayoutInflater layoutInflater = getLayoutInflater();
                View alertLayout = layoutInflater.inflate(R.layout.frmwithholding, null);

                WithholdingAdapter adapter = new WithholdingAdapter(frmIngress.this, R.layout.withholding_details, withholdings);
                ListView view = (ListView)alertLayout.findViewById(R.id.list_withholding);
                view.setAdapter(adapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(frmIngress.this);
                builder.setTitle("Withholding");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dba = DBAccess.getInstance(frmIngress.this);
                        String trip = GlobalVariable.getLasttrip();
                        String devicename = GlobalVariable.getPhoneName();
                        String sql = "delete from TRIPUSAGE where TRIPID='"+trip+"'";

                        if (!dba.executeQuery(sql)) {
                            Toast.makeText(frmIngress.this, "Can't delete data to database!. tripusage", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.wtf("delete", "tripusage");
                            String sql2 = "delete from TRIPWITHHOLDING where TRIPID='"+trip+"'";
                            if (!dba.executeQuery(sql2)) {
                                Toast.makeText(frmIngress.this, "Can't delete data to database!. tripwithholding", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.wtf("delete", "tripwithholding");

                                withholdings = dbQuery.withholdingList();
                               // List<Object> list = new ArrayList<Object>(Arrays.asList(withholdings));
                                    for (Object list: withholdings){
                                        Object x0 = list.equals(0);
                                        Object x1 = list.equals(0);
                                        Object x2 = list.equals(0);
                                        Object x3 = list.equals(0);
                                        if(x0.equals("W")){
                                            String sql3 = "INSERT INTO TRIPWITHHOLDING VALUES('" + trip + "', '" + devicename + "', '"+ x1 +"','" + x3 + "') ";
                                            if(!dba.executeQuery(sql3)){
                                                Toast.makeText(frmIngress.this, "Can't insert data to database . TRIPWITHHOLDING", Toast.LENGTH_LONG).show();
                                            }else{
                                                Log.wtf("insert","tripusage");
                                            }
                                        }else{
                                            String sql4 = "INSERT INTO TRIPUSAGE VALUES('" + trip + "', '" + devicename + "', '"+ x1 +"','" + x3 + "') ";
                                            if(!dba.executeQuery(sql4)){
                                                Toast.makeText(frmIngress.this, "Can't insert data to database . TRIPUSAGE", Toast.LENGTH_LONG).show();
                                            }else{
                                                Log.wtf("insert","tripusage");
                                            }
                                        }
                                    }
                            }
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setView(alertLayout);
                builder.show();
            }
        });


    }

    private void formLoad(){
        DBQuery dbQuery = new DBQuery(frmIngress.this);
        String tripid = GlobalVariable.getLasttrip();

        computeGrossAddons();
        computeExpense();



        //cancel = Integer.parseInt(dbQuery.getTotalAmount(tripid));
        cancel = 0;
        //remitfinal = 0;

        totalgross = totalgross + manual - cancel;

        partialremit = dbQuery.getPartialremit(tripid);
        expense = dbQuery.getExpenses(tripid);
        withholding = dbQuery.getWithholding(tripid);
        lastremit = dbQuery.getLastRemit(tripid);


        shortamt = totalgross - partialremit - lastremit - expense + withholding;
        //finalremit.setText(String.valueOf(remitfinal));
        totalCollection_text.setText(gross+ "");
        specialTrip.setText(String.valueOf(manual));
        cancelled.setText(String.valueOf(cancel));
        totalGross_text.setText(String.valueOf(totalgross));
        expenses_text.setText(String.valueOf(expense));
        withholding_text.setText(String.valueOf(withholding));

        double com = driamt + condamt;
        totalCommission_text.setText(String.format("%.2f", com));

        double net = totalgross - expense + withholding;
        netsales_text.setText(net + "");

        partial_text.setText(partialremit+ "");

        double so = totalgross - partialremit - lastremit - expense + withholding;
        shortover_text.setText(-so + "");

        specialTrip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(specialTrip.getText().equals("0")){
                    specialTrip.setText(0 + "");
                }else{
                    dba = DBAccess.getInstance(frmIngress.this);
                    String trip = GlobalVariable.getLasttrip();
                    String d = GlobalVariable.getPhoneName();
                    String c = cancelled.getText().toString();
                    String query = "Insert into TRIPCOST values ('" + trip + "', '" + d + "', '26', '"+c+"', 'null', 'User Generated')";

                    if (!dba.executeQuery(query)) {
                        Toast.makeText(frmIngress.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        cancelled.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(specialTrip.getText().equals("0")){
                    specialTrip.setText(0 + "");
                }else{
                    dba = DBAccess.getInstance(frmIngress.this);
                    String trip = GlobalVariable.getLasttrip();
                    String d = GlobalVariable.getPhoneName();
                    String c = cancelled.getText().toString();
                    String query = "Insert into TRIPCOST values ('" + trip + "', '" + d + "', '24', '"+c+"', 'null', 'User Generated')";

                    if (!dba.executeQuery(query)) {
                        Toast.makeText(frmIngress.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void computeGrossAddons(){
        totalgross = gross;

        dba = DBAccess.getInstance(frmIngress.this);

        DBQuery dbQuery = new DBQuery(frmIngress.this);

        String sp = dbQuery.getSpecialTrip(GlobalVariable.getLasttrip());
        if(sp == null && sp.isEmpty()){
            manual = Integer.parseInt(sp);
        }else{
            manual = 0;
        }

        Double ttg =  Double.parseDouble(dbQuery.getPenalty(GlobalVariable.getLasttrip()));
        if(ttg == null){
            totalgross = totalgross + ttg;
        }else{
            totalgross = totalgross + ttg;
        }




        String segment = GlobalVariable.getIng_linesegment();
        String km = GlobalVariable.getIng_kmpost();
        String dir = GlobalVariable.getIng_direction();
        int qty = Integer.parseInt(GlobalVariable.getIng_qty());
        mpadcount = getControlCount(segment, km, dir);


        //penalty = getPenaltyAmt(lineid, km, dir, busid);

        if(mpadcount < qty ){
            penalty =  getPenaltyAmt(dir, km) * (qty - mpadcount);
        }

        totalgross = totalgross + penalty;
    }

    public void computeExpense() {

        dba = DBAccess.getInstance(frmIngress.this);

        basisgross = totalgross;

        DBQuery dbQuery = new DBQuery(frmIngress.this);
        String trip = GlobalVariable.getLasttrip();
        String device = GlobalVariable.getPhoneName();
        dbQuery.getCondDri(trip);
        String cond = GlobalVariable.getCurrent_cond();
        String dri = GlobalVariable.getCurrent_dri();
        String mode = GlobalVariable.getModeid();
        String line = GlobalVariable.getLineid();

        dbQuery.getPercentCond(cond, mode, line);
        Double amountcond = Double.parseDouble(GlobalVariable.getCondpercent_amount());
        String remarkscond = GlobalVariable.getCondpercent_remarks();

        if (remarkscond.equals("")) {
            condrate = amountcond;
        }
        dbQuery.getPercentDri(cond, mode, line);
        Double amountdri = Double.parseDouble(GlobalVariable.getCondpercent_amount());
        String remarksdri = GlobalVariable.getCondpercent_remarks();

        if (remarksdri.equals("")) {
            drirate = amountdri;
        }

        Double commission = Double.parseDouble(dbQuery.getlessGrossCommission(trip));
        if ( commission == 0.00) {
            basisgross = basisgross - commission;
        } else{
            basisgross = basisgross - commission;
        }

        driamt = Double.parseDouble(basisgross * drirate + "");
        condamt = Double.parseDouble(basisgross * condrate + "");


        sqlQuery = "delete from TRIPCOST where COSTTYPEID in (14,15) and TRIPID='" + trip + "'";

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmIngress.this, "Can't insert data to database! delete from tripcost", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("success", "delete tripcost");
            String sqlQuery2 = "insert into TRIPCOST values ( '"+trip+"','"+device+"', '14', '"+ driamt +"', 'NULL', 'System Generated' ) ";
            if (!dba.executeQuery(sqlQuery2)) {
                Toast.makeText(frmIngress.this, "Can't insert data to database! insert tripcost dri", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("success", "tripcostdri");
                String sqlQuery3 = "insert into TRIPCOST values ( '"+trip+"','"+device+"', '15', '"+ condamt +"', 'NULL', 'System Generated' ) ";
                if (!dba.executeQuery(sqlQuery3)) {
                    Toast.makeText(frmIngress.this, "Can't insert data to database! insert tripcost dri", Toast.LENGTH_SHORT).show();
                } else {
                    Log.wtf("success", "tripcostcond");
                }
            }
        }

        basisgross = totalgross;
        String linename = GlobalVariable.getLine_name();
        String tag = dbQuery.getLineDB(linename);
        String modeid = GlobalVariable.getModeid();

        dbQuery.getEmployeeID(dri, cond);

        String driID = GlobalVariable.getDri_employeeID();
        String condID = GlobalVariable.getCond_employeeID();
        String driTag = GlobalVariable.getDri_tag();
        String condTag = GlobalVariable.getCond_tag();


        if(tag.toString().equals("1")){ //city
            if(modeid.toString().equals("1")) { //aircon

                switch (driID){
                    case "1" : dribonus = ((basisgross - 14000) / 1000) * 25 + 75;
                        if(dribonus  < 14000){
                            dribonus  = 0.00;
                        }
                        break;
                    case "4" : dribonus  = ((basisgross - 12000) / 1000) * 25 + 50;
                        if(dribonus  < 12000){
                            dribonus  = 0.00;
                        }
                        break;
                    default: dribonus = 0.00;
                }


                if(driTag.equals("200") && dribonus > 200){
                    dribonus = 200.00;
                }else if(driTag.equals("1000")) {
                    dribonus = 0.00;
                }

                switch (condID){
                    case "1" :  condbonus = ((basisgross - 14000) / 1000) * 25 + 75;
                        if(condbonus  < 14000){
                            condbonus  = 0.00;
                        }
                        break;
                    case "4" : condbonus  = ((basisgross - 12000) / 1000) * 25 + 50;
                        if(condbonus  < 12000){
                            condbonus  = 0.00;
                        }
                        break;
                    default: condbonus = 0.00;
                }
                if(condTag.equals("200") && dribonus > 200){
                    dribonus = 20.00;
                }else if(driTag.equals("1000")) {
                    dribonus = 0.00;
                }
            }else if (modeid.toString().equals("2")){

                switch (driID){
                    case "1" : dribonus = ((basisgross - 12000) / 1000) * 25 + 75;
                        if(dribonus  < 12000){
                            dribonus  = 00.00;
                        }
                        break;
                    case "4" : dribonus  = ((basisgross - 11000) / 1000) * 25 + 50;
                        if(dribonus  < 11000){
                            dribonus  = 00.00;
                        }
                        break;
                    default: dribonus = 00.00;
                }


                if(driTag.equals("200") && dribonus > 200){
                    dribonus = 200.00;
                }else if(driTag.equals("1000")) {
                    dribonus = 00.00;
                }

                switch (condID){
                    case "1" :  condbonus = ((basisgross - 12000) / 1000) * 25 + 75;
                        if(condbonus  < 12000){
                            condbonus  = 00.00;
                        }
                        break;
                    case "4" : condbonus  = ((basisgross - 11000) / 1000) * 25 + 50;
                        if(condbonus  < 11000){
                            condbonus  = 00.00;
                        }
                        break;
                    default: condbonus = 00.00;
                }
                if(condTag.equals("200") && dribonus > 200){
                    dribonus = 200.00;
                }else if(driTag.equals("1000")) {
                    dribonus = 00.00;
                }

            }

        }else{ //provincial

            if(modeid.toString().equals("1")) {
                switch (driID) {
                    case "1":
                        dribonus = ((basisgross - 12000) / 1000) * 25 + 75;
                        if (dribonus < 12000) {
                            dribonus = 00.00;
                        }
                        break;
                    case "3":
                        dribonus = ((basisgross - 12000) / 1000) * 25 + 25;
                        if (dribonus < 12000) {
                            dribonus = 00.00;
                        }
                        break;

                    case "4":
                        dribonus = ((basisgross - 12000) / 1000) * 25 + 25;
                        if (dribonus < 12000) {
                            dribonus = 00.00;
                        }
                        break;
                    default:
                        dribonus = 00.00;
                }


                if (driTag.equals("200") && dribonus > 200) {
                    dribonus = 200.00;
                } else if (driTag.equals("1000")) {
                    dribonus = 00.00;
                }

                switch (condID) {
                    case "1":
                        condbonus = ((basisgross - 12000) / 1000) * 25 + 75;
                        if (condbonus < 12000) {
                            condbonus = 00.00;
                        }
                        break;
                    case "3":
                        condbonus = ((basisgross - 12000) / 1000) * 25 + 25;
                        if (condbonus < 12000) {
                            condbonus = 00.00;
                        }
                        break;

                    case "4":
                        condbonus = ((basisgross - 12000) / 1000) * 25 + 25;
                        if (condbonus < 12000) {
                            condbonus = 00.00;
                        }
                        break;
                    default:
                        condbonus = 00.00;
                }
                if (condTag.equals("200") && dribonus > 200) {
                    dribonus = 200.00;
                } else if (driTag.equals("1000")) {
                    dribonus = 00.00;
                }

            }else if(modeid.toString().equals("2")){
                switch (driID) {
                    case "1":
                        dribonus = ((basisgross - 10000) / 1000) * 25 + 75;
                        if (dribonus < 10000) {
                            dribonus = 00.00;
                        }
                        break;
                    case "3":
                        dribonus = ((basisgross - 10000) / 1000) * 25 + 25;
                        if (dribonus < 10000) {
                            dribonus = 00.00;
                        }
                        break;

                    case "4":
                        dribonus = ((basisgross - 10000) / 1000) * 25 + 25;
                        if (dribonus < 10000) {
                            dribonus = 00.00;
                        }
                        break;
                    default:
                        dribonus = 00.00;
                }


                if (driTag.equals("200") && dribonus > 200) {
                    dribonus = 200.00;
                } else if (driTag.equals("1000")) {
                    dribonus = 00.00;
                }

                switch (condID) {
                    case "1":
                        condbonus = ((basisgross - 10000) / 1000) * 25 + 75;
                        if (condbonus < 10000) {
                            condbonus = 00.00;
                        }
                        break;
                    case "3":
                        condbonus = ((basisgross - 10000) / 1000) * 25 + 25;
                        if (condbonus < 10000) {
                            condbonus = 00.00;
                        }
                        break;

                    case "4":
                        condbonus = ((basisgross - 10000) / 1000) * 25 + 25;
                        if (condbonus < 10000) {
                            condbonus = 00.00;
                        }
                        break;
                    default:
                        condbonus = 00.00;
                }
                if (condTag.equals("200") && dribonus > 200) {
                    dribonus = 200.00;
                } else if (driTag.equals("1000")) {
                    dribonus = 00.00;
                }
            }

        }


        sqlQuery = "delete from TRIPCOST where COSTTYPEID in (17,18) and TRIPID='" + trip + "'";

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmIngress.this, "Can't insert data to database! delete from tripcost", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("success", "delete tripcost");
            String sqlQuery2 = "insert into TRIPCOST values ( '"+trip+"','"+device+"', '17', '"+ dribonus +"', 'NULL', 'System Generated' ) ";
            if (!dba.executeQuery(sqlQuery2)) {
                Toast.makeText(frmIngress.this, "Can't insert data to database! insert tripcost dri", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("success", "tripcostdri");
                String sqlQuery3 = "insert into TRIPCOST values ( '"+trip+"','"+device+"', '18', '"+ condbonus +"', 'NULL', 'System Generated' ) ";
                if (!dba.executeQuery(sqlQuery3)) {
                    Toast.makeText(frmIngress.this, "Can't insert data to database! insert tripcost dri", Toast.LENGTH_SHORT).show();
                } else {
                    Log.wtf("success", "tripcostcond");
                }
            }
        }

        String id = dbQuery.getCashBond();
        String query = "delete from TRIPWITHHOLDING where ATTRIBUTEID='"+ id +"' and TRIPID='"+ trip +"'";

        if (!dba.executeQuery(query)) {
            Toast.makeText(frmIngress.this, "Can't insert data to database! delete from tripcost", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("delete", "tripholding");


            int amount = getAmountFromBracket(gross, GlobalVariable.getCashbond_remarks());
            String query1 = "insert into TRIPWITHHOLDING values ('" + trip + "','" + device + "', '" + id + "', '" + amount + "')";
            if (!dba.executeQuery(query1)) {
                Toast.makeText(frmIngress.this, "Can't insert data to database! delete from tripcost", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("insert", "tripholding");

            }
        }



    }

    private int getAmountFromBracket(Double gross, String bracket ){

        String[] rangeall = bracket.split(";");
        Log.wtf("range", rangeall + "");

        String one = rangeall[0];
        String two = rangeall[1];
        String three = rangeall[2];

        String[] range1 = one.split(":");
        String[] range2 = two.split(":");
        String[] range3 = three.split(":");


        //25
        String g1 = range1[0]; //range
        String g2 = range1[1]; //amount

        //50
        String g11 = range2[0];
        String g22 = range2[1];

        //75
        String g111 = range2[0];
        String g222 = range2[1];

        String[] limits1 = g1.split("-");
        String[] limits2 = g11.split("-");
        String[] limits3 = g111.split("-");

        Double limit1min = Double.parseDouble(limits1[0]);
        Double limit1max = Double.parseDouble(limits1[1]);

        Double limit2min = Double.parseDouble(limits2[0]);
        Double limit2max = Double.parseDouble(limits2[1]);

        Double limit3min = Double.parseDouble(limits3[0]);
        Double limit3max = Double.parseDouble(limits3[1]);

        Double g = gross;

        if(isBetween(g, limit1min, limit1max)){
            return 25;
        }else if(isBetween(g, limit2min, limit2max)){
            return 50;
        }else if(isBetween(g, limit3min, limit3max)){
            return 75;
        }else{
            return 0;
        }

    }

    public static boolean isBetween(Double x, Double lower, Double upper) {
        return lower <= x && x <= upper;
    }

    private int getControlCount(String segment, String km, String dir){
        DBQuery dbQuery = new DBQuery(frmIngress.this);
        String id = GlobalVariable.getLasttrip();
        String ccount = dbQuery.getControlCount(segment, km, dir, id );
        int c = Integer.valueOf(ccount);
        return c;
    }

    private int getPenaltyAmt( String dir, String km){
        String busid = GlobalVariable.getModeid();
        String lineid = GlobalVariable.getLineid();
        DBQuery dbQuery = new DBQuery(frmIngress.this);
        String amount = dbQuery.getPenaltyAmt(lineid, km, dir, busid);
        int a = Integer.valueOf(amount);
        return a;
    }

    private void finalremit(){

        int lastremit = Integer.parseInt(finalremit.getText().toString());

        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        DBQuery dbQuery = new DBQuery(frmIngress.this);


        String tripid = GlobalVariable.getLasttrip();
        String devicename = GlobalVariable.getPhoneName();
        String cashier = GlobalVariable.getCashierID();
        String date = dtstartTime;
        String term = terminal.getSelectedItem().toString();
        int segmentNum = dbQuery.getSegmentNum(tripid);
        String remit = finalremit.getText().toString();

        String query = "INSERT into TRIPRECEIPT values ('"+ tripid +"', '"+ devicename+"', '2', '"+ cashier +"', '"+ date +"','"+ remit +"', '"+ term +"', '"+ segmentNum +"' )";

        if (!dba.executeQuery(query)) {
            Toast.makeText(frmIngress.this, "Can't insert data to database! insert tripreceipt", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("success", "printingress finalremit");
        }

        finalremit.setEnabled(false);
        shortamt = totalgross - expense + withholding - partialremit - lastremit;
        shortover_text.setText(-shortamt+"");


        }

    private void ingress(String tripid, String devicename){


        dba = DBAccess.getInstance(frmIngress.this);

        String query = "insert into TRIPCOST select "+ tripid  +",'"+ devicename +"',C.ID,0,null,'' " +
                "from COSTTYPE C left join TRIPCOST TC ON TC.COSTTYPEID=C.ID and TC.TRIPID='"+ tripid +"' where TC.COSTTYPEID is null";

        if (!dba.executeQuery(query)) {
            Toast.makeText(frmIngress.this, "Can't insert data to database! insert tripcost ingress", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("success", "printingress tripcost");

            String query1 = "update TRIP set ENDDATETIMESTAMP=(datetime('now', 'localtime')) where ID='"+ tripid +"'";

            if (!dba.executeQuery(query1)) {
                Toast.makeText(frmIngress.this, "Can't insert data to database! insert tripcost ingress", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("success", "printingress tripcost");
                printIngresso();

            }

            }

    }

    private void tripreport(int Segment){
        DBQuery dbQuery = new DBQuery(frmIngress.this);
        vehicle = GlobalVariable.getName_bus();
        busmode = GlobalVariable.getModeid();
        String trip = GlobalVariable.getLasttrip();
        dbQuery.getDispatch(trip);
        gCurrentKM = -1;
        String line = GlobalVariable.getLineid();
        if(Segment == 0){

            if(gCurrentKM != -1){
                int c = dbQuery.getCurrentKMone(trip, gCurrentKM);
            }else{
                int cc = dbQuery.getCurrentKMtwo(trip);
                if(cc == 0){
                    String d = GlobalVariable.getDirection();
                    if(d.toString().equals("1")){
                        linesegment = dbQuery.getCurrentKMthree(line);
                    }else{
                        linesegment = dbQuery.getCurrentKMfour(line);
                    }
                }else{
                    checkpoint = dbQuery.getCurrentKMfive(line, linesegment);
                }
            }
        }else {
            String d = GlobalVariable.getDirection();
            if (d.toString().equals("1")) {
                checkpoint = dbQuery.getCurrentKMthree(line);
            }else{
                checkpoint = dbQuery.getCurrentKMfour(line);
            }
        }

        tripcount = dbQuery.getTripCount(trip);
        if(Segment == 0 ) {
            if (tripcount == 1) {
                refDate = dbQuery.getRefDateOne(trip);
            } else {
                refDate = dbQuery.getRefDateTwo(trip);
            }



            ptype = dbQuery.getTicketsPerPassengers(trip,refDate);
            ptype2 = dbQuery.getTicketsIndividual(trip,refDate);


        }else{

            ptype = dbQuery.getTicketsCountOne(trip);
            ptype2 = dbQuery.getTicketsCountTwo(trip);


        }

        String one = ptype.toString();
        String two = "" +ptype.equals(1);
        String three= "" +ptype.equals(2);
        String four = "" +ptype.equals(3);

        String[] test = ptype;
        String[] test2 = ptype2;
        String str = convertStringArrayToString(test2, ",");
        Log.wtf("shit", str);

        List<Object> list = new ArrayList<Object>(Arrays.asList(test2));
        for(int i = 0 ; i < test2.length; i ++){
           Object o =  list.get(i).equals("netAmount");
          String g = String.valueOf(o);
        }




//        for(String object: list){
//            Log.wtf("Stringfuck", object);
//            if(object.contains("netAmount")){
//
//                String x = String.valueOf(object.contains("netAmount"));
//                String[] xx = x.split("/");
//                String x4 = xx[0];
//                Log.wtf("fuckString", x4);
//                continue;
//            }
//
//
//
//        }

//        for(String type: ptype2){
//            tripcount_tr = 0;
//            int  tripc= 1;
//
//            int x2 = Integer.parseInt(ptype2.get(2));
//            int x3 = Integer.parseInt(ptype2.get(3));
//            int x4 = Integer.parseInt(ptype2.get(4));
//            gross_tr += x4;
//
//            if(curdir != (x3 - x2) / Math.abs(x3 - x2)){
//                tripcount_tr = tripcount_tr + tripc;
//            }
//            curdir = (x3 - x2) / Math.abs(x3 - x2);
//        }
    }

    private static String convertStringArrayToString(String[] strArr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr)
            sb.append(str).append(delimiter);
        return sb.substring(0, sb.length() - 1);
    }

    private void updateAmounts(){

        totalgross = gross +  penalty + manual - cancel;
        computeExpense();
        specialTrip.setText(String.valueOf(manual));
        cancelled.setText(String.valueOf(cancel));
        totalGross_text.setText(String.valueOf(totalgross));

        DBQuery dbQuery = new DBQuery(frmIngress.this);
        expense = Integer.parseInt(dbQuery.updateAmountExpense(GlobalVariable.getLasttrip()));


        expenses_text.setText(String.format("%.2f", expense - (driamt + condamt )));
        withholding_text.setText(String.valueOf(withholding));
        totalCommission_text.setText(String.format("%.2f", driamt + condamt));
        netsales_text.setText(String.format("%.2f", totalgross - expense + withholding));
        partial_text.setText(String.valueOf(partialremit));
        shortamt = totalgross - partialremit - expense + withholding;
        shortover_text.setText(String.format("%.2f", -shortamt));
    }

    private void printIngresso(){


        AlertDialog.Builder builder = new AlertDialog.Builder(frmIngress.this);
        builder.setTitle("Ingress");
        builder.setMessage("Print another copy of Ingresso?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                printIngresso();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                b_ingress.setVisibility(View.GONE);
                dialog.dismiss();
                Toast.makeText(frmIngress.this, "Trip Ended.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void printTripReport(){



    }



}
