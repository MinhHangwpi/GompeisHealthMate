package com.example.cs528finalproject.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.FragmentMapsBinding
import com.example.cs528finalproject.models.LocationModel
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel
import com.example.cs528finalproject.viewmodels.LocationViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsFragment : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    private lateinit var locationRequest: LocationRequest

    private var currentLocation: Location? = null

    private lateinit var googleMap: GoogleMap
    private var marker: Marker? = null
    private var centered = false

    private val foodLocationsViewModel: FoodLocationsViewModel by activityViewModels()

    private val locationViewModel: LocationViewModel by activityViewModels()

    private var currentLocationModel: LocationModel? = null

    private lateinit var binding: FragmentMapsBinding

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this.googleMap = googleMap

        foodLocationsViewModel.foodLocations.value?.forEach{
            val circle: Circle = googleMap.addCircle(
                CircleOptions()
                    .center(LatLng(it.latitude, it.longitude))
                    .radius(50.0)
                    .strokeColor(0x75FF0000)
                    .strokeWidth(3F)
                    .fillColor(0x22000000)
            )
            val marker: Marker? = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .title(it.name)
            )
        }

        val loc = LatLng(42.27485378466768, -71.80838814915141)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(100)
            .setMaxUpdateDelayMillis(1000)
            .build()

//        val foodLocationsViewModel: FoodLocationsViewModel by activityViewModels()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Normally, you want to save a new location to a database. We are simplifying
                // things a bit and just saving it as a local variable, as we only need it again
                // if a Notification is created (when the user navigates away from app).
                currentLocation = locationResult.lastLocation
                Log.i("LOCATION", "location received $currentLocation.latitude")

                if (currentLocation != null) {
                    val currentLocation = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                    if (marker == null) {

                        val smallMarker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker), 64, 64, false)
                        val m = googleMap.addMarker(
                            MarkerOptions().position(currentLocation).title("Location").icon(
                                BitmapDescriptorFactory.fromBitmap(smallMarker))
                        )
                        marker = m

                        currentLocationModel = LocationModel(currentLocation, "")
                    }
                    marker!!.position = currentLocation

                    val geoCoder = context?.let { Geocoder(it, Locale("en", "us")) }
                    val addresses = geoCoder?.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
                    if (addresses != null && addresses.size == 1) {
                        Log.i("LOCATION", addresses[0].getAddressLine(0))
                        currentLocationModel?.address = addresses[0].getAddressLine(0)
                    }
                    locationViewModel.selectItem(currentLocationModel!!)

                    foodLocationsViewModel.updateDistance(currentLocation)

                    if (!centered) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18.0f))
                        centered = true
                    }
                }

            }
        }


    }


    override fun onPause() {
        super.onPause()
        unsubscribeToLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        subscribeToLocationUpdates()
    }

    private fun subscribeToLocationUpdates() {
        try {
            // TODO: Step 1.5, Subscribe to location changes.
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())
            Log.d("LOCATION", "Location Callback added.")
        } catch (unlikely: SecurityException) {
            Log.e("LOCATION", "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    private fun unsubscribeToLocationUpdates() {
        try {
            // TODO: Step 1.6, Unsubscribe to location changes.
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LOCATION", "Location Callback removed.")
                } else {
                    Log.d("LOCATION", "Failed to remove Location Callback.")
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e("LOCATION", "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }
}