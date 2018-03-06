package com.example.androidlearnning.demomap

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.support.v4.app.ActivityCompat
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import android.location.LocationListener
import android.location.Location
import android.location.LocationManager
import android.util.Log
import kotlinx.android.synthetic.main.map_activity.*
import com.google.android.gms.maps.model.CameraPosition


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnMapClickListener,LocationListener {
    val REQUEST_PERMISSION_LOCATION = 100
    private val TAG = MapsActivity::class.java.name
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        requestPermisson()
        btnMylocation.setOnClickListener {
            var mLcation = getMylocation()
            if (mLcation != null) {
                zoomToLocation(mLcation)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.setOnCameraMoveListener(this)
        mMap.setOnMapClickListener(this)
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun zoomToLocation(location: Location) {
        if (mMap != null) {
            try {
                val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(location.latitude, location.longitude))      // Sets the center of the map to location user
                        .zoom(16f)                   // Sets the zoom size
                        .build()                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * check and request permission
     */
    fun requestPermisson() {
        if (!LocationUtil.checkPermissonMyLocation(this)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
        } else {
            enableLocationSetting()
        }
    }

    /**
     * enable location setting
     */
    fun enableLocationSetting() {
        if (!LocationUtil.checkLocationEnable(this)) {
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
    }

    /**
     * get my location
     */
    fun getMylocation(): Location? {
        var location: Location? = null
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider = LocationUtil.getEnabledLocationProvider(this)
        if (locationProvider != null) {
            val MIN_TIME_BW_UPDATES: Long = 500
            val MIN_DISTANCE_CHANGE_FOR_UPDATES = 100f

            try {
                locationManager.requestLocationUpdates(
                        locationProvider,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this as LocationListener
                )

                location = locationManager
                        .getLastKnownLocation(locationProvider)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        return location
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enableLocationSetting()
            } else {
                Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCameraMove() {

    }

    override fun onMapClick(lt: LatLng?) {
        if (lt != null) {
            Log.d(TAG, "latitude " + lt.latitude + " and longitude " + lt.longitude)
        }

    }

    override fun onLocationChanged(p0: Location?) {
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }
}
