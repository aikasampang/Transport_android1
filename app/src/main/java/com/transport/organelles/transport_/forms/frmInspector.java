package com.transport.organelles.transport_.forms;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.transport.organelles.transport_.classforms.BluetoothService;
import com.transport.organelles.transport_.classforms.DBAccess;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.DeviceListActivity;
import com.transport.organelles.transport_.classforms.GlobalClass;
import com.transport.organelles.transport_.classforms.GlobalVariable;
import com.transport.organelles.transport_.R;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Organelles on 6/14/2017.
 */

public class frmInspector extends AppCompatActivity {

    TextView txtDateTime, pax_count;
    EditText kmpost, pax_acount;
    Button save;
    String sqlQuery, i_name, i_password, name_inspector, attributeid , segment;
    String dtstartTime = "", lastreversedate;
    private DBAccess dba;
    ImageView bluetooth;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    LinearLayout linearLayout;
    Bundle bundle;
    String name_type, type;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    private static String bluetooth_name = "Qsprinter";
    private static final boolean D = true;
    private static final String TAG = "BTPrinter";
    private String mConnectedDeviceName = null;
    private BluetoothService mService = null;
    private List<String> list;
    private List<String> listTicket;
    private List<String> tickets;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frminspector);
        getSupportActionBar().show();
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        getSupportActionBar().setSubtitle(currentDateTimeString);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      //  setTitleBar();
        setObject();
        objectListener();

        bundle= getIntent().getExtras();
        name_type = bundle.getString("name_type");
        type = bundle.getString("type");
        Log.wtf("type", name_type + type);

        employeeLogin();

    //if inspector attribute id is 2 else 1


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

        pax_count = (TextView)findViewById(R.id.i_pcount);
        pax_acount = (EditText) findViewById(R.id.i_acount);
        kmpost = (EditText)findViewById(R.id.i_km);
        save = (Button) findViewById(R.id.i_save);
//        bluetooth = (ImageView)findViewById(R.id.bluetooth);

    }
    private void objectListener(){


//        bluetooth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LayoutInflater inflater = getLayoutInflater();
//                View alertLayout = inflater.inflate(R.layout.modal_bluetooth, null);
//                final Button connect = (Button) alertLayout.findViewById(R.id.connect);
//                final Button disconnect = (Button) alertLayout.findViewById(R.id.disconnect);
//                AlertDialog.Builder alert = new AlertDialog.Builder(frmInspector.this);
//
//                connect.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent serverIntent = new Intent(frmInspector.this, DeviceListActivity.class);
//                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
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
//            }
//        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mService.getState() != BluetoothService.STATE_CONNECTED) {
//                    Toast.makeText(frmInspector.this, R.string.not_connected, Toast.LENGTH_LONG).show();
//                    return;
//                }

                saveInspector();
            }
        });

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
                String trip = dbQuery.getLastTrip();
                String k = kmpost.getText().toString();
                String c = dbQuery.getRemainingPaxControl(trip, k).toString();
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
        //final AutoCompleteTextView name = (AutoCompleteTextView) alertLayout.findViewById(R.id.name);
        final EditText password = (EditText) alertLayout.findViewById(R.id.password);

