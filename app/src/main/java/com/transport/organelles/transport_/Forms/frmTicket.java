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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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



/**
 * Created by Organelles on 6/14/2017.
 */

public class frmTicket extends AppCompatActivity {

    private TextView txtDateTime, ticketno, bound, price, kmpost,tremaining ;
    NumberPicker origin, destination;
    RadioGroup radiogroup;
    RadioButton regular, senior, student, baggage, rb;
    Button print, gc, aoc, hotspot;
    String totalkm, paxtype, dtstartTime = "", hotspot_km, fare;
    int origin_v, des_v, o_refpoint, d_refpoint;
    String service_a, amount_a, minrange_a, minamount_a ;
    double ticketprice, km, totalkm2;
    String sqlQuery= "", hasHotspot="";
    private DBAccess dba;
    String lineID;  private static final boolean D = true;
    private static final String TAG = "BTPrinter";
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;

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
    View include;
    ImageView bluetooth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmticket);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitleBar();
        setObject();
        objectListener();
        //getDiscount();

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

        ticketno = (TextView)findViewById(R.id.t_ticket);
        tremaining = (TextView)findViewById(R.id.t_remaining);
        bound = (TextView)findViewById(R.id.t_direction);
        price = (TextView)findViewById(R.id.t_price);
        kmpost = (TextView)findViewById(R.id.t_km);
        origin = (NumberPicker)findViewById(R.id.t_origin);
        destination = (NumberPicker)findViewById(R.id.t_destination);
        regular = (RadioButton) findViewById(R.id.td_regular);
        senior = (RadioButton)findViewById(R.id.td_senior);
        student = (RadioButton)findViewById(R.id.td_student);
        baggage = (RadioButton)findViewById(R.id.td_baggage);
        print = (Button)findViewById(R.id.t_print);
        gc = (Button)findViewById(R.id.t_gc);
        aoc = (Button)findViewById(R.id.t_aoc);
        hotspot = (Button)findViewById(R.id.t_hotspot);
        radiogroup = (RadioGroup)findViewById(R.id.radiogroup);
        include = findViewById(R.id.actionbar);
        bluetooth = (ImageView)findViewById(R.id.bluetooth);


