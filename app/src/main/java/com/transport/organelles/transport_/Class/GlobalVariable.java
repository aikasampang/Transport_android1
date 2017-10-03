package com.transport.organelles.transport_.Class;

import android.bluetooth.BluetoothAdapter;

import com.transport.organelles.transport_.R;

import java.util.ArrayList;

/**
 * Created by Organelles on 6/8/2017.
 */

public class GlobalVariable {

    //SQLite Database Variables
    public static String DB_Path = "";
    public static String DB_Name = "transport_db.db";
    public static String dbBackupName = "mapaDB";
    public static final String  DATABASE_FILE_PATH = "/sdcard";
    public static int DB_Version = 1;
    public static String sqlException;

    //Server Variables
    public static String serverIP = "";
    public static String serverUser = "";
    public static String serverPass = "";
    public static String serverPhoneNum = "";
    public static String serverDatabase = "";
    public static String serverNetworkSSID = "";
    public static String serverNetworkPass  = "";

    //Device Variables
    public static String deviceID = "";
    public static String deviceID_secured = "";
    public static String deviceName = "";
    public static int lastDispatch = 0;
    public static String d_companyName = "";
    public static String d_machinenum = "";


    public static String[] moduleTXT = new String[] {
            "Dispatch",
            "Ticketing",
            "Inspector",
            "Partial",
            "Reverse",
            "Ingress"
    };

    public static int[] moduleIMG = new int[] {
            R.drawable.dispatch,
            R.drawable.ticket,
            R.drawable.inspector,
            R.drawable.partial,
            R.drawable.reverse,
            R.drawable.ingress,

    };

    //Module from user database
    public static String[] userModuleTXT;
    public static int[] userModuleIMG;

    //Temporary list of array
    public static ArrayList<String> TempuserModuleTxt;
    public static ArrayList<Integer> TempuserModuleIMG;



    //Dispatch
    public static String line_id = "";
    public static String d_pass = "";
    public static String d_lineid = "";
    public static String d_modeid = "";
    public static String d_lastticketid = "";
    public static String d_lasttripid = "";
    public static String d_remarks = "";
    public static String d_direction = "";
    public static String d_direct="";
    public static String d_busname = "";
    public static String d_drivername ="";
    public static String d_condname = "";
    public static String d_dispatchername = "";

    public static String getD_remarks() {
        return d_remarks;
    }

    public static void setD_remarks(String d_remarks) {
        GlobalVariable.d_remarks = d_remarks;
    }

    //Ticket
    public static String a_serviceid= "";
    public static String a_amount= "";
    public static String a_minrange= "";
    public static String a_minamount= "";



    //Employee
    public static String e_dispatcherId = "";
    public static String e_driverId = "";
    public static String e_conductor = "";





    public static String getPhoneName()
    {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        return deviceName;
    }

    public static String getPhoneAddress()
    {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getAddress();
        return deviceName;
    }


    public static String gross_sum;
    public static String gross_tag;

    public static String getGross_sum() {
        return gross_sum;
    }

    public static void setGross_sum(String gross_sum) {
        GlobalVariable.gross_sum = gross_sum;
    }

    public static String getGross_tag() {
        return gross_tag;
    }

    public static void setGross_tag(String gross_tag) {
        GlobalVariable.gross_tag = gross_tag;
    }

    public static String name_dispatcher;
    public static String name_bus;
    public static String name_driver;
    public static String name_conductor;

    public static String getDispatch_date() {
        return dispatch_date;
    }

    public static void setDispatch_date(String dispatch_date) {
        GlobalVariable.dispatch_date = dispatch_date;
    }

    public static String dispatch_date;

    public static String getName_dispatcher() {
        return name_dispatcher;
    }

    public static void setName_dispatcher(String name_dispatcher) {
        GlobalVariable.name_dispatcher = name_dispatcher;
    }

    public static String getName_bus() {
        return name_bus;
    }

    public static void setName_bus(String name_bus) {
        GlobalVariable.name_bus = name_bus;
    }

    public static String getName_driver() {
        return name_driver;
    }

    public static void setName_driver(String name_driver) {
        GlobalVariable.name_driver = name_driver;
    }

    public static String getName_conductor() {
        return name_conductor;
    }

    public static void setName_conductor(String name_conductor) {
        GlobalVariable.name_conductor = name_conductor;
    }

    public static String lineid;
    public static String modeid;
    public static String origin;
    public static String destination;

    public static String getLineid() {
        return lineid;
    }

    public static void setLineid(String lineid) {
        GlobalVariable.lineid = lineid;
    }

    public static String getModeid() {
        return modeid;
    }

    public static void setModeid(String modeid) {
        GlobalVariable.modeid = modeid;
    }

    public static String getOrigin() {
        return origin;
    }

    public static void setOrigin(String origin) {
        GlobalVariable.origin = origin;
    }

    public static String getDestination() {
        return destination;
    }

    public static void setDestination(String destination) {
        GlobalVariable.destination = destination;
    }

    public static String lastticket;
    public static String lasttrip;

    public static String getLastticket() {
        return lastticket;
    }

    public static void setLastticket(String lastticket) {
        GlobalVariable.lastticket = lastticket;
    }

    public static String getLasttrip() {
        return lasttrip;
    }

    public static void setLasttrip(String lasttrip) {
        GlobalVariable.lasttrip = lasttrip;
    }

    public static String paxtype;

    public static String getPaxtype() {
        return paxtype;
    }

    public static void setPaxtype(String paxtype) {
        GlobalVariable.paxtype = paxtype;
    }

    public static String PaxTypeName;

    public static String getPaxTypeName() {
        return PaxTypeName;
    }

