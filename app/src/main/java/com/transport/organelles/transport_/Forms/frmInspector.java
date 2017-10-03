package com.transport.organelles.transport_.Forms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.transport.organelles.transport_.Class.DBAccess;
import com.transport.organelles.transport_.Class.DBQuery;
import com.transport.organelles.transport_.Class.GlobalClass;
import com.transport.organelles.transport_.Class.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Organelles on 6/14/2017.
 */

public class frmInspector extends AppCompatActivity {

    TextView txtDateTime, pax_count;
    EditText kmpost, pax_acount;
    Button save;
    Bundle bundle;
    String name_type, type, sqlQuery, i_name, i_password;
    String dtstartTime = "";
    private DBAccess dba;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frminspector);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitleBar();
        setObject();
        objectListener();

        bundle= getIntent().getExtras();
        name_type = bundle.getString("name_type");
        type = bundle.getString("type");
        Log.wtf("type", name_type + type);

        employeeLogin();

    //if inspector attribute id is 2 else 1


    }

    private void setObject(){

        pax_count = (TextView)findViewById(R.id.i_pcount);
        pax_acount = (EditText) findViewById(R.id.i_acount);
        kmpost = (EditText)findViewById(R.id.i_km);
        save = (Button) findViewById(R.id.i_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInspector();
            }
        });
    }
    private void objectListener(){

        kmpost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pax_count.setText("0");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                DBQuery dbQuery = new DBQuery(frmInspector.this);
                String line = GlobalVariable.getLasttrip();
                String k = kmpost.getText().toString();
                String c = dbQuery.getRemainingPaxControl(line, k).toString();
                pax_count.setText(c);
            }
        });



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
                                String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmInspector.this));
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
        final DBQuery dbQuery = new DBQuery(frmInspector.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.modal_employeelogin, null);
        final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
        final EditText password = (EditText) alertLayout.findViewById(R.id.password);

        String[] spinnerNames = dbQuery.getName(Integer.parseInt(type));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmInspector.this,android.R.layout.simple_spinner_item, spinnerNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name.setAdapter(spinnerAdapter);
        AlertDialog.Builder alert = new AlertDialog.Builder(frmInspector.this);
        alert.setTitle("Login");
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                i_name = name.getText().toString();
                i_password = password.getText().toString();

                String authorize = dbQuery.getPassword(i_name);
                if(authorize.toString().equals(i_password)){
                    dialog.dismiss();
                }else{
                    Toast.makeText(frmInspector.this, "Wrong Password!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setCancelable(false);
        alert.show();


    }
    private void saveInspector(){

        dba = DBAccess.getInstance(frmInspector.this);
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        DBQuery dbQuery = new DBQuery(frmInspector.this);


        String trip = GlobalVariable.d_lasttripid;
        String devicename = GlobalVariable.getPhoneName();
        String datetime = dtstartTime;
        String dispatcher = dbQuery.getEmployeeID(i_name);
        String origin = kmpost.getText().toString();
        String lastticket = GlobalVariable.d_lastticketid;

        sqlQuery = "INSERT INTO `TRIPINSPECTION` (TRIPID, DEVICENAME, DATETIMESTAMP, EMPLOYEEID, ATTRIBUTEID, QTY, KMPOST, PCOUNT, LINESEGMENT, DIRECTION, BCOUNT, TICKETID) " +
                " VALUES ( '"+ trip +"', '" +
                devicename + "', '" +
                datetime + "', '" +
                dispatcher+ "','" +
                '2' + "','" +
                pax_acount.getText().toString() + "','" +
                origin+ "','" +
                '0' + "','" +
                '1' + "','" +
                GlobalVariable.d_direction + "','" +
                "NULL" + "','" +
                lastticket+ "'" +
//                dispatcher+ "', '5' , '0', '"+
//                origin+ "','0','1','0',' " +
//                lastticket + "' " +
                " ); ";
        Log.wtf("logged sql",sqlQuery);

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmInspector.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
        }else{
            Log.wtf("TEST","save tripinspecion");
        }


    }





}
