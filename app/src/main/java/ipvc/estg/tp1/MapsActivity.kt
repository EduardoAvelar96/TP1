package ipvc.estg.tp1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.tp1.api.EndPoints
import ipvc.estg.tp1.api.OutputPost
import ipvc.estg.tp1.api.ServiceBuilder
import ipvc.estg.tp1.api.User
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,SensorEventListener {

    var sensor: Sensor? = null
    var sensorManager: SensorManager? = null
    var isRunning = false


    private val newWordActivityRequestCode = 1
    private val newWordActivityRequestCode2 = 2
    private val newWordActivityRequestCode3 = 3
    private var Tipo: Int = 0

    private lateinit var mMap: GoogleMap
    private lateinit var users: List<User>

    //last known location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //location periodic updates
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //initialize fusedLocationClient (Request Localização)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        //location periodic updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)

                //preenche as coordenadas
                findViewById<TextView>(R.id.txtcoordenadas).setText("Lat: " + loc.latitude + " - Long: " + loc.longitude)

                //reverse geocoding (morada)
                val address = getAddress(lastLocation.latitude, lastLocation.longitude)
                findViewById<TextView>(R.id.txtmorada).setText("Morada: " + address)

            }
        }

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getUsers()
        var position: LatLng
        var position2: LatLng

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val Value = sharedPref.getInt(getString(R.string.id_login), 0)

        // ADD MARKERS
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    users = response.body()!!
                    for (problems in users) {
                        if (problems.ID == Value) {
                            position = LatLng(
                                problems.lat.toDouble(),
                                problems.lng.toDouble()
                            )
                            mMap.addMarker(
                                MarkerOptions().position(position)
                                    .title("Coordenadas: " + problems.lat + " - " + problems.lng + "/ Tipo de problema:" + problems.type)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_AZURE
                                        )
                                    )
                            )
                        } else {
                            position2 = LatLng(
                                problems.lat.toDouble(),
                                problems.lng.toDouble()
                            )
                            mMap.addMarker(
                                MarkerOptions().position(position2)
                                    .title("Coordenadas: " + problems.lat + " - " + problems.lng + "/ Tipo de problema:" + problems.type)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@MapsActivity, R.string.Empty, Toast.LENGTH_SHORT).show()
                mMap.clear()
            }
        })

        //Request creation
        createLocationRequest()
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

        setUpMap()

    }

    companion object {
        // add to implement last known location
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        // add to implement location periodic updates
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    //Ultima localização obtida
    fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        } else {
            //Animação da posição
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lastLocation = location
                    Toast.makeText(this@MapsActivity, lastLocation.toString(), Toast.LENGTH_SHORT)
                        .show()
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        // interval specifies the rate at which your app will like to receive updates.
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager!!.unregisterListener(this)
        Log.d("**** Eduardo", "onPause - removeLocationUpdates")
    }

    public override fun onResume() {
        super.onResume()
        startLocationUpdates()
        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d("**** Eduardo", "onResume - startLocationUpdates")
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].getAddressLine(0)
    }

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        // distance in meter
        return results[0]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            Tipo = data?.getIntExtra(RequestType.EXTRA_REPLY, 0)!!

            val sharedPref: SharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val Value = sharedPref.getInt(getString(R.string.id_login), 0)

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.postTest(
                Value,
                Tipo,
                lastLocation.latitude.toString(),
                lastLocation.longitude.toString()
            )

            call.enqueue(object : Callback<OutputPost> {
                override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>) {
                    if (response.isSuccessful) {
                        val c: OutputPost = response.body()!!
                        Toast.makeText(this@MapsActivity, c.MSG, Toast.LENGTH_SHORT).show()
                        resetMarker()
                    }
                }

                override fun onFailure(call: Call<OutputPost>, t: Throwable) {
                    Toast.makeText(this@MapsActivity, R.string.Empty, Toast.LENGTH_SHORT).show()
                }
            })

        } else if (requestCode == newWordActivityRequestCode2 && resultCode == Activity.RESULT_OK) {
            Tipo = data?.getIntExtra(RequestType.EXTRA_REPLY, 0)!!
            var position: LatLng

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.filtrar(Tipo)

            call.enqueue(object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        mMap.clear();
                        users = response.body()!!
                        for (problems in users) {
                            position = LatLng(
                                problems.lat.toDouble(),
                                problems.lng.toDouble()
                            )
                            mMap.addMarker(
                                MarkerOptions().position(position)
                                    .title("Coordenadas: " + problems.lat + " - " + problems.lng + "/ Tipo de problema:" + problems.type)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_ROSE
                                        )
                                    )
                            )
                        }
                        createLocationRequest()
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Toast.makeText(this@MapsActivity, R.string.SemMarker, Toast.LENGTH_SHORT).show()
                }
            })

        } else if (requestCode == newWordActivityRequestCode3 && resultCode == Activity.RESULT_OK) {

            val km = data?.getDoubleExtra(RequestType.EXTRA_REPLY, 0.0)!!
            val meters = km * 1000

            mMap.clear();
            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.getUsers()
            var position: LatLng

            call.enqueue(object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        users = response.body()!!
                        for (problems in users) {
                            val dist = calculateDistance(
                                lastLocation.latitude,
                                lastLocation.longitude,
                                problems.lat.toDouble(),
                                problems.lng.toDouble()
                            )
                            if (dist < meters) {
                                position = LatLng(
                                    problems.lat.toDouble(),
                                    problems.lng.toDouble()
                                )
                                mMap.addMarker(
                                    MarkerOptions().position(position)
                                        .title("Coordenadas: " + problems.lat + " - " + problems.lng + "/ Tipo de problema:" + problems.type)
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_ROSE
                                            )
                                        )
                                )
                            }
                        }
                        createLocationRequest()
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Toast.makeText(this@MapsActivity, R.string.Empty, Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            Toast.makeText(
                applicationContext, R.string.Empty, Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu_maps: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_maps, menu_maps)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //logout
            R.id.btn3 -> {
                val sharedPref: SharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.automatic_login), false)
                    putString(getString(R.string.username_login), null)
                    putInt(getString(R.string.id_login), 0)
                    commit()
                }

                //volta a atividade login
                val intent = Intent(this@MapsActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.btn4 -> {
                val intent = Intent(this@MapsActivity, RequestType::class.java)
                startActivityForResult(intent, newWordActivityRequestCode)

                true
            }
            R.id.btn5 -> {
                val intent = Intent(this@MapsActivity, RequestType::class.java)
                startActivityForResult(intent, newWordActivityRequestCode2)
                true
            }
            R.id.btn6 -> {
                val intent = Intent(this@MapsActivity, RequestDist::class.java)
                startActivityForResult(intent, newWordActivityRequestCode3)
                true
            }
            R.id.btn7 -> {
                resetMarker()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun resetMarker() {
        mMap.clear();
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getUsers()
        var position: LatLng
        var position2: LatLng

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val Value = sharedPref.getInt(getString(R.string.id_login), 0)

        // ADD MARKERS
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    users = response.body()!!
                    for (problems in users) {
                        if (problems.ID == Value) {
                            position = LatLng(
                                problems.lat.toDouble(),
                                problems.lng.toDouble()
                            )
                            mMap.addMarker(
                                MarkerOptions().position(position)
                                    .title("Coordenadas:" + problems.lat + " - " + problems.lng + "/ Tipo de problema:" + problems.type)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_AZURE
                                        )
                                    )
                            )
                        } else {
                            position2 = LatLng(
                                problems.lat.toDouble(),
                                problems.lng.toDouble()
                            )
                            mMap.addMarker(
                                MarkerOptions().position(position2)
                                    .title("Coordenadas: " + problems.lat + " - " + problems.lng + "/ Tipo de problema:" + problems.type)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@MapsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                mMap.clear()
            }
        })
    }

    override fun onSensorChanged(event: SensorEvent?) {
            try {
                if (event!!.values[0] < 30 && isRunning == false) {
                    isRunning = true
                    val success: Boolean = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json_night
                        )
                    )
                } else{
                    isRunning = false
                }
            } catch (e: Exception) {

            }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}
