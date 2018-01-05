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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
 * Created by Organelles on 7/10/2017.
 */

public class frmReverse extends AppCompatActivity {
    TextView txtDateTime,line_direction;
    Spinner line;
    Button save;
    String line_, line_name, i_name, i_password;
    String lDirection, min, max, dtstartTime, kmpost, remarks;
    DBAccess dba;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmreverse);
        getSupportActionBar().show();
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        getSupportActionBar().setSubtitle(currentDateTimeString);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setObject();
        objectListener();
        //setTitleBar();
        login();


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


        //Thread use for time in mapa main
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
                                String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmReverse.this));
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
    private void login(){
        final DBQuery dbQuery = new DBQuery(frmReverse.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.modal_employeelogin, null);
       // final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
        final EditText password = (EditText) alertLayout.findViewById(R.id.password);

//        String[] spinnerNames = dbQuery.getNames();
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmReverse.this,android.R.layout.simple_spinner_item, spinnerNames);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        name.setAdapter(spinnerAdapter);
        AlertDialog.Builder alert = new AlertDialog.Builder(frmReverse.this);
        alert.setTitle("Login");
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //i_name = name.getText().toString();
                i_password = password.getText().toString();

                String authorize = dbQuery.getAuthenticate(i_password);
                if(authorize.equals("invalid")){
                    dialog.dismiss();
                    Toast.makeText(frmReverse.this, "Please input the right password for this User..", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(frmReverse.this, frmMain.class);
                    startActivity(intent);
                    finish();
                }else{
                   dialog.dismiss();
                    i_name = authorize;


                }
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    private void setObject(){
        line = (Spinner)findViewById(R.id.spin_line);
        line_direction = (TextView)findViewById(R.id.r_direction);
        save = (Button)findViewById(R.id.r_ok);
    }

    private void objectListener(){
        final DBQuery db = new DBQuery(frmReverse.this);
        String[] spinnerLists = db.getLine();
        ArrayAdapter<String> spinnerAdapterDis = new ArrayAdapter<String>(frmReverse.this,android.R.layout.simple_spinner_item, spinnerLists);
        spinnerAdapterDis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        line.setAdapter(spinnerAdapterDis);
        line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                line_ = parent.getItemAtPosition(position).toString();
                line_name = parent.getItemAtPosition(position).toString();
                String idfromName = db.getIDfromLine(line_);
                line_ = idfromName;
                Log.wtf("reverse line",idfromName);
                line_direction.setText(line_name);
                getLineSegment();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReverse();
            }
        });


    }

    private void getLineSegment(){
        DBQuery dbQuery = new DBQuery(frmReverse.this);
        dbQuery.getLineReverse(line_);
        dbQuery.getLineSegmentReverse(line_);
        String device = GlobalVariable.getPhoneName();
        lDirection = dbQuery.getDirectionValue(device);
        min = GlobalVariable.getMin_linesegment();
        max = GlobalVariable.getMax_linesegment();

        if(lDirection.toString().equals("-1"))
        {
            line_direction.setText(min + "-" + max);
        }else{
            line_direction.setText(max + "-" + min);
        }

    }

    public void onClick(View v){
        Toast.makeText(frmReverse.this, "clicked!! " + lDirection, Toast.LENGTH_LONG).show();
        if(!lDirection.toString().equals("-1"))
        {
            line_direction.setText(min + "-" + max);
            lDirection = "-1";
        }else{

            line_direction.setText(max + "-" + min);
            lDirection = "1";

        }

    }

    public void saveReverse(){

        dba = DBAccess.getInstance(frmReverse.this);
        DBQuery dbQuery = new DBQuery(frmReverse.this);
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);




        String device = GlobalVariable.getPhoneName();
        String trip = dbQuery.getLastTrip();
        String datetime = dtstartTime;
        String direction = lDirection;
        String dispatcher = dbQuery.getEmployeeID(i_name);
        if(lDirection.toString().equals("-1")){
             kmpost = GlobalVariable.getMin_reverse();
             remarks = min;
            GlobalVariable.setDirection("SOUTH");
        }else{
            kmpost = GlobalVariable.getMax_reverse();
            remarks = max;
            GlobalVariable.setDirection("NORTH");         }
        String devicename = GlobalVariable.getPhoneName();
        String last_line = GlobalVariable.getLineid();
        if(line_.toString().equals(last_line)){

            String line = line_;
            String query = "INSERT INTO TRIPREVERSE VALUES ('"+trip+"','"+device+"', '"+datetime+"', '"+direction+"', '"+dispatcher+"', '"+kmpost+"', '"+line+"', '"+remarks+"'  )";
            Log.wtf("logged sql",query);

            if (!dba.executeQuery(query)) {
                Toast.makeText(frmReverse.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            }else{
                Log.wtf("","save tripreverse");
                String queryy = "UPDATE DEVICEDATA SET VALUE = '" + lDirection + "' WHERE DEVICENAME = '" + devicename + "' AND KEY = 'DIRECTION'";
                Log.wtf("logged sql",queryy);
                if (!dba.executeQuery(queryy)) {
                    Toast.makeText(frmReverse.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                }else{
                    Log.wtf("","update directiondevicedata");

                }
            }
        }else{
            //new line
            Toast.makeText(frmReverse.this,"new line", Toast.LENGTH_LONG).show();
            String line = line_;
            GlobalVariable.setLineid(line_);
            String query = "INSERT INTO TRIPREVERSE VALUES ('"+trip+"','"+device+"', '"+datetime+"', '"+direction+"', '"+dispatcher+"', '"+kmpost+"', '"+line+"', '"+remarks+"'  )";
            Log.wtf("logged sql",query);

            if (!dba.executeQuery(query)) {
                Toast.makeText(frmReverse.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            }else{
                Log.wtf("","save tripreverse");
                String queryy = "UPDATE DEVICEDATA SET VALUE = '" + lDirection + "' WHERE DEVICENAME = '" + devicename + "' AND KEY = 'DIRECTION'";
                Log.wtf("logged sql",queryy);

                if (!dba.executeQuery(queryy)) {
                    Toast.makeText(frmReverse.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                }else{
                    Log.wtf("fuck","update directiondevicedata");
                    String sql= "UPDATE DEVICEDATA SET VALUE = '" + line + "' WHERE DEVICENAME = '" + devicename + "' AND KEY = 'LINE'";
                    Log.wtf("logged sql",queryy);

                        if (!dba.executeQuery(sql)) {
                            Toast.makeText(frmReverse.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.wtf("fuck again","update linedevicedata");
                        }
                    }
            }
        }
    }

}