    public static void setPaxTypeName(String paxTypeName) {
        PaxTypeName = paxTypeName;
    }

    public static String operationtype;

    public static String getOperationtype() {
        return operationtype;
    }

    public static void setOperationtype(String operationtype) {
        GlobalVariable.operationtype = operationtype;
    }
    public static String directionMax;
    public static String directionMin;

    public static String getDirectionMax() {
        return directionMax;
    }

    public static void setDirectionMax(String directionMax) {
        GlobalVariable.directionMax = directionMax;
    }

    public static String getDirectionMin() {
        return directionMin;
    }

    public static void setDirectionMin(String directionMin) {
        GlobalVariable.directionMin = directionMin;
    }

    public static String min_reverse;
    public static String max_reverse;

    public static String getMin_reverse() {
        return min_reverse;
    }

    public static void setMin_reverse(String min_reverse) {
        GlobalVariable.min_reverse = min_reverse;
    }

    public static String getMax_reverse() {
        return max_reverse;
    }

    public static void setMax_reverse(String max_reverse) {
        GlobalVariable.max_reverse = max_reverse;
    }

    public static String min_linesegment;
    public static String max_linesegment;

    public static String getMax_linesegment() {
        return max_linesegment;
    }

    public static void setMax_linesegment(String max_linesegment) {
        GlobalVariable.max_linesegment = max_linesegment;
    }

    public static String getMin_linesegment() {
        return min_linesegment;
    }

    public static void setMin_linesegment(String min_linesegment) {
        GlobalVariable.min_linesegment = min_linesegment;
    }

    public static String direction;

    public static String getDirection() {
        return direction;
    }

    public static void setDirection(String direction) {
        GlobalVariable.direction = direction;
    }

    public static String ing_linesegment;
    public static String ing_kmpost;
    public static String ing_direction;
    public static String ing_qty;

    public static String getIng_linesegment() {
        return ing_linesegment;
    }

    public static void setIng_linesegment(String ing_linesegment) {
        GlobalVariable.ing_linesegment = ing_linesegment;
    }

    public static String getIng_kmpost() {
        return ing_kmpost;
    }

    public static void setIng_kmpost(String ing_kmpost) {
        GlobalVariable.ing_kmpost = ing_kmpost;
    }

    public static String getIng_direction() {
        return ing_direction;
    }

    public static void setIng_direction(String ing_direction) {
        GlobalVariable.ing_direction = ing_direction;
    }

    public static String getIng_qty() {
        return ing_qty;
    }

    public static void setIng_qty(String ing_qty) {
        GlobalVariable.ing_qty = ing_qty;
    }

    public static String current_dri;
    public static String current_cond;

    public static String getCurrent_cond() {
        return current_cond;
    }

    public static void setCurrent_cond(String current_cond) {
        GlobalVariable.current_cond = current_cond;
    }

    public static String getCurrent_dri() {
        return current_dri;
    }

    public static void setCurrent_dri(String current_dri) {
        GlobalVariable.current_dri = current_dri;
    }

    public static String condpercent_amount;
    public static String condpercent_remarks;


    public static String getCondpercent_amount() {
        return condpercent_amount;
    }

    public static void setCondpercent_amount(String condpercent_amount) {
        GlobalVariable.condpercent_amount = condpercent_amount;
    }

    public static String getCondpercent_remarks() {
        return condpercent_remarks;
    }

    public static void setCondpercent_remarks(String condpercent_remarks) {
        GlobalVariable.condpercent_remarks = condpercent_remarks;
    }

    public static String dripercent_amount;
    public static String dripercent_remarks;

    public static String getDripercent_remarks() {
        return dripercent_remarks;
    }

    public static void setDripercent_remarks(String dripercent_remarks) {
        GlobalVariable.dripercent_remarks = dripercent_remarks;
    }

    public static String getDripercent_amount() {
        return dripercent_amount;
    }

    public static void setDripercent_amount(String dripercent_amount) {
        GlobalVariable.dripercent_amount = dripercent_amount;
    }

    public static String line_name;

    public static String getLine_name() {
        return line_name;
    }

    public static void setLine_name(String line_name) {
        GlobalVariable.line_name = line_name;
    }

    public static String dri_employeeID;
    public static String cond_employeeID;

    public static String getDri_employeeID() {
        return dri_employeeID;
    }

    public static void setDri_employeeID(String dri_employeeID) {
        GlobalVariable.dri_employeeID = dri_employeeID;
    }

    public static String getCond_employeeID() {
        return cond_employeeID;
    }

    public static void setCond_employeeID(String cond_employeeID) {
        GlobalVariable.cond_employeeID = cond_employeeID;
    }

    public static String dri_tag;
    public static String cond_tag;

    public static String getDri_tag() {
        return dri_tag;
    }

    public static void setDri_tag(String dri_tag) {
        GlobalVariable.dri_tag = dri_tag;
    }

    public static String getCond_tag() {
        return cond_tag;
    }

    public static void setCond_tag(String cond_tag) {
        GlobalVariable.cond_tag = cond_tag;
    }

    public static String cashbond_id;
    public static String cashbond_remarks;

    public static String getCashbond_id() {
        return cashbond_id;
    }

    public static void setCashbond_id(String cashbond_id) {
        GlobalVariable.cashbond_id = cashbond_id;
    }

    public static String getCashbond_remarks() {
        return cashbond_remarks;
    }

    public static void setCashbond_remarks(String cashbond_remarks) {
        GlobalVariable.cashbond_remarks = cashbond_remarks;
    }

    public static String cashierID;

    public static String getCashierID() {
        return cashierID;
    }

    public static void setCashierID(String cashierID) {
        GlobalVariable.cashierID = cashierID;
    }



}
