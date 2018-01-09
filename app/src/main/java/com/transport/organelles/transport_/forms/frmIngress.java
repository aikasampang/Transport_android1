package com.transport.organelles.transport_.forms;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.transport.organelles.transport_.classforms.BluetoothService;
import com.transport.organelles.transport_.classforms.Costtype;
import com.transport.organelles.transport_.classforms.DBAccess;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.DeviceListActivity;
import com.transport.organelles.transport_.classforms.GlobalClass;
import com.transport.organelles.transport_.classforms.GlobalVariable;
import com.transport.organelles.transport_.classforms.Withholding;
import com.transport.organelles.transport_.R;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Organelles on 6/14/2017.
 */

public class frmIngress extends AppCompatActivity {
    TextView txtDateTime, totalCollection_text, totalGross_text, expenses_text, withholding_text, totalCommission_text, netsales_text, partial_text, shortover_text;
    EditText specialTrip, cancelled, finalremit;
    Button b_finalremit, b_ingress, b_tripreport, b_expense, b_withholding;
    String  tripId, sqlQuery = "", dtstartTime, vehicle, busmode,linesegment = "" , checkpoint ="", refDate = "" , tripnum;
    int gCurrentKM= 0, mpadcount, penalty,  manual, partialremit, expense, withholding, lastremit,
            tripcount, gross_tr,tripcount_tr, remitfinal, segment;
    Double driamt, condamt, condrate, drirate, gross, totalgross, shortamt, basisgross, dribonus, condbonus, cancel;
    DBAccess dba;
    Spinner terminal;

    String[] ptype, ptype2;
    ArrayList<Costtype> costtypes = new ArrayList<>();
    ArrayList<Withholding> withholdings = null;
    ArrayList<List> uploadWith;

    Costtype costtype;
    ArrayList<Costtype> arrCost = new ArrayList<>();
    private static final boolean D = true;
    private static final String TAG = "BTPrinter";
    private String mConnectedDeviceName = null;
    private BluetoothService mService = null;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;

    private List<String> list_drislip, list_condslip, list_inspectionlog, list_dispatchIngress, list_arrival, list_partialdetails, list_reverselog, list_inspectionTripReport,
    list_controlledTripReport, list_aoc;
    int curdir = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmingress);
        getSupportActionBar().show();
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        getSupportActionBar().setSubtitle(currentDateTimeString);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setTitleBar();
        setObjects();
        objectListeners();
        formLoad();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        // Initialize the BluetoothService to perform bluetooth connections
        mService = new BluetoothService(this, bluetoothHandler);



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


        final DBQuery dbQuery = new DBQuery(frmIngress.this);
        tripId = dbQuery.getLastTrip();
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

                if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                    Toast.makeText(frmIngress.this, R.string.not_connected, Toast.LENGTH_LONG).show();
                    return;
                }
                Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dtstartTime = formatter.format(dtTemp);
                sqlQuery = "update TRIP set ENDDATETIMESTAMP='"+dtstartTime +"' where ID='"+tripId+"'";

                if (!dba.executeQuery(sqlQuery)) {
                    Toast.makeText(frmIngress.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.wtf("update direction", "update direction");
                    String tripid = dbQuery.getLastTrip();
                    String devicename = GlobalVariable.getPhoneName();
                    ingress(tripid, devicename);
                }
            }
        });

        b_tripreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                    Toast.makeText(frmIngress.this, R.string.not_connected, Toast.LENGTH_LONG).show();
                    return;
                }

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
                        Double c = Double.parseDouble(c_cancel.getText().toString());
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

///test


