package ru.rodniki.bikestat.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.FragmentManager;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.ScreenRect;

import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;

import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;

import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.bicycle.BicycleRouter;
import com.yandex.mapkit.transport.bicycle.Route;
import com.yandex.mapkit.transport.bicycle.VehicleType;
import com.yandex.mapkit.transport.bicycle.Weight;
import com.yandex.mapkit.uri.UriObjectMetadata;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import ru.rodniki.bikestat.BuildConfig;
import ru.rodniki.bikestat.MapKitInitializer;
import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.models.RouteRealm;


public class NewTrailActivity extends AppCompatActivity implements UserLocationObjectListener, Session.SearchListener, CameraListener, com.yandex.mapkit.transport.bicycle.Session.RouteListener {
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private MapView mapView;
    CardView cardTime, cardDate;
    FragmentManager fm;
    CardView backLayout;
    ConstraintLayout layout;
    ImageView close;
    Dialog dialog;
    Button dialogContinue, configBtn, scheduleRoute;
    CardView ViewMapCard;
    EditText editTextFrom, editTextDist;
    UserLocationLayer locationMapKit;
    TextView totalTime, totalDistance;
    SearchManager searchManager;
    Session session;
    MapObjectCollection mapObjectCollection;
    ArrayList<Point> arrayList;
    BicycleRouter bicycleRouter;
    ScrollView scrollView;
    ImageView transparentImage;
    MaterialTimePicker pickerTime;
    MaterialDatePicker<Long> pickerDate;
    Realm uiThreadRealm;
    RouteRealm routeRealm;
    Weight weight;
    UriObjectMetadata uriRoute;
    Boolean initialized;
    com.yandex.mapkit.transport.bicycle.Session drivingSession;


    private void sumbitQuery(String query){
        session = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()), new SearchOptions(),this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        initialized = intent.getBooleanExtra("isInit", false);
        MapKitInitializer mapKitInitializer = new MapKitInitializer();
        mapKitInitializer.initializeMapKit(getApplicationContext(), initialized);
        setContentView(R.layout.activity_new_trail);
        requestLocationPermission();
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
//      TODO: Create sticky button at bottom of the screen
        fm = this.getSupportFragmentManager();

        mapView = findViewById(R.id.mapView);
        cardDate = findViewById(R.id.cardDate);
        cardTime = findViewById(R.id.cardTime);
        layout = findViewById(R.id.layoutMap);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        backLayout = findViewById(R.id.layoutBack);
        close = findViewById(R.id.closeBtn);
        ViewMapCard = findViewById(R.id.ViewMapCard);
        configBtn = findViewById(R.id.configRoute);
        totalTime = findViewById(R.id.totalTime);
        totalDistance = findViewById(R.id.totalDistance);
        scrollView = findViewById(R.id.scrollView);
        transparentImage = findViewById(R.id.transparent_image);
        scheduleRoute = findViewById(R.id.scheduleRoute);

        dialog = new Dialog(NewTrailActivity.this);
        dialog.setContentView(R.layout.layout_custom_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialogContinue = dialog.findViewById(R.id.btnDialog);
        editTextFrom = dialog.findViewById(R.id.ETFrom);
        editTextDist = dialog.findViewById(R.id.ETDist);

        SearchFactory.initialize(this);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);
        mapView.getMap().addCameraListener(this);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().setTiltGesturesEnabled(false);
        bicycleRouter = TransportFactory.getInstance().createBicycleRouter();

        arrayList = new ArrayList<>();
        routeRealm = new RouteRealm();

        Realm.init(this);
        uiThreadRealm = Realm.getDefaultInstance();

        transparentImage.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return false;

                case MotionEvent.ACTION_UP:
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return false;

                default:
                    return true;
            }
        });
        scheduleRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickerTime != null && pickerDate != null){
                    routeRealm.setTimeStart(pickerTime.getHour() + "ч. " + pickerTime.getMinute() + "мин.");
                    routeRealm.setDateStart(DateFormat.format("dd/MM/yyyy", new Date(pickerDate.getSelection())).toString());
                    routeRealm.setTimeTotal(weight.getTime().getText());
                    routeRealm.setDistanceTotal(weight.getDistance().getText());
                    routeRealm.setMapURI(uriRoute.getUris().get(0).getValue());
                    uiThreadRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealm(routeRealm);
                            routeRealm = new RouteRealm();
                        }
                    });
                    Intent intent = new Intent(NewTrailActivity.this, MainActivity.class);
                    intent.putExtra("isInit", true);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Вы должны ввести дату и время", Toast.LENGTH_LONG);
                }
            }
        });
        configBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        dialogContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumbitQuery(editTextFrom.getText().toString());
                sumbitQuery(editTextDist.getText().toString());
                dialog.dismiss();
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewTrailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        cardTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });

        cardDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
    }

    private void sumbitRequest() {
        ArrayList<RequestPoint> requestPoints = new ArrayList();
        requestPoints.add(new RequestPoint(arrayList.get(0), RequestPointType.WAYPOINT, null, null));
        requestPoints.add(new RequestPoint(arrayList.get(1), RequestPointType.WAYPOINT, null, null));
        drivingSession = bicycleRouter.requestRoutes(requestPoints, VehicleType.BICYCLE, this);
    }

    @Override
    protected void onStart() {
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    private void openTimePicker(){
        pickerTime = new MaterialTimePicker.Builder().setHour(12).setMinute(0).setTitleText("Выбрать время поездки").build();
        pickerTime.show(fm, "TAG");
    }
    private void openDatePicker(){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Выбрать дату поездки");
        pickerDate = builder.build();
        pickerDate.show(fm, pickerDate.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiThreadRealm.close();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        locationMapKit.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83))
        );
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(this, com.yandex.maps.mobile.R.drawable.arrow));
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean finished) {

    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        mapObjectCollection = mapView.getMap().getMapObjects();
        Point resultLocation = response.getCollection().getChildren().get(0).getObj().getGeometry().get(0).getPoint();
        if(response!=null) {
            if (arrayList.size() + 1 > 2){
                mapObjectCollection.clear();
                arrayList.clear();
            }
            arrayList.add(resultLocation);
            mapObjectCollection.addPlacemark(resultLocation, ImageProvider.fromResource(this, com.yandex.maps.mobile.R.drawable.search_layer_pin_icon_default));
            if (arrayList.size() == 2){
                sumbitRequest();
            }
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = "Неизвестная ошибка!";
        if(error instanceof RemoteError){
            errorMessage = "Ошибка доступа";
        } else if (error instanceof NetworkError) {
            errorMessage = "Нет доступа к интернету!";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
    }

    @Override
    public void onBicycleRoutes(@NonNull List<Route> list) {
        weight = list.get(0).getWeight();
        uriRoute = list.get(0).getUriMetadata();
        totalTime.setText(weight.getTime().getText());
        totalDistance.setText(weight.getDistance().getText());
        PolylineMapObject line = mapObjectCollection.addPolyline(list.get(0).getGeometry());
        Geometry polyGeo = Geometry.fromPolyline(line.getGeometry());
        ScreenRect screenRect = new ScreenRect(
                new ScreenPoint(0f,0f),
                new ScreenPoint(
                        ViewMapCard.getWidth(),
                        ViewMapCard.getHeight()
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
}