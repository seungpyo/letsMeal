package com.example.letsmeal;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.letsmeal.support.PermissionRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

/* Reference: https://duzi077.tistory.com/122 */

public class GeoSearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private SearchView sv;

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY";
    private static final String LOCATION_PERMISSION_GRANTED_KEY = "LOCATION_PERMISSION_GRANTED_KEY";

    // private Task<Location> mLastKnownLocation;
    private boolean mLocationPermissionGranted = false;
    private boolean requestingLocationUpdates = false;

    /* https://gist.github.com/graydon/11198540 */
    private static final LatLngBounds SOUTH_KOREA = new LatLngBounds(new LatLng(34.5000458847, 126.117397903), new LatLng( 38.6122429469, 129.508304478));
    private static final float MINIMUM_ZOOM_LEVEL = 6.5f;
    private static final float DEFAULT_ZOOM_LEVEL = 16f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setSupportActionBar((Toolbar) findViewById(R.id.map_actionbar));

        // Set a map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get a fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set Location Request
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Location Callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("Hams", locationResult.toString());
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // 갱신된 위치로 부드럽게 이동
                    moveTo(location.getLatitude(), location.getLongitude(), true);
                }
            }
        };

        // Save the current state
        updateValuesFromBundle(savedInstanceState);

        // Request for a permission
        PermissionRequest.with(this).needLocation().request();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        sv.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates);
        outState.putBoolean(LOCATION_PERMISSION_GRANTED_KEY, mLocationPermissionGranted);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PermissionRequest.REQUEST_CODE){
            try {
                // 위치정보 접근 권한 검사
                if (permissions.length == 2 && PermissionRequest.allGranted(grantResults)) {
                    mLocationPermissionGranted = true;
                    // 내 위치를 마커로 표시
                    map.setMyLocationEnabled(true);
                    // 현재 위치로 이동하는 버튼 활성화
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    // 현재 위치로 이동
                    setLastLocation();
                    // 현재 위치 주기적으로 업데이트
                    requestingLocationUpdates = true;
                } else {
                    mLocationPermissionGranted = false;
                    // 내 위치를 마커로 표시 비활성화
                    map.setMyLocationEnabled(false);
                    // 현재 위치로 이동하는 버튼 비활성화
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    moveTo(SOUTH_KOREA.getCenter().latitude, SOUTH_KOREA.getCenter().longitude);
                    requestingLocationUpdates = false;
                }
            }catch (SecurityException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        // 이동 범위가 한국을 벗어나지 않도록 카메라 경계 설정
        map.setLatLngBoundsForCameraTarget(SOUTH_KOREA);
        // 다른 국가가 보일 정도로 줌 레벨이 작아지지 않도록 않도록 설정
        map.setMinZoomPreference(MINIMUM_ZOOM_LEVEL);
    }

    private void setLastLocation() throws SecurityException{
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    moveTo(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
        }

        if (savedInstanceState.keySet().contains(LOCATION_PERMISSION_GRANTED_KEY)) {
            mLocationPermissionGranted = savedInstanceState.getBoolean(LOCATION_PERMISSION_GRANTED_KEY);
        }

        // Update UI to match restored state
        if(mLocationPermissionGranted)
            setLastLocation();

        if(requestingLocationUpdates)
            startLocationUpdates();
    }

    private void startLocationUpdates() throws SecurityException{
        Log.d("Hams", "startLocationUpdates()");
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void moveTo(double lat, double lng, boolean animate){
        if(animate)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), DEFAULT_ZOOM_LEVEL));
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), DEFAULT_ZOOM_LEVEL));
    }

    private void moveTo(double lat, double lng){
        this.moveTo(lat, lng, false);
    }
}
