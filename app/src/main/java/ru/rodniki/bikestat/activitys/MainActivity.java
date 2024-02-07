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
import java.util.List;

import io.realm.Realm;
import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.models.RouteRealm;
import ru.rodniki.bikestat.adapters.BI_RecyclerViewAdapterFirst;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    ArrayList<RouteRealm> RouteRealm = new ArrayList<>();
    Realm uiThreadRealm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);

        uiThreadRealm = Realm.getDefaultInstance();

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

        BI_RecyclerViewAdapterFirst adapter = new BI_RecyclerViewAdapterFirst(this, RouteRealm);

        recyclerViewFirst.setAdapter(adapter);
        recyclerViewFirst.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiThreadRealm.close();
    }

    private  void setUpInfoTrailModels(){
        List<RouteRealm> routeRealmList = uiThreadRealm.where(RouteRealm.class).findAll();
        for (RouteRealm i : routeRealmList){
            RouteRealm.add(new RouteRealm(i.getTimeStart(), i.getKkal(), i.getTimeTotal(),
                    i.getDateStart(), i.getAvgBPM(), i.getAvgVelocity(), i.getMapURI(), i.getDistanceTotal()));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

}