//        String[] spinnerNames = dbQuery.getName(Integer.parseInt(type));
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmInspector.this,android.R.layout.simple_spinner_item, spinnerNames);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        name.setAdapter(spinnerAdapter);
        AlertDialog.Builder alert = new AlertDialog.Builder(frmInspector.this);
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
                    Toast.makeText(frmInspector.this, "Please input the right password for this User..", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (frmInspector.this, frmMain.class);
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
    private void saveInspector(){

        dba = DBAccess.getInstance(frmInspector.this);
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        DBQuery dbQuery = new DBQuery(frmInspector.this);


        String trip = dbQuery.getLastTrip();
        String devicename = GlobalVariable.getPhoneName();
        String datetime = dtstartTime;
        String dispatcher = dbQuery.getEmployeeID(name_inspector);
        String origin = kmpost.getText().toString();
        String lastticket = dbQuery.getLastTicket();
        String direction = dbQuery.getDirectionfromDB();
        String actual_count = pax_acount.getText().toString();
        String pass_count = pax_count.getText().toString();
        int segment = dbQuery.getSegmentNum(trip);

        if(type.equals("1")){
            attributeid = "1";
        }else{
            attributeid = "2";
        }

        sqlQuery = "INSERT INTO `TRIPINSPECTION` (TRIPID, DEVICENAME, DATETIMESTAMP, EMPLOYEEID, ATTRIBUTEID, QTY, KMPOST, PCOUNT, LINESEGMENT, DIRECTION, BCOUNT, TICKETID) " +
                " VALUES ( '"+ trip +"', '" +
                devicename + "', '" +
                datetime + "', '" +
                dispatcher+ "','" +
                attributeid + "','" +
                actual_count+ "','" +
                origin + "','" +
                pass_count + "','" +
                segment + "','" +
                direction + "','" +
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
            Log.wtf("SAVING.. ","save tripinspecion");

            if(type.equals("1")){

                printInspector();
            }else{
                printController();
            }
        }


    }


    private void printInspector(){

        DBQuery dbQuery = new DBQuery(frmInspector.this);

        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);

        String check = dbQuery.getCheckpoint(kmpost.getText().toString(), GlobalVariable.getLineid());
        lastreversedate = dbQuery.getreversedate(dbQuery.getLastTrip());
        String direct = dbQuery.getDirectionfromDB();
        list = dbQuery.getPassengerTicket(direct, dbQuery.getLastTrip(), lastreversedate, kmpost.getText().toString());
        listTicket = dbQuery.getlistTicket(direct, dbQuery.getLastTrip(), lastreversedate, kmpost.getText().toString());

        String tripId = dbQuery.getLastTrip();
        String device = GlobalVariable.getPhoneName();
        String Name = dbQuery.getCompanyName(device) + "\n";
        String title = "Inspector Report" + " \n";
            String ticketCount = dbQuery.getLastTicket(device);
        String deviceticket = device + ticketCount + "\n";
        String date = "Date : " + dtstartTime + "\n";
        String vehicle =  "Vehicle: " + dbQuery.getResourceName(tripId)+ "\n";
        String driver = "Driver: " + dbQuery.nameDriver(tripId, "1") + "\n";
        String cond = "Conductor: " + dbQuery.nameDriver(tripId, "2") + "\n";
        String inspector = "Inspector:" + name_inspector+ "\n";
        String route = "ROUTE: " + dbQuery.getLineName(tripId)+ "\n";
        String Mode = "MODE: " + dbQuery.getModeNameTrip(tripId)+ "\n";
        String checkpoint = "Checkpoint: " + check + "\n";
        String direction = "Direction: " + dbQuery.getDirectionfromDB() + "\n";
                int l = Integer.parseInt(dbQuery.getLastTicket());
          //      int t = 1;
             //   int nextticket = l+t;
        String format = String.format("%1$05d ", l);//String.format("%07d", "0");
              //  String datereverse = dbQuery.getDateReverse(kmpost.getText().toString(), tripId);
             //   String cpassengers = dbQuery.getPassengersNorth(tripId, datereverse ,kmpost.getText().toString() );
        //String pass = "Passengers :" + cpassengers + "\n";
        String inspect = "Inspection: " + kmpost.getText().toString() +"\n";
        String batteryLevel = "Battery Level : " +String.format("%6.0f", GlobalClass.getBatteryLevel(frmInspector.this)) + "%" + "\n";
        String passengerCount = list.get(0)+": " + list.get(1) + "\n";







       // String tickets = Arrays.toString(listTicket.toArray()) + "\n";
//        int chunk = 25; // chunk size to divide
//        for(int i=0;i<listTicket.;i+=chunk){
//            System.out.println(Arrays.toString(Arrays.copyOfRange(original, i, Math.min(original.length,i+chunk))));
//        }



        Log.wtf("To print", Name + " " + title + " " + deviceticket+ " " + date  + " " +vehicle + " " + driver + " " + cond + " " +
                inspector + " " + route + " " + Mode + " " + checkpoint + " " + direction+ " "  + passengerCount +
                " " + inspect + " " + batteryLevel + " " + tickets);

                callBluetooth(Name + " " + title + " " + deviceticket+ " " + date  + " " +vehicle + " " + driver + " " + cond + " " +
                inspector + " " + route + " " + Mode + " " + checkpoint + " " + direction+ " "  + passengerCount +
                " " + inspect + " " + batteryLevel );

        for (int start =0; start < listTicket.size(); start+= 5){
            int end = Math.min(start + 5 , listTicket.size());
            tickets = listTicket.subList(start, end) ;
            Log.wtf("listticket",
                    tickets + "\n");
            callBluetooth(tickets + "\n");
        }






