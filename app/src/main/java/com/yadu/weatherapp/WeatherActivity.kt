package com.yadu.weatherapp

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.yadu.imageapp.view.adapter.DailyAdapter
import com.yadu.weatherapp.data.Constants
import com.yadu.weatherapp.data.model.Daily
import com.yadu.weatherapp.data.model.Weather
import com.yadu.weatherapp.data.repository.DataService
import com.yadu.weatherapp.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.content_weather.*

class WeatherActivity : AppCompatActivity() ,GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    var longitude = 0.0
    var latitude = 0.0
    var context: Context? = null

    var mLastLocation: Location? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    private val REQUEST_LOCATION = 1

    lateinit var viewModel:WeatherViewModel
    lateinit var viewAdapter:DailyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        //setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        context = this

        viewModel = getViewModelInstance()
        val viewManager = LinearLayoutManager(this)
        viewAdapter = DailyAdapter(context)

        var recyclerView = findViewById<RecyclerView>(R.id.rv_forecast).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        initLocation()
        registerReceiver()

        viewModel.mutableWeatherLiveData.observe(this, Observer {
            setWeatherData(it)
        })
    }

    private fun getViewModelInstance(): WeatherViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(
                ) as T
            }
        })[WeatherViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
        checkGPSEnableDevice()
    }

    private fun checkGPSEnableDevice(): Boolean {
        var flag = true
        if (!hasGPSDevice(context)) {
            Toast.makeText(context, "Gps not Supported", Toast.LENGTH_SHORT).show()
        }
        val manager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(context)) {
            enableLoc()
            flag = false
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(context)) {
            flag = true
        }
        return flag
    }

    private fun hasGPSDevice(context: Context?): Boolean {
        val mgr =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                ?: return false
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    private fun enableLoc() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        val builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        if (mGoogleApiClient != null) {
            val result =
                LocationServices.SettingsApi.checkLocationSettings(
                    mGoogleApiClient,
                    builder.build()
                )
            result.setResultCallback { result ->
                val status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                            context as Activity,
                            REQUEST_LOCATION
                        )
                    } catch (e: SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

    private fun startDownload(lat:Double, lon:Double) {
        val intent = Intent(this, DataService::class.java)
        intent.action = Constants.WeatherData
        intent.putExtra("lat", lat)
        intent.putExtra("lon", lon)
        startService(intent)
    }

    private fun registerReceiver() {
        val bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.WeatherData)
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Constants.WeatherData) {
                val weather: Weather = intent.getSerializableExtra("weather")as Weather
                todayTemperature.setText("${weather.current.temp.toInt()} °C")
                todayDescription.setText("${weather.current.weather[0].description}")
                todayHumidity.setText("Humidity: ${weather.current.humidity} %")
                todayPressure.setText("Pressure: ${weather.current.pressure} hpa")
                todayWind.setText("Wind: ${weather.current.windSpeed} m/s")
                tv_weather.setText("Weather")

                viewAdapter.setDailyListData(weather.daily as ArrayList<Daily>)
            }
        }
    }

    private fun setWeatherData(weather:Weather){
        todayTemperature.setText("${weather.current.temp.toInt()} °C")
        todayDescription.setText("${weather.current.weather[0].description}")
        todayHumidity.setText("Humidity: ${weather.current.humidity} %")
        todayPressure.setText("Pressure: ${weather.current.pressure} hpa")
        todayWind.setText("Wind: ${weather.current.windSpeed} m/s")
        tv_weather.setText("Weather")

        viewAdapter.setDailyListData(weather.daily as ArrayList<Daily>)
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
            //startDownload(latitude, longitude)
            viewModel.getWeather(latitude, longitude)
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
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            mGoogleApiClient =
                GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build()
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
