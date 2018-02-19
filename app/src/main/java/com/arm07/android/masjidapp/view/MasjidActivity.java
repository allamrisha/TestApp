package com.arm07.android.masjidapp.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arm07.android.masjidapp.R;
import com.arm07.android.masjidapp.util.AppUtils;
import com.arm07.android.masjidapp.util.DatePickerSelectionInterface;
import com.arm07.android.masjidapp.util.TimePickerSelectionInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MasjidActivity extends AppCompatActivity implements TimePickerSelectionInterface, DatePickerSelectionInterface,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,AdapterView.OnItemSelectedListener {

    private static final String TAG = "Masjid Activity";
    private long mLastTimePickerValue;
    private long mLastDatePickerValue;
    private TextView mSelectedDateOrTimeTextView;
    private TextView mSelectedTimeTextView;

    GoogleApiClient mGoogleApiClient;

    TextView latitudeText;
    TextView longitudeText;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Double myLatitude;
    private Double myLongitude;

    String[] announcements = { "BYOD", "Umrah Seminar", "Summer Camp", "Halaqua", "Dar Al Taqwa" };
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masjid);
        initViews();
        initLocationRequest();
        initSpinner();

        /*WebView webView=(WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.google.com");*/

        mButton=findViewById(R.id.buttonAds);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MasjidActivity.this,WebActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSpinner() {
        Spinner spin = (Spinner) findViewById(R.id.spinnerAnnounce);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,announcements);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),announcements[position] ,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void initLocationRequest() {
        latitudeText = (TextView) findViewById(R.id.tvLatitude);
        longitudeText = (TextView) findViewById(R.id.tvLongitude);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "+ connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        latitudeText.setText("Latitude : " + String.valueOf(myLatitude));
        longitudeText.setText("Longitude : " + String.valueOf(myLongitude));
    }


    private void initViews() {
        mSelectedDateOrTimeTextView = findViewById(R.id.selectedDateTime);
        mSelectedTimeTextView = findViewById(R.id.tvSelectedTime);

        Button mDatePickerButton = findViewById(R.id.datePicker);
        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO call date picker fragment here
                openDatePicker();
            }
        });

        Button mTimePickerButton = findViewById(R.id.timePicker);
        mTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO call time picker fragment here
                openTimePicker();
            }
        });
    }

    private void openDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        if (mLastDatePickerValue > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mLastDatePickerValue);
            Bundle bundle = new Bundle();
            bundle.putInt("year", calendar.get(Calendar.YEAR));
            bundle.putInt("month", calendar.get(Calendar.MONTH)+1);
            bundle.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));
            datePickerFragment.setArguments(bundle);
        }
        datePickerFragment.delegate = MasjidActivity.this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void openTimePicker() {
        TimePickerFragment timePicker = new TimePickerFragment();

        if (mLastTimePickerValue > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mLastTimePickerValue);
            Bundle bundle = new Bundle();
            bundle.putInt("hour", calendar.get(Calendar.HOUR_OF_DAY));
            bundle.putInt("minute", calendar.get(Calendar.MINUTE));
            timePicker.setArguments(bundle);
        }
        timePicker.delegate = MasjidActivity.this;
        timePicker.setCancelable(false);
        timePicker.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSelected(int hours, int minute) {
        mLastDatePickerValue=0;
       // mSelectedDateOrTimeTextView.setText(String.valueOf(AppUtils.formatCharLength(2, hours) + ":" + AppUtils.formatCharLength(2, minute)));
        mSelectedTimeTextView.setText(String.valueOf(AppUtils.formatCharLength(2, hours) + ":" + AppUtils.formatCharLength(2, minute)));
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        String formatted = format1.format(cal.getTime());
        mLastTimePickerValue = AppUtils.timeIntoTimeStamp(formatted + " " + hours + ":" + minute);
    }

    @Override
    public void onDateSelected(int day, int month, int year) {
        mLastTimePickerValue=0;
        mSelectedDateOrTimeTextView.setText(String.valueOf(AppUtils.formatCharLength(2, day) + " " + AppUtils.formatCharLength(2, (month + 1)) + " " + year));
        mLastDatePickerValue = AppUtils.dateIntoTimeStamp(String.valueOf(day + " " + (month) + " " + year));
    }


}
