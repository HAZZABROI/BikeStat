package ru.rodniki.bikestat.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;

import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.ScreenRect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
import com.yandex.mapkit.transport.bicycle.Session;

import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import org.jetbrains.annotations.Async;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.rodniki.bikestat.MapKitInitializer;
import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.bpmAPI;
import ru.rodniki.bikestat.models.RouteRealm;
import ru.rodniki.bikestat.models.bpmModel;

public class RouteGoing extends AppCompatActivity implements UserLocationObjectListener, CameraListener, Session.RouteListener, LocationListener{
    Button buttonEndRoute;
    MapView mapView;
    Boolean initialized;
    BicycleRouter bicycleRouter;
    String mapURI, timeStart, dateStart, distanceTotal;
    MapObjectCollection mapObjectCollection;
    com.yandex.mapkit.transport.bicycle.Session drivingSession;
    String getUrl = "https://dt.miet.ru";
    String token = "az4fvf7nzi1XPIsYiMEu";
    UserLocationLayer userLocationLayer;
    ArrayList<Float> arrayV;
    ResponseBody result;
    Realm uiThreadRealm;
    RouteRealm routeRealm;
    bpmAPI retrofitRes;
    long startTime = System.currentTimeMillis();
    bpmModel responseAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);
        initialized = intent.getBooleanExtra("isInit", false);
        MapKitInitializer mapKitInitializer = new MapKitInitializer();
        mapKitInitializer.initializeMapKit(getApplicationContext(), initialized);
        setContentView(R.layout.activity_route_going);
        mapURI = intent.getStringExtra("mapURI");
        timeStart = intent.getStringExtra("timeStart");
        dateStart = intent.getStringExtra("dateStart");
        distanceTotal = intent.getStringExtra("distanceTotal");
        requestLocationPermission();
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        buttonEndRoute = findViewById(R.id.btnEnd);
        mapView = findViewById(R.id.mapView);
        mapView.getMap().addCameraListener(this);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().setTiltGesturesEnabled(false);
        bicycleRouter = TransportFactory.getInstance().createBicycleRouter();
        mapObjectCollection = mapView.getMap().getMapObjects();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setObjectListener(this);
        routeRealm = new RouteRealm();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        uiThreadRealm = Realm.getInstance(realmConfiguration);
        arrayV = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        retrofitRes = retrofit.create(bpmAPI.class);
        Realm.init(this);

        submitRequest();
        buttonEndRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBPM();
            }
        });
    }

    private void submitRequest(){
        drivingSession = bicycleRouter.resolveUri(mapURI, this);
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    1);
        }
    }
    private void getBPM(){
        retrofitRes.getBpm(token).enqueue(new Callback<bpmModel>() {
            @Override
            public void onResponse(Call<bpmModel> call, Response<bpmModel> response) {
                responseAll = response.body();
                Long time = System.currentTimeMillis() - startTime;
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String text = formatter.format(new Date(time));
                System.out.println(responseAll.getData().getPulse().getAvg());
                Intent intent = new Intent(RouteGoing.this, NewTrailActivity.class);
                String kkal = String.valueOf(0.014 * 63.4 * (time/60000) * (0.12 * responseAll.getData().getPulse().getAvg()));
                intent.putExtra("isNewRoute", true);
                intent.putExtra("isInit", true);
                intent.putExtra("mapURI", mapURI);
                intent.putExtra("timeStart", timeStart);
                intent.putExtra("dateStart", dateStart);
                intent.putExtra("distanceTotal", distanceTotal);
                intent.putExtra("timeTotal", text);
                intent.putExtra("kkal", kkal);
                intent.putExtra("avgBPM", responseAll.getData().getPulse().getAvg().toString());
                intent.putExtra("minBPM", responseAll.getData().getPulse().getMin().toString());
                intent.putExtra("maxBPM", responseAll.getData().getPulse().getMax().toString());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<bpmModel> call, Throwable t) {
                System.out.println(Arrays.toString(t.getStackTrace()));
            }
        });
    }
    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiThreadRealm.close();
    }

    @Override
    protected void onStart() {
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        super.onStart();
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
                        mapView.getWidth(),
                        mapView.getHeight()
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
        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight()*0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight()*0.83))
        );
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(this, R.drawable.arrow_norm));
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        System.out.println(location.getSpeed());
    }
}