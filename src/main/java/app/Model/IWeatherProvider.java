package app.Model;

import app.Weather.WeatherData;

public interface IWeatherProvider {
    WeatherData getWeather(double lat, double lng);
}
