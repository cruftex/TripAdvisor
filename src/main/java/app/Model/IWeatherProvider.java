package app.Model;

import app.Weather.WeatherDto;

public interface IWeatherProvider {
    WeatherDto getWeather(double lat, double lng);
}
