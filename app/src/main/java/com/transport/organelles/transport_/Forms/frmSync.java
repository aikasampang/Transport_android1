package com.transport.organelles.transport_.forms;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.transport.organelles.transport_.classforms.DBAccess;
import com.transport.organelles.transport_.classforms.GlobalVariable;
import com.transport.organelles.transport_.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Organelles on 6/8/2017.
 */

public class frmSync extends AppCompatActivity {

    RequestQueue requestQueue;
    String sqlQuery;
    String  url_selectEmployee, url_selectControlmatrix, url_selectEmployeeMatrix, url_selectHotspot, url_selectResource, url_selectResourcemodel, url_selectServiceMatrix,
            url_insertDevicedata, url_insertTripcost, url_insertTripcrew, url_insertTripinspection, url_insertTriplog, url_insertTripreceipt,
            url_insertTripreverse, url_insertTripusage, url_insertTripwithholding, url_insertTicket, url_insertTrip;
    Cursor c;
    DBAccess dba;
    ConnectivityManager connManager;
    WifiInfo info;
    WifiManager wifiManager;
    NetworkInfo wifiStat;
    TextView txtWifiStatus, txtWifiSSID, txtProcesses;
    Button btnSync;
    String  dataSync;
    boolean noError = false;
    int pendingRequest;
    Handler mHandler;
    int transactCurrent, transactOverAll;
    int ticketCurrent, ticketOverAll;
    int loginCurrent, loginOverAll;
    String verTransact, verTransactOverAll, verTicket, verTicketOverAll, verLogin, verLoginOverAll;
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView syncList_Status;
    private static final boolean D = true;
    private static final String TAG = "BTPrinter";
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    //private BluetoothService mService = null;

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static int transaction = 0, ticketsales = 0 , logintrans = 0, deviceprop = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmsync);


        getObject();
        connectWifi();
        setTitleBar();
        Sync();



    }


    private void getObject(){

        btnSync= (Button) findViewById(R.id.btnSync);
        GlobalVariable.serverIP = "192.168.11.30:8080";

        url_selectEmployee = "http://" + GlobalVariable.serverIP + "/transport/selectEmployee.php";
        url_selectControlmatrix = "http://" + GlobalVariable.serverIP + "/transport/selectControlmatrix.php";
        url_selectEmployeeMatrix = "http://" + GlobalVariable.serverIP + "/transport/selectEmployeematrix.php";
        url_selectHotspot = "http://" + GlobalVariable.serverIP + "/transport/selectHotspot.php";
        url_selectResource = "http://" + GlobalVariable.serverIP + "/transport/selectResource.php";
        url_selectResourcemodel = "http://" + GlobalVariable.serverIP + "/transport/selectResourcemodel.php";
        url_selectServiceMatrix = "http://" + GlobalVariable.serverIP + "/transport/selectServicematrix.php";




        url_insertDevicedata = "http://" + GlobalVariable.serverIP + "/transport/insertDevicedata.php";
        url_insertTripcost = "http://" + GlobalVariable.serverIP + "/transport/insertTripcost.php";
        url_insertTripcrew = "http://" + GlobalVariable.serverIP + "/transport/insertTripcrew.php";
        url_insertTripinspection = "http://" + GlobalVariable.serverIP + "/transport/insertTripinspection.php";
        url_insertTriplog = "http://" + GlobalVariable.serverIP + "/transport/insertTriplog.php";
        url_insertTripreceipt = "http://" + GlobalVariable.serverIP + "/transport/insertTripreceipt.php";
        url_insertTripreverse = "http://" + GlobalVariable.serverIP + "/transport/insertTripreverse.php";
        url_insertTripusage = "http://" + GlobalVariable.serverIP + "/transport/insertTripusage.php";
        url_insertTripwithholding = "http://" + GlobalVariable.serverIP + "/transport/insertTripwithholding.php";
        url_insertTicket = "http://" + GlobalVariable.serverIP + "/transport/insertTicket.php";

        requestQueue = Volley.newRequestQueue(frmSync.this);

    }


    private void setTitleBar(){

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                wifiStat = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                                info = wifiManager.getConnectionInfo ();

                            if (wifiStat.isConnected()) {
                                Toast.makeText(frmSync.this, "Connected", Toast.LENGTH_LONG).show();
//                                txtWifiStatus.setText("Connected");
//                                txtWifiSSID.setText(info.getBSSID() + ":" + info.toString() );
//                                txtWifiSSID.setText(info.getSSID());
                            }
                            else {
                                Toast.makeText(frmSync.this, "Not Connected", Toast.LENGTH_LONG).show();
//                                txtWifiStatus.setText("Disconnected");
//                                txtWifiSSID.setText("No Data");
                            }

                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        t.start();
    }

    private void Sync(){

       btnSync.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //insert
               //verifyEmployee();


                //get data
               insert_Devicedata();
               insert_Tripcost();
               insert_Tripcrew();
               insert_Tripinspection();
               insert_Triplog();
               insert_Tripreceipt();
               insert_Tripreverse();
               insert_Tripusage();
               insert_Tripwithholding();
               insert_Ticket();
               insert_trip();

               verifyEmployee();
               verifyControlMatrix();
               verifyEmployeematrix();
               verifyHotspot();
               verifyResource();
               verifyResourcemodel();
               verifyServicematrix();


           }
       });
    }
    private void updateListView() {
        listItems.clear();
//        listItems.add("Pushing Transaction " + String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll));
//        listItems.add("Pushing TicketSales " + String.valueOf(ticketCurrent) + "/" + String.valueOf(ticketOverAll));
//        listItems.add("Pushing LoginTransaction " + String.valueOf(loginCurrent) + "/" + String.valueOf(loginOverAll));
        listItems.add("Sync Device Data " + verTransact + "/" + verTransactOverAll);
        listItems.add("Sync TicketSales " + verTicket + "/" + verTicketOverAll);
        listItems.add("Sync LoginTransaction " + verLogin + "/" + verLoginOverAll);
        syncList_Status.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    //get in database
    private void verifyEmployee(){
        StringRequest stringRequest = new StringRequest(url_selectEmployee,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON_Employee(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        requestQueue.add(stringRequest);

    }
    private void parseJSON_Employee(String json){

        JSONObject jsonObject = null;
        JSONArray results = null;
        ContentValues cv;

        String[] ID, NAME, TAG, REMARKS,EMPLOYEEROLEID,PIN,EMPLOYEETYPEID, LOANTAG,RESIGNTAG;


        String query;
        dba = DBAccess.getInstance(frmSync.this);

        try {
            jsonObject = new JSONObject(json);
            results = jsonObject.getJSONArray("JSONresults");

            //ID = new String[results.length()];
            NAME = new String[results.length()];
            TAG = new String[results.length()];
            REMARKS = new String[results.length()];
            EMPLOYEEROLEID = new String[results.length()];
            PIN = new String[results.length()];
            EMPLOYEETYPEID= new String[results.length()];
            LOANTAG = new String[results.length()];
            RESIGNTAG = new String[results.length()];

            //Delete all data in EmployeeRole table
            if (results.length() > 0) {

                //DELETE data
                query = "DELETE FROM Employee";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Deleting Employee");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }

                //RESEED primary key
                query = " DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Employee' ";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Reseed Employee");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<results.length();i++){

                JSONObject jo = results.getJSONObject(i);

               // ID[i] = jo.getString("ID");
                NAME[i] = jo.getString("NAME");
                TAG[i] = jo.getString("TAG");
                REMARKS[i] = jo.getString("REMARKS");
                EMPLOYEEROLEID[i] = jo.getString("EMPLOYEEROLEID");
                PIN[i] = jo.getString("PIN");
                EMPLOYEETYPEID[i] = jo.getString("EMPLOYEETYPEID");
                LOANTAG[i] = jo.getString("LOANTAG");
                RESIGNTAG[i] = jo.getString("RESIGNTAG");

                // Delete spaces
                //deviceID[i] = deviceID[i].replace(" ", "");
                //deviceName[i] = deviceName[i].replace(" ", "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(NAME[i], "null") || Objects.equals(NAME[i], "NULL") || NAME[i] == null) {
                        NAME[i] = null;
                    }
                    if (Objects.equals(TAG[i], "null") || Objects.equals(TAG[i], "NULL") || TAG[i] == null) {
                        TAG[i] = null;
                    }
                    if (Objects.equals(REMARKS[i], "null") || Objects.equals(REMARKS[i], "NULL") || REMARKS[i] == null) {
                        REMARKS[i] = null;
                    }
                    if (Objects.equals(REMARKS[i], "null") || Objects.equals(REMARKS[i], "NULL") || REMARKS[i] == null) {
                        REMARKS[i] = null;
                    }
                    if (Objects.equals(EMPLOYEEROLEID[i], "null") || Objects.equals(EMPLOYEEROLEID[i], "NULL") || EMPLOYEEROLEID[i] == null) {
                        EMPLOYEEROLEID[i] = null;
                    }
                    if (Objects.equals(PIN[i], "null") || Objects.equals(PIN[i], "NULL") || PIN[i] == null) {
                        PIN[i] = null;
                    }
                    if (Objects.equals(EMPLOYEETYPEID[i], "null") || Objects.equals(EMPLOYEETYPEID[i], "NULL") || EMPLOYEETYPEID[i] == null) {
                        EMPLOYEETYPEID[i] = null;
                    }
                    if (Objects.equals(LOANTAG[i], "null") || Objects.equals(LOANTAG[i], "NULL") || LOANTAG[i] == null) {
                        LOANTAG[i] = null;
                    }
                    if (Objects.equals(RESIGNTAG[i], "null") || Objects.equals(RESIGNTAG[i], "NULL") || RESIGNTAG[i] == null) {
                        RESIGNTAG[i] = null;
                    }
                }

                //INSERT to database
                cv = new ContentValues();
                //cv.put("ID", ID[i]);
                cv.put("NAME", NAME[i]);
                cv.put("TAG", TAG[i]);
                cv.put("REMARKS", REMARKS[i]);
                cv.put("EMPLOYEEROLEID", EMPLOYEEROLEID[i]);
                cv.put("PIN", PIN[i]);
                cv.put("EMPLOYEETYPEID", EMPLOYEETYPEID[i]);
                cv.put("LOANTAG", LOANTAG[i]);
                cv.put("RESIGNTAG", RESIGNTAG[i]);

                if(dba.insertDB("EMPLOYEE",cv)) {
                    Toast.makeText(frmSync.this,"insert!", Toast.LENGTH_LONG).show();
                    Log.d("Response", "Error Inserting Employee");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyControlMatrix(){
        StringRequest stringRequest = new StringRequest(url_selectControlmatrix,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON_ControlMatrix(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        requestQueue.add(stringRequest);

    }
    private void parseJSON_ControlMatrix(String json){

        JSONObject jsonObject = null;
        JSONArray results = null;
        ContentValues cv;

        String[] LINEID, DIRECTION, FROMKM, TOKM, PENALTYAMOUNT, OPERATION, COSTTYPEID;


        String query;
        dba = DBAccess.getInstance(frmSync.this);

        try {
            jsonObject = new JSONObject(json);
            results = jsonObject.getJSONArray("JSONresults");

            //ID = new String[results.length()];
            LINEID = new String[results.length()];
            DIRECTION = new String[results.length()];
            FROMKM = new String[results.length()];
            TOKM = new String[results.length()];
            PENALTYAMOUNT = new String[results.length()];
            OPERATION = new String[results.length()];
            COSTTYPEID = new String[results.length()];


            //Delete all data in EmployeeRole table
            if (results.length() > 0) {

                //DELETE data
                query = "DELETE FROM CONTROLMATRIX";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Deleting CONTROLMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }

                //RESEED primary key
                query = " DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'CONTROLMATRIX' ";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Reseed CONTROLMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<results.length();i++){

                JSONObject jo = results.getJSONObject(i);

                // ID[i] = jo.getString("ID");
                LINEID[i] = jo.getString("LINEID");
                DIRECTION[i] = jo.getString("DIRECTION");
                FROMKM[i] = jo.getString("FROMKM");
                TOKM[i] = jo.getString("TOKM");
                PENALTYAMOUNT[i] = jo.getString("PENALTYAMOUNT");
                OPERATION[i] = jo.getString("OPERATION");
                COSTTYPEID[i] = jo.getString("COSTTYPEID");


                // Delete spaces
                //deviceID[i] = deviceID[i].replace(" ", "");
                //deviceName[i] = deviceName[i].replace(" ", "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(LINEID[i], "null") || Objects.equals(LINEID[i], "NULL") || LINEID[i] == null) {
                        LINEID[i] = null;
                    }
                    if (Objects.equals(DIRECTION[i], "null") || Objects.equals(DIRECTION[i], "NULL") || DIRECTION[i] == null) {
                        DIRECTION[i] = null;
                    }
                    if (Objects.equals(FROMKM[i], "null") || Objects.equals(FROMKM[i], "NULL") || FROMKM[i] == null) {
                        FROMKM[i] = null;
                    }
                    if (Objects.equals(TOKM[i], "null") || Objects.equals(TOKM[i], "NULL") || TOKM[i] == null) {
                        TOKM[i] = null;
                    }
                    if (Objects.equals(PENALTYAMOUNT[i], "null") || Objects.equals(PENALTYAMOUNT[i], "NULL") || PENALTYAMOUNT[i] == null) {
                        PENALTYAMOUNT[i] = null;
                    }
                    if (Objects.equals(OPERATION[i], "null") || Objects.equals(OPERATION[i], "NULL") || OPERATION[i] == null) {
                        OPERATION[i] = null;
                    }
                    if (Objects.equals(COSTTYPEID[i], "null") || Objects.equals(COSTTYPEID[i], "NULL") || COSTTYPEID[i] == null) {
                        COSTTYPEID[i] = null;
                    }
                }

                //INSERT to database
                cv = new ContentValues();
                //cv.put("ID", ID[i]);
                cv.put("LINEID", LINEID[i]);
                cv.put("DIRECTION", DIRECTION[i]);
                cv.put("FROMKM", FROMKM[i]);
                cv.put("TOKM", TOKM[i]);
                cv.put("PENALTYAMOUNT", PENALTYAMOUNT[i]);
                cv.put("OPERATION", OPERATION[i]);
                cv.put("COSTTYPEID", COSTTYPEID[i]);


                if(dba.insertDB("CONTROLMATRIX",cv)) {
                    Toast.makeText(frmSync.this,"insert!", Toast.LENGTH_LONG).show();
                    Log.d("Response", "Error Inserting CONTROLMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyEmployeematrix(){
        StringRequest stringRequest = new StringRequest(url_selectEmployeeMatrix,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON_EmployeeMatrix(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        requestQueue.add(stringRequest);

    }
    private void parseJSON_EmployeeMatrix(String json){

        JSONObject jsonObject = null;
        JSONArray results = null;
        ContentValues cv;

        String[] LINEID, MODEID, RESOURCEMODELID, RESOURCEID, EMPLOYEEROLEID, EMPLOYEETYPEID, EMPLOYEEID, OPERATIONTYPEID, AMOUNT, TAG, REMARKS;


        String query;
        dba = DBAccess.getInstance(frmSync.this);

        try {
            jsonObject = new JSONObject(json);
            results = jsonObject.getJSONArray("JSONresults");

            //ID = new String[results.length()];
            LINEID = new String[results.length()];
            MODEID= new String[results.length()];
            RESOURCEMODELID = new String[results.length()];
            RESOURCEID = new String[results.length()];
            EMPLOYEEROLEID = new String[results.length()];
            EMPLOYEETYPEID = new String[results.length()];
            EMPLOYEEID = new String[results.length()];
            OPERATIONTYPEID = new String[results.length()];
            AMOUNT = new String[results.length()];
            TAG = new String[results.length()];
            REMARKS = new String[results.length()];


            //Delete all data in EmployeeRole table
            if (results.length() > 0) {

                //DELETE data
                query = "DELETE FROM EMPLOYEEMATRIX";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Deleting EMPLOYEEMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }

                //RESEED primary key
                query = " DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'EMPLOYEEMATRIX' ";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Reseed EMPLOYEEMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<results.length();i++){

                JSONObject jo = results.getJSONObject(i);

                // ID[i] = jo.getString("ID");
                LINEID[i] = jo.getString("LINEID");
                MODEID[i] = jo.getString("MODEID");
                RESOURCEMODELID[i] = jo.getString("RESOURCEMODELID");
                RESOURCEID[i] = jo.getString("RESOURCEID");
                EMPLOYEEROLEID[i] = jo.getString("EMPLOYEEROLEID");
                EMPLOYEETYPEID[i] = jo.getString("EMPLOYEETYPEID");
                EMPLOYEEID[i] = jo.getString("EMPLOYEEID");
                OPERATIONTYPEID[i] = jo.getString("OPERATIONTYPEID");
                AMOUNT[i] = jo.getString("AMOUNT");
                TAG[i] = jo.getString("TAG");
                REMARKS[i] = jo.getString("REMARKS");


                // Delete spaces
                //deviceID[i] = deviceID[i].replace(" ", "");
                //deviceName[i] = deviceName[i].replace(" ", "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(LINEID[i], "null") || Objects.equals(LINEID[i], "NULL") || LINEID[i] == null) {
                        LINEID[i] = null;
                    }
                    if (Objects.equals(MODEID[i], "null") || Objects.equals(MODEID[i], "NULL") || MODEID[i] == null) {
                        MODEID[i] = null;
                    }
                    if (Objects.equals(RESOURCEMODELID[i], "null") || Objects.equals(RESOURCEMODELID[i], "NULL") || RESOURCEMODELID[i] == null) {
                        RESOURCEMODELID[i] = null;
                    }
                    if (Objects.equals(RESOURCEID[i], "null") || Objects.equals(RESOURCEID[i], "NULL") || RESOURCEID[i] == null) {
                        RESOURCEID[i] = null;
                    }
                    if (Objects.equals(EMPLOYEEROLEID[i], "null") || Objects.equals(EMPLOYEEROLEID[i], "NULL") || EMPLOYEEROLEID[i] == null) {
                        EMPLOYEEROLEID[i] = null;
                    }
                    if (Objects.equals(EMPLOYEETYPEID[i], "null") || Objects.equals(EMPLOYEETYPEID[i], "NULL") || EMPLOYEETYPEID[i] == null) {
                        EMPLOYEETYPEID[i] = null;
                    }
                    if (Objects.equals(EMPLOYEEID[i], "null") || Objects.equals(EMPLOYEEID[i], "NULL") || EMPLOYEEID[i] == null) {
                        EMPLOYEEID[i] = null;
                    }
                    if (Objects.equals(OPERATIONTYPEID[i], "null") || Objects.equals(OPERATIONTYPEID[i], "NULL") || OPERATIONTYPEID[i] == null) {
                        OPERATIONTYPEID[i] = null;
                    }
                    if (Objects.equals(AMOUNT[i], "null") || Objects.equals(AMOUNT[i], "NULL") || AMOUNT[i] == null) {
                        AMOUNT[i] = null;
                    }
                    if (Objects.equals(TAG[i], "null") || Objects.equals(TAG[i], "NULL") || TAG[i] == null) {
                        TAG[i] = null;
                    }
                    if (Objects.equals(REMARKS[i], "null") || Objects.equals(REMARKS[i], "NULL") || REMARKS[i] == null) {
                        REMARKS[i] = null;
                    }

                }

                //INSERT to database
                cv = new ContentValues();
                //cv.put("ID", ID[i]);
                cv.put("LINEID", LINEID[i]);
                cv.put("MODEID", MODEID[i]);
                cv.put("RESOURCEMODELID", RESOURCEMODELID[i]);
                cv.put("RESOURCEID", RESOURCEID[i]);
                cv.put("EMPLOYEEROLEID", EMPLOYEEROLEID[i]);
                cv.put("EMPLOYEETYPEID", EMPLOYEETYPEID[i]);
                cv.put("EMPLOYEEID", EMPLOYEEID[i]);
                cv.put("OPERATIONTYPEID", OPERATIONTYPEID[i]);
                cv.put("AMOUNT", AMOUNT[i]);
                cv.put("TAG", AMOUNT[i]);
                cv.put("REMARKS", REMARKS[i]);



                if(dba.insertDB("EMPLOYEEMATRIX",cv)) {
                    Toast.makeText(frmSync.this,"insert!", Toast.LENGTH_LONG).show();
                    Log.d("Response", "Error Inserting EmployeeMatrix");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyHotspot(){
        StringRequest stringRequest = new StringRequest(url_selectHotspot,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON_Hotspot(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        requestQueue.add(stringRequest);

    }
    private void parseJSON_Hotspot(String json){

        JSONObject jsonObject = null;
        JSONArray results = null;
        ContentValues cv;

        String[] ID, NAME, LINEID, MODEID, NAME2, POINTFROM, POINTTO, TAG, FARE;


        String query;
        dba = DBAccess.getInstance(frmSync.this);

        try {
            jsonObject = new JSONObject(json);
            results = jsonObject.getJSONArray("JSONresults");

            //ID = new String[results.length()];
            ID = new String[results.length()];
            NAME= new String[results.length()];
            LINEID = new String[results.length()];
            MODEID = new String[results.length()];
            NAME2 = new String[results.length()];
            POINTFROM= new String[results.length()];
            POINTTO = new String[results.length()];
            TAG = new String[results.length()];
            FARE= new String[results.length()];


            //Delete all data in EmployeeRole table
            if (results.length() > 0) {

                //DELETE data
                query = "DELETE FROM HOTSPOT";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Deleting HOTSPOT");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }

                //RESEED primary key
                query = " DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'HOTSPOT' ";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Reseed HOTSPOT");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<results.length();i++){

                JSONObject jo = results.getJSONObject(i);

                // ID[i] = jo.getString("ID");
                ID[i] = jo.getString("ID");
                NAME[i] = jo.getString("NAME");
                LINEID[i] = jo.getString("LINEID");
                MODEID[i] = jo.getString("MODEID");
                NAME2[i] = jo.getString("NAME2");
                POINTFROM[i] = jo.getString("POINTFROM");
                POINTTO[i] = jo.getString("POINTTO");
                TAG[i] = jo.getString("TAG");
                FARE[i] = jo.getString("FARE");


                // Delete spaces
                //deviceID[i] = deviceID[i].replace(" ", "");
                //deviceName[i] = deviceName[i].replace(" ", "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(ID[i], "null") || Objects.equals(ID[i], "NULL") || ID[i] == null) {
                        ID[i] = null;
                    }
                    if (Objects.equals(NAME[i], "null") || Objects.equals(NAME[i], "NULL") || NAME[i] == null) {
                        NAME[i] = null;
                    }

                    if (Objects.equals(LINEID[i], "null") || Objects.equals(LINEID[i], "NULL") || LINEID[i] == null) {
                        LINEID[i] = null;
                    }
                    if (Objects.equals(MODEID[i], "null") || Objects.equals(MODEID[i], "NULL") || MODEID[i] == null) {
                        MODEID[i] = null;
                    }
                    if (Objects.equals(NAME2[i], "null") || Objects.equals(NAME2[i], "NULL") || NAME2[i] == null) {
                        NAME2[i] = null;
                    }
                    if (Objects.equals(POINTFROM[i], "null") || Objects.equals(POINTFROM[i], "NULL") || POINTFROM[i] == null) {
                        POINTFROM[i] = null;
                    }
                    if (Objects.equals(POINTTO[i], "null") || Objects.equals(POINTTO[i], "NULL") || POINTTO[i] == null) {
                        POINTTO[i] = null;
                    }
                    if (Objects.equals(TAG[i], "null") || Objects.equals(TAG[i], "NULL") || TAG[i] == null) {
                        TAG[i] = null;
                    }
                    if (Objects.equals(FARE[i], "null") || Objects.equals(FARE[i], "NULL") || FARE[i] == null) {
                        FARE[i] = null;
                    }

                }

                //INSERT to database
                cv = new ContentValues();
                //cv.put("ID", ID[i]);
                cv.put("ID", ID[i]);
                cv.put("NAME", NAME[i]);
                cv.put("LINEID", LINEID[i]);
                cv.put("MODEID", MODEID[i]);
                cv.put("NAME2", NAME2[i]);
                cv.put("POINTFROM", POINTFROM[i]);
                cv.put("POINTTO", POINTTO[i]);
                cv.put("TAG", TAG[i]);
                cv.put("FARE", FARE[i]);



                if(dba.insertDB("HOTSPOT",cv)) {
                    Toast.makeText(frmSync.this,"insert!", Toast.LENGTH_LONG).show();
                    Log.d("Response", "Error Inserting HOTSPOT");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyResource(){
        StringRequest stringRequest = new StringRequest(url_selectResource,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON_Resource(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        requestQueue.add(stringRequest);

    }
    private void parseJSON_Resource(String json){

        JSONObject jsonObject = null;
        JSONArray results = null;
        ContentValues cv;

        String[] ID, DESCRIPTION, TAG, REMARKS, CODE, RESOURCEMODELID;


        String query;
        dba = DBAccess.getInstance(frmSync.this);

        try {
            jsonObject = new JSONObject(json);
            results = jsonObject.getJSONArray("JSONresults");

            //ID = new String[results.length()];
            ID = new String[results.length()];
            DESCRIPTION= new String[results.length()];
            TAG = new String[results.length()];
            REMARKS= new String[results.length()];
            CODE= new String[results.length()];
            RESOURCEMODELID= new String[results.length()];

            //Delete all data in EmployeeRole table
            if (results.length() > 0) {

                //DELETE data
                query = "DELETE FROM RESOURCE";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Deleting RESOURCE");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }

                //RESEED primary key
                query = " DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'RESOURCE' ";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Reseed RESOURCE");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<results.length();i++){

                JSONObject jo = results.getJSONObject(i);

                // ID[i] = jo.getString("ID");
                ID[i] = jo.getString("ID");
                DESCRIPTION[i] = jo.getString("DESCRIPTION");
                TAG[i] = jo.getString("TAG");
                REMARKS[i] = jo.getString("REMARKS");
                CODE[i] = jo.getString("CODE");
                RESOURCEMODELID[i] = jo.getString("RESOURCEMODELID");

                // Delete spaces
                //deviceID[i] = deviceID[i].replace(" ", "");
                //deviceName[i] = deviceName[i].replace(" ", "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(ID[i], "null") || Objects.equals(ID[i], "NULL") || ID[i] == null) {
                        ID[i] = null;
                    }
                    if (Objects.equals(DESCRIPTION[i], "null") || Objects.equals(DESCRIPTION[i], "NULL") || DESCRIPTION[i] == null) {
                        DESCRIPTION[i] = null;
                    }

                    if (Objects.equals(TAG[i], "null") || Objects.equals(TAG[i], "NULL") || TAG[i] == null) {
                        TAG[i] = null;
                    }
                    if (Objects.equals(REMARKS[i], "null") || Objects.equals(REMARKS[i], "NULL") || REMARKS[i] == null) {
                        REMARKS[i] = null;
                    }
                    if (Objects.equals(CODE[i], "null") || Objects.equals(CODE[i], "NULL") || CODE[i] == null) {
                        CODE[i] = null;
                    }
                    if (Objects.equals(RESOURCEMODELID[i], "null") || Objects.equals(RESOURCEMODELID[i], "NULL") || RESOURCEMODELID[i] == null) {
                        RESOURCEMODELID[i] = null;
                    }

                }

                //INSERT to database
                cv = new ContentValues();
                //cv.put("ID", ID[i]);
                cv.put("ID", ID[i]);
                cv.put("DESCRIPTION", DESCRIPTION[i]);
                cv.put("TAG", TAG[i]);
                cv.put("REMARKS", REMARKS[i]);
                cv.put("CODE", CODE[i]);
                cv.put("RESOURCEMODELID", RESOURCEMODELID[i]);



                if(dba.insertDB("RESOURCE",cv)) {
                    Toast.makeText(frmSync.this,"insert!", Toast.LENGTH_LONG).show();
                    Log.d("Response", "Error Inserting RESOURCE");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyResourcemodel(){
        StringRequest stringRequest = new StringRequest(url_selectResourcemodel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON_Resourcemodel(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        requestQueue.add(stringRequest);

    }
    private void parseJSON_Resourcemodel(String json){

        JSONObject jsonObject = null;
        JSONArray results = null;
        ContentValues cv;

        String[] ID, NAME, TAG, REMARKS;


        String query;
        dba = DBAccess.getInstance(frmSync.this);

        try {
            jsonObject = new JSONObject(json);
            results = jsonObject.getJSONArray("JSONresults");

            //ID = new String[results.length()];
            ID = new String[results.length()];
            NAME= new String[results.length()];
            TAG = new String[results.length()];
            REMARKS= new String[results.length()];

            //Delete all data in EmployeeRole table
            if (results.length() > 0) {

                //DELETE data
                query = "DELETE FROM RESOURCEMODEL";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Deleting RESOURCEMODEL");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }

                //RESEED primary key
                query = " DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'RESOURCEMODEL' ";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Reseed RESOURCEMODEL");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<results.length();i++){

                JSONObject jo = results.getJSONObject(i);

                // ID[i] = jo.getString("ID");
                ID[i] = jo.getString("ID");
                NAME[i] = jo.getString("NAME");
                TAG[i] = jo.getString("TAG");
                REMARKS[i] = jo.getString("REMARKS");

                // Delete spaces
                //deviceID[i] = deviceID[i].replace(" ", "");
                //deviceName[i] = deviceName[i].replace(" ", "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(ID[i], "null") || Objects.equals(ID[i], "NULL") || ID[i] == null) {
                        ID[i] = null;
                    }
                    if (Objects.equals(NAME[i], "null") || Objects.equals(NAME[i], "NULL") || NAME[i] == null) {
                        NAME[i] = null;
                    }

                    if (Objects.equals(TAG[i], "null") || Objects.equals(TAG[i], "NULL") || TAG[i] == null) {
                        TAG[i] = null;
                    }
                    if (Objects.equals(REMARKS[i], "null") || Objects.equals(REMARKS[i], "NULL") || REMARKS[i] == null) {
                        REMARKS[i] = null;
                    }


                }

                //INSERT to database
                cv = new ContentValues();
                //cv.put("ID", ID[i]);
                cv.put("ID", ID[i]);
                cv.put("NAME", NAME[i]);
                cv.put("TAG", TAG[i]);
                cv.put("REMARKS", REMARKS[i]);



                if(dba.insertDB("RESOURCEMODEL",cv)) {
                    Toast.makeText(frmSync.this,"insert!", Toast.LENGTH_LONG).show();
                    Log.d("Response", "Error Inserting RESOURCEMODEL");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyServicematrix(){
        StringRequest stringRequest = new StringRequest(url_selectServiceMatrix,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON_ServiceMatrix(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        requestQueue.add(stringRequest);

    }
    private void parseJSON_ServiceMatrix(String json){

        JSONObject jsonObject = null;
        JSONArray results = null;
        ContentValues cv;

        String[] SERVICEID, RESOURCEID, RESOURCEMODELID, CUSTOMERID, CUSTOMERTYPEID, MODEID,  LINEID, FROMLINESEGMENTID, TOLINESEGMENTID, OPERATIONID, AMOUNT, MINRANGE, MINAMOUNT;


        String query;
        dba = DBAccess.getInstance(frmSync.this);

        try {
            jsonObject = new JSONObject(json);
            results = jsonObject.getJSONArray("JSONresults");

            //ID = new String[results.length()];
            SERVICEID = new String[results.length()];
            RESOURCEID= new String[results.length()];
            RESOURCEMODELID= new String[results.length()];
            CUSTOMERID= new String[results.length()];
            CUSTOMERTYPEID= new String[results.length()];
            MODEID= new String[results.length()];
            LINEID= new String[results.length()];
            FROMLINESEGMENTID= new String[results.length()];
            TOLINESEGMENTID= new String[results.length()];
            OPERATIONID= new String[results.length()];
            AMOUNT= new String[results.length()];
            MINRANGE= new String[results.length()];
            MINAMOUNT= new String[results.length()];

            //Delete all data in EmployeeRole table
            if (results.length() > 0) {

                //DELETE data
                query = "DELETE FROM SERVICEMATRIX";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Deleting SERVICEMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }

                //RESEED primary key
                query = " DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'SERVICEMATRIX' ";
                if (!dba.executeQuery(query)) {
                    Log.d("Response", "Error Reseed SERVICEMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<results.length();i++){

                JSONObject jo = results.getJSONObject(i);

                // ID[i] = jo.getString("ID");
                SERVICEID[i] = jo.getString("SERVICEID");
                RESOURCEID[i] = jo.getString("RESOURCEID");
                RESOURCEMODELID[i] = jo.getString("RESOURCEMODELID");
                CUSTOMERID[i] = jo.getString("CUSTOMERID");
                CUSTOMERTYPEID[i] = jo.getString("CUSTOMERTYPEID");
                MODEID[i] = jo.getString("MODEID");
                LINEID[i] = jo.getString("LINEID");
                FROMLINESEGMENTID[i] = jo.getString("FROMLINESEGMENTID");
                TOLINESEGMENTID[i] = jo.getString("TOLINESEGMENTID");
                OPERATIONID[i] = jo.getString("OPERATIONID");
                AMOUNT[i] = jo.getString("AMOUNT");
                MINRANGE[i] = jo.getString("MINRANGE");
                MINAMOUNT[i] = jo.getString("MINAMOUNT");


                // Delete spaces
                //deviceID[i] = deviceID[i].replace(" ", "");
                //deviceName[i] = deviceName[i].replace(" ", "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(RESOURCEID[i], "null") || Objects.equals(RESOURCEID[i], "NULL") || RESOURCEID[i] == null) {
                        RESOURCEID[i] = null;
                    }
                    if (Objects.equals(RESOURCEMODELID[i], "null") || Objects.equals(RESOURCEMODELID[i], "NULL") || RESOURCEMODELID[i] == null) {
                        RESOURCEMODELID[i] = null;
                    }

                    if (Objects.equals(CUSTOMERID[i], "null") || Objects.equals(CUSTOMERID[i], "NULL") || CUSTOMERID[i] == null) {
                        CUSTOMERID[i] = null;
                    }
                    if (Objects.equals(CUSTOMERTYPEID[i], "null") || Objects.equals(CUSTOMERTYPEID[i], "NULL") || CUSTOMERTYPEID[i] == null) {
                        CUSTOMERTYPEID[i] = null;
                    }
                    if (Objects.equals(FROMLINESEGMENTID[i], "null") || Objects.equals(FROMLINESEGMENTID[i], "NULL") || FROMLINESEGMENTID[i] == null) {
                        FROMLINESEGMENTID[i] = null;
                    }

                    if (Objects.equals(TOLINESEGMENTID[i], "null") || Objects.equals(TOLINESEGMENTID[i], "NULL") || TOLINESEGMENTID[i] == null) {
                        TOLINESEGMENTID[i] = null;
                    }



                }

                //INSERT to database
                cv = new ContentValues();
                //cv.put("ID", ID[i]);
                cv.put("SERVICEID", SERVICEID[i]);
                cv.put("RESOURCEID", RESOURCEID[i]);
                cv.put("RESOURCEMODELID", RESOURCEMODELID[i]);
                cv.put("CUSTOMERID", CUSTOMERID[i]);
                cv.put("CUSTOMERTYPEID", CUSTOMERTYPEID[i]);
                cv.put("MODEID", MODEID[i]);
                cv.put("LINEID", LINEID[i]);
                cv.put("FROMLINESEGMENTID", FROMLINESEGMENTID[i]);
                cv.put("TOLINESEGMENTID", TOLINESEGMENTID[i]);
                cv.put("OPERATIONID", OPERATIONID[i]);
                cv.put("AMOUNT", AMOUNT[i]);
                cv.put("MINRANGE", MINRANGE[i]);
                cv.put("MINAMOUNT", MINAMOUNT[i]);





                if(dba.insertDB("SERVICEMATRIX",cv)) {
                    Toast.makeText(frmSync.this,"insert!", Toast.LENGTH_LONG).show();
                    Log.d("Response", "Error Inserting SERVICEMATRIX");
//                    Toast.makeText(MapaSync.this, GlobalVariable.sqlException, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //set in database
    private void insert_Devicedata() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [DEVICEDATA] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncDevicedata_Data(c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("KEY")),
                            c.getString(c.getColumnIndex("VALUE"))
                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncDevicedata_Data ( String DEVICENAME, String KEY,  String VALUE) {


        Log.d("URL", url_insertDevicedata);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(KEY, "") || KEY == null || Objects.equals(KEY, "null")) {KEY = "NULL";}
            if (Objects.equals(VALUE, "") || VALUE== null || Objects.equals(VALUE, "null")) {VALUE= "NULL";}

        }

        final String finaldevicename = DEVICENAME;
        final String finalkey = KEY;
        final String finalvalue = VALUE;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertDevicedata,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("DEVICEDATA");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("DEVICENAME", finaldevicename);
                params.put("KEY", finalkey);
                params.put("VALUE", finalvalue);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Ticket() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TICKET] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncticket_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("CUSTOMERID")),
                            c.getString(c.getColumnIndex("SERVICEID")),
                            c.getString(c.getColumnIndex("QTY")),
                            c.getString(c.getColumnIndex("UOMIN")),
                            c.getString(c.getColumnIndex("UNITPRICE")),
                            c.getString(c.getColumnIndex("DISCOUNTID")),
                            c.getString(c.getColumnIndex("DISCOUNTAMOUNT")),
                            c.getString(c.getColumnIndex("TAXAMOUNT")),
                            c.getString(c.getColumnIndex("NETAMOUNT")),
                            c.getString(c.getColumnIndex("PAYMENTTYPEID")),
                            c.getString(c.getColumnIndex("DATETIMESTAMP")),
                            c.getString(c.getColumnIndex("CONTROLCODE")),
                            c.getString(c.getColumnIndex("UPGRADECODE")),
                            c.getString(c.getColumnIndex("OVERRIDECODE")),
                            c.getString(c.getColumnIndex("PRINTCOPIES")),
                            c.getString(c.getColumnIndex("TAG")),
                            c.getString(c.getColumnIndex("REMARKS")),
                            c.getString(c.getColumnIndex("LINEID")),
                            c.getString(c.getColumnIndex("FROMREFPOINT")),
                            c.getString(c.getColumnIndex("TOREFPOINT")),
                            c.getString(c.getColumnIndex("RESOURCEMODELID")),
                            c.getString(c.getColumnIndex("PASSENGER"))
                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncticket_Data ( String TRIPID, String DEVICENAME,  String CUSTOMERID, String SERVICEID, String QTY, String UOMID,  String UNITPRICE, String DISCOUNTID,
                                   String DISCOUNTAMOUNT, String TAXAMOUNT, String NETAMOUNT, String PAYMENTTYPEID, String DATETIMESTAMP, String CONTROLCODE,
                                   String UPGRADECODE, String OVERRIDECODE, String PRINTCOPIES, String TAG, String REMARKS, String LINEID, String FROMREFPOINT,
                                   String TOREFPOINT, String RESOURCEMODELID, String PASSENGER) {


        Log.d("URL", url_insertTicket);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(CUSTOMERID, "") || CUSTOMERID== null || Objects.equals(CUSTOMERID, "null")) {CUSTOMERID= "NULL";}
            if (Objects.equals(SERVICEID, "") || SERVICEID== null || Objects.equals(SERVICEID, "null")) {SERVICEID= "NULL";}
            if (Objects.equals(QTY, "") || QTY== null || Objects.equals(QTY, "null")) {QTY= "NULL";}
            if (Objects.equals(UOMID, "") || UOMID== null || Objects.equals(UOMID, "null")) {UOMID= "NULL";}
            if (Objects.equals(UNITPRICE, "") || UNITPRICE== null || Objects.equals(UNITPRICE, "null")) {UNITPRICE= "NULL";}
            if (Objects.equals(DISCOUNTID, "") || DISCOUNTID== null || Objects.equals(DISCOUNTID, "null")) {DISCOUNTID= "NULL";}
            if (Objects.equals(DISCOUNTAMOUNT, "") || DISCOUNTAMOUNT== null || Objects.equals(DISCOUNTAMOUNT, "null")) {DISCOUNTAMOUNT= "NULL";}
            if (Objects.equals(TAXAMOUNT, "") || TAXAMOUNT== null || Objects.equals(TAXAMOUNT, "null")) {TAXAMOUNT= "NULL";}
            if (Objects.equals(NETAMOUNT, "") || NETAMOUNT== null || Objects.equals(NETAMOUNT, "null")) {NETAMOUNT= "NULL";}
            if (Objects.equals(PAYMENTTYPEID, "") || PAYMENTTYPEID== null || Objects.equals(PAYMENTTYPEID, "null")) {PAYMENTTYPEID= "NULL";}
            if (Objects.equals(DATETIMESTAMP, "") || DATETIMESTAMP== null || Objects.equals(DATETIMESTAMP, "null")) {DATETIMESTAMP= "NULL";}
            if (Objects.equals(CONTROLCODE, "") || CONTROLCODE== null || Objects.equals(CONTROLCODE, "null")) {CONTROLCODE= "NULL";}
            if (Objects.equals(UPGRADECODE, "") || UPGRADECODE== null || Objects.equals(UPGRADECODE, "null")) {UPGRADECODE= "NULL";}
            if (Objects.equals(OVERRIDECODE, "") || OVERRIDECODE== null || Objects.equals(OVERRIDECODE, "null")) {OVERRIDECODE= "NULL";}
            if (Objects.equals(PRINTCOPIES, "") || PRINTCOPIES== null || Objects.equals(PRINTCOPIES, "null")) {PRINTCOPIES= "NULL";}
            if (Objects.equals(TAG, "") || TAG== null || Objects.equals(TAG, "null")) {TAG= "NULL";}
            if (Objects.equals(REMARKS, "") || REMARKS== null || Objects.equals(REMARKS, "null")) {REMARKS= "NULL";}
            if (Objects.equals(LINEID, "") || LINEID== null || Objects.equals(LINEID, "null")) {LINEID= "NULL";}
            if (Objects.equals(FROMREFPOINT, "") || FROMREFPOINT== null || Objects.equals(FROMREFPOINT, "null")) {FROMREFPOINT= "NULL";}
            if (Objects.equals(TOREFPOINT, "") || TOREFPOINT== null || Objects.equals(TOREFPOINT, "null")) {TOREFPOINT= "NULL";}
            if (Objects.equals(RESOURCEMODELID, "") || RESOURCEMODELID== null || Objects.equals(RESOURCEMODELID, "null")) {RESOURCEMODELID= "NULL";}
            if (Objects.equals(PASSENGER, "") || PASSENGER== null || Objects.equals(PASSENGER, "null")) {PASSENGER= "NULL";}
        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finalcustomerid= CUSTOMERID;
        final String finalserviceid= SERVICEID;
        final String finalqty= QTY;
        final String finaluomid= UOMID;
        final String finalunitprice= UNITPRICE;
        final String finaldiscountid= DISCOUNTID;
        final String finaldiscountamount= DISCOUNTAMOUNT;
        final String finaltaxamount= TAXAMOUNT;
        final String finalnetamount= NETAMOUNT;
        final String finalpaymentypeid= PAYMENTTYPEID;
        final String finaldatetimestamp= DATETIMESTAMP;
        final String finalcontrolcode= CONTROLCODE;
        final String finalupgradecode = UPGRADECODE;
        final String finaloverridecode = OVERRIDECODE;
        final String finalprintcopies = PRINTCOPIES;
        final String finaltag = TAG;
        final String finalremarks = REMARKS;
        final String finallineid = LINEID;
        final String finalfromrefpoint = FROMREFPOINT;
        final String finaltorefpoint = TOREFPOINT;
        final String finalresourcemodelid = RESOURCEMODELID;
        final String finalpassenger = PASSENGER;


        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTicket,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPCOST");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("CUSTOMERID", finalcustomerid);
                params.put("SERVICEID", finalserviceid);
                params.put("QTY", finalqty);
                params.put("UOMID", finaluomid);
                params.put("UNITPRICE", finalunitprice);
                params.put("DISCOUNTID", finaldiscountid);
                params.put("DISCOUNTAMOUNT", finaldiscountamount);
                params.put("TAXAMOUNT", finaltaxamount);
                params.put("NETAMOUNT", finalnetamount);
                params.put("PAYMENTTYPEID", finalpaymentypeid);
                params.put("DATETIMESTAMP", finaldatetimestamp);
                params.put("CONTROLCODE", finalcontrolcode);
                params.put("UPGRADECODE", finalupgradecode);
                params.put("OVERRIDECODE", finaloverridecode);
                params.put("PRINTCOPIES", finalprintcopies);
                params.put("TAG", finaltag);
                params.put("REMARKS", finalremarks);
                params.put("LINEID", finallineid);
                params.put("FROMREFPOINT", finalfromrefpoint);
                params.put("TOREFPOINT", finaltorefpoint);
                params.put("RESOURCEMODELID", finalresourcemodelid);
                params.put("PASSENGER", finalpassenger);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_trip() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIP] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTrip_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("LINEID")),
                            c.getString(c.getColumnIndex("RECOURCEID")),
                            c.getString(c.getColumnIndex("MODEID")),
                            c.getString(c.getColumnIndex("STARTDATETIMESTAMP")),
                            c.getString(c.getColumnIndex("ENDDATETIMESTAMP")),
                            c.getString(c.getColumnIndex("TAG")),
                            c.getString(c.getColumnIndex("REMARKS"))
                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTrip_Data ( String TRIPID, String DEVICENAME,  String LINEID,String RESOURCEID, String MODEID, String STARTDATETIMESTAMP, String ENDDATETIMESTAMP,
                                 String TAG, String REMARKS) {


        Log.d("URL", url_insertTrip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(LINEID, "") || LINEID == null || Objects.equals(LINEID, "null")) {LINEID= "NULL";}
            if (Objects.equals(RESOURCEID, "") || RESOURCEID== null || Objects.equals(RESOURCEID, "null")) {RESOURCEID= "NULL";}
            if (Objects.equals(MODEID, "") || MODEID== null || Objects.equals(MODEID, "null")) {MODEID= "NULL";}
            if (Objects.equals(STARTDATETIMESTAMP, "") || STARTDATETIMESTAMP== null || Objects.equals(STARTDATETIMESTAMP, "null")) {STARTDATETIMESTAMP= "NULL";}
            if (Objects.equals(ENDDATETIMESTAMP, "") || ENDDATETIMESTAMP== null || Objects.equals(ENDDATETIMESTAMP, "null")) {ENDDATETIMESTAMP= "NULL";}
            if (Objects.equals(TAG, "") || TAG== null || Objects.equals(TAG, "null")) {TAG= "NULL";}
            if (Objects.equals(REMARKS, "") || REMARKS== null || Objects.equals(REMARKS, "null")) {REMARKS= "NULL";}
        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finallineid= LINEID;
        final String finalresourceid = RESOURCEID;
        final String finalmodeid = MODEID;
        final String finalstartdatetime = STARTDATETIMESTAMP;
        final String finalenddatetime = ENDDATETIMESTAMP;
        final String finaltag = TAG;
        final String finalremarks = REMARKS;


        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTrip,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPCOST");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("LINE", finallineid);
                params.put("RESOURCEID", finalresourceid);
                params.put("MODEID", finalmodeid);
                params.put("STARTDATETIMESTAMP", finalstartdatetime);
                params.put("ENDDATETIMESTAMP", finalenddatetime);
                params.put("TAG", finaltag);
                params.put("REMARKS", finalremarks);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Tripcost() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPCOST] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTripcost_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("COSTTYPEID")),
                            c.getString(c.getColumnIndex("TAG")),
                            c.getString(c.getColumnIndex("REMARKS"))
                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTripcost_Data ( String TRIPID, String DEVICENAME,  String COSTTYPEID, String TAG, String REMARKS) {


        Log.d("URL", url_insertTripcost);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(COSTTYPEID, "") || COSTTYPEID== null || Objects.equals(COSTTYPEID, "null")) {COSTTYPEID= "NULL";}
            if (Objects.equals(TAG, "") || TAG== null || Objects.equals(TAG, "null")) {TAG= "NULL";}
            if (Objects.equals(REMARKS, "") || REMARKS== null || Objects.equals(REMARKS, "null")) {REMARKS= "NULL";}
        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finalcosttypeid = COSTTYPEID;
        final String finaltag = TAG;
        final String finalremarks = REMARKS;


        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTripcost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPCOST");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("COSTTYPEID", finalcosttypeid);
                params.put("TAG", finaltag);
                params.put("REMARKS", finalremarks);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Tripcrew() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPCREW] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTripcrew_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("EMPLOYEEROLEID")),
                            c.getString(c.getColumnIndex("EMPLOYEEID"))

                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTripcrew_Data ( String TRIPID, String DEVICENAME,  String EMPLOYEEROLEID, String EMPLOYEEID) {


        Log.d("URL", url_insertTripcrew);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(EMPLOYEEROLEID, "") || EMPLOYEEROLEID== null || Objects.equals(EMPLOYEEROLEID, "null")) {EMPLOYEEROLEID= "NULL";}
            if (Objects.equals(EMPLOYEEID, "") || EMPLOYEEID == null || Objects.equals(EMPLOYEEID, "null")) {EMPLOYEEID= "NULL";}

        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finalemployeeroleid = EMPLOYEEROLEID;
        final String finalemployeeid = EMPLOYEEID;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTripcrew,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPCREW");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("EMPLOYEEROLEID", finalemployeeroleid);
                params.put("EMPLOYEEID", finalemployeeid);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Tripinspection() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPINSPECTION] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTripinspection_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("DATETIMESTAMP")),
                            c.getString(c.getColumnIndex("EMPLOYEEID")),
                            c.getString(c.getColumnIndex("ATTRIBUTEID")),
                            c.getString(c.getColumnIndex("QTY")),
                            c.getString(c.getColumnIndex("KMPOST")),
                            c.getString(c.getColumnIndex("PCOUNT")),
                            c.getString(c.getColumnIndex("LINESEGMENT")),
                            c.getString(c.getColumnIndex("DIRECTION")),
                            c.getString(c.getColumnIndex("BCOUNT")),
                            c.getString(c.getColumnIndex("TICKETID"))
                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTripinspection_Data ( String TRIPID, String DEVICENAME,  String DATETIMESTAMP, String EMPLOYEEID, String ATTRIBUTEID, String QTY,
                                           String KMPOST, String PCOUNT, String LINESEGMENT, String DIRECTION, String BCOUNT, String TICKETID) {


        Log.d("URL", url_insertTripcrew);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(DATETIMESTAMP, "") || DATETIMESTAMP== null || Objects.equals(DATETIMESTAMP, "null")) {DATETIMESTAMP= "NULL";}
            if (Objects.equals(EMPLOYEEID, "") || EMPLOYEEID == null || Objects.equals(EMPLOYEEID, "null")) {EMPLOYEEID= "NULL";}
            if (Objects.equals(ATTRIBUTEID, "") || ATTRIBUTEID == null || Objects.equals(ATTRIBUTEID, "null")) {ATTRIBUTEID= "NULL";}
            if (Objects.equals(QTY, "") || QTY == null || Objects.equals(QTY, "null")) {QTY= "NULL";}
            if (Objects.equals(KMPOST, "") || KMPOST == null || Objects.equals(KMPOST, "null")) {KMPOST= "NULL";}
            if (Objects.equals(PCOUNT, "") || PCOUNT == null || Objects.equals(PCOUNT, "null")) {PCOUNT= "NULL";}
            if (Objects.equals(LINESEGMENT, "") || LINESEGMENT == null || Objects.equals(LINESEGMENT, "null")) {LINESEGMENT= "NULL";}
            if (Objects.equals(DIRECTION, "") || DIRECTION == null || Objects.equals(DIRECTION, "null")) {DIRECTION= "NULL";}
            if (Objects.equals(BCOUNT, "") || BCOUNT == null || Objects.equals(BCOUNT, "null")) {BCOUNT= "NULL";}
            if (Objects.equals(TICKETID, "") || TICKETID == null || Objects.equals(TICKETID, "null")) {TICKETID= "NULL";}

        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finaldatetimestamp = DATETIMESTAMP;
        final String finalemployeeid = EMPLOYEEID;
        final String finalattributeid = ATTRIBUTEID;
        final String finalqty = QTY;
        final String finalkmpost = KMPOST;
        final String finalpcount = PCOUNT;
        final String finallinesegment = LINESEGMENT;
        final String finaldirection = DIRECTION;
        final String finalbcount = BCOUNT;
        final String finalticketid = TICKETID;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTripinspection,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPCREW");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("DATETIMESTAMP", finaldatetimestamp);
                params.put("EMPLOYEEID", finalemployeeid);
                params.put("ATTRIBUTEID", finalattributeid);
                params.put("QTY", finalqty);
                params.put("KMPOST", finalkmpost);
                params.put("PCOUNT", finalpcount);
                params.put("LINESEGMENT", finallinesegment);
                params.put("DIRECTION", finaldirection);
                params.put("BCOUNT", finalbcount);
                params.put("TICKETID", finalticketid);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }
    private void insert_Triplog() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPLOG] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTriplog_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("DATETIMESTAMP")),
                            c.getString(c.getColumnIndex("LOG"))

                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTriplog_Data ( String TRIPID, String DEVICENAME,  String DATETIMESTAMP, String LOG) {


        Log.d("URL", url_insertTriplog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(DATETIMESTAMP, "") || DATETIMESTAMP== null || Objects.equals(DATETIMESTAMP, "null")) {DATETIMESTAMP= "NULL";}
            if (Objects.equals(LOG, "") || LOG == null || Objects.equals(LOG, "null")) {LOG= "NULL";}

        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finaldatetimestamp = DATETIMESTAMP;
        final String finallog= LOG;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTriplog,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPCREW");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("DATEIMESTAMP", finaldatetimestamp);
                params.put("LOG", finallog);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Tripreceipt() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPRECEIPT] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTripreceipt_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("CUSTOMERID")),
                            c.getString(c.getColumnIndex("EMPLOYEEID")),
                            c.getString(c.getColumnIndex("DATETIMESTAMP")),
                            c.getString(c.getColumnIndex("AMOUNT")),
                            c.getString(c.getColumnIndex("ORNUMBER"))

                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTripreceipt_Data ( String TRIPID, String DEVICENAME, String CUSTOMERID, String EMPLOYEEID, String DATETIMESTAMP, String AMOUNT, String ORNUMBER) {


        Log.d("URL", url_insertTriplog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(CUSTOMERID, "") || CUSTOMERID == null || Objects.equals(CUSTOMERID, "null")) { CUSTOMERID= "NULL";}
            if (Objects.equals(EMPLOYEEID, "") || EMPLOYEEID == null || Objects.equals(EMPLOYEEID, "null")) { EMPLOYEEID= "NULL";}
            if (Objects.equals(DATETIMESTAMP, "") || DATETIMESTAMP== null || Objects.equals(DATETIMESTAMP, "null")) {DATETIMESTAMP= "NULL";}
            if (Objects.equals(AMOUNT, "") || AMOUNT == null || Objects.equals(AMOUNT, "null")) {AMOUNT= "NULL";}
            if (Objects.equals(ORNUMBER, "") || ORNUMBER == null || Objects.equals(ORNUMBER, "null")) {ORNUMBER= "NULL";}

        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finalcustomerid = CUSTOMERID;
        final String finalemployeeid = EMPLOYEEID;
        final String finaldatetimestamp = DATETIMESTAMP;
        final String finalamount = AMOUNT;
        final String finalornumber = ORNUMBER;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTripreceipt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPRECEIPT");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("CUSTOMERID", finalcustomerid);
                params.put("EMPLOYEEID", finalemployeeid);
                params.put("DATEIMESTAMP", finaldatetimestamp);
                params.put("AMOUNT", finalamount);
                params.put("ORNUMBER", finalornumber);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Tripreverse() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPREVERSE] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTripreverse_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("DATETIMESTAMP")),
                            c.getString(c.getColumnIndex("DIRECTION")),
                            c.getString(c.getColumnIndex("EMPLOYEEID")),
                            c.getString(c.getColumnIndex("KMPOST"))

                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTripreverse_Data ( String TRIPID, String DEVICENAME, String DATETIMESTAMP, String DIRECTION, String EMPLOYEEID, String KMPOST) {


        Log.d("URL", url_insertTriplog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(DATETIMESTAMP, "") || DATETIMESTAMP== null || Objects.equals(DATETIMESTAMP, "null")) {DATETIMESTAMP= "NULL";}
            if (Objects.equals(DIRECTION, "") || DIRECTION == null || Objects.equals(DIRECTION, "null")) {DIRECTION= "NULL";}
            if (Objects.equals(EMPLOYEEID, "") || EMPLOYEEID == null || Objects.equals(EMPLOYEEID, "null")) { EMPLOYEEID= "NULL";}
            if (Objects.equals(KMPOST, "") || KMPOST == null || Objects.equals(KMPOST, "null")) {KMPOST= "NULL";}

        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finaldatetimestamp = DATETIMESTAMP;
        final String finaldirection = DIRECTION;
        final String finalemployeeid = EMPLOYEEID;
        final String finalkmpost = KMPOST;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTripreceipt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPRECEIPT");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("DATEIMESTAMP", finaldatetimestamp);
                params.put("DIRECTION", finaldirection);
                params.put("EMPLOYEEID", finalemployeeid);
                params.put("KMPOST", finalkmpost);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Tripusage() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPUSAGE] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTripusage_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("ATTRIBUTEID")),
                            c.getString(c.getColumnIndex("QTY"))
                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTripusage_Data ( String TRIPID, String DEVICENAME, String ATTRIBUTEID, String QTY) {


        Log.d("URL", url_insertTripusage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(ATTRIBUTEID, "") || ATTRIBUTEID== null || Objects.equals(ATTRIBUTEID, "null")) {ATTRIBUTEID= "NULL";}
            if (Objects.equals(QTY, "") || QTY == null || Objects.equals(QTY, "null")) {QTY= "NULL";}
        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finalattributeid = ATTRIBUTEID;
        final String finalqty = QTY;


        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTripusage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                        clearTable("TRIPRECEIPT");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("ATTRIBBUTEID", finalattributeid);
                params.put("QTY", finalqty);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }

    private void insert_Tripwithholding() {

        transactCurrent = 0;
        transactOverAll = 0;
        dataSync = "";

        sqlQuery = "SELECT * FROM [TRIPWITHHOLDING] ";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor c = null;
        c = dba.selectQuery(sqlQuery);

        transactOverAll = c.getCount();
//        dataSync = String.valueOf(transactCurrent) + "/" + String.valueOf(transactOverAll);
//        txtStatusData.setText(dataSync);

        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                do {
                    syncTripwithholding_Data(c.getString(c.getColumnIndex("TRIPID")),
                            c.getString(c.getColumnIndex("DEVICENAME")),
                            c.getString(c.getColumnIndex("ATTRIBUTEID")),
                            c.getString(c.getColumnIndex("AMOUNT"))
                    );
                } while (c.moveToNext());
            } finally {
                c.close();
            }
            //checkTransactionUpload();
        }
    }
    private void syncTripwithholding_Data ( String TRIPID, String DEVICENAME, String ATTRIBUTEID, String AMOUNT) {


        Log.d("URL", url_insertTripwithholding);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(TRIPID, "") || TRIPID == null || Objects.equals(TRIPID, "null")) {TRIPID= "NULL";}
            if (Objects.equals(DEVICENAME, "") || DEVICENAME == null || Objects.equals(DEVICENAME, "null")) { DEVICENAME= "NULL";}
            if (Objects.equals(ATTRIBUTEID, "") || ATTRIBUTEID== null || Objects.equals(ATTRIBUTEID, "null")) {ATTRIBUTEID= "NULL";}
            if (Objects.equals(AMOUNT, "") || AMOUNT== null || Objects.equals(AMOUNT, "null")) {AMOUNT= "NULL";}
        }
        final String finaltripid = TRIPID;
        final String finaldevicename = DEVICENAME;
        final String finalattributeid = ATTRIBUTEID;
        final String finalamount = AMOUNT;


        StringRequest postRequest = new StringRequest(Request.Method.POST, url_insertTripwithholding,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        transactCurrent = transactCurrent + 1;
                        updateListView();
                        Log.d("Response-Trans", response);
                        pendingRequest--;
                        transaction = 1;

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d("Error.Response-Trans", String.valueOf(error));
                        pendingRequest--;
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TRIPID", finaltripid);
                params.put("DEVICENAME", finaldevicename);
                params.put("ATTRIBBUTEID", finalattributeid);
                params.put("AMOUNT", finalamount);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        requestQueue.add(postRequest);
        pendingRequest++;
    }



    private void checkTransactionUpload() {

        String query = "";


    }
    private void connectWifi() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + GlobalVariable.serverNetworkSSID + "\"";

//        //FOR WEP NETWORK SECURITY
//        conf.wepKeys[0] = "\"" + GlobalVariable.serverNetworkPass + "\"";
//        conf.wepTxKeyIndex = 0;
//        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        //FOR WPA NETWORK SECURITY
        conf.preSharedKey = "\""+ GlobalVariable.serverNetworkPass +"\"";

//        //FOR OPEN NETWORK KEYS
//        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        wifiManager = (WifiManager)frmSync.this.getSystemService(Context.WIFI_SERVICE);

        wifiManager.addNetwork(conf);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

    }


    private void clearTable(String tableName){
        sqlQuery = "DELETE FROM " + tableName + "";
        dba = DBAccess.getInstance(frmSync.this);
        Cursor cursor = null;
        c = dba.selectQuery(sqlQuery);

    }


}
