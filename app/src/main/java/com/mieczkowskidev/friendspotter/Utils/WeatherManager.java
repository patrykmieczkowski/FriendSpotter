package com.mieczkowskidev.friendspotter.Utils;

import com.mieczkowskidev.friendspotter.R;

/**
 * Created by Patryk Mieczkowski on 2015-12-27
 */
public class WeatherManager {

    public static int getDrawableForWeather (String weather){

        switch (weather){
            case "Cloudy":
                return R.drawable.weather_sun_clouds;
            case "Chance of Rain":
                return R.drawable.weather_clouds1;
            case "Rain":
                return R.drawable.weather_rain1;
            case "Mostly Cloudy":
                return R.drawable.weather_clouds1;
            case "Sunny":
                return R.drawable.weather_sun;
            case "40% Chance Frozen Mix":
                return R.drawable.weather_winter;
            default:
                return R.drawable.weather_sun;
        }
    }
}
