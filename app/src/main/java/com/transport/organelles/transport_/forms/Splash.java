package com.transport.organelles.transport_.forms;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.transport.organelles.transport_.classforms.DBAccess;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.GlobalVariable;
import com.transport.organelles.transport_.R;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    String sqlQuery= "";
    DBAccess dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Splash.this, frmMain.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
        deviceData();
    }

    private void deviceData(){
        DBQuery dbQuery = new DBQuery(Splash.this);
        dba = DBAccess.getInstance(Splash.this);
        final String devicename = GlobalVariable.getPhoneName();
        String name = dbQuery.getDeviceName(devicename);

        if(name.toString().equals("-1")) {
            String machinenum = GlobalVariable.getPhoneAddress();
            String serialnum = Build.SERIAL;
            sqlQuery = "INSERT INTO DEVICEDATA (DEVICENAME, KEY, VALUE) VALUES ('" + devicename + "', 'CLIENTNAME', 'DELA ROSA' ), " +
                    "('" + devicename + "', 'ORGNAME', 'ORGANELLES'), " +
                    "('" + devicename + "', 'DIRECTION', '')," +
                    "('" + devicename + "', 'INGRESSPOINT', 'DELA ROSA')," +
                    "('" + devicename + "', 'MACHINENUMER', '" + machinenum + "')," +
                    "('" + devicename + "', 'PERMITNUMBER', '000-0000')," +
                    "('" + devicename + "', 'SERIALNUMBER', '" + serialnum + "')," +
                    "('" + devicename + "', 'LASTTICKETID', '0')," +
                    "('" + devicename + "', 'LASTTRIPID', '0')," +
                    "('" + devicename + "', 'LINE', '0') ";
            Log.wtf("logged sql", sqlQuery);

            if (!dba.executeQuery(sqlQuery)) {
                Toast.makeText(Splash.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(Splash.this, "Saved", Toast.LENGTH_LONG).show();

        }else{

            dbQuery.getlastTicket(GlobalVariable.getPhoneName());
            String lastticketid = GlobalVariable.d_lastticketid;
            String lasttripid = GlobalVariable.d_lasttripid;
            Toast.makeText(Splash.this, "Last ticket = " +lastticketid + " "  + "Last trip id = " + lasttripid , Toast.LENGTH_LONG).show();


        }

    }

}
