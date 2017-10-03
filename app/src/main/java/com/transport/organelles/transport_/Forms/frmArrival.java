package com.transport.organelles.transport_.Forms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.transport.organelles.transport_.Class.DBAccess;
import com.transport.organelles.transport_.Class.DBQuery;
import com.transport.organelles.transport_.Class.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Organelles on 7/3/2017.
 */

public class frmArrival extends AppCompatActivity {

    AutoCompleteTextView name;
    EditText password;
    Button save;
    String dtstartTime, sqlQuery, direct, direction;
    DBAccess dba;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmarrival);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setObject();
        objectListener();
    }

    private void setObject(){
        name = (AutoCompleteTextView)findViewById(R.id.a_name);
        password = (EditText)findViewById(R.id.a_password);
        save = (Button)findViewById(R.id.a_save);
    }

    private void objectListener(){
        DBQuery dbQuery = new DBQuery(frmArrival.this);
        String[] spinnerNames = dbQuery.getName(1);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmArrival.this,android.R.layout.simple_spinner_item, spinnerNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name.setAdapter(spinnerAdapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTripInspection();
            }
        });
    }
    private void saveTripInspection(){
        String n = name.getText().toString();
        String p = password.getText().toString();
        DBQuery dbQuery = new DBQuery(frmArrival.this);
        dba = DBAccess.getInstance(frmArrival.this);
        String authenticate = dbQuery.getPassword(n);
        if(authenticate.equals(p)){
            Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dtstartTime = formatter.format(dtTemp);
            String device = GlobalVariable.getPhoneName();
            direct = dbQuery.getDirectionValue(device);
            dbQuery.getDirectionMinMax(GlobalVariable.getLineid());
            if(direct.toString().equals("1")){
                direction = GlobalVariable.getDirectionMax();
            }else{
                direction = GlobalVariable.getDirectionMin();
            }


            String trip = GlobalVariable.d_lasttripid;
            String devicename = GlobalVariable.getPhoneName();
            String datetime = dtstartTime;
            String dispatcher = dbQuery.getEmployeeID(name.getText().toString());
            String origin = direction;
            String lastticket = GlobalVariable.d_lastticketid;
            //check pcount and qty both 0
            sqlQuery = "INSERT INTO `TRIPINSPECTION` (TRIPID, DEVICENAME, DATETIMESTAMP, EMPLOYEEID, ATTRIBUTEID, QTY, KMPOST, PCOUNT, LINESEGMENT,DIRECTION, BCOUNT, TICKETID) " +
                    " VALUES ( '"+ trip +"', '" +
                    devicename + "', '" +
                    datetime + "', '" +
                    dispatcher+ "', '11' , '0', '"+
                    origin+ "','0','1','"+ direct +"','0', '" +
                    lastticket + "' " +
                    " ); ";
            Log.wtf("logged sql",sqlQuery);

            if (!dba.executeQuery(sqlQuery)) {
                Toast.makeText(frmArrival.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            }else{
                Log.wtf("test","save tripinspecion");
            }


        }else{
            Toast.makeText(frmArrival.this, "Wrong Password", Toast.LENGTH_SHORT).show();
        }
    }
}