//        String[] array = tickets.split("(\\d+,\\d+,\\d+)");
//        String[] items = tickets.split(",");
//        for (String item : array)
//        {
//            System.out.println("item = " + item);
//        }
//
//
//        String[] parts= tickets.split(",");
//        String[] fiveinput = Arrays.copyOf(parts, 5);
//        for(String entry:fiveinput){
//
//            System.out.println("tickets : "+entry + "\n");
//        }


        // callBluetooth(tickets);





    }

    private void printController(){

        DBQuery dbQuery = new DBQuery(frmInspector.this);

        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        lastreversedate = dbQuery.getreversedate(dbQuery.getLastTrip());
        String direct = dbQuery.getDirectionfromDB();
        list = dbQuery.getPassengerTicket(direct, dbQuery.getLastTrip(), lastreversedate, kmpost.getText().toString());
        listTicket = dbQuery.getlistTicket(direct, dbQuery.getLastTrip(), lastreversedate, kmpost.getText().toString());


        String check = dbQuery.getCheckpoint(kmpost.getText().toString(), GlobalVariable.getLineid());
        String tripId = dbQuery.getLastTrip();
        String device = GlobalVariable.getPhoneName();
        String Name = dbQuery.getCompanyName(device) + "\n";
        String title = "Controller Report" + " \n";
        String ticketCount = dbQuery.getLastTicket(device);
        String deviceticket = device + ticketCount + "\n";
        String date = "Date : " + dtstartTime + "\n";
        String vehicle =  "Vehicle: " + dbQuery.getResourceName(tripId)+ "\n";
        String driver = "Driver: " + dbQuery.nameDriver(tripId, "1") + "\n";
        String cond = "Conductor: " + dbQuery.nameDriver(tripId, "2") + "\n";
        String inspector = "Inspector:" + name_inspector+ "\n";
        String route = "ROUTE: " + dbQuery.getLineName(tripId)+ "\n";
        String Mode = "MODE: " + dbQuery.getModeNameTrip(tripId)+ "\n";
        String checkpoint = "Checkpoint: " + check + "\n";
        String direction = "Direction: " + dbQuery.getDirectionfromDB() + "\n";
        int seg = dbQuery.getTripCount(tripId);

        if(seg == 1){
            segment = "1st Trip";
        }else if( seg == 2 ){
            segment = "2nd Trip";
        }else if( seg == 3){
            segment = "3rd Trip";
        }else {
            segment = seg + "th Trip";
        }
        String segmentnum = "Segment No: " + segment + "\n";
        String cash = "Cash: " + "Php"+  dbQuery.getCash(dbQuery.getLastTrip(), lastreversedate, kmpost.getText().toString(), dbQuery.getDirectionfromDB()) + ".00 "+ "\n";

        int l = Integer.parseInt(dbQuery.getLastTicket());
        //      int t = 1;
        //   int nextticket = l+t;
        String lastticket = "Last ticket:  "+String.format("%1$05d ", l) + "\n";//String.format("%07d", "0");
        String passengerCount = list.get(0)+": " + list.get(1) + "\n";
        String inspect = "Control Count: " + pax_acount.getText().toString() +"\n";
        String batteryLevel = "Battery Level: " + String.format("%6.0f", GlobalClass.getBatteryLevel(frmInspector.this)) + "%";

        Log.wtf("To print", Name + " " + title + " " + deviceticket+ " " + date  + " " +vehicle + " " + driver + " " + cond + " " +
        inspector + " " + route + " " + Mode + " " + checkpoint + " " + direction + " " + segmentnum + " " + cash + " " + lastticket +
        " "  + passengerCount + " " + inspect + " " + batteryLevel );

        callBluetooth(Name + " " + title + " " + deviceticket+ " " + date  + " " +vehicle + " " + driver + " " + cond + " " +
                inspector + " " + route + " " + Mode + " " + checkpoint + " " + direction + " " + segmentnum + " " + cash + " " + lastticket +
                " "  + passengerCount + " " + inspect + " " + batteryLevel);

        Intent intent = new Intent (frmInspector.this, frmMain.class);
        startActivity(intent);
        finish();

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
                            Toast.makeText(frmInspector.this, R.string.title_connected_to + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(frmInspector.this, R.string.title_connecting, Toast.LENGTH_LONG).show();
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
                Intent serverIntent = new Intent(frmInspector.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.action_bluetoothOff:
                mService.stop();
                return true;


        }


        return super.onOptionsItemSelected(item);
    }


}
