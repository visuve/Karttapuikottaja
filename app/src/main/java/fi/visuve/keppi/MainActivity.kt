package fi.visuve.keppi

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Greeting("Push button to display your location here")
            }
        }
    }
}

// https://github.com/android/car-samples/blob/main/car_app_library/helloworld/automotive/src/main/AndroidManifest.xml
// https://reintech.io/blog/building-kotlin-app-android-auto-in-car-experiences


@SuppressLint("MissingPermission")
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    //val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var locationInfo: String by remember {
        mutableStateOf(name)
    }

    Column (
        modifier = modifier.padding(36.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)) {

        Text(text = locationInfo)

        Button(onClick = {
            Log.i("Keppi", "Getting location...")

            val cancellationTokenSource = CancellationTokenSource()
            val locationTask = locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token)

            locationTask.addOnSuccessListener { location : Location? ->

                if (location != null) {
                    val time = System.currentTimeMillis().milliseconds.toIsoString()

                    locationInfo = String.format("LATITUDE: %S\nLONGITUDE: %S\nSPEED: %S\nTIME: %S",
                        location.latitude, location.longitude, location.speed, time)
                } else {
                    Log.e("Keppi", "Failed to get location!")
                }
            }

           }
        ) {
            Text("Get Location")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
     Greeting("Preview")
}