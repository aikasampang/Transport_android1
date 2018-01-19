package com.transport.organelles.transport_.forms;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.transport.organelles.transport_.R;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.classforms.linesegmentAdapter;
import com.transport.organelles.transport_.model.linesegment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Organelles on 1/18/2018.
 */

public class frmLineSegment extends AppCompatActivity {

    Button cancel, ok;
    ListView listview;
    ArrayList<linesegment> ls = null;
    String loc_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmlinesegment);

        setObject();

    }


    private void setObject(){


        cancel = (Button) findViewById(R.id.i_save);
        ok = (Button)findViewById(R.id.i_cancel);
        listview = (ListView) findViewById(R.id.linesegment_list);




        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (frmLineSegment.this, frmTicket.class);
                startActivity(intent);
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        DBQuery dbQuery = new DBQuery(frmLineSegment.this);
        String line = dbQuery.getLineDb();
        ls = dbQuery.linesegmentList(line);

        final linesegmentAdapter adapter = new linesegmentAdapter(frmLineSegment.this, R.layout.linesegment_details, ls);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                loc_name = listview.getAdapter().getItem(position).toString();
                Log.i("Selected Item in list", loc_name);
               // Toast.makeText(frmLineSegment.this, loc_name, Toast.LENGTH_LONG).show();
            }
        });



    }




}
