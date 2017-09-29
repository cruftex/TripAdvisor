package app.Weather;

import app.Model.IWeatherProvider;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.integration.CacheLoader;
import org.eclipse.jetty.util.StringUtil;

import java.util.concurrent.TimeUnit;

public class WeatherProvider implements IWeatherProvider {


    //weather cache using Cache2K lib
    Cache<String, WeatherData> weatherCache = new Cache2kBuilder<String, WeatherData>() {
    }
            .name("weatherCache")
            .expireAfterWrite(WeatherConfig.ttlInMinutes, TimeUnit.MINUTES)    // expire/refresh after 5 minutes
            .resilienceDuration(30, TimeUnit.SECONDS) // cope with at most 30 seconds
            .entryCapacity(40000)
            .loader(new CacheLoader<String, WeatherData>() {
                @Override
                public WeatherData load(String s) throws Exception {
                    System.out.println("cache miss on " + s + ". Reloading");
                    String[] args = s.split(":");
                    double lat = Double.parseDouble(args[0]);
                    double lng = Double.parseDouble(args[1]);
                    String weatherInfo = weatherLookup(lat, lng);
                    return StringUtil.isNotBlank(weatherInfo) ? new WeatherData(s) : null;
                }
            })
            .build();

    @Override
    public WeatherData getWeather(double lat, double lng) {
        return weatherCache.get(lat + ":" + lng);
    }

    private String weatherLookup(double lat, double lng) {
        try {
            HttpResponse<String> weatherResponse = Unirest.post(WeatherConfig.ApiEndPoint)
                    .header("accept", "application/json")
                    .queryString("appid", WeatherConfig.ApiKey)
                    .queryString("lat", lat)
                    .queryString("lon", lng)
                    .asString();
            if (weatherResponse.getStatus() == 200)
                return weatherResponse.getBody();
            return null;

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