//        DBQuery dbQuery = new DBQuery(frmTicket.this);
//        String device = GlobalVariable.getPhoneName();
//        String value = dbQuery.getTripId(device);
//        String line = dbQuery.getLine(value);
//        GlobalVariable.d_lineid = line;
//        Log.wtf("lineid", line);




    }

    private void setTitleBar() {

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
                                String batteryLevel = String.format("%6.0f", GlobalClass.getBatteryLevel(frmTicket.this));
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

    private void objectListener(){
        final DBQuery dbQuery = new DBQuery(frmTicket.this);
        String directionDB = dbQuery.getDirectionfromDB();
//        String direction = GlobalVariable.d_direct;
        String lDirection = GlobalVariable.getDirection();
        Log.wtf("DIRECTION!!!!", lDirection);

        if(lDirection == null){
            if(directionDB.toString().contains("-1")){
                bound.setText("S BOUND");

                String id  = dbQuery.getLinefrmDB();
                ascending(id);

            }else if(directionDB.toString().contains("1")){
                bound.setText("N BOUND");
                String id  = dbQuery.getLinefrmDB();
                descending(id);
            }
        }else{
            if(lDirection.toString().contains("SOUTH") ){
                bound.setText("S BOUND");

            }else if(lDirection.toString().contains("NORTH")){
                bound.setText("N BOUND");
            }

            if(lDirection.toString().contains("SOUTH") ){
                String id  = dbQuery.getLinefrmDB();
                ascending(id);

            }else if(lDirection.toString().contains("NORTH")){
                String id  = dbQuery.getLinefrmDB();
                descending(id);
            }



        }

        getDiscount();

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = GlobalVariable.getPaxtype();

                if(type.equals("1")){
                    String typename = "Regular";
                    saveTicket(type);
                    printTicket(typename);
                }else if(type.equals("2")){
                    String typename= "Senior";
                    printTicket(typename);
                    saveTicket(type);
                }else if(type.equals("3")){
                    String typename = "Student";
                    printTicket(typename);
                    saveTicket(type);
                }else if(type.equals("5")){
                    String typename = "Baggage";
                    printTicket(typename);
                    saveTicket(type);
                }else {
                    Toast.makeText(frmTicket.this, "Please select a Passenger Type.", Toast.LENGTH_LONG).show();
                }

            }
        });

        gc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grossCollect();
            }
        });
        aoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                areaOfClosing();
            }
        });


        String de = GlobalVariable.getPhoneName();
        dbQuery.getlastTicket(de);
        int l = Integer.parseInt(GlobalVariable.d_lastticketid);
        int t = 1;
        int nextticket = l + t;
        String format =  String.format("%1$05d ", nextticket);//String.format("%07d", "0");
        ticketno.setText(format);

        int remain = dbQuery.getRemainingPax(GlobalVariable.d_lasttripid);
        tremaining.setText(remain+"");

        hotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasHotspot = "1";
                String line = GlobalVariable.getLineid();
                hotspotPax(o_refpoint, line);
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.modal_bluetooth, null);
                final Button connect = (Button) alertLayout.findViewById(R.id.connect);
                final Button disconnect = (Button) alertLayout.findViewById(R.id.disconnect);
                AlertDialog.Builder alert = new AlertDialog.Builder(frmTicket.this);

                connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent serverIntent = new Intent(frmTicket.this, DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

                    }
                });

                disconnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mService.stop();
                    }
                });
                alert.setTitle("Bluetooth");
                alert.setView(alertLayout);
                alert.show();



            }
        });



        }

    private void ascending(final String id){
        Toast.makeText(frmTicket.this,"ascending", Toast.LENGTH_LONG ).show();
        final DBQuery dbQuery = new DBQuery(frmTicket.this);
        //final String id = GlobalVariable.getLineid();
        Log.wtf("id", id);
        String[] o = dbQuery.getLineSegment(id, "ASC");
        origin.setMinValue(0);
        origin.setMaxValue(o.length-1);
        origin.setDisplayedValues(o);
        origin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hasHotspot = "0";
                origin_v = newVal;
                String[] o = dbQuery.getLineSegment(id, "ASC");
                String name = o[origin_v];
                o_refpoint = dbQuery.getRefpoint(name);
                Log.wtf("value -o",o_refpoint +"");
                getDiscount2();
                GlobalVariable.setOrigin(String.valueOf(o_refpoint));
                if(o_refpoint >= 0){
                    remainingPax(o_refpoint);
                }

            }
        });

        String[] d = dbQuery.getLineSegment(id, "DESC");
        destination.setMinValue(0);
        destination.setMaxValue(d.length-1);
        destination.setDisplayedValues(d);
        destination.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hasHotspot = "0";
                des_v = newVal;
                String[] d = dbQuery.getLineSegment(id, "DESC");
                String name = d[des_v];
                d_refpoint = dbQuery.getRefpoint(name);
                Log.wtf("value -d",d_refpoint +"");
                Log.wtf("paxtype",paxtype +"");
                getDiscount2();
                GlobalVariable.setDestination(String.valueOf(d_refpoint));
                //computeTicketPrice();
            }
        });

    }
    private void descending(final String id){
        Toast.makeText(frmTicket.this,"descending", Toast.LENGTH_LONG ).show();
        final DBQuery dbQuery = new DBQuery(frmTicket.this);
        //final String id = GlobalVariable.getLineid();
        Log.wtf("id", id);
        String[] o = dbQuery.getLineSegment(id, "DESC");
        origin.setMinValue(0);
        origin.setMaxValue(o.length-1);
        origin.setDisplayedValues(o);
        origin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hasHotspot = "0";
                origin_v = newVal;
                String[] o = dbQuery.getLineSegment(id, "DESC");
                String name = o[origin_v];
                o_refpoint = dbQuery.getRefpoint(name);
                Log.wtf("value -o",o_refpoint +"");
                getDiscount2();
                GlobalVariable.setOrigin(String.valueOf(o_refpoint));
                if(o_refpoint >= 0){
                    remainingPax(o_refpoint);
                }

            }
        });

        String[] d = dbQuery.getLineSegment(id, "ASC");
        destination.setMinValue(0);
        destination.setMaxValue(d.length-1);
        destination.setDisplayedValues(d);
        destination.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hasHotspot = "0";
                des_v = newVal;
                String[] d = dbQuery.getLineSegment(id, "ASC");
                String name = d[des_v];
                d_refpoint = dbQuery.getRefpoint(name);
                Log.wtf("value -d",d_refpoint +"");
                Log.wtf("paxtype",paxtype +"");
                getDiscount2();
                GlobalVariable.setDestination(String.valueOf(d_refpoint));
                //computeTicketPrice();
            }
        });


    }

    private void getDiscount(){

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            final String device = GlobalVariable.getPhoneName();
            final DBQuery dbQuery = new DBQuery(frmTicket.this);
            final String mode = dbQuery.getMode(device);
            final String line = GlobalVariable.d_lineid;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                 rb = (RadioButton) group.findViewById(checkedId);
                if( null != rb && checkedId > -1){
                    Toast.makeText(frmTicket.this, rb.getText(), Toast.LENGTH_LONG).show();
                    if(rb.getText().equals("Reg")){
                        String type = "1";
                        paxtype = String.valueOf(type);
                        //paxtype = dbQuery.getdiscountrate(type, line, mode);
                        GlobalVariable.setPaxtype(type);
                        Log.wtf("paxtype", paxtype);
                        computeTicketPrice(type);
                    }else if(rb.getText().equals("Senior")){
                        String type  = "2";
                        Log.wtf("discount", "type" + type + "line " + line + "mode" + mode);
                        GlobalVariable.setPaxtype(type);
                        computeTicketPrice(type);
                    }else if(rb.getText().equals("Student")){
                        String type = "3";
                        //paxtype = dbQuery.getdiscountrate(type, line, mode);
                        GlobalVariable.setPaxtype(type);
                        computeTicketPrice(type);
                    }else if(rb.getText().equals("Baggage")){
                        String type = "5";
//                       paxtype = dbQuery.getdiscountrate(type, line, mode);
                        GlobalVariable.setPaxtype(type);
                        computeTicketPrice(type);
                    }else {
                        Toast.makeText(frmTicket.this, "Please select a Passenger Type.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void computeTicketPrice(String type){

        if(hasHotspot.toString().equals("1")){
            Log.wtf("hotspot", "meron");
            d_refpoint = Integer.parseInt(hotspot_km);
        }

        DBQuery dbQuery = new DBQuery(frmTicket.this);
        String device = GlobalVariable.getPhoneName();
        String lineid  = dbQuery.getLinefrmDB();
        String mode = dbQuery.getMode(device);
        int km = o_refpoint - d_refpoint;
        String totalkm1 = Integer.toString(Math.abs(km)) ;
        Log.wtf("COMPUTE TICKET", type + lineid + mode);
        String test = dbQuery.getdiscountrate(type, lineid, mode);

        dbQuery.getTicketAmount(mode, lineid, d_refpoint+ "", o_refpoint + "");
        service_a = GlobalVariable.a_serviceid;
        amount_a = GlobalVariable.a_amount;
        minrange_a = GlobalVariable.a_minrange;
        minamount_a = GlobalVariable.a_minamount;

        totalkm2 = Double.parseDouble(totalkm1);
        double minrange = Double.parseDouble(GlobalVariable.a_minrange);
        double minamount = Double.parseDouble(GlobalVariable.a_minamount);
        double typee = Double.parseDouble(test);
        double amount = Double.parseDouble(amount_a);


        Log.wtf("totalkm", totalkm2 + " " + service_a + " " + amount_a + " " + minrange_a + " " +minamount_a );


        if(hasHotspot.toString().equals("1")){
            Log.wtf("hotspot", "meron");

                if (typee == 0.2) {
                    ticketprice = Double.parseDouble(fare);
                    double price = ticketprice * 0.2;
                    ticketprice = roundCase(ticketprice - price);
                }else{
                     ticketprice = Double.parseDouble(fare);
                }
            price.setText("Php" + ticketprice );
            kmpost.setText(String.valueOf(totalkm2)+ "KM");

        }else {
            if (totalkm2 <= minrange) {
                ticketprice = roundCase(minamount - (minamount * typee));
            } else {
                if (typee == 0.2) {
                    double price = totalkm2 * amount;
                    ticketprice = roundCase(price - (price * typee));
                } else {
                    ticketprice = roundCase(totalkm2 * amount);
                }
            }
        }
        price.setText("Php" + ticketprice );
        kmpost.setText(String.valueOf(totalkm2)+ "KM");

        //saveTicket(type);

    }

    private void  getDiscount2(){
        final DBQuery dbQuery = new DBQuery(frmTicket.this);
        String device = GlobalVariable.getPhoneName();
        String line = GlobalVariable.d_lineid;
        String mode = dbQuery.getMode(device);
        if(regular.isChecked()){
            Log.wtf("discount", "regular");
            String type = "1";
            paxtype = String.valueOf(type);
            GlobalVariable.setPaxtype(type);
            //paxtype = dbQuery.getdiscountrate(type, line, mode);
            Log.wtf("paxtype", paxtype);
            computeTicketPrice(type);
        }else if(senior.isChecked()){
            String type  = "2";
            Log.wtf("discount", "type" + type + "line " + line + "mode" + mode);
            //paxtype = dbQuery.getdiscountrate(type, line, mode);
            Log.wtf("paxtype", paxtype);
            GlobalVariable.setPaxtype(type);
            computeTicketPrice(type);
            Log.wtf("discount", "senior");
        }else if(student.isChecked()){
            String type ="3";
            GlobalVariable.setPaxtype(type);
            Log.wtf("discount", "student");
        }else if(baggage.isChecked()){
            String type ="5";
            GlobalVariable.setPaxtype(type);
            Log.wtf("discount", "baggage");
        }



    }

    private double roundCase(double amount){
        DBQuery dbQuery = new DBQuery(frmTicket.this);
        String device = GlobalVariable.getPhoneName();
        String modeid = dbQuery.getMode(device);

        double mode = Double.parseDouble(modeid);
        if(mode == 1){
            return roundTrue(amount / 0.5) * 0.5;
        }else{
            return roundTrue(amount);
        }
    }

    private double roundTrue(double amt){
        double dec = amt - Math.floor(amt);
        if (dec >= 0.5){
            return Math.ceil(amt);
        }else {
            return Math.floor(amt);
        }
    }

    private void saveTicket(String type) {
        String device = GlobalVariable.getPhoneName();
        DBQuery dbQuery = new DBQuery(frmTicket.this);
        dba = DBAccess.getInstance(frmTicket.this);
        String tkid = dbQuery.getLastTicket(device);
        int last = Integer.parseInt(tkid);
        String t = "1";
        int ticket = Integer.parseInt(t);
        int lastticket = last + ticket;

        String trip = dbQuery.getTripId(device);
        String serviceID = GlobalVariable.a_serviceid;
        String km = String.valueOf(totalkm2);
        String amount = amount_a;
        String price = String.valueOf(ticketprice);
        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        String date = dtstartTime;
        String o = String.valueOf(o_refpoint);
        String d = String.valueOf(d_refpoint);
        String line = GlobalVariable.getLineid();

        String tripid = GlobalVariable.getLasttrip();
        String segmentNum = String.valueOf(dbQuery.getSegmentNum(tripid));


        sqlQuery = "INSERT INTO `TICKET` (ID, TRIPID ,CUSTOMERID, SERVICEID, QTY, UOMID, UNITPRICE, NETAMOUNT, PAYMENTTYPEID, DATETIMESTAMP, PRINTCOPIES, " +
                "TAG, FROMREFPOINT, TOREFPOINT, LINEID) " +
                " VALUES ( '" + lastticket + "', '" +
                trip + "', '" +
                type + "', '" +
                serviceID + "', '" +
                km + "',' 3','" +
                amount + "',' " +
                price + "','1','" +
                date + "','1','" +
                segmentNum + "',' " +
                o + "',' " +
                d + "',' " +
                line + "' " +
                " ); ";
        Log.wtf("logged sql", sqlQuery);

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmTicket.this, "Can't insert data to database!. save ticket", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(frmTicket.this, "Saved ticket", Toast.LENGTH_LONG).show();
            updateLastTicket();

        }
    }

    public void updateLastTicket(){
        DBQuery dbQuery = new DBQuery(frmTicket.this);
        String device = GlobalVariable.getPhoneName();
        String tkid = dbQuery.getLastTicket(device);
        int last = Integer.parseInt(tkid);
        String t = "1";
        int ticket = Integer.parseInt(t);
        //String ticket = String.valueOf(lticket);
        int lastticket = last + ticket;
        GlobalVariable.setLastticket(lastticket +"");

        sqlQuery = "UPDATE DEVICEDATA SET VALUE = '" + lastticket + "' WHERE DEVICENAME = '" + device + "' AND KEY = 'LASTTICKETID'";

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmTicket.this, "Can't insert data to database!.", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf("update lastticket", "update lasttripid");
        }

    }

    public void printTicket(String typename){
        DBQuery dbQuery = new DBQuery(frmTicket.this);
        String devicename = GlobalVariable.getPhoneName();
        String c = dbQuery.getCompanyName(devicename);
        String l = dbQuery.getLastTicket(devicename);

        Calendar today = Calendar.getInstance();
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);




        String companyName = c + "\n";
        String tin = "" + "\n";
        String mn = GlobalVariable.d_machinenum + "\n";
        String accdtn = "Accdtn# 000-00000000 " + "\n \n";
        String p = "Passenger - Ticket" + "\n \n";
        String device = GlobalVariable.getPhoneName() + "-" + l + "\n";
        String date = "Date:" + dtstartTime + "\n";
        String vehicle = "Vehicle:" + GlobalVariable.d_busname;
        String price = "Amount Due:" + ticketprice + "pesos" + "\n";
        String o = "From:" + o_refpoint + "\n";
        String d = "To:" + d_refpoint + "\n";
        String type = "Type:" + typename + "\n";
        String driver = "Driver:" + GlobalVariable.getName_driver() + "\n";
        String cond = "Conductor:" + GlobalVariable.getName_conductor()+ "\n";


        Log.wtf("Print Ticket", companyName + tin + mn + accdtn + p + device + date + vehicle + price +
        o + d + type + driver + cond);


        callBluetooth(companyName + tin + mn + accdtn + p + device + date + vehicle + price +
                o + d + type + driver + cond);


    }

    public void areaOfClosing(){
        DBQuery dbQuery = new DBQuery(frmTicket.this);

        String tripid = GlobalVariable.getLasttrip();
        String p = String.valueOf(dbQuery.getRemainingPax(tripid));
        double pax = Double.parseDouble(p);
        if(pax > 0){
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.menuaoc, null);
            final EditText passengers = (EditText) alertLayout.findViewById(R.id.aoc);

            AlertDialog.Builder builder = new AlertDialog.Builder(frmTicket.this);
            builder.setTitle("Area of Closing");
            builder.setMessage("Passengers");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int pp = Integer.parseInt(passengers.getText().toString());
                    printaoc(pp);
                }
            });
            builder.setView(alertLayout);
            builder.show();


        }
    }

    public void printaoc(int count){
        Date dtTemp = new Date(DateFormat.getDateTimeInstance().format(new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dtstartTime = formatter.format(dtTemp);
        DBQuery dbQuery = new DBQuery(frmTicket.this);


        String trip = GlobalVariable.getLasttrip();
        String devicename = GlobalVariable.getPhoneName();
        String datetime = dtstartTime;
        String dispatcher = dbQuery.getEmployeeID(GlobalVariable.getName_dispatcher());
        String origin = GlobalVariable.getOrigin();
        String lastticket = GlobalVariable.getLastticket();
        String d = dbQuery.getDirectionValue(devicename);


        sqlQuery = "INSERT INTO `TRIPINSPECTION` (TRIPID, DEVICENAME, DATETIMESTAMP, EMPLOYEEID, ATTRIBUTEID, QTY, KMPOST, PCOUNT, LINESEGMENT,DIRECTION, BCOUNT, TICKETID) " +
                " VALUES ( '"+ trip +"', '" +
                devicename + "', '" +
                datetime + "', '" +
                dispatcher+ "', '5' , '0', '"+
                origin+ "','0','1','"+d+"','0',' " +
                lastticket + "' " +
                " ); ";
        Log.wtf("logged sql",sqlQuery);

        if (!dba.executeQuery(sqlQuery)) {
            Toast.makeText(frmTicket.this, "Can't insert data to database!.aoc", Toast.LENGTH_SHORT).show();
        }else{
            Log.wtf("","save tripinspecion aoc");

            String title = "Area of Closing" +"\n";
            String date = "Date/Time:" + datetime + "\n";
            String km = "Post KM:" + origin  + "\n";
            String ser  = "Serial Number:" + dbQuery.getLastTicketinTicket(GlobalVariable.d_lineid) + "\n";
            String p =  "Pax:" + String.valueOf(count) + "\n";
            String remain = "Add Pax" +  dbQuery.getRemainingPax(trip)  + "\n";
            int r = dbQuery.getRemainingPax(trip);
            int a = count;
            int aa = r;
            int aaa = a-aa;
            String ad = "Add Pax" + String.valueOf(aaa) + "\n";
            String vehicle = "Vehicle: " + GlobalVariable.d_busname + "\n";


            Log.wtf("AOC", title + date + km + ser + p + remain + ad + vehicle);


        }
    }

    public void grossCollect(){

        DBQuery dbQuery = new DBQuery(frmTicket.this);
        dbQuery.getGrossCollection(GlobalVariable.getLasttrip());
        String sum = GlobalVariable.getGross_sum();
        String tag = GlobalVariable.getGross_tag();

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.menugross, null);
        final TextView g = (TextView) alertLayout.findViewById(R.id.gross);
        final TextView p = (TextView) alertLayout.findViewById(R.id.trip);
        p.setText("Trip"+tag+":");
        g.setText(sum);

        AlertDialog.Builder builder = new AlertDialog.Builder(frmTicket.this);
        builder.setTitle("Gross Collection");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

            }
        });
        builder.setView(alertLayout);
        builder.show();




    }

    public void hotspotPax(int origin, final String line){
        final DBQuery dbQuery = new DBQuery(frmTicket.this);
        if(origin == 0) {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.modal_hotspot, null);
            final EditText km = (EditText) alertLayout.findViewById(R.id.h_km);

            AlertDialog.Builder builder = new AlertDialog.Builder(frmTicket.this);
            builder.setTitle("Hotspot");
            builder.setView(alertLayout);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hotspot_km = km.getText().toString();
                    fare = dbQuery.getHotspotFare(line, hotspot_km);
                    getDiscount2();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }else{
            Toast.makeText(frmTicket.this, "There's no hotspot available in the current origin.", Toast.LENGTH_LONG).show();
        }

    }

    public void remainingPax(int origin){
        DBQuery dbQuery = new DBQuery(frmTicket.this);
        String lineid = GlobalVariable.getLineid();
        String trip = GlobalVariable.getLasttrip();
        int less = dbQuery.getRemainingPaxOrigin(lineid, origin);
        int pax = dbQuery.getRemainingPax(trip);
        int remain = pax - less;

        tremaining.setText(String.valueOf(remain));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            byte [] send ;
            try {
                send = message.getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }
            mService.write(send);
            Log.wtf("bluetooth", "connected" + send + "" );
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
            if (mService == null);

        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        mService = new BluetoothService(this, bluetoothHandler);
//    }

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
                            Toast.makeText(frmTicket.this, R.string.title_connected_to   + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(frmTicket.this, R.string.title_connecting, Toast.LENGTH_LONG).show();
                            //  mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Log.wtf("BluetoothService","not connected!");
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


}
