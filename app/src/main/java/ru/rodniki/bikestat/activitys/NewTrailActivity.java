package ru.rodniki.bikestat.activitys;

import static java.lang.System.in;
import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.format.DateFormat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.ScreenRect;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.BoundingBoxHelper;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.BaseMapObjectCollection;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.map.RotationType;
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
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import ru.dgis.sdk.Context;
import ru.dgis.sdk.DGis;
import ru.dgis.sdk.KeyFromString;
import ru.dgis.sdk.KeySource;
import ru.rodniki.bikestat.BuildConfig;
import ru.rodniki.bikestat.R;

import ru.rodniki.bikestat.interfaces.ApiInterface;
import ru.rodniki.bikestat.map.Result;

public class NewTrailActivity extends AppCompatActivity implements UserLocationObjectListener, Session.SearchListener, CameraListener, com.yandex.mapkit.transport.bicycle.Session.RouteListener {
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private MapView mapView;
    CardView cardTime, cardDate;
    FragmentManager fm;
    ConstraintLayout backLayout;
    ConstraintLayout layout;
    ImageView close;
    Dialog dialog;
    Button dialogContinue, configBtn;
    CardView ViewMapCard;
    EditText editTextFrom, editTextDist;
    UserLocationLayer locationMapKit;

    SearchManager searchManager;
    Session session;
    MapObjectCollection mapObjectCollection;
    ArrayList<Point> arrayList;
    BicycleRouter bicycleRouter;
    com.yandex.mapkit.transport.bicycle.Session drivingSession;


    private void sumbitQuery(String query){
        session = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()), new SearchOptions(),this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_new_trail);
        requestLocationPermission();
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();

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
        bicycleRouter = TransportFactory.getInstance().createBicycleRouter();

        arrayList = new ArrayList<>();

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
        MaterialTimePicker picker = new MaterialTimePicker.Builder().setHour(12).setMinute(0).setTitleText("Выбрать время поездки").build();
        picker.show(fm, "TAG");
    }
    private void openDatePicker(){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Выбрать дату поездки");
        MaterialDatePicker<Long> picker = builder.build();
        picker.show(fm, picker.toString());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int height = layout.getHeight();
        System.out.println(height);
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