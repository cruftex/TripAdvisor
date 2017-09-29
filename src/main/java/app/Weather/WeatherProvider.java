package app.Weather;

import app.Model.IWeatherProvider;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.integration.CacheLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class WeatherProvider implements IWeatherProvider {


    //weather cache using Cache2K lib
    Cache<String, WeatherDto> weatherCache = initCache();

    @Override
    public WeatherDto getWeather(double lat, double lng) {
        WeatherDto dto =  weatherCache.get(lat + ":" + lng);
        return dto.isValid() ? dto : null;
    }


    private WeatherDto weatherLookup(double lat, double lng) {
        try {
            HttpResponse<String> weatherResponse = Unirest.post(WeatherConfig.ApiEndPoint)
                    .header("accept", "application/json")
                    .queryString("appid", WeatherConfig.ApiKey)
                    .queryString("lat", lat)
                    .queryString("lon", lng)
                    .queryString("units", "metric")
                    .asString();
            if (weatherResponse.getStatus() == 200)
                return parseFromResponse(weatherResponse.getBody());
            return new WeatherDto();

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private WeatherDto parseFromResponse(String body) {
        try {
            JSONObject obj = new JSONObject(body);
            double temp = obj.getJSONObject("main").getDouble("temp");
            String desc = obj.getJSONArray("weather").getJSONObject(0).getString("description");
            return new WeatherDto(temp, desc);
        } catch (JSONException ex) {
            return new WeatherDto();
        }
    }

    private Cache<String, WeatherDto> initCache() {
        return new Cache2kBuilder<String, WeatherDto>() {}
            .name("weatherCache")
            .expireAfterWrite(WeatherConfig.ttlInMinutes, TimeUnit.MINUTES)    // expire/refresh after 5 minutes
            .resilienceDuration(30, TimeUnit.SECONDS) // cope with at most 30 seconds
            .entryCapacity(40000)
            .loader(new CacheLoader<String, WeatherDto>() {
                @Override
                public WeatherDto load(String s) throws Exception {
                    String[] args = s.split(":");
                    double lat = Double.parseDouble(args[0]);
                    double lng = Double.parseDouble(args[1]);
                    return weatherLookup(lat, lng);
                }
            })
            .build();
    }

}