//        b_expense.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DBQuery dbQuery1 = new DBQuery(frmIngress.this);
//                String trip = GlobalVariable.getLasttrip();
//                LayoutInflater inflater = getLayoutInflater();
//                View alertLayout = inflater.inflate(R.layout.frmexpense, null);
//                costtypes = dbQuery1.expenseList(trip);
//                final ExpenseAdapter adapter = new ExpenseAdapter(frmIngress.this, R.layout.expense_details,costtypes);
//                final ListView lv= (ListView) alertLayout.findViewById(R.id.list_expense);
//                lv.setAdapter(adapter);
//                AlertDialog.Builder alert = new AlertDialog.Builder(frmIngress.this);
//                alert.setTitle("Expenses");
//                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                        for (int i = 0; i < costtypes.size(); i++) {
//                            costtype = new Costtype();
//                            costtype.getAmount();
//                            costtype.getCosttype();
//                            arrCost.add(costtype);
//
//                            saveCosttype();
//
//                        }
//
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
                final EditText ed_valedri = (EditText) alertLayout.findViewById(R.id.valedri);
                final EditText ed_valecon = (EditText) alertLayout.findViewById(R.id.valecon);
                final EditText ed_otherpay = (EditText) alertLayout.findViewById(R.id.otherpay);
                final EditText ed_epass = (EditText) alertLayout.findViewById(R.id.epass);
                final EditText ed_alimall = (EditText) alertLayout.findViewById(R.id.alimall);
                //WithholdingAdapter adapter = new WithholdingAdapter(frmIngress.this, R.layout.withholding_details, withholdings);
