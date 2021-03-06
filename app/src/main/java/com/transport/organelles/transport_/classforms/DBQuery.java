package com.transport.organelles.transport_.classforms;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Organelles on 6/14/2017.
 */

public class DBQuery extends DBObject {

    public DBQuery(Context context) {
        super(context);

    }


    /*getdispatch*/

    public String getDirectionfromDB(){

        String sql = "select VALUE from DEVICEDATA where KEY = 'DIRECTION'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String directionDB = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        GlobalVariable.setDirectionfromDB(directionDB );
        return directionDB ;
    }

    public String getLinefrmDB(){

        String sql = "select VALUE from DEVICEDATA where KEY = 'LINE'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String lineDB = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        GlobalVariable.setLinefromDB(lineDB );
        return lineDB;
    }

    public String getLastTicket(){

        String sql = "select VALUE from DEVICEDATA where KEY = 'LASTTRIPID'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String lastTrip = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        GlobalVariable.setLasttrip(lastTrip);
        cursor.close();

        return lastTrip;

    }


    public String[] getName(int emprole) {

        String query = "Select * from Employee where EMPLOYEEROLEID = '" +emprole+ "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> spinnerContent = new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                spinnerContent.add(name);

            }while(cursor.moveToNext());
        }
        cursor.close();

        String[] allSpinner = new String[spinnerContent.size()];
        allSpinner = spinnerContent.toArray(allSpinner);

        return allSpinner;
    }

    public String getIDfromName(String name){

        String query= "select ID from EMPLOYEE where NAME ='"+ name +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query,null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
        cursor.close();
        return id;
    }

    public String[] getNames(){
        {

            String query = "Select * from Employee where EMPLOYEEROLEID in (3,4)";
            Cursor cursor = this.getDbConnection().rawQuery(query, null);
            ArrayList<String> spinnerContent = new ArrayList<String>();
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));

                    spinnerContent.add(name);
                } while (cursor.moveToNext());
            }
            cursor.close();

            String[] allSpinner = new String[spinnerContent.size()];
            allSpinner = spinnerContent.toArray(allSpinner);

            return allSpinner;
        }

    }

    public String[] getBus() {

        String query = "Select DESCRIPTION from RESOURCE";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> spinnerContent = new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                String word = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
                spinnerContent.add(word);
            }while(cursor.moveToNext());
        }
        cursor.close();

        String[] allSpinner = new String[spinnerContent.size()];
        allSpinner = spinnerContent.toArray(allSpinner);

        return allSpinner;
    }

    public String[] getLine() {
        String query = "Select * from v_line";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> spinnerContent = new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                String ROUTE = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                spinnerContent.add(ROUTE);
                // spinnerContent.add(password);
            }while(cursor.moveToNext());
        }
        cursor.close();

        String[] allSpinner = new String[spinnerContent.size()];
        allSpinner = spinnerContent.toArray(allSpinner);

        return allSpinner;
    }

    public String getIDfromLine(String NAME) {

        String query = "Select ID from LINE where NAME = '" + NAME + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
        cursor.close();
        return id;
    }

    public String[] getDirection(String line) {

        String query = "Select NAME, REMARKS from v_direction where LINE = '"+ line +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> spinnerContent = new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                String direction = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                String remarks = cursor.getString(cursor.getColumnIndexOrThrow("REMARKS"));
                GlobalVariable.setD_remarks(remarks);
                spinnerContent.add(direction);
                // spinnerContent.add(password);
            }while(cursor.moveToNext());
        }
        cursor.close();

        String[] allSpinner = new String[spinnerContent.size()];
        allSpinner = spinnerContent.toArray(allSpinner);

        return allSpinner;
    }

    public void getLineRemarks(String line){

        String sql = "select REMARKS from LINE where ID ='"+ line+"'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        if(cursor.moveToFirst() && cursor != null){
            String remarks = cursor.getString(cursor.getColumnIndex("REMARKS"));
            GlobalVariable.setD_remarks(remarks);
            cursor.close();
        }
    }

    public String[] getMode(){
        String query = "Select * from v_mode";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> spinnerContent = new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                String MODE = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                spinnerContent.add(MODE);
                // spinnerContent.add(password);
            }while(cursor.moveToNext());
        }
        cursor.close();

        String[] allSpinner = new String[spinnerContent.size()];
        allSpinner = spinnerContent.toArray(allSpinner);

        return allSpinner;
    }

    public String getModeName(String modeId){

        String sql = "Select NAME from MODE where ID  = '" + modeId + "'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String MODE = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        cursor.close();
        return MODE;
    }

    public String getPassword(String name){

        String query = "Select * from EMPLOYEE where NAME = '" + name + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndexOrThrow("PIN"));
        cursor.close();
        return password;
    }

    public String getIDfromResource(String NAME){

        String query = "Select ID from RESOURCE where DESCRIPTION = '" + NAME + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
        cursor.close();
        return id;
    }

    public String getIDfromMode(String NAME){

        String query = "Select ID from MODE where NAME = '" + NAME + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
        cursor.close();
        return id;
    }

    public String[] getLineSegment(String lineid, String direction){

        if(direction.toString().equals("ASC")) {
            String query = "Select * from v_linesegment where LINEID ='" + lineid + "' ORDER BY REFPOINT ASC";
            Cursor cursor = this.getDbConnection().rawQuery(query, null);
            final ArrayList<String> spinnerContent = new ArrayList<String>();
            if(cursor.moveToFirst()){
                do{
                    String segment = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                    spinnerContent.add(segment);
                    // spinnerContent.add(password);
                }while(cursor.moveToNext());
            }
            cursor.close();
            String[] allSpinner = new String[spinnerContent.size()];
            allSpinner = spinnerContent.toArray(allSpinner);

            return allSpinner;
        }else {
            String query = "Select * from v_linesegment where LINEID ='" + lineid + "' ORDER BY REFPOINT DESC";
            Cursor cursor = this.getDbConnection().rawQuery(query, null);
            final ArrayList<String> spinnerContent = new ArrayList<String>();
            if (cursor.moveToFirst()) {
                do {
                    String segment = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                    spinnerContent.add(segment);
                    // spinnerContent.add(password);
                } while (cursor.moveToNext());
            }
            cursor.close();
            String[] allSpinner = new String[spinnerContent.size()];
            allSpinner = spinnerContent.toArray(allSpinner);

            return allSpinner;
        }
    }

    public String getdiscountrate(String passtype, String lineid, String modeid){

        String query = "select AMOUNT from LINEDISCOUNT where OPERATIONTYPEID='1' AND LINEID ='"+ lineid +"'" +
                "AND MODEID = '"+ modeid+ "' AND CUSTOMERTYPEID='" + passtype + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String AMOUNT = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        cursor.close();
        return AMOUNT;
    }

    public String[] getTicketAmount(String mode, String line, String kmto, String kmfrom) {


        String query = "select SERVICEID, AMOUNT, MINRANGE, MINAMOUNT from SERVICEMATRIX where MODEID ='" + mode + "' AND LINEID = '" + line + "' ORDER BY FROMLINESEGMENTID";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        final ArrayList<String> spinnerContent = new ArrayList<String>();
        int row = cursor.getCount();
        if (row == 1 ) {
            if (cursor.moveToFirst()) {
                do {
                    String serviceid = cursor.getString(cursor.getColumnIndexOrThrow("SERVICEID"));
                    String amount = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
                    String minrange = cursor.getString(cursor.getColumnIndexOrThrow("MINRANGE"));
                    String minamount = cursor.getString(cursor.getColumnIndexOrThrow("MINAMOUNT"));
                    GlobalVariable.a_serviceid = serviceid;
                    GlobalVariable.a_amount = amount;
                    GlobalVariable.a_minrange = minrange;
                    GlobalVariable.a_minamount = minamount;
                    // spinnerContent.add(password);
                } while (cursor.moveToNext());
            }

        } else {

            String query2 = "select SERVICEID, AMOUNT, MINRANGE, MINAMOUNT from SERVICEMATRIX where MODEID ='" + mode + "' AND LINEID = '" + line + "' AND " +
                    "(" + kmfrom + " BETWEEN FROMLINESEGMENTID AND TOLINESEGMENTID) AND (" + kmto + " BETWEEN FROMLINESEGMENTID AND TOLINESEGMENTID )";
            Cursor cursor1 = this.getDbConnection().rawQuery(query2, null);
            cursor1.moveToFirst();
            String serviceid = cursor.getString(cursor.getColumnIndexOrThrow("SERVICEID"));
            String amount = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
            String minrange = cursor.getString(cursor.getColumnIndexOrThrow("MINRANGE"));
            String minamount = cursor.getString(cursor.getColumnIndexOrThrow("MINAMOUNT"));
            cursor1.close();
            GlobalVariable.a_serviceid = serviceid;
            GlobalVariable.a_amount = amount;
            GlobalVariable.a_minrange = minrange;
            GlobalVariable.a_minamount = minamount;

        }
        String[] allSpinner = new String[spinnerContent.size()];
        allSpinner = spinnerContent.toArray(allSpinner);

        return allSpinner;

    }

    public int getRefpoint(String name){

        String query = "select REFPOINT from LINESEGMENT where NAME='" + name + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("REFPOINT"));
        cursor.close();
        Log.wtf("id", id+ "");
        return id;
    }

    public String getDeviceName(String name){

        String query = "Select DEVICENAME from DEVICEDATA where DEVICENAME = '" + name + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        String n = "-1";
        if(cursor != null & cursor.getCount() >0){
            cursor.moveToFirst();
             n = cursor.getString(cursor.getColumnIndexOrThrow("DEVICENAME"));
            cursor.close();
        }
        Log.wtf("cursor", n);
            return n;
    }

    public String getlastTicket(String name){

        String query = "Select VALUE from DEVICEDATA where DEVICENAME = '" + name + "' and KEY = 'LASTTICKETID'" +
                "UNION ALL " +
                "Select VALUE from DEVICEDATA where DEVICENAME = '" + name + "' and KEY = 'LASTTRIPID'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String lastticketid = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        Log.wtf("ticket", lastticketid);
        cursor.moveToNext();
        String lasttripid= cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        Log.wtf("trip", lasttripid);
        cursor.close();
        GlobalVariable.d_lastticketid = lastticketid;
        GlobalVariable.d_lasttripid = lasttripid;
        //GlobalVariable.setLastticket(lastticketid);
        //GlobalVariable.setLasttrip(lasttripid);

        return lastticketid;
    }

    public String getEmployeeID(String name){

        String query = "Select ID from EMPLOYEE where NAME = '" + name + "'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
        cursor.close();
        return id;
    }

    public String getOrigin(String line){
        String query = "Select REFPOINT from LINESEGMENT where LINEID = '" + line + "' ORDER BY REFPOINT LIMIT 1";

        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String origin = cursor.getString(cursor.getColumnIndexOrThrow("REFPOINT"));
        cursor.close();
        return origin ;
    }

    public String getTripId(String name){

        String query = "Select VALUE from DEVICEDATA where DEVICENAME = '"+ name +"' AND KEY = 'LASTTRIPID'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String n = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        Log.wtf("tripid", n);

//        String n = "-1";
//        if(cursor != null & cursor.getCount() >0){
//            cursor.moveToFirst();
//            n = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
//            cursor.close();
//            return n;
//        }
//        Log.wtf("cursor", n);
        return n;
    }

    public String getTicketCount(String id){

        String query = "Select * from TICKET where TRIPID = '"+ id +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        String count = cursor.getCount() + "";
        String none = "-1";
        if(cursor != null & cursor.getCount() > 0){
            return count;
        }
        return none;

    }

    public String getLine(String ID){

        String query = "Select LINE from TRIP  where ID = '"+ ID +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String n = cursor.getString(cursor.getColumnIndexOrThrow("LINE"));
        Log.wtf("lineid", n);
        return n;
    }

    public String getMode(String name){
        String query = "Select MODEID from TRIP where DEVICENAME = '"+ name +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String mode = cursor.getString(cursor.getColumnIndexOrThrow("MODEID"));
        return mode;


    }

    public String getLastTicket(String name){

        String query = "Select VALUE from DEVICEDATA where DEVICENAME = '"+ name +"' AND KEY = 'LASTTICKETID'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String n = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        Log.wtf("lastticket", n);

        return n;

    }

    public int getSegmentNum(String id){

        String query = "select count(TRIPID) from TRIPREVERSE where TRIPID = '"+ id +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        int count = cursor.getCount() ;
        Log.wtf("count",  count+ "");
            if(count == 0){
                return 1;
            }else if(cursor == null){
                return 1;
            }else{
                return count + 1;
            }
    }

    public String getCompanyName(String name){

        String query = "select VALUE from DEVICEDATA where KEY = 'INGRESSPOINT' AND DEVICENAME ='"+name+"' " +
                "UNION ALL " +
                "select VALUE from DEVICEDATA where KEY ='MACHINENUMER' AND DEVICENAME = '"+name+"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String company = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        Log.wtf("company", company);
        cursor.moveToNext();
        String number = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        Log.wtf("machinenum", number);
        cursor.close();
        GlobalVariable.d_companyName = company;
        GlobalVariable.d_machinenum = number;
        return company;
    }

    public int getRemainingPax(String id){
        try{
            String query = "select tk.*, t.LINE, (case when torefpoint>fromrefpoint then 1 else -1 end) as direction, coalesce(ct.TAG,0) as TAG2 from TICKET \n" +
                    "tk left join TRIP t on t.ID=tk.TRIPID left join CUSTOMER c on c.ID=tk.CUSTOMERID left join CUSTOMERTYPE ct on ct.ID=c.ID where \n" +
                    "TRIPID ='"+id+"' order by ID ";
            Cursor cursor = this.getDbConnection().rawQuery(query,null);
            if(cursor.getCount() > 0){
                Log.wtf("get count", cursor.getCount() + "");
                return cursor.getCount();
            }else{
                Log.wtf("get count", "this row is empty");
                return 0;
            }
        }catch (Exception e){
            Log.wtf("exception", "error:" + e);
        }
        return 0;

    }

    public String getDirectionValue(String name){

        String query = "Select VALUE from DEVICEDATA where DEVICENAME = '"+ name +"' AND KEY = 'DIRECTION'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String n = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
        Log.wtf("DIRECTION", n);
        return n;

    }

    public String getLastTicketinTicket(String id){

        String query = "select coalesce( max(ID), 0)as LAST from TICKET where TRIPID='"+ id +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String ticket = cursor.getString(cursor.getColumnIndexOrThrow("LAST"));
        cursor.close();
        return ticket;

    }

    public String getGrossCollection(String id){
        Log.wtf("gross", id);

        String query = "select coalesce(sum(NETAMOUNT),0) as SUM, TAG from TICKET where TRIPID='"+id+"' group by tag";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
            String sum = cursor.getString(cursor.getColumnIndexOrThrow("SUM"));
            String tag  = cursor.getString(cursor.getColumnIndexOrThrow("TAG"));
            GlobalVariable.setGross_sum(sum);
            GlobalVariable.setGross_tag(tag);
        return sum;
    }

    public String getDirectionMinMax(String direction){

        String query = "select min(REFPOINT)as MIN, max(REFPOINT)as MAX from LINESEGMENT where LINEID='"+ direction+"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String min = cursor.getString(cursor.getColumnIndexOrThrow("MIN"));
        String max = cursor.getString(cursor.getColumnIndexOrThrow("MAX"));
        GlobalVariable.setDirectionMin(min);
        GlobalVariable.setDirectionMax(max);
        return min;
    }

    public String getRemainingPaxControl(String id, String km){
        try{
            String query = "select tk.*, t.LINE, (case when torefpoint>fromrefpoint then 1 else -1 end) as direction, coalesce(ct.TAG,0) as TAG2 from TICKET \n" +
                    "tk left join TRIP t on t.ID=tk.TRIPID left join CUSTOMER c on c.ID=tk.CUSTOMERID left join CUSTOMERTYPE ct on ct.ID=c.ID where \n" +
                    "TRIPID ='"+id+"'and torefpoint >= '"+km+"' order by ID ";
            Cursor cursor = this.getDbConnection().rawQuery(query,null);
            if(cursor.getCount() > 0){
                Log.wtf("controlled", cursor.getCount() + "");
                return cursor.getCount() +"";
            }else{
                Log.wtf("controlled", "this row is empty");
                return "0";
            }
        }catch (Exception e){
            Log.wtf("exception", "error:" + e);
        }
        return "0";

    }

    public String getTicketSales(String tripid){

        String sql = "SELECT SUM(netamount)as TOTALTICKETS FROM Ticket WHERE TripID ='"+ tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String total = cursor.getString(cursor.getColumnIndexOrThrow("TOTALTICKETS"));
        cursor.close();
        return total;
    }

    public String getDataDeviceCompany(String name){


        String query = "Select VALUE from DEVICEDATA where DEVICENAME = '"+ name +"' AND KEY = 'CLIENTNAME'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String n = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));Log.wtf("DIRECTION", n);
        return n;

    }

    public String getOriginName(String line){
        String query = "Select NAME from LINESEGMENT where LINEID = '" + line + "' ORDER BY REFPOINT LIMIT 1";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String origin = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        cursor.close();
        return origin ;
    }

    public String getHotspotFare(String line, String km){

        String query = "select FARE from Hotspot where Lineid = '"+ line +"' and Pointto = '"+ km +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String fare = cursor.getString(cursor.getColumnIndexOrThrow("FARE"));
        cursor.close();
        return fare;
    }

    public int getRemainingPaxOrigin(String lineid, int o_refpoint){
        try {

            String query = "select TOREFPOINT from Ticket where LINEID = '" + lineid + "' and TOREFPOINT < '" + o_refpoint + "'";
            Cursor cursor = this.getDbConnection().rawQuery(query, null);
            if(cursor.getCount() > 0){
                Log.wtf("remaining dbquery", cursor.getCount() + "");
                return cursor.getCount();
            }else{
                Log.wtf("remaining dbquery", "this row is empty");
                return 0;
            }
        }catch (Exception e){

        }
        return 0;

    }

    public String getLineReverse(String lineid){

        String query = "select min(refpoint) as minimum from linesegment where lineid = '"+lineid+"' UNION ALL\n" +
                "select max(refpoint) as minimum from linesegment where lineid = '"+lineid+"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String min = cursor.getString(cursor.getColumnIndexOrThrow("minimum"));
        cursor.moveToNext();
        String max = cursor.getString(cursor.getColumnIndexOrThrow("minimum"));
        GlobalVariable.setMin_reverse(min);
        GlobalVariable.setMax_reverse(max);
        return min;


    }

    public String getLineSegmentReverse(String lineid){
        String min = GlobalVariable.getMin_reverse();
        String max = GlobalVariable.getMax_reverse();
        String query = "select name from linesegment where lineid = '"+ lineid +"' and refpoint = '"+ min+"' union all select name from linesegment where lineid = '"+ lineid +"' and refpoint = '"+ max+"' ";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String min_name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        cursor.moveToNext();
        String max_name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        GlobalVariable.setMin_linesegment(min_name);
        GlobalVariable.setMax_linesegment(max_name);
        return min_name;


    }

    public String getGross(String tripid){

        String query = "select sum(NETAMOUNT) as AMOUNT from TICKET where TRIPID= '"+tripid+"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String gross = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        cursor.close();

        return gross;
    }

    public String getSpecialTrip(String tripid){

        String query = "select coalesce(AMOUNT,0) as SP from TRIPCOST where COSTTYPEID=26 and TRIPID='"+ tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        if (cursor.getCount() == 0){
            return "0";
        }
        cursor.moveToFirst();
        String sptrip = cursor.getString(cursor.getColumnIndexOrThrow("SP"));
        cursor.close();
        return sptrip;
    }

    public String getPenalty(String tripid){
        //test

        String query = "select distinct * from TRIPINSPECTION where ATTRIBUTEID=2 and TRIPID='"+tripid+"'";
        Cursor cursor = this.getDbConnection().rawQuery(query,null);
        int c = cursor.getCount();
        if(c == 0){
            GlobalVariable.setIng_direction("0");
            GlobalVariable.setIng_kmpost("0");
            GlobalVariable.setIng_linesegment("0");
            GlobalVariable.setIng_qty("0");
            return "0";
        }else {
            cursor.moveToFirst();
            String linesegment = cursor.getString(cursor.getColumnIndexOrThrow("LINESEGMENT"));
            String kmpost = cursor.getString(cursor.getColumnIndexOrThrow("KMPOST"));
            String direction = cursor.getString(cursor.getColumnIndexOrThrow("DIRECTION"));
            String qty = cursor.getString(cursor.getColumnIndexOrThrow("QTY"));
            cursor.close();
            GlobalVariable.setIng_direction(direction);
            GlobalVariable.setIng_kmpost(kmpost);
            GlobalVariable.setIng_linesegment(linesegment);
            GlobalVariable.setIng_qty(qty);
            return linesegment;
        }
    }

    public String getControlCount(String segment, String km, String dir, String tripid){

        try {

            if (dir.equals("1")) {

                final String query = "select TAG as SEGMENT, coalesce(count(ID),0) as PAX, max(case when FROMREFPOINT<TOREFPOINT then 1 else -1 end) as DIR" +
                        "              from TICKET" +
                        "              where TOREFPOINT >= '" + km + "' and FROMREFPOINT <= '" + km + "' and TRIPID='" + tripid + "'" +
                        "              group by TAG having tag='" + segment + "'";
                Cursor cursor = this.getDbConnection().rawQuery(query, null);
                cursor.moveToFirst();
                int cnt = cursor.getCount();
                if (cnt > 0) {
                    return cnt + "";
                } else {
                    return "0";
                }
            } else {

                final String query = "select TAG as SEGMENT, coalesce(count(ID),0) as PAX, max(case when FROMREFPOINT<TOREFPOINT then 1 else -1 end) as DIR\n" +
                        "              from TICKET" +
                        "              where FROMREFPOINT >= '" + km + "' and TOREFPOINT <= '" + km + "' and TRIPID='" + tripid + "'" +
                        "              group by TAG having tag='" + segment + "'";
                Cursor cursor = this.getDbConnection().rawQuery(query, null);
                cursor.moveToFirst();
                int cnt = cursor.getCount();
                if (cnt > 0) {
                    return cnt + "";
                } else {
                    return "0";
                }
            }
        }catch (Exception e){
            Log.wtf("exception", "error:" + e);
        }

        return 0 + "";
    }

    public String getPenaltyAmt(String lineid, String km, String dir, String busid){

        try{
            String query = "select PENALTYAMT from CONTROLMATRIX where LINEID='"+ lineid +"' and DIRECTION='"+ dir +"' and FROMKM='"+ km +"' and PENALTYAMT is not null and TOKM in ("+ busid +",0)";
            Cursor cursor = this.getDbConnection().rawQuery(query,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                String penalty = cursor.getString(cursor.getColumnIndexOrThrow("PENALTYAMT"));
                Log.wtf("controlled", cursor.getCount() + "");
                return penalty;
            }else{
                Log.wtf("controlled", "this row is empty");
                return "0";
            }
        }catch (Exception e){
            Log.wtf("exception", "error:" + e);
        }
        return "0";

    }

    public String getOtherAmt(String tripid){

        String query = "select coalesce(AMOUNT,0)as AMOUNT from TRIPCOST where COSTTYPEID=24 and TRIPID='"+ tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query,null);
        cursor.moveToFirst();
        String amt = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        cursor.close();
        return amt;

    }

    public void getPercentCond(String id, String mode, String line){

        String query = "select AMOUNT, ltrim(rtrim(coalesce(EX.REMARKS, ''))) as REMARKS from EMPLOYEEMATRIX EX left join EMPLOYEE E on E.EMPLOYEEROLEID=EX.EMPLOYEEROLEID and E.EMPLOYEETYPEID=EX.EMPLOYEETYPEID where E.ID='"+ id +"' and EX.MODEID='"+ mode +"' and EX.LINEID='"+ line +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String amount = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        String remarks = cursor.getString(cursor.getColumnIndexOrThrow("REMARKS"));
        cursor.close();

        GlobalVariable.setCondpercent_amount(amount);
        GlobalVariable.setCondpercent_remarks(remarks);




    }

    public void getCondDri(String tripid){

        String queryCond = "select EMPLOYEEID from TRIPCREW where EMPLOYEEROLEID=2 AND TRIPID='"+ tripid +"'";
        String queryDri = "select EMPLOYEEID from TRIPCREW where EMPLOYEEROLEID=1 AND TRIPID='"+ tripid +"'";

        Cursor cursorCond = this.getDbConnection().rawQuery(queryCond, null);
        Cursor cursorDri = this.getDbConnection().rawQuery(queryDri, null);
        cursorCond.moveToFirst();
        cursorDri.moveToFirst();
        String cond = cursorCond.getString(cursorCond.getColumnIndexOrThrow("EMPLOYEEID"));
        String dri = cursorDri.getString(cursorDri.getColumnIndexOrThrow("EMPLOYEEID"));

        GlobalVariable.setCurrent_cond(cond);
        GlobalVariable.setCurrent_dri(dri);
    }

    public void getPercentDri(String id, String mode, String line){

        String query = "select AMOUNT, ltrim(rtrim(coalesce(EX.REMARKS, ''))) as REMARKS from EMPLOYEEMATRIX EX left join EMPLOYEE E on E.EMPLOYEEROLEID=EX.EMPLOYEEROLEID and E.EMPLOYEETYPEID=EX.EMPLOYEETYPEID where E.ID='"+ id +"' and EX.MODEID='"+ mode +"' and EX.LINEID='"+ line +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String amount = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        String remarks = cursor.getString(cursor.getColumnIndexOrThrow("REMARKS"));
        cursor.close();

        GlobalVariable.setDripercent_amount(amount);
        GlobalVariable.setDripercent_remarks(remarks);

    }

    public String getlessGrossCommission(String tripid){

        String query = "select sum(AMOUNT) as AMOUNT from TRIPCOST tc left join COSTTYPE ct on ct.ID=tc.COSTTYPEID where ct.TAG like '%111' and TRIPID='"+ tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query,null);
        cursor.moveToFirst();
        String amt = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        if(amt == null){
            return "0.00";
        }else{
            return amt;
        }
    }

    public String getLineDB(String line){

        String query = "select TAG from LINE where ID ='"+ line +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String tag = cursor.getString(cursor.getColumnIndexOrThrow("TAG"));
        return tag;

    }

    public void getEmployeeID(String icond, String idri){

        String query = "select EMPLOYEEROLEID, coalesce(TAG,0) as TAG, EMPLOYEETYPEID from EMPLOYEE where ID in ("+idri+","+icond+") order by EMPLOYEEROLEID";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String dri_employeeid = cursor.getString(cursor.getColumnIndexOrThrow("EMPLOYEETYPEID"));
        String dri_tag = cursor.getString(cursor.getColumnIndexOrThrow("TAG"));
        GlobalVariable.setDri_employeeID(dri_employeeid);
        GlobalVariable.setDri_tag(dri_tag);
        cursor.moveToNext();
        String cond_employeeid = cursor.getString(cursor.getColumnIndexOrThrow("EMPLOYEETYPEID"));
        String cond_tag = cursor.getString(cursor.getColumnIndexOrThrow("TAG"));
        GlobalVariable.setCond_employeeID(cond_employeeid);
        GlobalVariable.setCond_tag(cond_tag);
    }

    public String getCashBond(){

        String query = "select ID, coalesce(REMARKS,'') as REMARKS from WITHHOLDING where TAG=1";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToNext();
        String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
        String remarks = cursor.getString(cursor.getColumnIndexOrThrow("REMARKS"));
        cursor.close();
        GlobalVariable.setCashbond_id(id);
        GlobalVariable.setCashbond_remarks(remarks);
        return id;

    }

    public String getTotalAmount(String tripid){

        String query = "select coalesce(AMOUNT,0) as AMOUNT from TRIPCOST where COSTTYPEID=24 and TRIPID='"+  tripid +"";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToNext();
        String amount = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        cursor.close();
        return amount;
    }

    public int getPartialremit (String tripid){

        String query = "select sum(AMOUNT) as SUM from TRIPRECEIPT where CUSTOMERID=1 and TRIPID='"+ tripid+"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        int sum = cursor.getInt(cursor.getColumnIndexOrThrow("SUM"));
        cursor.close();
        return sum;

    }

    public int getExpenses(String tripid){

        String query = "select sum(AMOUNT) as SUM from TRIPCOST TC left join COSTTYPE CT on CT.ID=TC.COSTTYPEID where (CT.TAG is null or CT.TAG<2000) and CT.NAME<>'Refund' AND TRIPID='"+ tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        int sum = cursor.getInt(cursor.getColumnIndexOrThrow("SUM"));
        cursor.close();
        return sum;

    }

    public int getWithholding(String tripid){

        String query = "select sum(AMOUNT) as SUM from TRIPWITHHOLDING where TRIPID='"+ tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        int sum = cursor.getInt(cursor.getColumnIndexOrThrow("SUM"));
        cursor.close();
        return sum;

    }

    public int getLastRemit(String tripid){

        String query = "select sum(AMOUNT) as SUM from TRIPRECEIPT where CUSTOMERID=2 and TRIPID='"+ tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        int sum = cursor.getInt(cursor.getColumnIndexOrThrow("SUM"));
        cursor.close();
        return sum;

    }

    public void getDispatch (String trip){

        String query = "select t.STARTDATETIMESTAMP as DISPATCHDATE, e.NAME as DISPATCHER from TRIP t " +
                "left join TRIPCREW tc on tc.TRIPID=t.ID and tc.EMPLOYEEROLEID=4 " +
                "left join EMPLOYEE e on e.ID=tc.EMPLOYEEID where t.ID='"+ trip +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String dispatchdate = cursor.getString(cursor.getColumnIndexOrThrow("DISPATCHDATE"));
        String dispatcher = cursor.getString(cursor.getColumnIndexOrThrow("DISPATCHER"));
        cursor.close();

        GlobalVariable.setDispatch_date(dispatchdate);
        GlobalVariable.setName_dispatcher(dispatcher);
    }

    /*Ingresso Part*/

    public int getCurrentKMone(String trip, int currentKm){
        String query = "select NAME from LINESEGMENT where LINEID='"+ trip +"' and REFPOINT='"+ currentKm +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        int cnt = cursor.getCount();
        if(cnt == 0){
            return 0;
        }else{
            return cnt;
        }
    }

    public int getCurrentKMtwo(String trip){
        String query = "select FROMREFPOINT from TICKET where TRIPID='"+ trip +"' order by ID desc";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        int cnt = cursor.getCount();
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndexOrThrow("FROMREFPOINT"));
        if(cnt == 0){
            return 0;
        }else{
            return count;
        }
    }

    public String getCurrentKMthree(String line){
        String query = "select REFPOINT from LINESEGMENT where LINEID='"+ line +"' order by REFPOINT desc";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String r = cursor.getString(cursor.getColumnIndexOrThrow("REFPOINT"));
        cursor.close();
        return r;
    }

    public String getCurrentKMfour(String line){
        String query = "select REFPOINT from LINESEGMENT where LINEID='"+ line +"' order by REFPOINT asc";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String r = cursor.getString(cursor.getColumnIndexOrThrow("REFPOINT"));
        cursor.close();
        return r;
    }

    public String getCurrentKMfive(String line, String linesegment){
        String query = "select NAME from LINESEGMENT where LINEID='"+ line +"' and REFPOINT='"+ linesegment +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String n = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        cursor.close();
        return n;
    }

    public int getTripCount(String tripid){

        String query = "select * from TRIPREVERSE where TRIPID='"+tripid +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        int cnt = cursor.getCount();
        if(cnt == 0){
            return 0 +1;
        }else{
            return cnt + 1;
        }
    }

    public String getRefDateOne (String trip){

        String query = "select STARTDATETIMESTAMP from TRIP where ID='"+ trip +"'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndexOrThrow("STARTDATETIMESTAMP"));
        cursor.close();
        return date;
    }

    public String getRefDateTwo (String trip){

        String query = "select DATETIMESTAMP from TRIPREVERSE where TRIPID='"+ trip +"' order by DATETIMESTAMP desc";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        String date = cursor.getString(cursor.getColumnIndexOrThrow("DATETIMESTAMP"));
        cursor.close();
        return date;
    }

    public String[] getTicketsPerPassengers(String trip, String datetime){

        String query = "select ct.NAME, count(tk.ID) as COUNT, ct.ID  from CUSTOMERTYPE ct left join CUSTOMER c on ct.ID=c.CUSTOMERTYPEID" +
                "                                left join TICKET tk on c.ID=tk.CUSTOMERID and tk.TRIPID='"+trip+"'" +
                "                                 and tk.DATETIMESTAMP>='"+datetime+"'" +
                "                                group by CT.NAME, ct.ID" +
                "                                order by ct.ID";

        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> ptype = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                String count = cursor.getString(cursor.getColumnIndexOrThrow("COUNT"));
                String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));

                ptype.add(name);
                ptype.add(count);
                ptype.add(id);

            }while(cursor.moveToNext());

        }
        cursor.close();
        String[] allSpinner = new String[ptype.size()];
        allSpinner = ptype.toArray(allSpinner);

        return allSpinner;
    }

    public String[] getTicketsIndividual(String trip, String datetime){

            String query = "select tk.DATETIMESTAMP, tk.ID, tk.FROMREFPOINT, tk.TOREFPOINT, tk.NETAMOUNT, t.LINE, c.REMARKS \n" +
                    "                               from TICKET tk left join TRIP t on t.ID=tk.TRIPID \n" +
                    "                               left join CUSTOMER c on c.ID=tk.CUSTOMERID\n" +
                    "                               where TRIPID='"+trip+"' and tk.DATETIMESTAMP>='"+datetime+"' order by tk.ID";

        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> ptype = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String datetimestamp = cursor.getString(cursor.getColumnIndexOrThrow("DATETIMESTAMP"));
                String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                String fromrefpoint = cursor.getString(cursor.getColumnIndexOrThrow("FROMREFPOINT"));
                String torefpoint = cursor.getString(cursor.getColumnIndexOrThrow("TOREFPOINT"));
                String netamount = cursor.getString(cursor.getColumnIndexOrThrow("NETAMOUNT"));
                String line = cursor.getString(cursor.getColumnIndexOrThrow("LINE"));
                String remarks = cursor.getString(cursor.getColumnIndexOrThrow("REMARKS"));
                ptype.add(datetimestamp);
                ptype.add(id);
                ptype.add(fromrefpoint);
                ptype.add(torefpoint);
                ptype.add(netamount + "/netAmount");
                ptype.add(line);
                ptype.add(remarks);


            }while(cursor.moveToNext());

        }
        cursor.close();
        String[] allSpinner = new String[ptype.size()];
        allSpinner = ptype.toArray(allSpinner);

        return allSpinner;
    }

    public String[] getTicketsCountOne(String trip){

        String query = "select ct.NAME, count(tk.ID) as Count, ct.ID from CUSTOMERTYPE ct \n" +
                "                                left join CUSTOMER c on ct.ID=c.CUSTOMERTYPEID \n" +
                "                                left join TICKET tk on c.ID=tk.CUSTOMERID and tk.TRIPID='"+trip+"'\n" +
                "                                group by CT.NAME, ct.ID \n" +
                "                                order by ct.I";

        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> ptype = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                String count = cursor.getString(cursor.getColumnIndexOrThrow("Count"));
                String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));

                ptype.add(name);
                ptype.add(count);
                ptype.add(id);


            }while(cursor.moveToNext());

        }
        cursor.close();
        String[] allSpinner = new String[ptype.size()];
        allSpinner = ptype.toArray(allSpinner);

        return allSpinner;
    }

    public String[] getTicketsCountTwo(String trip){

        String query = "select tk.DATETIMESTAMP, tk.ID, tk.FROMREFPOINT, tk.TOREFPOINT, tk.NETAMOUNT, t.LINE, c.REMARKS\n" +
                "                               from TICKET tk left join TRIP t on t.ID=tk.TRIPID\n" +
                "                               left join CUSTOMER c on c.ID=tk.CUSTOMERID \n" +
                "                               where TRIPID='"+trip+"' order by tk.ID";

        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> ptype = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String datetimestamp = cursor.getString(cursor.getColumnIndexOrThrow("DATETIMESTAMP"));
                String fromrefpoint = cursor.getString(cursor.getColumnIndexOrThrow("FROMREFPOINT"));
                String torefpoint = cursor.getString(cursor.getColumnIndexOrThrow("TOREFPOINT"));
                String netamount = cursor.getString(cursor.getColumnIndexOrThrow("NETAMOUNT"));
                String line = cursor.getString(cursor.getColumnIndexOrThrow("LINE"));
                String remarks = cursor.getString(cursor.getColumnIndexOrThrow("REMARKS"));
                ptype.add(datetimestamp);
                ptype.add(fromrefpoint);
                ptype.add(torefpoint);
                ptype.add(netamount);
                ptype.add(line);
                ptype.add(remarks);



            }while(cursor.moveToNext());

        }
        cursor.close();
        String[] allSpinner = new String[ptype.size()];
        allSpinner = ptype.toArray(allSpinner);

        return allSpinner;
    }

    public String updateAmountExpense(String trip){

        String sql = "select sum(AMOUNT) as AMOUNT from TRIPCOST TC left join COSTTYPE CT on CT.ID=TC.COSTTYPEID where (CT.TAG is null or CT.TAG<2000) and CT.NAME<>'Refund' AND TRIPID='"+trip+"'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String amount = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
        if(amount == null){
            return "0";
        }else{
            return amount;
        }
    }

    public ArrayList<Costtype> expenseList(String trip){

        String sql = "select A.ID as COSTTYPEID, A.NAME as COSTTYPE, coalesce(X.AMOUNT,0) as AMOUNT from COSTTYPE A left join TRIPCOST X  on X.COSTTYPEID=A.ID AND X.TRIPID= '"+ trip+"' where A.REMARKS IS NOT 'SG'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        ArrayList<Costtype> cost = new ArrayList<Costtype>();
        if(cursor.moveToFirst()){
            do{
                //String costtypeid = cursor.getString(cursor.getColumnIndexOrThrow("COSTTYPEID"));
                //String costtype = cursor.getString(cursor.getColumnIndexOrThrow("COSTTYPE"));
                //String amount = cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"));
                cost.add(new Costtype(cursor.getString(cursor.getColumnIndexOrThrow("COSTTYPE")), cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"))));
//                cost.add(costtypeid);
//                cost.add(amount);
            }while(cursor.moveToNext());
        }
        cursor.close();
//        String[] allSpinner = new String[cost.size()];
//        allSpinner = cost.toArray(allSpinner);

        return cost;
    }

    public ArrayList<Withholding> withholdingList(){

        String sql = "select 'W' as TAG, w.ID as TYPEID, w.NAME as TYPENAME, coalesce(t.AMOUNT,coalesce(w.AMOUNT,0)) as AMOUNT from WITHHOLDING w left join TRIPWITHHOLDING t on t.ATTRIBUTEID=w.ID where w.TAG=3 UNION ALL select 'U' as TAG, u.ID, u.NAME, coalesce(tu.QTY,0) from ATTRIBUTE u left join TRIPUSAGE tu on tu.ATTRIBUTEID=u.ID where u.ID in (4,6) ";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);

        ArrayList<Withholding> with = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                with.add(new Withholding(cursor.getString(cursor.getColumnIndexOrThrow("TAG")),cursor.getString(cursor.getColumnIndexOrThrow("TYPEID")),cursor.getString(cursor.getColumnIndexOrThrow("TYPENAME")),cursor.getString(cursor.getColumnIndexOrThrow("AMOUNT"))));
            }while(cursor.moveToNext());
        }
        return with;
    }

    public String dricomm (String trip){


        String sql ="select A.ID as COSTTYPEID, A.NAME as COSTTYPE, coalesce(X.AMOUNT,0) as AMOUNT from COSTTYPE A left join TRIPCOST X on X.COSTTYPEID=A.ID AND X.TRIPID='"+trip+"' where A.NAME = 'Driver Comm'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);

        //if( cursor != null && cursor.moveToFirst() ) {
        cursor.moveToFirst();
            String cost_id = cursor.getString(cursor.getColumnIndex("COSTTYPEID"));
            String name = cursor.getString(cursor.getColumnIndex("COSTTYPE"));
             String amount = cursor.getString(cursor.getColumnIndex("AMOUNT"));
            cursor.close();
            GlobalVariable.setDri_cost_id(cost_id);
            GlobalVariable.setDri_cost_name(name);
            GlobalVariable.setDri_cost_amount(amount + "");
            return amount;
        //}
        //return amount;
    }


    public String condcomm (String trip){

        String sql ="select A.ID as COSTTYPEID, A.NAME as COSTTYPE, coalesce(X.AMOUNT,0) as AMOUNT from COSTTYPE A left join TRIPCOST X on X.COSTTYPEID=A.ID AND X.TRIPID='"+trip+"' where A.NAME = 'Cond Comm' ";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String cost_id = cursor.getString(cursor.getColumnIndex("COSTTYPEID"));
        String name = cursor.getString(cursor.getColumnIndex("COSTTYPE"));
        String amountc = cursor.getString(cursor.getColumnIndex("AMOUNT"));
        cursor.close();
        GlobalVariable.setCond_cost_id(cost_id);
        GlobalVariable.setCond_cost_name(name);
        GlobalVariable.setCond_cost_amount(amountc);


        return amountc;

    }

    public String getCheckpoint(String km, String lineID){

        String sql = "select NAME from LINESEGMENT where REFPOINT='"+ km + " AND LINEID='" + lineID+ "'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        int c = cursor.getCount();
        if(c > 0){
            String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            return name;
        }else{
            return km;
        }
    }

    //get passengers to north
    public String getPassengersToNorth(SQLiteDatabase db, String tableName, String tripid, String date, String km) {
        Log.d("","tableToString called");
        String tableString = String.format("Table %s:\n", tableName);
        String sql = "select ct.NAME, count(tk.ID), ct.ID from CUSTOMER c" +
                "                            left join TICKET tk on c.ID=tk.CUSTOMERID" +
                "                            left join CUSTOMERTYPE ct on ct.ID=c.CUSTOMERTYPEID\n" +
                "                            where tk.TRIPID='"+ tripid +"' and tk.DATETIMESTAMP >='"+ date+"'" +
                "                            and tk.TOREFPOINT >  '"+ km +"'" +  // if direction is 1? = > or <
                "                            group by CT.NAME, ct.ID  " +
                "                            order by ct.ID";
        Cursor allRows  = this.getDbConnection().rawQuery("SELECT * FROM " + tableName, null);
      //  Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        tableString += cursorToString(allRows);
        return tableString;
    }

    //get passengers to south
    public String getPassengersToSouth(String tableName, String tripid, String date, String km) {
        Log.d("","tableToString called");
        String tableString = String.format("Table %s:\n", tableName);
        String sql = "select ct.NAME, count(tk.ID), ct.ID from CUSTOMER c" +
                "                            left join TICKET tk on c.ID=tk.CUSTOMERID" +
                "                            left join CUSTOMERTYPE ct on ct.ID=c.CUSTOMERTYPEID\n" +
                "                            where tk.TRIPID='"+ tripid +"' and tk.DATETIMESTAMP >='"+ date+"'" +
                "                            and tk.TOREFPOINT <  '"+ km +"'" +  // if direction is 1? = > or <
                "                            group by CT.NAME, ct.ID  " +
                "                            order by ct.ID";
        Cursor allRows  = this.getDbConnection().rawQuery(sql, null);
        //  Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        tableString += cursorToString(allRows);
        return tableString;
    }

    public String cursorToString(Cursor cursor){
        String cursorString = "";
        if (cursor.moveToFirst() ){
            String[] columnNames = cursor.getColumnNames();
            for (String name: columnNames)
                cursorString += String.format("%s ][ ", name);
            cursorString += "\n";
            do {
                for (String name: columnNames) {
                    cursorString += String.format("%s ][ ",
                            cursor.getString(cursor.getColumnIndex(name)));
                }
                cursorString += "\n";
            } while (cursor.moveToNext());
        }
        return cursorString;
    }

    public String getDateReverse(String km, String tripid){

        String sql = "select DATETIMESTAMP from TRIPREVERSE where TRIPID = '"+km +"' and KMPOST = '"+ tripid+"'";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex("DATETIMESTAMP"));
        cursor.close();
        return date;

    }

    public String getPassengersNorth(String tripid, String date, String km){
        String sql = "select ct.NAME, count(tk.ID) as count, ct.ID from CUSTOMER c" +
                "                            left join TICKET tk on c.ID=tk.CUSTOMERID" +
                "                            left join CUSTOMERTYPE ct on ct.ID=c.CUSTOMERTYPEID" +
                "                            where tk.TRIPID='"+ tripid +"' and tk.DATETIMESTAMP >='"+ date+"'" +
                "                            and tk.TOREFPOINT >  '"+ km +"'" +  // if direction is 1? = > or <
                "                            group by CT.NAME, ct.ID  " +
                "                            order by ct.ID";
        Cursor cursor = this.getDbConnection().rawQuery(sql, null);
        cursor.moveToFirst();
        String count = cursor.getString(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }






//    public JSONArray converttojsonArray(String trip, String datetime){
//
//
//        String query = "select tk.DATETIMESTAMP, tk.ID, tk.FROMREFPOINT, tk.TOREFPOINT, tk.NETAMOUNT, t.LINE, c.REMARKS \n" +
//                "                               from TICKET tk left join TRIP t on t.ID=tk.TRIPID \n" +
//                "                               left join CUSTOMER c on c.ID=tk.CUSTOMERID\n" +
//                "                               where TRIPID='"+trip+"' and tk.DATETIMESTAMP>='"+datetime+"' order by tk.ID";
//
//        Cursor cursor = this.getDbConnection().rawQuery(query, null);
//        ArrayList<String> ptype = new ArrayList<>();
//        if(cursor.moveToFirst()){
//            do{
//                String datetimestamp = cursor.getString(cursor.getColumnIndexOrThrow("DATETIMESTAMP"));
//                String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
//                String fromrefpoint = cursor.getString(cursor.getColumnIndexOrThrow("FROMREFPOINT"));
//                String torefpoint = cursor.getString(cursor.getColumnIndexOrThrow("TOREFPOINT"));
//                String netamount = cursor.getString(cursor.getColumnIndexOrThrow("NETAMOUNT"));
//                String line = cursor.getString(cursor.getColumnIndexOrThrow("LINE"));
//                String remarks = cursor.getString(cursor.getColumnIndexOrThrow("REMARKS"));
//                ptype.add(datetimestamp);
//                ptype.add(id);
//                ptype.add(fromrefpoint);
//                ptype.add(torefpoint);
//                ptype.add(netamount + "/netAmount");
//                ptype.add(line);
//                ptype.add(remarks);
//
//
//            }while(cursor.moveToNext());
//
//        }
//        cursor.close();
//        String[] allSpinner = new String[ptype.size()];
//        allSpinner = ptype.toArray(allSpinner);
//
//        return allSpinner;
//
//
//
//
//
//    }


















}
