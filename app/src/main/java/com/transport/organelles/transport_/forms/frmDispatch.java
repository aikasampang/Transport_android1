package com.transport.organelles.transport_.forms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Date;
import android.support.v7.app.ActionBar;

/**
 * Created by Organelles on 6/13/2017.
 */

public class frmDispatch extends AppCompatActivity  {

    Button save;
    ListView list_dispatcher;
    private AutoCompleteTextView name, bus, driver, cond;
    Spinner line, direction, mode;
    String line_, direction_, mode_, line_name, name_, bus_, driver_, cond_;
    private String dtstartTime = "", dtendTime = "";
    String sqlQuery = "", sqlQuery2 = "";
    private DBAccess dba;
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
    LinearLayout linearLayout;
    Bundle bundle;
    String update = "";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    private static String bluetooth_name = "Qsprinter";
    View include;
    ImageView bluetooth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmdispatch);
        getSupportActionBar().show();
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        getSupportActionBar().setSubtitle(currentDateTimeString);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
        setObject();
        objectListener();
        //setTitleBar();
        // get action bar

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        // Initialize the BluetoothService to perform bluetooth connections
        mService = new BluetoothService(this, bluetoothHandler);


        Bundle b = getIntent().getExtras();
        bundle = b;
        update = b.getString("data");


    }

    private void setObject() {

        name = (AutoCompleteTextView) findViewById(R.id.d_name);
        bus = (AutoCompleteTextView) findViewById(R.id.d_bus);
        cond = (AutoCompleteTextView) findViewById(R.id.d_cond);
        driver = (AutoCompleteTextView) findViewById(R.id.d_driver);
        save = (Button) findViewById(R.id.d_ok);
        line = (Spinner) findViewById(R.id.spin_line);
        direction = (Spinner) findViewById(R.id.spin_direction);
        mode = (Spinner) findViewById(R.id.spin_mode);
//        include = findViewById(R.id.actionbar);
//        bluetooth = (ImageView) include.findViewById(R.id.bluetooth);
        final DBQuery db = new DBQuery(frmDispatch.this);

        String[] spinnerNames = db.getName(4);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(frmDispatch.this, android.R.layout.simple_spinner_item, spinnerNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name.setAdapter(spinnerAdapter);

        String[] spinnerDriver = db.getName(1);
        ArrayAdapter<String> spinnerAdapterDri = new ArrayAdapter<String>(frmDispatch.this, android.R.layout.simple_spinner_item, spinnerDriver);
        spinnerAdapterDri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driver.setAdapter(spinnerAdapterDri);

        String[] spinnerCond = db.getName(2);
        ArrayAdapter<String> spinnerAdapterCond = new ArrayAdapter<String>(frmDispatch.this, android.R.layout.simple_spinner_item, spinnerCond);
        spinnerAdapterCond.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cond.setAdapter(spinnerAdapterCond);

        String[] spinnerBus = db.getBus();
        ArrayAdapter<String> spinnerAdapterBus = new ArrayAdapter<String>(frmDispatch.this, android.R.layout.simple_spinner_item, spinnerBus);
        spinnerAdapterBus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bus.setAdapter(spinnerAdapterBus);


        String[] spinnerLists = db.getLine();
        ArrayAdapter<String> spinnerAdapterDis = new ArrayAdapter<String>(frmDispatch.this, android.R.layout.simple_spinner_item, spinnerLists);
        spinnerAdapterDis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        line.setAdapter(spinnerAdapterDis);
        line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                line_ = parent.getItemAtPosition(position).toString();
                line_name = parent.getItemAtPosition(position).toString();
                DBQuery dbQuery = new DBQuery(frmDispatch.this);
                String idfromName = dbQuery.getIDfromLine(line_);
                line_ = idfromName;
                Log.wtf("LINEID", idfromName);
                GlobalVariable.setLineid(line_);
                GlobalVariable.setLine_name(line_name);
                getDirection(idfromName);


                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] spinnerMode = db.getMode();
        ArrayAdapter<String> spinnerAdapterMode = new ArrayAdapter<String>(frmDispatch.this, android.R.layout.simple_spinner_item, spinnerMode);
        spinnerAdapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode.setAdapter(spinnerAdapterMode);
        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mode_ = parent.getItemAtPosition(position).toString();
                DBQuery dbQuery = new DBQuery(frmDispatch.this);
                mode_ = dbQuery.getIDfromMode(mode_);
                GlobalVariable.setModeid(mode_);
                GlobalVariable.setModeName(dbQuery.getModeName(mode_));

                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void objectListener() {
//
//        bluetooth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LayoutInflater inflater = getLayoutInflater();
//                View alertLayout = inflater.inflate(R.layout.modal_bluetooth, null);
//                final Button connect = (Button) alertLayout.findViewById(R.id.connect);
//                final Button disconnect = (Button) alertLayout.findViewById(R.id.disconnect);
//                AlertDialog.Builder alert = new AlertDialog.Builder(frmDispatch.this);
//
//                connect.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent serverIntent = new Intent(frmDispatch.this, DeviceListActivity.class);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //  Check that we're actually connected before trying anything
                if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                    Toast.makeText(frmDispatch.this, R.string.not_connected, Toast.LENGTH_LONG).show();
                    return;
                }



                if (name.getText().toString().equals("") || bus.getText().toString().equals("") || driver.getText().toString().equals("") || cond.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(frmDispatch.this);
                    builder.setTitle("");
                    builder.setMessage("Please complete the required fields.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                } else {

                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.password, null);
                    final EditText password = (EditText) alertLayout.findViewById(R.id.password);

                    AlertDialog.Builder alert = new AlertDialog.Builder(frmDispatch.this);
                    alert.setTitle("Password for Dispatcher");
                    alert.setView(alertLayout);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pass = password.getText().toString();
                            String namedis = name.getText().toString();
                            String rpass = password(pass, namedis);

                            if (pass.equals(rpass)) {
                                saveDispatch();
                               // printDispatch();
                                dialog.dismiss();
                                Intent intent = new Intent(frmDispatch.this, frmMain.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(frmDispatch.this, "Wrong Password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();


                    // saveDispatch();
                }
            }
        });

    }

