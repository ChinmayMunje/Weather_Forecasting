package com.gtappdevelopers.weather_report

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    val API: String = "Enter your key"

    lateinit var editText: EditText
    lateinit var cityName: String
    lateinit var cityNameTV: TextView
    lateinit var searchIV: ImageView
    lateinit var tempTV: TextView
    lateinit var dateTV: TextView
    lateinit var sunRiseTV: TextView
    lateinit var sunSetTV: TextView
    lateinit var windSpeedTV: TextView
    lateinit var pressureTV: TextView
    lateinit var iconIV: ImageView
    lateinit var minTempTV: TextView
    lateinit var maxTempTV: TextView
    var PERMISSION_CODE = 1
    var locationManager: LocationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        setContentView(R.layout.activity_main)
        cityNameTV = findViewById(R.id.cityName)
        editText = findViewById(R.id.searchET)
        dateTV = findViewById(R.id.date)
        searchIV = findViewById(R.id.idIVSearch)
        tempTV = findViewById(R.id.temp);
        sunRiseTV = findViewById(R.id.sunRise_time)
        sunSetTV = findViewById(R.id.sunSet_time)
        windSpeedTV = findViewById(R.id.windSpeed_val)
        pressureTV = findViewById(R.id.pressure_val)
        iconIV = findViewById(R.id.iconIV)
        minTempTV = findViewById(R.id.min_temp)
        maxTempTV = findViewById(R.id.max_temp)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_CODE
            )
        }
        val location: Location? =
            locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            cityName = getLocationName(location.latitude, location.longitude)
        }
        Log.e("TAG","City name is "+cityName)
        getWeather(cityName)

        searchIV.setOnClickListener {
            cityName = editText.text.toString()
            if (cityName.isEmpty()) {
                Toast.makeText(applicationContext, "Please Enter City", Toast.LENGTH_SHORT).show()
            } else {
                cityNameTV.findViewById<TextView>(R.id.cityName).setText(cityName)
                getWeather(cityName)
            }
        }
    }

    private fun getWeather(cityName: String) {
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=$API&q=" + cityName + "&days=1&aqi=yes&alerts=yes"
        val queue = Volley.newRequestQueue(this@MainActivity)
        val request =
            JsonObjectRequest(Request.Method.GET, url, null, { response ->
                try {
                    val locationObj = response.getJSONObject("location")
                    val name: String = locationObj.getString("name");
                    cityNameTV.text = name
                    val currentObj = response.getJSONObject("current")
                    val date: String = currentObj.getString("last_updated")
                    dateTV.text = date
                    val temperature: String = currentObj.getDouble("temp_c").toString() + "°C"
                    tempTV.text = temperature
                    val forcastObj = response.getJSONObject("forecast")
                    val forecastDay = forcastObj.getJSONArray("forecastday").getJSONObject(0)
                    val dayObj = forecastDay.getJSONObject("day")
                    val minTemp: String = dayObj.getDouble("mintemp_c").toString() + "°C"
                    minTempTV.text = minTemp
                    val maxTemp: String = dayObj.getDouble("maxtemp_c").toString() + "°C"
                    maxTempTV.text = maxTemp
                    val astroObj = forecastDay.getJSONObject("astro")
                    val sunRise: String = astroObj.getString("sunrise")
                    sunRiseTV.text = sunRise
                    val sunSet: String = astroObj.getString("sunset")
                    sunSetTV.text = sunSet
                    val hourArray = forecastDay.getJSONArray("hour")
                    for (i in 0 until hourArray.length()) {
                        val hourObj = hourArray.getJSONObject(i)
                        var img = hourObj.getJSONObject("condition").getString("icon")
                        img = img.substring(2)
                        Picasso.get().load("http://$img").into(iconIV)
                        val wind = hourObj.getString("wind_kph") + "kph"
                        windSpeedTV.text = wind
                        val pressure = hourObj.getString("pressure_in")
                        pressureTV.text = pressure
                    }
                } catch (e: JSONException) {
                    e.printStackTrace();
                }
            },
                { error ->
                    Toast.makeText(this@MainActivity, "Fail to get response", Toast.LENGTH_SHORT)
                        .show()
                })
        queue.add(request)
    }

    fun getLocationName(lattitude: Double, longitude: Double): String {
        var cityName = "Not Found"
        val gcd = Geocoder(baseContext, Locale.getDefault())
        try {
            val addresses: List<Address> = gcd.getFromLocation(
                lattitude, longitude,
                10
            )
            cityName = addresses.get(0).locality
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName
    }
}