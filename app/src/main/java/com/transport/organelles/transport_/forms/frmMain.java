package com.transport.organelles.transport_.forms;

import android.app.AlertDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.transport.organelles.transport_.classforms.BluetoothA2DPRequester;
import com.transport.organelles.transport_.classforms.BluetoothBroadcastReceiver;
import com.transport.organelles.transport_.classforms.DBObject;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.GlobalClass;
import com.transport.organelles.transport_.classforms.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Organelles on 6/8/2017.
 */

public class frmMain extends AppCompatActivity  implements BluetoothBroadcastReceiver.Callback, BluetoothA2DPRequester.Callback{

    private TextView txtDateTime, companyName, deviceName, battStatus,datetime;
    final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    String dtstartTime = "";
    String data;
    private static String bluetooth_name = "Qsprinter";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.frmmain);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setModuleContent();
        //setTitleBar();
        setObjects();
       // connectBluetooth();
        objectListener();
        String test = GlobalVariable.d_lasttripid;
        String test2 = GlobalVariable.d_lineid;
        Log.wtf("TRIPID", test + "lineid" + test2);


    }


    private void setObjects() {

        companyName = (TextView) findViewById(R.id.companyName);
        deviceName = (TextView) findViewById(R.id.devicename);
        battStatus = (TextView) findViewById(R.id.battstatus);
        datetime = (TextView)findViewById(R.id.datetime);

        DBQuery dbQuery = new DBQuery(frmMain.this);
        String d = GlobalVariable.getPhoneName();
        String name = dbQuery.getDataDeviceCompany(d);
        String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmMain.this));
        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);


        companyName.setText(name);
        deviceName.setText(d);
        battStatus.setText(batteryLevel + "%");
        datetime.setText(dtstartTime);
    }

    private void objectListener(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter.isEnabled()){
            onBluetoothConnected();
            return;
        }
        if (mBluetoothAdapter.enable()) {
            BluetoothBroadcastReceiver.register(this, this);
        } else {
            Log.e("TAG", "Unable to enable Bluetooth. Is Airplane Mode enabled?");
        }




    }

    private void setModuleContent() {
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
                        int ticket_count = dbQuery.getTicketCount(trip);

                        if (trip.equals("0")) {
                            String data = "newdata";
                            Intent intent = new Intent(getBaseContext(), frmDispatch.class);
                            intent.putExtra("data", data);
                            startActivity(intent);

                        } else { //update dispatch

                            if (ticket_count == 0) { // ticket = 0
                                AlertDialog.Builder builder = new AlertDialog.Builder(frmMain.this);
                                builder.setTitle("Dispatch");
                                builder.setMessage("There is " + ticket_count + "number of ticket(s) on this trip");
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
                                builder.setCancelable(false);
                                builder.show();


                            } else { //have tickets
                                AlertDialog.Builder builder = new AlertDialog.Builder(frmMain.this);
                                builder.setTitle("Dispatch")
                                        .setMessage("There is" + ticket_count + "number of ticket(s) on this trip")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }


                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Ticketing"))) {
                        data = getCheckDatabase();
                        if (data.equals("0")) {
                            Toast.makeText(frmMain.this, "Please dispatch first.. ", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getBaseContext(), frmTicket.class);
                            startActivity(intent);
                        }

                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Inspector"))) {
                        data = getCheckDatabase();
                        if (data.equals("0")) {
                            Toast.makeText(frmMain.this, "Please dispatch first.. ", Toast.LENGTH_SHORT).show();
                        } else {


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
                                    String type = "1";
                                    Intent intent = new Intent(getBaseContext(), frmInspector.class);
                                    intent.putExtra("name_type", name_type);
                                    intent.putExtra("type", type);
                                    startActivity(intent);
                                }
                            });
                            controller.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name_type = "controller";
                                    String type = "2";
                                    Intent intent = new Intent(getBaseContext(), frmInspector.class);
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
                                    Intent intent = new Intent(getBaseContext(), frmDispatchfromTerminal.class);
                                    startActivity(intent);
                                }
                            });
                            alert.setView(alertLayout);
                            alert.show();
                        }

                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Partial"))) {
                        data = getCheckDatabase();
                        if (data.equals("0")) {
                            Toast.makeText(frmMain.this, "Please dispatch first.. ", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getBaseContext(), frmPartial.class);
                            startActivity(intent);
                        }

                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Reverse"))) {
                        data = getCheckDatabase();
                        if (data.equals("0")) {
                            Toast.makeText(frmMain.this, "Please dispatch first.. ", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getBaseContext(), frmReverse.class);
                            startActivity(intent);
                        }

                    } else if ((Objects.equals(GlobalVariable.moduleTXT[position], "Ingress"))) {
                        data = getCheckDatabase();
                        if (data.equals("0")) {
                            Toast.makeText(frmMain.this, "Please dispatch first.. ", Toast.LENGTH_SHORT).show();
                        } else {
                            final DBQuery dbQuery = new DBQuery(frmMain.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View alertLayout = inflater.inflate(R.layout.modal_employeelogin, null);
                            //final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
                            final EditText pass = (EditText) alertLayout.findViewById(R.id.password);

                            AlertDialog.Builder alert = new AlertDialog.Builder(frmMain.this);
                            alert.setTitle("Code");
                            alert.setView(alertLayout);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DBQuery db = new DBQuery(frmMain.this);
                                    String code = db.universalcode();
                                    String pw = pass.getText().toString();
                                    if (code.equals(pw)) {
                                        login();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(frmMain.this, "Please input the correct password..", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setCancelable(false);
                            alert.show();
//                        String[] spinnerNames = dbQuery.getName(5);
//                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmMain.this, android.R.layout.simple_spinner_item, spinnerNames);
//                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //name.setAdapter(spinnerAdapter);
                        }
                    }
                }
            }
        });
    }

