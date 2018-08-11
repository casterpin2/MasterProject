package project.view.gui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suke.widget.SwitchButton;

import project.view.R;
import project.view.model.StoreInformation;
import project.view.util.CustomInterface;

public class EditStoreInformationPage extends BasePage implements OnMapReadyCallback {
    private EditText etStoreName, etPhone;
    private TextView handleAddressText;
    private RelativeLayout handleAddressLayout,main_layout;
    private SwitchButton switch_button;

    //getIntent data
    private int storeID = 0;
    private String storeName = "";
    private String ownerName = "";
    private String address = "";
    private String phone = "";
    private String registedDate = "";
    private StoreInformation storeInformation;
    private double storeLongtitude = 0.0;
    private double storeLatitude = 0.0;
    private Button registerBtn;

    //googleMap
    private double handleLongtitude = 0.0;
    private double handleLatitude = 0.0;
    private double autoLatitude = 0.0;
    private double autoLongtitude = 0.0;
    private GoogleMap mMap;
    private String handleLocationPlace = "";
    private LocationManager locationManager;
    private final String GOOGLE_MAP_KEY = "AIzaSyB_Wiwy7Mu3fjzCHO_7E5dn5n1n6ZaxaWs";
    final static int REQUEST_LOCATION = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public void setAutoLatitude(double autoLatitude) {
        this.autoLatitude = autoLatitude;
    }

    public void setAutoLongtitude(double autoLongtitude) {
        this.autoLongtitude = autoLongtitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store_information_page);
        CustomInterface.setSoftInputMode(this);
        CustomInterface.setStatusBarColor(this);
        findView();
        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CustomInterface.hideKeyboard(view,getBaseContext());
                return false;
            }
        });
        getSupportActionBar().setTitle("Thay đổi thông tin cửa hàng");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorApplication)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentFromStoreInformationPage();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        etStoreName.setText(storeName);
        handleAddressText.setText(address);
        etPhone.setText(phone);

        switch_button.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(view.isChecked()) {
                    getLocation();
                    handleAddressLayout.setEnabled(false);
                    handleAddressText.setText("Vị trí hiện tại của bạn");
                } else {
                    setAutoLatitude(0.0);
                    setAutoLongtitude(0.0);
                    handleAddressText.setText(handleLocationPlace);
                    handleAddressLayout.setEnabled(true);
                    if(handleLongtitude == 0.0 && handleLatitude == 0.0) {
                        LatLng defaultLocation = new LatLng(21.028511, 105.804817);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
                        mMap.setMinZoomPreference(10.0f);
                        mMap.setMaxZoomPreference(10.1f);
                    } else {
                        markerToMap(handleLongtitude,handleLatitude,mMap,"Vị trí đăng kí");
                    }

                }
            }

        });

        handleAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(EditStoreInformationPage.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }

        });


    }

    public void findView(){
        main_layout = findViewById(R.id.main_layout);
        etStoreName = (EditText) findViewById(R.id.etStoreName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        handleAddressText = (TextView) findViewById(R.id.handleAddressText);
        handleAddressLayout = (RelativeLayout) findViewById(R.id.handleAddressLayout);
        switch_button = (SwitchButton) findViewById(R.id.switch_button);
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setText("Thay dổi");
    }

    public void getIntentFromStoreInformationPage() {
        storeName = getIntent().getStringExtra("storeName");
        ownerName = getIntent().getStringExtra("storeName");
        address = getIntent().getStringExtra("storeName");
        phone = getIntent().getStringExtra("storeName");
        registedDate = getIntent().getStringExtra("storeName");
        storeLongtitude = getIntent().getDoubleExtra("longtitude", 0.0);
        storeLatitude = getIntent().getDoubleExtra("latitude", 0.0);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                autoLatitude = location.getLatitude();
                autoLongtitude = location.getLongitude();
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(autoLatitude).append(",").append(autoLongtitude);
//                mAPI = ApiUtils.getAPIServiceMap();
//                final Call<GoogleMapJSON> call = mAPI.getLocation(stringBuilder.toString(),GOOGLE_MAP_KEY);
//                new RegisterStorePage.CallMapAPI().execute(call);
                markerToMap(autoLongtitude, autoLatitude, mMap, "Ví trí của bạn");
            } else {
                Toast.makeText(this, "Chưa có vị trí định vị!!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double latitude = storeLatitude;
                double longtitude = storeLongtitude;

//                 Add a marker in Ha Noi and move the camer
                LatLng storeAddress = new LatLng(latitude, longtitude);
                mMap.addMarker(new MarkerOptions().position(storeAddress).title("Vị trí của bạn"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(storeAddress));
                mMap.setMinZoomPreference(10.0f);

            }

        }
    }

    public void markerToMap(double longtitude, double latitude, GoogleMap mMap, String markerContent) {
        markerContent = "";
        LatLng myLocation = new LatLng(latitude, longtitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(myLocation).title(markerContent));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.setMinZoomPreference(15);
        mMap.setMaxZoomPreference(25);
        mMap.getUiSettings().isMyLocationButtonEnabled();
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }
}
