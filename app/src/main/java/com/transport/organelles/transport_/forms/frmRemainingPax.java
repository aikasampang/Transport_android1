package com.transport.organelles.transport_.forms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.transport.organelles.transport_.R;
import com.transport.organelles.transport_.classforms.DBQuery;
import com.transport.organelles.transport_.model.linesegment;
import com.transport.organelles.transport_.model.remainingpax;

import java.util.ArrayList;

/**
 * Created by Organelles on 1/24/2018.
 */

public class frmRemainingPax extends AppCompatActivity {

    private GridView grid;
    public static ArrayList<String> ArrayofName = new ArrayList<String>();
    ArrayList<remainingpax> ls = null;
    private Button back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frmremaingpax);



        setObject();
        objectListener();


    }


    private void setObject(){

        grid = (GridView) findViewById(R.id.remaining_grid);
        back = (Button) findViewById(R.id.back_remaining);

    }

    private void objectListener(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (frmRemainingPax.this, frmTicket.class);
                startActivity(intent);
            }
        });


        DBQuery dbQuery = new DBQuery(frmRemainingPax.this);
        String line = dbQuery.getLineDb();
        String direction = dbQuery.getDirectionfromDB();
        //ls = dbQuery.getRemainingPaxForm(line, direction,);


    }
}
