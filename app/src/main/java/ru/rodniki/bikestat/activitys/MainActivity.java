package ru.rodniki.bikestat.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.compose.ui.platform.ComposeView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
    TextView allDistance;
    Realm uiThreadRealm;
    boolean initialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        initialized = intent.getBooleanExtra("isInit", false);
        Realm.init(this);

        uiThreadRealm = Realm.getDefaultInstance();

        RecyclerView recyclerViewFirst = findViewById(R.id.mRecyclerViewFirst);
        allDistance = findViewById(R.id.allDistance);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewTrailActivity.class);
                intent.putExtra("isInit", initialized);
                startActivity(intent);
            }
        });

        setUpInfoTrailModels();

        BI_RecyclerViewAdapterFirst adapter = new BI_RecyclerViewAdapterFirst(this, RouteRealm, initialized);
        recyclerViewFirst.setAdapter(adapter);
        recyclerViewFirst.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiThreadRealm.close();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    private  void setUpInfoTrailModels(){
        List<RouteRealm> routeRealmList = uiThreadRealm.where(RouteRealm.class).findAll();
        for (RouteRealm i : routeRealmList){
            RouteRealm.add(new RouteRealm(i.getTimeStart(), i.getKkal(), i.getTimeTotal(),
                    i.getDateStart(), i.getBPM(), i.getAvgVelocity(), i.getMapURI(), i.getDistanceTotal(), i.getDiff(), i.getDiffPre(), i.getDistanceTotalMetr(), i.getSchedule()));
        }
        allDistance.setText("Количество " + routeRealmList.size());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}