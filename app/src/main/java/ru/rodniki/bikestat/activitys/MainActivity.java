package ru.rodniki.bikestat.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.transport.bicycle.Route;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ru.rodniki.bikestat.BuildConfig;
import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.models.RouteRealm;
import ru.rodniki.bikestat.adapters.BI_RecyclerViewAdapterFirst;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    ArrayList<RouteRealm> RouteRealm = new ArrayList<>();
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