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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.transport.organelles.transport_.classforms.BluetoothService;
import com.transport.organelles.transport_.classforms.DBAccess;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.DeviceListActivity;
import com.transport.organelles.transport_.classforms.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Organelles on 7/3/2017.
 */

public class frmDispatchfromTerminal extends AppCompatActivity {

    AutoCompleteTextView name;
    EditText count;
    Button save;
    String direct, direction, dtstartTime;
    String sqlQuery = "";
    DBAccess dba;
    String i_password, name_inspector;

    private TextView txtDateTime;
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
    ImageView bluetooth;
    View include;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frminspectionfromterminal);
        getSupportActionBar().show();
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        getSupportActionBar().setSubtitle(currentDateTimeString);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setObject();
        objectListener();
        employeeLogin();

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

    private void setObject(){


       // name  = (AutoCompleteTextView)findViewById(R.id.ift_name);
        count = (EditText)findViewById(R.id.paxCount);
        save = (Button)findViewById(R.id.ift_save);
        //bluetooth = (ImageView)findViewById(R.id.bluetooth);
    }
    private void objectListener(){
//        DBQuery dbQuery = new DBQuery(frmDispatchfromTerminal.this);
//        String[] spinnerNames = dbQuery.getName(1);
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmDispatchfromTerminal.this,android.R.layout.simple_spinner_item, spinnerNames);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        name.setAdapter(spinnerAdapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                    Toast.makeText(frmDispatchfromTerminal.this, R.string.not_connected, Toast.LENGTH_LONG).show();
                    return;
                }


                saveTripInspection();
            }
        });

//        bluetooth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LayoutInflater inflater = getLayoutInflater();
//                View alertLayout = inflater.inflate(R.layout.modal_bluetooth, null);
//                final Button connect = (Button) alertLayout.findViewById(R.id.connect);
//                final Button disconnect = (Button) alertLayout.findViewById(R.id.disconnect);
//                AlertDialog.Builder alert = new AlertDialog.Builder(frmDispatchfromTerminal.this);
//
//                connect.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent serverIntent = new Intent(frmDispatchfromTerminal.this, DeviceListActivity.class);
//                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//
//                    }
//                });
//
//                disconnect.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mService.stop();
//                    }
//                });
//                alert.setTitle("Bluetooth");
//                alert.setView(alertLayout);
//                alert.show();
//
//
//
//            }
//        });
    }
    private void employeeLogin(){
        final DBQuery dbQuery = new DBQuery(frmDispatchfromTerminal.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.modal_employeelogin, null);
        //final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
        final EditText password = (EditText) alertLayout.findViewById(R.id.password);

//        String[] spinnerNames = dbQuery.getName(Integer.parseInt(type));
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmInspector.this,android.R.layout.simple_spinner_item, spinnerNames);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        name.setAdapter(spinnerAdapter);
        AlertDialog.Builder alert = new AlertDialog.Builder(frmDispatchfromTerminal.this);
        alert.setTitle("Login");
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                // i_name = name.getText().toString();
                i_password = password.getText().toString();

                String authorize = dbQuery.getAuthenticate(i_password);
                if(authorize.equals("invalid")){
                    dialog.dismiss();
                    Toast.makeText(frmDispatchfromTerminal.this, "Please input the right password for this User..", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (frmDispatchfromTerminal.this, frmMain.class);
                    startActivity(intent);
                    finish();
                }else{
                    name_inspector = authorize;
                    dialog.dismiss();
                }

//                if(authorize.toString().equals(i_password)){
//                    dialog.dismiss();
//                }else{
//                    Toast.makeText(frmInspector.this, "Wrong Password!!", Toast.LENGTH_LONG).show();
//                }
            }
        });
        alert.setCancelable(false);
        alert.show();


    }


    private void saveTripInspection(){
        //String n = name.getText().toString();
       // String p = password.getText().toString();
        DBQuery dbQuery = new DBQuery(frmDispatchfromTerminal.this);
        dba = DBAccess.getInstance(frmDispatchfromTerminal.this);
//        String authenticate = dbQuery.getPassword(n);
//        if(authenticate.equals(p)){
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
            String dispatcher = dbQuery.getEmployeeID(name_inspector);
            String origin = direction;
            String lastticket = GlobalVariable.d_lastticketid;
            //check pcount and qty both 0
            sqlQuery = "INSERT INTO `TRIPINSPECTION` (TRIPID, DEVICENAME, DATETIMESTAMP, EMPLOYEEID, ATTRIBUTEID, QTY, KMPOST, PCOUNT, LINESEGMENT,DIRECTION, BCOUNT, TICKETID) " +
                    " VALUES ( '"+ trip +"', '" +
                    devicename + "', '" +
                    datetime + "', '" +
                    dispatcher+ "', '10' , '0', '"+
                    origin+ "','0','1','"+ direct +"','0', '" +
                    lastticket + "' " +
                    " ); ";
            Log.wtf("logged sql",sqlQuery);

            if (!dba.executeQuery(sqlQuery)) {
                Toast.makeText(frmDispatchfromTerminal.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            }else{
                Log.wtf("test","save tripinspecion");
                printDispatchTerminal();
            }


//        }else{
//            Toast.makeText(frmDispatchfromTerminal.this, "Wrong Password", Toast.LENGTH_SHORT).show();
//        }

    }

    private void printDispatchTerminal() {

        DBQuery dbQuery = new DBQuery(frmDispatchfromTerminal.this);


        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        String tripid = dbQuery.getLastTrip();
        String busname = GlobalVariable.getName_bus();
        String linename = GlobalVariable.getLine_name();
        //String dispatcher = dbQuery.nameDriver()

        String title = "DISPATCH FROM TERMINAL" + "\n";
        String date = "Date: " + dtstartTime + "\n";
        String Bus = "Bus: " +  dbQuery.getResourceName(tripid)+  "\n";
        String line = "Line: " +  dbQuery.getLineName(tripid) + "\n";
        String inspector = "Dispatcher: " + dbQuery.nameDriver(tripid, "4")  + "\n";
        String Driver = "Driver: " + dbQuery.nameDriver(tripid, "1")   + "\n";
        String Conductor = "Conductor : " + dbQuery.nameDriver(tripid, "2")  + "\n";
        // String inspector = "Dispatcher : " +   + "\n";
        //String Driver = "Driver : " +   + "\n";
        //String Conductor = "Conductor : " +   + "\n";
        String lastticket ="Passenger: "+count.getText().toString() ;
        Log.wtf("dispath from arrival",title + date + Bus + line + inspector+ Driver + Conductor +lastticket);
       callBluetooth(title + date + Bus + line + inspector+ Driver + Conductor +lastticket);


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
        if (mService != null)
            mService.stop();
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
                            Toast.makeText(frmDispatchfromTerminal.this, R.string.title_connected_to + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(frmDispatchfromTerminal.this, R.string.title_connecting, Toast.LENGTH_LONG).show();
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
                Intent serverIntent = new Intent(frmDispatchfromTerminal.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.action_bluetoothOff:
                mService.stop();
                return true;


        }


        return super.onOptionsItemSelected(item);
    }


}
