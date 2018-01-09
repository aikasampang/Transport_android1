package com.transport.organelles.transport_.forms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.transport.organelles.transport_.classforms.DBAccess;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.GlobalClass;
import com.transport.organelles.transport_.classforms.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Organelles on 6/14/2017.
 */

public class frmPartial extends AppCompatActivity {

    TextView txtDateTime, p_ticketsales;
    EditText p_amount, p_cashiername, p_cashierpin;
    String p_name, p_password,p_ts, p_amt, sqlQuery, dtstartTime;
    Button save;
    DBAccess dba;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmpartial);
        getSupportActionBar().show();
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        getSupportActionBar().setSubtitle(currentDateTimeString);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // setTitleBar();
        employeeLogin();
        setObjects();
        objectListeners();


    }
    private void setObjects(){
        p_ticketsales = (TextView)findViewById(R.id.p_ticketsales);
        p_amount = (EditText) findViewById(R.id.p_amount);
//        p_cashiername = (EditText)findViewById(R.id.p_cashier);
//        p_cashierpin = (EditText)findViewById(R.id.p_cashierpin);
        save = (Button)findViewById(R.id.p_save);
    }

    private void objectListeners(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p_amount.getText().toString().length() == 0){
                    Toast.makeText(frmPartial.this, "Invalid Partial Amount.", Toast.LENGTH_LONG).show();
                }else{
                    confirmation();
                }
           }
        });

        DBQuery dbQuery = new DBQuery(frmPartial.this);
        String name = GlobalVariable.getPhoneName();
        String tripid = dbQuery.getTripId(name);
        String totaltickets = dbQuery.getTicketSales(tripid);
        p_ticketsales.setText(totaltickets);
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
                                String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmPartial.this));
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

    private void employeeLogin(){
        final DBQuery dbQuery = new DBQuery(frmPartial.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.modal_employeelogin, null);
       // final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
        final EditText password = (EditText) alertLayout.findViewById(R.id.password);

//        String[] spinnerNames = dbQuery.getName(5);
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmPartial.this,android.R.layout.simple_spinner_item, spinnerNames);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        name.setAdapter(spinnerAdapter);
        AlertDialog.Builder alert = new AlertDialog.Builder(frmPartial.this);
        alert.setTitle("Login");
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


               // p_name = name.getText().toString();
                p_password = password.getText().toString();

                String authorize = dbQuery.getAuthenticate(p_password);
                if(authorize.equals("invalid")){
                    dialog.dismiss();
                    Toast.makeText(frmPartial.this, "Please input the right password for this User..", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(frmPartial.this, frmMain.class);
                    startActivity(intent);
                    finish();
                }else{
                    dialog.dismiss();
                    p_name = authorize;
//                    p_cashiername.setText(p_name);
//                    p_cashierpin.setText(p_password);

                }
            }
        });
        alert.setCancelable(false);
        alert.show();


    }

    private void confirmation(){


        AlertDialog.Builder builder = new AlertDialog.Builder(frmPartial.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you wish to continue?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePartial();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    private void savePartial(){

        DBQuery dbQuery = new DBQuery(frmPartial.this);
        dba = DBAccess.getInstance(frmPartial.this);
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        String trip = dbQuery.getLastTrip();
        String devicename = GlobalVariable.getPhoneName();
        String cashierID = dbQuery.getEmployeeID(p_name);
        String date = dtstartTime;
        String txtAmount = p_amount.getText().toString();
        String remarks = "";

        sqlQuery = "INSERT INTO TRIPRECEIPT (TRIPID, DEVICENAME, CUSTOMERID, EMPLOYEEID, DATETIMESTAMP, AMOUNT, REMARKS) " +
                " VALUES ( '"+ trip +"', '" +
                devicename + "', " +
                '1' + ", '" +
                cashierID+ "', '"+
                date+ "',' " +
                txtAmount + "',' " +
                remarks + "' " +
                " ); ";
        Log.wtf("logged sql",sqlQuery);
        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmPartial.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
        }else{
            Log.wtf("frmPartial","save tripreceipt");
            Toast.makeText(frmPartial.this, "Partial Saved!", Toast.LENGTH_LONG).show();
        }
    }


}
