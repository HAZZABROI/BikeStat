package ru.rodniki.bikestat.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.adapters.BI_RecyclerViewAdapterFirst;
import ru.rodniki.bikestat.models.InfoTrailModel;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    ArrayList<InfoTrailModel> infoTrailModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerViewFirst = findViewById(R.id.mRecyclerViewFirst);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewTrailActivity.class);
                startActivity(intent);
            }
        });

        setUpInfoTrailModels();

        BI_RecyclerViewAdapterFirst adapter = new BI_RecyclerViewAdapterFirst(this, infoTrailModels);

        recyclerViewFirst.setAdapter(adapter);
        recyclerViewFirst.setLayoutManager(new LinearLayoutManager(this));

    }



    private  void setUpInfoTrailModels(){
        String[] infoTrailTimeStart = getResources().getStringArray(R.array.trail_timeStart);
        String[] infoTrailTimeTotal = getResources().getStringArray(R.array.trail_timeTotal);
        String[] infoTrailTotalRange = getResources().getStringArray(R.array.trail_totalRange);
        String[] infoTrailDateStart = getResources().getStringArray(R.array.trail_dateStart);

        String[] infoTrailID = getResources().getStringArray(R.array.trail_ids);
        String[] infoTrailAvgBPM = getResources().getStringArray(R.array.trail_avgBPM);
        String[] infoTrailAvgVelocity = getResources().getStringArray(R.array.trail_avgVelocity);
        String[] infoTrailKkal = getResources().getStringArray(R.array.trail_kkal);


        for (int i = 0; i < infoTrailTimeStart.length; i++){
            infoTrailModels.add(new InfoTrailModel(infoTrailTimeStart[i],
                    infoTrailTimeTotal[i], infoTrailAvgBPM[i],
                    infoTrailAvgVelocity[i], infoTrailTotalRange[i], infoTrailDateStart[i], infoTrailID[i], infoTrailKkal[i]));
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}