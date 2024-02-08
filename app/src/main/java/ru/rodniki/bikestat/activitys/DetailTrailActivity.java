package ru.rodniki.bikestat.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.ScreenRect;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.bicycle.BicycleRouter;
import com.yandex.mapkit.transport.bicycle.Route;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.List;

import ru.rodniki.bikestat.BuildConfig;
import ru.rodniki.bikestat.MapKitInitializer;
import ru.rodniki.bikestat.R;

public class DetailTrailActivity extends AppCompatActivity implements UserLocationObjectListener, CameraListener, com.yandex.mapkit.transport.bicycle.Session.RouteListener {

    String totalTime, totalDistance, avgBPM, kkal, avgVelocity, startDate, mapURI;
    TextView startDateT, kkalT, avgBPMT, totalDistanceT,
            totalTimeT, textMapT, textGraphT;
    CardView cardMap;
    ConstraintLayout layoutBack;
    MapView mapView;
    BicycleRouter bicycleRouter;
    MapObjectCollection mapObjectCollection;
    com.yandex.mapkit.transport.bicycle.Session drivingSession;

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
        mapView = findViewById(R.id.mapView);
        startDateT = findViewById(R.id.dateStart);
        kkalT = findViewById(R.id.totalKkal);
        avgBPMT = findViewById(R.id.textBPM);
        totalDistanceT = findViewById(R.id.totalDistance);
        totalTimeT = findViewById(R.id.totalTime);
        textMapT = findViewById(R.id.textMap);
        textGraphT = findViewById(R.id.textGraph);
        cardMap = findViewById(R.id.cardMap);
        layoutBack = findViewById(R.id.layoutBack);

        mapView.getMap().addCameraListener(this);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().setTiltGesturesEnabled(false);
        bicycleRouter = TransportFactory.getInstance().createBicycleRouter();
        mapObjectCollection = mapView.getMap().getMapObjects();

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTrailActivity.this, MainActivity.class);
                intent.putExtra("isInit", true);
                startActivity(intent);
            }
        });

        getIntentExtras(intent);
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

    private void setTextInfo(){
        startDateT.setText(startDate);
        kkalT.setText(kkal);
        avgBPMT.setText(avgBPM);
        totalDistanceT.setText(totalDistance);
        totalTimeT.setText(totalTime);
    }
    private void getIntentExtras(Intent intent){
        totalTime = intent.getStringExtra("totalTime");
        totalDistance = intent.getStringExtra("totalDistance");
        avgBPM = intent.getStringExtra("avgBPM");
        kkal = intent.getStringExtra("kkal");
        avgVelocity = intent.getStringExtra("avgVelocity");
        startDate = intent.getStringExtra("startDate");
        mapURI = intent.getStringExtra("mapURI");
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