//    private void pairDevice(BluetoothDevice device) {
//        try {
//            Log.d("pairDevice()", "Start Pairing...");
//            Method m = device.getClass().getMethod("createBond", (Class[]) null);
//            m.invoke(device, (Object[]) null);
//            Log.d("pairDevice()", "Pairing finished.");
//        } catch (Exception e) {
//            Log.e("pairDevice()", e.getMessage());
//        }
//
//    }

    public void login(){
        final DBQuery dbQuery = new DBQuery(frmMain.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.modal_employeelogin, null);
        //final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
        final EditText pass = (EditText) alertLayout.findViewById(R.id.password);
        AlertDialog.Builder alert = new AlertDialog.Builder(frmMain.this);
        alert.setTitle("Login");
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //String i_name = name.getText().toString();
                String i_password = pass.getText().toString();
                //String cashierID = dbQuery.getIDfromName(i_name);
                //Log.wtf("cashierid", cashierID);
                // GlobalVariable.setCashierID(cashierID);
                String authorize = dbQuery.getAuthenticate(i_password);
                if (authorize.equals("invalid")) {
                    Toast.makeText(frmMain.this, "Wrong Password!!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Intent intent = new Intent(getBaseContext(), frmIngress.class);
                    startActivity(intent);
                }
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();



    }

    public String getCheckDatabase(){
        DBQuery dbQuery = new DBQuery(frmMain.this);
        String hasValue = dbQuery.checkdatabase();
        return hasValue;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode==RESULT_OK){
                Toast.makeText(frmMain.this, "BlueTooth Turned On", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(frmMain.this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    public void onA2DPProxyReceived(BluetoothA2dp proxy) {
        Method connect = getConnectMethod();
        BluetoothDevice device = findBondedDeviceByName(mBluetoothAdapter, bluetooth_name);


        //If either is null, just return. The errors have already been logged
        if (connect == null || device == null) {
            return;
        }

        try {
            connect.setAccessible(true);
            connect.invoke(proxy, device);
        } catch (InvocationTargetException ex) {
            Log.e("TAG", "Unable to invoke connect(BluetoothDevice) method on proxy. " + ex.toString());
        } catch (IllegalAccessException ex) {
            Log.e("TAG", "Illegal Access! " + ex.toString());
        }

    }

    @Override
    public void onBluetoothConnected() {
        new BluetoothA2DPRequester(this).request(this, mBluetoothAdapter);
    }

    @Override
    public void onBluetoothError() {
        Log.e("Error", "There was an error enabling the Bluetooth Adapter.");
    }


    private Method getConnectMethod () {
        try {
            return BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
        } catch (NoSuchMethodException ex) {
            Log.e("tag", "Unable to find connect(BluetoothDevice) method in BluetoothA2dp proxy.");
            return null;
        }
    }

    private static BluetoothDevice findBondedDeviceByName (BluetoothAdapter adapter, String name) {
        for (BluetoothDevice device : getBondedDevices(adapter)) {
            if (name.matches(device.getName())) {
                Log.v("TAG", String.format("Found device with name %s and address %s.", device.getName(), device.getAddress()));
                return device;
            }
        }
        Log.w("TAG", String.format("Unable to find device with name %s.", name));
        return null;
    }

    private static Set<BluetoothDevice> getBondedDevices (BluetoothAdapter adapter) {
        Set<BluetoothDevice> results = adapter.getBondedDevices();
        if (results == null) {
            results = new HashSet<BluetoothDevice>();
        }
        return results;
    }
}