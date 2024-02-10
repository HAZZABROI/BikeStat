package ru.rodniki.bikestat.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.ScreenRect;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.bicycle.BicycleRouter;
import com.yandex.mapkit.transport.bicycle.Route;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmResults;

import ru.rodniki.bikestat.models.RouteRealm;

import ru.rodniki.bikestat.utils.MapKitInitializer;
import ru.rodniki.bikestat.R;

public class DetailTrailActivity extends AppCompatActivity implements UserLocationObjectListener, CameraListener, com.yandex.mapkit.transport.bicycle.Session.RouteListener {

    String totalTime, totalDistance, avgBPM, kkal, avgVelocity, startDate, mapURI, diff, diffPre, diffStr;
    TextView startDateT, kkalT, avgBPMT, totalDistanceT,
            totalTimeT, textMapT, textGraphT, textDiff;
    CardView cardMap;
    ConstraintLayout layoutBack;
    MapView mapView;
    BicycleRouter bicycleRouter;
    ImageView imageDelete;
    MapObjectCollection mapObjectCollection;
    Button buttonStart;
    Realm uiThreadRealm;
    boolean isSchedule;
    com.yandex.mapkit.transport.bicycle.Session drivingSession;
    float k;
    long startTime = System.currentTimeMillis();
    Boolean initialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        initialized = intent.getBooleanExtra("isInit", false);
        MapKitInitializer mapKitInitializer = new MapKitInitializer();
        mapKitInitializer.initializeMapKit(getApplicationContext(), initialized);
        setContentView(R.layout.activity_detail_trail);
        startDateT = findViewById(R.id.dateStart);
        kkalT = findViewById(R.id.totalKkal);
        avgBPMT = findViewById(R.id.textBPM);
        totalDistanceT = findViewById(R.id.totalDistance);
        totalTimeT = findViewById(R.id.totalTime);
        buttonStart = findViewById(R.id.buttonStart);
        textMapT = findViewById(R.id.textMap);
        textGraphT = findViewById(R.id.imageGraph);
        cardMap = findViewById(R.id.cardMap);
        layoutBack = findViewById(R.id.layoutBack);
        imageDelete = findViewById(R.id.imageDelete);
        textDiff = findViewById(R.id.textDiff);
        mapView = findViewById(R.id.mapView);
        mapView.getMap().addCameraListener(this);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().setTiltGesturesEnabled(false);
        bicycleRouter = TransportFactory.getInstance().createBicycleRouter();
        getIntentExtras(intent);
        mapObjectCollection = mapView.getMap().getMapObjects();
        if (isSchedule){
            buttonStart.setVisibility(View.VISIBLE);
        }
        Realm.init(this);
        uiThreadRealm = Realm.getDefaultInstance();
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTrailActivity.this, RouteGoing.class);
                intent.putExtra("isInit", true);
                intent.putExtra("mapURI", mapURI);
                intent.putExtra("timeStart", startTime);
                intent.putExtra("dateStart", startDate);
                intent.putExtra("distanceTotal", totalDistance);
                intent.putExtra("distanceTotalMetr", ((totalDistance.contains("km")?Float.parseFloat(totalDistance.replaceAll("\\D+", ""))*100:totalDistance.replaceAll("\\D+", ""))).toString());
                intent.putExtra("totalTime", totalTime);
                intent.putExtra("isSchedule", true);
                startActivity(intent);
            }
        });
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiThreadRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmResults<RouteRealm> routes = realm
                                .where(RouteRealm.class)
                                .findAll();
                        RouteRealm routeRealm = routes.where().equalTo("mapURI",mapURI).findFirst();
                        routeRealm.deleteFromRealm();
                        Intent intent = new Intent(DetailTrailActivity.this, MainActivity.class);
                        intent.putExtra("isInit", true);
                        startActivity(intent);
                    }
                });
            }
        });
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTrailActivity.this, MainActivity.class);
                intent.putExtra("isInit", true);
                startActivity(intent);
            }
        });


        setTextInfo();
        toggleCards();
        submitRequest();
    }


    private void toggleCards(){
        textMapT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardMap.setVisibility(View.VISIBLE);
            }
        });
        textGraphT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardMap.setVisibility(View.INVISIBLE);
            }
        });
    }
    public int toTime(String time){
        Pattern pattern = Pattern.compile("(\\d+)\\s*(days?|hr|min)");
        Matcher matcher = pattern.matcher(time);

        int days = 0;
        int hours = 0;
        int minutes = 0;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "days":
                    days = value;
                    break;
                case "hr":
                    hours = value;
                    break;
                case "min":
                    minutes = value;
                    break;
            }
        }

        int totalMinutes = days * 24 + hours + minutes / 60;;

        return totalMinutes;
    }
    public String getDiff(String time, String distance, String avgBPM){
        k = (Float.parseFloat(avgBPM)*Float.parseFloat(time))/(Float.parseFloat(distance));
        System.out.println(k);
        if(k < 0.003){
            diffStr = "легкий";
        } else if (k < 0.02) {
            diffStr = "средний";
        } else {
            diffStr = "сложный";
        }
        return diffStr;
    }
    private void setTextInfo(){
        startDateT.setText(startDate);
        kkalT.setText(kkal);
        avgBPMT.setText(avgBPM);
        totalDistanceT.setText(totalDistance);
        totalTimeT.setText(totalTime);
        textDiff.setText(diffPre + ((diff == null)?"":"/"+diff));
    }
    private void getIntentExtras(Intent intent){
        totalTime = intent.getStringExtra("totalTime");
        totalDistance = intent.getStringExtra("totalDistance");
        avgBPM = intent.getStringExtra("avgBPM");
        kkal = intent.getStringExtra("kkal");
        avgVelocity = intent.getStringExtra("avgVelocity");
        startDate = intent.getStringExtra("startDate");
        diff = intent.getStringExtra("diff");
        diffPre = intent.getStringExtra("diffPre");
        mapURI = intent.getStringExtra("mapURI");
        isSchedule = intent.getBooleanExtra("isSchedule",false);
    }

    @Override
    protected void onStart() {
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        super.onStart();
    }

    private void submitRequest() {
        drivingSession = bicycleRouter.resolveUri(mapURI, this);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }
    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean b) {

    }

    @Override
    public void onBicycleRoutes(@NonNull List<Route> list) {
        PolylineMapObject line = mapObjectCollection.addPolyline(list.get(0).getGeometry());
        Geometry polyGeo = Geometry.fromPolyline(line.getGeometry());
        ScreenRect screenRect = new ScreenRect(
                new ScreenPoint(0f,0f),
                new ScreenPoint(
                        cardMap.getWidth(),
                        cardMap.getHeight()
                )
        );
        CameraPosition cameraPosition = mapView.getMap().cameraPosition(polyGeo, screenRect);
        mapView.getMap().move(
                new CameraPosition(cameraPosition.getTarget(), cameraPosition.getZoom() - 1f, cameraPosition.getAzimuth(), cameraPosition.getTilt()),
                new Animation(Animation.Type.SMOOTH, 1f),
                null
        );
    }

    @Override
    public void onBicycleRoutesError(@NonNull Error error) {
        String errorMessage = "Неизвестная ошибка!";
        if(error instanceof RemoteError){
            errorMessage = "Ошибка доступа";
        } else if (error instanceof NetworkError) {
            errorMessage = "Нет доступа к интернету!";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }
}