//    private void setTitleBar() {
//        //Display login user
//        TextView txtUser = (TextView) findViewById(R.id.txtUser);
//        final TextView txtBattery = (TextView) findViewById(R.id.txtBattery);
//        txtDateTime = (TextView) findViewById(R.id.txtDateTime);
//        DBQuery dbQuery = new DBQuery(frmDispatch.this);
//
//        final String percent = "%";
//        final String batteryLevel = "";
//
//        assert txtUser != null;
//        String company = dbQuery.getCompanyName();
//        txtUser.setText(company);
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
//                                String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmDispatch.this));
//                                txtBattery.setText(batteryLevel + "%          ");
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

    private String password(String pass, String name) {
        DBQuery dbQuery = new DBQuery(frmDispatch.this);
        String n = dbQuery.getPassword(name);
        return n;
    }

    private void getDirection(String line) {
        final DBQuery db = new DBQuery(frmDispatch.this);
        String[] spinnerLists = db.getDirection(line);
        ArrayAdapter<String> spinnerAdapterDis = new ArrayAdapter<String>(frmDispatch.this, android.R.layout.simple_spinner_item, spinnerLists);
        spinnerAdapterDis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        direction.setAdapter(spinnerAdapterDis);
        direction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                direction_ = parent.getItemAtPosition(position).toString();
                GlobalVariable.d_direct = direction_;
                GlobalVariable.setDirection(direction_);
                Log.wtf("ID", direction_);
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void saveDispatch() {

        GlobalVariable.setName_bus(bus.getText().toString());
        GlobalVariable.setName_driver(driver.getText().toString());
        GlobalVariable.setName_conductor(cond.getText().toString());
        GlobalVariable.setName_dispatcher(name.getText().toString());




        DBQuery dbQuery = new DBQuery(frmDispatch.this);
        dba = DBAccess.getInstance(frmDispatch.this);
        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        String mode = mode_;
        String resource = dbQuery.getIDfromResource(bus.getText().toString());
        dbQuery.getlastTicket(GlobalVariable.getPhoneName());


        if (!update.equals("updatedispatch")) { //new dispatch
            int lasttrip = Integer.parseInt(dbQuery.getLastTrip());
            int lasttripid = lasttrip + 1;
            String devicename = GlobalVariable.getPhoneName();
            String lineID = GlobalVariable.getLineid();
            String resourceID = resource;
            String modeID = mode;
            String startDate = dtstartTime;
            String endDate = "";
            String tag = "";
            String remarks = "";

            sqlQuery = "INSERT INTO `TRIP` (ID, DEVICENAME, LINE ,RESOURCEID, MODEID, STARTDATETIMESTAMP, ENDDATETIMESTAMP, TAG, REMARKS) " +
                    " VALUES ( '" + lasttripid + "', '" +
                    devicename + "', '" +
                    lineID + "', '" +
                    resourceID + "', '" +
                    modeID + "', '" +
                    startDate + "',' " +
                    endDate + "',' " +
                    tag + "','" +
                    remarks + "'" +
                    " ); ";
            Log.wtf("logged sql", sqlQuery);

            if (!dba.executeQuery(sqlQuery)) {
                Toast.makeText(frmDispatch.this, "Can't insert data in Dispatch..", Toast.LENGTH_SHORT).show();
            } else {
                String last = String.valueOf(lasttripid);
                Log.wtf("lasttip", last);
                GlobalVariable.setLasttrip(last);
                updateDirection();
                updateLine();
                insertTripInspection();
                printDispatch();


            }
        } else {
            String devicename = GlobalVariable.getPhoneName();
            String lineID = GlobalVariable.getLineid();
            String resourceID = resource;
            String modeID = mode;
            String remarks = "";
            String id = dbQuery.getLastTrip();
            GlobalVariable.setLasttrip(id);


            String query = "Update TRIP set LINE='" + lineID + "' AND RESOURCEID ='" + resourceID + "' AND MODEID = '" + modeID + "' AND REMARKS='" + remarks + "' WHERE ID='" + id + "'";
            Log.wtf("logged sql update trip", query);
            if (!dba.executeQuery(query)) {
                Toast.makeText(frmDispatch.this, "Can't insert data in Dispatch..", Toast.LENGTH_SHORT).show();
            } else {
                updateDirection();
                updateLine();
                printDispatch();
            }
        }
    }

    private void updateDirection() {
        DBQuery dbQuery = new DBQuery(frmDispatch.this);
        dbQuery.getLineRemarks(GlobalVariable.getLineid());
        String direction = GlobalVariable.getD_remarks();
        String trip = GlobalVariable.getLasttrip();
        String devicename = GlobalVariable.getPhoneName();


        if (direction.toString().equals("SB")) {
            int N = 1;
            String d_north = String.valueOf(N);
            GlobalVariable.d_direction = d_north;
        } else {
            int S = -1;
            String d_south = String.valueOf(S);
            GlobalVariable.d_direction = d_south;
        }

        if (!update.equals("updatedispatch")) {

            String d = GlobalVariable.d_direction;
            sqlQuery = "UPDATE DEVICEDATA SET VALUE = '" + d + "' WHERE DEVICENAME = '" + devicename + "' AND KEY = 'DIRECTION'";

            if (!dba.executeQuery(sqlQuery)) {
                Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("update direction", "update direction");
            }

            sqlQuery2 = "UPDATE DEVICEDATA SET VALUE = '" + trip + "' WHERE DEVICENAME = '" + devicename + "' AND KEY = 'LASTTRIPID'";

            if (!dba.executeQuery(sqlQuery2)) {
                Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("update tripid", "update lasttripid");
            }

        } else {

            String d = GlobalVariable.d_direction;
            sqlQuery = "UPDATE DEVICEDATA SET VALUE = '" + d + "' WHERE DEVICENAME = '" + devicename + "' AND KEY = 'DIRECTION'";

            if (!dba.executeQuery(sqlQuery)) {
                Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("update direction", "update direction");
            }
        }
        insertCrew();

    }

    private void updateLine() {

        String devicename = GlobalVariable.getPhoneName();
        String line = GlobalVariable.getLineid();

        sqlQuery = "UPDATE DEVICEDATA SET VALUE = '" + line + "' WHERE DEVICENAME = '" + devicename + "' AND KEY = 'LINE'";

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("update line", "update line");
        }


    }

    private void insertCrew() {

        DBQuery dbQuery = new DBQuery(frmDispatch.this);
        String tripid = dbQuery.getLastTrip();
        String devicename = GlobalVariable.getPhoneName();
        String id_dispatcher = dbQuery.getEmployeeID(name.getText().toString());


        if (!update.equals("updatedispatch")) {


            sqlQuery = "INSERT INTO TRIPCREW VALUES('" + tripid + "', '" + devicename + "', '4','" + id_dispatcher + "') ";
            Log.wtf("logged sql", sqlQuery);
            if (!dba.executeQuery(sqlQuery)) {
                Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("dispatcher crew", "save dispatcher");
                String id_driver = dbQuery.getEmployeeID(driver.getText().toString());
                String sql = "INSERT INTO TRIPCREW VALUES('" + tripid + "', '" + devicename + "', '1','" + id_driver + "') ";
                if (!dba.executeQuery(sql)) {
                    Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.wtf("driver crew", "save driver");
                    String id_cond = dbQuery.getEmployeeID(cond.getText().toString());
                    String sql1 = "INSERT INTO TRIPCREW VALUES('" + tripid + "', '" + devicename + "', '2','" + id_cond + "') ";
                    if (!dba.executeQuery(sql1)) {
                        Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.wtf("conductor crew", "save conductor");
                    }
                }
            }

        } else {
            String trip = GlobalVariable.d_lasttripid;
            String query = "DELETE FROM TRIPCREW WHERE TRIPID = '" + trip + "'";
            if (!dba.executeQuery(query)) {
                Toast.makeText(frmDispatch.this, "Can't insert data to database!. DELETE TRIPCREW", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("conductor crew", "delete conductor");

                sqlQuery = "INSERT INTO TRIPCREW VALUES('" + tripid + "', '" + devicename + "', '4','" + id_dispatcher + "') ";
                Log.wtf("logged sql", sqlQuery);
                if (!dba.executeQuery(sqlQuery)) {
                    Toast.makeText(frmDispatch.this, "Can't insert data to database!. UPDATE DISPATCHER", Toast.LENGTH_SHORT).show();
                } else {
                    Log.wtf("dispatcher crew", "update dispatcher");
                    String id_driver = dbQuery.getEmployeeID(driver.getText().toString());
                    String sql = "INSERT INTO TRIPCREW VALUES('" + tripid + "', '" + devicename + "', '1','" + id_driver + "') ";
                    if (!dba.executeQuery(sql)) {
                        Toast.makeText(frmDispatch.this, "Can't insert data to database!. UPDATE DRIVER", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.wtf("driver crew", "update driver");
                        String id_cond = dbQuery.getEmployeeID(cond.getText().toString());
                        String sql1 = "INSERT INTO TRIPCREW VALUES('" + tripid + "', '" + devicename + "', '2','" + id_cond + "') ";
                        if (!dba.executeQuery(sql1)) {
                            Toast.makeText(frmDispatch.this, "Can't insert data to database!. UPDATE COND", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.wtf("conductor crew", "update conductor");
                        }
                    }
                }

            }
        }

    }

    private void insertTripInspection() {

        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        DBQuery dbQuery = new DBQuery(frmDispatch.this);


        String trip = dbQuery.getLastTrip();
        String devicename = GlobalVariable.getPhoneName();
        String datetime = dtstartTime;
        String dispatcher = dbQuery.getEmployeeID(name.getText().toString());
        String origin = dbQuery.getOrigin(line_);
        String lastticket = GlobalVariable.d_lastticketid;


        sqlQuery = "INSERT INTO `TRIPINSPECTION` (TRIPID, DEVICENAME, DATETIMESTAMP, EMPLOYEEID, ATTRIBUTEID, QTY, KMPOST, PCOUNT, LINESEGMENT, BCOUNT, TICKETID) " +
                " VALUES ( '" + trip + "', '" +
                devicename + "', '" +
                datetime + "', '" +
                dispatcher + "', '5' , '0', '" +
                origin + "','0','1','0',' " +
                lastticket + "' " +
                " ); ";
        Log.wtf("logged sql", sqlQuery);

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmDispatch.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("", "save tripinspecion");
        }
    }

    private void printDispatch()
    {
        dba = DBAccess.getInstance(frmDispatch.this);
        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        GlobalVariable.setName_bus(bus.getText().toString());
        DBQuery dbQuery = new DBQuery(frmDispatch.this);
        String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmDispatch.this));
        int l = Integer.parseInt(GlobalVariable.d_lastticketid);
        int t = 1;
        int nextticket = l + t;
        String format = String.format("%1$05d ", nextticket);//String.format("%07d", "0");
        String origin = dbQuery.getOriginName(line_);
        String devicename = GlobalVariable.getPhoneName() + "\n";
        String dispatch = "DISPATCH" + "\n \n ";
        String startDate = "Date:" + dtstartTime + "\n";
        String resourceID = "Bus:" + bus.getText().toString() + "\n";
        String lineID = "Line:" + line_name.toString() + "\n";
        GlobalVariable.setLine_name(line_name.toString());
        String dispatcher = "Dispatcher:" + name.getText().toString() + "\n";
        String dri = "Driver:" + driver.getText().toString() + "\n";
        String conductor = "Conductor:" + cond.getText().toString() + "\n";
        String opening = "Opening:" + format + "\n"; //
        String terminal = "Terminal: " + origin + "\n"; //get origin
        String battery = "Battery:" + batteryLevel + "%" + "\n"; //

        Log.wtf("toPrint", devicename + " " + dispatch + " " + startDate + " " + resourceID + " " + lineID + " " + dispatcher + " " + dri + " " + conductor + " " + opening + " " + terminal + battery);

        callBluetooth(devicename + " " + dispatch + " " + startDate + " " + resourceID + " " + lineID + " " + dispatcher + " " + dri + " " + conductor + " " + opening + " " + terminal + battery);

        GlobalVariable.setName_driver(driver.getText().toString());
        GlobalVariable.setName_conductor(cond.getText().toString());
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        mService = new BluetoothService(this, bluetoothHandler);
//    }

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
                            Toast.makeText(frmDispatch.this, R.string.title_connected_to + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(frmDispatch.this, R.string.title_connecting, Toast.LENGTH_LONG).show();
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
                Intent serverIntent = new Intent(frmDispatch.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.action_bluetoothOff:
                mService.stop();
                return true;


        }


        return super.onOptionsItemSelected(item);
    }
}
