package ru.rodniki.bikestat.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.adapters.BI_RecyclerViewAdapterFirst;
import ru.rodniki.bikestat.models.InfoTrailModel;

public class MainActivity extends AppCompatActivity {

    ArrayList<InfoTrailModel> infoTrailModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerViewFirst = findViewById(R.id.mRecyclerViewFirst);

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
}