//                ListView view = (ListView)alertLayout.findViewById(R.id.list_withholding);
//                view.setAdapter(adapter);
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
                            String sql2 = "delete from TRIPWITHHOLDING where TRIPID='" + trip + "'";
                            if (!dba.executeQuery(sql2)) {
                                Toast.makeText(frmIngress.this, "Can't delete data to database!. tripwithholding", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.wtf("delete", "tripwithholding");
                            }
                        }

                                String dri = ed_valedri.getText().toString();
                                String con = ed_valecon.getText().toString();
                                String pay = ed_otherpay.getText().toString();
                                String ep = ed_epass.getText().toString();
                                String mall = ed_alimall.getText().toString();

                                String sql3 = "insert into TRIPWITHHOLDING (TRIPID, DEVICENAME, ATTRIBUTEID, AMOUNT)" +
                                        "VALUES" +
                                        "('"+trip+"', '"+ devicename+"' , '4', '"+dri+"' )," +
                                        "('"+trip+"', '"+ devicename+"' , '5', '"+con+"' )," +
                                        "('"+trip+"', '"+ devicename+"' , '6', '"+pay+"' );";
                                if (!dba.executeQuery(sql3)) {
                                    Toast.makeText(frmIngress.this, "Can't delete data to database!. tripwithholding", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.wtf("INSERT", "tripwithholding");
                                }

                                String sql4 = "insert into TRIPUSAGE (TRIPID, DEVICENAME, ATTRIBUTEID, QTY)" +
                                        "VALUES" +
                                        "('"+trip+"', '"+ devicename+"' , '4', '"+ep+"' )," +
                                        "('"+trip+"', '"+ devicename+"' , '6', '"+mall+"' );";

                                if (!dba.executeQuery(sql4)) {
                                    Toast.makeText(frmIngress.this, "Can't delete data to database!. tripusage", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.wtf("INSERT", "tripusage");
                                }



//                                withholdings = dbQuery.withholdingList();
//                               // List<Object> list = new ArrayList<Object>(Arrays.asList(withholdings));
//                                    for (Object list: withholdings){
//                                        Object x0 = list.equals(0);
//                                        Object x1 = list.equals(0);
//                                        Object x2 = list.equals(0);
//                                        Object x3 = list.equals(0);
//                                        Log.wtf("list", x0.toString());
//                                        if(x0.equals("W")){
//                                            String sql3 = "INSERT INTO TRIPWITHHOLDING VALUES('" + trip + "', '" + devicename + "', '"+ x1 +"','" + x3 + "') ";
//                                            Log.wtf("sql", sql3);
//                                            if(!dba.executeQuery(sql3)){
//                                                Toast.makeText(frmIngress.this, "Can't insert data to database . TRIPWITHHOLDING", Toast.LENGTH_LONG).show();
//                                            }else{
//                                                Log.wtf("insert","tripusage");
//                                            }
//                                        }else{
//                                            String sql4 = "INSERT INTO TRIPUSAGE VALUES('" + trip + "', '" + devicename + "', '"+ x1 +"','" + x3 + "') ";
//                                            Log.wtf("sql", sql4);
//                                            if(!dba.executeQuery(sql4)){
//
//                                                Toast.makeText(frmIngress.this, "Can't insert data to database . TRIPUSAGE", Toast.LENGTH_LONG).show();
//                                            }else{
//                                                Log.wtf("insert","tripusage");
//                                            }
//                                        }
//                                    }
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
        final DBQuery dbQuery = new DBQuery(frmIngress.this);
        String tripid = dbQuery.getLastTrip();

        computeGrossAddons();
        computeExpense();



        cancel = Double.parseDouble(dbQuery.getOtherAmt(tripid));
        //cancel = 0;
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

        shortamt = totalgross - partialremit - lastremit - expense + withholding;
        shortover_text.setText(-shortamt + "");

        specialTrip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(specialTrip.getText().equals("0")){
                    specialTrip.setText(0 + "");
                }else{
                    dba = DBAccess.getInstance(frmIngress.this);
                    String trip = dbQuery.getLastTrip();
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
                    String trip = dbQuery.getLastTrip();
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
        String trip = dbQuery.getLastTrip();
        String device = GlobalVariable.getPhoneName();
        dbQuery.getCondDri(trip);
        String cond = GlobalVariable.getCurrent_cond();
        String dri = GlobalVariable.getCurrent_dri();
        String mode = dbQuery.getMode(device);
        String line = dbQuery.getLinefrmDB();

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
        String linename = dbQuery.getLinefrmDB();
        String tag = dbQuery.getLineDB(linename);
        String modeid = dbQuery.getMode(device);

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


        String tripid = dbQuery.getLastTrip();
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
                //printIngresso();
                printTripIngress();
            }

            }

    }

    private void tripreport(int Segment){
        DBQuery dbQuery = new DBQuery(frmIngress.this);
        vehicle = GlobalVariable.getName_bus();
        busmode = GlobalVariable.getModeid();
        String trip = dbQuery.getLastTrip();
        dbQuery.getDispatch(trip);
        gCurrentKM = -1;
        String line = dbQuery.getLineDb();

        list_partialdetails = dbQuery.getPartialDetails(trip);
        list_reverselog = dbQuery.getReverseLog(trip);
        list_inspectionTripReport = dbQuery.getInspectionTripReport(trip);
        list_controlledTripReport = dbQuery.getControlledTripReport(trip);
        dbQuery.getInspection(trip);
        list_aoc = dbQuery.getAreaOfClosing(trip);



        if(Segment == 0){

            if(gCurrentKM != -1){
                int c = dbQuery.getCurrentKMone(trip, gCurrentKM);
            }else{
                segment = dbQuery.getCurrentKMtwo(trip);
                if(segment == 0){
                    String device = GlobalVariable.getPhoneName();
                    String d = dbQuery.getDirectionValue(device);
                    if(d.toString().equals("1")){
                        linesegment = dbQuery.getCurrentKMthree(line);
                    }else{
                        linesegment = dbQuery.getCurrentKMfour(line);
                    }
                }else{
                    checkpoint = dbQuery.getCurrentKMfive(line, segment);
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

//        String one = ptype.toString();
//        String two = "" +ptype.equals(1);
//        String three= "" +ptype.equals(2);
//        String four = "" +ptype.equals(3);
//
//        String[] test = ptype;
//        String[] test2 = ptype2;
//        String str = convertStringArrayToString(test2, ",");
//        Log.wtf("shit", str);

        ptype2 = dbQuery.getTicketsCountTwo(trip);
//        gross = gross + Double.parseDouble(GlobalVariable.getGross_trip());


       // Double fromref = Integer.parseInt(GlobalVariable.getGross_fromrefpoint());
       // Double toref = Double.parseDouble(GlobalVariable.getGross_torefpoint());
        int fref = Integer.parseInt(GlobalVariable.getGross_fromrefpoint());
        int tref = Integer.parseInt(GlobalVariable.getGross_torefpoint());
        if(curdir > ((fref - tref) / (fref - tref))) {
            tripcount = tripcount + 1;
        }
        curdir = (fref - tref) / (fref - tref);


        /*printing of trip report*/

        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        String deviceName = GlobalVariable.getPhoneName();
        String r = dbQuery.getLineName(trip);
        String m = dbQuery.getModeNameTrip(trip);
        String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmIngress.this));


        String companyname = dbQuery.getCompanyName() + "\n";
        String tripreportname = "Trip Report" + "\n";
        String devicename = GlobalVariable.getPhoneName()  + " - " + GlobalVariable.getLasttrip() + "\n";
        String date = "Date: " + dtstartTime + "\n";
        String vehicle = "Vehicle: " + dbQuery.getBusNamefromTrip(trip)  +"\n";
        String dri = "Driver: " + dbQuery.nameDriver(dbQuery.getLastTrip(), "1")+ "\n";
        String cond= "Conductor: " + dbQuery.nameDriver(dbQuery.getLastTrip(), "2") + "\n";
        String route = "Route: " + r  +  "\n";
        String mode = "Mode: " +  m + "\n";
        String cpoint = "Checkpoint: " + checkpoint  + "\n";
        String tcount = "Trip count: " + tripcount +"trip/s"+ "\n";
        String gross = "Gross" + totalgross.toString() + "\n";
        String bat = "Battery Level: " + batteryLevel + "\n";

        Log.wtf("1 tripreport", companyname + " " + tripreportname + " " + devicename + " " + date + " " + vehicle + " " + dri + " " + cond + " " + route + " " + mode + " " + cpoint + " " + tcount + " " + gross + " " + bat );

        //callBluetooth(companyname + " " + tripreportname + " " + devicename + " " + date + " " + vehicle + " " + dri + " " + cond + " " + route + " " + mode + " " + cpoint + " " + tcount + " " + gross + " " + bat );

        String pd = "Partial Details: " + list_partialdetails.toString()  + "\n";
        String rl = "Reverse Log: " + list_reverselog.toString() + "\n";
        String in = "Inspection: " + list_inspectionTripReport.toString()  + "\n";
        String c = "Controlled: " + list_controlledTripReport.toString() + "\n";
        String cvm = "Controlled (mPAD vs Controller)"; //arrange return

        String aoc = "Area of Closing: " +list_aoc.toString()+ "\n";

        String ts = ptype.toString() + "\n";

        String tline = GlobalVariable.getGross_line();
        String tnum = "1";
        String tdir = "0";
        String ttcount = "0";
        String tttotal = "0";

        if(tnum.equals("1")){
            tripnum = "1st";
        }else if(tnum.equals("2")){
            tripnum = "2nd";
        }else if (tnum.equals("3")){
            tripnum = "3rd";
        }else{
            tripnum = tnum + "th";
        }

        String ttpax = " Pax:" + ttcount + "Total: " + tttotal + "\n";

        String th = "--------------- TEAR HERE ------------------";

        Log.wtf("2 tripreport", pd + " " + rl + " " + in + " " + c + " " + cvm + " " + aoc + " " + ts + " " +
         ttpax);

        callBluetooth(pd + " " + rl + " " + in + " " + c + " " + cvm + " " + aoc + " " + ts + " " +
                ttpax);

//        List<Object> list = new ArrayList<Object>(Arrays.asList(test2));
//        for(int i = 0 ; i < test2.length; i ++){
//           Object o =  list.get(i).equals("netAmount");
//          String g = String.valueOf(o);
//        }




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
                printTripIngress();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                b_ingress.setVisibility(View.GONE);
                dialog.dismiss();
                Toast.makeText(frmIngress.this, "Trip Ended.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent (frmIngress.this, frmMain.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();

    }

    private void printTripIngress(){

        DBQuery dbQuery = new DBQuery(frmIngress.this);
        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        tripId = dbQuery.getLastTrip();
        dbQuery.tripData(tripId);
        dbQuery.dispatchIngresso(tripId);
        dbQuery.tripcostIngress(tripId);
        list_inspectionlog = dbQuery.inspectionIngress(tripId);
        list_arrival = dbQuery.arrivalIngress(tripId);
        list_dispatchIngress = dbQuery.dispatchTerminalIngress(tripId);
        dbQuery.controlledIngress(tripId);
        dbQuery.getPartial(tripId);
        dbQuery.getInspection(tripId);

        String title = "Ingresso"+ "\n";
        String device = GlobalVariable.getPhoneName()+ "-" + dbQuery.getLastTicket();

        String date = "Date: " + GlobalVariable.getTripenddate() + "\n";
        String transdatetime = "TransDateTime: " + GlobalVariable.getTripenddate()+   "\n";
        //String transtime = "TransTime: " + "\n";
        String vehicle = "Vehicle:" + GlobalVariable.getTripvehicle()  + "\n";
        String dri = "Driver: " + dbQuery.nameDriver(dbQuery.getLastTrip(), "1")+ "\n";
        String cond= "Conductor: " + dbQuery.nameDriver(dbQuery.getLastTrip(), "2") + "\n";
        String cashier = "Cashier" + GlobalVariable.getTripcashier() +"\n";
        String line = "Line: " + GlobalVariable.getTripline() +"\n";
        String mode = "Mode: " + GlobalVariable.getTripvmode() +"\n";
        String pax = "Passengers: " + dbQuery.getPassengerCount(tripId) +"\n";
        String cash = "Cash Sales: " + String.format("%.2f", gross) + "\n";
        String gross = "Gross Income: " +  String.format("%.2f", totalgross) +"\n \n";

        String ddatetime = "Dispatch: " + GlobalVariable.getD_ingDate()  +"\n";
        String dname = "Dispatcher Name: " + GlobalVariable.getD_ingName()  +"\n";
        String dterminal = "Terminal: " + GlobalVariable.getD_ingRemarks() + "\n";
        String dopening = "Opening: " + GlobalVariable.getD_ingMintk() + "\n";

        Log.wtf("1 ingress", title + device + date + transdatetime + vehicle + dri + cond + cashier + line + mode + pax + cash + gross + ddatetime + dname + dterminal + dopening);

        callBluetooth(title + device + date + transdatetime + vehicle + dri + cond + cashier + line + mode + pax + cash + gross + ddatetime + dname + dterminal + dopening);

        String cost = "Cost: " + String.valueOf(expense) + "\n";

        //print tripcost


        Double netsales = totalgross - expense;
        String net = "Net Sales: " + netsales + "\n \n" ;
        String with = "Withholding: " + String.valueOf(withholding) + "\n";

        // cond and dri bond
        String arr = "Arrival: " + list_arrival.toString() + "px" + " \n";
        //String arr = "Arrival: " + GlobalVariable.getArr_datetime() + " "+  GlobalVariable.getArr_name()+ ""+ GlobalVariable.getArr_post() + ""  + GlobalVariable.getArr__tkid() + "" + GlobalVariable.getArr_qty()  + "px" + " \n";
        //String dft = "Dispatch from Terminal: " + GlobalVariable.getDt_datetime() + " "+  GlobalVariable.getDt_name()+ "" + GlobalVariable.getDt_kmpost() + ""  + GlobalVariable.getDt_tkid() + "" + GlobalVariable.getDt_qty()  + "px" + " \n";
        String dft = "Dispatch from Terminal: " + list_dispatchIngress.toString()  + "px" + " \n";
        //String inspection = "Inspection: " + GlobalVariable.getI_datetime() + " "+  GlobalVariable.getI_name()+ "" + GlobalVariable.getI_kmpost() + ""  + GlobalVariable.getI_tkid() + "" + GlobalVariable.getI_qty()  + "px" + " \n";
        String inspection = "Inspection: " +list_inspectionlog.toString() + "px" + " \n";
        String controlled = "Controlled: " +  GlobalVariable.getC_datetime() + " "+  GlobalVariable.getC_name()+ "" + GlobalVariable.getC_kmpost() + ""  + GlobalVariable.getC_tkid() + "" + GlobalVariable.getC_qty()  + "px" + " \n";
        String cvm = "Controlled (mPAD vs Controller)";

        Log.wtf("2 ingress", cost + "" + net + " " + with + " " + arr + " " + dft + " " + inspection + " " + controlled + "" + cvm);

        callBluetooth(cost + "" + net + " " + with + " " + arr + " " + dft + " " + inspection + " " + controlled + "" + cvm);

        Double rec = Double.parseDouble (dbQuery.getReceipt(tripId));
        cancel = Double.parseDouble(dbQuery.getOtherAmt(tripId));
//        String camt = String.valueOf(cancel);


        String receive = "Received:" + String.format("%.2f", rec) + "\n \n";
        String partial = GlobalVariable.getPartial_name() + "  " + GlobalVariable.getPartial_amount() + "\n";
        String so = "ShortAmount: " + shortamt.toString();
        String c = "Cancelled: " + String.format("%.2f",cancel) + "\n";
        String e = "Thank you." + "\n" + "powered by mPAD" + "\n";

        String fline = "-----------------------------" + "\n \n \n";

        String driName = dbQuery.nameDriver(dbQuery.getLastTrip(), "1") + "\n";
        String driDate = GlobalVariable.getTripenddate()+ "\n";
        list_drislip  = dbQuery.getDripayslip(tripId);
        String dslip = list_drislip.toString() + "\n";

        String condName = dbQuery.nameDriver(dbQuery.getLastTrip(), "2") + "\n";
        String condDate = GlobalVariable.getTripenddate()+ "\n";
        list_condslip = dbQuery.getCondpayslip(tripId);
        String cslip = list_condslip.toString() + "\n";

        Log.wtf("3 ingress", receive + " " + partial + " " + so + " " + c + " " + e + " " + fline+ " " + driName + " " + driDate + " " +  dslip + " " + condName + " " + condDate + " " + cslip);

        callBluetooth(receive + " " + partial + " " + so + " " + c + " " + e + " " + fline+ " " + driName + " " + driDate + " " +  dslip + " " + condName + " " + condDate + " "+ cslip);


        printIngresso();





    }



    private void saveCosttype(){



        Gson gson = new Gson();
        String inputString= gson.toJson(arrCost);

        Log.wtf("inputString= " , inputString+ "");

    }


    private void callBluetooth(String message) {


        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }


        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send;
            try {
                send = message.getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
//				//鍘婚櫎/n;
//				int length = send.length;
//				for(int i=0; i<length;i++){
//					if(send[i]==10){
//						for(int j =i;j<length-1;j++){
//							send[j]=send[j+1];
//						}
//					}
//				}
            }
            mService.write(send);
            Log.wtf("bluetooth", "connected" + send + "");
            //
            // // Reset out string buffer to zero and clear the edit text field
            // mOutStringBuffer.setLength(0);
            // mOutEditText.setText(mOutStringBuffer);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else {
            if (mService == null) ;

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mService != null)
            mService.stop();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter
                                .getRemoteDevice(address);
                        // Attempt to connect to the device
                        mService.connect(device);
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session

                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private final Handler bluetoothHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(frmIngress.this, R.string.title_connected_to + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(frmIngress.this, R.string.title_connecting, Toast.LENGTH_LONG).show();
                            //  mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Log.wtf("BluetoothService", "not connected!");
                            //  Toast.makeText(MapaTicketSales.this, R.string.title_not_connected, Toast.LENGTH_LONG).show();
                            //  mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    // byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    // String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    // byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    // String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_bluetoothOn:
                Intent serverIntent = new Intent(frmIngress.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.action_bluetoothOff:
                mService.stop();
                return true;


        }


        return super.onOptionsItemSelected(item);
    }

}
