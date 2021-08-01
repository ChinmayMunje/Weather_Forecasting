package com.gtappdevelopers.weather_report

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.util.*

class MainActivity : AppCompatActivity() {

    val CITYNAME: String = "dhaka,bd"

    //    val API: String ="06c921750b9a82d8f5d1294e1586276f"
    val API: String = "dca892fc2d534382a1461857211207"
    lateinit var editText: EditText
    lateinit var string: String
    lateinit var cityNameTV: TextView
    lateinit var searchIV: ImageView
    lateinit var tempTV: TextView
    lateinit var dateTV: TextView
    lateinit var sunRiseTV:TextView
    lateinit var sunSetTV: TextView
    lateinit var windSpeedTV: TextView
    lateinit var pressureTV: TextView
    lateinit var cloudyTV: TextView
    lateinit var iconIV: ImageView
    lateinit var minTempTV: TextView
    lateinit var maxTempTV: TextView


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
        searchIV.setOnClickListener {
            string = editText.text.toString()
            if (string.isEmpty()) {
                Toast.makeText(applicationContext, "Please Enter City", Toast.LENGTH_SHORT).show()
            } else {
                cityNameTV.findViewById<TextView>(R.id.cityName).setText(string)
//                dateTV.findViewById<TextView>(R.id.date).setText(string)
//                tempTV.findViewById<TextView>(R.id.temp).setText(string)
                getWeather(string)
            }
        }


        //WeatherInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeather(cityName: String) {
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=$API&q=" + cityName + "&days=1&aqi=yes&alerts=yes"
        val queue = Volley.newRequestQueue(this@MainActivity)
        val request =
            JsonObjectRequest(Request.Method.GET, url, null, { response ->

                try {
                    Log.e("TAG", "RESPOSNE IS " + response);
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
                    val minTemp: String = dayObj.getDouble("mintemp_c").toString()+ "°C"
                    minTempTV.text = minTemp
                    val maxTemp: String = dayObj.getDouble("maxtemp_c").toString()+ "°C"
                    maxTempTV.text = maxTemp
                    val astroObj = forecastDay.getJSONObject("astro")
                    val sunRise: String = astroObj.getString("sunrise")
                    sunRiseTV.text = sunRise
                    val sunSet: String = astroObj.getString("sunset")
                    sunSetTV.text = sunSet
                    val hourArray = forecastDay.getJSONArray("hour")
                    for (i in 0 until hourArray.length()) {
                        val hourObj = hourArray.getJSONObject(i)
                        val time = hourObj.getString("time")
                        val temper = hourObj.getString("temp_c")
                        var img = hourObj.getJSONObject("condition").getString("icon")
                        img = img.substring(2)
                        Picasso.get().load("http://$img").into(iconIV)
                        val wind = hourObj.getString("wind_kph") +"kph"
                        windSpeedTV.text = wind
                        val pressure = hourObj.getString("pressure_in")
                        pressureTV.text = pressure
                    }


                } catch (e: JSONException) {
                    //below line is for handling json exception.
                    e.printStackTrace();
                }
            },
                { error ->
                    //this method is called when we get any error while fetching data from our API
                    Log.e("TAG", "RESPONSE IS $error")
                    //in this case we are simply dislaying a toast message.
                    Toast.makeText(this@MainActivity, "Fail to get response", Toast.LENGTH_SHORT)
                        .show()
                })
        queue.add(request)
    }

}