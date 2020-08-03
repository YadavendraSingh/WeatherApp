package com.yadu.weatherapp

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.yadu.weatherapp.data.Constants.WeatherData
import com.yadu.weatherapp.data.model.Weather
import com.yadu.weatherapp.data.repository.DataService
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.content_weather.*


class MainActivity : AppCompatActivity(),GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener {

    var longitude = 0.0
    var latitude = 0.0
    var context: Context? = null

    var mLastLocation: Location? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        mGoogleApiClient =
            GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        initLocation()
        registerReceiver()
    }

    private fun startDownload(lat:Double, lon:Double) {
        val intent = Intent(this, DataService::class.java)
        intent.action = WeatherData
        intent.putExtra("lat", lat)
        intent.putExtra("lon", lon)
        startService(intent)
    }

    private fun registerReceiver() {
        val bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(WeatherData)
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == WeatherData) {
                val weather: Weather = intent.getSerializableExtra("weather")as Weather
                todayTemperature.setText("${weather.current.temp.toInt()} °C")
                todayDescription.setText("${weather.current.weather[0].description}")
                todayHumidity.setText("Humidity: ${weather.current.humidity} %")
                todayPressure.setText("Pressure: ${weather.current.pressure} hpa")
                todayWind.setText("Wind: ${weather.current.windSpeed} m/s")
                tv_weather.setText("Weather")
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setInterval(100000) // Update location every second
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation!!.getLatitude()
            longitude = mLastLocation!!.getLongitude()
            startDownload(latitude, longitude)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location?) {
        if(location!=null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    private fun initLocation() {
        if (checkAndRequestPermissions(context)) {
            Toast.makeText(context, "Permission Granded", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkAndRequestPermissions(context: Context?): Boolean {
        val locationPermission = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }
}
