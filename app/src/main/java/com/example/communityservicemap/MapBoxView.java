package com.example.communityservicemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentConstants;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MapBoxView extends AppCompatActivity implements PermissionsListener {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;
//    private Location originLocation;
//    this is just the icon ID, not the actual image file name
    private static final String ID_ICON_PLACEMARK = "annotation";

    private static final String PUBLIC_ACCESS_TOKEN = "pk.eyJ1Ijoia2Vsam91IiwiYSI6ImNrbmh2ZGViaTBwMzYyb3FoM3Nud25lb2kifQ.Weyh5b0R8XUuA6JxTaeH9w";

    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// retrieving public access token from string resources
//        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        Mapbox.getInstance(this, PUBLIC_ACCESS_TOKEN);

        setContentView(R.layout.activity_map_box_view);
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//            mapView = (MapView) findViewById(R.id.mapView);
//            mapView.onCreate(savedInstanceState);
//            mapView.getMapAsync(this::onMapReady);
//
//            } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(this);
//        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);
    }


    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.addClickListener(new OnSymbolClickListener() {
                    @Override
                    public boolean onAnnotationClick(Symbol symbol) {
                        throw new RuntimeException();
                    }
                });

                // Set non-data-driven properties.
                symbolManager.setIconAllowOverlap(true);
                symbolManager.setIconTranslate(new Float[]{-4f, 5f});
                symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);

                // Create symbol at specified location
                SymbolOptions symbolOptions = new SymbolOptions()
                        .withLatLng(new LatLng(37.6213, -122.3790))
                        .withIconImage(ID_ICON_PLACEMARK)
                        .withIconSize(1.3f)
                        .withIconOffset(new Float[]{5.0f, -12.0f});



                // Get the actual image from @drawable and slap it on our map style
                addAirplaneImageToStyle(style);

                // Use manager to draw the symbol
                symbolManager.create(symbolOptions);
            }
        });
    }


//    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {


            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
//            set the location component activation options
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
            locationComponent.setRenderMode(RenderMode.GPS);
            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressLint("Missing Permission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

//        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
//        locationEngine.getLastLocation(callback);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionToExplain) {
        Toast.makeText(this, "need access to location!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
                enableLocationComponent(mapboxMap.getStyle());
            }

        } else {
            Toast.makeText(this, "AHHHHHHHH", Toast.LENGTH_LONG).show();
            finish();

        }
    }
    private void addAirplaneImageToStyle(Style style) {
        style.addImage(ID_ICON_PLACEMARK,
                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_baseline_place_24)), true);
    }

    private void reverseGeocodeFunc(LatLng point, int c) {
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(PUBLIC_ACCESS_TOKEN)
                .query(Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();
        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();
                if (results.size() > 0) {
                    CarmenFeature feature;
                    Point firstResultPoint = results.get(0).center();
                    feature = results.get(0);


                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}

