package fi.visuve.keppi;

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class KarttapuikottajaScreen(carContext: CarContext) : Screen(carContext) {

    private var locationText: String = "Your location is unknown"
    private var locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(carContext)

    private fun getLocation() {
        if (carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationText = "Getting location..."
            invalidate()

            val locationTask =
                locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)

            locationTask.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    locationText = String.format("(%.3f, %.3f)\n@ %.2fkm/h",
                        location.latitude,
                        location.longitude,
                        location.speed * 3.6)
                    invalidate()
                } else {
                    Log.e("Karttapuikottaja", "The location is null!")
                }
            }
        } else {
            carContext.requestPermissions(listOf(Manifest.permission.ACCESS_FINE_LOCATION)) { granted, _ ->
                if (granted.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getLocation();
                }
            }
        }
    }

    override fun onGetTemplate(): Template {
        return MessageTemplate
            .Builder(locationText)
            .setTitle("Karttapuikottaja")
            .addAction(
                Action.Builder()
                .setTitle("Get location?")
                .setOnClickListener { getLocation() }
                .build()
            )
            .build()
    }
}