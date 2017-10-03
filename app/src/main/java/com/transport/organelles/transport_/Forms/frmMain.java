package com.transport.organelles.transport_.Forms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.transport.organelles.transport_.Class.DBQuery;
import com.transport.organelles.transport_.Class.GlobalClass;
import com.transport.organelles.transport_.Class.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Organelles on 6/8/2017.
 */

public class frmMain extends AppCompatActivity {

    private TextView txtDateTime, companyName, deviceName, battStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.frmmain);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setModuleContent();
        //setTitleBar();
        setObjects();

        String test = GlobalVariable.d_lasttripid;
        String test2 = GlobalVariable.d_lineid;
        Log.wtf("TRIPID", test + "lineid" + test2);

    }


    private void setObjects(){

        companyName = (TextView)findViewById(R.id.companyName);
        deviceName = (TextView)findViewById(R.id.devicename);
        battStatus = (TextView)findViewById(R.id.battstatus);

        DBQuery dbQuery = new DBQuery(frmMain.this);
        String d = GlobalVariable.getPhoneName();
        String name = dbQuery.getDataDeviceCompany(d);
        String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmMain.this));

        companyName.setText(name);
        deviceName.setText(d);
        battStatus.setText(batteryLevel + "%          ");




    }

//    private void setTitleBar() {
//        //Display login user
//        TextView txtUser = (TextView) findViewById(R.id.txtUser);
//        final TextView txtBattery = (TextView) findViewById(R.id.txtBattery);
//        txtDateTime = (TextView) findViewById(R.id.txtDateTime);
//
//
//        final String percent = "%";
//        final String batteryLevel = "";
//
//        assert txtUser != null;
//        txtUser.setText("Erjohn");
//
//
//        //Thread use for time in mapa main
//        Thread t = new Thread() {
//
//            @Override
//            public void run() {
//                try {
//                    while (!isInterrupted()) {
//                        Thread.sleep(1000);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
//                                assert txtDateTime != null;
//                                txtDateTime.setText(currentDateTimeString);
//                                assert txtBattery != null;
//                                String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmMain.this));
//                                txtBattery.setText( batteryLevel + "%          ");
//                            }
//                        });
//                    }
//                } catch (InterruptedException ignored) {
//                }
//            }
//        };
//        t.start();
//
//    }
    private void setModuleContent(){
        //Fill grid with user module
        final mainGrid gridAdapter = new mainGrid(frmMain.this, GlobalVariable.moduleTXT, GlobalVariable.moduleIMG);
        final GridView myGrid = (GridView) findViewById(R.id.myGrid);
        assert myGrid != null;
        myGrid.setAdapter(gridAdapter);
        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(GlobalVariable.moduleTXT[position], "Dispatch")) {
                        DBQuery dbQuery = new DBQuery(frmMain.this);
                        String device = GlobalVariable.getPhoneName();
                        String trip = dbQuery.getTripId(device);
                        String ticket_count = dbQuery.getTicketCount(trip);

                        if(trip.equals("0")){
                            String data = "newdata";
                            Intent intent = new Intent(getBaseContext(), frmDispatch.class);
                            intent.putExtra("data", data);
                            startActivity(intent);

                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(frmMain.this);
                            builder.setTitle("Dispatch");
                            builder.setMessage("There is "+ticket_count+"number of ticket(s) on this trip");
                            builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String data = "updatedispatch";
                                    Intent intent = new Intent(getBaseContext(), frmDispatch.class);
                                    intent.putExtra("data", data);
                                    startActivity(intent);
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



                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Ticketing"))) {
                        Intent intent = new Intent (getBaseContext(), frmTicket.class);
                        startActivity(intent);


                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Inspector"))) {

                        LayoutInflater inflater = getLayoutInflater();
                        View alertLayout = inflater.inflate(R.layout.menu_control, null);
                        final Button inspector = (Button) alertLayout.findViewById(R.id.inspectors);
                        final Button controller = (Button) alertLayout.findViewById(R.id.controller);
                        final Button arrival = (Button) alertLayout.findViewById(R.id.arrival);
                        final Button ift = (Button) alertLayout.findViewById(R.id.ift);
                        AlertDialog.Builder alert = new AlertDialog.Builder(frmMain.this);
                        alert.setTitle("Select Operation");


                        inspector.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name_type = "inspector";
                                String type = "3";
                                Intent intent = new Intent (getBaseContext(), frmInspector.class);
                                intent.putExtra("name_type", name_type);
                                intent.putExtra("type", type);
                                startActivity(intent);
                            }
                        });
                        controller.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name_type = "controller";
                                String type = "4";
                                Intent intent = new Intent (getBaseContext(), frmInspector.class);
                                intent.putExtra("name_type", name_type);
                                intent.putExtra("type", type);
                                startActivity(intent);
                            }
                        });
                        arrival.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(), frmArrival.class);
                                startActivity(intent);
                            }
                        });
                        ift.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(), frmInspectionfromTerminal.class);
                                startActivity(intent);
                            }
                        });
                        alert.setView(alertLayout);
                        alert.show();





                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Partial"))) {
                        Intent intent = new Intent (getBaseContext(), frmPartial.class);
                        startActivity(intent);

                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Reverse"))) {
                        Intent intent = new Intent (getBaseContext(), frmReverse.class);
                        startActivity(intent);

                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Ingress"))) {
                        final DBQuery dbQuery = new DBQuery(frmMain.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View alertLayout = inflater.inflate(R.layout.modal_employeelogin, null);
                        final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
                        final EditText pass = (EditText) alertLayout.findViewById(R.id.password);

                        String[] spinnerNames = dbQuery.getName(5);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmMain.this,android.R.layout.simple_spinner_item, spinnerNames);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        name.setAdapter(spinnerAdapter);

                        AlertDialog.Builder alert = new AlertDialog.Builder(frmMain.this);
                        alert.setTitle("Login");
                        alert.setView(alertLayout);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String i_name = name.getText().toString();
                                String i_password = pass.getText().toString();
                                String cashierID = dbQuery.getIDfromName(i_name);
                                Log.wtf("cashierid", cashierID);
                                GlobalVariable.setCashierID(cashierID);
                                String authorize = dbQuery.getPassword(i_name);
                                if(authorize.toString().equals(i_password)){
                                    dialog.dismiss();
                                    Intent intent = new Intent (getBaseContext(), frmIngress.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(frmMain.this, "Wrong Password!!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();





                    }
                }
            }
        });
    }


}
